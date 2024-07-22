package hawaiiappbuilders.omniversapp;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission_group.CAMERA;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;

public class ActivitySplash extends BaseActivity {
    public static final String TAG = ActivitySplash.class.getSimpleName();
    ImageView imgLogo;
    TextView tvTitle;
    View vSlogon;
//    TextView tvVersion;
    int retry;

    DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.e("eKyR2", K.eKy("22824"));

        Log.e("eKy", DateUtil.toStringFormat_38(new Date()));

        AppSettings appSettings = new AppSettings(this);
        appSettings.setIsAppKilled(false);
        dataUtil = new DataUtil(this, "ActivitySplash");
        retry = 0;
        int LL = 1;
        if (getIntent().getExtras() != null) {
            LL = 2;
            String message = getIntent().getExtras().getString("error");
            Log.e("ActivitySplash", message);
            if (message != null) {
                zzzLogItSplash(LL, message, "onCreate-function", ActivitySplash.class.getSimpleName());
            }
        }

        if (isPermissionGranted(POST_NOTIFICATIONS)) {
            startSplash();
        } else {
            showRationaleAndRequestPermission(this, CAMERA, new String[]{POST_NOTIFICATIONS}, PERMISSION_REQUEST_POST_NOTIFICATIONS);
        }

        /*Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.hack_date));

        String localTime = dateFormat.format(date);

        int utc = Integer.parseInt(appSettings.getUTC());
        String timezoneString = String.format("GMT%s%d:00", (utc >= 0 ? "+" : ""), utc);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezoneString));
        String apiTime = dateFormat.format(date);

        String[] hacks = apiTime.split("-");
        String Val = String.valueOf(Integer.parseInt(hacks[2]));

        String dateDebugString = String.format("LocalTime:%s\nAPI Time:%s\nUTC:%d", localTime, apiTime, utc);
        msg(dateDebugString);*/


        // appSettings.setUserId("1");
        // appSettings.setLoggedIn();
        // appSettings.logOut();
        // Firebase
        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ActivitySplash.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("Token1", newToken);
                appSettings.setDeviceToken(newToken);
            }
        });
        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                Log.e("Token2", task.getResult().getToken());
            }
        });*/

        imgLogo = findViewById(R.id.splashlogo);
        tvTitle = findViewById(R.id.tvTitle);
        vSlogon = findViewById(R.id.vSlogon);
//        tvVersion = findViewById(R.id.tvVersion);
        tvTitle.setText(Html.fromHtml("Everything Simple & Secure!"));
        //Giving <b>YOU</b> the Power to Connect!
//        tvVersion.setText(getVersionName());
        /*imgLogo.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imgLogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(imgLogo, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(imgLogo, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(imgLogo, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(800);
                        mAnimatorSet.start();
                    }
                });

        tvTitle.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tvTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(tvTitle, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(tvTitle, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(tvTitle, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(800);
                        mAnimatorSet.start();
                    }
                });

        tvSlogon.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tvTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(ObjectAnimator.ofFloat(tvSlogon, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(tvSlogon, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(tvSlogon, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(800);
                        mAnimatorSet.start();
                    }
                });*/


    }

    public void startSplash() {
        // should retry 100 times if not successful
        getToken();

        // Check App Status
        mMyApp.checkAppStatus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doMainAction();
            }
        }, SPLASH_DELAY_TIME);
    }


    public void getToken() {
        if (retry < 100) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> token) {
                    if (token.isSuccessful()) {
                        String newToken = token.getResult();
                        // Log.e("Token3", newToken);
                        Log.e("ActivitySplash", newToken);
                        appSettings.setTokenRetry(retry);
                        appSettings.setDeviceToken(newToken);
                    } else {
                        retry++;
                        Log.e("ActivitySplash", "Retry:getToken:" + retry);
                        getToken();
                    }
                }
            });
        }
    }

    private void doMainAction() {

        if (appSettings.isLoggedIn()) {
            //Intent homeMenuIntent = new Intent(mContext, ActivityHomeEvents.class);
            Intent homeMenuIntent = new Intent(mContext, ActivityHomeMenu.class);
            if (getIntent().getExtras() != null) {
                homeMenuIntent.putExtras(getIntent().getExtras());
            }
            startActivity(homeMenuIntent);
            finish();
        } else {
            // Call SecurityCk before login
            AppSettings appSettings = new AppSettings(mContext);
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "securityCk",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "1" +
                            "&WeMightNeedRefreshTokenLaterButNotInAppsNow=" + appSettings.getDeviceToken();
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("securityCk1", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            sr.setShouldCache(false);
            queue.add(sr);

            //startActivity(new Intent(mContext, ActivityLogin.class));
            startActivity(new Intent(mContext, ZAUHowItWorksActivity.class));
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_POST_NOTIFICATIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Notifications set to allowed");
                    startSplash();
                } else {
                    // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    startSplash();
                }
        }
    }
}

// https://developer.squareup.com/docs/in-app-payments-sdk/what-it-does

