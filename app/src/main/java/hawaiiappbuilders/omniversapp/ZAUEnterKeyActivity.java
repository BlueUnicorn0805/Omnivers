package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

public class ZAUEnterKeyActivity extends BaseActivity implements View.OnClickListener {

    View panelEmailInput;
    EditText account_email;

    View panelEmailCodeAndPhoneNumber;
    EditText account_email_code;
    EditText account_phone;
    Button btnSendCodeToPhone;

    View panelSubmit;
    EditText account_name;
    EditText key_value;
    PhonenumberUtils phonenumberUtils;
    String verifyKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_key);

        phonenumberUtils = new PhonenumberUtils(this);
        Intent intent = getIntent();

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Move Account to new Phone");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);

        panelEmailInput = findViewById(R.id.panelEmailInput);
        account_email = findViewById(R.id.account_email);

        panelEmailCodeAndPhoneNumber = findViewById(R.id.panelEmailCodeAndPhoneNumber);
        panelEmailCodeAndPhoneNumber.setVisibility(View.GONE);
        account_email_code = findViewById(R.id.account_email_code);
        account_email_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailCode = account_email_code.getText().toString().trim();
                if (!TextUtils.isEmpty(emailCode) && emailCode.equals(verifyKey)) {
                    account_phone.setVisibility(View.VISIBLE);
                    btnSendCodeToPhone.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        account_phone = findViewById(R.id.account_phone);
        btnSendCodeToPhone = findViewById(R.id.btnSendCodeToPhone);

        panelSubmit = findViewById(R.id.panelSubmit);
        panelSubmit.setVisibility(View.GONE);
        account_name = findViewById(R.id.account_name);
        key_value = findViewById(R.id.key_value);

        findViewById(R.id.btnSendCodeToEmail).setOnClickListener(this);
        findViewById(R.id.btnSendCodeToPhone).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.btnSendCodeToEmail) {
            requestEmailVerificationCode();
        } else if (viewID == R.id.btnSendCodeToPhone) {
            requestPhoneVerificationCode();
        } else if (viewID == R.id.btnSubmit) {
            resetKey();
        } else if (viewID == R.id.btnBack) {
            finish();
        }
    }

    private void requestEmailVerificationCode() {

        String email = account_email.getText().toString().trim();
        if (!isValidEmail(email)) {
            showToastMessage("Please input valid email");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "GetEmailCode",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&email=" + email;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        //HttpsTrustManager.allowAllSSL();
        showProgressDialog();

        RequestQueue queue = Volley.newRequestQueue(mContext);

        GoogleCertProvider.install(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.e("GetEmailCode", response);

                /*if (needsLoginStatusFromResponse(response)) {
                    return;
                }*/

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        String msgData = jsonObject.getString("msg");
                        if (msgData.startsWith("[{")) {
                            JSONArray jsonDataArray = new JSONArray(msgData);
                            // [{"status":true,"msg":"[{\"theCode\":360177,\"WeStoppedAcct\":0,\"OwnerStoppedAcct\":0,\"cID\":187207,\"StreetNum\":\"\",\"FN\":\"fh\",\"LN\":\"cb\",\"PIN\":\"1234\",\"CP\":\"(+18) 685-6885\",\"address\":\" \",\"City\":\"\",\"St\":\"\",\"Zip\":\"\",\"marital\":\"S\",\"gender\":\"M\"}]"}]
                            JSONObject jsonData = jsonDataArray.getJSONObject(0);

                            verifyKey = jsonData.optString("theCode");

                            int cID = jsonData.optInt("cID");
                            if (cID > 0) {
                                /*msg("You are already registered,\nPlease log in.",
                                        v -> {
                                            Intent intent = new Intent(mContext, ActivityLogin.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        });*/
                                appSettings.setUserId(cID);
                            } else {
                                appSettings.setUserId(0);
                            }
                        } else {
                            verifyKey = msgData;
                        }

                        panelEmailInput.setVisibility(View.GONE);
                        panelEmailCodeAndPhoneNumber.setVisibility(View.VISIBLE);
                        account_phone.setVisibility(View.GONE);
                        btnSendCodeToPhone.setVisibility(View.GONE);
                        showToastMessage("Sent code to your Email!");
                    } else {
                        showToastMessage(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToastMessage(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                showToastMessage("Server response error!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void requestPhoneVerificationCode() {
        String phoneNumber = account_phone.getText().toString().trim();
        if (!phonenumberUtils.isValidPhoneNumber(phoneNumber)) {
            showToastMessage("Please input valid phone number");
            return;
        }

        String phone = String.format("%s", phoneNumber);

        HashMap<String, String> params = new HashMap<>();
        // params.put("telID", tm.getSimOperator());
        String networkOperator = getNetworkOperator();
        String MCC = "MCC";
        String MNC = "MNC";
        if (!TextUtils.isEmpty(networkOperator)) {
            if (networkOperator.length() > 3) {
                MCC = networkOperator.substring(0, 3);
                MNC = networkOperator.substring(3);
            } else {
                MCC = networkOperator;
            }
        }
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "GetPhoneCode",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&telID=" + getServiceProvider() +
                        "&cellNum=" + PhonenumberUtils.getFilteredPhoneNumber(phone) +
                        "&MCC=" + MCC +
                        "&MNC=" + MNC;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        //HttpsTrustManager.allowAllSSL();
        showProgressDialog();

        RequestQueue queue = Volley.newRequestQueue(mContext);

        GoogleCertProvider.install(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.e("GetPhoneCode", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        String msgData = jsonObject.getString("msg");
                        if (msgData.startsWith("[{")) {
                            JSONArray jsonDataArray = new JSONArray(msgData);
                            // [{"status":true,"msg":"[{\"theCode\":360177,\"WeStoppedAcct\":0,\"OwnerStoppedAcct\":0,\"cID\":187207,\"StreetNum\":\"\",\"FN\":\"fh\",\"LN\":\"cb\",\"PIN\":\"1234\",\"CP\":\"(+18) 685-6885\",\"address\":\" \",\"City\":\"\",\"St\":\"\",\"Zip\":\"\",\"marital\":\"S\",\"gender\":\"M\"}]"}]
                            JSONObject jsonData = jsonDataArray.getJSONObject(0);

                            verifyKey = jsonData.optString("theCode");
                        } else {
                            verifyKey = msgData;
                        }

                        appSettings.setALev(jsonObject.optString("msg"));

                        showToastMessage("Sent code to your phone");
                        panelEmailCodeAndPhoneNumber.setVisibility(View.GONE);
                        panelSubmit.setVisibility(View.VISIBLE);
                    } else {
                        panelEmailCodeAndPhoneNumber.setVisibility(View.VISIBLE);
                        panelSubmit.setVisibility(View.GONE);
                        showToastMessage("Not successful sending to the device");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToastMessage(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                showToastMessage("Server response error!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    private void resetKey() {

        String emailCode = key_value.getText().toString().trim();
        if (!emailCode.equals(verifyKey)) {
            showToastMessage("Please input correct key");
            return;
        }

        String email = account_email.getText().toString().trim();
        String keyVal = key_value.getText().toString().trim();

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "resetZA",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&code=" + keyVal +
                            "&SvcPrvID=" + verifyKey +
                            "&email=" + email +
                            "&token=" + appSettings.getDeviceToken() +
                            "&telID=" + getServiceProvider();
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

                    Log.e("resetZA", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
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
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert("Server error!");
                    } else {
                        showAlert(error.getMessage());
                    }
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

    private void sendCodeToEmail() {
        String email = account_email.getText().toString().trim();
        if (!isValidEmail(email)) {
            showToastMessage("Please input valid email");
            return;
        }

        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "sendMail",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "1" +
                            "&email=" + email;
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

                    Log.e("sendMail", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("msg")) {
                                showToastMessage(jsonObject.getString("msg"));
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
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert("Server error!");
                    } else {
                        showAlert(error.getMessage());
                    }
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
