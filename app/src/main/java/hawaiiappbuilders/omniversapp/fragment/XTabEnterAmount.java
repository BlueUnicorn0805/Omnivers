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

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

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

import hawaiiappbuilders.omniversapp.ActivityBank;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class XTabEnterAmount extends BaseFragment implements View.OnClickListener {

    private static final String TAG = XTabEnterAmount.class.getSimpleName();
    BaseFunctions baseFunctions;
    EditText tvVerifyAmount;

    // 041215663
    public static XTabEnterAmount newInstance(String text) {
        XTabEnterAmount mFragment = new XTabEnterAmount();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtabenteramt, container, false);

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

        // Verify Panel
        tvVerifyAmount = parentView.findViewById(R.id.tvVerifyAmount);
        parentView.findViewById(R.id.btnSubmit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.btnSubmit) {
            hideKeyboard(tvVerifyAmount);
            String amt = tvVerifyAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amt)) {
                showToastMessage("Please enter amount!");
            } else {
                getVerifyStatus(Float.parseFloat(amt), true);
            }
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
                                    ((ActivityBank)parentActivity).showTab(0);

                                    appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_VERIFIED);
                                } else if (needsVerified == 20) {
                                    appSettings.setTransMoneyStatus(ActivityBank.TRANSFER_NEEDS_VERIFY);
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
}
