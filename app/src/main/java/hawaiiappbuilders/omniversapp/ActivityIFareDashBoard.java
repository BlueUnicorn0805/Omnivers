package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityIFareDashBoard extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityIFareDashBoard.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifare_dashboard);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ship/Receive");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        findViewById(R.id.btnRequestNew).setOnClickListener(this);
        findViewById(R.id.btnViewDelsStatus).setOnClickListener(this);
        findViewById(R.id.tvStartHere).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnRequestNew) {
            //startActivity(new Intent(mContext, NewDeliveryActivity.class));
            startActivity(new Intent(mContext, ActivityRecentDels.class));
        } else if(viewId == R.id.btnToolbarHome) {
          backToHome();
        }else if (viewId == R.id.btnViewDelsStatus) {
            startActivity(new Intent(mContext, iFareDelsActivity.class));
            /*if (TextUtils.isEmpty(appSettings.getDriverID())) {
                GetMyDriverID();
            } else {
                startActivity(new Intent(mContext, iFareDelsActivity.class));
            }*/
        } else if (viewId == R.id.tvStartHere) {
            openLink("https://play.google.com/store/apps/details?id=hawaiiappbuilders.udx");
        }
    }

    private void GetMyDriverID() {

        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "DelGetID" +
                            "&industryID=" + "80" +
                            "&sellerID=" + "0" +
                            "&misc=" + "";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("CJLGet", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("DriverID") && !jsonObject.isNull("DriverID")) {
                                appSettings.setDriverID(jsonObject.getString("DriverID"));

                                startActivity(new Intent(mContext, iFareDelsActivity.class));
                            } else {
                                showToastMessage("No Current Orders Pending");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
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
        }
    }
}