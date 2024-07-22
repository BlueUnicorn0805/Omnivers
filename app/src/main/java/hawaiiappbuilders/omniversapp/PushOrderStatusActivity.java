package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import hawaiiappbuilders.omniversapp.adapters.OrderStatusAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.OrderData;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PushOrderStatusActivity extends BaseActivity implements View.OnClickListener {

    //TextView tvParams;
    //TextView tvFrom;
    RecyclerView rcOrders;
    ArrayList<OrderData> orderData = new ArrayList<>();
    OrderStatusAdapter orderStatusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushorderstatus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_orderstatus);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*Intent intent = getIntent();
        String pushData = intent.getStringExtra("payloads");
        if (!TextUtils.isEmpty(pushData)) {
            try {
                //jAdditionalData.put("CALID", selectedItem.getCalId());
                //jAdditionalData.put("SenderCP", appSettings.getCP());
                //jAdditionalData.put("SenderID", appSettings.getUserId());

                JSONObject jsonObject = new JSONObject(pushData);
                if (jsonObject.has("SenderCP")) {
                    String sourceCP = jsonObject.getString("SenderCP");
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", sourceCP, null));
                    if (smsIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(smsIntent);
                        finish();
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/


        /*tvParams = findViewById(R.id.tvParams);
        tvFrom = findViewById(R.id.tvFrom);

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        // Show Params and Values
        tvParams.setText(String.format("Message : %s", message));
        tvFrom.setText("From : " + title);*/

        rcOrders = findViewById(R.id.rcOrders);
        rcOrders.setHasFixedSize(true);
        rcOrders.setLayoutManager(new LinearLayoutManager(mContext));

        orderStatusAdapter = new OrderStatusAdapter(mContext, orderData, new OrderStatusAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                OrderData orderData = PushOrderStatusActivity.this.orderData.get(position);

                if (orderData.getStatusID().equals("2170")) {
                    showTip(orderData);
                }
            }
        });
        rcOrders.setAdapter(orderStatusAdapter);

        getOrders();

        findViewById(R.id.btnClose).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getOrders() {
        if (getLocation()) {
            String userLat = getUserLat();
            String userLon = getUserLon();

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "orderStatus1";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("orderStatus1", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonOrderInfo = jsonArray.getJSONObject(i);

                                    OrderData newOrderData = new OrderData();
                                    newOrderData.setID(jsonOrderInfo.optString("ID"));
                                    newOrderData.setSeller(jsonOrderInfo.optString("Seller"));
                                    newOrderData.setStatus(jsonOrderInfo.optString("Status"));
                                    newOrderData.setStatusID(jsonOrderInfo.optString("statusID"));
                                    orderData.add(newOrderData);
                                }

                                orderStatusAdapter.notifyDataSetChanged();
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

                    //showSuccessDlg();
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_conn_error);
                    } else {
                        showAlert(error.getMessage());
                    }

                    //showMessage(error.getMessage());
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

    private void showTip(OrderData orderData) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating_from_user, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Questions
        final RadioGroup groupOnTime = (RadioGroup) dialogView.findViewById(R.id.groupOnTime);
        final RadioGroup groupComplete = (RadioGroup) dialogView.findViewById(R.id.groupComplete);
        final RadioGroup groupAddFav = (RadioGroup) dialogView.findViewById(R.id.groupAddFav);

        // Ratings
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        final TextView tvErrorRate = (TextView) dialogView.findViewById(R.id.tvErrorRate);
        tvErrorRate.setVisibility(View.GONE);

        final EditText edtTips = (EditText) dialogView.findViewById(R.id.edtTips);

        // Button Actions
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDlg.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                inputDlg.dismiss();

                // Check current rating
                if (ratingBar.getRating() == 0) {
                    tvErrorRate.setVisibility(View.VISIBLE);
                    return;
                }

                float tipsValue = 0;
                try {
                    tipsValue = Float.parseFloat(edtTips.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                        "SendRatingTipToServer",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                String a1 = groupOnTime.getCheckedRadioButtonId() == R.id.btnOnTimeYes ? "1" : "0";
                String a2 = groupComplete.getCheckedRadioButtonId() == R.id.btnCompleteYes ? "1" : "0";
                String a3 = groupAddFav.getCheckedRadioButtonId() == R.id.btnFavYes ? "1" : "0";
                String extraParams =
                        "&orderID=" + orderData.getID() +
                                "&a1=" + a1 +
                                "&a2=" + a2 +
                                "&a3=" + a3 +
                                "&a4=" + "0" +
                                "&rating=" + String.valueOf(ratingBar.getRating()) +
                                "&tip=" + String.valueOf(tipsValue);
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SendRatingTipToServer", response);
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
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnClose) {
            Intent intent = new Intent(getApplicationContext(), ActivityHomeEvents.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
