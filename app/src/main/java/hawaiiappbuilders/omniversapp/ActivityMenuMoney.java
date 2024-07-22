package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
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

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityMenuMoney extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuMoney.class.getSimpleName();
    TextView tvBalanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_money);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vault");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        findViewById(R.id.btnSendReceivePay).setOnClickListener(this);
        findViewById(R.id.btnBalance).setOnClickListener(this);
        findViewById(R.id.btnTransactions).setOnClickListener(this);
        findViewById(R.id.btnInvoices).setOnClickListener(this);
        findViewById(R.id.btnTransFunds).setOnClickListener(this);

        tvBalanceInfo = findViewById(R.id.tvBalanceInfo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSuccessDlg() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if(viewId == R.id.btnToolbarHome) {
           backToHome();
        }else if (viewId == R.id.btnSendReceivePay) {
            Intent intent = new Intent(mContext, ConnectionActivity.class);
            intent.putExtra("page", ConnectionActivity.PANEL_PAY_SEND);
            startActivity(intent);
        } else if (viewId == R.id.btnBalance) {
            getAvaBalance();
        } else if (viewId == R.id.btnTransactions) {
            startActivity(new Intent(mContext ,ActivityTransaction.class));
        } else if (viewId == R.id.btnInvoices) {

        } else if (viewId == R.id.btnTransFunds) {
            startActivity(new Intent(mContext, ActivityTransIntro.class));
        }
    }

    private void getAvaBalance() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
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

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

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

                                tvBalanceInfo.setText(cashInfo);
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
