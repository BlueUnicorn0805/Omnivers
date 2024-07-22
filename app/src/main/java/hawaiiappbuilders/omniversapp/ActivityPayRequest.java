package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

public class ActivityPayRequest extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityPayRequest.class.getSimpleName();
    EditText edtEmail;
    TextView tvUserName;
    EditText edtMemo;
    EditText edtAmount;
    Button btnRequest;

    Handler mCheckNameHandler;

    int mlid = 0;
    PhonenumberUtils phonenumberUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_request);

        phonenumberUtils = new PhonenumberUtils(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        edtEmail = findViewById(R.id.edtEmail);
        tvUserName = findViewById(R.id.tvUserName);

        edtMemo = findViewById(R.id.edtMemo);
        edtAmount = findViewById(R.id.edtAmount);
        btnRequest = findViewById(R.id.btnRequest);

        mCheckNameHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);

                checkUserName();
            }
        };
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = edtEmail.getText().toString().trim();
                if (userName.length() > 2) {
                    mCheckNameHandler.removeMessages(0);
                    mCheckNameHandler.sendEmptyMessageDelayed(0, 2000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnRequest.setTag(0);

        findViewById(R.id.btnRequest).setOnClickListener(this);
        findViewById(R.id.btnImport).setOnClickListener(this);
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
        } else if(viewId == R.id.btnToolbarHome) {
      backToHome();
        }else if (viewId == R.id.btnRequest) {
            if(!isOnline(mContext)) {
                showToastMessage(mContext, "Please check your internet connection");
                return;
            }
            createInvoice();
        } else if (viewId == R.id.btnImport) {
        }
    }

    private void checkUserName() {

        String userName = edtEmail.getText().toString().trim();

        if (phonenumberUtils.isValidPhoneNumber(userName)) {
            // Phone number and no need change
        } else if (isValidEmail(userName)) {
            // Email and no need change
        } else if (userName.contains("@")) {
            userName = userName.replace("@", "");
        }

        if (TextUtils.isEmpty(userName)) {
            showToastMessage("Please input username.");
            return;
        }

        tvUserName.setVisibility(View.GONE);
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "testUN" +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&misc=" + userName;
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

                    Log.e("testUN", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray baseJsonArray = new JSONArray(response);
                            if (baseJsonArray.length() > 0) {
                                if(baseJsonArray.length() == 1) {
                                    JSONObject statusObject = baseJsonArray.getJSONObject(0);
                                    if(statusObject.has("status") && statusObject.get("status") instanceof Boolean && !statusObject.getBoolean("status")) {
                                        showToastMessage("No info was returned");
                                        hideKeyboard();
                                    }
                                    return;
                                }
                                JSONArray baseInfoArray = baseJsonArray.getJSONArray(1);
                                JSONObject baseInfoObject = (JSONObject) baseInfoArray.get(0);
                                if (baseInfoObject.getInt("status") == 0) {
                                    showToastMessage("No info was returned");
                                } else {
                                    if (baseInfoObject.has("name")) {
                                        String foundName = baseInfoObject.getString("name");
                                        tvUserName.setText(HtmlCompat.fromHtml("<b>Found:</b> " + foundName, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                        tvUserName.setVisibility(View.VISIBLE);
                                    } else {
                                        tvUserName.setVisibility(View.GONE);
                                    }
                                    String toMLID = baseInfoObject.getString("toMLID");
                                    mlid = Integer.parseInt(toMLID);
                                    String handle = baseInfoObject.getString("handle");
                                }
                            } else {
                                showAlert("Not able to contact the Attendee using Notifications.\n" +
                                        "You might want to call them.");
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

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    private void createInvoice() {

        String contact = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            showToastMessage("Please select contact");
            return;
        }

        int qty = 1;
        float amt = 0;
        try{
            amt = Float.parseFloat(edtAmount.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String desc = edtMemo.getText().toString().trim();

        if (amt <= 0) {
            showToastMessage("Please input correct value");
            return;
        }

        if (getLocation()) {
            String orderDueDate = DateUtil.toStringFormat_7(new Date());
            String orderDueAt = DateUtil.toStringFormat_13(new Date());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serviceusedid", 2387);
                jsonObject.put("promoid", "0");
                jsonObject.put("orderdueat", orderDueAt);
                jsonObject.put("industryID", 123);
                jsonObject.put("nickid", "0");
                jsonObject.put("totship", "0");
                jsonObject.put("totlabor", "0");

                jsonObject.put("orname", "");
                jsonObject.put("oraddr", "");
                jsonObject.put("orph", "");
                jsonObject.put("delname", "");
                jsonObject.put("deladdr", "");
                jsonObject.put("delzip", "");
                jsonObject.put("delph", "");
                jsonObject.put("deldir", "");

                jsonObject.put("sellerid", appSettings.getUserId());
                jsonObject.put("buyerid", mlid);
                jsonObject.put("email", contact);
                //jsonObject.put("PaidWith", "IC");
                JSONArray menuItemsArray = new JSONArray();
                JSONObject itemObj = new JSONObject();
                itemObj.put("prodid", 0);
                itemObj.put("name", "Invoice");
                itemObj.put("des", desc);
                itemObj.put("price", amt);
                itemObj.put("size", 1);
                itemObj.put("quantity", qty);
                itemObj.put("oz", "0");
                itemObj.put("gram", "0");
                menuItemsArray.put(itemObj);
                jsonObject.put("menus", menuItemsArray);
                jsonObject.put("totprice", amt * qty);
                jsonObject.put("tottax", 0);
                jsonObject.put("token", appSettings.getDeviceToken());
                jsonObject.put("paynow", false);
                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "CreateInvoice", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());


                // Try to Creat Transaction
                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(mContext);
                String finalOrderDueDate = orderDueDate;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("CreateInvoice", response);

                        hideProgressDialog();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    long orderId = jsonObject.getLong("OrderID");
                                    // token has no value
                                    if(!jsonObject.optString("token").isEmpty()) {
                                        String token = jsonObject.getString("token");

                                        NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                        tokenList.add(new FCMTokenData(token, FCMTokenData.OS_UNKNOWN));
                                        JSONObject payload = new JSONObject();
                                        payload.put("message", "New Invoice");
                                        payload.put("orderId", orderId);
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Invoice_Sent, payload);
                                    }
                                    showToastMessage(jsonObject.getString("msg"));
                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    if (jsonObject.optInt("OrderID") == -2) {
                                        showAlert("Insufficient Funds");
                                    } else {
                                        showAlert(jsonObject.getString("msg"));
                                    }
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
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
