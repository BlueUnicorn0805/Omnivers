package hawaiiappbuilders.omniversapp.server;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ApiUtil {

    private static final String TAG = ApiUtil.class.getSimpleName();
    BaseFunctions baseFunctions;
    Context context;

    WeakReference<BaseActivity> weakReference;

    KTXApplication app;

    OnHandleApiResponseListener callback;

    AppSettings appSettings;

    // Callback interface for API
    public interface OnHandleApiResponseListener {
        void onSuccess(String response);

        void onResponseError(String msg);

        void onServerError();
    }

    public ApiUtil(Context context) {
        this.context = context;
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            weakReference = new WeakReference<>(activity);
        }

        app = (KTXApplication) context.getApplicationContext();
        appSettings = new AppSettings(context);
        this.baseFunctions = new BaseFunctions(this.context, TAG);
    }

    /**
     * @param fullUrl The API url.
     */
    public void callApi(String fullUrl, OnHandleApiResponseListener callback) {
        this.callback = callback;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, fullUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // TODO:  Handle success response here
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    final JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                        if (jsonObject.has("msg")) {
                            responseError(jsonObject.getString("msg"));
                        } else {
                            responseError("An error occurred");
                        }
                    } else {
                        responseSuccess(response);
                    }
                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // baseFunctions.handleVolleyError(context, error, "ApiUtil", BaseFunctions.getApiName(fullUrl));
                responseError("An error occurred");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return null;
            }
        };
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void responseError(String msg) {
        // Check Callback Status
        if (callback == null) return;

        // Check Activity Status
        if (weakReference != null) {
            BaseActivity weakActivity = weakReference.get();
            if (weakActivity == null || weakActivity.isFinishing()) {
                return;
            }
        }

        callback.onResponseError(msg);
    }

    private void responseSuccess(String response) {
        // Check Callback Status
        if (callback == null) return;

        // Check Activity Status
        if (weakReference != null) {
            BaseActivity weakActivity = weakReference.get();
            if (weakActivity == null || weakActivity.isFinishing()) {
                return;
            }
        }

        callback.onSuccess(response);
    }
}
