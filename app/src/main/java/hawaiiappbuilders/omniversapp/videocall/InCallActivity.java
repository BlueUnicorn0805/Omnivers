package hawaiiappbuilders.omniversapp.videocall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.videocall.models.WaitCallModel;
import timber.log.Timber;

/**
 * Created by Abhishek Vamja on 03-04-2024.
 */
public class InCallActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private static final String TAG = "InCallActivity";
    private InCallActivity context;
    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
    private CountDownTimer countDownTimer;
    private Handler handler;
    private long secondsRemaining;
    private Button btnSwitchCamera;
    private Button btnCancelCall;
    private PreviewView previewView;
    private RelativeLayout relLoader;
    private TextView tvLoaderText;
    private String callID;
    //    public String iStartedTheCall;
    public static int iStartedTheCall;
    private DataUtil dataUtil;
    private String firstName;
    private String lastName;
    private boolean isDecline = false;
    private String cameraIp;
    private String comingFrom;
    private String callingIp;
    private SurfaceView surfaceView;
    private SurfaceHolder _surfaceHolder;
    private MediaPlayer _mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_in_call);
        dataUtil = new DataUtil(this, InCallActivity.class.getSimpleName());
        context = this;
        getBundleData();
        initialise();
        initialiseSurfaceView();
    }

    private void getBundleData() {
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        firstName = intent.getStringExtra(Constants.FIRST_NAME);
        lastName = intent.getStringExtra(Constants.LAST_NAME);
        comingFrom = intent.getStringExtra(Constants.COMING_FROM);
        callingIp = intent.getStringExtra(Constants.CALLING_IP);
        iStartedTheCall = intent.getIntExtra(Constants.I_STARTED_CALL, 9999);
//        iStartedTheCall = intent.getStringExtra(Constants.I_STARTED_CALL);
    }

    @SuppressLint("SetTextI18n")
    private void initialise() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnCancelCall = findViewById(R.id.btnCancelCall);
        previewView = findViewById(R.id.previewView);
        relLoader = findViewById(R.id.relLoader);
        tvLoaderText = findViewById(R.id.tvLoaderText);
        Log.e(TAG, "initialise: " + firstName + " last " + lastName);
        if (firstName != null && lastName != null) {
            tvLoaderText.setText(firstName + " " + lastName);
        }
        btnSwitchCamera.setOnClickListener(v -> changeCamera());
        btnCancelCall.setOnClickListener(v -> cancelCallApi(1380));
        startCamera();
        handler = new Handler();
        startCountDownTimer();
        handler.postDelayed(this::callWaitingVideoCallApi, 1000);
    }

    private void initialiseSurfaceView() {
        surfaceView = findViewById(R.id.surfaceView);
        _surfaceHolder = surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
        _surfaceHolder.setFixedSize(320, 240);
    }

    @SuppressLint("LogNotTimber")
    private void cancelCallApi(int status) {
        try {
//            showProgressDialog();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "setVCstatus",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
            builder.appendQueryParameter("VCsecurityID", String.valueOf(vCSecurityID));
            builder.appendQueryParameter("statusID", String.valueOf(status));
            builder.appendQueryParameter("callID", callID);
            String urlWithParams = builder.build().toString();
            Log.d(TAG, "cancel_api_request: " + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        // Handle response
                        Log.d(TAG, "setVCStatus Response: " + response);
                        try {
                            Gson gson = new Gson();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
//                                hideProgressDialog();
                                if (model.getCallStatus() == Constants.CALL_DECLINED || model.getCallStatus() == Constants.CALL_CANCELED_HUNG_UP) {
                                    isDecline = true;
                                    handler.removeCallbacksAndMessages(null);
                                    Toast.makeText(this, model.getMsg(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
//                                hideProgressDialog();
                            }
                        } catch (Exception e) {
//                            hideProgressDialog();
                            if (dataUtil != null) {
                                dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setVCstatus");
                            }
                        }
                    }, error -> {
//                hideProgressDialog();
                Log.e(TAG, "ApiError: " + error);
                if (dataUtil != null) {
                    dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "setVCstatus");
                }
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setVCstatus");
            }
        }
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(5000, 1000) {
            @SuppressLint("LogNotTimber")
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished / 1000;
                Log.d(TAG, "onTick: " + secondsRemaining);
            }

            @Override
            public void onFinish() {
                handler.postDelayed(() -> callWaitingVideoCallApi(), 1000);
                countDownTimer.start();
            }
        }.start();
    }

    @SuppressLint("LogNotTimber")
    private void callWaitingVideoCallApi() {
        try {
            showLoader(true);
            String baseUrl = BaseFunctions.getBaseUrl(context,
                    "waitingOnCall",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
            builder.appendQueryParameter("VCsecurityID", String.valueOf(vCSecurityID));
            builder.appendQueryParameter("callID", callID);
            builder.appendQueryParameter("iStartedTheCall", String.valueOf(iStartedTheCall));
            String urlWithParams = builder.build().toString();
            Log.d(TAG, "waitingOnCall Request:" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        Log.d(TAG, "waitingOnCall Response:%s" + response);
                        // Handle response
                        try {
                            Gson gson = new Gson();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
                                if (handler == null) {
                                    handler = new Handler();
                                }
                                showLoader(false);
                                if (model.getCallStatus() < 1400) {
                                    handler.removeCallbacksAndMessages(null);
                                    handler.removeCallbacks(this::callWaitingVideoCallApi);
                                    Toast.makeText(context, model.getMsg(), Toast.LENGTH_LONG).show();
                                    closeScreen();
                                } else {
                                    if (model.getCallStatus() != 1400) {
                                        Toast.makeText(context, model.getMsg(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        } catch (Exception e) {
                            showLoader(false);
                            Log.e(TAG, "waitingOnCall Exception:%s" + e.getLocalizedMessage());
                            if (dataUtil != null) {
                                dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "waitingOnCall");
                            }
                        }
                    }, error -> {
                showLoader(false);
                Log.e(TAG, "waitingOnCall error:%s" + error);
                if (dataUtil != null) {
                    dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "waitingOnCall");
                }
                finish();
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(InCallActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "waitingOnCall");
            }
        }
    }

    private void closeScreen() {
        handler.postDelayed(() -> {
            showLoader(false);
            finish();
        }, 1500);
    }

    private void changeCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
        } else {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        }
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (Exception exc) {
                Timber.tag("TAG").e(exc, "Use case binding failed");
            }

        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (handler != null) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(this::callWaitingVideoCallApi);
            }
        }
        if (!isDecline && !isFinishing()) {
            cancelCallApi(1375);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    private void showLoader(boolean isLoad) {
//        relLoader.setVisibility(isLoad ? View.VISIBLE : View.GONE);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        _mediaPlayer = new MediaPlayer();
        _mediaPlayer.setDisplay(_surfaceHolder);

        Context context = getApplicationContext();
        Map<String, String> headers = getRtspHeaders();
        Uri source = Uri.parse(getStreamUrl());

        try {
            // Specify the IP camera's URL and auth headers.
            _mediaPlayer.setDataSource(context, source, headers);

            // Begin the process of setting up a video stream.
            _mediaPlayer.setOnPreparedListener(this);
            _mediaPlayer.prepareAsync();
        } catch (Exception e) {
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private Map<String, String> getRtspHeaders() {
        Map<String, String> headers = new HashMap<>();
        String basicAuthValue = getBasicAuthValue(appSettings.getHandle(), "Surya1234");
        headers.put("Authorization", basicAuthValue);
        return headers;
    }

    private String getBasicAuthValue(String usr, String pwd) {
        String credentials = usr + ":" + pwd;
        int flags = Base64.URL_SAFE | Base64.NO_WRAP;
        byte[] bytes = credentials.getBytes();
        return "Basic " + Base64.encodeToString(bytes, flags);
    }

    private String getStreamUrl() {
        return "rtmp://" + callingIp + ":1935/live/venkat";
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        _mediaPlayer.stop();
    }
}