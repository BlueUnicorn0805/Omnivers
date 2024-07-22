package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

public class RegisterCheckNameActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = RegisterCheckNameActivity.class.getSimpleName();
    String phoneNumber;
    String email;
    PhonenumberUtils phonenumberUtils;
    EditText edtUsername;
    Handler mCheckNameHandler;

    Button btnCheckName;
    Button btnNext;

    TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_checkname);

        phonenumberUtils = new PhonenumberUtils(this);
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");
        textResult = findViewById(R.id.textResult);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        mCheckNameHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);

                checkUserName();
            }
        };

        String blockCharacterSet = "@'{}\"\\/~#^|$%&*!;";
        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        edtUsername.setFilters(new InputFilter[]{filter});
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userName = edtUsername.getText().toString().trim();
                if (userName.length() > 4) {
                    //mCheckNameHandler.removeMessages(0);
                    //mCheckNameHandler.sendEmptyMessageDelayed(0, 3000);
                    btnCheckName.setVisibility(View.VISIBLE);
                } else {
                    btnCheckName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setVisibility(View.GONE);

        findViewById(R.id.btnBack).setOnClickListener(this);
        btnCheckName = findViewById(R.id.btnCheckName);
        findViewById(R.id.btnCheckName).setOnClickListener(this);
        btnCheckName.setVisibility(View.GONE);

        findViewById(R.id.btnNext).setOnClickListener(this);
    }

    private void checkUserName() {

        String userName = edtUsername.getText().toString().trim().replace("@", "");

        edtUsername.setText(userName);
        //final String theLastCP = edtPayNotes.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            showToastMessage("Please input username.");
            return;
        }

        if (userName.contains(" ")) {
            showToastMessage("Space is not allowed.");
            return;
        }

        // Never check location in registration process
        if (true || getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrlForRegistration(this,
                    "cjlGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "testUN" +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&misc=" + userName +
                            "&countrycode=" + appSettings.getCountryCode();
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

                    /*if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray baseJsonArray = new JSONArray(response);
                            if (baseJsonArray.length() > 0) {
                                if(baseJsonArray.length() == 1) {
                                    JSONObject statusObject = baseJsonArray.getJSONObject(0);
                                    if(statusObject.has("status") && statusObject.get("status") instanceof Boolean && !statusObject.getBoolean("status")) {
                                        textResult.setVisibility(View.GONE);
                                        showToastMessage("No info was returned");
                                        hideKeyboard();
                                    }
                                    return;
                                }
                                JSONArray baseInfoArray = baseJsonArray.getJSONArray(1);
                                JSONObject baseInfoObject = (JSONObject) baseInfoArray.get(0);
                                if (baseInfoObject.getInt("status") == 0) {
                                    textResult.setVisibility(View.VISIBLE);
                                    showToastMessage(mContext, "Good choice! That name is available");
                                    btnNext.setVisibility(View.VISIBLE);
                                    btnCheckName.setVisibility(View.GONE);
                                } else {
                                    if (baseInfoObject.has("name")) {
                                        textResult.setVisibility(View.GONE);
                                        showAlert(mContext, "Not available");
                                        btnNext.setVisibility(View.GONE);
                                        btnCheckName.setVisibility(View.VISIBLE);
                                    } else {
                                        textResult.setVisibility(View.GONE);
                                        btnNext.setVisibility(View.VISIBLE);
                                        btnCheckName.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                showAlert(mContext, "Not able to contact the Attendee using Notifications.\n" +
                                        "You might want to call them.");
                                textResult.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textResult.setVisibility(View.GONE);
                            showAlert(mContext, e.getMessage());
                        }
                    }*/

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray baseJsonArray = new JSONArray(response);
                            if (baseJsonArray.length() > 0) {
                                JSONObject statusObject = baseJsonArray.getJSONObject(0);
                                if (statusObject.has("status") && !statusObject.optBoolean("status")) {
                                    textResult.setVisibility(View.VISIBLE);
                                    showToastMessage(mContext, "Good choice! That name is available");
                                    btnNext.setVisibility(View.VISIBLE);
                                    btnCheckName.setVisibility(View.GONE);
                                } else {
                                    textResult.setVisibility(View.GONE);
                                    showToastMessage(statusObject.optString("msg"));
                                    hideKeyboard();
                                    btnCheckName.setVisibility(View.VISIBLE);
                                }
                            } else {
                                showAlert(mContext, "Not able to contact the Attendee using Notifications.\n" +
                                        "You might want to call them.");
                                textResult.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            textResult.setVisibility(View.GONE);
                            showToastMessage("Sorry! That name is not available");
                            hideKeyboard();
                            btnCheckName.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            textResult.setVisibility(View.GONE);
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

    @Override
    public void onClick(View view) {
        int viewid = view.getId();
        if (viewid == R.id.btnNext) {
            gotoNextScreen();
        } else if (viewid == R.id.btnBack) {
            finish();
        } else if (viewid == R.id.btnCheckName) {
            checkUserName();
        }
    }

    private void gotoNextScreen() {
        String userName = edtUsername.getText().toString().trim();

        Intent intent = new Intent(mContext, ActivityRegistration.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("email", email);
        intent.putExtra("handle", userName);
        startActivity(intent);
        finish();
    }
}
