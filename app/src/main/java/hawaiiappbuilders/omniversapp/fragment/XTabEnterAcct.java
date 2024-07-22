/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package hawaiiappbuilders.omniversapp.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

import hawaiiappbuilders.omniversapp.ActivityBank;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class XTabEnterAcct extends BaseFragment implements View.OnClickListener {

    private static final String TAG = XTabEnterAcct.class.getSimpleName();
    BaseFunctions baseFunctions;
    // Bank Information
    TextView tvAccount;
    TextView tvAccountConfirm;

    TextView tvRouting;
    TextView tvBankName;

    WebView webViewTerms;

    CheckBox chkAgreeSubmit;
    int bankID = 0;
    //091901024

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String routing = tvRouting.getText().toString().trim();
            if (routing.length() > 8) {
                getRoutingCodes(routing);
            }
        }
    };

    public static XTabEnterAcct newInstance(String text) {
        XTabEnterAcct mFragment = new XTabEnterAcct();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtabenteracct, container, false);

        baseFunctions = new BaseFunctions(mContext, TAG);
        init(savedInstanceState);
        initLayout(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initLayout(View parentView) {

        // ---------------------------- About the Bank ---------------------------
        tvAccount = parentView.findViewById(R.id.tvAccount);
        tvAccountConfirm = parentView.findViewById(R.id.tvAccountConfirm);

        tvRouting = parentView.findViewById(R.id.tvRouting);
        tvRouting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });

        // UI Bank Information
        tvBankName = parentView.findViewById(R.id.tvBankName);

        webViewTerms = parentView.findViewById(R.id.webViewTerms);
        WebSettings webSettings = webViewTerms.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewTerms.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
            }
        });
        webViewTerms.setBackgroundColor(Color.WHITE);
        webViewTerms.loadUrl(String.format("file:///android_asset/techs/payment_tc.html"));
        webViewTerms.requestFocus();

        chkAgreeSubmit = parentView.findViewById(R.id.chkAgreeSubmit);
        chkAgreeSubmit.setChecked(true);
        chkAgreeSubmit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    showAlert("Without selecting “I Agree” we will not be able to complete your purchase.");
                }
            }
        });

        parentView.findViewById(R.id.btnSave).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.btnSave) {
            saveBankInformation();
        }
    }

    public void getVerifyStatus(float amt, boolean showResponse) {
        if (parentActivity.getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "verifyBank",
                    BaseFunctions.MAIN_FOLDER,
                    parentActivity.getUserLat(),
                    parentActivity.getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&amt=" + String.valueOf(amt);
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("verifyBank", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray dataArray = new JSONArray(response);

                            JSONObject resultJson = dataArray.getJSONObject(0);
                            int needsVerified = resultJson.optInt("status");
                            if (showResponse) {
                                showAlert(resultJson.optString("msg"), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        parentActivity.finish();
                                    }
                                });
                            } else {
                                if (needsVerified == 30) {
                                    showToastMessage("You have already completed this.");
                                    ((ActivityBank) parentActivity).showTab(0);

                                    appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_VERIFIED);
                                } else if (needsVerified == 20) {
                                    appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_NEEDS_VERIFY);

                                    ((ActivityBank) parentActivity).showTab(2);
                                } else if (needsVerified == 0) {
                                    appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_NOT_SETUP_ACCT);
                                } else if (needsVerified == 25) {
                                    showAlert("Once deposit made, you will be able to enter that amount.", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            parentActivity.finish();
                                        }
                                    });
                                } else if (needsVerified == 35) {
                                    showAlert("Not usable.", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            parentActivity.finish();
                                        }
                                    });
                                }
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

    synchronized private void getRoutingCodes(String routing) {

        if (routing.length() > 9) {
            showAlert("Your routing number should always be 9 digits.");
            return;
        }

        if (parentActivity.getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "MasterBankByRt",
                    BaseFunctions.MAIN_FOLDER,
                    parentActivity.getUserLat(),
                    parentActivity.getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&RT=" + routing;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("RT", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray dataArray = new JSONArray(response);
                            JSONObject resultJson = dataArray.getJSONObject(0);
                            if (resultJson.has("status") && resultJson.getBoolean("status") == false) {
                                showAlert(resultJson.getString("msg"));
                                return;
                            }

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObj = dataArray.getJSONObject(i);
                                bankID = dataObj.getInt("ID");
                                tvBankName.setText(dataObj.getString("BankName"));

                                String city = dataObj.getString("BankCity");
                                String state = dataObj.getString("BankState");
                                String zip = dataObj.getString("BankZip");

                                break;
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

    private void saveBankInformation() {
        // Send Card Information to the server

        String accountName = tvAccount.getText().toString().trim();
        String accountConfirm = tvAccountConfirm.getText().toString().trim();

        if (!accountName.equals(accountConfirm)) {
            showToastMessage("Account number mismatch!");
            return;
        }

        if (bankID == 0) {
            showToastMessage("Please enter routing number");
            return;
        }

        if (TextUtils.isEmpty(accountName) /*|| accountName.length() < 8 || accountName.length() > 30*/) {
            //showToastMessage("Account number is generally 8 ~ 30 digits");
            showToastMessage("Please input account number");
            return;
        }

        // Check user agreement
        if (!chkAgreeSubmit.isChecked()) {
            showAlert("Without selecting “I Agree” we will not be able to transfer funds.");
            return;
        }

        inputSSN();
    }

    private void inputSSN() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ssn_input, null);

        final EditText edtSSN = alertLayout.findViewById(R.id.edtSSN);

        final Button submit = alertLayout.findViewById(R.id.btnSubmit);

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ssn = edtSSN.getText().toString().trim();
                if (ssn.length() != 9) {
                    showToastMessage("Please input 9 digits for SSN");
                    return;
                }

                dialog.dismiss();
                hideKeyboard(edtSSN);

                Calendar myCalendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(mContext,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                          int dayOfMonth) {

                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, monthOfYear);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        if (parentActivity.getLocation()) {
                                            HashMap<String, String> params = new HashMap<>();
                                            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                                                    "AddAcct",
                                                    BaseFunctions.MAIN_FOLDER,
                                                    parentActivity.getUserLat(),
                                                    parentActivity.getUserLon(),
                                                    mMyApp.getAndroidId());
                                            String extraParams =
                                                            "&acctOwnerID=" + appSettings.getUserId() +
                                                            "&Acct=" + tvAccount.getText().toString().trim() +
                                                            "&bankID=" + String.valueOf(bankID) +
                                                            "&Nick=" + "" +
                                                            "&SSN=" + ssn +
                                                            "&DOB=" + DateUtil.toStringFormat_14(myCalendar.getTime());
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
                                                                String msg = jsonObject.getString("msg");
                                                                if (msg.contains("duplicate")) {
                                                                    showToastMessage("Duplicates not allowed");
                                                                } else {
                                                                    showToastMessage(msg);
                                                                }
                                                            } else {
                                                                String newNickId = "0";
                                                                if (jsonObject.has("NickID") && !jsonObject.isNull("NickID")) {
                                                                    newNickId = jsonObject.getString("NickID");
                                                                }

                                                                showAlert("Saved successfully!", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        // Goto HP
                                                                        parentActivity.finish();
                                                                    }
                                                                });
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
                                },
                                myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle("Set Birthday");
                datePickerDialog.setMessage("Set Birthday");
                datePickerDialog.show();
            }
        });
    }
}
