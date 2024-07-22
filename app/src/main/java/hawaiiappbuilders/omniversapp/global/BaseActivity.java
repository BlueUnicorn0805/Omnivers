package hawaiiappbuilders.omniversapp.global;

import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.UPDATE_STATUS_ID;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Text_Message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import hawaiiappbuilders.omniversapp.ActivityHomeEvents;
import hawaiiappbuilders.omniversapp.ActivityHomeMenu;
import hawaiiappbuilders.omniversapp.ActivityIFareDashBoard;
import hawaiiappbuilders.omniversapp.ActivityLogin;
import hawaiiappbuilders.omniversapp.ActivityPermission;
import hawaiiappbuilders.omniversapp.ActivityReceiveValetConfirm;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.ZAUHowItWorksActivity;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.meeting.utilities.PreferenceManager;
import hawaiiappbuilders.omniversapp.messaging.BroadcastUtility;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

import io.agora.rtc2.ChannelMediaOptions;
//import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

public class BaseActivity extends AppCompatActivity {

    // Fill in the App ID obtained from the Agora Console
    private String AGORA_APP_ID = "6e34b78ced7648c98baf1be932017f4e";

    public RtcEngine mRtcEngine;


    public BaseFunctions baseFunctions;
    public static final int SPLASH_DELAY_TIME = 2000;
    public static final int INTRO_DELAY_TIME = 1000;
    //public static final String BASE_URL = "http://chuck.com/CJL/";
    //public static final String BASE_URL_F2F_REC = BASE_URL + "F2FRec.php?"; // referenced but method call is commented

    // public static final String BASE_URL_BUDGET = BASE_URL + "BudgetCJL.php?";
    // public static final String BASE_URL_FRIEND_LIST = BASE_URL + "RoloDexListCJL.php?";
    // public static final String BASE_URL_MAKE_PAYMENT = BASE_URL + "TXmoneyCJL.php?";
    // public static final String BASE_URL_ADD_EVENT = BASE_URL + "CalAdd.php?";
    //public static final String BASE_URL_ADD_NICKNAME = BASE_URL + "SaveNickTxtEmailCJL.php?";
    //public static final String BASE_URL_JOB_TPES = BASE_URL + "JobTypes.php?";
    //public static final String BASE_URL_JOB_LIST = BASE_URL + "JobsBizListCJL.php?";
    //public static final String BASE_URL_ADD_SKILLS = BASE_URL + "AddSkillResume.php?";
    //public static final String BASE_URL_READY_TO_WORK = BASE_URL + "ReadyToWork.php?";
    //public static final String BASE_URL_CALL_GET = BASE_URL + "CalGet.php?";
    public static final long LOGIN_TIME_OUT = 4 * 60 * 1000;

    protected static final int PERMISSION_REQUEST_CODE_QRSCAN = 100;
    protected static final String[] PERMISSION_REQUEST_QRSCAN_STRING = {Manifest.permission.CAMERA};

    public static final String[] PERMISSION_REQUEST_PHONE_STRING = {Manifest.permission.CALL_PHONE};
    protected static final int PERMISSION_REQUEST_CODE_LOCATION = 101;
    protected static final String[] PERMISSION_REQUEST_LOCATION_STRING = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    protected static final int PERMISSION_REQUEST_CODE_GALLERY = 102;
    protected static final String[] PERMISSION_REQUEST_GALLERY_STRING = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    protected static final int PERMISSION_REQUEST_POST_NOTIFICATIONS = 104;
    protected static final int PERMISSION_REQUEST_CODE_CAMERA = 103;
    protected static final int PERMISSION_REQUEST_WRITE_EXTERNAL = 105;
    protected static final int PERMISSION_REQUEST_READ_EXTERNAL = 106;
    protected static final String[] PERMISSION_REQUEST_CAMERA_STRING = {Manifest.permission.CAMERA/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/};

    protected static final int REQUEST_IMAGE = 2000;
    protected static final int REQUEST_AUTH = 200;
    protected static final int REQUEST_CHOOSE_VALET = 400;
    protected static final int REQUEST_SETTING = 300;
    protected static final int REQUEST_LOCATION = 500;

    private static ProgressDialog globalReference;
    HttpInterface httpInterface;
    String uuid;
    String userId;
    public static final int vCSecurityID = 0;
    protected AppSettings appSettings;

    public AppSettings getAppSettings() {
        return appSettings;
    }

    private static GsonBuilder mGsonBuilder;
    private static Gson mGson;
    private String TAG = BaseActivity.class.getSimpleName();

    protected KTXApplication mMyApp;

    protected Context mContext;
    protected ProgressDialog mProgress;

    DataUtil dataUtil;

