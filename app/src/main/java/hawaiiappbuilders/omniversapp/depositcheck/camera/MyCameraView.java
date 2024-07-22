package hawaiiappbuilders.omniversapp.depositcheck.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCameraView extends SurfaceView implements Camera.PreviewCallback, Camera.PictureCallback, SurfaceHolder.Callback, Camera.ErrorCallback, OCRThread.TextRegionsListener {
   static {
      System.loadLibrary("livecamera");
   }
   private static final String TAG = "myCameraView";
   private String mPictureFileName;
   private final List<Rect> regions = new ArrayList<>();
   private OCRThread ocrThread;
   private Paint focusPaint;
   private Rect focusRect;
   private Paint paintText;
   private float horizontalRectRation;
   private float verticalRectRation;

   private byte[] mVideoSource;

   private Bitmap mBackBuffer;

   private Camera mCamera;

   OnTakePictureCompleteListener listener;

   Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {

      @Override
      public void onAutoFocus(boolean arg0, Camera arg1) {
         if (arg0) {
            mCamera.cancelAutoFocus();
         }
      }
   };

   @Override
   public void onTextRegionsRecognized(List<Rect> textRegions) {
      regions.clear();
      regions.addAll(textRegions);
      invalidate();
   }

   public interface OnTakePictureCompleteListener {
      void onTakePictureComplete();

      void onError(int error);
   }

   public void setListeners(OnTakePictureCompleteListener listener) {
      this.listener = listener;
   }


   @Override
   public void onError(int error, Camera camera) {
      listener.onError(error);
   }

   public MyCameraView(final Context context) {
      this(context, null);
   }

   public MyCameraView(Context context, AttributeSet attrs) {
      super(context, attrs);
      focusPaint = new Paint();
      focusPaint.setColor(0xeed7d7d7);
      focusPaint.setStyle(Paint.Style.STROKE);
      focusPaint.setStrokeWidth(2);
      focusRect = new Rect(0, 0, 0, 0);

      paintText = new Paint();
      paintText.setColor(0xeeff0000);
      paintText.setStyle(Paint.Style.STROKE);
      paintText.setStrokeWidth(4);

      ocrThread = new OCRThread(context);
      horizontalRectRation = 1.0f;
      verticalRectRation = 1.0f;
   }

   public void setShowTextBounds(final boolean show) {
      regions.clear();
      ocrThread.setRegionsListener(show ? this : null);
      invalidate();
   }

   public void makeOCR(final OCRThread.TextRecognitionListener listener) {
      ocrThread.setTextRecognitionListener(listener, "eng");
   }

   public List<String> getEffectList() {
      return mCamera.getParameters().getSupportedColorEffects();
   }

   public boolean isEffectSupported() {
      return (mCamera.getParameters().getColorEffect() != null);
   }

   public String getEffect() {
      return mCamera.getParameters().getColorEffect();
   }

   public void setEffect(String effect) {
      Camera.Parameters params = mCamera.getParameters();
      params.setColorEffect(effect);
      mCamera.setParameters(params);
   }

   public List<Camera.Size> getResolutionList() {
      return mCamera.getParameters().getSupportedPreviewSizes();
   }

   /*public void setResolution(Size resolution) {
      disconnectCamera();
      mMaxHeight = (int) resolution.height;
      mMaxWidth = (int) resolution.width;
      connectCamera(getWidth(), getHeight());
   }*/

   public Camera.Size getResolution() {
      return mCamera.getParameters().getPreviewSize();
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
         fos.write(data);
         fos.close();
         listener.onTakePictureComplete();
      } catch (java.io.IOException e) {

      }

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
      Camera.Size lSize = findBestResolution();
      updateTextRectsRatio(width, height, lSize);
      PixelFormat lPixelFormat = new PixelFormat();
      PixelFormat.getPixelFormatInfo(mCamera.getParameters()
              .getPreviewFormat(), lPixelFormat);
      int lSourceSize = lSize.width * lSize.height * lPixelFormat.bitsPerPixel / 8;
      mVideoSource = new byte[lSourceSize];
      mBackBuffer = Bitmap.createBitmap(lSize.width, lSize.height,
              Bitmap.Config.ARGB_8888);
      Camera.Parameters lParameters = mCamera.getParameters();
      lParameters.setPreviewSize(lSize.width, lSize.height);
      mCamera.setParameters(lParameters);
      mCamera.addCallbackBuffer(mVideoSource);
      mCamera.startPreview();
   }

   private Camera.Size findBestResolution() {
      List<Camera.Size> lSizes = mCamera.getParameters().getSupportedPreviewSizes();
      Camera.Size lSelectedSize = mCamera.new Size(0, 0);
      for (Camera.Size lSize : lSizes) {
         if ((lSize.width >= lSelectedSize.width) && (lSize.height >= lSelectedSize.height)) {
            lSelectedSize = lSize;
         }
      }
      if ((lSelectedSize.width == 0) || (lSelectedSize.height == 0)) {
         lSelectedSize = lSizes.get(0);
      }
      return lSelectedSize;
   }

   private void updateTextRectsRatio(final int width, final int height, final Camera.Size cameraSize) {
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

}