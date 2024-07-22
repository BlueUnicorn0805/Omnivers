package hawaiiappbuilders.omniversapp.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.videocall.models.WaitCallModel;
import io.agora.rtc2.IRtcEngineEventHandler;
import timber.log.Timber;

/**
 * Created by Abhishek Vamja on 03-04-2024.
 */
public class InCallActivityAgora extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private static final String TAG = "InCallActivityAgora";
    private InCallActivityAgora context;
    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
    private CountDownTimer countDownTimer;
    private Handler handler;
    private long secondsRemaining;
    private CardView btnSwitchLocalCamera;
    private CardView btnDisableLocalCamera;
    private CardView btnSwitchRemoteCamera;
    private CardView btnDollar;
    private AppCompatImageView btnSwitchMute;
    private CardView btnCancelCall;
    private AppCompatImageView btnMic;
    private PreviewView previewView;
    private RelativeLayout relLoader;
    private FrameLayout remoteContainer ;
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

    // ?????
    private String agoraChannelName = "vcall-3";

    private String agoraAppId = "6e34b78ced7648c98baf1be932017f4e";
    // ????????????? Token
    private String agoraToken = "007eJxTYBCojFn76PPeRN2Z3PM+sB1zNX2vo1N/dkrlvrTDlhfL1A8rMJilGpskmVskp6aYm5lYJFtaJCWmGSalWhobGRiap5mkVvv3pzUEMjKoeDozMjJAIIjPzlCWnJiTo2vMwAAAxp0f7w==";

    private boolean isTurnonVideo = true;
    private boolean isTurnonRemoteVideo = true;
    private boolean isTurnonSwitchCam = true;
    private boolean isTurnonMic = true;

    private CountDownTimer callTimeTimer;

    private long callTime = 0;

    private TextView tvCallTime;
    private TextView tvCurrentTime;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Log.e("InCallActivity", "Join channel success");
                Toast.makeText(InCallActivityAgora.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }
        @Override
        // ??????????,????? uid ??
        public void onUserJoined(int uid, int elapsed) {
            Log.e("InCallActivity", "uid: " + uid);
            runOnUiThread(() -> {
                // ?? uid ?,????????
                FrameLayout remoteContainer = findViewById(R.id.remote_video_view_container);
                setupRemoteVideo(uid, remoteContainer);
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                Toast.makeText(InCallActivityAgora.this, "User offline: " + uid, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private static final int PERMISSION_REQ_ID = 22;

    // ???????????????????????
    private String[] getRequiredPermissions(){
        // ?? targetSDKVersion 31 ?????????
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO, // ????
                    Manifest.permission.CAMERA, // ?????
                    Manifest.permission.READ_PHONE_STATE, // ????????
                    Manifest.permission.BLUETOOTH_CONNECT // ??????
            };
        } else {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
        }
    }

    private boolean checkPermissions() {
        for (String permission : getRequiredPermissions()) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setBackgroundDrawableResource(android.R.color.black);
        setContentView(R.layout.activity_in_call_agora);
        dataUtil = new DataUtil(this, InCallActivityAgora.class.getSimpleName());
        context = this;
        startCountDownTimer();
        getBundleData();
        initialise();
//        initialiseSurfaceView();
    }

    private void getBundleData() {
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        firstName = intent.getStringExtra(Constants.FIRST_NAME);
        lastName = intent.getStringExtra(Constants.LAST_NAME);
        comingFrom = intent.getStringExtra(Constants.COMING_FROM);
        callingIp = intent.getStringExtra(Constants.CALLING_IP);
        iStartedTheCall = intent.getIntExtra(Constants.I_STARTED_CALL, 9999);
//        agoraChannelName = intent.getStringExtra("agoraChannel");
//        agoraAppId = intent.getStringExtra(Constants.KEY_USER_Agora_Id);
//        agoraToken = intent.getStringExtra("agoraToken");
        isTurnonVideo = intent.getBooleanExtra(Constants.KEY_USER_Camera_Statue,true);
//        iStartedTheCall = intent.getStringExtra(Constants.I_STARTED_CALL);
    }

    @SuppressLint("SetTextI18n")
    private void initialise() {
//        cameraExecutor = Executors.newSingleThreadExecutor();
        btnDisableLocalCamera = findViewById(R.id.btnCamera);
        btnSwitchLocalCamera = findViewById(R.id.btnSwitchCamera);
        btnSwitchRemoteCamera = findViewById(R.id.btnSwitchRemoteCamera);
        btnCancelCall = findViewById(R.id.btnReject);
        btnDollar = findViewById(R.id.btnDollar);
        btnMic = findViewById(R.id.ivMic);
        tvCallTime = findViewById(R.id.text_calling_time);
        tvCallTime.setText(DataUtil.convertSECtoHMS(callTime));
        tvCurrentTime = findViewById(R.id.text_current_time);

        tvCurrentTime.setText(DataUtil.currentTime());
//        relLoader = findViewById(R.id.relLoader);
//        tvLoaderText = findViewById(R.id.tvLoaderText);
//        Log.e(TAG, "initialise: " + firstName + " last " + lastName);
//        if (firstName != null && lastName != null) {
//            tvLoaderText.setText(firstName + " " + lastName);
//        }

//        switchCamera(isTurnonVideo);
//        setRemoteVideo();
        remoteContainer = findViewById(R.id.remote_video_view_container);
//        setAgoraRemotePreview(remoteContainer);

        if (checkPermissions()) {
            FrameLayout localContainer = findViewById(R.id.local_video_view_container);
            initializeAndJoinChannel(agoraAppId, agoraChannelName, agoraToken, localContainer, mRtcEventHandler);
        }

        btnDisableLocalCamera.setOnClickListener(v -> changeCamera());
        btnSwitchLocalCamera.setOnClickListener(v -> switchLocalCamera());
        btnSwitchRemoteCamera.setOnClickListener(v -> switchRemoteCamera());
        btnMic.setOnClickListener(v -> changeMic());


        btnDollar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog sendMoneyDlg = new Dialog(InCallActivityAgora.this);
                sendMoneyDlg.setContentView(R.layout.layout_alert_dollar_send_dialog);
                sendMoneyDlg.findViewById(R.id.btn_send).setOnClickListener(v -> {
                    sendMoneyDlg.dismiss();
                });
                sendMoneyDlg.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
                    sendMoneyDlg.dismiss();
                });
                sendMoneyDlg.show();
            }
        });
        btnCancelCall.setOnClickListener(v -> cancelCallApi(1380));
