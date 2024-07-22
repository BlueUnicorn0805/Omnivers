package hawaiiappbuilders.omniversapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;

import org.devio.rn.splashscreen.SplashScreen;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.localdb.ContactsDataSource;
import hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class KTXApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private static KTXApplication INSTANCE;

    public static KTXApplication getInstance() {
        return INSTANCE;
    }

    Uri defaultRingtoneUri;
    Ringtone defaultRingtone;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /*public static GooglePayChargeClient createGooglePayChargeClient(Activity activity) {
        KTXApplication application = (KTXApplication) activity.getApplication();
        return new GooglePayChargeClient(application.chargeCallFactory);
    }*/


    // private ChargeCall.Factory chargeCallFactory;

    AppSettings mAppSettings;

    int mCntActiveActivities = 0;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // localizationDelegate.onConfigurationChanged(this);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Log.e("ResList", "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Log.e("ResList", "Portrait");
        }

        mAppSettings.setAppOrientation(newConfig.orientation);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        INSTANCE = this;
        defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
        defaultRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri);
        // Use Android ID
        mAppSettings = new AppSettings(this);
        TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mAppSettings.setDeviceId(androidId);


        // Retrofit retrofit = ConfigHelper.createRetrofitInstance();
        // chargeCallFactory = new ChargeCall.Factory(retrofit);

        /*CardEntryBackgroundHandler cardHandler =
                new CardEntryBackgroundHandler(chargeCallFactory, getResources());
        CardEntry.setCardNonceBackgroundHandler(cardHandler);*/

        mCntActiveActivities = 0;
        this.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        setCrashHandler();
    }

    private Boolean checkEmulator() {
        boolean isEmulator = (Build.MANUFACTURER.contains("Asus")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase().contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "vbox86"
                || Build.HARDWARE.toLowerCase().contains("nox")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.PRODUCT == "sdk"
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "vbox86p"
                || Build.PRODUCT.toLowerCase().contains("nox")
                || Build.BOARD.toLowerCase().contains("nox")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")));
        return isEmulator;
    }

    public String getAndroidId() {
        if (checkEmulator()) {
            return "uuidEmpty";
        }
        TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID", "Android ID: " + androidId);

        if(androidId.length() < 7){
            return "uuidEmpty";
        }

        return androidId;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mCntActiveActivities++;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        AppFirebaseMessagingService.setAppInBackground(false);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppFirebaseMessagingService.setAppInBackground(false);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        AppFirebaseMessagingService.setAppInBackground(true);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mCntActiveActivities--;

        if (false && mCntActiveActivities == 0) {
            if (!mAppSettings.isLoggedIn())
                return;

            GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
            String lat, lon;
            if (gpsTracker.canGetLocation()) {
                lat = String.valueOf(gpsTracker.getLatitude());
                lon = String.valueOf(gpsTracker.getLongitude());
            } else {
                lat = "0.0";
                lon = "0.0";
            }
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    lat,
                    lon,
                    getAndroidId());
            String extraParams = "&misc=";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            GoogleCertProvider.install(KTXApplication.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                            } else {
                                mAppSettings.clear();
                                mAppSettings.logOut();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            stringRequest.setShouldCache(false);
            VolleySingleton.getInstance(KTXApplication.this).addToRequestQueue(stringRequest);
        }
    }

    @SuppressLint("Range")
    public void getContactList(ArrayList<ContactInfo> contactInfoArrayList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if ((cur != null ? cur.getCount() : 0) > 0) {

                    ContactsDataSource contactsDataSource = new ContactsDataSource(KTXApplication.this);
                    contactsDataSource.open();


                    while (cur != null && cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        // If Name exists, then try to get other informations
                        if (!TextUtils.isEmpty(name)) {

                            ContactInfo newContactInfo = new ContactInfo();
                            newContactInfo.setName(name);

                            String phoneNumber = "";
                            String emailAddr = "";

                            // Get Phone Numbers
                            if (cur.getInt(cur.getColumnIndex(
                                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor phoneCur = cr.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{id}, null);

                                while (phoneCur.moveToNext()) {
                                    String phoneNo = phoneCur.getString(phoneCur.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    //phoneNo = phoneNo.replaceAll("[^0-9]+","");
                                    newContactInfo.addNewPhone(phoneNo);

                                    phoneNumber += "," + phoneNo;
                                }
                                phoneCur.close();
                            }

                            // Get Emails
                            Cursor emailCur = cr.query(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                    new String[]{id}, null);

                            while (emailCur.moveToNext()) {
                                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                                newContactInfo.addNewEmail(email);

                                emailAddr += "," + email;
                            }
                            emailCur.close();

                            String userInfo = String.format("Name: %s, Phone: %s, Email: %s", name, phoneNumber, emailAddr);
                            Log.e("contacts", userInfo);

                            // Add to the list
                            contactInfoArrayList.add(newContactInfo);

                            // Save new contact info
                            contactsDataSource.createUserInfo(newContactInfo);
                        }
                    }

                    contactsDataSource.close();
                }

                if (cur != null) {
                    cur.close();
                }
            }
        }).run();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onAppBackgrounded() {
        Log.e("Awww", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onAppForegrounded() {
        Log.e("Awww", "App in foreground");

        // don't call securityCk when app returns in foreground
        // checkAppStatus();
    }

    public void playVideoCallRingtone() {

        if (defaultRingtone != null && !defaultRingtone.isPlaying()) {
            defaultRingtone.play();
        }
    }

    public void stopVideoCallRingtone() {
        if (defaultRingtone != null && defaultRingtone.isPlaying()) {
            defaultRingtone.stop();
        }
    }

    public void checkAppStatus() {

        AppSettings appSettings = new AppSettings(this);

        if (!appSettings.isLoggedIn()) return;
        /*GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
        String lat, lon;
        if(gpsTracker.canGetLocation()) {
            lat = String.valueOf(gpsTracker.getLatitude());
            lon = String.valueOf(gpsTracker.getLongitude());
        } else {
            lat = "0.0";
            lon = "0.0";
        }*/
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "securityCk",
                BaseFunctions.MAIN_FOLDER,
                appSettings.getLastLocationLat(),
                appSettings.getLastLocationLon(),
                getAndroidId());
        String extraParams = "&mode=1" +
                "&WeMightNeedRefreshTokenLaterButNotInAppsNow=" + appSettings.getDeviceToken();
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        /*String fullParams = "";
        for (String key : params.keySet()) {
            fullParams += String.format("&%s=%s", key, params.get(key));
        }*/

        RequestQueue queue = Volley.newRequestQueue(this);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(this);

        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("securityCk2", response);

                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            appSettings.clear();
                            appSettings.logOut();

                            Intent intent = new Intent(KTXApplication.this, ActivityLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
    }

    private void setCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
    }

    public class CrashHandler implements Thread.UncaughtExceptionHandler {

        private static final String LINE_SEPARATOR = "\n";
        private final Context myContext;

        public CrashHandler(Context context) {
            this.myContext = context;
        }

        @Override
        public void uncaughtException(@NonNull Thread thread, Throwable exception) {

            Intent intent = new Intent(myContext, ActivitySplash.class);
            intent.putExtra("error", exception.getMessage());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            myContext.startActivity(intent);

            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }
}
