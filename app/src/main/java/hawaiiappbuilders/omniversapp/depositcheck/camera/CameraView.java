package hawaiiappbuilders.omniversapp.depositcheck.camera;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jsjem on 18.11.2016.
 */
public class CameraView extends CameraBridgeViewBase implements Camera.PreviewCallback, SurfaceHolder.Callback, Camera.PictureCallback,
		OCRThread.TextRegionsListener {

	private static final String TAG = CameraView.class.getSimpleName();

	// CameraView

	private final List<Rect> regions = new ArrayList<>();

	private Camera mCamera;
	private byte[] mVideoSource;
	private Bitmap mBackBuffer;
	private OCRThread ocrThread;

	private Paint focusPaint;
	private Rect focusRect;

	private Paint paintText;

	private float horizontalRectRation;
	private float verticalRectRation;

	static {
		System.loadLibrary("livecamera");
	}

	String[] permissions;

	public static class JavaCameraSizeAccessor implements CameraBridgeViewBase.ListItemAccessor {

		@Override
		public int getWidth(Object obj) {
			Camera.Size size = (Camera.Size) obj;
			return size.width;
		}

		@Override
		public int getHeight(Object obj) {
			Camera.Size size = (Camera.Size) obj;
			return size.height;
		}
	}

	public CameraView(final Context context) {
		this(context, null);
	}

	public CameraView(final Context context, final AttributeSet attributes) {
		super(context, attributes);
		getHolder().addCallback(this);
		setWillNotDraw(false);
		focusPaint = new Paint();
		focusPaint.setColor(0xeed7d7d7);
		focusPaint.setStyle(Paint.Style.STROKE);
		focusPaint.setStrokeWidth(2);
		focusRect = new Rect(0, 0, 0, 0);

		paintText = new Paint();
		paintText.setColor(0xeeff0000);
		paintText.setStyle(Paint.Style.STROKE);
		paintText.setStrokeWidth(4);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			permissions = new String[]{Manifest.permission.CAMERA,
					Manifest.permission.READ_MEDIA_AUDIO,
					Manifest.permission.READ_MEDIA_VIDEO, READ_MEDIA_IMAGES};
		} else {
			permissions = new String[]{Manifest.permission.CAMERA,
					WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
		}

		ocrThread = new OCRThread(context);
		horizontalRectRation = 1.0f;
		verticalRectRation = 1.0f;
	}

	public void setShowTextBounds(final boolean show) {
		regions.clear();
		ocrThread.setRegionsListener(this);
		invalidate();
	}

	public void makeOCR(final OCRThread.TextRecognitionListener listener, String languageCode) {
		ocrThread.setTextRecognitionListener(listener, languageCode);
	}

	public void setBitmapToScan(final Bitmap bitmap) {
		ocrThread.setCapturedBitmap(bitmap);
		ocrThread.performOcr();
	}

	public native void decode(final Bitmap pTarget, final byte[] pSource);


	@Override
	protected void onDraw(final Canvas canvas) {
		if (focusRect.width() > 0) {
			canvas.drawRect(focusRect, focusPaint);
		}
		if (mCamera != null) {
			mCamera.addCallbackBuffer(mVideoSource);
			drawTextBounds(canvas);
		}
	}

	private void drawTextBounds(final Canvas canvas) {
		for (Rect region : regions) {
			canvas.drawRect(region.left * horizontalRectRation, region.top * verticalRectRation,
					region.right * horizontalRectRation, region.bottom * verticalRectRation, paintText);
		}
	}

	@Override
	public void onPreviewFrame(final byte[] bytes, final Camera camera) {
		decode(mBackBuffer, bytes);
		ocrThread.updateBitmap(mBackBuffer);
	}

	@Override
	public void surfaceCreated(final SurfaceHolder surfaceHolder) {
		try {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.setPreviewCallbackWithBuffer(this);
			startOcrThread();
		} catch (IOException eIOException) {
			mCamera.release();
			mCamera = null;
			throw new IllegalStateException();
		}
	}

	private void startOcrThread() {
		ocrThread.start();
	}

	@Override
	public void surfaceChanged(final SurfaceHolder surfaceHolder, final int format, final int width, final int height) {
		mCamera.stopPreview();

		// Video source
		Size lSize = findBestResolution();
		updateTextRectsRatio(width, height, lSize);
		PixelFormat lPixelFormat = new PixelFormat();
		PixelFormat.getPixelFormatInfo(mCamera.getParameters()
				.getPreviewFormat(), lPixelFormat);
		int lSourceSize = lSize.width * lSize.height * lPixelFormat.bitsPerPixel / 8;
		mVideoSource = new byte[lSourceSize];

		// Bitmap
		mBackBuffer = Bitmap.createBitmap(lSize.width, lSize.height,
				Bitmap.Config.ARGB_8888);

		// Camera parameters
		Camera.Parameters lParameters = mCamera.getParameters();
		lParameters.setPreviewSize(lSize.width, lSize.height);

		// Fix camera orientation
		setDisplayOrientation(mCamera, 0);
		mCamera.setParameters(lParameters);
		try {
			mCamera.setPreviewDisplay(getHolder());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		mCamera.addCallbackBuffer(mVideoSource);
		mCamera.startPreview();

		/* Now set camera parameters */
		/*try {
			mCamera.stopPreview();
			Size lSize = findBestResolution();
			updateTextRectsRatio(width, height, lSize);

			Camera.Parameters params = mCamera.getParameters();
			Log.d(TAG, "getSupportedPreviewSizes()");
			List<android.hardware.Camera.Size> sizes = params.getSupportedPreviewSizes();

			if (sizes != null) {
				*//* Select the size that fits surface considering maximum size allowed *//*
				org.opencv.core.Size frameSize = calculateCameraFrameSize(sizes, new CameraView.JavaCameraSizeAccessor(), width, height);

				*//* Image format NV21 causes issues in the Android emulators *//*
				if (Build.FINGERPRINT.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator")
						|| Build.MODEL.contains("Android SDK built for x86")
						|| Build.MANUFACTURER.contains("Genymotion")
						|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
						|| "google_sdk".equals(Build.PRODUCT))
					params.setPreviewFormat(ImageFormat.YV12);  // "generic" or "android" = android emulator
				else
					params.setPreviewFormat(ImageFormat.NV21);

				mPreviewFormat = params.getPreviewFormat();

				Log.d(TAG, "Set preview size to " + Integer.valueOf((int)frameSize.width) + "x" + Integer.valueOf((int)frameSize.height));
				params.setPreviewSize((int)frameSize.width, (int)frameSize.height);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !android.os.Build.MODEL.equals("GT-I9100"))
					params.setRecordingHint(true);

				List<String> FocusModes = params.getSupportedFocusModes();
				if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
				{
					params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				}

				mCamera.setParameters(params);
				params = mCamera.getParameters();

				mFrameWidth = params.getPreviewSize().width;
				mFrameHeight = params.getPreviewSize().height;
				*//*mBackBuffer = Bitmap.createBitmap(lSize.width, lSize.height,
						Bitmap.Config.ARGB_8888);*//*
				if ((getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) && (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT))
					mScale = Math.min(((float)height)/mFrameHeight, ((float)width)/mFrameWidth);
				else
					mScale = 0;

				if (mFpsMeter != null) {
					mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
				}

				int size = mFrameWidth * mFrameHeight;
				size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
				mBuffer = new byte[size];

				mCamera.addCallbackBuffer(mBuffer);
				mCamera.setPreviewCallbackWithBuffer(this);

				*//*mFrameChain = new Mat[2];
				mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
				mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);

				AllocateCache();*//*

				mCameraFrame = new CameraView.JavaCameraFrame[2];
				// mCameraFrame[0] = new CameraView.JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
				// mCameraFrame[1] = new CameraView.JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
					mCamera.setPreviewTexture(mSurfaceTexture);
				} else
					mCamera.setPreviewDisplay(null);

				*//* Finally we are ready to start the preview *//*
				Log.d(TAG, "startPreview");
				setDisplayOrientation(mCamera, 0);
				mCamera.setPreviewDisplay(getHolder());
				mCamera.startPreview();
			}
			else {
				// result = false;
			}
		} catch (Exception e) {
			// result = false;
			e.printStackTrace();
		}*/
	}

	private Size findBestResolution() {
		List<Size> lSizes = mCamera.getParameters().getSupportedPreviewSizes();
		Size lSelectedSize = mCamera.new Size(0, 0);
		for (Size lSize : lSizes) {
			if ((lSize.width >= lSelectedSize.width) && (lSize.height >= lSelectedSize.height)) {
				lSelectedSize = lSize;
			}
		}
		if ((lSelectedSize.width == 0) || (lSelectedSize.height == 0)) {
			lSelectedSize = lSizes.get(0);
		}
		return lSelectedSize;
	}

	private void updateTextRectsRatio(final int width, final int height, final Size cameraSize) {
		verticalRectRation = ((float) height) / cameraSize.width;
		horizontalRectRation = ((float) width) / cameraSize.height;
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mVideoSource = null;
			mBackBuffer = null;
			stopOcrThread();
		}
	}

	@Override
	protected boolean connectCamera(int width, int height) {
		return false;
	}

	@Override
	protected void disconnectCamera() {

	}

	private void stopOcrThread() {
		boolean retry = true;
		ocrThread.cancel();
		ocrThread.setRegionsListener(null);
		while (retry) {
			try {
				ocrThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();

			Rect touchRect = new Rect(
					(int) (x - 100),
					(int) (y - 100),
					(int) (x + 100),
					(int) (y + 100));

			final Rect targetFocusRect = new Rect(
					touchRect.left * 2000 / this.getWidth() - 1000,
					touchRect.top * 2000 / this.getHeight() - 1000,
					touchRect.right * 2000 / this.getWidth() - 1000,
					touchRect.bottom * 2000 / this.getHeight() - 1000);

			doTouchFocus(targetFocusRect);
			focusRect = touchRect;
			invalidate();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					focusRect = new Rect(0, 0, 0, 0);
					invalidate();
				}
			}, 1000);
		}
		return false;
	}

	private void doTouchFocus(final Rect tfocusRect) {
		try {
			final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
			Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
			focusList.add(focusArea);

			Camera.Parameters para = mCamera.getParameters();
			para.setFocusAreas(focusList);
			para.setMeteringAreas(focusList);
			mCamera.setParameters(para);
			mCamera.autoFocus(myAutoFocusCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * AutoFocus callback
	 */
	Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			if (arg0) {
				mCamera.cancelAutoFocus();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTextRegionsRecognized(final List<Rect> textRegions) {
		regions.clear();
		regions.addAll(textRegions);
		invalidate();
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[]{angle});
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private String mPictureFileName;
	OnTakePictureCompleteListener listener;
	public interface OnTakePictureCompleteListener {
		void onTakePictureComplete(String filename);

		void onError(int error);
	}

	public void setListeners(OnTakePictureCompleteListener listener) {
		this.listener = listener;
	}

	public void takePicture(final String fileName) {
		try {
			this.mPictureFileName = fileName;

			// Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
			// Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
			mCamera.setPreviewCallback(null);
			// PictureCallback is implemented by the current class
			mCamera.takePicture(null, null, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// The camera preview was automatically stopped. Start it again.
		mCamera.startPreview();
		mCamera.setPreviewCallback(this);
		// Write the image in a file (in jpeg format)
		try {

			FileOutputStream fos = new FileOutputStream(mPictureFileName);
			// fos.write(data);
			Bitmap bitmap = null;
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			if(listener != null) {
				listener.onTakePictureComplete(mPictureFileName);
			}
		} catch (java.io.IOException e) {

		}

	}
}
