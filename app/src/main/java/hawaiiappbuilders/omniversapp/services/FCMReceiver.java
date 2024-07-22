package hawaiiappbuilders.omniversapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class FCMReceiver extends BroadcastReceiver {
    private static final String TAG = FCMReceiver.class.getSimpleName();
    BaseFunctions baseFunctions;

    @Override
    public void onReceive(Context context, Intent intent) {
        baseFunctions = new BaseFunctions(context, TAG);
        AppSettings appSettings = new AppSettings(context);
        boolean isAppNotOpened = appSettings.isAppKilled();

//        Log.i("-----------FCMReceiver---------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (isAppNotOpened) {
            if (intent.getAction().equalsIgnoreCase("com.google.android.c2dm.intent.RECEIVE")) {
                JSONObject json = new JSONObject();
                Bundle bundle = intent.getExtras();
                Log.d(TAG, "onReceive: "+intent);
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    try {
                        json.put(key, JSONObject.wrap(bundle.get(key)));
                    } catch (JSONException e) {
                        //Handle exception here
                    }
                }
                try {
                    int payloadType = json.getInt("payloadtype");
                    Log.d(TAG, "onReceive:payloadType >> "+payloadType );
                    String message = json.getString("message");
                    Log.d(TAG, "onReceive:message >>  "+message);
                    String msg = String.format("%s,%s", payloadType, message);
                    String payloadString = json.getString("payloads");
                    Log.i("FCMReceiver", "New payload received: " + payloadString);
                    // sendPayloadToServer(context, msg.replace(" ", "%20"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getAndroidId(Context context) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID", "Android ID: " + androidId);
        return androidId;
    }

    double userLat;
    double userLon;

    private void getLocationUpdates(Context context) {
        AppSettings appSettings = new AppSettings(context);
        if (appSettings.getLocationPermission() == 1) {
            GpsTracker gpsTracker = new GpsTracker(context);
            if (gpsTracker.canGetLocation()) {
                userLat = gpsTracker.getLatitude();
                userLon = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    private void sendPayloadToServer(Context context, String payloads) {
        getLocationUpdates(context);
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(context,
                "PMsIgnore",
                BaseFunctions.MAIN_FOLDER,
                String.valueOf(userLat),
                String.valueOf(userLon),
                getAndroidId(context));

        String extraParams = "&msg=" + payloads;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        RequestQueue queue = Volley.newRequestQueue(context);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(context);

        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("PMsIgnored", response);
                if (response != null && !response.isEmpty()) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                baseFunctions.handleVolleyError(context, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
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
}