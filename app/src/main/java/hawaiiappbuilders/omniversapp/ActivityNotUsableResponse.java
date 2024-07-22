package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityNotUsableResponse extends BaseActivity implements View.OnClickListener {

    TextView tvScannedData;
    String scannedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_not_usable_response);

        if (getIntent().getExtras() != null) {

            initClicks();

            tvScannedData = findViewById(R.id.tvScannedData);
            scannedData = getIntent().getStringExtra("response");
            tvScannedData.setText(scannedData);

            addQRscan();
        }
    }

    private void initClicks() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        findViewById(R.id.web).setOnClickListener(this);
        findViewById(R.id.amazon).setOnClickListener(this);
        findViewById(R.id.ebay).setOnClickListener(this);
        findViewById(R.id.walmart).setOnClickListener(this);
        findViewById(R.id.best_buy).setOnClickListener(this);
        findViewById(R.id.target).setOnClickListener(this);
        findViewById(R.id.home_depot).setOnClickListener(this);
        findViewById(R.id.open_food_facts).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            finish();
        } else if (viewId == R.id.web) {
            openURL("https://www.google.com/search?q=" + tvScannedData.getText().toString());
        } else if (viewId == R.id.amazon) {
            openURL("https://www.amazon.com/s?k=" + tvScannedData.getText().toString() + "&tag=qrbot-android-20");
        } else if (viewId == R.id.ebay) {
            openURL("https://www.ebay.com/sch/i.html?_nkw=" + tvScannedData.getText().toString() + "&mkcid=1&mkrid=711-53200-19255-0&campid=5338078982&toolid=20008&mkevt=1");
        } else if (viewId == R.id.walmart) {
            openURL("https://www.walmart.com/search/?query=" + tvScannedData.getText().toString());
        } else if (viewId == R.id.best_buy) {
            openURL("https://www.bestbuy.com/site/searchpage.jsp?st=" + tvScannedData.getText().toString());
        } else if (viewId == R.id.target) {
            openURL("https://www.target.com/s?searchTerm=" + tvScannedData.getText().toString() + "&clkid=79c48995N1ed711efa4c083a1f3fdd850&cpng=PTID1&lnm=81938&afid=TeaCapps+GmbH&ref=tgt_adv_xasd0002&btn_ref=srctok-756545101ac0a2d2");
        } else if (viewId == R.id.home_depot) {
            openURL("https://www.homedepot.com/s/" + tvScannedData.getText().toString());
        } else if (viewId == R.id.open_food_facts) {
            openURL("https://world.openfoodfacts.org/product/" + tvScannedData.getText().toString());
        }
    }

    private void openURL(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mMyApp, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addQRscan() {
        HashMap<String, String> params = new HashMap<>();

        String baseUrl = BaseFunctions.getBaseUrl(ActivityNotUsableResponse.this,
                "addQRscan",
                BaseFunctions.STORE_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String queryParams = "&scanned=" + scannedData;
        baseUrl += queryParams;

        Log.e("request", "request -> " + baseUrl);

        RequestQueue queue = Volley.newRequestQueue(mContext);
        GoogleCertProvider.install(mContext);
        String finalBaseUrl = baseUrl;
        showProgressDialog();
        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.e("response", "response ->" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Toast.makeText(ActivityNotUsableResponse.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
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
