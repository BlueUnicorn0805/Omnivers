package hawaiiappbuilders.omniversapp.videocall;

import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_TEXT_MESSAGE_CHAT_ACTIVE;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_VIDEO_RESPONSE_ACTIVE;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.UPDATE_STATUS_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
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


public class MakeCallActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private static final String TAG = "MakeCallActivity";
    private MakeCallActivity context;
    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
    //    private CountDownTimer countDownTimer;
    private Handler handler;
    //    private long secondsRemaining;
    private CardView btnSwitchCamera;
    private CardView btnCancelCall;
    private PreviewView previewView;
    private RelativeLayout relLoader;
    private TextView tvLoaderText;
    private String callID;
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

    public static final int iStartedTheCall = 1;
//    public static int activityStatus = -1; // if it's < 0, this activity is destroyed or not created
    // ?????
    private String agorachannelName = "";

    private String agoraAppId = "6e34b78ced7648c98baf1be932017f4e";
    // ????????????? Token
    private String agoratoken = "";

    private boolean isTurnonVideo = true;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(MakeCallActivity.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        // ??????????,????? uid ??
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> {
                // ?? uid ?,????????
//                setupRemoteVideo(uid);
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                Toast.makeText(MakeCallActivity.this, "User offline: " + uid, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_make_call);
        dataUtil = new DataUtil(this, MakeCallActivity.class.getSimpleName());
        context = this;
        // Register Receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter(ACTION_VIDEO_RESPONSE_ACTIVE));
        getBundleData();
        initialise();
//        initialiseSurfaceView();
    }
    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //playChinChin(0);
            goToVideoCallActivity();
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ????????
        if (checkPermissions()) {
            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        }
    }

    private void getBundleData() {
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        firstName = intent.getStringExtra(Constants.FIRST_NAME);
        lastName = intent.getStringExtra(Constants.LAST_NAME);
        comingFrom = intent.getStringExtra(Constants.COMING_FROM);
        agoratoken = intent.getStringExtra("agoraToken");
        agorachannelName = "Vcall" + callID;
    }

    private void initialise() {
//        cameraExecutor = Executors.newSingleThreadExecutor();
        btnSwitchCamera = findViewById(R.id.btnCamera);
        btnCancelCall = findViewById(R.id.btnReject);
//        previewView = findViewById(R.id.previewView);
//        relLoader = findViewById(R.id.relLoader);
        tvLoaderText = findViewById(R.id.tvLoaderText);
        Log.e(TAG, "initialise: " + firstName + " last " + lastName);
        if (firstName != null && lastName != null) {
            tvLoaderText.setText(String.format("Calling\n %s %s", firstName, lastName));
        }

        agoraInit();
        if (checkPermissions()) {
            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID);
        }

        isTurnonVideo = true;
        btnSwitchCamera.setOnClickListener(v -> changeCamera());
        btnCancelCall.setOnClickListener(v -> cancelCallApi(Constants.CALL_CANCELED_HUNG_UP));//
         ;
