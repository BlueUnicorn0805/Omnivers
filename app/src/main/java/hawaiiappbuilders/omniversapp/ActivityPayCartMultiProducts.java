package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Product;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityPayCartMultiProducts extends BaseActivity implements View.OnClickListener {

    TextView textCompany;
    TextView textAddress;
    private TableLayout tableLayout;
    private double totalAmount = 0;
    TextView totalTextView;
    FrameLayout layoutQr;
    CodeScannerView scannerView;
    private CodeScanner mCodeScanner;

    String TAG = "ActivityPayCartMultiProducts";

    int redMLID = 0;

    DecimalFormat formatter = new DecimalFormat("#,###,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_cart_multi_products);

        init();
        initClicks();
        initScanner();
        layoutQr.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            String mode = extras.getString("mode");
            if (mode != null) {
                handleQrResult(extras.getString("scanResult"));
            } else {
                Log.e(TAG, "unknown mode");
                finish();
            }
        }
    }

    private void init() {
        textCompany = findViewById(R.id.textCompany);
        textAddress = findViewById(R.id.textAddress);
        tableLayout = findViewById(R.id.tableLayout);
        totalTextView = findViewById(R.id.text_total_amount);
        layoutQr = findViewById(R.id.layout_qr);
        scannerView = findViewById(R.id.scanner_cart);
    }

    private void initClicks() {
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.button_scan_again).setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btnToolbarHome:
            case R.id.btnBack:
                finish();
                break;
            case R.id.button_scan_again:
                layoutQr.setVisibility(View.VISIBLE);
                hideKeyboard();
                if (checkPermissions(mContext, PERMISSION_REQUEST_QRSCAN_STRING, false, PERMISSION_REQUEST_CODE_QRSCAN)) {
                    mCodeScanner.startPreview();
                }
                break;
            case R.id.btnSend:
                askForPIN(totalAmount);
                break;
        }
    }

    private void initScanner() {
        mCodeScanner = new CodeScanner(mContext, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                vibrate(200);

                runOnUiThread(() -> {
                    String qrcodeString = result.getText();
                    Log.e(TAG, qrcodeString);
                    layoutQr.setVisibility(View.GONE);
                    handleQrResult(qrcodeString);
                });
            }
        });
    }

    private void handleQrResult(String qrcodeString) {
        if (qrcodeString != null) {
            if (!qrcodeString.isEmpty()) {
                UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(qrcodeString);
                if (urlQuery.getValue("mlid") != null && urlQuery.getValue("prodid") != null) {

                    if (redMLID != Integer.parseInt(urlQuery.getValue("mlid"))) {
                        redMLID = Integer.parseInt(urlQuery.getValue("mlid"));
//                        ((TextView) findViewById(R.id.txtMLID)).setText("MLID: " + redMLID);
                        products.clear();
                        handleTable(true, null);
                    }
                    getProduct(Integer.parseInt(urlQuery.getValue("prodid")));
                }
            }
        }
    }

    List<Product> products = new ArrayList<>();
    Gson gson = new Gson();

    private void getProduct(int prodId) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
