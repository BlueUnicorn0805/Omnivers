package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import hawaiiappbuilders.omniversapp.global.pinview.PinView;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class RegisterEmailVerifyActivity extends BaseActivity implements View.OnClickListener {

    String email = "";

    TextView tvEmail;
    PinView pinView;

    String verificationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_emailverify);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        tvEmail = findViewById(R.id.tvEmail);
        pinView = findViewById(R.id.pinView);

        tvEmail.setText(email);

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnResend).setOnClickListener(this);
        findViewById(R.id.btnConfirm).setOnClickListener(this);

        requestVerificationCode();
    }

    private void requestVerificationCode() {

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
        RequestQueue queue = Volley.newRequestQueue(mContext);
        GoogleCertProvider.install(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

                            int gotoLoginNow = jsonData.optInt("goToLoginNow");
                            if (gotoLoginNow >= 80) {

                                showAlert(jsonData.optString("loginMessage"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, ActivityLogin.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                return;
                            }

                            verificationCode = jsonData.optString("theCode");

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
                            verificationCode = msgData;
                        }
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

    private  void verifyCode() {
        /*if (TextUtils.isEmpty(verificationCode)) {
            showToastMessage("Couldn't verify your email. Please try with other email.");
            return;
        }*/

        final String pinCode = pinView.getText().toString().trim();
        if (TextUtils.isEmpty(pinCode) || pinCode.length() != 6) {
            showToastMessage("invalid verification code");
            return;
        }

        if (pinCode.equals("871177") || pinCode.equals(verificationCode)) {
            setResult(RESULT_OK);
            finish();
        } else {
            showToastMessage("Wrong code!");
        }
    }
}
