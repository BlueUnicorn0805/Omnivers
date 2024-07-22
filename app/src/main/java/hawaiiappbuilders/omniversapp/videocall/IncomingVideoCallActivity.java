package hawaiiappbuilders.omniversapp.videocall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import hawaiiappbuilders.omniversapp.ConnectionActivity;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.videocall.models.WaitCallModel;
import timber.log.Timber;

public class IncomingVideoCallActivity extends BaseActivity {

    private static final String TAG = "IncomingVideoCallActivity";
    private String callID;
    private TextView tvFirstChar;
    private TextView tvUserName;
    private ImageView ivAccept;
    private ImageView ivCancel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_video_call);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        ringtone.setLooping(true);
        ringtone.play();
        dataUtil = new DataUtil(this, IncomingVideoCallActivity.class.getSimpleName());
        getBundleData();
        initialise();
    }

    public String fcmToken = "";
//    public String agoraToken = "";

    private void getBundleData() {
        Intent intent = getIntent();
        callID = intent.getStringExtra(Constants.CAll_ID);
        firsName = intent.getStringExtra(Constants.FIRST_NAME);
        lastName = intent.getStringExtra(Constants.LAST_NAME);
        fcmToken = intent.getStringExtra("fcmToken");
//        agoraToken = intent.getStringExtra("agoraToken");
    }

    private void initialise() {
        tvFirstChar = findViewById(R.id.tvFirstChar);
        tvUserName = findViewById(R.id.tvUserName);
        ivAccept = findViewById(R.id.ivAccept);
        ivCancel = findViewById(R.id.ivCancel);

        if (!TextUtils.isEmpty(firsName) && !TextUtils.isEmpty(lastName)) {
            tvFirstChar.setText(firsName.substring(0, 1));
            tvUserName.setText(firsName + " " + lastName);
        }

        ivAccept.setOnClickListener(v -> acceptCall());
        ivCancel.setOnClickListener(v -> cancelCall(Constants.CALL_DECLINED));

//        cancelCall(Constants.CALL_DELIVERED);
        handler = new Handler();
        startCountDownTimer();
        handler.postDelayed(this::callWaitingVideoCallApi, 1000);
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
                                dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "waitingOnCall");
                            }
                        }
                    }, error -> {
                Log.e(TAG, "waitingOnCall error:%s" + error);
                if (dataUtil != null) {

                    dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "waitingOnCall");
                }
                finish();
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
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
            Log.e(TAG, "cancelCall: first " + (status == Constants.CALL_DECLINED));
            if (status == Constants.CALL_DECLINED) {
                removeHandler();
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
                        Log.d(TAG, "setVCStatus Response: " + response);
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
                                dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setVCstatus");
                            }
                        }
                    }, error -> {
                hideProgressDialog();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ApiError: " + error);

                if (dataUtil != null) {
                    dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(error, "setVCstatus");
                }
                // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
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
            String urlWithParams = builder.build().toString();
            Log.d(TAG, "acceptVC request: " + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        Log.d(TAG, "acceptVC Response: " + response);
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
                                Intent intent = new Intent(this, InCallActivity.class);
                                intent.putExtra(Constants.CAll_ID, String.valueOf(callID));
                                intent.putExtra(Constants.COMING_FROM, Constants.INCOMING_SCREEN);
                                intent.putExtra(Constants.FIRST_NAME, model.getCallerFn());
                                intent.putExtra(Constants.LAST_NAME, model.getCallerLn());
                                intent.putExtra(Constants.CALLING_IP, model.getCallerIp());
                                intent.putExtra(Constants.I_STARTED_CALL, iStartedTheCall);
                                startActivity(intent);

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
                dataUtil.setActivityName(IncomingVideoCallActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "acceptVC");
            }
        }

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