//                    "getProdByID",
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "getProdByID" +
                            "&prodid=" + prodId;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            showProgressDialog();

            RequestQueue queue = Volley.newRequestQueue(mContext);
            String finalBaseUrl = baseUrl;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    hideProgressDialog();

                    Log.e(TAG, response);

                    try {
                        JSONArray responseArr = new JSONArray(response);
                        JSONObject responseObj = responseArr.getJSONObject(0);

                        boolean productInListAt = false;

                        Product p = gson.fromJson(responseObj.toString(), Product.class);

                        for (Product product : products) {
                            if (product.getId() == p.getId()) {
                                productInListAt = true;
                                break;
                            }
                        }

                        textCompany.setText(responseObj.optString("Co"));
                        textAddress.setText(responseObj.optString("Address"));

                        if (!productInListAt) {
                            if (p.getQty() == 0) {
                                p.setQty(1);
                            }
                            handleTable(false, p);
                        } else {
                            Toast.makeText(ActivityPayCartMultiProducts.this, "That is a Duplicate", Toast.LENGTH_SHORT).show();
                            vibrate(500);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
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

    private void handleTable(boolean newTable, Product product) {
        if (newTable) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1); // Clear all rows except the header
            totalAmount = 0;

            for (int i = 0; i < products.size(); i++) {
                addRow(products.get(i));
            }
        } else {
            products.add(product);
            addRow(product);
        }

        calculateTotalSum();
    }

    private void addRow(Product product) {
        final TableRow newRow = new TableRow(this);

        final EditText qtyEditText = new EditText(this);
        qtyEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        qtyEditText.setText(String.valueOf(product.getQty()));
        qtyEditText.setGravity(Gravity.CENTER);
        qtyEditText.setTextColor(getResources().getColor(R.color.white));
        qtyEditText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        final TextView amtTextView = new TextView(this);
        amtTextView.setText(formatter.format(product.getAmt()));
        amtTextView.setGravity(Gravity.CENTER);
        amtTextView.setTextColor(getResources().getColor(R.color.white));
        amtTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        final TextView nameTextView = new TextView(this);
        nameTextView.setText(product.getName());
        nameTextView.setGravity(Gravity.CENTER);
        nameTextView.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.padding13));
        nameTextView.setTextColor(getResources().getColor(R.color.white));
        nameTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        final TextView totTextView = new TextView(this);
        totTextView.setText(formatter.format(product.getQty() * product.getAmt()));
        totTextView.setGravity(Gravity.CENTER);
        totTextView.setTextColor(getResources().getColor(R.color.white));
        totTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        qtyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int qty = 0;
                try {
                    qty = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                product.setQty(qty);
                updateTotalOfItem(product, totTextView);
                calculateTotalSum();
            }
        });

        newRow.addView(qtyEditText);
        newRow.addView(amtTextView);
        newRow.addView(nameTextView);
        newRow.addView(totTextView);

        tableLayout.addView(newRow);
    }

    private void updateTotalOfItem(Product product, TextView totTextView) {
        double total = product.getQty() * product.getAmt();
        totTextView.setText(formatter.format(total));
    }

    private void calculateTotalSum() {
        totalAmount = 0;
        for (Product product : products) {
            totalAmount += (product.getQty() * product.getAmt());
        }
        totalTextView.setText("Total: " + formatter.format(totalAmount));
    }

    private void askForPIN(double amtValue) {
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
                    addOrder();
                    dialog.dismiss();
                } else {
                    showToastMessage(mContext, "Incorrect PIN");
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

    private void addOrder() {
        if (getLocation()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serviceusedid", 2325);
                jsonObject.put("promoid", "0");

                Date oderDueAt = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(oderDueAt);
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                String orderDueDate = DateUtil.toStringFormat_13(calendar.getTime());
                jsonObject.put("orderdueat", DateUtil.toStringFormat_17(calendar.getTime()));     // Aug 21 11:15 AM,
                jsonObject.put("industryID", "0");
                jsonObject.put("nickid", "70");
                jsonObject.put("totship", "0");
                jsonObject.put("totlabor", "0");

                jsonObject.put("orname", "");
                jsonObject.put("oraddr", "");
                jsonObject.put("orph", "");
                jsonObject.put("delname", "");
                jsonObject.put("deladdr", "");
                jsonObject.put("delzip", "");
                jsonObject.put("delph", "");
                jsonObject.put("deldir", "");

                jsonObject.put("sellerid", redMLID);
                jsonObject.put("buyerid", appSettings.getUserId());
                jsonObject.put("totprice", totalAmount);
                jsonObject.put("tottax", 0);

                JSONArray menuItemsArray = new JSONArray();
                for (Product product : products) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("prodid", product.getId());
                    itemObj.put("name", product.getName());
                    itemObj.put("des", product.getDescription());
                    itemObj.put("price", product.getAmt());
                    itemObj.put("size", "1");
                    itemObj.put("quantity", product.getQty());
                    itemObj.put("oz", "0");
                    itemObj.put("gram", "0");
                    menuItemsArray.put(itemObj);
                }
                jsonObject.put("menus", menuItemsArray);

                jsonObject.put("paynow", true);

                String baseUrl = BaseFunctions.getBaseData(jsonObject,
                        getApplicationContext(),
                        "AddOrder",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                Log.e("request", "request -> " + BaseFunctions.decodeBaseURL(baseUrl));

                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(mContext);
                String finalOrderDueDate = orderDueDate;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("CreateOrder", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    final int orderId = jsonObject.optInt("OrderID");

                                    appSettings.setOrderID(String.valueOf(orderId));
                                    appSettings.setOrderDueDate(finalOrderDueDate);

                                    showToastMessage("Success to add order!");
                                    finish();
                                } else {
                                    showAlert(jsonObject.optString("msg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_conn_error);
                        } else {
                            showAlert(error.getMessage());
                        }
                    }
                });

                sr.setShouldCache(false);
                sr.setRetryPolicy(new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                );
                queue.add(sr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutQr.getVisibility() == View.VISIBLE) {
            layoutQr.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCodeScanner = null;
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
}
