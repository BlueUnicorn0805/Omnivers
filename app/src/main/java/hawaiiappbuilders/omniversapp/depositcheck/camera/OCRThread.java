package hawaiiappbuilders.omniversapp.depositcheck.camera;

import static hawaiiappbuilders.omniversapp.depositcheck.camera.TextRecognitionHelper.TESSERACT_PATH;
import static hawaiiappbuilders.omniversapp.depositcheck.camera.TextRecognitionHelper.TESSERACT_TRAINED_DATA_FOLDER;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import hawaiiappbuilders.omniversapp.utils.DateUtil;

/**
 * Created by jsjem on 18.11.2016.
 */
public class OCRThread extends Thread {

	private static final String TAG = OCRThread.class.getSimpleName();
	private final Handler handler;
	private final TextRecognitionHelper textRecognitionHelper;
	private final AtomicBoolean bitmapChanged;

	private boolean runFlag = false;
	private Bitmap bitmap;
	private TextRegionsListener regionsListener;
	private TextRecognitionListener textRecognitionListener;

	private String languageCode;

	private boolean backScanner = false;

	private Bitmap capturedBitmap;

	/**
	 * Constructor.
	 *
	 * @param context Application context.
	 */
	public OCRThread(final Context context) {
		this.textRecognitionHelper = new TextRecognitionHelper(context);
		this.bitmapChanged = new AtomicBoolean();
		this.handler = new Handler();
	}

	public void setCapturedBitmap(Bitmap bitmap) {
		this.capturedBitmap = bitmap;
		this.bitmap = bitmap;
		bitmapChanged.set(true);

	}

	public static void saveBitmap(final Context context, Bitmap bitmap, String area) {
		String date = DateUtil.toStringFormat_16(new Date(DateUtil.getCurrentDate().getTimeInMillis()));
		String pathToDataFile = TESSERACT_PATH + TESSERACT_TRAINED_DATA_FOLDER + "/" + area + "-" + date;
		Log.i(TAG, pathToDataFile);
		if (!(new File(pathToDataFile)).exists()) {
			try {
				FileOutputStream out = new FileOutputStream(pathToDataFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
				Log.i(TAG, "Saved to " + pathToDataFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Update image data for recognition.
	 *
	 * @param bitmap camera frame data.
	 */
	public void updateBitmap(final Bitmap bitmap) {
		this.bitmap = bitmap;
		bitmapChanged.set(true);
	}

	@Override
	public synchronized void start() {
		super.start();
	}

	/**
	 * Stop thread execution.
	 */
	public void cancel() {
		runFlag = false;
		this.regionsListener = null;
		this.textRecognitionListener = null;
	}

	/**
	 * Setter for recognized text region updates listener.
	 *
	 * @param regionsListener Listener for recognized text regions updates.
	 */
	public void setRegionsListener(final TextRegionsListener regionsListener) {
		this.regionsListener = regionsListener;
	}

	/**
	 * Setter for recognized text updates listener.
	 *
	 * @param textRecognitionListener Listener for recognized text updates.
	 */
	public void setTextRecognitionListener(final TextRecognitionListener textRecognitionListener, String languageCode) {
		this.textRecognitionListener = textRecognitionListener;
		this.languageCode = languageCode;
		backScanner = false;
	}

	public void setBackPageListener(final TextRecognitionListener textRecognitionListener) {
		this.textRecognitionListener = textRecognitionListener;
		backScanner = true;
	}

	public void performOcr() {
		textRecognitionHelper.prepareTesseract(languageCode);
		if (bitmapChanged.compareAndSet(true, false)) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			if (bitmap != null) {
				Bitmap rotatedBitmap = Bitmap
						.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				textRecognitionHelper.setBitmap(bitmap);
				// updateTextRegions();
				updateOCRText(bitmap);
				rotatedBitmap.recycle();
			}
		}
		textRecognitionHelper.stop();
	}

	/**
	 * Perform text recognition.
	 */
	@Override
	public void run() {
		/*if(!backScanner) {
			textRecognitionHelper.prepareTesseract(languageCode);
			while (runFlag) {
				if (bitmapChanged.compareAndSet(true, false)) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					if(bitmap != null) {
						Bitmap rotatedBitmap = Bitmap
								.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
						textRecognitionHelper.setBitmap(bitmap);
						// updateTextRegions();
						updateOCRText(bitmap);
						rotatedBitmap.recycle();
					}
				}
			}
			textRecognitionHelper.stop();
		} else {
			while (runFlag) {
				if (bitmapChanged.compareAndSet(true, false)) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					if(bitmap != null) {
						Bitmap rotatedBitmap = Bitmap
								.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
						textRecognitionListener.onBackPageScanned(bitmap);
						rotatedBitmap.recycle();
					}
				}
			}
		}*/
	}


	private void updateTextRegions() {
		if (regionsListener != null) {
			final List<Rect> regions = textRecognitionHelper.getTextRegions();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (regions != null && !regions.isEmpty()) {
						if (regionsListener != null) {
							regionsListener.onTextRegionsRecognized(regions);
						}
					}
				}
			});

		}
	}

	private void updateOCRText(Bitmap bitmap) {
		if (textRecognitionListener != null) {
			final String text = textRecognitionHelper.getText();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (textRecognitionListener != null) {
						textRecognitionListener.onTextRecognized(text, languageCode, bitmap);
						// textRecognitionListener = null;
					}
				}
			});
		}
	}

	/**
	 * Listener for recognized text regions updates.
	 */
	public interface TextRegionsListener {

		/**
		 * Notify about recognized text regions update.
		 *
		 * @param textRegions list of recognized text regions.
		 */
		void onTextRegionsRecognized(final List<Rect> textRegions);
	}

	/**
	 * Listener for recognized text updates.
	 */
	public interface TextRecognitionListener {

		/**
		 * Notify text recognized.
		 *
		 * @param text Recognized text.
		 */
		void onTextRecognized(final String text, final String languageCode, final Bitmap bitmap);

		void onBackPageScanned(final Bitmap bitmap);
	}
}
