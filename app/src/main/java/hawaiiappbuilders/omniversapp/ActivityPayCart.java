package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.MissionItem;
import hawaiiappbuilders.omniversapp.model.Videos;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;
import me.abhinay.CurrencyEditText;

public class ActivityPayCart extends BaseActivity {
    public static final String TAG = ActivityPayCart.class.getSimpleName();
    public String MODE_API;

    public String modeFromExtras;
    // defined modes
    public static String MODE_CART = "cart";
    public static String MODE_PAY_SEND = "pay/send";

    TextView titleText;
    FrameLayout layoutQr;
    CodeScannerView scannerView;
    Button btnScanRecipient;
    EditText edtEmail; // recipient
    TextView foundNameTextView;

    LinearLayout layoutMemo;

    EditText edtMemo;
    CurrencyEditText edtAmount;
    private String current = "";
    DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    LinearLayout layoutQuantity;
    EditText edtQuantity;
    LinearLayout layoutRecurring;
    Spinner recurringPaymentsSpinner;
    LinearLayout layoutMission;
    Spinner missionSpinner;
    TextView missionNameTextView;
    TextView missionDescriptionTextView;
    Button btnSend;

    /*Button btnHope;
    Button btnFounder;*/
    int sellerID;

    Handler mCheckNameHandler;
    private CodeScanner mCodeScanner;

    String[] recurringPayments;
    boolean hasQuantity = false;
    boolean testUn = false;
    long productId;
    double missionPrice;

    Videos bodyVideoData;
    ImageView btnToolbarHome;

    TextView textTotalAmt;
    ImageView qtyDown;
    ImageView qtyUp;

    boolean isMemoHidden;

    int freq = 1;

    PhonenumberUtils phonenumberUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_cart);
        phonenumberUtils = new PhonenumberUtils(this);
        initViews();

        btnToolbarHome = findViewById(R.id.btnToolbarHome);
        btnToolbarHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        // defaults
        hasQuantity = false;
        foundNameTextView.setVisibility(View.GONE);
        layoutMission.setVisibility(View.GONE);
        // layoutQuantity.setVisibility(View.GONE);  // We always show it
        layoutQr.setVisibility(View.GONE);
        btnScanRecipient.setVisibility(View.VISIBLE);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            String mode = extras.getString("mode");
            if (mode != null) {
                modeFromExtras = mode;
                if (mode.contentEquals(MODE_CART)) {
                    bodyVideoData = extras.getParcelable("event");
                    sellerID = bodyVideoData.getSellerID();
                    titleText.setText("Cart");
                    String handle = bodyVideoData.getPOC();
                    productId = bodyVideoData.getProdID();
                    double amt = bodyVideoData.getGenAdmission();
                    String eventTitle = bodyVideoData.getTitle();
                    btnScanRecipient.setVisibility(View.GONE);
                    edtEmail.setText(handle);
                    edtEmail.setEnabled(false);
                    // layoutQuantity.setVisibility(View.VISIBLE); // We always show it
                    edtQuantity.setText(String.valueOf(2)); // set 2 as default value
                    hasQuantity = true;
                    DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
                    String formattedAmt = "$" + formatter.format(amt);
                    edtAmount.setText(formattedAmt);

                    edtMemo.setText(eventTitle);
                    edtMemo.setEnabled(false);

                    //edtAmount.setEnabled(false);

                    testUn = false; // call testUN api if true, false otherwise.
                    MODE_API = "270"; // Tickets
                    btnSend.setText("PURCHASE");
                    // testUN is only set to true if user scan recipient from qr
                } else if (mode.contentEquals(MODE_PAY_SEND)) {
                    titleText.setText("Pay/Send");
                    // layoutQuantity.setVisibility(View.GONE); // We always show it

//                    https://OmniVers.info/biz/?appid=17&semp=1&handle=hope&d=2024-04-23&prodid=1&msg=&qrid=0&pmt=410

                    hasQuantity = false;
                    edtEmail.setEnabled(true);
                    testUn = true;
                    edtMemo.setEnabled(true);
                    MODE_API = "161"; // Funds Direct
                    btnSend.setText("SEND");
                    if (extras.get("scanResult") != null) {
                        // from QR view, get usern
                        handleQrResult(extras.getString("scanResult"));
                    }
                } else {
                    showToastMessage("mode undefined");
                    finish();
                }
            } else {
                Log.e("ActivityPayCart", "unknown mode");
                finish();
            }
        }
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
        /*btnHope = findViewById(R.id.button_mode_hope);
        btnFounder = findViewById(R.id.button_mode_founder);*/

