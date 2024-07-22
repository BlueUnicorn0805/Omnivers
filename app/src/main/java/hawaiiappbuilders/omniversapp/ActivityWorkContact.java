package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

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

public class ActivityWorkContact extends BaseActivity {
    public static final String TAG = ActivityWorkContact.class.getSimpleName();

    TextView titleText;
    FrameLayout layoutQr;
    CodeScannerView scannerView;
    Button btnScanRecipient;
    EditText edtEmail; // recipient
    TextView foundNameTextView;

    boolean testUn = true;

    Handler mCheckNameHandler;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workcontactinfo);
        initViews();

        foundNameTextView.setVisibility(View.GONE);
        layoutQr.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanning();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void initViews() {

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText = findViewById(R.id.text_title);

        // recipient
        edtEmail = findViewById(R.id.edtEmail);
        foundNameTextView = findViewById(R.id.text_found_name);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                foundNameTextView.setVisibility(View.GONE);
                if (testUn) {
                    String userName = edtEmail.getText().toString().trim();
                    if (userName.length() > 2) {
                        mCheckNameHandler.removeMessages(0);
                        mCheckNameHandler.sendEmptyMessageDelayed(0, 2000);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mCheckNameHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);
                checkUserName();
            }
        };

        // qr layout
        layoutQr = findViewById(R.id.layout_qr);

        scannerView = findViewById(R.id.scanner_cart);
        mCodeScanner = new CodeScanner(mContext, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String qrcodeString = result.getText();
                        Log.e("qrcode", qrcodeString);


                        handleQrResult(qrcodeString);
                    }
                });
            }
        });

        // button scan recipient
        btnScanRecipient = findViewById(R.id.button_scan_recipient);
        btnScanRecipient.setVisibility(View.GONE);
        btnScanRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch QR Scanner
                // startActivity(new Intent(mContext, ActivityScanRecipient.class));
                layoutQr.setVisibility(View.VISIBLE);
                // mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
                if (checkPermissions(mContext, PERMISSION_REQUEST_QRSCAN_STRING, false, PERMISSION_REQUEST_CODE_QRSCAN)) {
                    mCodeScanner.startPreview();
                }

            }
        });

        // button submit
        Button btnUseThisPerson = findViewById(R.id.btnUseThisPerson);
        btnUseThisPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEmail.getText().toString().isEmpty()) {
                    showToastMessage("Please enter recipient");
                    return;
                }

                Intent intent = new Intent(mContext, ActivityWorkProjects.class);
                intent.putExtra("name", edtEmail.getText().toString());
                startActivity(intent);
            }
        });

        // button submit
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityWorkSearchByIndustry.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleQrResult(String qrCodeString) {
        UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(qrCodeString);
        if (qrCodeString.contains("usern")) {
            testUn = true;
            String recipient = "";
            if (urlQuery.getValue("usern") != null) {
                recipient = urlQuery.getValue("usern");
            }
            String memo = "";
            if (urlQuery.getValue("pNote") != null) {
                memo = urlQuery.getValue("pNote");
            }
            double amt = 0;
            testUn = true;
            edtEmail.setText(recipient);
            edtEmail.setEnabled(false);

            layoutQr.setVisibility(View.GONE);
        }
    }

    private void startScanning() {
        if (checkPermissions(mContext, PERMISSION_REQUEST_QRSCAN_STRING, false, PERMISSION_REQUEST_CODE_QRSCAN)) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("ActivityPayCart", "Permission granted!");

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            startScanning();
        } else {
            showToastMessage("Need permissions to use function.");
        }
    }

    private void checkUserName() {
        String userName = edtEmail.getText().toString().trim();

        if (userName.contains("@")) {
            userName = userName.replace("@", "");
        }

        if (TextUtils.isEmpty(userName)) {
            showToastMessage("Please input username.");
            return;
        }

        foundNameTextView.setVisibility(View.GONE);
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "cjlGet",
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
                    hideKeyboard();
                    Log.e("testUN", response);
                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray baseJsonArray = new JSONArray(response);
                            if (baseJsonArray.length() > 0) {
                                if (baseJsonArray.length() == 1) {
                                    JSONObject statusObject = baseJsonArray.getJSONObject(0);
                                    if (statusObject.has("status") && statusObject.get("status") instanceof Boolean && !statusObject.getBoolean("status")) {
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
                                        foundNameTextView.setText(HtmlCompat.fromHtml("<b>Found:</b> " + foundName, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                        foundNameTextView.setVisibility(View.VISIBLE);
                                    } else {
                                        foundNameTextView.setVisibility(View.GONE);
                                    }
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
