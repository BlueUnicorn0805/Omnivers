package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityChangePassword extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityChangePassword.class.getSimpleName();
    EditText edtOriginal;
    EditText edtPwd;
    EditText edtConfirm;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);
        dataUtil = new DataUtil(this, ActivityChangePassword.class.getSimpleName());

        edtOriginal = (EditText) findViewById(R.id.edtOriginal);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        edtConfirm = (EditText) findViewById(R.id.edtConfirm);

        //edtEmail.setText("testfreelancerbd@gmail.com");
        //edtPassword.setText("abcdEF1234##");

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnSubmit) {
            //loginUserWithNST();
            loginUserWithGF();
        }
    }

    private void loginUserWithGF() {

        hideKeyboard(edtOriginal);
        hideKeyboard(edtPwd);
        hideKeyboard(edtConfirm);

        // Get Location
        if (!getLocation()) {
            return;
        }

        final String original = edtOriginal.getText().toString().trim();
        final String password = edtPwd.getText().toString().trim();
        final String confirm = edtConfirm.getText().toString().trim();

        /*if (TextUtils.isEmpty(original)) {
            edtOriginal.setError("Please input original password");
            return;
        }*/

        if (TextUtils.isEmpty(password)) {
            edtPwd.setError(getText(R.string.error_password));
            return;
        }

        if (password.length() < 5) {
            edtPwd.setError(getText(R.string.error_invalid_password));
            return;
        }

        if (password.contains("#")) {
            edtPwd.setError("Password couldn't accept # symbol");
            return;
        }

        if (!password.equals(confirm)) {
            edtConfirm.setError(getText(R.string.error_password_not_match));
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("PIN is required to make purchase");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Continue");

        final Button cancel = alertLayout.findViewById(R.id.pin_cancel);
        cancel.setText("Cancel");

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumber = pin.getText().toString().trim();
                boolean pinTrue = false;
                hideKeyboard();
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    pinTrue = false;
                } else {
                    pinTrue = true;
                }

                String userPin = appSettings.getPIN().trim();
                if (pinTrue && userPin.equalsIgnoreCase(pinNumber)) {
                    dialog.dismiss();

                    if (getLocation()) {
                        try {
                            showProgressDialog();

                            HashMap<String, String> params = new HashMap<>();
                            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                                    "resetPW",
                                    BaseFunctions.MAIN_FOLDER,
                                    getUserLat(),
                                    getUserLon(),
                                    mMyApp.getAndroidId());
                            String extraParams =
                                    "&industryID=" + appSettings.getIndustryid() +
                                            "&oldPW=" + original +
                                            "&newPW=" + password +
                                            "&misc=" + "" +
                                            "&sellerID=" + appSettings.getWorkid();
                            baseUrl += extraParams;
                            Log.e("Request", baseUrl);

                            GoogleCertProvider.install(mContext);
                            RequestQueue queue = Volley.newRequestQueue(mContext);
                            String finalBaseUrl = baseUrl;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideProgressDialog();

                                    JSONArray responseArry = null;
                                    try {
                                        responseArry = new JSONArray(response);
                                        JSONObject responseObj = responseArry.getJSONObject(0);

                                        String msg = "Your PW has been updated.";
                                        if (responseObj.has("msg")) {
                                            msg = responseObj.getString("msg");
                                        }


                                        showAlert(msg, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        });
                                    } catch (JSONException e) {
                                        if (dataUtil!=null) {
                                            dataUtil.setActivityName(ActivityChangePassword.class.getSimpleName());
                                            dataUtil.zzzLogIt(e, "resetPW");
                                        }
                                        e.printStackTrace();
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
                            stringRequest.setShouldCache(false);
                            queue.add(stringRequest);
                        }catch (Exception e){
                            if (dataUtil!=null) {
                                dataUtil.setActivityName(ActivityChangePassword.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "resetPW");
                            }
                        }
                    }
                } else {
                    showToastMessage("Wrong PIN");
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