//        // for qr
//        btnHope.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String recipient = "hope";
//                String memo = "church donation";
//                double amt = 12.00;
//                testUn = true;
//                edtEmail.setText(recipient);
//                edtEmail.setEnabled(false);
//
//                // memo is hidden if mission items > 0
//                edtMemo.setText(memo);
//                edtMemo.setEnabled(false);
//                edtAmount.setText("$" + amt);
//            }
//        });
//
//        // for qr
//        btnFounder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String recipient = "founder";
//                String memo = "cash";
//                double amt = 24.00;
//                sellerID = 1;
//                testUn = false;
//                edtEmail.setText(recipient);
//                edtEmail.setEnabled(false);
//                layoutMemo.setVisibility(View.VISIBLE);
//                edtMemo.setText(memo);
//                edtMemo.setEnabled(false);
//                edtAmount.setText("$" + amt);
//            }
//        });
//        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

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

        // memo
        layoutMemo = findViewById(R.id.layout_memo);
        edtMemo = findViewById(R.id.edtMemo);

        // recurring spinner layout
        layoutRecurring = findViewById(R.id.layout_recurring_payments);

        recurringPaymentsSpinner = findViewById(R.id.spinnerPlaceholder1);
        recurringPayments = new String[]{"Choose One", "One Time", "Weekly", "Monthly", "Annually"};
        ArrayAdapter<String> paymentsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item, recurringPayments);
        recurringPaymentsSpinner.setAdapter(paymentsAdapter); // visible always
        recurringPaymentsSpinner.setSelection(0);
        recurringPaymentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = recurringPaymentsSpinner.getSelectedItemPosition();
                freq = 1;
                switch (pos) {
                    case 2:
                        freq = 7;
                        break;
                    case 3:
                        freq = 30;
                        break;
                    case 4:
                        freq = 365;
                        break;
                }
                updateTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // mission spinner layout
        layoutMission = findViewById(R.id.layout_mission);

        missionSpinner = findViewById(R.id.spinnerPlaceholder2);
        missionNameTextView = findViewById(R.id.text_mission_name);
        missionDescriptionTextView = findViewById(R.id.text_mission_description);

        layoutQuantity = findViewById(R.id.layout_quantity);
        layoutQuantity.setVisibility(View.VISIBLE);

        // quantity
        textTotalAmt = findViewById(R.id.text_total_amount);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    textTotalAmt.setVisibility(View.VISIBLE);
                    if (charSequence.toString().contentEquals(".")) {
                        edtAmount.setText("0.");
                    } else {
                        updateTotalAmount();
                    }
                } else {
                    textTotalAmt.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        qtyDown = findViewById(R.id.btnDown);
        qtyUp = findViewById(R.id.btnUp);

        qtyDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtQuantity.getText().toString().isEmpty()) {
                    if (Integer.parseInt(edtQuantity.getText().toString()) > 0) {
                        int qtyAmt = Integer.parseInt(edtQuantity.getText().toString()) - 1;
                        edtQuantity.setText(String.valueOf(qtyAmt));
                    } else {
                        edtQuantity.setText(String.valueOf(0));
                    }
                }
            }
        });
        qtyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtQuantity.getText().toString().isEmpty()) {
                    int qtyAmt = Integer.parseInt(edtQuantity.getText().toString()) + 1;
                    edtQuantity.setText(String.valueOf(qtyAmt));
                }
            }
        });
        // amount
        edtAmount = findViewById(R.id.edtAmount);
        // limit decimal places to 2
        //edtAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        edtAmount.setCurrency("USD");
        edtAmount.setDelimiter(false);
        edtAmount.setSpacing(false);
        edtAmount.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        edtAmount.setSeparator(",");

        edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                updateTotalAmount();

                /*if (charSequence.toString().contains("$")) {
                    charSequence = charSequence.toString().replace("$", "");
                }

                if (!charSequence.toString().isEmpty()) {
                    textTotalAmt.setVisibility(View.VISIBLE);
                    if (charSequence.toString().contentEquals(".")) {
                        edtAmount.setText("0.");
                        edtAmount.setSelection(edtAmount.length());
                    } else {
                        updateTotalAmount();
                    }
                } else {
                    textTotalAmt.setVisibility(View.GONE);
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

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
                        /*// switch, check type
                        // title dialog - not usable by seekur
                        String[] params = qrcodeString.split("&");
                        Map<String, String> map = new HashMap<String, String>();
                        for (String param : params) {
                            String name = param.split("=")[0];
                            String value = param.split("=")[1];
                            map.put(name, value);
                        }

                        if(map.containsValue("type")) {
                            showToastMessage("Type is " + map.get("type"));
                        }*/

                        // https://z99.io?appID=192&MLID=186305&d=2023-02-02&prj=1&usern=hope&m=1&pNote=Your%20Custom%20QRCode<BR>Message%20Here
                        // buyerID=login.userID
                        // sellerID=hope lookup
                        // note=pNote memo

                        handleQrResult(qrcodeString);

                    }
                });
            }
        });

        // button scan recipient
        btnScanRecipient = findViewById(R.id.button_scan_recipient);
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
        btnSend = findViewById(R.id.btnSend);
        btnSend.setTag(0);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEmail.getText().toString().isEmpty()) {
                    showToastMessage("Please enter recipient");
                    return;
                }

                if (edtEmail.getText().toString().length() < 4) {
                    showToastMessage("Invalid recipient");
                    return;
                }

                if (!(sellerID > 0 || isEmailValid(edtEmail.getText().toString()))) {
                    Toast.makeText(ActivityPayCart.this, "Recipient Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                String amt = edtAmount.getText().toString().trim();
                double amtValue;
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
                } catch (Exception e) {
                }
                if (TextUtils.isEmpty(amt)) {
                    showToastMessage("Please input amount");
                    return;
                } else {
                    amt = amt.replace("$", "");
                    amt = amt.replace(",", "");

                    amtValue = Double.parseDouble(amt) * quantity;
                    if (amtValue <= 0) {
                        showToastMessage("Please input an amount greater than 0");
                        return;
                    }
                }

                if (isCart()) {
                    if (edtQuantity.getText().toString().isEmpty()) {
                        showToastMessage(mContext, "Please enter quantity");
                        return;
                    }
                }

                int pos = recurringPaymentsSpinner.getSelectedItemPosition();
                if (pos == 0) {
                    showToastMessage("Please select recurring payment frequency");
                    return;
                }
                askForPIN(amtValue);
            }
        });
    }

    private void updateTotalAmount() {
        /*if (isPaySend()) {
            if (!edtAmount.getText().toString().isEmpty()) {
                String amountText = edtAmount.getText().toString();
                if (amountText.contains("$")) {
                    amountText = amountText.replace("$", "");
                }
                textTotalAmt.setVisibility(View.VISIBLE);
                //double amtVal = Double.parseDouble(amountText) * freq;
                BigDecimal amtVal = new BigDecimal(Double.parseDouble(amountText) * freq);

                textTotalAmt.setText(String.format("Total: $%s", DataUtil.toAmountFormat(amtVal)));
            } else {
                textTotalAmt.setVisibility(View.GONE);
            }
        } else {*/
        Log.e("Amt", edtAmount.getText().toString());
        if (!edtAmount.getText().toString().isEmpty()) {

            String amountText = edtAmount.getText().toString().trim();

            int qtyAmt = 0;
            if (!edtQuantity.getText().toString().isEmpty()) {
                qtyAmt = Integer.parseInt(edtQuantity.getText().toString());
            }
            if (amountText.contains("$")) {
                amountText = amountText.replace("$", "");
            }
            if (amountText.contains(",")) {
                amountText = amountText.replace(",", "");
            }

            textTotalAmt.setVisibility(View.VISIBLE);
            //double amtVal = Double.parseDouble(amountText);
            //double totalAmt = qtyAmt * amtVal * freq;
            BigDecimal amtVal = new BigDecimal(amountText);
            BigDecimal totalAmt = new BigDecimal(qtyAmt /** freq*/);
            totalAmt = totalAmt.multiply(amtVal);

            textTotalAmt.setText(String.format("Total: $%s", DataUtil.toAmountFormat(totalAmt)));
        } else {
            textTotalAmt.setVisibility(View.GONE);
        }
        /*}*/
    }

    public Double getTotalAmount() {
        double finalAmount = 0;
        /*if (isPaySend()) {
            double amtVal;
            if (!edtAmount.getText().toString().isEmpty()) {
                String amountText = edtAmount.getText().toString();
                if (amountText.contains("$")) {
                    amountText = amountText.replace("$", "");
                }
                textTotalAmt.setVisibility(View.VISIBLE);
                amtVal = Double.parseDouble(amountText) * freq;
                finalAmount = Double.parseDouble(DataUtil.toAmountFormat(amtVal));
            }
        } else {*/
        double amtVal = 0;
        double totalAmt = 0;
        if (!edtAmount.getText().toString().isEmpty()) {
            String amountText = edtAmount.getText().toString();

            int qtyAmt = Integer.parseInt(edtQuantity.getText().toString());
            if (amountText.contains("$")) {
                amountText = amountText.replace("$", "");
            }

            if (amountText.contains(",")) {
                amountText = amountText.replace(",", "");
            }

            textTotalAmt.setVisibility(View.VISIBLE);
            amtVal = Double.parseDouble(amountText);
            totalAmt = qtyAmt * amtVal/* * freq*/;
            finalAmount = Double.parseDouble(DataUtil.toAmountFormat(totalAmt));
        }
        /*}*/
        return finalAmount;
    }

    private void handleQrResult(String qrCodeString) {
        UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(qrCodeString);
        if (qrCodeString.contains("handle")) {
            testUn = true;
            String recipient = "";
            if (urlQuery.getValue("handle") != null) {
                recipient = urlQuery.getValue("handle");
            }
            String memo = "";
            if (urlQuery.getValue("pNote") != null) {
                memo = urlQuery.getValue("pNote");
            }
            String prodid = "";
            if (urlQuery.getValue("prodid") != null) {
                prodid = urlQuery.getValue("prodid");
            }
            double amt = 0;
            testUn = true;
            edtEmail.setText(recipient);
            edtEmail.setEnabled(false);

            // memo is hidden if mission items > 0
            edtMemo.setText(memo);
            edtMemo.setEnabled(false);
            edtAmount.setText(String.format("$%.02f", amt));
            layoutQr.setVisibility(View.GONE);
        }
    }

    private boolean isCart() {
        return modeFromExtras.contentEquals(MODE_CART);
    }

    private boolean isPaySend() {
        return modeFromExtras.contentEquals(MODE_PAY_SEND);
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

        foundNameTextView.setVisibility(View.GONE);
        layoutMission.setVisibility(View.GONE);
        missionDescriptionTextView.setVisibility(View.GONE);
        layoutMemo.setVisibility(View.VISIBLE);
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
                    if (!response.isEmpty()) {
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
                                    if (baseInfoObject.getString("name") != null
                                            && !baseInfoObject.getString("name").isEmpty()) {
                                        String foundName = baseInfoObject.getString("name");
                                        foundNameTextView.setText(HtmlCompat.fromHtml("<b>Found:</b> " + foundName, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                        foundNameTextView.setVisibility(View.VISIBLE);
                                    } else {
                                        foundNameTextView.setVisibility(View.GONE);
                                    }

                                    String toMLID = baseInfoObject.getString("toMLID");
                                    String handle = baseInfoObject.getString("handle");
                                    sellerID = Integer.parseInt(toMLID);

                                    // recurring payments
                                    int spin1 = baseInfoObject.getInt("spin1");
                                    // TODO:  Set recurring payments layout to always visible
                                    /*if (spin1 == 1) {
                                        layoutRecurring.setVisibility(View.VISIBLE);
                                    } else {
                                        layoutRecurring.setVisibility(View.GONE);
                                    }*/

                                    // missions
                                    int spin2 = baseInfoObject.getInt("spin2");
                                    if (spin2 == 1) { // Show mission spinner

                                        edtQuantity.setText("1");
                                        layoutQuantity.setVisibility(View.VISIBLE);

                                        layoutMission.setVisibility(View.VISIBLE);
                                        String spin2Label = baseInfoObject.getString("spin2label");
                                        missionNameTextView.setText(spin2Label);

                                        //layoutMemo.setVisibility(View.GONE);

                                        isMemoHidden = true;
                                        // String spin2Description = baseInfoObject.getString("spin2des");
                                        // missionDescriptionTextView.setText(spin2Description);

                                        JSONArray missionItems = (JSONArray) baseJsonArray.get(3);

                                        if (missionItems.length() > 0) {
                                            layoutMission.setVisibility(View.VISIBLE);
                                            Gson gson = new Gson();
                                            ArrayList<MissionItem> missionList = new ArrayList<>();
                                            String[] missionNames = new String[missionItems.length()];
                                            for (int i = 0; i < missionItems.length(); i++) {
                                                MissionItem newItem = gson.fromJson(missionItems.getString(i), MissionItem.class);
                                                missionList.add(newItem);
                                                missionNames[i] = newItem.getName();
                                            }

                                            ArrayAdapter<String> missionsAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list_item, missionNames);
                                            missionSpinner.setAdapter(missionsAdapter);

                                            missionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    productId = missionList.get(position).getProdID();
                                                    DecimalFormat formatter = new DecimalFormat("##0.00");
                                                    missionPrice = Double.parseDouble(formatter.format(missionList.get(position).getPrice()));
                                                    edtAmount.setText(String.format("$%.02f", missionPrice));
                                                    //edtAmount.setEnabled(false);
                                                    missionDescriptionTextView.setText(missionList.get(position).getDes());
                                                    missionDescriptionTextView.setVisibility(View.VISIBLE);

                                                    edtMemo.setText(missionList.get(position).getName());
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });

                                            missionSpinner.setSelection(0);
                                        } else {
                                            isMemoHidden = false;
                                            layoutMemo.setVisibility(View.VISIBLE);
                                            layoutMission.setVisibility(View.GONE);
                                            missionDescriptionTextView.setVisibility(View.GONE);
                                            missionSpinner.setAdapter(null);
                                            // missionSpinner.setSelection(0);
                                        }
                                    } else {
                                        edtQuantity.setText("1");
                                        layoutQuantity.setVisibility(View.GONE);

                                        //edtAmount.setEnabled(true);
                                        layoutMission.setVisibility(View.GONE);
                                        missionDescriptionTextView.setVisibility(View.GONE);
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

    private void askForPIN(double amtValue) {
        edtMemo.setEnabled(false);
        //edtAmount.setEnabled(false);
        LayoutInflater inflater = getLayoutInflater();
        View pinLayout = inflater.inflate(R.layout.dialog_pin_w_amount, null);
        final TextView title = pinLayout.findViewById(R.id.dialog_title);
        final TextView amount = pinLayout.findViewById(R.id.dialog_amount);
        title.setText("Enter PIN to Complete Payment");
        final EditText pin = pinLayout.findViewById(R.id.pin);

        final ImageView grey_line = pinLayout.findViewById(R.id.grey_line);
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        String formattedAmt = "Amount: $" + formatter.format(amtValue);
        amount.setText(formattedAmt);


        grey_line.setVisibility(View.GONE);
        pin.requestFocus();

        final Button submit = pinLayout.findViewById(R.id.pin_submit);
        submit.setText("Continue");

        final Button cancel = pinLayout.findViewById(R.id.pin_cancel);
        cancel.setText("Cancel");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(pinLayout);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPIN = appSettings.getPIN();
                String pinNumber = pin.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                    if (getLocation()) {
                        addSms();
                    }
                    dialog.dismiss();
                    edtMemo.setEnabled(true);
                    //edtAmount.setEnabled(true);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                edtMemo.setEnabled(false);
                //edtAmount.setEnabled(false);
            }
        });
    }

    private void addSms() {
        String userName = edtEmail.getText().toString().trim();
        String amt = edtAmount.getText().toString().trim();
        String notes = edtMemo.getText().toString().trim();
        hideKeyboard(edtAmount);

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "addSMS",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String extraParams = "";
        notes = notes.replace("&", "|||");

        // one time - 1
        // weekly - 7
        // monthly - 30
        // annually - 365

        int pos = recurringPaymentsSpinner.getSelectedItemPosition();

        if (pos == 0) {
            showToastMessage("Please select recurring payment frequency");
            return;
        }

        String quantity = edtQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(quantity)) {
            showToastMessage(mContext, "Please input quantity");
            return;
        }

        if (isCart()) {
            String missionName = missionNameTextView.getText().toString().trim();
            extraParams =
                    "&mode=" + MODE_API +
                            "&toMLID=" + sellerID +
                            "&Amt=" + amt +
                            "&email=" + userName +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&countryCode=" + appSettings.getCountryCode() +
                            "&note=" + notes +
                            "&productID=" + productId +
                            "&freq=" + freq +
                            "&qty=" + Integer.parseInt(quantity);
        } else if (isPaySend()) {
            if (isMemoHidden) {
                MODE_API = "175"; // Churches
                notes = missionSpinner.getSelectedItem().toString();
                notes = notes.replace("&", "|||");
            }

            extraParams =
                    "&mode=" + MODE_API +
                            "&toMLID=" + sellerID +
                            "&Amt=" + amt +
                            "&email=" + userName +
                            "&freq=" + freq +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&countryCode=" + appSettings.getCountryCode() +
                            "&note=" + notes +
                            "&productID=" + productId +
                            "&qty=" + Integer.parseInt(quantity);
        } else {
            return;
        }

        baseUrl += extraParams;
        notes = "";
        Log.e("Request", baseUrl);

        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(mContext);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(mContext);

        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.e("response", response.toString());

                if (response != null || !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                if (isCart()) {
                                    // Your item has been purchased
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Congratulations!")
                                            .setMessage("Your item has been purchased")
                                            .setPositiveButton("Thank you", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    onBackPressed();
                                                }
                                            }).show();
                                } else if (isPaySend()) {
                                    showSuccessDialog();
                                    sendPush(jsonObject);
                                }
                            } else {
                                if (isCart()) {
                                    new AlertDialog.Builder(mContext)
                                            .setTitle("Complications")
                                            .setMessage(jsonObject.getString("msg"))
                                            .setPositiveButton("Sorry", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // stay on this page
                                                }
                                            }).show();
                                } else if (isPaySend()) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                    alertDialogBuilder
                                            .setTitle("Results")
                                            .setMessage(jsonObject.getString("msg"))
                                            .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                            }
                        } else {
                            showAlert("Not able to contact the Attendee using Notifications.\n" +
                                    "You might want to call them.");
                        }
                    } catch (JSONException e) {
                        showAlert(e.getMessage());
                        zzzLogIt(e, "addSMS", "ActivityPayCart");
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

    private void showSuccessDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success_lrg, null);
        final android.app.AlertDialog successDlg = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDlg.dismiss();
                testUn = true;
                edtEmail.setText("");
                edtMemo.setText("");
                edtAmount.setText("0");
                layoutMission.setVisibility(View.GONE);
                layoutMemo.setVisibility(View.VISIBLE);
                edtEmail.setEnabled(true);
                edtMemo.setEnabled(true);
                btnScanRecipient.setVisibility(View.VISIBLE);
            }
        });
        successDlg.show();
        successDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void sendPush(JSONObject jsonObject) {
        NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
        String token = jsonObject.optString("Token");
        if (!TextUtils.isEmpty(token)) {
            tokenList.add(new FCMTokenData(token, FCMTokenData.OS_UNKNOWN));
        }
        if (!tokenList.isEmpty()) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("message", "You got payment");
                payload.put("orderId", 0); // all numbers/ids are 0 by default
                payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                payload.put("SenderID", appSettings.getUserId());
                notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Funds_Sent, payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