//        startCamera();
//        handler = new Handler();
//        startCountDownTimer();
//        handler.postDelayed(this::callWaitingVideoCallApi, 1000);
    }


    private void changeCamera() {
        isTurnonVideo = !isTurnonVideo;
        HiddenCamera(isTurnonVideo);
//        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
//            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
//        } else {
//            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
//        }
//        startCamera();
    }
    private void changeMic() {
        isTurnonMic = !isTurnonMic;

        if(isTurnonMic){
            ((AppCompatImageView)findViewById(R.id.ivMic)).setImageResource(R.drawable.baseline_mic_24);
            mRtcEngine.muteLocalAudioStream(true);
//            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        }else{
            ((AppCompatImageView)findViewById(R.id.ivMic)).setImageResource(R.drawable.baseline_mic_off_24);
            mRtcEngine.muteLocalAudioStream(false);
        }
    }

    private void HiddenCamera(boolean isTurnonVideo) {
        if(isTurnonVideo){
            ((AppCompatImageView)findViewById(R.id.ivCam)).setImageResource(R.drawable.baseline_video_camera_front_24);
//            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
            findViewById(R.id.local_video_view_container).setVisibility(View.VISIBLE);
        }else{
            ((AppCompatImageView)findViewById(R.id.ivCam)).setImageResource(R.drawable.round_videocam_off_24);
            findViewById(R.id.local_video_view_container).setVisibility(View.GONE);
        }
    }

    private void switchLocalCamera() {
        isTurnonSwitchCam = !isTurnonSwitchCam;
        mRtcEngine.switchCamera();
        if(isTurnonSwitchCam){
//            ((AppCompatImageView)findViewById(R.id.ivSwitchCam)).setImageResource(R.drawable.baseline_video_camera_front_24);
//            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));

        }else{
//            ((AppCompatImageView)findViewById(R.id.ivSwitchCam)).setImageResource(R.drawable.baseline_video_camera_back_24);

        }
    }
    private void switchRemoteCamera() {
        isTurnonRemoteVideo = !isTurnonRemoteVideo;
        if(isTurnonRemoteVideo){
            ((AppCompatImageView)findViewById(R.id.ivswitchRemoteCam)).setImageResource(R.drawable.baseline_switch_remote_video_24);
//            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
            mRtcEngine.muteAllRemoteVideoStreams(true);
        }else{
            ((AppCompatImageView)findViewById(R.id.ivswitchRemoteCam)).setImageResource(R.drawable.round_videocam_off_24);
//            findViewById(R.id.remote_video_view_container).setVisibility(View.GONE);
            mRtcEngine.muteAllRemoteVideoStreams(false);
        }
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
                                    Log.d("@InCallsetVCStatus Response: ","" + response);
                                    isDecline = true;
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    handler.removeCallbacksAndMessages(null);
                                    handler.removeCallbacks(this::callWaitingVideoCallApi);
                                    Toast.makeText(this, model.getMsg(), Toast.LENGTH_SHORT).show();
                                    Log.d("@@@InCallsetVCStatus Response: ","" + response);
                                    finish();
                                }
                            } else {
//                                hideProgressDialog();
                            }
                        } catch (Exception e) {
//                            hideProgressDialog();
                            if (dataUtil != null) {
                                dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setVCstatus");
                            }
                        }
                    }, error -> {
//                hideProgressDialog();
                Log.e(TAG, "ApiError: " + error);
                if (dataUtil != null) {
                    dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "setVCstatus");
                }
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setVCstatus");
            }
        }
    }

    private void startCountDownTimer() {
        if (handler == null) {
            handler = new Handler();
        }
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

        callTimeTimer = new CountDownTimer(1000, 1000) {
            @SuppressLint("LogNotTimber")
            @Override
            public void onTick(long millisUntilFinished) {
                callTime += 1;
                tvCallTime.setText(DataUtil.convertSECtoHMS(callTime));
                Log.d(TAG, "callTime: " + callTime);
            }

            @Override
            public void onFinish() {
                callTimeTimer.start();
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
                        Log.d( "@InCallWaitingOnCall Response:%s","" + response);
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
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    handler.removeCallbacksAndMessages(null);
                                    handler.removeCallbacks(this::callWaitingVideoCallApi);
                                    Toast.makeText(context, model.getMsg(), Toast.LENGTH_LONG).show();
                                    finish();
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
                                dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "waitingOnCall");
                            }
                        }
                    }, error -> {
                showLoader(false);
                Log.e(TAG, "waitingOnCall error:%s" + error);
                if (dataUtil != null) {
                    dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "waitingOnCall");
                }
                finish();
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(InCallActivityAgora.class.getSimpleName());
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
        if (callTimeTimer != null){
            callTimeTimer.cancel();
        }
        if (handler != null) {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(this::callWaitingVideoCallApi);
            }
        }
//        if (!isDecline && !isFinishing()) {
            cancelCallApi(1375);
//        }
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