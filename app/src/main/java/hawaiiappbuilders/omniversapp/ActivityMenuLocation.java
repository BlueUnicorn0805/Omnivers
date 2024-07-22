package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.ActivityDoorAccess.ACCESS_CAR;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.SearchRestaurantHelper;

public class ActivityMenuLocation extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMenuLocation.class.getSimpleName();
    Context mContext;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    EditText edtEmail;
    boolean mLocationPermissionGranted;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_locationfunc);
        mContext = this;
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Car");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnSeeCar).setOnClickListener(this);
        findViewById(R.id.btnScanValetQR).setOnClickListener(this);
        findViewById(R.id.btnValertBringCarUp).setOnClickListener(this);
        findViewById(R.id.btnOpenCarDoor).setOnClickListener(this);
        findViewById(R.id.btnSchedulePickup).setOnClickListener(this);
        findViewById(R.id.btnReadyToPickup).setOnClickListener(this);
        findViewById(R.id.btnCheckDriverStatus).setOnClickListener(this);
        findViewById(R.id.btnFavorites).setOnClickListener(this);
        findViewById(R.id.btnTip).setOnClickListener(this);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getBoolean("chooseValet")) {
                showChooseValetDialog(false);
            } else if (getIntent().getExtras().getString("scanresult") != null) {
                String scanResult = getIntent().getExtras().getString("scanresult");
                if (scanResult.contains("indust")) {
                    UrlQuerySanitizer urlQuery = new UrlQuerySanitizer(scanResult);
                    int industid = Integer.parseInt(urlQuery.getValue("indust"));
                    if (industid == 125) {
                        showChooseValetDialog(false);
                    }
                }
            }
        }
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
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewId == R.id.btnFavorites) {
            getFavorites();
        } else if (viewId == R.id.btnTip) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            String extraParams =
                    "&mode=" + "findTipLastTx";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, response -> {
                Log.e("driverStatus", response);

                hideProgressDialog();

                if (!TextUtils.isEmpty(response)) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            if (firstObj.has("status")) {
                                if (firstObj.optBoolean("status")) {
//                                showRateDlg();
                                } else {
                                    showAlertMessage(mContext, firstObj.optString("msg"));
                                }
                            } else {
                                showRateDlg(firstObj);
                            }
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }, error -> {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(sr);
        } else if (viewId == R.id.btnSeeCar) {
            Intent carintent = new Intent(this, MyCarActivity.class);
            startActivity(carintent);
        } else if (viewId == R.id.btnScanValetQR) {
            Intent qrintent = new Intent(mContext, QRCodeActivity.class);
            qrintent.putExtra("scan_valet", true);
            startActivity(qrintent);
            finish();
        } else if (viewId == R.id.btnValertBringCarUp) {
            showToastMessage("Sent, Your car will be waiting for you at front door");
        } else if (viewId == R.id.btnOpenCarDoor) {
            showPinDlg();
        } else if (viewId == R.id.btnSchedulePickup) {
            if (getLocation()) {

                IndustryInfo industryInfo = new IndustryInfo();
                industryInfo.setIndustryID("487");
                industryInfo.setTypeDesc("Schedule Pickup");

                String extraParams = "&sellerID=" + "0" +
                        "&industryID=" + "487" +
                        "&Company=" + "" +
                        "&mode=" + SearchRestaurantHelper.MODE_NEARBY +
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
                        "&del=" + "0";

                new SearchRestaurantHelper(ActivityMenuLocation.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                    @Override
                    public void onFailed(String message) {
                        showAlert(message);
                    }

                    @Override
                    public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {
                        if (restaurants.isEmpty()) {
                            showAlert("No search Result");
                            return;
                        }

                        //startActivity(new Intent(mContext,ActivityServiceProvider.class));
                        Intent intent = new Intent(mContext, ServiceListActivity.class);
                        intent.putExtra("parent", "industry");
                        intent.putExtra("industry_info", industryInfo);
                        intent.putExtra("restaurants", restaurants);
                        startActivity(intent);
                    }
                }, SearchRestaurantHelper.MODE_TYPE).execute();
            }
        } else if (viewId == R.id.btnReadyToPickup) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "pickMeUp",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            baseUrl += "&FN=" + appSettings.getFN() +
                    "&LN=" + appSettings.getLN();
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, response -> {
                Log.e("pickMeUp", response);

                hideProgressDialog();

                if (!TextUtils.isEmpty(response)) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            if (firstObj.optBoolean("status")) {
                                finish();
                            } else {
                                showAlertMessage(mContext, firstObj.optString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(sr);
        } else if (viewId == R.id.btnCheckDriverStatus) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(mContext,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            String extraParams =
                    "&mode=" + "driverStatus" +
                            "&misc=" + 0;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, response -> {
                Log.e("driverStatus", response);

                hideProgressDialog();

                if (!TextUtils.isEmpty(response)) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            if (firstObj.optBoolean("status")) {
                                finish();
                            } else {
                                showAlertMessage(mContext, firstObj.optString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            sr.setShouldCache(false);
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(sr);
        }
    }

    private void showRateDlg(JSONObject response) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating_tip, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        TextView orderId = dialogView.findViewById(R.id.orderId);
        TextView name = dialogView.findViewById(R.id.name);
        TextView amount = dialogView.findViewById(R.id.amount);
        TextView date = dialogView.findViewById(R.id.date);

        orderId.setText("Order ID : " + response.optString("OrderID"));
        String nameValue = response.optString("Name").split("To:")[1];
        name.setText("Name : " + nameValue);
        amount.setText("Amount : $" + response.optString("Amt"));
        date.setText("Date : " + response.optString("OrderDate"));

        double availableBal = response.optDouble("availBal");

        // Ratings
        final float[] ratings = {5};
        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> ratings[0] = rating);

        final TextView tvErrorRate = dialogView.findViewById(R.id.tvErrorRate);
        tvErrorRate.setVisibility(View.GONE);

        final EditText edtTips = dialogView.findViewById(R.id.edtTips);
        if (response.optString("Tip").isEmpty()) {
            edtTips.setText("0.00");
        } else {
            edtTips.setText(response.optString("Tip"));
        }

        // Button Actions
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> inputDlg.dismiss());

        dialogView.findViewById(R.id.btnOk).setOnClickListener(v -> {

//            if (ratings[0] > 0) {

            if (availableBal > Double.parseDouble(edtTips.getText().toString())) {
                showProgressDialog();

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                        "SendRatingTipToServer",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                String extraParams =
                        "&Amt=" + edtTips.getText().toString() +
                                "&rating=" + ratings[0] +
                                "&sellerID=" + response.optString("sellerID") +
                                "&TXID=" + response.optString("TXID") +
                                "&name=" + nameValue;
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
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() > 0) {
                                JSONObject firstObj = jsonArray.getJSONObject(0);
                                if (firstObj.optBoolean("status")) {
                                    inputDlg.dismiss();
                                    showAlertMessage(mContext, firstObj.optString("msg"));
                                } else {
                                    showAlertMessage(mContext, firstObj.optString("msg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("SendRatingTipToServer", response);
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
            } else {
                showAlertMessage(mContext, "Funds not available");
            }

//            } else {
//                tvErrorRate.setVisibility(View.VISIBLE);
//            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void showPinDlg() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("For your security, enter your PIN");
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

        submit.setOnClickListener(v -> {

            String appPIN = appSettings.getPIN();
            String pinNumber = pin.getText().toString().trim();
            if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                Intent intent = new Intent(mContext, ActivityDoorAccess.class);
                intent.putExtra("access", ACCESS_CAR);
                startActivity(intent);
                dialog.dismiss();
            } else {
                showToastMessage("Please enter a correct PIN");
            }
        });

        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void getFavorites() {
        if (getLocation()) {
            String extraParams = "&industryID=" + "0" +
                    "&Company=" + "" +
                    "&sellerID=" + "0" +
                    "&mode=" + SearchRestaurantHelper.MODE_FAV +
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
                    "&del=" + "0";

            new SearchRestaurantHelper(ActivityMenuLocation.this, extraParams, new SearchRestaurantHelper.SearchRestaurantCallback() {
                @Override
                public void onFailed(String message) {
                    showAlert("You have no favorites selected! Be sure to mark your favorite stores when you shop!");
                }

                @Override
                public void onSuccess(ArrayList<Restaurant> restaurants, int mode) {
                    if (restaurants.isEmpty()) {
                        showAlert("You have no favorites selected! Be sure to mark your favorite stores when you shop!");
                        return;
                    }

                    Intent intent = new Intent(mContext, ActivityFavorite.class);
                    intent.putExtra("parent", "favorites");
                    intent.putExtra("restaurants", restaurants);
                    startActivity(intent);
                }
            }, SearchRestaurantHelper.MODE_FAV).execute();
        }
    }
}