    protected static final String ACTION_EXIT_APP = "com.c.getfoodpos.EXITAPP";
    BroadcastReceiver appExistListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO:  Check if Activity Home Menu is created

        }
    };

    protected WeakReference<BaseActivity> weakReference;

    private BroadcastReceiver statusIdUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    protected void onBroadcastReceived(Intent intent) {
        // Actions common to all activities extending this one can be done here in ExampleBase
        if (intent.getAction().equals(UPDATE_STATUS_ID)) {
            // check if ActivityHomeMenu is initialized
            int statusId = intent.getExtras().getInt("statusID");
            /*Intent localMsg = new Intent("receivedstatusid");
            localMsg.putExtra("statusID", statusId);
            LocalBroadcastManag\er.getInstance(mContext).sendBroadcast(localMsg);*/
            if (ActivityHomeMenu.activityStatus < 0) {
                Intent i = new Intent("receivedstatusidfromotherpage");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("statusID", statusId);
                startActivity(i);
            }
        }
    }


    public String getAndroidId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID", "Android ID: " + androidId);
        return androidId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMyApp = (KTXApplication) getApplication();
        mContext = this;
        dataUtil = new DataUtil(mContext, "BaseActivity");
        baseFunctions = new BaseFunctions(this, "BaseActivity");

        appSettings = new AppSettings(mContext);
        BroadcastUtility.Register(getApplication(), statusIdUpdate, UPDATE_STATUS_ID);
        weakReference = new WeakReference<>(this);
        mProgress = new ProgressDialog(mContext, R.style.DialogTheme);
        mProgress.setMessage(getString(R.string.loading));
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        LocalBroadcastManager.getInstance(mContext).registerReceiver(appExistListener, new IntentFilter(ACTION_EXIT_APP));

        if ("88c5395c86294668".equalsIgnoreCase(appSettings.getDeviceId())) {
            DEV_MODE = true;
        } else {
            DEV_MODE = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtility.Unregister(getApplication(), statusIdUpdate);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(appExistListener);

//        if(mRtcEngine != null) {
//            mRtcEngine.stopPreview();
//            // Leave the channel
//            mRtcEngine.leaveChannel();
//            RtcEngine.destroy();
//        }
    }

    protected void exitApp() {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ACTION_EXIT_APP));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**** SHOW PROGRESS DIALOG ***/

    public static void showProgressDlg(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        globalReference = progressDialog;

    }

    public static void hideProgressDlg() {
        if (globalReference != null && globalReference.isShowing()) {
            globalReference.dismiss();
        }
    }

    public void zzzLogIt(Throwable throwable, String apiName, String activityName) {
        dataUtil.setActivityName(activityName);
        dataUtil.zzzLogIt(throwable, apiName);
    }

    public void zzzLogMessage(String message, String apiName, String activityName) {
        dataUtil.setActivityName(activityName);
        dataUtil.zzzLogMessage(0, message, apiName);
    }

    public void zzzLogItSplash(String message, String apiName, String activityName) {
        dataUtil.setActivityName(activityName);
        dataUtil.zzzLogItSplash(message, apiName);
    }

    public void zzzLogItSplash(int LL, String message, String apiName, String activityName) {
        dataUtil.setActivityName(activityName);
        dataUtil.zzzLogItSplash(LL, message, apiName);
    }

    public void backToHome() {
        Intent i = new Intent(mContext, ActivityHomeEvents.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void showProgressDialog() {
        try {
            if (mProgress != null && !mProgress.isShowing()) {
                mProgress.show();
                mProgress.setContentView(R.layout.dialog_loading);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**** SHOW TOAST MESSAGE ****/
    public static void showMessage(Context mContext, String message) {
        Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
    }

    protected void getTemperature(TextView temperatuerTextView) {
        if (getLocation()) {
            String urlGetRes = String.format("https://api.weatherusa.net/v1/forecast?q=%s,%s&daily=0&units=e", getUserLat(), getUserLon());
            //String urlGetRes = String.format("https://api.weatherusa.net/v1/forecast?q=%s,%s&daily=0&units=e", "39.7642543", "-104.995537");
            Log.e("temp", urlGetRes);
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.GET, urlGetRes, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("temp", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (!jsonObject.isNull("temp")) {
                                int temp = jsonObject.optInt("temp");
                                String wxStr = jsonObject.optString("wx_str");

                                // Save the last temperature value and string
                                appSettings.setTemperatureLastValue(temp);
                                appSettings.setTemperatureLastString(wxStr);

                                if (appSettings.getTemperatureUnitStatus() == 0) {
                                    temperatuerTextView.setText(String.format("%d °F", jsonObject.optInt("temp")));
                                } else {
                                    temperatuerTextView.setText(String.format("%d °C", (int) fahrenheitToCelsius(jsonObject.optInt("temp"))));
                                }
                            } else {
                                temperatuerTextView.setText("");
                            }
                        } else {
                            temperatuerTextView.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        temperatuerTextView.setText("");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    baseFunctions.handleVolleyError(mContext, error, TAG, "forecast");

                    //showMessage(error.getMessage());
                }
            });

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    private double fahrenheitToCelsius(double fafrenheit) {
        double b = fafrenheit - 32;
        double c = b * 5 / 9;
        return c;
    }

    // TODO: Investigate crash when this method is called
    public static void showAlertMessage(Context mContext, String message) {
        // Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
        if (mContext != null) {
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setMessage(message)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
            androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
            if (!((Activity) mContext).isDestroyed()) {
                alertDialog.show();
            }
        }
    }

    public void openLink(String link) {
        if (TextUtils.isEmpty(link)) {
            showToastMessage("Invalid URL!");
        }

        String lowerCase = link.toLowerCase();
        if (!lowerCase.startsWith("http://") && !lowerCase.startsWith("https://")) {
            link = "http://" + link;
        }

        try {
            Uri linkUri = Uri.parse(link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, linkUri);
            startActivity(browserIntent);
        } catch (Exception e) {
            showToastMessage("Invalid URL!");
        }
    }

    public void showSuccessDialog(Context mContext, final View.OnClickListener actionClickListener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        final android.app.AlertDialog errorDlg = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();

                if (actionClickListener != null) {
                    actionClickListener.onClick(v);
                }
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void showSuccessDialog(Context mContext, String title, final View.OnClickListener actionClickListener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        final android.app.AlertDialog errorDlg = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();

                if (actionClickListener != null) {
                    actionClickListener.onClick(v);
                }
            }
        });

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(title);
        }

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /****** CHECK NETWORK CONNECTION *******/
    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /****** HANDLE NETWORK ERROR INSTANCE *******/
    public static void networkErrorHandle(Context mContext, VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            showMessage(mContext, "The request has either time out or there is no Connection.");
        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            showMessage(mContext, "There was an Authentication Failure.");
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            showMessage(mContext, "The Server is not responding.");
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            showMessage(mContext, "There was network error, check your network!");
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            showMessage(mContext, "The server response could not be parsed.");
        }
    }

    @SuppressLint("MissingPermission")
    public String getUUID() {
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public void login(final Context context, String username, String password, String code, boolean isBioAuto) {

        // TODO: Do preauth
        /*new ApiUtil(context).callApi(BaseFunctions.getPreAuthUrl(context), new ApiUtil.OnHandleApiResponseListener() {

            @Override
            public void onSuccess(String response) {

                S.getEncryptedFile(mContext).writeToEncryptedFile("filename", "data");
                S.getEncryptedSharedPreference(mContext).getEncryptedString("hello");




            }

            @Override
            public void onResponseError() {

            }

            @Override
            public void onServerError() {

            }
        });*/


        hideKeyboard();
        httpInterface = (HttpInterface) context;
        appSettings = new AppSettings(context);


        HashMap<String, String> params = new HashMap<>();
        String networkOperator = getNetworkOperator();
        String MCC = "MCC";
        String MNC = "MNC";
        if (!TextUtils.isEmpty(networkOperator)) {
            if (networkOperator.length() > 3) {
                MCC = networkOperator.substring(0, 3);
                MNC = networkOperator.substring(3);
            } else {
                MCC = networkOperator;
            }
        }

        String bio = "";
        if (isBioAuto) { // user used biometrics
            if (code.contentEquals("-081057")) { // fail
                bio = "";
            } else {
                bio = "PAST";
            }
        }

        String baseUrl = BaseFunctions.getBaseUrl(this,
                "zzzLogin",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&un=" + username +
                        "&pw=" + password +
                        "&token=" + appSettings.getDeviceToken() +
                        "&retry=" + appSettings.getTokenRetry() +
                        "&currmlid=" + appSettings.getUserId() +
                        "&userZAcode=" + code +
                        "&ver=" + String.format("MCC=%s,MNC=%s", MCC, MNC) +
                        "&hostMLID=" + "182" +
                        "&bio=" + bio;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        String finalBaseUrl = baseUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //networkErrorHandle(context, error);
                hideProgressDlg();
                baseFunctions.handleVolleyError(mContext, error, "BaseActivity", BaseFunctions.getApiName(finalBaseUrl));
                // showToastMessage("Please update app with new version");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    boolean processing = false;
    int valertServiceOption = R.id.btnOption1;

    public void showChooseValetDialog(boolean shouldFinish) {
        // float ratings = Float.parseFloat(jsonObject.getString("Rating"));
        JSONObject jsonObject = new JSONObject();

        float Rating = 0;
        String Co = "";
        int IndustryID = 0;
        String StreetNum = "";
        String Street = "";
        String City = "";
        String St = "";
        String CP = "";
        String Zip = "";
        String FN = "";
        String LN = "";

        final String[] options = new String[]{"", ""};
        final float[] optionPrice = new float[]{0f, 0f};
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_valet_option, null);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        ratingBar.setRating(Rating);

        TextView tvCo = dialogView.findViewById(R.id.tvCo);
        TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
        TextView tvCSZ = dialogView.findViewById(R.id.tvCSZ);
        TextView tvDriver = dialogView.findViewById(R.id.tvDriver);

        tvCo.setText(Co);
        tvAddress.setText(StreetNum + " " + Street);
        tvCSZ.setText(City + ", " + St + ", " + Zip);
        tvDriver.setText(FN + " " + LN);

        RadioButton btnOption1 = dialogView.findViewById(R.id.btnOption1);
        RadioButton btnOption2 = dialogView.findViewById(R.id.btnOption2);
        btnOption1.setText(String.format("($%.2f) %s", optionPrice[0], options[0]));
        btnOption2.setText(String.format("($%.2f) %s", optionPrice[1], options[1]));

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Questions
        RadioGroup groupOnTime = (RadioGroup) dialogView.findViewById(R.id.groupService);
        groupOnTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                valertServiceOption = i;
            }
        });
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDlg.dismiss();
                processing = false;
                if (shouldFinish)
                    finish();
            }
        });

        dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDlg.dismiss();

                startActivity(new Intent(mContext, ActivityReceiveValetConfirm.class));
                if (shouldFinish)
                    finish();

                final int finalCarMLID = 0;
                try {
                    JSONObject valetServiceData = new JSONObject();
                    if (valertServiceOption == R.id.btnOption1) {
                        valetServiceData.put("price", String.valueOf(optionPrice[0]));
                        valetServiceData.put("option", 0);
                    } else {
                        valetServiceData.put("price", String.valueOf(optionPrice[1]));
                        valetServiceData.put("option", 1);
                    }
                    appSettings.setValetOrder(valetServiceData.toString());
                    appSettings.setValetStore(String.valueOf(finalCarMLID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String pushTitle = "New Valet Request";
                String pushMessage = "New Valet Request";
                String appToken = appSettings.getDeviceToken();

                final ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                try {

                    NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                    if (!tokenList.isEmpty()) {
                        jsonObject.put("title", pushTitle);
                        jsonObject.put("message", pushMessage);
                        jsonObject.put("srcToken", appToken);
                        notificationHelper.sendPushNotification(mContext, tokenList, PT_Text_Message, jsonObject);
                    } else {
                        processing = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public String generateRandomPassword(int length) {
        try {
            // create a string of uppercase and lowercase characters and numbers
            String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
            String numbers = "0123456789";

            // combine all strings
            String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

            // create random string builder
            StringBuilder sb = new StringBuilder();

            // create an object of Random class
            Random random = new Random();

            // specify length of random string

            for (int i = 0; i < length; i++) {

                // generate random index number
                int index = random.nextInt(alphaNumeric.length());

                // get character specified by index
                // from the string
                char randomChar = alphaNumeric.charAt(index);

                // append the character to string builder
                sb.append(randomChar);
            }

            return sb.toString();
        } catch (Exception e) {
            return "pwd123";
        }
    }

    public void getInstaCash(final Context context, String lat, String lon) {
        hideKeyboard();
        httpInterface = (HttpInterface) context;
        appSettings = new AppSettings(context);

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                lat,
                lon,
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "AllBal" +
                        "&misc=" + "0";
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        //urlGetRes += fullParams.substring(1);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(context);

        String finalBaseUrl = baseUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                hideProgressDlg();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }


    public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    /*public void getBudget(final Context context, String startdate, String enddate, String monthnum) {
        httpInterface = (HttpInterface) context;

        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("startdate", startdate);
            jsonObject.put("enddate", enddate);
            jsonObject.put("monthnum", monthnum);
            jsonObject.put("uuid", appSettings.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = BASE_URL_BUDGET.concat("data=" + encoded.replaceAll("\\+", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void getFriends(final Context context, String lat, String lon, String friendid, String nick, String email, String cp) {
        httpInterface = (HttpInterface) context;

        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("devid", "");
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("friendid", friendid);
            jsonObject.put("nick", nick);
            jsonObject.put("email", email);
            jsonObject.put("cp", cp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = BASE_URL_FRIEND_LIST.concat("data=" + encoded.replaceAll("\\+", "%20"));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void saveNickName(final Context context, String lat, String lon, String nick,
                             String email, String cp, final HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("nick", nick);
            jsonObject.put("email", email);
            jsonObject.put("cp", cp);
            jsonObject.put("uuid", appSettings.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = BASE_URL_ADD_NICKNAME.concat("data=" + encoded.replaceAll("\\+", "%20"));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                _httpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void makePayment(final Context context, String lat, String lon, String friendid, String amt, HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("lat", lat);
            jsonObject.put("lon", lon);
            jsonObject.put("friendid", friendid);
            jsonObject.put("amt", amt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = BASE_URL_MAKE_PAYMENT.concat("data=" + encoded.replaceAll("\\+", "%20"));

        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    public void logoutUser(final Context context, String lat, String lon, boolean autoLogout) {

        showProgressDlg(context, "");
        appSettings = new AppSettings(context);

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                lat,
                lon,
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "LogOut" +
                        "&misc=" + "";
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        GoogleCertProvider.install(context);
        String finalBaseUrl = baseUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDlg();

                Log.e("logout", response);

                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                            showMessage(context, jsonObject.getString("msg"));
                        } else {
                            appSettings.clear();
                            appSettings.logOut();

                            // Log out for firestore
                            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                            FirebaseFirestore database = FirebaseFirestore.getInstance();

                            if (preferenceManager.getString(Constants.KEY_USER_ID) != null) {
                                DocumentReference documentReference =
                                        database.collection(Constants.KEY_COLLECTION_USERS).document(
                                                preferenceManager.getString(Constants.KEY_USER_ID)
                                        );
                                HashMap<String, Object> updates = new HashMap<>();
                                updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                                documentReference.update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            preferenceManager.clearPreferences();
                                        })
                                        .addOnFailureListener(e ->
                                                // Toast.makeText(mContext, "Unable to sign out", Toast.LENGTH_SHORT).show()
                                                Log.e("BaseActivity", " "));

                            }
                            if (autoLogout) {
                                showToastMessage("Auto Logout");
                                exitApp();
                            } else {
                                //Intent intent = new Intent(context, ActivityLogin.class);
                                Intent intent = new Intent(context, ZAUHowItWorksActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                hideProgressDlg();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

 /*   public void getBizList(final Context context, String latitude, String longitude, String empBool,
                           String location, String remote, String selectedJobsIds,
                           HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("lon", longitude);
            jsonObject.put("lat", latitude);
            jsonObject.put("empbool", empBool + "");
            jsonObject.put("remote", remote + "");
            jsonObject.put("onlocation", location + "");
            jsonObject.put("skills", selectedJobsIds.trim());
            jsonObject.put("appid", "seekurandroid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_JOB_LIST + "data=" + encoded.replaceAll("\\+", "%20");

        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void getJobTypes(final Context context, String latitude, String longitude,
                            HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("lon", longitude);
            jsonObject.put("lat", latitude);
            jsonObject.put("appid", "seekurandroid");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_JOB_TPES + "data=" + encoded.replaceAll("\\+", "%20");

        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/


   /* public void addSkills(final Context context, String latitude, String longitude,
                          HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("userid", appSettings.getUserId());
            jsonObject.put("siteid", "411359");
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("lat", latitude);
            jsonObject.put("lon", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_ADD_SKILLS + "data=" + encoded.replaceAll("\\+", "%20");

        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/
/*
    public void setReadyToWork(final Context context, String latitude, String longitude,
                               HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("mlid", appSettings.getUserId());
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("lon", longitude);
            jsonObject.put("lat", latitude);
            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("toggle", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_READY_TO_WORK + "data=" + encoded.replaceAll("\\+", "%20");

        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void F2FRec(final Context context, JSONObject jsonObject,
                       HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        try {
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("userid", appSettings.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_F2F_REC + "data=" + encoded.replaceAll("\\+", "%20");
        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    /*public void calAdd(final Context context, JSONObject jsonObject,
                       HttpInterface _httpInterface) {
        appSettings = new AppSettings(context);
        try {
            jsonObject.put("txid", "0");
            jsonObject.put("callid", "0");
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("userid", appSettings.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        Log.e(TAG, "calAdd: " + encoded);

        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String urlString = BASE_URL_ADD_EVENT + "data=" + encoded;//.replaceAll("\\+", "%20");
        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

/*
    public void calGet(final Context context, JSONObject jsonObject,
                       HttpInterface _httpInterface) {
        // statusid apptmlid callid txid
        appSettings = new AppSettings(context);
        try {
            jsonObject.put("txid", "0");
            jsonObject.put("callid", "0");
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("appid", "seekurandroid");
            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("userid", appSettings.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encoded = jsonObject.toString().trim();
        try {
            encoded = URLEncoder.encode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = BASE_URL_CALL_GET + "data=" + encoded.replaceAll("\\+", "%20");
        final HttpInterface finalHttpInterface = _httpInterface;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                finalHttpInterface.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorHandle(context, error);
                hideProgressDlg();
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }*/

    public static Gson getGson() {
        if (mGsonBuilder == null) {
            mGsonBuilder = new GsonBuilder();
        }

        if (mGson == null) {
            mGson = mGsonBuilder.create();
        }
        return mGson;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = BaseActivity.this.getCurrentFocus();
        if (view == null) {
            view = new View(BaseActivity.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Remove EditText Keyboard
    public void hideKeyboard(EditText et) {
        if (et != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    public boolean isEmailValid(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 5;
    }

    public String formatMoney(String number) {
        try {
            double amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            System.out.println(formatter.format(amount));
            return formatter.format(amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public String getVersionName() {
        PackageInfo pInfo = null;
        int curVerionCode = 0;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int pxToDp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public void navigateToLoginIfUserIsLoggedOut() {
        if (!new AppSettings(this).isLoggedIn()) {
            Intent startActivityIntent = new Intent();
            startActivityIntent.setClassName("com.ver1.dot", "com.ver1.dot.ActivityLogin");
            startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startActivityIntent);
        }
    }

    public void goToDashBoard(Activity activity) {
        Intent intent = new Intent(activity, ActivityIFareDashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected String getServiceProvider() {
        String telID = "";
        try {
            TelephonyManager tm =
                    (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            telID = tm.getSimOperatorName();
            if (TextUtils.isEmpty(telID)) {
                telID = tm.getNetworkOperatorName();
            }
        } catch (Exception e) {
            telID = e.getMessage();
        }
        if (TextUtils.isEmpty(telID)) {
            telID = "No Sim Info";
        }

        if (DEV_MODE) {
            return "China Telecom";
        } else {
            return telID;
        }
    }

    protected String getNetworkOperator() {
        String telID = "NetworkOper";
        try {
            TelephonyManager tm =
                    (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            telID = tm.getNetworkOperator();
        } catch (Exception e) {
            telID = e.getMessage();
        }
        if (TextUtils.isEmpty(telID)) {
            telID = "No Sim Info";
        }

        if (DEV_MODE) {
            return "46003";
        } else {
            return telID;
        }
    }

    protected String getNetworkCountryIso() {
        String telID = "NetworkOper";
        try {
            TelephonyManager tm =
                    (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            telID = tm.getNetworkCountryIso();
        } catch (Exception e) {
            telID = e.getMessage();
        }
        if (TextUtils.isEmpty(telID)) {
            telID = "No Sim Info";
        }

        return telID;
    }

    protected boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            userLat = "0";
            userLon = "0";
            return false;
        } else {
            return true;
        }
    }

    // User Location
    private static boolean DEV_MODE = false;
    protected String userLat = "0", userLon = "0";

    public boolean getLocation() {
        if (appSettings.getLocationPermission() == 1) {
            // In case of user Accept Location
            GpsTracker gpsTracker = new GpsTracker(mContext);
            if (gpsTracker.canGetLocation()) {
                userLat = String.valueOf(gpsTracker.getLatitude());
                userLon = String.valueOf(gpsTracker.getLongitude());

                // Save User Lat and Lon
                appSettings.setLastLocationLat(userLat);
                appSettings.setLastLocationLon(userLon);

                return true;
            } else {
                gpsTracker.showSettingsAlert();
                return false;
            }
        } else {
            // In case of unknown or Deny
            startActivityForResult(new Intent(mContext, ActivityPermission.class), REQUEST_LOCATION);
            return false;
        }
    }

    public String getUserLat() {
        if (DEV_MODE || appSettings.getDeviceId().contains("86294668")) {
            return "41.797273";

        } else {
            return appSettings.getLastLocationLat();
        }
    }

    public String getUserLon() {
        if (DEV_MODE || appSettings.getDeviceId().contains("86294668")) {
            return "123.4287958";
        } else {
            return appSettings.getLastLocationLon();
        }
    }

    protected void shareApp() {
        /*try {
            // Uri imageUri = Uri.fromFile(imageFile);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
            // shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String chooserTitle = getResources().getString(R.string.share_title);
            startActivity(Intent.createChooser(shareIntent, chooserTitle));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent == null) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
            try {
                startActivity(intent);
            } catch (Exception e) {
                showToastMessage("Please install Email App to use function");
            }
        } else {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_body));
            //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send Location Mail"));
        }
    }

    public void showToastMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastMessage(int msgId) {
        Toast.makeText(mContext, msgId, Toast.LENGTH_SHORT).show();
    }

    public void showAlert(int resId) {
        String alertMsg = getString(resId);
        showAlert(alertMsg, null);
    }

    public void showAlert(int resId, View.OnClickListener clickListener) {
        String alertMsg = getString(resId);
        showAlert(alertMsg, clickListener);
    }

    public void showAlert(String message) {
        showAlert(message, null);
    }

    public void showAlert(Context context, String message) {
        showAlert(message, null);
    }

    public void showAlert(Context context, String message, final View.OnClickListener clickListener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_error, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        tvAlert.setText(message);
        dialogView.findViewById(R.id.btnCloseAlert).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public void showAlert(String message, final View.OnClickListener clickListener) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_error, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView tvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        tvAlert.setText(message);
        dialogView.findViewById(R.id.btnCloseAlert).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public Uri getFileUri(File downloadedFile) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Uri fileUri = Uri.fromFile(downloadedFile);
            return fileUri;
        } else {
            Uri fileUri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    getPackageName() + ".provider", // (use your app signature + ".provider" )
                    downloadedFile);
            return fileUri;
        }
    }

    public void msg(int resId) {
        String msg = getString(resId);
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    public void msg(String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alert.show();
    }

    public void msg(String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alert.setTitle(title);
        alert.setIcon(R.mipmap.ic_launcher1_foreground);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alert.show();
    }

    public void msg(String msg, View.OnClickListener onClickListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alert.setMessage(msg);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (onClickListener != null) {
                            onClickListener.onClick(null);
                        }
                    }
                });
        AlertDialog alertDialog = alert.show();
    }

    /**
     * Function to show settings alert dialog
     */
    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE_LOCATION);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // This will be used in Android6.0(Marshmallow) or above
    public static boolean isPermissionsAllowed(Context context, String[] permissions) {

        if (permissions == null || permissions.length == 0)
            return true;

        boolean allPermissionSetted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionSetted = false;
                break;
            }
        }

        return allPermissionSetted;
    }

    public static boolean checkPermissions(Context context, String[] permissions, boolean showHintMessage, int requestCode) {

        if (permissions == null || permissions.length == 0)
            return true;

        boolean allPermissionSetted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionSetted = false;
                break;
            }
        }

        if (allPermissionSetted)
            return true;

        // Should we show an explanation?
        boolean shouldShowRequestPermissionRationale = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                shouldShowRequestPermissionRationale = true;
                break;
            }
        }

        if (showHintMessage && shouldShowRequestPermissionRationale) {
            // Show an expanation to the user *asynchronously* -- don't
            // block
            // this thread waiting for the user's response! After the
            // user
            // sees the explanation, try again to request the
            // permission.
            String strPermissionHint = context.getString(R.string.request_permission_hint);
            Toast.makeText(context, strPermissionHint, Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
        } else {
        }
    }

    // Hit Server
    public static final int FITSERVER_DIRS = 1;
    public static final int FITSERVER_QRSCAN = 2;
    public static final int FITSERVER_Menus = 3;
    public static final int FITSERVER_ResTabs = 5;
    public static final int FITSERVER_Favs = 6;
    public static final int FITSERVER_Vids = 7;
    public static final int FITSERVER_Cater = 8;
    public static final int FITSERVER_Party = 9;
    public static final int FITSERVER_Ads = 10;
    public static final int FITSERVER_Push = 11;
    public static final int FITSERVER_DELIVERY = 12;
    public static final int FITSERVER_SideWalk = 14;
    public static final int FITSERVER_SPIN = 15;

    public static final int FITSERVER_MAP = 18;

    public static final int FITSERVER_HPCONNECT = 30;
    public static final int FITSERVER_CERTICON = 31;
    public static final int FITSERVER_VIEWCERT = 32;
    public static final int FITSERVER_CERTBRONZE = 33;
    public static final int FITSERVER_CERTSILVER = 34;
    public static final int FITSERVER_CERTGOLD = 35;

    public static final int FITSERVER_CURBSIDE = 41;
    public static final int FITSERVER_DONATE = 42;

    public void hitServer(int area, String industryId, int storeid) {
        String pagename = "results";
        if (area == FITSERVER_SideWalk) {
            pagename = "HP";
        } else if (area == FITSERVER_Ads) {
            pagename = "search";
        } else if (area == FITSERVER_SPIN) {
            pagename = "spin";
        } else if (area == FITSERVER_QRSCAN) {
            pagename = "QR";
        }

        if (mContext != null) {

            if (area == FITSERVER_Favs) {
                //http://localhost:60611/main/CJLaddToFavs?BuyerID=1&SellerID=10&UUID=uuuuuu
                /*String urlGetResFormat = URLResolver.getBaseUrl() + "CJLaddToFavs?" +
                        "P1=7&R1=8" +
                        "&buyerID=%s" +
                        "&industryID=%s" +
                        "&sellerID=%d" +
                        "&appname=nemtandroid" +
                        "&devID=1434741" +
                        "&UUID=%s" +
                        "&empID=%s";

                String urlHitServer = String.format(urlGetResFormat, appSettings.getUserId(), industryId, storeid, appSettings.getDeviceId(), appSettings.getEmpId());*/
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLaddToFavs",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&buyerID=" + appSettings.getUserId() +
                                "&industryID=" + industryId +
                                "&sellerID=" + storeid;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //Log.e("Fav", urlHitServer);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("AddFav", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AddFav", error.toString());
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);

            } else if (area == FITSERVER_DIRS || area == FITSERVER_Vids) {
                /*String urlGetResFormat = URLResolver.getBaseUrl() +
                        "save1Hit?" +
                        "P1=7" +
                        "&R1=8" +
                        "&sellerID=%d" +
                        "&buyerID=%s" +
                        "&appname=nemtandroid" +
                        "&devID=1434741" +
                        "&buttonID=%d" +
                        "&uuid=%s";

                String urlHitServer = String.format(urlGetResFormat, storeid, appSettings.getUserId(), area, appSettings.getDeviceId());*/
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "save1Hit",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&sellerID=" + storeid +
                                "&buyerID=" + appSettings.getUserId() +
                                "&buttonID=" + area;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                //Log.e("Hitserver", "area = " + area);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Hitserver", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Hitserver", error.toString());
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } else {
                /*String urlGetResFormat = URLResolver.getBaseUrl() + "save1Hit?P1=7&R1=8&sellerID=%d&buyerID=%s&appname=nemtandroid&devID=1434741&buttonID=%d&uuid=%s";

                String urlHitServer = String.format(urlGetResFormat, storeid, appSettings.getUserId(), area, appSettings.getDeviceId());*/
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "save1Hit",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&sellerID=" + storeid +
                                "&buyerID=" + appSettings.getUserId() +
                                "&buttonID=" + area;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                //Log.e("Hitserver", "area = " + area);
                //Log.e("HitserverURL", urlHitServer);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Hitserver", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Hitserver", error.toString());
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            }
        }
    }

    public void hit1Server(int area, int storeid) {

        if (mContext != null) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "save1Hit",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&sellerID=" + String.valueOf(storeid) +
                            "&buyerID=" + appSettings.getUserId() +
                            "&buttonID=" + String.valueOf(area);
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            // Main/save1Hit?buttonID=xxx&sellerID=1&buyerID=2&devID=yours&appname=nemtandroid&uuid=1234

            //String urlHitServer = String.format(urlGetResFormat, storeid, appSettings.getUserId(), area, mMyApp.getAndroidId());
            RequestQueue queue = Volley.newRequestQueue(mContext);

            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl/*urlHitServer*/, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.e("Hitserver", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Hitserver", error.toString());
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            queue.add(sr);

        }
    }

    protected int stringToInf(String strValue) {
        if (TextUtils.isEmpty(strValue) || strValue.equalsIgnoreCase("NULL")) {
            return 0;
        } else {
            return Integer.parseInt(strValue.trim());
        }
    }

    protected double stringToDouble(String strValue) {
        if (TextUtils.isEmpty(strValue) || strValue.equalsIgnoreCase("NULL")) {
            return 0.0;
        } else {
            return Double.parseDouble(strValue.trim());
        }
    }

    public boolean needsLoginStatusFromResponse(String apiResponse) {
        try {
            JSONArray jsonArray = new JSONArray(apiResponse);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            boolean isRequiredLoginNow = false;

            String msgData = jsonObject.getString("msg");
            if (msgData.startsWith("[{")) {
                JSONArray jsonDataArray = new JSONArray(msgData);
                // [{"status":true,"msg":"[{\"theCode\":360177,\"WeStoppedAcct\":0,\"OwnerStoppedAcct\":0,\"cID\":187207,\"StreetNum\":\"\",\"FN\":\"fh\",\"LN\":\"cb\",\"PIN\":\"1234\",\"CP\":\"(+18) 685-6885\",\"address\":\" \",\"City\":\"\",\"St\":\"\",\"Zip\":\"\",\"marital\":\"S\",\"gender\":\"M\"}]"}]
                JSONObject jsonData = jsonDataArray.getJSONObject(0);
                if (jsonData.has("requireLoginNow") && jsonData.optInt("requireLoginNow") > 7) {
                    isRequiredLoginNow = true;
                }
            } else {
                if (jsonObject.has("requireLoginNow") && jsonObject.optInt("requireLoginNow") > 7) {
                    isRequiredLoginNow = true;
                }
            }

            if (isRequiredLoginNow) {
                showToastMessage("You are already Setup. Please Login.");

                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToastMessage(e.getMessage());
        }

        return false;
    }

    MediaPlayer chinchinPlayer;

    public void playChinChin(int statusID) {
        // Play Sound
        try {
            if (chinchinPlayer != null && chinchinPlayer.isPlaying()) {
                chinchinPlayer.stop();
                chinchinPlayer.release();
            }
            chinchinPlayer = new MediaPlayer();

            String chinechineFile = "chinchin2.mp3";
            if (statusID == 2030) {
                chinechineFile = "chinchin10.mp3";
            }

            AssetFileDescriptor descriptor = getAssets().openFd(chinechineFile);
            chinchinPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            chinchinPlayer.prepare();
            chinchinPlayer.setVolume(1f, 1f);
            chinchinPlayer.setLooping(false);
            chinchinPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        vibrate(500);
    }

    public void vibrate(int millis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(millis);
        }
    }

    public void showRationaleAndRequestPermission(Activity activity, String permissionRationale, String[] permissions, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRationale)) {
            showExplanation(activity, "Permission Needed", "Rationale", permissions, requestCode);
        } else {
            requestPermission(activity, permissions, requestCode);
        }
    }

    public boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void showExplanation(Activity activity, String title,
                                String message,
                                final @NonNull String[] permissions,
                                final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(activity, permissions, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    public void requestPermission(Activity activity, final @NonNull String[] permissions, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity,
                permissions, permissionRequestCode);
    }

    public void agoraInit(){
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = AGORA_APP_ID;
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAgoraEventHandler(IRtcEngineEventHandler mRtcEventHandler){
        mRtcEngine.removeHandler(mRtcEventHandler);
        mRtcEngine.addHandler(mRtcEventHandler);
    }

    public void stopAgoraPreview(){
        mRtcEngine.stopPreview();
    }

    public void setAgoraLocalPreview(FrameLayout localViewContainer){
        // Enable the video module
        mRtcEngine.enableVideo();

// Enable local preview
        mRtcEngine.startPreview();

// Create a SurfaceView object and make it a child object of FrameLayout
        SurfaceView surfaceView = new SurfaceView(getBaseContext());
        localViewContainer.addView(surfaceView);
// Pass the SurfaceView object to the SDK and set the local view
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));

    }

    public void setupRemoteVideo(int uid, FrameLayout remoteContainer) {

        SurfaceView surfaceView = new SurfaceView (getBaseContext());
//        surfaceView.setZOrderMediaOverlay(true);
        remoteContainer.addView(surfaceView);
        // Pass the SurfaceView object to the SDK and set the remote view
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }


    public void joinChannel(String token, String channelName){
        // Create an instance of ChannelMediaOptions and configure it
        ChannelMediaOptions options = new ChannelMediaOptions();
// Set the user role to BROADCASTER or AUDIENCE according to the scenario
        options.clientRoleType = io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER;
// In the live broadcast scenario, set the channel profile to COMMUNICATION (live broadcast scenario)
        options.channelProfile = io.agora.rtc2.Constants.CHANNEL_PROFILE_COMMUNICATION;

// Use the temporary token to join thea channel
// Specify the user ID yourself and ensure it is unique within the channel
        mRtcEngine.joinChannel(token, channelName, 0, options);

    }

    public void initializeAndJoinChannel(String appId, String channelName, String token, FrameLayout localContainer, IRtcEngineEventHandler mRtcEventHandler) {
        try {
            // Create an RtcEngineConfig instance and configure it
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            // Create and initialize an RtcEngine instance
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
        // Enable the video module
        mRtcEngine.enableVideo();

        // Enable local preview
        mRtcEngine.startPreview();

        // Create a SurfaceView object and make it a child object of FrameLayout

        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        localContainer.addView(surfaceView);
        // Pass the SurfaceView object to the SDK and set the local view
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));

        // Create an instance of ChannelMediaOptions and configure it
        ChannelMediaOptions options = new ChannelMediaOptions();
        // Set the user role to BROADCASTER or AUDIENCE according to the scenario
        options.clientRoleType = 1;
        // In the video calling scenario, set the channel profile to CHANNEL_PROFILE_COMMUNICATION
        options.channelProfile = 0;

        // Join the channel using a temporary token and channel name, setting uid to 0 means the engine will randomly generate a username
        // The onJoinChannelSuccess callback will be triggered upon success
        mRtcEngine.joinChannel(token, channelName, 0, options);
    }

    public void agoraDestroy(){
        mRtcEngine.stopPreview();
        // Leave the channel
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }
}
