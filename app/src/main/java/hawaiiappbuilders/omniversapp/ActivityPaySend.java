package hawaiiappbuilders.omniversapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class ActivityPaySend extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityPaySend.class.getSimpleName();
    EditText edtEmail;
    TextView tvUserName;
    EditText edtMemo;
    EditText edtAmount;
    Button btnSend;

    Handler mCheckNameHandler;
    // LinearLayout panelInputPIN;
    // EditText edtPIN;
    PhonenumberUtils phonenumberUtils;
    int mlid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_send);

        phonenumberUtils = new PhonenumberUtils(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        edtEmail = findViewById(R.id.edtEmail);
        tvUserName = findViewById(R.id.tvUserName);

        edtMemo = findViewById(R.id.edtMemo);
        edtAmount = findViewById(R.id.edtAmount);
        btnSend = findViewById(R.id.btnSend);
        /*panelInputPIN = findViewById(R.id.panelInputPIN);
        panelInputPIN.setVisibility(View.GONE);*/
        /*edtPIN = findViewById(R.id.edtPIN);
        edtPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String appPIN = appSettings.getPIN();
                String inputPIN = edtPIN.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(inputPIN)) {
                    paySend();
                    // panelInputPIN.setVisibility(View.GONE);
                    hideKeyboard(edtPIN);
                    edtPIN.setText("");
                    btnSend.setVisibility(View.VISIBLE);
                }
            }
        });*/
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

        btnSend.setTag(0);
        findViewById(R.id.btnSend).setOnClickListener(this);
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
        }else if (viewId == R.id.btnSend) {
            showPinDlg();
            // panelInputPIN.setVisibility(View.VISIBLE);
            // btnSend.setVisibility(View.GONE);
        } else if (viewId == R.id.btnImport) {

        }
    }

    private void showPinDlg() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("PIN is required to pay now");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);
        pin.requestFocus();
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
                /*String pinNumber = pin.getText().toString().trim();
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

                    payNow();
                } else {
                    showToastMessage("Wrong PIN");
                    dialog.dismiss();
                }*/

                String appPIN = appSettings.getPIN();
                String pinNumber = pin.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                    paySend();
                    // panelInputPIN.setVisibility(View.GONE);
                    // hideKeyboard(edtPIN);
                    // edtPIN.setText("");
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_invalid_credentials);
                    } else {
                        showAlert("No Connection");
                    }

                    //showMessage(error.getMessage());
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

    private void paySend() {
        String userName = edtEmail.getText().toString().trim();
        String amt = edtAmount.getText().toString().trim();
        String notes = edtMemo.getText().toString().trim();

        hideKeyboard(edtAmount);

        if (TextUtils.isEmpty(amt)) {
            showToastMessage("Please input amount");
            return;
        }

        int mlid = (int) btnSend.getTag();

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "addSMS",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "161" +
                            "&toMLID=" + String.valueOf(mlid) +
                            "&Amt=" + amt +
                            "&email=" + userName +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&note=" + notes;
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

                    Log.e("2377", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert(jsonObject.getString("msg"));
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                    alertDialogBuilder
                                            .setTitle("Results")
                                            .setMessage(jsonObject.getString("msg"))
                                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                } else {

                                    // showToastMessage(jsonObject.getString("msg")/*"Success your payment!"*/);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                    alertDialogBuilder
                                            .setTitle("Results")
                                            .setMessage(jsonObject.getString("msg"))
                                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();


                                    NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                    ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                    String token = jsonObject.optString("Token");
                                    if (!TextUtils.isEmpty(token)) {
                                        tokenList.add(new FCMTokenData(token, FCMTokenData.OS_UNKNOWN));
                                    }
                                    if (!tokenList.isEmpty()) {
                                        JSONObject payload = new JSONObject();
                                        payload.put("message", "You got payment");
                                        payload.put("orderId", "1");
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Funds_Sent, payload);
                                    }
                                }
                            } else {
                                // Show Alert
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
