package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.orders.OrderStatus.JustOrdered;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.PickupAndApptInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class CheckoutActivity extends BaseActivity {
    public static final String TAG = CheckoutActivity.class.getSimpleName();
    int totItems;
    float totPrice;
    float totTax;

    Restaurant restaurantInfo;
    ArrayList<MenuItem> menuItems;
    String dateTime;

    float fAvaBalance = 0;
    float fAvaSavings = 0;
    float fLoyalty = 0;
    float fGift = 0;
    float fBogo = 0;

    AppCompatActivity _activity;

    RadioButton radioPayWithAva;
    RadioButton radioPayWithLoyalty;
    RadioButton radioPayWithGiftCard;
    RadioButton radioPayWithBogo;
    RadioButton radioPayWithTab;
    EditText edtTabNum;

    RadioButton radioJoin;
    EditText edtOrderNum;

    Button btnSubmit;

    PickupAndApptInfo pickupAndApptInfo;

    private static final int REQUEST_TRANSFUNDS = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout);

        _activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Checkout");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent data = getIntent();

        totItems = data.getIntExtra("tot_items", 0);
        totPrice = data.getFloatExtra("tot_price", 0);
        totTax = data.getFloatExtra("tot_tax", 0);

        restaurantInfo = data.getParcelableExtra("restaurant");
        menuItems = data.getParcelableArrayListExtra("menus");
        dateTime = data.getStringExtra("datetime");

        pickupAndApptInfo = data.getParcelableExtra("pickup_appt_info");
        if (pickupAndApptInfo == null) {
            // If no data, then reset it
            pickupAndApptInfo = new PickupAndApptInfo();
        }

        TextView txtItemCount = findViewById(R.id.txtItemCount);
        TextView txtSubTotal = findViewById(R.id.txtSubTotal);
        TextView txtTaxAndFees = findViewById(R.id.txtTaxAndFees);
        TextView txtTotal = findViewById(R.id.txtTotal);

        txtItemCount.setText(String.format("Total Items: %d", totItems));
        txtSubTotal.setText(String.format("Sub-Total: $%.2f", totPrice));
        txtTaxAndFees.setText(String.format("Tax and Fees: $%.2f", totTax));
        txtTotal.setText(String.format("Total: $%.2f", (totPrice + totTax)));

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        updatePaymentChannels();

        btnSubmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioPayWithAva.isChecked()) {
                    if (false && fAvaBalance < (totPrice + totTax)) { // We always pass to ask Checkout
                        showAlert(R.string.msg_funds_not_enough, new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(R.string.app_name);
                                builder.setMessage(R.string.msg_trans_funds_now);
                                builder.setCancelable(false);
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }

                                });
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        // Charge Balance Here
                                        startActivityForResult(new Intent(mContext, ActivityTransIntro.class), REQUEST_TRANSFUNDS);
                                    }

                                });
                                builder.show();
                            }
                        });
                    } else {
                        confirmOrder();
                    }
                } else {
                    confirmOrder();
                }
            }
        });

        getAvaBalance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showError(String message) {
        showOkDialog(R.string.unsuccessful_order, message);
    }

    public void showSuccessCharge() {
        showOkDialog(R.string.successful_order_title, getString(R.string.successful_order_message));
    }

    public void showServerHostNotSet() {
        showOkDialog(R.string.server_host_not_set_title, Html.fromHtml(getString(R.string.server_host_not_set_message)));
    }

    private void showMerchantIdNotSet() {
        showOkDialog(R.string.merchant_id_not_set_title, Html.fromHtml(getString(R.string.merchant_id_not_set_message)));
    }

    private void showOkDialog(int titleResId, CharSequence message) {
        new AlertDialog.Builder(this)
                .setTitle(titleResId)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    // , R.style.Theme_AppCompat_Light_Dialog_Alert
    public void showNetworkErrorRetryPayment(Runnable retry) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_failure_title)
                .setMessage(getString(R.string.network_failure))
                .setPositiveButton(R.string.retry, (dialog, which) -> retry.run())
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // -----------------------------------------------------------------------------

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updatePaymentChannels() {
        radioPayWithAva = findViewById(R.id.rbZinta);
        radioPayWithLoyalty = findViewById(R.id.rbLoyal);
        radioPayWithGiftCard = findViewById(R.id.rbGift);
        radioPayWithBogo = findViewById(R.id.rbBogo);
        radioPayWithTab = findViewById(R.id.rbTab);
        edtTabNum = findViewById(R.id.edtTabNum);
        radioJoin = findViewById(R.id.rbJoin);
        edtOrderNum = findViewById(R.id.edtOrderNum);

        View.OnClickListener payOptionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                if (viewId != R.id.rbZinta) {
                    radioPayWithAva.setChecked(false);
                }
                if (viewId != R.id.rbLoyal) {
                    radioPayWithLoyalty.setChecked(false);
                }
                if (viewId != R.id.rbGift) {
                    radioPayWithGiftCard.setChecked(false);
                }
                if (viewId != R.id.rbBogo) {
                    radioPayWithBogo.setChecked(false);
                }
                if (viewId != R.id.rbTab) {
                    radioPayWithTab.setChecked(false);
                }
                if (viewId != R.id.rbJoin) {
                    radioJoin.setChecked(false);
                }
            }
        };

        radioPayWithAva.setOnClickListener(payOptionClickListener);
        radioPayWithLoyalty.setOnClickListener(payOptionClickListener);
        radioPayWithGiftCard.setOnClickListener(payOptionClickListener);
        radioPayWithBogo.setOnClickListener(payOptionClickListener);
        radioPayWithTab.setOnClickListener(payOptionClickListener);
        radioJoin.setOnClickListener(payOptionClickListener);
    }

    private void getAvaBalance() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "AllBal" +
                            "&misc=" + String.valueOf(restaurantInfo.get_id());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(_activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(_activity);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("avaBal", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                fAvaBalance = (float) jsonObject.getDouble("instaCash");
                                fAvaSavings = (float) jsonObject.getDouble("instaSavings");
                                fLoyalty = (float) jsonObject.getDouble("Loyalty");
                                fGift = (float) jsonObject.getDouble("Gift");
                                fBogo = (float) jsonObject.getDouble("BOGO");

                                //String.format("Pay with Zinta(Current Balance: $%.2f)"
                                radioPayWithAva.setText(getString(R.string.format_pay_with_zinta, fAvaBalance));
                                if (fAvaBalance < totPrice + totTax) {
                                    radioPayWithAva.setEnabled(false);
                                }

                                radioPayWithLoyalty.setText(getString(R.string.format_pay_with_loyalty, fLoyalty));
                                if (fLoyalty < totPrice + totTax) {
                                    radioPayWithLoyalty.setEnabled(false);
                                }

                                radioPayWithGiftCard.setText(getString(R.string.format_pay_with_gift, fGift));
                                if (fGift < totPrice + totTax) {
                                    radioPayWithGiftCard.setEnabled(false);
                                }

                                radioPayWithBogo.setText(getString(R.string.format_pay_with_bogo, (int) fBogo));
                                if (fBogo < totPrice + totTax) {
                                    radioPayWithGiftCard.setEnabled(false);
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

    public void hideSoftKeyboard() {
        InputMethodManager keyboard =
                (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (_activity != null && _activity.getCurrentFocus() != null) {
            keyboard.hideSoftInputFromInputMethod(_activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void confirmOrder() {
        //Uncomment the below code to Set the message and title from the strings.xml file
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Please confirm purchase").setMessage("Ready to commit all Checked Functions?");

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        createOrder();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createOrder() {

        if (getLocation()) {
            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("serviceusedid", 2199);
                jsonObject.put("promoid", "0");

                // Convert dateTime To Universal Format
                //jsonObject.put("OrderDueAt", DateUtil.toStringFormat_17(new Date()));  // "7-1-2019 12:01"

                String orderDueDate = "";

                Date oderDueAt = new Date();
                if (!TextUtils.isEmpty(dateTime)) {
                    oderDueAt = DateUtil.parseDataFromFormat19(dateTime);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(oderDueAt);
                calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                orderDueDate = DateUtil.toStringFormat_13(calendar.getTime());
                jsonObject.put("orderdueat", DateUtil.toStringFormat_17(calendar.getTime()));     // Aug 21 11:15 AM,
                jsonObject.put("industryID", restaurantInfo.get_industryID());

                if (radioPayWithLoyalty.isChecked()) {
                    jsonObject.put("paidwithid", "71");
                    jsonObject.put("nickid", "71");
                } else if (radioPayWithGiftCard.isChecked()) {
                    jsonObject.put("paidwithid", "72");
                    jsonObject.put("nickid", "72");
                } else if (radioPayWithBogo.isChecked()) {
                    jsonObject.put("paidwithid", "73");
                    jsonObject.put("nickid", "73");
                } else if (radioPayWithTab.isChecked()) {
                    jsonObject.put("paidwithid", "74");
                    jsonObject.put("nickid", "74");
                } else {
                    jsonObject.put("paidwithid", "70");
                    jsonObject.put("nickid", "70");
                }
                jsonObject.put("totship", "0");
                jsonObject.put("totlabor", "0");

                /*jsonObject.put("orname", "");
                jsonObject.put("oraddr", "");
                jsonObject.put("orph", "");
                jsonObject.put("delname", "");
                jsonObject.put("deladdr", "");
                jsonObject.put("delzip", "");
                jsonObject.put("delph", "");
                jsonObject.put("deldir", "");*/

                jsonObject.put("sellerid", restaurantInfo.get_id());

                jsonObject.put("sellerid", restaurantInfo.get_id());
                jsonObject.put("buyerid", appSettings.getUserId());
                //double fTotPrice = Float.parseFloat(String.format("%.2f", totPrice));
                //double fTotTax = Float.parseFloat(String.format("%.2f", totTax));
                jsonObject.put("totprice", String.format("%.2f", totPrice));
                jsonObject.put("tottax", String.format("%.2f", totTax));
                JSONArray menuItemsArray = new JSONArray();
                for (int i = 0; i < menuItems.size(); i++) {
                    MenuItem item = menuItems.get(i);
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("prodid", item.get_id());
                    itemObj.put("name", item.get_name());
                    itemObj.put("des", ""/*item.get_description()*/); // Make it as blank to send more data.

                    String price = item.get_price();
                    price = price.replace("$", "");
                    float fPrice = 0f;
                    try {
                        fPrice = Float.parseFloat(price);
                    } catch (Exception e) {
                    }

                    itemObj.put("price", price);
                    itemObj.put("size", item.get_size());
                    itemObj.put("quantity", 1/*item.get_quantity()*/);

                    itemObj.put("oz", "0");
                    itemObj.put("gram", "0");

                    menuItemsArray.put(i, itemObj);
                }
                jsonObject.put("menus", menuItemsArray);
                jsonObject.put("serviceusedid", "126");
                jsonObject.put("tableid", "0");
                jsonObject.put("totprice", String.format("%.2f", totPrice + totTax));
                jsonObject.put("tottax", String.format("%.2f", totTax));
                jsonObject.put("totfee", String.format("%.2f", 0.0));
                jsonObject.put("delfee", String.format("%.2f", 0.0));
                jsonObject.put("paynow", true);
                //jsonObject.put("token", appSettings.getDeviceToken());

                jsonObject.put("CP", pickupAndApptInfo.puPhone);
                jsonObject.put("Email", appSettings.getEmail());
                jsonObject.put("DOB", appSettings.getDOB());
                jsonObject.put("countryCode", appSettings.getCountryCode());
                jsonObject.put("FN", appSettings.getFN());
                jsonObject.put("LN", appSettings.getLN());
                jsonObject.put("Nick", appSettings.getCompany());

                jsonObject.put("puCo", pickupAndApptInfo.puCompany);
                jsonObject.put("pustNum", pickupAndApptInfo.puStreetNum);
                jsonObject.put("puStreet", pickupAndApptInfo.puStreet);
                jsonObject.put("puSte", pickupAndApptInfo.puSuite);
                jsonObject.put("puCity", pickupAndApptInfo.puCity);
                jsonObject.put("puSt", pickupAndApptInfo.puState);
                jsonObject.put("puZip", pickupAndApptInfo.puZip);
                jsonObject.put("puNote", pickupAndApptInfo.puNote);
                jsonObject.put("puLonTrip", getUserLon());
                jsonObject.put("puLatTrip", getUserLat());
                jsonObject.put("oneWay", pickupAndApptInfo.isOneWayTrip ? 1 : 0);
                jsonObject.put("puTime", pickupAndApptInfo.puDate + " " + pickupAndApptInfo.puTime);

                jsonObject.put("apptCo", pickupAndApptInfo.company);
                jsonObject.put("apptstNum", pickupAndApptInfo.streetNum);
                jsonObject.put("apptStreet", pickupAndApptInfo.street);
                jsonObject.put("apptSte", pickupAndApptInfo.suite);
                jsonObject.put("apptCity", pickupAndApptInfo.city);
                jsonObject.put("apptSt", pickupAndApptInfo.state);
                jsonObject.put("apptZip", pickupAndApptInfo.zip);
                jsonObject.put("apptNote", pickupAndApptInfo.note);
                jsonObject.put("apptLonTrip", getUserLon());
                jsonObject.put("apptLatTrip", getUserLat());
                jsonObject.put("vehID", pickupAndApptInfo.puVehicleTypeID);

                jsonObject.put("apptTime", pickupAndApptInfo.date + " " + pickupAndApptInfo.time);

//                jsonObject.put("weight", appSettings.getWeight());
//                jsonObject.put("height", appSettings.getHeight());

                String repeat = pickupAndApptInfo.repeat;
                if (repeat.length() < 7) {
                    repeat = "0000000";
                }
                jsonObject.put("M", repeat.substring(0, 1).equals("1") ? 1 : 0);
                jsonObject.put("T", repeat.substring(1, 2).equals("1") ? 1 : 0);
                jsonObject.put("W", repeat.substring(2, 3).equals("1") ? 1 : 0);
                jsonObject.put("TH", repeat.substring(3, 4).equals("1") ? 1 : 0);
                jsonObject.put("F", repeat.substring(4, 5).equals("1") ? 1 : 0);
                jsonObject.put("S", repeat.substring(5, 6).equals("1") ? 1 : 0);
                jsonObject.put("SU", repeat.substring(6).equals("1") ? 1 : 0);


                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "AddOrder", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                Log.e("CreateOrder", baseUrl);
                // Try to Creat Order
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
                                    final String orderId = jsonObject.getString("OrderID");

                                    appSettings.setOrderID(orderId);
                                    appSettings.setOrderDueDate(finalOrderDueDate);

                                    //addTrip(orderId);
                                    getToken(orderId);
                                    // showSuccess();
                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    if (TextUtils.isEmpty(jsonObject.optString("msg"))) {
                                        showAlert("Order not entered. Please try again.");
                                    } else {
                                        showAlert(jsonObject.getString("msg"));
                                    }
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
                queue.add(sr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTrip(String orderID) {
        if (pickupAndApptInfo != null) {
            if (getLocation()) {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "addTrip",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&orderID=" + orderID +
                                "&LonPU=" + getUserLon() +
                                "&LatPU=" + getUserLat() +
                                "&LonAppt=" + getUserLon() +
                                "&LatAppt=" + getUserLat() +
                                "&puTime=" + pickupAndApptInfo.puDate + " " + pickupAndApptInfo.puTime +
                                "&apptTime=" + pickupAndApptInfo.date + " " + pickupAndApptInfo.time +
                                "&vehicleID=" + pickupAndApptInfo.puVehicleTypeID +
                                "&stNumPU=" + pickupAndApptInfo.puStreetNum +
                                "&StreetPU=" + pickupAndApptInfo.puStreet +
                                "&StePU=" + pickupAndApptInfo.puSuite +
                                "&CityPU=" + pickupAndApptInfo.puCity +
                                "&StPU=" + pickupAndApptInfo.puState +
                                "&ZipPU=" + pickupAndApptInfo.puZip +
                                "&CPPU=" + pickupAndApptInfo.puPhone +
                                "&NotePU=" + pickupAndApptInfo.puNote +
                                "&oneway=" + (pickupAndApptInfo.isOneWayTrip ? "1" : "0") +

                                "&stNum=" + pickupAndApptInfo.streetNum +
                                "&Street=" + pickupAndApptInfo.street +
                                "&Ste=" + pickupAndApptInfo.suite +
                                "&City=" + pickupAndApptInfo.city +
                                "&St=" + pickupAndApptInfo.state +
                                "&Zip=" + pickupAndApptInfo.zip +
                                "&CP=" + pickupAndApptInfo.phone +
                                "&Note=" + pickupAndApptInfo.note +

                                "&SMTWTFS=" + pickupAndApptInfo.repeat +
                                "&Medicaid=" + appSettings.getMedicaid() +
                                "&Medicare=" + appSettings.getMedicare();

                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(_activity);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(_activity);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("addTrip", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                } else {
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

                        //showMessage(error.getMessage());
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
    }

    private void getToken(final String orderId) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGetToken",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "storeowner" +
                            "&TokenMLID=" + String.valueOf(restaurantInfo.get_id());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(_activity);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(_activity);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("CJLGetToken", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                showSuccess();
                                return;
                            }
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                //showMsgAndGo2Home(jsonObject.getString("msg"));
                                showSuccess();
                            } else {
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                tokenList.add(new FCMTokenData("ccvEqnK2ejY:APA91bFisMzXQSHONIG7JQx-Fusaw2ohf2khstSs0FDLmtqCnjXmV950tSYf_U8zXXOnYEsxieR0n7nR-kAIY126_0GSKiEXQv6HtuzGrh8TKn_98oLVuUM2eRsTapEd9VKT_QGUplL9", FCMTokenData.OS_UNKNOWN));
                                tokenList.add(new FCMTokenData("dKhtRmxzCd4:APA91bGhyIbvvQkCjBhklwk4MMDLpblYRieFJEVl8cqQjy5YNS0bdWxBAGbic8KAio74K9-robiaPIvy_hzXGYDjaJT9MbykU-u1Rt-pdGQy2F5_8ALkk5wVtsNxH0r6RGuBo6bh-V9J", FCMTokenData.OS_UNKNOWN));
                                JSONObject payload = new JSONObject();
                                payload.put("title", "New Order Request");
                                payload.put("message", String.format("You got new Order(%s) request from user", orderId));
                                payload.put("orderId", orderId);
                                payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                payload.put("SenderID", appSettings.getUserId());
                                payload.put("statusID", JustOrdered.statusId);
                                notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Order_Status, payload);
                                showSuccess();
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
                        showAlert(R.string.error_conn_error);
                    } else {
                        showAlert(error.getMessage());
                    }

                    //showMessage(error.getMessage());
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

    private void showSuccess() {
        showSuccessDialog(mContext, "Request Sent", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ActivityIFareDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showMsgAndGo2Home(String msg) {
        showAlert(msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityIFareDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
