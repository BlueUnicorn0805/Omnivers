package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.ActivityPayCart.MODE_PAY_SEND;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.SearchRestaurantHelper;

public class QRCodeActivity extends BaseActivity {

    private final String TAG = "QRCodeActivity";

    boolean scanValet = false;

    AppCompatActivity _activity;
    private CodeScanner mCodeScanner;
    boolean processing = false;

    int valertServiceOption = -1;
    Button showIdBtn;

    Handler mUIHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode);

        Intent intent = getIntent();
        scanValet = intent.getBooleanExtra("scan_valet", false);
        showIdBtn = findViewById(R.id.btnShowId);
//        showIdBtn.setOnClickListener(v -> startActivity(new Intent(mContext, ActivityShowId.class)));
        showIdBtn.setOnClickListener(v -> startActivity(new Intent(mContext, ActivityFTFSendReceive.class)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Scan QR Code");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initStuff();

        mUIHandler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                final String scanResult = msg.getData().getString("result");
                Log.e(TAG, "updated Result => : " + scanResult);

            /*if (scanResult.toLowerCase().contains("iyestaurants.com")) {
                showAlert("This is an YES app QR code, please use YES app to read.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processing = false;
                    }
                });
                return;
            }*/

                UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(scanResult);
                Log.e(TAG, "updated urlQuery =>  " + urlQuery);
                if (urlQuery.getValue("pmt") != null) {
                    int pmt = 0;
                    if (urlQuery.hasParameter("pmt"))
                        pmt = Integer.parseInt(urlQuery.getValue("pmt"));

                    switch (pmt) {
                        case 410:
                            Intent intent = new Intent(mContext, ActivityPayCart.class);
                            intent.putExtra("mode", MODE_PAY_SEND);
                            intent.putExtra("scanResult", scanResult);
                            Log.d("Result", "scanned -> " + scanResult);
                            startActivity(intent);
                            finish();
                            return;
                        case 750:
                            Log.d("Result", "scanned -> " + scanResult);
                            startActivity(new Intent(mContext, ActivityPayCartMultiProducts.class)
                                    .putExtra("mode", MODE_PAY_SEND)
                                    .putExtra("scanResult", scanResult));
                            finish();
                            return;
                        case 700:
                        case 710:
                        case 720:
                            startActivity(new Intent(mContext, ActivityVote.class));
                            finish();
                            break;
                        case 701:
                            Intent intentScan = new Intent(mContext, ActivityFTFSendReceive.class);
                            intentScan.putExtra("mode", MODE_PAY_SEND);
                            intentScan.putExtra("scanResult", scanResult);
                            startActivity(intentScan);
                            finish();
                            return;
                        case 260:
                            try {
                                double lat = Float.parseFloat(urlQuery.getValue("lat"));
                                double lon = Float.parseFloat(urlQuery.getValue("lon"));
                                //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", lat, lon));

                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                //if (mapIntent.resolveActivity(_ctx.getPackageManager()) != null) {
                                startActivity(mapIntent);
                                finish();
                                //}

                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                finish();
                            }
                            break;
                        case 20:
                            showChooseValetDialog(true);
                            break;
                        case 222:
                            startActivity(new Intent(mContext, ActivityShowId.class));
                            finish();
                            break;
                        case 107:
                        case 510:
                        case 555:
                            if (getLocation()) {

                                IndustryInfo industryInfo = new IndustryInfo();
                                industryInfo.setIndustryID("487");
                                industryInfo.setTypeDesc("Schedule Pickup");

                                String extraParams = "&sellerID=" + "0" +
                                        "&industryID=" + "487" +
                                        "&Company=" + "" +
                                        "&B=" + "0" +
                                        "&L=" + "0" +
                                        "&D=" + "0" +
                                        "&it=" + "0" +
                                        "&mx=" + "0" +
                                        "&am=" + "0" +
                                        "&asi=" + "0" +
                                        "&des=" + "0" +
                                        "&semp=" + urlQuery.getValue("semp") +
                                        "&fr=" + "0" +
                                        "&sal=" + "0" +
                                        "&sea=" + "0" +
                                        "&sf=" + "0" +
                                        "&stk=" + "0" +
                                        "&Deli=" + "0" +
                                        "&gr=" + "0" +
                                        "&ind=" + "0" +
                                        "&jew=" + "0" +
                                        "&veg=" + "0" +
                                        "&gFr=" + "0" +
                                        "&cof=" + "0" +
                                        "&bar=" + "0" +
                                        "&cat=" + "0" +
                                        "&res=" + "0" +
                                        "&del=" + "0" +
                                        "&mode=" + pmt;

                                new SearchRestaurantHelper(QRCodeActivity.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                                    @Override
                                    public void onFailed(String message) {
                                        showAlert(message, v -> finish());
                                    }

                                    @Override
                                    public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {
                                        if (restaurants.isEmpty()) {
                                            showAlert("No search Result", v -> finish());
                                            return;
                                        }

                                        //startActivity(new Intent(mContext,ActivityServiceProvider.class));
                                        Intent intent = new Intent(mContext, ServiceListActivity.class);
                                        intent.putExtra("parent", "industry");
                                        intent.putExtra("industry_info", industryInfo);
                                        intent.putExtra("restaurants", restaurants);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, SearchRestaurantHelper.MODE_TYPE).execute();
                            } else {
                                Toast.makeText(QRCodeActivity.this, "Could not get Location!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            break;
                        case -79:
                            boolean lowercase = scanResult.contains("pmt");
                            String SID = new UrlQuerySanitizer(scanResult).getValue(lowercase ? "sid" : "SID");

                            if (SID == null || SID.isEmpty())
                                SID = "0";

                            String extraParams = "&sellerid=" + SID +
                                    "&industryID=" + "123" +
                                    "&Company=" + "" +
                                    "&B=" + "0" +
                                    "&L=" + "0" +
                                    "&D=" + "0" +
                                    "&it=" + "0" +
                                    "&mx=" + "0" +
                                    "&am=" + "0" +
                                    "&asi=" + "0" +
                                    "&des=" + "0" +
                                    "&fr=" + "0" +
                                    "&sal=" + "0" +
                                    "&sea=" + "0" +
                                    "&sf=" + "0" +
                                    "&stk=" + "0" +
                                    "&Deli=" + "0" +
                                    "&gr=" + "0" +
                                    "&ind=" + "0" +
                                    "&jew=" + "0" +
                                    "&veg=" + "0" +
                                    "&gFr=" + "0" +
                                    "&cof=" + "0" +
                                    "&bar=" + "0" +
                                    "&cat=" + "0" +
                                    "&res=" + "0" +
                                    "&del=" + "0" +
                                    "&mode=" + SearchRestaurantHelper.MODE_NEARBY;

                            String finalSID = SID;
                            new SearchRestaurantHelper(QRCodeActivity.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                                @Override
                                public void onFailed(String message) {
                                    showAlert(message, v -> {
                                        processing = false;
                                        finish();
                                    });
                                }

                                @Override
                                public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {

                                    if (restaurants.isEmpty()) {
                                        showAlert("No search result", v -> {
                                            processing = false;
                                            finish();
                                        });
                                        return;
                                    }

                                    Intent intent = new Intent(mContext, ServiceListActivity.class);
                                    intent.putExtra("SID", finalSID);
                                    intent.putExtra("restaurants", restaurants);
                                    intent.putExtra("parent", "NEMT");
                                    startActivity(intent);
                                    finish();
                                }
                            }, SearchRestaurantHelper.MODE_TYPE).execute();
                            break;
                        case 7:
                            Intent i = new Intent(mContext, ActivityFTFSendReceive.class);
                            i.putExtra("SCANNED_DATA", scanResult);
                            startActivity(i);
                            finish();
                            break;
                        case 4:
                        case 5:
                            Intent scannedIntent = new Intent(mContext, ActivityFTFReceiveResult.class);
                            scannedIntent.putExtra("SCANNED_DATA", scanResult);
                            startActivity(scannedIntent);
                            finish();
//                            HashMap<String, String> params = new HashMap<>();
//
//                            String baseUrl = BaseFunctions.getBaseUrl(QRCodeActivity.this,
//                                    "CJLGet",
//                                    BaseFunctions.MAIN_FOLDER,
//                                    getUserLat(),
//                                    getUserLon(),
//                                    mMyApp.getAndroidId());
//                            String queryParams =
//                                    "&mode=" + "nameFromHandle" +
//                                            "&handle=" + urlQuery.getValue("handle");
//                            baseUrl += queryParams;
//
//                            RequestQueue queue = Volley.newRequestQueue(mContext);
//                            GoogleCertProvider.install(mContext);
//                            String finalBaseUrl = baseUrl;
//                            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    hideProgressDialog();
//
//                                    Log.e("cjlrtnMLID", response);
//                                    String fullName = "";
//                                    try {
//                                        // Refresh Data
//                                        JSONArray jsonArray = new JSONArray(response);
//                                        JSONObject responseStatus = jsonArray.getJSONObject(0);
//                                        fullName = responseStatus.getString("fullname");
//                                        showAlert(fullName, v -> finish());
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                        showAlert(e.getMessage(), v -> finish());
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Toast.makeText(QRCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            }) {
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    return params;
//                                }
//                            };
//
//                            sr.setRetryPolicy(new DefaultRetryPolicy(
//                                    25000,
//                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//                            sr.setShouldCache(false);
//                            queue.add(sr);
                            break;
                        case 57:
                            StringBuilder message = new StringBuilder();

                            HashMap<String, String> params = new HashMap<>();

                            String baseUrl = BaseFunctions.getBaseUrl(QRCodeActivity.this,
                                    "CJLGet",
                                    BaseFunctions.MAIN_FOLDER,
                                    getUserLat(),
                                    getUserLon(),
                                    mMyApp.getAndroidId());

                            int tOrderID = 0;

                            if (urlQuery.getValue("orderid") != null) {
                                try {
                                    tOrderID = Integer.parseInt(urlQuery.getValue("orderid"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            String queryParams = "&mode=DriverBadgeID" +
                                    "&pmt=" + urlQuery.getValue("pmt") +
                                    "&orderid=" + tOrderID +
                                    "&semp=" + urlQuery.getValue("semp");
                            baseUrl += queryParams;

                            Log.e("request", "request -> " + baseUrl);

                            RequestQueue queue = Volley.newRequestQueue(mContext);
                            GoogleCertProvider.install(mContext);
                            String finalBaseUrl = baseUrl;
                            showProgressDialog();
                            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    hideProgressDialog();

                                    Log.e("response", "response ->" + response);
                                    try {
                                        JSONArray responseArray = new JSONArray(response);
                                        JSONObject responseJSON = responseArray.getJSONObject(0);

                                        if (responseJSON.has("status")) {
                                            showAlert(responseJSON.getString("msg"), v -> finish());
                                            return;
                                        }

                                        message.append("Company : ").append(responseJSON.get("CO")).append("\n");
                                        message.append("FN : ").append(responseJSON.get("FN")).append("\n");
                                        message.append("LN : ").append(responseJSON.get("LN")).append("\n");
                                        message.append("CP : ").append(responseJSON.get("CP")).append("\n");
                                        message.append("WP : ").append(responseJSON.get("WP")).append("\n");
                                        message.append("Title : ").append(responseJSON.get("Title")).append("\n");

                                        if (responseJSON.has("orderid") && responseJSON.getInt("orderid") > 0)
                                            message.append("OrderID : ").append(urlQuery.getValue("orderid")).append("\n");

                                        showAlert(message.toString(), v -> finish());

                                    } catch (Exception e) {
                                        Log.e("Exception", e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    hideProgressDialog();
                                    if (TextUtils.isEmpty(error.getMessage())) {
                                        showAlert(R.string.error_invalid_response);
                                    } else {
                                        showAlert(error.getMessage());
                                    }
                                    finish();
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

                            break;
                        default:
                            if (scanResult != null && (scanResult.contains("http") || scanResult.contains("www."))) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                alertDialogBuilder
                                        .setTitle(scanResult)
                                        .setMessage("Would you like to open this in your browser?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                try {
                                                    String url = scanResult;
                                                    if (!scanResult.startsWith("http://") && !scanResult.startsWith("https://")) {
                                                        url = "http://" + scanResult;
                                                    }
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                    startActivity(browserIntent);
                                                } catch (ActivityNotFoundException e) {
                                                    showAlert("No application can handle this request. Please install a web browser", v -> {
                                                        processing = false;
                                                        finish();
                                                    });
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        }).create().show();
                            } else {
                                showAlert("Found This\n\n" + scanResult, v -> {
                                    processing = false;
                                    finish();
                                });
                            }
                            break;
                    }
                } else {

                    int industid;
                    boolean hasF2f = false;
                    if (scanResult.contains("f2f")) {
                        if (Integer.parseInt(urlQuery.getValue("f2f")) == 1) {
                            hasF2f = true;
                        }
                    }

                    if (scanResult.contains("indust")) {
                        industid = 0;
                        if (urlQuery.getValue("indust") != null && !urlQuery.getValue("indust").equals(""))
                            industid = Integer.parseInt(urlQuery.getValue("indust"));
                        if (industid == 125) {
                            finish();
                            Intent intent = new Intent(mContext, ActivityMenuLocation.class);
                            intent.putExtra("chooseValet", true);
                            startActivity(intent);
                            finish();
                        }
                    } else if (scanResult.toLowerCase().startsWith("https://omnivers.info")
                            || scanResult.toLowerCase().startsWith("https://www.omnivers.info")
                            || scanResult.toLowerCase().startsWith("http://omnivers.info")
                            || scanResult.toLowerCase().startsWith("http://www.omnivers.info")) {
                        // Original Logic
                        //https://getqix.com/171/helpOthers.html?mlid=1&FN=Chuck&LN=

                /*UrlQuerySanitizer urlQuerySanitizer = new UrlQuerySanitizer();
                UrlQuerySanitizer.ValueSanitizer valueSanitizer = urlQuerySanitizer.getUrlAndSpaceLegal();
                urlQuerySanitizer.setAllowUnregisteredParamaters(true);
                urlQuerySanitizer.setUnregisteredParameterValueSanitizer(UrlQuerySanitizer.getUrlAndSpaceLegal());
                urlQuerySanitizer.parseUrl(scanResult);

                List<UrlQuerySanitizer.ParameterValuePair> paramValues = urlQuerySanitizer.getParameterList();
                try {
                    JSONObject jsonObject = new JSONObject();
                    for (UrlQuerySanitizer.ParameterValuePair item : paramValues) {
                        jsonObject.put(item.mParameter, item.mValue);
                    }

                    Intent intent = new Intent(mContext, ActivityFTFReceiveResult.class);
                    intent.putExtra("SCANNED_DATA", jsonObject.toString());
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                        Intent intent = new Intent(mContext, ActivityFTFReceiveResult.class);
                        intent.putExtra("SCANNED_DATA", scanResult);
                        startActivity(intent);
                        finish();
                    } else if (scanResult.contains("AuthCode")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                        alertDialogBuilder
                                .setMessage("Purchase ID used during purchase")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                }).create().show();
                    } else if (scanResult.contains("www.") || scanResult.contains("http")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                        alertDialogBuilder
                                .setTitle(scanResult)
                                .setMessage("Are you sure want to open this url?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        try {
                                            String url = scanResult;
                                            if (!scanResult.startsWith("http://") && !scanResult.startsWith("https://")) {
                                                url = "http://" + scanResult;
                                            }
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(browserIntent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(QRCodeActivity.this, "No application can handle this request."
                                                    + " Please install a web browser", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    finish();
                                }).create().show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(scanResult);

                            if (jsonObject.has("pmt")) {
                                int pmt = jsonObject.getInt("pmt");
                                String message = "";
                                switch (pmt) {
                                    case 57:
                                        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                                            String key = it.next();
                                            if (!key.equalsIgnoreCase("driverid")
                                                    && !key.equalsIgnoreCase("workid")
                                                    && !key.equalsIgnoreCase("pmt")) {
                                                message += key + " : " + jsonObject.get(key) + "\n";
                                            }
                                        }

                                        if (!message.isEmpty()) {
                                            showAlert(message, v -> finish());
                                        } else {
                                            Toast.makeText(_activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        break;
                                    default:
                                        Toast.makeText(_activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                }
                            } else {
                                Toast.makeText(_activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            processing = false;
                            startActivity(new Intent(QRCodeActivity.this, ActivityNotUsableResponse.class).putExtra("response", scanResult));

//                            showAlert("Not usable\n\n" + scanResult, v -> {
//                                processing = false;
//                                finish();
//                            });
                        } catch (Exception e) {
                            e.getMessage();
                            showAlert(e.getMessage(), v -> {
                                processing = false;
                                finish();
                            });
                        }
                    }

//                    try {
//                        JSONObject jsonObject = new JSONObject(scanResult);
//
//                        Intent intent = new Intent(mContext, ActivityFTFReceiveResult.class);
//                        intent.putExtra("SCANNED_DATA", scanResult);
//                        startActivity(intent);
//                        finish();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }


            }
        };
    }

    private void initStuff() {
        _activity = this;
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String qrcodeString = result.getText();

                        if (!processing) {

                            // Vibrate
                            vibrate(200);

                            Log.e("ScanResult", "ScanResult -> " + qrcodeString);

                            processing = true;

                            Message message = new Message();
                            message.what = 0;
                            Bundle data = new Bundle();
                            data.putString("result", qrcodeString);
                            message.setData(data);
                            mUIHandler.sendMessage(message);
                        }
                    }
                });
            }
        });
        /*scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });*/
    }

    private void startScanning() {
        if (checkPermissions(mContext, PERMISSION_REQUEST_QRSCAN_STRING, false, PERMISSION_REQUEST_CODE_QRSCAN)) {
            mCodeScanner.startPreview();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUIHandler.removeMessages(0);
        mUIHandler.removeMessages(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
