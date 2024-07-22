package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

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
import hawaiiappbuilders.omniversapp.location.Constants;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressIntentService;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressResultReceiver;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class NewDeliveryActivity extends BaseActivity implements View.OnClickListener, GeocodeAddressResultReceiver.OnReceiveGeocodeListener {
    public static final String TAG = NewDeliveryActivity.class.getSimpleName();
    Handler addressUpdateHandler;
    GeocodeAddressResultReceiver mResultReceiver;

    double fromLatitude = 0;
    double fromLongitude = 0;
    double toLatitude = 0;
    double toLongitude = 0;

    // Favorite Deliveries
    ArrayList<FCMTokenData> favDriversTokenList = new ArrayList<>();

    EditText edtPhoneFrom;
    EditText edtNameFrom;
    EditText edtAddressFrom;
    EditText edtApartmentFrom;
    EditText edtFloorFrom;
    EditText edtCityStateZipFrom;

    EditText edtPhoneTo;
    EditText edtNameTo;
    EditText edtAddressTo;
    EditText edtApartmentTo;
    EditText edtFloorTo;
    EditText edtCityStateZipTo;

    EditText edtInstructions;

    EditText edtPackageSize;
    EditText edtTotalWeight;
    EditText edtQTY;

    CheckBox radioNone;
    CheckBox radioCold;
    CheckBox radioHot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdelivery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (toolbar != null) {
            actionBar.setTitle("Request New Pickup");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        Intent dataIntent = getIntent();
        favDriversTokenList = dataIntent.getParcelableExtra("fav_tokens");

        mResultReceiver = new GeocodeAddressResultReceiver(null, this);
        addressUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (msg.what == 0) {
                    String strFromAddr = edtAddressFrom.getText().toString().trim();
                    String strFromCSZ = edtCityStateZipFrom.getText().toString().trim();

                    if (!TextUtils.isEmpty(strFromAddr) && !TextUtils.isEmpty(strFromCSZ)) {
                        Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
                        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, strFromAddr + " " + strFromCSZ);
                        intent.putExtra(Constants.REQUEST_CODE, 0);
                        startService(intent);
                    }
                } else if (msg.what == 1) {
                    String strToAddr = edtAddressTo.getText().toString().trim();
                    String strToCSZ = edtCityStateZipTo.getText().toString().trim();
                    if (!TextUtils.isEmpty(strToAddr) && !TextUtils.isEmpty(strToCSZ)) {

                        // Try to get Location
                        showProgressDialog();

                        // Reset and get new geo location
                        toLatitude = 0;
                        toLongitude = 0;

                        Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
                        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, strToAddr + " " + strToCSZ);
                        intent.putExtra(Constants.REQUEST_CODE, 1);
                        startService(intent);
                    }
                }
            }
        };

        edtPhoneFrom = (EditText) findViewById(R.id.edtPhoneFrom);
        edtNameFrom = (EditText) findViewById(R.id.edtNameFrom);
        edtAddressFrom = (EditText) findViewById(R.id.edtAddressFrom);
        edtAddressFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // addressUpdateHandler.removeMessages(0);
                // addressUpdateHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });

        edtApartmentFrom = (EditText) findViewById(R.id.edtApartmentFrom);
        edtFloorFrom = (EditText) findViewById(R.id.edtFloorFrom);
        edtCityStateZipFrom = (EditText) findViewById(R.id.edtCityStateZipFrom);
        edtCityStateZipFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // addressUpdateHandler.removeMessages(0);
                // addressUpdateHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });

        edtPhoneTo = (EditText) findViewById(R.id.edtPhoneTo);
        edtNameTo = (EditText) findViewById(R.id.edtNameTo);
        edtAddressTo = (EditText) findViewById(R.id.edtAddressTo);
        edtAddressTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addressUpdateHandler.removeMessages(1);
                addressUpdateHandler.sendEmptyMessageDelayed(1, 2500);
            }
        });

        edtApartmentTo = (EditText) findViewById(R.id.edtApartmentTo);
        edtFloorTo = (EditText) findViewById(R.id.edtFloorTo);
        edtCityStateZipTo = (EditText) findViewById(R.id.edtCityStateZipTo);
        edtCityStateZipTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addressUpdateHandler.removeMessages(1);
                addressUpdateHandler.sendEmptyMessageDelayed(1, 2500);
            }
        });


        // Instructions
        edtInstructions = (EditText) findViewById(R.id.edtInstructions);

        // Package Information
        edtPackageSize = (EditText) findViewById(R.id.edtPackageSize);
        edtTotalWeight = (EditText) findViewById(R.id.edtTotalWeight);
        edtQTY = (EditText) findViewById(R.id.edtQTY);

        // Set Login info first
        edtPhoneFrom.setText(appSettings.getCP());
        edtNameFrom.setText(appSettings.getFN() + " " + appSettings.getLN());
        edtAddressFrom.setText(appSettings.getStreetNum() + " " + appSettings.getStreet());
        edtApartmentFrom.setText(appSettings.getApt());
        edtFloorFrom.setText(appSettings.getALev());
        edtCityStateZipFrom.setText(String.format("%s, %s %s", appSettings.getCity(), appSettings.getSt(), appSettings.getZip()).trim());

        // Set saved values
        String deliveryInformationString = appSettings.getDeliveryInfo();
        try {
            JSONObject jsonDelivery = new JSONObject(deliveryInformationString);

            edtPhoneFrom.setText(jsonDelivery.getString("PhoneFrom"));
            edtNameFrom.setText(jsonDelivery.getString("NameFrom"));
            edtAddressFrom.setText(jsonDelivery.getString("AddressFrom"));
            edtApartmentFrom.setText(jsonDelivery.getString("ApartmentFrom"));
            edtFloorFrom.setText(jsonDelivery.getString("FloorFrom"));
            edtCityStateZipFrom.setText(jsonDelivery.getString("CityStateZipFrom"));

            edtPhoneTo.setText(jsonDelivery.getString("PhoneTo"));
            edtNameTo.setText(jsonDelivery.getString("NameTo"));
            edtAddressTo.setText(jsonDelivery.getString("AddressTo"));
            edtApartmentTo.setText(jsonDelivery.getString("ApartmentTo"));
            edtFloorTo.setText(jsonDelivery.getString("FloorTo"));
            edtCityStateZipTo.setText(jsonDelivery.getString("CityStateZipTo"));

        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        // Package Type None, Cold and Hot
        radioNone = findViewById(R.id.radioNone);
        radioCold = findViewById(R.id.radioCold);
        radioHot = findViewById(R.id.radioHot);

        // Make New Delivery Button
        findViewById(R.id.btnOk).setOnClickListener(this);

        getFavTokens();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnOk) {
            makeNewDel();
        }
    }

    private void getFavTokens() {
        //favDriversTokenList
        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "GetDriverFavs";
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

                    Log.e("CJLGet", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            String status = jsonObject.getString("status");
                            if (jsonObject.getBoolean("status")) {

                            } else {
                                // showAlert(jsonObject.getString("msg"));
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

    private void makeNewDel() {

        hideKeyboard(edtPhoneFrom);
        hideKeyboard(edtNameFrom);
        hideKeyboard(edtAddressFrom);
        hideKeyboard(edtApartmentFrom);
        hideKeyboard(edtFloorFrom);
        hideKeyboard(edtCityStateZipFrom);

        hideKeyboard(edtPhoneTo);
        hideKeyboard(edtNameTo);
        hideKeyboard(edtAddressTo);
        hideKeyboard(edtApartmentTo);
        hideKeyboard(edtFloorTo);
        hideKeyboard(edtCityStateZipTo);

        hideKeyboard(edtInstructions);
        hideKeyboard(edtPackageSize);
        hideKeyboard(edtTotalWeight);
        hideKeyboard(edtQTY);

        final String phoneFrom = edtPhoneFrom.getText().toString().trim();
        final String nameFrom = edtNameFrom.getText().toString().trim();
        final String addressFrom = edtAddressFrom.getText().toString().trim();
        final String apartmentFrom = edtApartmentFrom.getText().toString().trim();
        final String floorFrom = edtFloorFrom.getText().toString().trim();
        final String cityStateZipFrom = edtCityStateZipFrom.getText().toString().trim();

        final String phoneTo = edtPhoneTo.getText().toString().trim();
        final String nameTo = edtNameTo.getText().toString().trim();
        final String addressTo = edtAddressTo.getText().toString().trim();
        final String apartmentTo = edtApartmentTo.getText().toString().trim();
        final String floorTo = edtFloorTo.getText().toString().trim();
        final String cityStateZipTo = edtCityStateZipTo.getText().toString().trim();

        final String instructions = edtInstructions.getText().toString().trim();
        final String packageSize = edtPackageSize.getText().toString().trim();
        final String weight = edtTotalWeight.getText().toString().trim();
        final String qty = edtQTY.getText().toString().trim();

        // Check Information From:
        if (TextUtils.isEmpty(phoneFrom)) {
            edtPhoneFrom.setError("Invalid input");
            edtPhoneFrom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nameFrom)) {
            edtNameFrom.setError("Invalid input");
            edtNameFrom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(addressFrom)) {
            edtAddressFrom.setError("Invalid input");
            edtAddressFrom.requestFocus();
            return;
        }

        /*if (fromLatitude == -1 || fromLongitude == -1) {
            edtAddressFrom.setError("Invalid Address");
            edtAddressFrom.requestFocus();
            return;
        }*/

        if (TextUtils.isEmpty(cityStateZipFrom)) {
            edtCityStateZipFrom.setError("Invalid input");
            edtCityStateZipFrom.requestFocus();
            return;
        }

        // Check Information To:
        if (TextUtils.isEmpty(phoneTo)) {
            edtPhoneTo.setError("Invalid input");
            edtPhoneTo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nameTo)) {
            edtNameTo.setError("Invalid input");
            edtNameTo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(addressTo)) {
            edtAddressTo.setError("Invalid input");
            edtAddressTo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cityStateZipTo)) {
            edtCityStateZipTo.setError("Invalid input");
            edtCityStateZipTo.requestFocus();
            return;
        }

        if (toLatitude == 0 || toLongitude == 0) {
            edtAddressTo.setError("Invalid Address");
            edtAddressTo.requestFocus();
            return;
        }

        int valuePackageSize = 0;
        try {
            valuePackageSize = Integer.parseInt(packageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (valuePackageSize <= 0) {
            edtPackageSize.setError("Invalid input");
            edtPackageSize.requestFocus();
            return;
        }

        int valueWeight = 0;
        try {
            valueWeight = Integer.parseInt(weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (valueWeight <= 0) {
            edtTotalWeight.setError("Invalid input");
            edtTotalWeight.requestFocus();
            return;
        }

        int valueQTY = 0;
        try {
            valueQTY = Integer.parseInt(qty);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (valueQTY <= 0) {
            edtQTY.setError("Invalid input");
            edtQTY.requestFocus();
            return;
        }

        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "DelAdd",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String none = radioNone.isChecked() ? "1" : "0";
            String hot = radioHot.isChecked() ? "1" : "0";
            String cold = radioCold.isChecked() ? "1" : "0";

            String dueTime = DateUtil.toStringFormat_7(new Date());
            String extraParams =
                    "&tMLID=" + "0" +
                            "&DeliveryDue=" + dueTime +
                            "&MaxLocalTime=" + dueTime +
                            "&currTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&capabilitiesid=" + "44" +
                            "&statusid=" + "0" +
                            "&tolon=" + String.valueOf(toLongitude) +
                            "&tolat=" + String.valueOf(toLatitude) +
                            "&paksize=" + packageSize +
                            "&pakwgt=" + weight +
                            "&instruct=" + instructions +
                            "&qty=" + qty +
                            "&fph=" + phoneFrom +
                            "&fname=" + nameFrom +
                            "&fadd=" + addressFrom +
                            "&fapt=" + apartmentFrom +
                            "&ffloor=" + floorFrom +
                            "&fcsz=" + cityStateZipFrom +
                            "&tph=" + phoneTo +
                            "&tname=" + nameTo +
                            "&tadd=" + addressTo +
                            "&tapt=" + apartmentTo +
                            "&tfloor=" + floorTo +
                            "&tcsz=" + cityStateZipTo +
                            "&none=" + none +
                            "&hot=" + hot +
                            "&cold=" + cold +
                            "&sellerID=" + "0" +
                            "&orderID=" + "0" +
                            "&toserv=" + "0" +
                            "&storeDelFee=" + "0";
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

                    Log.e("DelAdd", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            showToastMessage("Deliver has been requested");

                            String delID = jsonObject.getString("msg").replace("DelID: ", "").trim();
                            int newdelId = 0;
                            try {
                                newdelId = Integer.parseInt(delID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            appSettings.setNewDelID(newdelId);

                            // Get new Del ID
                            // String newDeliveryId = jsonObject.getString("NewID=");

                            // Send push to all Favorite driver devices
                            if (favDriversTokenList != null &&
                                    !favDriversTokenList.isEmpty()) {
                                String curUserName = String.format("%s %s", appSettings.getFN(), appSettings.getLN()).trim();
                                String title = String.format("%s invited you for new delivery", curUserName);
                                String addressInfo = String.format("From %s to %s", addressFrom, addressTo);
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                if (!favDriversTokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    payload.put("title", title);
                                    payload.put("message", addressInfo);
                                    payload.put("SenderName", curUserName);
                                    payload.put("SenderID", appSettings.getUserId());
                                    notificationHelper.sendPushNotification(mContext, favDriversTokenList, PayloadType.PT_Text_Message, payload);
                                }
                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showAlert(jsonObject.getString("msg"));
                        }
                    } catch (JSONException e) {
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

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            sr.setShouldCache(false);
            queue.add(sr);
        }

        // Save the information for next use
        // Set saved values
        try {
            JSONObject jsonDelivery = new JSONObject();

            jsonDelivery.put("PhoneFrom", phoneFrom);
            jsonDelivery.put("NameFrom", nameFrom);
            jsonDelivery.put("AddressFrom", addressFrom);
            jsonDelivery.put("ApartmentFrom", apartmentFrom);
            jsonDelivery.put("FloorFrom", floorFrom);
            jsonDelivery.put("CityStateZipFrom", cityStateZipFrom);

            jsonDelivery.put("PhoneTo", phoneTo);
            jsonDelivery.put("NameTo", nameTo);
            jsonDelivery.put("AddressTo", addressTo);
            jsonDelivery.put("ApartmentTo", apartmentTo);
            jsonDelivery.put("FloorTo", floorTo);
            jsonDelivery.put("CityStateZipTo", cityStateZipTo);

            appSettings.setDeliveryInfo(jsonDelivery.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        // This call back is not in the UI thread.

        hideProgressDialog();

        if (resultCode == Constants.SUCCESS_RESULT) {
            Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
            int requestCode = resultData.getInt(Constants.REQUEST_CODE);
            if (requestCode == 0) {
                fromLatitude = address.getLatitude();
                fromLongitude = address.getLongitude();

                Log.e("From", String.format("%f, %f", fromLatitude, fromLongitude));
            } else {
                toLatitude = address.getLatitude();
                toLongitude = address.getLongitude();

                Log.e("To", String.format("%f, %f", toLatitude, toLongitude));
            }
        } else {
            showToastMessage("Couldn't get geo location for destination");
        }
    }
}
