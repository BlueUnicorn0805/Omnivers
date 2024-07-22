package hawaiiappbuilders.omniversapp.videocall;

import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_VIDEO_CALL;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_VIDEO_RESPONSE_ACTIVE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.OnGetTokenListener;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.K;
import hawaiiappbuilders.omniversapp.videocall.models.WaitCallModel;
import io.agora.rtc2.IRtcEngineEventHandler;
import timber.log.Timber;

public class IncomingVideoCallActivityAgora extends BaseActivity {

    private static final String TAG = "IncomingVideoCallActivity";
    private String callID;
    private TextView tvFirstChar;
    private TextView tvUserName;
    private CardView btnAccept;
    private CardView btnReject;
    private DataUtil dataUtil;
    private String firsName;
    private String lastName;
    Ringtone ringtone;
    private boolean isCancelAPi = false;

    public static final int iStartedTheCall = 0;
    private Handler handler;
    private boolean isDecline = false;
    private String cameraIp;
    private boolean isAcceptButtonClick = false;

    private CountDownTimer countDownTimer;
    private long secondsRemaining;

    // ?????
    public String agoraChannelName = "";
//
//    private String agoraAppId = "6e34b78ced7648c98baf1be932017f4e";
//    // ????????????? Token
    public String agoraToken = "";
    public String fromFcmToken = "";
    private boolean isTurnonVideo = true;


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // ????????
        if (checkPermissions()) {
            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_video_call_agora);
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        Log.d("@IncomingActivity", "Received callId: " + callID);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        ringtone.setLooping(true);
        ringtone.play();
        dataUtil = new DataUtil(this, IncomingVideoCallActivityAgora.class.getSimpleName());
        // Register Receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter(ACTION_VIDEO_CALL));

        getBundleData();
        initialise();
    }
    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //playChinChin(0);
            callID = intent.getStringExtra(Constants.CAll_ID);
            Log.d("@callId_Broadcast",""+callID);
        }
    };
    public String fcmToken = "";

    private void getBundleData() {
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        firsName = intent.getStringExtra(Constants.FIRST_NAME);
        lastName = intent.getStringExtra(Constants.LAST_NAME);
        fromFcmToken = intent.getStringExtra("fromFcmToken");
//        agoraAppId = intent.getStringExtra(Constants.KEY_USER_Agora_Id);
        agoraToken = intent.getStringExtra("agoraToken");
//        agoraChannelName = callID + "";//firsName + lastName;
//        agoraToken = intent.getStringExtra("agoraToken");
//        agoraAppId = intent.getStringExtra("agoraId");
//        Log.d("@callId",""+callID);
        Log.d("@firsName",""+firsName);
    }

    private void initialise() {
        tvFirstChar = findViewById(R.id.tvFirstChar);
        tvUserName = findViewById(R.id.tvUserName);
        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        if (!TextUtils.isEmpty(firsName) && !TextUtils.isEmpty(lastName)) {
            tvFirstChar.setText(firsName.substring(0, 1));
            tvUserName.setText(firsName + " " + lastName);
        }

        agoraInit();
        if (checkPermissions()) {
            setAgoraLocalPreview(findViewById(R.id.local_video_view_container));
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID);
        }

        btnAccept.setOnClickListener(v -> acceptCall());
        btnReject.setOnClickListener(v -> cancelCall(Constants.CALL_DECLINED));

