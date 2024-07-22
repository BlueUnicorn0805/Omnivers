package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_RECEIVE_OTP_PHONE_VERIFY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.pinview.PinView;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

public class RegisterPhoneVerifyActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = RegisterPhoneVerifyActivity.class.getSimpleName();
    String phoneNumber = "";
    String countryCode = "";

    TextView tvPhone;
    PinView pinView;

    String verificationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phoneverify);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        countryCode = intent.getStringExtra("countryCode");
        tvPhone = findViewById(R.id.tvPhone);
        pinView = findViewById(R.id.pinView);

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    findViewById(R.id.btnConfirm).setVisibility(View.VISIBLE);
                    verifyCode();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvPhone.setText(String.format("%s", phoneNumber));

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
        findViewById(R.id.btnConfirm).setOnClickListener(this);
        findViewById(R.id.btnConfirm).setVisibility(View.GONE);

        requestVerificationCode();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter(ACTION_RECEIVE_OTP_PHONE_VERIFY));
    }

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.contentEquals(ACTION_RECEIVE_OTP_PHONE_VERIFY)) {
                String OTP = intent.getStringExtra("OTP");
                String msg = intent.getStringExtra("msg");
                verificationCode = OTP;
                //  showToastMessage(mContext, msg);
                pinView.setText(OTP);
                findViewById(R.id.btnConfirm).setVisibility(View.GONE);
                findViewById(R.id.btnConfirm).performClick();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister Receiver
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(msgReceiver);
    }

    private void requestVerificationCode() {
        // Never check location in registration process
        if (true || getLocation()) {
            String phone = String.format("%s", phoneNumber);
            HashMap<String, String> params = new HashMap<>();
            String networkOperator = getNetworkOperator();
            String MCC = "0";
            String MNC = "0";
            if (!TextUtils.isEmpty(networkOperator)) {
                if (networkOperator.length() > 3) {
                    MCC = networkOperator.substring(0, 3);
                    MNC = networkOperator.substring(3);
                } else {
                    MCC = networkOperator;
                }
            }
            String baseUrl = hawaiiappbuilders.omniversapp.utils.BaseFunctions.getBaseUrl(this,
                    "GetPhoneCode",
                    hawaiiappbuilders.omniversapp.utils.BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String countryValue = "+" + countryCode;
            String extraParams = "&telID=" + getServiceProvider() +
                    "&cellNum=" + PhonenumberUtils.getFilteredPhoneNumber(phone) +
                    "&MCC=" + MCC +
                    "&MNC=" + MNC +
                    "&countryCode=" + countryValue +
                    "&Token=" + appSettings.getDeviceToken();

            baseUrl += extraParams;

            Log.e("Request", baseUrl);

            //HttpsTrustManager.allowAllSSL();
            showProgressDialog();

            RequestQueue queue = Volley.newRequestQueue(mContext);

            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
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
                            String OTP = jsonObject.getString("OTP");

                            responseCode = OTP;

                            NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                            ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                            tokenList.add(new FCMTokenData(appSettings.getDeviceToken(), FCMTokenData.OS_ANDROID));
                            JSONObject payload = new JSONObject();
                            payload.put("OTP", OTP);
                            payload.put("message", msgData);
                            notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Phone_Verify_Receive_OTP, payload);
                            appSettings.setALev(jsonObject.optString("msg"));
                            appSettings.setCountryCode(countryCode);
                        } else {
                            findViewById(R.id.btnConfirm).setVisibility(View.GONE);
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
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
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
    }

    @Override
    public void onClick(View view) {
        int viewid = view.getId();
        if (viewid == R.id.btnBack) {
            finish();
        } else if (viewid == R.id.btnResend) {
            requestVerificationCode();
        } else if (viewid == R.id.btnConfirm) {
            verifyCode();
        }
    }

    String responseCode = "";

    private void verifyCode() {
        /*if (TextUtils.isEmpty(verificationCode)) {
            showToastMessage("Couldn't verify your phone. Please try with other number");
            return;
        }*/

        final String pinCode = pinView.getText().toString().trim();

        Log.e("testing", "response code -> " + responseCode
                +"\nnotificationCode -> "+ verificationCode
                +"\nuserCode -> "+pinCode);

        if (TextUtils.isEmpty(pinCode) || pinCode.length() != 6) {
            showToastMessage("Invalid verification code");
            return;
        }


        // Process Exception Too
        String simID = getServiceProvider();

        if (pinCode.equals(verificationCode)) {
            setResult(RESULT_OK);
            finish();
        } else {
            findViewById(R.id.btnConfirm).setVisibility(View.GONE);
            showToastMessage("Wrong code!");
        }
        /*if (pinCode.equals("515515") || pinCode.equals(verificationCode) || ("TFW".equals(simID) && "195722".equals(pinCode))) {
            setResult(RESULT_OK);
            finish();
        } else {
            showToastMessage("Wrong code!");
        }*/
    }
}
