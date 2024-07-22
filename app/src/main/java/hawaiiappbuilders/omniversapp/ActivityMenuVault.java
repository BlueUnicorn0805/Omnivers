package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.depositcheck.checks.ActivityCheckHistory;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityMenuVault extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuVault.class.getSimpleName();
    TextView tvValAvailFunds;
    TextView tvValRewards;
    TextView tvValGiftCards;

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_vault);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vault");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnTransactions).setOnClickListener(this);
        findViewById(R.id.btnTransFunds).setOnClickListener(this);
        findViewById(R.id.btnDepositCheck).setOnClickListener(this);
        findViewById(R.id.btnMilestones).setOnClickListener(this);
        findViewById(R.id.btnFamilyHierarchy).setOnClickListener(this);

        findViewById(R.id.btnDepositCheck).setVisibility(View.GONE);
        findViewById(R.id.btnFamilyHierarchy).setVisibility(View.GONE);

        tvValAvailFunds = findViewById(R.id.tvValAvailFunds);
        tvValRewards = findViewById(R.id.tvValRewards);
        tvValGiftCards = findViewById(R.id.tvValGiftCards);

        getAvaBalance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewId == R.id.btnTransactions) {
            startActivity(new Intent(mContext, ActivityTransaction.class));
        } else if (viewId == R.id.btnTransFunds) {
            startActivity(new Intent(mContext, ActivityTransIntro.class));
        } else if (viewId == R.id.btnDepositCheck) {
            startActivity(new Intent(mContext, ActivityCheckHistory.class));
        } else if (viewId == R.id.btnMilestones) {
            startActivity(new Intent(mContext, ActivityWorkContact.class));
        } else if (viewId == R.id.btnFamilyHierarchy) {
            startActivity(new Intent(mContext, ActivityFamilyHierarchy.class));
        }
    }

    private void getAvaBalance() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "AllBal" +
                            "&misc=" + "0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.e("response", "response -> " + response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                float fAvaBalance = (float) jsonObject.getDouble("instaCash");
                                float fAvaSavings = (float) jsonObject.getDouble("instaSavings");
                                float fLoyalty = (float) jsonObject.getDouble("Loyalty");
                                float fGift = (float) jsonObject.getDouble("Gift");
                                int fBogo = jsonObject.getInt("BOGO");

                                DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                                String cashInfo = String.format("Available Funds: $%s\nRewards: $%s\nGift Card: $%s",
                                        formatter.format(fAvaBalance), formatter.format(fLoyalty), formatter.format(fGift));

                                /*//showAlert(String.format("Balance : $ %.2f", fAvaBalance));
                                androidx.appcompat.app.AlertDialog idInputDlg = new androidx.appcompat.app.AlertDialog.Builder(mContext)
                                        .setCancelable(true)
                                        .setMessage(cashInfo)
                                        .create();

                                idInputDlg.setCanceledOnTouchOutside(true);
                                idInputDlg.setCancelable(true);
                                idInputDlg.show();*/

                                //tvBalanceInfo.setText(cashInfo);

                                tvValAvailFunds.setText(String.format("$%s", formatter.format(fAvaBalance)));
                                tvValRewards.setText(String.format("$%s", formatter.format(fLoyalty)));
                                tvValGiftCards.setText(String.format("$%s", formatter.format(fGift)));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
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

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }
}