//        startCamera();
//        handler = new Handler();
//        if (!TextUtils.isEmpty(comingFrom) && comingFrom.equalsIgnoreCase(Constants.OUTGOING_SCREEN)) {
//        }
        handler = new Handler();
        startCountDownTimer();
        handler.postDelayed(this::callWaitingVideoCallApi, 3000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                goToVideoCallActivity();
//            }
//        }, 10000);
    }
    private CountDownTimer countDownTimer;
    private long secondsRemaining;
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(3000, 1000) {
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
    private void goToVideoCallActivity() {
//        Intent intent = new Intent(this, InCallActivityAgora.class);
        Intent intent = new Intent(this, InCallActivityAgora.class);
//        intent.putExtra("callingMLID", String.valueOf(contactInfo.getMlid()));
//        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.FIRST_NAME, String.valueOf(contactInfo.getFname()));
//        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.LAST_NAME, String.valueOf(contactInfo.getLname()));
//        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.COMING_FROM, hawaiiappbuilders.omniversapp.meeting.utilities.Constants.OUTGOING_SCREEN);
//        if (videoModel.getCallId() != null) {
//            intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.CAll_ID, String.valueOf(videoModel.getCallId()));
//        }
        startActivity(intent);
        finish();
    }

//    @Override
//    protected void onBroadcastReceived(Intent intent) {
//        // super.onBroadcastReceived(intent);
//        if (intent.getAction().equals(UPDATE_STATUS_ID)) {
//            if (intent.getExtras().getInt("statusID") > 2000) {
//                int statusId = intent.getExtras().getInt("statusID");
//                // not visible, destroyed, minimized
//                if (activityStatus <= 1) {
//                    Intent i = new Intent("receivedstatusidfromotherpage");
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    i.putExtra("statusID", statusId);
//                    startActivity(i);
//                } else {
//                    Intent localMsg = new Intent("receivedstatusid");
//                    localMsg.putExtra("statusID", statusId);
//                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(localMsg);
//                }
//
//            }
//        }
//    }

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
            isDecline = true;
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
            Log.d( "@Cancel_api_request: ", "" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        // Handle response
                        Log.d("@setVCStatus Response::: ", "" + response);
                        try {
                            Gson gson = new Gson();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
//                                hideProgressDialog();
                                if (model.getCallStatus() == Constants.CALL_DECLINED || model.getCallStatus() == Constants.CALL_CANCELED_HUNG_UP) {
                                    closeScreen();
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    removeHandler();
                                    Toast.makeText(this, model.getMsg(), Toast.LENGTH_SHORT).show();
                                    Log.d("@Cancel_Call_HangUP::: ", "" + response);
                                    finish();
                                }
                            }
                        } catch (Exception e) {
//                            hideProgressDialog();
                            if (dataUtil != null) {
                                dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setVCstatus");
                            }
                        }
                    }, error -> {
//                hideProgressDialog();
                Log.e(TAG, "ApiError: " + error);
                if (dataUtil != null) {
                    dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "setVCstatus");
                }
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setVCstatus");
            }
        }
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
//            builder.appendQueryParameter("callerhandle", appSettings.getHandle());
            String urlWithParams = builder.build().toString();
            Log.d(TAG, "waitingOnCall Request:" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        Log.d("@WaitingOnCall Response:%s", "" + response);
                        // Handle response
                        try {
                            Gson gson = new Gson();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
                                if (handler == null) {
//                                    handler = new Handler();
                                }
                                showLoader(false);
                                Log.e(TAG, "callWaitingVideoCallApi: " + model.getCallStatus());
                                if (model.getCallStatus() <= 1360) {
//                                    handler.postDelayed(this::callWaitingVideoCallApi, 3000);
                                } else if (model.getCallStatus() == 1400) {
//                                    isDecline = true;
                                    cameraIp = model.getCallingIp();
                                    Log.e(TAG, "goToVideoCallActivity: ");
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    removeHandler();
                                    Intent intent = new Intent(this, InCallActivityAgora.class);
                                    intent.putExtra(Constants.CAll_ID, String.valueOf(callID));
                                    intent.putExtra(Constants.COMING_FROM, Constants.OUTGOING_SCREEN);
                                    intent.putExtra(Constants.FIRST_NAME, firstName);
                                    intent.putExtra(Constants.LAST_NAME, lastName);
                                    intent.putExtra(Constants.I_STARTED_CALL, iStartedTheCall);
                                    intent.putExtra("agoraToken", agoratoken);
                                    intent.putExtra("agoraChannel", agorachannelName);
                                    startActivity(intent);
                                    closeScreen();
                                    finish();
                                } else if (model.getCallStatus() > 1360 && model.getCallStatus() < 1400) {
                                    isDecline = true;
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    removeHandler();
                                    Toast.makeText(context, model.getMsg(), Toast.LENGTH_LONG).show();
                                    closeScreen();
                                    finish();
                                }
                            }

                        } catch (Exception e) {
                            showLoader(false);
                            Log.e(TAG, "waitingOnCall Exception:%s" + e.getLocalizedMessage());
                            if (dataUtil != null) {
                                dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "waitingOnCall");
                            }
                        }
                    }, error -> {
                showLoader(false);
                Log.e(TAG, "waitingOnCall error: " + error.getMessage());
                if (dataUtil != null) {
                    dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "waitingOnCall");
                }
//                finish();
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(MakeCallActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "waitingOnCall");
            }
        }
    }

    private void removeHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(this::callWaitingVideoCallApi);
        }
    }

    private void closeScreen() {
        handler.postDelayed(() -> {
            showLoader(false);
            finish();
        }, 1500);
    }

    private void changeCamera() {
        isTurnonVideo = !isTurnonVideo;
        if(isTurnonVideo){
            ((AppCompatImageView)findViewById(R.id.ivCam)).setImageResource(R.drawable.round_videocam_24);
            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        }else{
            ((AppCompatImageView)findViewById(R.id.ivCam)).setImageResource(R.drawable.round_videocam_off_24);
            stopAgoraPreview();
        }
//        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
//            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
//        } else {
//            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
//        }
//        startCamera();
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
        removeHandler();
        if (!isDecline && !isFinishing()) {
            cancelCallApi(Constants.CALL_DECLINED);
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