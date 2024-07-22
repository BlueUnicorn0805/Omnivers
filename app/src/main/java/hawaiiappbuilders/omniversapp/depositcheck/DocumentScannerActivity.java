package hawaiiappbuilders.omniversapp.depositcheck;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static hawaiiappbuilders.omniversapp.depositcheck.camera.ScanANewCheckActivity.BACK;
import static hawaiiappbuilders.omniversapp.depositcheck.camera.ScanANewCheckActivity.FRONT;
import static hawaiiappbuilders.omniversapp.depositcheck.camera.TextRecognitionHelper.TESSERACT_TRAINED_DATA_FOLDER;
import static hawaiiappbuilders.omniversapp.depositcheck.helpers.Utils.addImageToGallery;
import static hawaiiappbuilders.omniversapp.depositcheck.helpers.Utils.decodeSampledBitmapFromUri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checkscanner.helpers.ScannedDocument;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.depositcheck.checks.Check;
import hawaiiappbuilders.omniversapp.depositcheck.helpers.DocumentMessage;
import hawaiiappbuilders.omniversapp.depositcheck.helpers.PreviewFrame;
import hawaiiappbuilders.omniversapp.depositcheck.uicamera.CameraSource;
import hawaiiappbuilders.omniversapp.depositcheck.uicamera.CameraSourcePreview;
import hawaiiappbuilders.omniversapp.depositcheck.uicamera.GraphicOverlay;
import hawaiiappbuilders.omniversapp.depositcheck.uicamera.OcrGraphic;
import hawaiiappbuilders.omniversapp.depositcheck.views.HUDCanvasView;
import hawaiiappbuilders.omniversapp.dialog.util.DialogUtil;
import hawaiiappbuilders.omniversapp.dialog.util.OnDialogViewListener;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DocumentScannerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, SurfaceHolder.Callback,
        Camera.PictureCallback, Camera.PreviewCallback {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static final int CREATE_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 3;
    String[] permissions;
    private static final int RESUME_PERMISSIONS_REQUEST_CAMERA = 11;

    public static final String WidgetCameraIntent = "WidgetCameraIntent";

    public boolean widgetCameraIntent = false;
    public boolean widgetOCRIntent = false;
    private TextView textFrontBack;
    public static int block;
    private final Handler mHideHandler = new Handler();

    String TESS_PATH;
    TessBaseAPI tess;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private View mControlsView;
    Check newCheck;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            // mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    private static final String TAG = "DocumentScannerActivity";
    private MediaPlayer _shootMP = null;

    private boolean safeToTakePicture;
    private ImageView scanDocButton;
    private Button ocr_click;
    private ImageView saved_ocr_texts;
    private HandlerThread mImageThread;
    private ImageProcessor mImageProcessor;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private boolean mFocused;
    private HUDCanvasView mHud;
    private View mWaitSpinner;
    private boolean mBugRotate = false;
    private SharedPreferences mSharedPref;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    public HUDCanvasView getHUD() {
        return mHud;
    }

    public void setImageProcessorBusy(boolean imageProcessorBusy) {
        this.imageProcessorBusy = imageProcessorBusy;
    }

    private MessageDataManager db;
    private boolean imageProcessorBusy = true;

    private TextView statusMessage;

    private static final int RC_OCR_CAPTURE = 9003;

    private String area;

    //Static OpenCV init
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV initialization Failed");
        } else {
            Log.d("OpenCV", "OpenCV initialization Succeeded");
        }
    }

    AppSettings appSettings;
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            area = getIntent().getStringExtra("mode");
            newCheck = new Check();
            appSettings = new AppSettings(this);
            if (appSettings.getScanCheckId() == 0 || appSettings.getScanCheckId() == null) {
                appSettings.setScanCheckId(1);
            } else {
                appSettings.setScanCheckId(appSettings.getScanCheckId() + 1);
            }
            gestureDetector = new GestureDetector(this, new CaptureGestureListener());
            db = new MessageDataManager(this);
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            TESS_PATH = getFilesDir().getAbsolutePath() + "/tessdatademo/";
            setContentView(R.layout.activity_document_scanner);
            textFrontBack = findViewById(R.id.textFrontBack);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{Manifest.permission.CAMERA, READ_MEDIA_IMAGES};
            } else {
                permissions = new String[]{Manifest.permission.CAMERA,
                        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
            }
            initializeCamera();
        } catch (Exception e) {
            Dialog d = new Dialog(this);
            d.setTitle(R.string.error_dsa);
            TextView tv = new TextView(this);
            tv.setText(e.toString());
            d.setContentView(tv);
            d.show();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();
    }
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private void initializeCamera() {
        mVisible = true;
        mContentView = findViewById(R.id.surfaceView);
        mHud = (HUDCanvasView) findViewById(R.id.hud);
        mWaitSpinner = findViewById(R.id.wait_spinner);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mPreview.setCustomSurfaceView((SurfaceView) mContentView);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        mGraphicOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraSource();
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Display display = getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getRealSize(size);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false);
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /*private boolean checkCameraPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE_CAMERA);
            return false;
        }
        return true;
    }*/

    private void checkResumePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    RESUME_PERMISSIONS_REQUEST_CAMERA);
        } else {
            enableCameraView();
        }
    }

    private void checkCreatePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    MY_PERMISSIONS_REQUEST_WRITE);
        }
    }

    public void turnCameraOn() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.setVisibility(SurfaceView.VISIBLE);
        mPreview.setCustomSurfaceView(mSurfaceView);
        enableCameraView();
    }

    public void enableCameraView() {
        if (mSurfaceView == null) {
            turnCameraOn();
        }
        startScan();
    }

    public void startScan() {
        createCameraSource(true, false);
        safeToTakePicture = false;
        startCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        switch (requestCode) {
            case CREATE_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnCameraOn();
                }
                break;
            }

            case RESUME_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    enableCameraView();
                    // Check for the camera permission before accessing the camera.  If the
                    // permission is not granted yet, request permission.
                }
                break;
            }
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    public int getBlock() {
        return block;
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                // return text.getValue();
            } else {
                Log.d(TAG, "text data is null");
            }
        } else {
            Log.d(TAG, "no text detected");
        }
        return text != null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds,
     * canceling any previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    checkResumePermissions();
                }
                break;
                default: {
                    Log.d(TAG, "OpenCVstatus: " + status);
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        Log.d(TAG, "resuming");

        for (String build : Build.SUPPORTED_ABIS) {
            Log.d(TAG, "myBuild " + build);
        }

        checkCreatePermissions();
        startCameraSource();

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        //CustomOpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        if (mImageThread == null) {
            mImageThread = new HandlerThread("Worker Thread");
            mImageThread.start();
        }

        if (mImageProcessor == null) {
            mImageProcessor = new ImageProcessor(mImageThread.getLooper(), new Handler(), this);
        }
        this.setImageProcessorBusy(false);

    }

    public void waitSpinnerVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWaitSpinner.setVisibility(View.VISIBLE);
            }
        });
    }

    public void waitSpinnerInvisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWaitSpinner.setVisibility(View.GONE);
            }
        });
    }

    private SurfaceView mSurfaceView;

    private boolean scanClicked = false;

    private boolean colorMode = false;
    private boolean filterMode = true;

    private boolean autoMode = true;
    private boolean mFlashMode = false;

    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // FIXME: check disableView()
        if (mPreview != null) {
            mPreview.release();
        }
    }

    public List<Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public Camera.Size getMaxPreviewResolution() {
        int maxWidth = 0;
        Camera.Size curRes = null;

        mCamera.lock();

        for (Camera.Size r : getResolutionList()) {
            if (r.width > maxWidth) {
                Log.d(TAG, "supported preview resolution: " + r.width + "x" + r.height);
                maxWidth = r.width;
                curRes = r;
            }
        }
        return curRes;
    }

    public List<Camera.Size> getPictureResolutionList() {
        return mCamera.getParameters().getSupportedPictureSizes();
    }

    public Camera.Size getMaxPictureResolution(float previewRatio) {
        int maxPixels = 0;
        int ratioMaxPixels = 0;
        Camera.Size currentMaxRes = null;
        Camera.Size ratioCurrentMaxRes = null;
        for (Camera.Size r : getPictureResolutionList()) {
            float pictureRatio = (float) r.width / r.height;
            Log.d(TAG, "supported picture resolution: " + r.width + "x" + r.height + " ratio: " + pictureRatio);
            int resolutionPixels = r.width * r.height;

            if (resolutionPixels > ratioMaxPixels && pictureRatio == previewRatio) {
                ratioMaxPixels = resolutionPixels;
                ratioCurrentMaxRes = r;
            }

            if (resolutionPixels > maxPixels) {
                maxPixels = resolutionPixels;
                currentMaxRes = r;
            }
        }

        boolean matchAspect = mSharedPref.getBoolean("match_aspect", true);

        if (ratioCurrentMaxRes != null && matchAspect) {

            Log.d(TAG, "Max supported picture resolution with preview aspect ratio: "
                    + ratioCurrentMaxRes.width + "x" + ratioCurrentMaxRes.height);
            return ratioCurrentMaxRes;
        }
        return currentMaxRes;
    }


    private int findBestCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
            cameraId = i;
        }
        return cameraId;
    }

    private int paramWidth = 0;
    private int paramHeight = 0;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            int cameraId = findBestCamera();
            mCamera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        Camera.Parameters param;
        param = mCamera.getParameters();

        Camera.Size pSize = getMaxPreviewResolution();
        param.setPreviewSize(pSize.width, pSize.height);

        paramWidth = pSize.width;
        paramHeight = pSize.height;

        float previewRatio = (float) pSize.width / pSize.height;

        Display display = getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getRealSize(size);

        int displayWidth = Math.min(size.y, size.x);
        int displayHeight = Math.max(size.y, size.x);

        float displayRatio = (float) displayHeight / displayWidth;

        int previewHeight = displayHeight;

        if (displayRatio > previewRatio) {
            ViewGroup.LayoutParams surfaceParams = mSurfaceView.getLayoutParams();
            previewHeight = (int) ((float) size.y / displayRatio * previewRatio);
            surfaceParams.height = previewHeight;
            mSurfaceView.setLayoutParams(surfaceParams);

            mHud.getLayoutParams().height = previewHeight;
        }

        int hotAreaWidth = displayWidth / 4;
        int hotAreaHeight = previewHeight / 2 - (hotAreaWidth * 2);
        int hotAreaHeightHalf = hotAreaHeight / 2;
        ImageView angleNorthWest = (ImageView) findViewById(R.id.nw_angle);
        ImageView angleNorthEast = (ImageView) findViewById(R.id.ne_angle);
        ImageView angleSouthEast = (ImageView) findViewById(R.id.se_angle);
        ImageView angleSouthWest = (ImageView) findViewById(R.id.sw_angle);

        RelativeLayout.LayoutParams paramsNW = (RelativeLayout.LayoutParams) angleNorthWest.getLayoutParams();
        RelativeLayout.LayoutParams paramsNE = (RelativeLayout.LayoutParams) angleNorthEast.getLayoutParams();
        RelativeLayout.LayoutParams paramsSE = (RelativeLayout.LayoutParams) angleSouthEast.getLayoutParams();
        RelativeLayout.LayoutParams paramsSW = (RelativeLayout.LayoutParams) angleSouthWest.getLayoutParams();

        int nwLeft = hotAreaWidth - paramsNW.width;
        int nwTop = hotAreaHeight - paramsNW.height / 2;

        int neLeft = displayWidth - hotAreaWidth;
        int neTop = hotAreaHeight - paramsNE.height / 2;

        int swLeft = hotAreaWidth - paramsSW.width;
        int swTop = previewHeight - hotAreaHeight + hotAreaHeightHalf;

        int seLeft = displayWidth - hotAreaWidth;
        int seTop = previewHeight - hotAreaHeight + hotAreaHeightHalf;

        paramsNW.leftMargin = nwLeft;
        paramsNW.topMargin = nwTop;
        angleNorthWest.setLayoutParams(paramsNW);

        paramsNE.leftMargin = neLeft;
        paramsNE.topMargin = neTop;
        angleNorthEast.setLayoutParams(paramsNE);

        paramsSE.leftMargin = seLeft;
        paramsSE.topMargin = seTop;
        angleSouthEast.setLayoutParams(paramsSE);

        paramsSW.leftMargin = swLeft;
        paramsSW.topMargin = swTop;
        angleSouthWest.setLayoutParams(paramsSW);

        Camera.Size maxRes = getMaxPictureResolution(previewRatio);
        if (maxRes != null) {
            param.setPictureSize(maxRes.width, maxRes.height);
            Log.d(TAG, "max supported picture resolution: " + maxRes.width + "x" + maxRes.height);
        }

        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            Log.d(TAG, "Enabling Autofocus");
        } else {
            mFocused = true;
            Log.d(TAG, "Autofocus not available");
        }
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            param.setFlashMode(mFlashMode ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
        }

        mCamera.setParameters(param);

        mBugRotate = mSharedPref.getBoolean("bug_rotate", false);

        if (mBugRotate) {
            mCamera.setDisplayOrientation(270);
        } else {
            mCamera.setDisplayOrientation(0);
        }

        if (mImageProcessor != null) {
            mImageProcessor.setBugRotate(mBugRotate);
        }

        try {
            mCamera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
                @Override
                public void onAutoFocusMoving(boolean start, Camera camera) {
                    mFocused = !start;
                    Log.d(TAG, "focusMoving: " + mFocused);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Failed setting AutoFocusMoveCallback");
        }

        // some devices doesn't call the AutoFocusMoveCallback - fake the focus to true at the start
        mFocused = true;

        safeToTakePicture = true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    private void refreshCamera() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);

            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        try {
            if(camera != null && camera.getParameters() != null) {
                Camera.Size pictureSize = camera.getParameters().getPreviewSize();

                Log.d(TAG, "onPreviewFrame - received image " + pictureSize.width + "x" + pictureSize.height
                        + " focused: " + mFocused + " imageprocessor: " + (imageProcessorBusy ? "busy" : "available"));

                if (mFocused && !imageProcessorBusy) {
                    setImageProcessorBusy(true);
                    Mat yuv = new Mat(new Size(pictureSize.width, pictureSize.height * 1.5), CvType.CV_8UC1);
                    yuv.put(0, 0, data);

                    Mat mat = new Mat(new Size(pictureSize.width, pictureSize.height), CvType.CV_8UC4);
                    Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2RGB_NV21, 4);

                    yuv.release();

                    sendImageProcessorMessage("previewFrame", new PreviewFrame(mat, autoMode, !(autoMode || scanClicked)));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            Log.d(TAG, "onPreviewFrame - received image " + pictureSize.width + "x" + pictureSize.height
                    + " focused: " + mFocused + " imageprocessor: " + (imageProcessorBusy ? "busy" : "available"));

            if (mFocused && !imageProcessorBusy) {
                setImageProcessorBusy(true);
                Mat yuv = new Mat(new Size(paramWidth, paramHeight * 1.5), CvType.CV_8UC1);
                yuv.put(0, 0, data);

                Mat mat = new Mat(new Size(paramWidth, paramHeight), CvType.CV_8UC4);
                Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2RGB_NV21, 4);

                yuv.release();

                sendImageProcessorMessage("previewFrame", new PreviewFrame(mat, autoMode, !(autoMode || scanClicked)));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void invalidateHUD() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHud.invalidate();
            }
        });
    }

    private class ResetShutterColor implements Runnable {
        @Override
        public void run() {
            // scanDocButton.setBackgroundTintList(null);
        }
    }

    private ResetShutterColor resetShutterColor = new ResetShutterColor();

    public boolean requestPicture() {
        if (safeToTakePicture) {
            // runOnUiThread(resetShutterColor);
            safeToTakePicture = false;
            mCamera.takePicture(null, null, this);
            return true;
        }
        return false;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        shootSound();

        Camera.Size pictureSize = camera.getParameters().getPictureSize();

        Log.d(TAG, "onPictureTaken - received image " + pictureSize.width + "x" + pictureSize.height);

        Mat mat = new Mat(new Size(pictureSize.width, pictureSize.height), CvType.CV_8U);
        mat.put(0, 0, data);

        setImageProcessorBusy(true);
        sendImageProcessorMessage("pictureTaken", mat);

        scanClicked = false;
        safeToTakePicture = true;

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
       //  boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return c || super.onTouchEvent(e);
    }

    public void sendImageProcessorMessage(String messageText, Object obj) {
        Log.d(TAG, "sending message to ImageProcessor: " + messageText + " - " + obj.toString());
        Message msg = mImageProcessor.obtainMessage();
        msg.obj = new DocumentMessage(messageText, obj);
        mImageProcessor.sendMessage(msg);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    public void getTexts() {
        startCameraSource();
    }

    public String detectTexts(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        return text;
    }

    @SuppressLint("SimpleDateFormat")
    public void saveDocument(ScannedDocument scannedDocument) {
        Mat doc = (scannedDocument.processed != null) ? scannedDocument.processed : scannedDocument.original;
        Intent intent = getIntent();
        String intentText = intent.toString();
        Log.d(TAG, "intent text: " + intentText);
        if (intent.getAction() != null) {

            String fileName;
            boolean isIntent = false;
            Uri fileUri = null;
            if (intent.getAction().equals("android.media.action.IMAGE_CAPTURE")) {
                fileUri = ((Uri) intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT));
                Log.d(TAG, "intent uri: " + fileUri.toString());
                try {
                    fileName = File.createTempFile("onsFile", ".jpg", this.getCacheDir()).getPath();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                isIntent = true;
            } else {
                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                        + "/AlohaCheckScan");
                if (!folder.exists()) {
                    folder.mkdir();
                    Log.d(TAG, "wrote: created folder " + folder.getPath());
                }
                // ex: 1-DOC-front-date.jpg
                fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                        + "/AlohaCheckScan/" + appSettings.getScanCheckId() + "-DOC-" + area + "-"
                        + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
                        + ".jpg";
            }

            Mat endDoc = new Mat(Double.valueOf(doc.size().width).intValue(),
                    Double.valueOf(doc.size().height).intValue(), CvType.CV_8UC4);

            Core.flip(doc.t(), endDoc, 1);

            Imgcodecs.imwrite(fileName, endDoc);
            endDoc.release();

            try {
                ExifInterface exif = new ExifInterface(fileName);
                exif.setAttribute("UserComment", "Generated using OmniVers App");
                String nowFormatted = mDateFormat.format(new Date().getTime());
                exif.setAttribute(ExifInterface.TAG_DATETIME, nowFormatted);
                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, nowFormatted);
                exif.setAttribute("Software", "OmniVers " + BuildConfig.VERSION_NAME);
                exif.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (isIntent) {
                InputStream inputStream = null;
                OutputStream realOutputStream = null;
                try {
                    inputStream = new FileInputStream(fileName);
                    realOutputStream = this.getContentResolver().openOutputStream(fileUri);
                    // Transfer bytes from in to out
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        realOutputStream.write(buffer, 0, len);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    try {
                        inputStream.close();
                        realOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            animateDocument(fileName, scannedDocument);

            Log.d(TAG, "wrote: " + fileName);

            if (isIntent) {
                // new File(fileName).delete();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                extractText(Uri.fromFile(new File(fileName)));
                addImageToGallery(fileName, this);
            }

            // Record goal "PictureTaken"
            // DocumentScannerApplication.getInstance().trackEvent("Event", "Picture Taken", "Document Scanner Activity");

            refreshCamera();
        } else {
            intent.setAction("android.media.action.IMAGE_CAPTURE");
            saveDocument(scannedDocument);
        }
    }

    private void extractText(Uri uri) {
        if (area.contentEquals(FRONT)) {
            newCheck.setFrontImage(uri.getPath());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textFrontBack.setText(BACK);
                }
            });
            area = BACK;
            safeToTakePicture = false;
            waitSpinnerInvisible();
            new DialogUtil(this)
                    .setTitleAndMessage("Deposit Check", "Please scan back of the check")
                    .setPositiveButton("Okay").createDialog(new OnDialogViewListener() {
                        @Override
                        public void onPositiveClick() {
                            int counter = 1;
                            new CountDownTimer(counter * 1000, 1000) {
                                public void onFinish() {
                                    // When timer is finished
                                    safeToTakePicture = true;
                                }

                                public void onTick(long millisUntilFinished) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // showToastMessage(String.valueOf(millisUntilFinished / 1000));
                                        }
                                    });
                                }
                            }.start();
                        }

                        @Override
                        public void onNegativeClick() {

                        }

                        @Override
                        public void onNeutralClick() {

                        }
                    }).show();
        } else {
            newCheck.setBackImage(uri.getPath());
            String bankName = "";
            String payee = "";
            String payeeAddress = "";
            String routingNo = "";
            String accountNo = "";
            String checkNo = "";
            String checkDate = DateUtil.toStringFormat_13(new Date(Calendar.getInstance().getTimeInMillis()));
            double checkAmount = 0.0;
            String memo = "";
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(newCheck.getFrontImage())));
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap rotatedBitmap = Bitmap
                        .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


                com.google.android.gms.vision.text.TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                Frame frameImage = new Frame.Builder().setBitmap(rotatedBitmap).build();
                SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frameImage);
                StringBuilder stringImageLines = new StringBuilder();
                boolean isAddressFound = false;
                boolean isCheckAmountFound = false;
                // Detect entities
                String orderOfStr = "order of";
                String memoStr = "memo";
                String allLines = textBlockSparseArray.get(textBlockSparseArray.keyAt(0)).getValue();
                String[] blocks = allLines.split("\n");
                for (int i = 0; i < blocks.length - 1; i++) {
                    // TextBlock textBlock = textBlockSparseArray.get(textBlockSparseArray.keyAt(i));
                    String blockValue = blocks[i];
                    // Detect entities

                    // Bank Name
                    if (blockValue.toLowerCase().contains("bank")) {
                        bankName = blockValue;
                    }

                    // Payee
                    if (isFullName(blockValue)) {
                        payee = blockValue;
                        if (!isAddressFound && (blocks[i + 1] != null && blocks[i + 2] != null)) {
                            payeeAddress = blocks[i + 1] + " " + blocks[i + 2];
                            isAddressFound = true;
                        }
                    }

                    if (blockValue.toLowerCase().contains("order of")) {
                        int indexStr = blockValue.toLowerCase().indexOf(orderOfStr);
                        if (indexStr != -1) {
                            payee = blockValue.substring(orderOfStr.length() - 1);
                        }
                        if (payee.isEmpty()) {
                            if (blocks[i + 1] != null) {
                                payee = blocks[i + 1];
                            }
                        }
                    }

                    // Routing No - dataset
                    // I456789012 I 1654321098i189098765432109
                    // 45678901216543210981:89098765432109
                    // 0456789012 1:6543210981:89098765432109
                    // 456789012 "I6543210981:89098765432109i

                    /*String currLineWOSpace = blockValue.replace(" ", "");
                    String[] MICRValues = currLineWOSpace.split(":");
                    if (MICRValues.length == 3) {
                        routingNo = getNumber(MICRValues[1]);
                    }*/
                    String newStr = blockValue.replace(" ", "-");
                    if (newStr.contains(":")) {
                        String[] matcher = newStr.split(":");
                        if (matcher.length == 3) {
                            if (matcher[1] != null) {
                                routingNo = matcher[1];
                            }
                        }
                    }

                    // Account No
                    if (blockValue.toLowerCase().contains("account")) {
                        accountNo = blockValue;
                    }

                    // Check No
                    if (blockValue.toLowerCase().contains("check")) {
                        checkNo = blockValue;
                    }

                    // Check Date
                    /*if (blockValue.toLowerCase().contains("date")) {
                        checkDate = blockValue;
                    }*/
                    // Get date today

                    // Check Amount

                    int currencyIndex;
                    if (blockValue.contains("$")) {
                        currencyIndex = blockValue.indexOf("$");
                    } else if (blockValue.contains("₱")) {
                        currencyIndex = blockValue.indexOf("₱");
                    } else if (blockValue.contains("¥")) {
                        currencyIndex = blockValue.indexOf("¥");
                    } else {
                        try {
                            String checkAmountStr = blockValue.replace(",", "");
                            DecimalFormat df = new DecimalFormat("#.00");
                            double testAmount = Double.parseDouble(df.format(Double.parseDouble(checkAmountStr) / 100));
                            currencyIndex = 0;
                        } catch (Exception e) {
                            currencyIndex = -1;
                        }
                    }
                    try {
                        if (!isCheckAmountFound) {
                            if (currencyIndex > 0) { // currency found
                                String checkAmountStr = blockValue.toLowerCase().substring(currencyIndex + 1).replace(",", "");
                                DecimalFormat df = new DecimalFormat("#.00");
                                checkAmount = Double.parseDouble(df.format(Double.parseDouble(checkAmountStr) / 100));
                                isCheckAmountFound = true;
                            } else if (currencyIndex == 0) {
                                String checkAmountStr = blockValue.replace(",", "");
                                DecimalFormat df = new DecimalFormat("#.00");
                                checkAmount = Double.parseDouble(df.format(Double.parseDouble(checkAmountStr) / 100));
                                isCheckAmountFound = true;
                            } else {
                                checkAmount = 0.0;
                            }
                        }
                    } catch (Exception e) {

                    }


                    // Memo

                    if (blockValue.toLowerCase().contains(memoStr)) {
                        // memo = blockValue;
                        int indexStr = blockValue.toLowerCase().indexOf(memoStr);
                        if (indexStr != -1) {
                            memo = blockValue.substring(memoStr.length() - 1);
                        }

                        if (memo.isEmpty()) {
                            if (blocks[i + 1] != null) {
                                memo = blocks[i + 1];
                            }
                        }
                    }
                }

                /*tess = new TessBaseAPI();
                prepareTess("MICR0");
                tess.setImage(rotatedBitmap);
                String text = tess.getUTF8Text();
                String[] numbers = text.split("\n");
                String line = numbers[numbers.length - 1].replace(" ", "");
                ArrayList<String> tokens = getNumbers(line);*/

                // ⑆ Transit (delimit bank branch routing transit #)
                // ⑈ On-us (delimit customer account number)
                // ⑇ Amount (delimit transaction amount)
                // ⑉ Dash (delimit parts of numbers, such as routing or account)

                if (accountNo.isEmpty()) {
                    // accountNo = "12345";
                }

                if (checkNo.isEmpty()) {
                    // checkNo = "0001";
                }

                if (routingNo.isEmpty()) {
                    // routingNo = "000123456";
                } else {
                    routingNo = routingNo.substring(0, routingNo.length() - 1);
                }

                // name, amount

                newCheck.setBankName(bankName); // ok
                newCheck.setName(payee); // ok
                newCheck.setAddress(payeeAddress); // ok
                newCheck.setRoutingNumber(routingNo);
                newCheck.setAccountNumber(getNumber(accountNo.replace(" ", ""))); // ok
                newCheck.setCheckNumber(getNumber(checkNo.replace(" ", ""))); // ok
                newCheck.setTransactionDate(checkDate); // ok
                newCheck.setAmount(checkAmount);
                newCheck.setFrontImage(Uri.fromFile(new File(newCheck.getFrontImage())).getPath()); // ok
                newCheck.setBackImage(Uri.fromFile(new File(newCheck.getBackImage())).getPath()); // ok
                newCheck.setMemo(memo); // ok


                // verifyCheck(newCheck);

                if (isValidCheck(newCheck).isValid()) {
                    // Capture Back Image of Check
                    // Show review page
                    Intent intent = new Intent();
                    intent.putExtra("check", newCheck);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(mContext, isValidCheck(newCheck).getErrorMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("check", newCheck);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }


            } catch (Exception e) {
                //handle exception
                // Toast.makeText(this, "An error occurred while scanning", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("check", newCheck);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }

    public class CheckValidation {
        public boolean isValid;
        public String errorMessage;

        public CheckValidation() {
        }

        public CheckValidation(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private void verifyCheck(Check check) {
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "verifyCheckIsUsable",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String extraParams =
                "&RT=" + check.getRoutingNumber() +
                        "&Acct=" + check.getAccountNumber() +
                        "&Amt=" + check.getAmount() +
                        "&Cknum=" + check.getCheckNumber();
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                if (response != null && !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            int status = jsonArray.getJSONObject(0).getInt("status");
                            String message = "";
                            switch (status) {
                                case -1:
                                    message = "USED";
                                    break;
                                case 0:
                                    message = "NOT BEEN USED";
                                    break;
                                case 1:
                                    message = "THERE'S BEEN AN ERROR";
                                    break;
                            }
                            String finalMessage = message;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // showToastMessage(mContext, finalMessage);
                                }
                            });

                            if (status == 0 && isValidCheck(check).isValid()) {
                                // Capture Back Image of Check
                                // Show review page
                                Intent intent = new Intent();
                                intent.putExtra("check", check);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(mContext, "Check is invalid", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.putExtra("check", check);
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        showAlert(e.getMessage());
                    }
                }
            }

            @Override
            public void onResponseError(String msg) {
                showToastMessage(msg);
            }

            @Override
            public void onServerError() {

            }
        });
    }

    public ArrayList<String> getNumbers(String lineNumbers) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(lineNumbers);
        ArrayList<String> str = new ArrayList<>();
        while (m.find()) {
            str.add(m.group());
        }
        return str;
    }

    public String getNumber(String number) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(number);
        ArrayList<String> str = new ArrayList<>();
        while (m.find()) {
            return m.group();
        }
        return "";
    }

    public boolean isInteger(String s) {
        return s.matches("-?(0|[1-9]\\d*)");
    }

    public boolean isFullName(String str) {
        return str.matches("(([A-Z]\\.?\\s?)*([A-Z][a-z]+\\.?\\s?)+([A-Z]\\.?\\s?[a-z]*)*)");
    }

    public String isAddress(String s) {
        Pattern compile = Pattern.compile("^(\\d+) ?([A-Za-z](?= ))? (.*?) ([^ ]+?) ?((?<= )APT)? ?((?<= )\\d*)?$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = compile.matcher(s);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                return matcher.group(1);
            }
        }
        return "";
    }

    public double isAmount(String a) {
        double result = 0.0;
        Pattern compile = Pattern.compile("^\\$?[1-9]\\d?(?:,\\d{3})*(?:\\.\\d{2})?$");
        Matcher matcher = compile.matcher(a);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String resultStr = matcher.group().replace("$", "");
                // consider adding more currencies
                result = Double.parseDouble(resultStr.replace(",", ""));
                return result;
            }
        }
        return result;
    }

    private CheckValidation isValidCheck(Check check) {
        boolean isNotEmpty = !check.getAccountNumber().isEmpty() && !check.getRoutingNumber().isEmpty() && !check.getCheckNumber().isEmpty();
        boolean isAlreadyAddedInDb = db.isCheckAdded(check.getCheckNumber(), check.getRoutingNumber(), check.getAccountNumber());
        boolean isAmountGreaterThanZero = check.getAmount() > 0;
        boolean isCheckValid = isNotEmpty && !isAlreadyAddedInDb && isAmountGreaterThanZero;
        CheckValidation checkValidation = new CheckValidation();
        checkValidation.setValid(isCheckValid);

        StringBuilder sb = new StringBuilder();
        if (!isNotEmpty) {
            sb.append("Check numbers are invalid.");
            sb.append("\n");
        }

        if (isAlreadyAddedInDb) {
            sb.append("Check already added in db.");
            sb.append("\n");
        }

        if (!isAmountGreaterThanZero) {
            sb.append("Check amount should be greater than 0");
            sb.append("\n");
        }

        if (sb.toString().isEmpty()) {
            sb.append("No errors found");
            sb.append("\n");
        }
        checkValidation.setErrorMessage(sb.toString());
        return checkValidation;
    }

    /**
     * Initialize tesseract engine.
     *
     * @param language Language code in ISO-639-3 format.
     */
    public void prepareTess(final String language) {
        try {
            prepareDirectory(TESS_PATH + TESSERACT_TRAINED_DATA_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTessDataFiles(TESSERACT_TRAINED_DATA_FOLDER);
        tess.init(TESS_PATH, language);
    }

    private String getMatch(String text, String stringPattern) {
        Pattern pattern = Pattern.compile(stringPattern);
        Matcher matcher = pattern.matcher(text);
        StringBuilder str = new StringBuilder();
        while (matcher.find()) {
            str.append(matcher.group(0));
        }
        return str.toString();
    }

    public List<Rect> getTextRegions() {
        Pixa regions = tess.getWords();
        List<Rect> lineRects = new ArrayList<>(regions.getBoxRects());
        regions.recycle();
        return lineRects;
    }

    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = getAssets().list(path);
            for (String fileName : fileList) {
                String pathToDataFile = TESS_PATH + path + "/" + fileName;
                Log.i(TAG, pathToDataFile);
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = getAssets().open(path + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buf = new byte[1024];
                    int length;
                    while ((length = in.read(buf)) > 0) {
                        out.write(buf, 0, length);
                    }
                    in.close();
                    out.close();
                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.getMessage());
        }
    }

    private void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG,
                        "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i(TAG, "Created directory " + path);
        }
    }

    class AnimationRunnable implements Runnable {

        private Size imageSize;
        private Point[] previewPoints = null;
        public Size previewSize = null;
        public String fileName = null;
        public int width;
        public int height;
        private Bitmap bitmap;

        public AnimationRunnable(String filename, ScannedDocument document) {
            this.fileName = filename;
            this.imageSize = document.processed.size();

            if (document.quadrilateral != null) {
                this.previewPoints = document.previewPoints;
                this.previewSize = document.previewSize;
            }
        }

        public double hipotenuse(Point a, Point b) {
            return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        }

        @Override
        public void run() {
            final ImageView imageView = (ImageView) findViewById(R.id.scannedAnimation);


            Display display = getWindowManager().getDefaultDisplay();
            android.graphics.Point size = new android.graphics.Point();
            display.getRealSize(size);

            int width = Math.min(size.x, size.y);
            int height = Math.max(size.x, size.y);

            // ATENTION: captured images are always in landscape, values should be swapped
            double imageWidth = imageSize.height;
            double imageHeight = imageSize.width;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();

            if (previewPoints != null) {
                double documentLeftHeight = hipotenuse(previewPoints[0], previewPoints[1]);
                double documentBottomWidth = hipotenuse(previewPoints[1], previewPoints[2]);
                double documentRightHeight = hipotenuse(previewPoints[2], previewPoints[3]);
                double documentTopWidth = hipotenuse(previewPoints[3], previewPoints[0]);

                double documentWidth = Math.max(documentTopWidth, documentBottomWidth);
                double documentHeight = Math.max(documentLeftHeight, documentRightHeight);

                Log.d(TAG, "device: " + width + "x" + height + " image: " + imageWidth + "x" + imageHeight + " document: " + documentWidth + "x" + documentHeight);
                Log.d(TAG, "previewPoints[0] x=" + previewPoints[0].x + " y=" + previewPoints[0].y);
                Log.d(TAG, "previewPoints[1] x=" + previewPoints[1].x + " y=" + previewPoints[1].y);
                Log.d(TAG, "previewPoints[2] x=" + previewPoints[2].x + " y=" + previewPoints[2].y);
                Log.d(TAG, "previewPoints[3] x=" + previewPoints[3].x + " y=" + previewPoints[3].y);

                // ATTENTION: again, swap width and height
                double xRatio = width / previewSize.height;
                double yRatio = height / previewSize.width;

                params.topMargin = (int) (previewPoints[3].x * yRatio);
                params.leftMargin = (int) ((previewSize.height - previewPoints[3].y) * xRatio);
                params.width = (int) (documentWidth * xRatio);
                params.height = (int) (documentHeight * yRatio);
            } else {
                params.topMargin = height / 4;
                params.leftMargin = width / 4;
                params.width = width / 2;
                params.height = height / 2;
            }

            bitmap = decodeSampledBitmapFromUri(fileName, params.width, params.height);

            imageView.setImageBitmap(bitmap);

            imageView.setVisibility(View.VISIBLE);

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -params.leftMargin,
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, height - params.topMargin
            );

            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0);

            AnimationSet animationSet = new AnimationSet(true);

            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(translateAnimation);

            animationSet.setDuration(600);
            animationSet.setInterpolator(new AccelerateInterpolator());

            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setImageBitmap(null);
                    if (bitmap != null) {
                        AnimationRunnable.this.bitmap.recycle();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            imageView.startAnimation(animationSet);
        }
    }

    private void animateDocument(String filename, ScannedDocument quadrilateral) {

        AnimationRunnable runnable = new AnimationRunnable(filename, quadrilateral);
        runOnUiThread(runnable);

    }

    private void shootSound() {
        AudioManager meng = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (_shootMP == null) {
                _shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            }
            if (_shootMP != null) {
                _shootMP.start();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

}