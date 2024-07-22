package hawaiiappbuilders.omniversapp.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckAppUpdateHelper {

    public interface SearchRestaurantCallback {
        void onFailed(String message);

        void onSuccess(boolean haveUpdates);
    }

    public static final String TAG = CheckAppUpdateHelper.class.getSimpleName();
    BaseFunctions baseFunctions;
    BaseActivity activity;
    AppSettings appSettings;

    SearchRestaurantCallback callback;

    int mode;

    public CheckAppUpdateHelper(BaseActivity _activity, SearchRestaurantCallback _callback) {
        this.activity = _activity;
        this.appSettings = new AppSettings(this.activity);

        this.callback = _callback;
        this.baseFunctions = new BaseFunctions(this.activity, TAG);

    }

    public void execute() {
        if (activity.getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            KTXApplication mMyApp = (KTXApplication) activity.getApplication();
            String baseUrl = BaseFunctions.getBaseUrl(activity, "CJLGet", BaseFunctions.MAIN_FOLDER,  activity.getUserLat(),
                    activity.getUserLon(), mMyApp.getAndroidId());
            String extraParams = "&mode=" + "appVer" +
                    "&sellerID=" + "0" +
                    "&misc=1";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);
            activity.showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(activity);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    activity.hideProgressDialog();

                    Log.e("appVer", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                sendError(jsonObject.getString("msg"));
                            } else {
                                int verMajor = jsonObject.getInt("VerMajor");
                                int verMiner = jsonObject.getInt("VerMiner");
                                int lastVerionCode = verMajor * 1000 + verMiner;

                                try {
                                    PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                    String version = pInfo.versionName;

                                    int curMajor = Integer.parseInt(version.substring(0, version.indexOf(".")));
                                    int curMinor = Integer.parseInt(version.substring(version.indexOf(".") + 1));
                                    int curVerionCode = curMajor * 1000 + curMinor;

                                    if (lastVerionCode > curVerionCode) {
                                        sendResult(true);
                                    } else {
                                        sendResult(false);
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();

                                    sendError("Couldn't check app updates!");
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    sendError("Couldn't check app updates!");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sendError("Couldn't check app updates!");
                        }
                    } else {
                        sendError("Couldn't check app updates!");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activity.hideProgressDialog();
                    baseFunctions.handleVolleyError(activity, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
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
        } else {
            sendError("Please allow the GPS to hit Server");
        }
    }

    private void sendResult(boolean haveUpdates) {
        if (callback != null) {
            callback.onSuccess(haveUpdates);
        }
    }

    private void sendError(String error) {
        if (callback != null) {
            callback.onFailed(error);
        }
    }
}