//        cancelCall(Constants.CALL_DELIVERED);
        handler = new Handler();
        startCountDownTimer();
        handler.postDelayed(this::callWaitingVideoCallApi, 1000);
    }

    private void changeCamera() {
        isTurnonVideo = !isTurnonVideo;
        if(isTurnonVideo){
            ((AppCompatImageView)findViewById(R.id.ivCam)).setImageResource(R.drawable.round_videocam_24);
            FrameLayout localViewContainer =  findViewById(R.id.local_video_view_container);
            setAgoraLocalPreview(localViewContainer);
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

    @SuppressLint("LogNotTimber")
    private void callWaitingVideoCallApi() {
        try {
            Log.d("@CallID :::","" + callID);
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "waitingOnCall",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
            builder.appendQueryParameter("VCsecurityID", String.valueOf(vCSecurityID));
            builder.appendQueryParameter("callID", !TextUtils.isEmpty(callID) ? callID : "2");
            builder.appendQueryParameter("iStartedTheCall", String.valueOf(iStartedTheCall));
//            builder.appendQueryParameter("callerhandle", appSettings.getHandle());
            String urlWithParams = builder.build().toString();
            Log.d(TAG, "waitingOnCall Request:" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        Log.d( "@WaitingOnCall Response:%s", "" + response);
                        // Handle response
                        try {
                            Gson gson = new Gson();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
                                if (handler == null) {
                                    handler = new Handler();
                                }
                                if (model.getCallStatus() > 1360 && model.getCallStatus() < 1400) {
                                    Toast.makeText(this, model.getMsg(), Toast.LENGTH_LONG).show();
                                    if (countDownTimer != null) countDownTimer.cancel();
                                    removeHandler();
                                    closeScreen();
                                    finish();
                                }
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "waitingOnCall Exception:%s" + e.getLocalizedMessage());
                            if (dataUtil != null) {
                                dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "waitingOnCall");
                            }
                        }
                    }, error -> {
                Log.e(TAG, "waitingOnCall error:%s" + error);
                if (dataUtil != null) {

                    dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "waitingOnCall");
                }
                finish();
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                dataUtil.zzzLogIt(e, "waitingOnCall");
            }
        }
    }

    private void closeScreen() {
        handler.postDelayed(this::finish, 1500);
    }

    @Override
    protected void onDestroy() {
        removeHandler();
        stopRingTone();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (!isCancelAPi && !isDecline && !isFinishing()) {
            cancelCall(Constants.CALL_DECLINED);
        }
        super.onDestroy();
    }

    @SuppressLint("LogNotTimber")
    private void stopRingTone() {
        try {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
        } catch (Exception e) {
            Log.d(TAG, "stopRingTone: " + e.getLocalizedMessage());
        }
    }

    @SuppressLint("LogNotTimber")
    private void cancelCall(int status) {
        try {
            Log.e( "@CancelCall: first ", "" + (status == Constants.CALL_DECLINED));
            if (status == Constants.CALL_DECLINED) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                removeHandler();
                if (!isFinishing()) {
                    finish();
                }
            }
            isAcceptButtonClick = true;
            showProgressDialog();
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
            Log.d(TAG, "setVCStatus request: " + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        // Handle response
                        Log.d( "@setVCStatus Response: ","" + response);
                        isCancelAPi = true;
                        try {
                            Gson gson = new Gson();
                            JsonArray json = gson.fromJson(response, JsonArray.class);
                            Toast.makeText(this, json.get(0).getAsJsonObject().get("msg").getAsString(), Toast.LENGTH_SHORT).show();
                            WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                            if (listOfModels != null && listOfModels.length > 0) {
                                WaitCallModel model = listOfModels[0];
                                hideProgressDialog();
                                Toast.makeText(this, model.getMsg(), Toast.LENGTH_SHORT).show();

                                switch (model.getCallStatus()) {
                                    case 1375:
                                        if (countDownTimer != null) {
                                            countDownTimer.cancel();
                                        }
                                        removeHandler();
                                        stopRingTone();
                                        if (!isFinishing()) {
                                            finish();
                                        }
                                        break;
                                    case 1400:
                                        if (countDownTimer != null) {
                                            countDownTimer.cancel();
                                        }
                                        stopRingTone();
                                        removeHandler();
                                        goToVideoCallActivity(model);
                                        finish();
                                        break;
                                    case 1360:
                                        break;
                                }
                            } else {
                                isCancelAPi = false;
                                hideProgressDialog();
                            }
                        } catch (Exception e) {
                            hideProgressDialog();
                            isCancelAPi = false;
                            Timber.tag("TAG").e("onApiResponseError: %s", e.getLocalizedMessage());
                            if (dataUtil != null) {
                                dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setVCstatus");
                            }
                        }
                    }, error -> {
                hideProgressDialog();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ApiError: " + error);

                if (dataUtil != null) {
                    dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "setVCstatus");
                }
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setVCstatus");
            }
        }

    }

    private void removeHandler() {
        isAcceptButtonClick = true;
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(this::callWaitingVideoCallApi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LogNotTimber")
    private void acceptCall() {
        try {
            removeHandler();
            isAcceptButtonClick = true;
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "acceptVC",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());

            Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
            builder.appendQueryParameter("VCsecurityID", String.valueOf(vCSecurityID));
            builder.appendQueryParameter("callID", callID);
//            builder.appendQueryParameter("callID", "41");
            String urlWithParams = builder.build().toString();
            Log.d( "@AcceptVC request::: urlWithParams", "" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                response -> {
                    Log.d( "@AcceptVC Response::: response", "" + response);
                    isCancelAPi = true;
                    Gson gson = new Gson();
                    WaitCallModel[] listOfModels = gson.fromJson(response, WaitCallModel[].class);
                    Log.e(TAG, "acceptCall: " + listOfModels);
                    if (listOfModels != null && listOfModels.length > 0) {
                        WaitCallModel model = listOfModels[0];
                        Log.e(TAG, "acceptCall: " + gson.toJson(model));
                        if (model.getCallStatus() > 1360 && model.getCallStatus() < 1400) {
                            removeHandler();
                            stopRingTone();
                            Toast.makeText(this, model.getMsg(), Toast.LENGTH_SHORT).show();
                            cancelCall(model.getCallStatus());
                        } else if (model.getCallStatus() == 1400) {
                            Log.e(TAG, "goToAnswerCallActivity: ");
                            isDecline = true;
                            Intent intent = new Intent(this, InCallActivityAgora.class);
                            intent.putExtra(Constants.CAll_ID, String.valueOf(callID));
                            intent.putExtra(Constants.COMING_FROM, Constants.INCOMING_SCREEN);
                            intent.putExtra(Constants.FIRST_NAME, model.getCallerFn());
                            intent.putExtra(Constants.LAST_NAME, model.getCallerLn());
                            intent.putExtra(Constants.CALLING_IP, model.getCallerIp());
                            intent.putExtra(Constants.I_STARTED_CALL, iStartedTheCall);
                            intent.putExtra("agoraToken", agoraToken);
                            intent.putExtra("agoraChannel", agoraChannelName);
//                            intent.putExtra(Constants.KEY_USER_Agora_Channel, agoraChannelName);
//                            intent.putExtra(Constants.KEY_USER_Agora_Token, agoraToken);
//                            intent.putExtra(Constants.KEY_USER_Agora_Id, agoraAppId);
                            intent.putExtra(Constants.KEY_USER_Camera_Statue, isTurnonVideo);
                            startActivity(intent);
//                            sendPushResponseVCall();
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            removeHandler();
                            stopRingTone();
                            finish();
                        }
                    }
                }, error -> {
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            isCancelAPi = false;
            if (dataUtil != null) {
                dataUtil.setActivityName(IncomingVideoCallActivityAgora.class.getSimpleName());
                dataUtil.zzzLogIt(e, "acceptVC");
            }
        }

    }
    private void sendPushResponseVCall() {
//        ContactInfo contactInfo = getSelectedContact();
//        Log.d("@SendPushResponseV", "" + contactInfo);
//        int mlid = contactInfo.getMlid();

        try {
            // Prepare JSON containing the FCM message content. What to send and where to send.
            JSONObject fcmData = new JSONObject();
            JSONObject customData = new JSONObject();

            // Where to send FCM message.
            // "to": "" // device token
            fcmData.put("to", fromFcmToken);
               /*JSONObject apns = new JSONObject();
               try {
                  JSONObject apnsHeader = new JSONObject();
                  apnsHeader.put("apns-priority", "10");
                  apns.put("headers", apnsHeader);
                  JSONObject apnsPayloads = new JSONObject();
                  JSONObject apnsPayloadsAps = new JSONObject();
                  apnsPayloadsAps.put("sound", "default");
                  apnsPayloads.put("aps", apnsPayloadsAps);
                  apns.put("payload", apnsPayloads);
               } catch (JSONException e) {
                  e.printStackTrace();
               }

               JSONObject android = new JSONObject();
               try {
                  android.put("priority", "high");
                  JSONObject androidNotification = new JSONObject();
                  androidNotification.put("sound", "default");
                  android.put("notification", androidNotification);
               } catch (JSONException e) {
                  e.printStackTrace();
               }

               if (device.getType() == FCMTokenData.OS_IOS) {
                  fcmData.put("notification", notification);
                  fcmData.put("apns", apns);
               } else if (device.getType() == FCMTokenData.OS_ANDROID) {
                  fcmData.put("android", android);
               }*/
            customData.put("payloadtype", PayloadType.PT_INCOMING_Response_VIDEO_CALL);
            // What to send in FCM message.
            fcmData.put("data", customData);

            JSONObject notification = new JSONObject();
            try {
//                notification.put("title", notificationTitleMessage.getTitle());
//                notification.put("body", notificationTitleMessage.getBody());
                notification.put("sound", "default");
                notification.put("priority", 100);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fcmData.put("notification", notification);

            Log.d("@SendPushTask::: fcmData", ""+fcmData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + K.gKy(BuildConfig.PM));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(fcmData.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
//        if (getLocation()) {
//            try {
////                NotificationHelper notificationHelper = new NotificationHelper(mlid, mContext, (BaseActivity) mContext);
//
//                String timesent = DateUtil.dateToString(new Date(), DateUtil.DATE_FORMAT_38);
//                String name = appSettings.getFN() + " " + appSettings.getLN();
//                JSONObject payloadsData = new JSONObject();
//                payloadsData.put("message", String.format("%s%s", name, timesent));
//                payloadsData.put("subject", "vcalling");   // This is required for msg
//
//
////                notificationHelper.sendPushNotification(mContext, fcmToken, PayloadType.PT_INCOMING_Response_VIDEO_CALL, payloadsData);
////                // Already got token, send push directly using available token/s
////                if (currUserMLID == mlid) {
////                    notificationHelper.sendPushNotification(mContext, currUserTokens, PayloadType.PT_INCOMING_VIDEO_CALL, payloadsData);
////                    return;
////                }
//                // Retrieve token and send push
////                notificationHelper.getToken(1, payloadsData, new OnGetTokenListener() {
////                    @Override
////                    public void onSuccess(String response) {
////                        currUserMLID = mlid;
////                        currUserTokens.clear();
////                    }
////
////                    @Override
////                    public void onVolleyError(VolleyError error) {
////                    }
////
////                    @Override
////                    public void onEmptyResponse() {
////                    }
////
////                    @Override
////                    public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
////                        currUserTokens.addAll(tokenList);
////                    }
////
////                    @Override
////                    public void onJsonArrayEmpty() {
////
////                    }
////
////                    @Override
////                    public void onJsonException() {
////                    }
////
////                    @Override
////                    public void onTokenListEmpty() {
////                    }
////                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }
    private void goToVideoCallActivity(WaitCallModel model) {
        Log.e(TAG, "goToVideoCallActivity: ");

        Intent intent = new Intent(this, MakeCallActivity.class);
        intent.putExtra(Constants.CAll_ID, String.valueOf(callID));
        intent.putExtra(Constants.COMING_FROM, Constants.INCOMING_SCREEN);
        intent.putExtra(Constants.FIRST_NAME, firsName);
        intent.putExtra(Constants.LAST_NAME, lastName);
        intent.putExtra(Constants.CALLING_IP, model.getCallerIp());
        startActivity(intent);
        finish();
    }
}