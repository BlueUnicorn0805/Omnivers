package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPwdActivity extends BaseActivity implements View.OnClickListener {
    boolean forgotPIN = false;
    TextView titleForgot;
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);

        Intent intent = getIntent();
        forgotPIN = intent.getBooleanExtra("forgot_pin", false);

        titleForgot = (TextView) findViewById(R.id.titleForgot);
        edtEmail = (EditText) findViewById(R.id.edtEmail);

        if (forgotPIN) {
            titleForgot.setText("Forgot PIN?");
        } else {
            titleForgot.setText("Forgot Password?");
        }
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnSubmit) {
            loginUser();
        }
    }

    private void loginUser() {
        hideKeyboard(edtEmail);

        final String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(getText(R.string.error_invalid_email));
            return;
        }

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId()); // forgotPIN ? appSettings.getUserId() : "0"
            String extraParams =
                    "&mode=" + "ForgotPW" +
                            "&sellerID=" + "0" +
                            "&industryID=" + "0" +
                            "&misc=" + email;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            showProgressDialog();

            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    hideProgressDialog();

                    Log.e("ForgotPW", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            showToastMessage(jsonObject.getString("msg"));
                        } else {
                            showToastMessage(jsonObject.getString("msg"));
                            /*if (forgotPIN) {
                                showToastMessage("We sent email to reset your pin.");
                            } else {
                                showToastMessage("We sent email to reset your password.");
                            }*/
                            finish();
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

                    networkErrorHandle(mContext, error);
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
}
