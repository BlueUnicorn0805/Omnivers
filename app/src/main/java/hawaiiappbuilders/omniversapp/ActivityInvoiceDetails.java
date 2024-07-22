package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.OrderInfoAdapter;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityInvoiceDetails extends BaseActivity {
    public static final String TAG = ActivityInvoiceDetails.class.getSimpleName();
    long orderId;
    String dateTime;
    Restaurant restaurantInfo;
    ArrayList<MenuItem> menuList;
    String orderType;
    int VenderMLID;
    int statusID;

    // Store Information
    TextView tvStoreName;
    TextView tvStoreInfo1;
    TextView tvStoreInfo2;
    TextView tvStoreInfo3;

    TextView tvOrderType;

    // Order Information
    TextView tvOrderDate;
    TextView tvOrderId;

    RecyclerView recyclerView;
    AppCompatActivity _activity;

    TextView txtItemCount;
    TextView txtSubTotal;
    TextView txtTaxAndFees;
    TextView txtTotal;

    int totalItems = 0;
    float totalPrice = 0;
    float totalTax = 0;
    float subTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoicedetails);
        _activity = this;
        orderId = getIntent().getExtras().getLong("orderId");
        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getInvoiceDetails(orderId);
    }


    private void getInvoiceDetails(long orderId) {
        HashMap<String, String> params = new HashMap<>();
        KTXApplication mMyApp = (KTXApplication) getApplication();
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "OrderDetailsByOrderID" +
                        "&misc=" + orderId;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        showProgressDialog();
        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(mContext);

        String finalBaseUrl = baseUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Log.e("OrderDetailsByOrderID", response);
                if (response != null || !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                        ArrayList<MenuItem> menuList = new ArrayList<>();

                        String orderDueAt = "";
                        int VenderMLID = 0;
                        int statusID = 0;

                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            showToastMessage(jsonObject.getString("msg"));
                        } else {
                            // Get Restaurant information from First Order Info
                            Restaurant resInfo = new Restaurant();
                            resInfo.set_address("");
                            resInfo.set_name("");
                            resInfo.set_city("");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObj = jsonArray.getJSONObject(i);
                                if (dataObj.has("address")) {
                                    resInfo.set_address(dataObj.getString("address"));
                                } else {
                                    resInfo.set_address("No Address");
                                }

                                if (dataObj.has("Co")) {
                                    resInfo.set_name(dataObj.getString("Co"));
                                } else {
                                    resInfo.set_name("No Name");
                                }

                                if (dataObj.has("CSZ")) {
                                    resInfo.set_csz(dataObj.getString("CSZ"));
                                } else {
                                    resInfo.set_csz("No CSZ");
                                }

                                if (dataObj.has("OrderID")) {
                                    resInfo.set_id(dataObj.getInt("OrderID"));
                                } else {
                                    resInfo.set_id(Integer.parseInt(String.valueOf(orderId)));
                                }

                                if (dataObj.has("TimePlaced")) {
                                    orderDueAt = dataObj.getString("TimePlaced");
                                } else {
                                    orderDueAt = "No time";
                                }
                                VenderMLID = dataObj.optInt("VenderMLID");
                                statusID = dataObj.optInt("statusID");

                                MenuItem newMenuItem = new MenuItem();
                                newMenuItem.set_name(dataObj.getString("Name"));
                                newMenuItem.set_description(dataObj.getString("Des"));
                                newMenuItem.set_price(String.format("$%.2f", dataObj.getDouble("Price")));
                                newMenuItem.set_size(dataObj.getString("Size"));
                                newMenuItem.set_quantity(dataObj.getInt("Qty"));
                                newMenuItem.set_taxfees(dataObj.getString("TaxFees"));
                                newMenuItem.set_lineTot(dataObj.getString("LineTot"));
                                newMenuItem.set_totPrice(dataObj.getString("TotPrice"));
                                newMenuItem.set_subTotal(dataObj.getString("SubTotal"));

                                menuList.add(newMenuItem);
                            }

                            displayInvoiceDetails(resInfo, orderId, orderDueAt, menuList, VenderMLID, statusID);
                          /*  Intent invoiceIntent = new Intent(mContext, ActivityInvoiceDetails.class);
                            invoiceIntent.putExtra("restaurant", resInfo);
                            invoiceIntent.putExtra("orderid", orderID);
                            invoiceIntent.putExtra("datetime", orderDueAt);
                            invoiceIntent.putExtra("menus", menuList);
                            invoiceIntent.putExtra("VenderMLID", VenderMLID);
                            invoiceIntent.putExtra("statusID", statusID);*/

                            // activity.startActivity(invoiceIntent);
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

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    private void displayInvoiceDetails(Restaurant restaurantInfo, long orderId, String dateTime, ArrayList<MenuItem> menuList, int VenderMLID, int statusID) {
        //  orderType = data.getStringExtra("type");
        try {
            dateTime = DateUtil.toStringFormat_19(DateUtil.parseDataFromFormat20(dateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show Store Info
        tvStoreName = findViewById(R.id.storeName);
        tvStoreInfo1 = findViewById(R.id.storeInfo1);
        tvStoreInfo2 = findViewById(R.id.storeInfo2);
        tvStoreInfo3 = findViewById(R.id.storeInfo3);
        if (restaurantInfo != null) {
            tvStoreName.setText(restaurantInfo.get_name());
            tvStoreInfo1.setText(String.valueOf(restaurantInfo.get_address()));
            tvStoreInfo2.setText(restaurantInfo.get_csz());
        } else {
            tvStoreName.setText("");
            tvStoreInfo1.setText("");
            tvStoreInfo2.setText("");
        }

        tvStoreInfo3.setText("");

        tvOrderType = findViewById(R.id.tvOrderType);
        if (TextUtils.isEmpty(orderType)) {
            tvOrderType.setVisibility(View.GONE);
        } else {
            tvOrderType.setText(String.format("Type: %s", orderType));
        }

        // Show Order Information
        tvOrderDate = findViewById(R.id.orderDate);
        tvOrderId = findViewById(R.id.valueOrderId);
        tvOrderDate.setText(dateTime);
        tvOrderId.setText(String.valueOf(orderId));

        TextView tvStoreInfo = findViewById(R.id.tvStoreInfo);
        tvStoreInfo.setText(String.format("%d - %s", restaurantInfo.get_id(), restaurantInfo.get_name()));

        for (MenuItem item : menuList) {
            item.setSelected(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Invoice");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDateTime = findViewById(R.id.tvDateTime);
        tvDateTime.setText(dateTime);



        Button btnPayNow = (Button) findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPinDlg();
            }
        });

        btnPayNow.setVisibility(View.GONE);
        /*if(appSettings.getUserId() == VenderMLID) {
            btnPayNow.setVisibility(View.VISIBLE);
        }*/
        if (statusID >= 4500 && statusID < 4550 && VenderMLID != appSettings.getUserId()) { // New Invoice
            btnPayNow.setVisibility(View.VISIBLE);
        }

        if (statusID < 2160) {
            btnPayNow.setVisibility(View.VISIBLE);
        }

        /*ArrayList<CartItem> cartList;
        cartList = new ArrayList<CartItem>();
        cartList.add(new CartItem(1,"Steak 1", "$15.99", "This is a description of the Menu Item that will make people want to purchase this item",1,15.99, false));
        cartList.add(new CartItem(2,"The best steak west of the Mississippi", "$35.99", "This is a description of the Menu Item that will make people want to purchase this item", 2,35.99, true));*/

        recyclerView = (RecyclerView) findViewById(R.id.rcvMenuList);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        OrderInfoAdapter adapter = new OrderInfoAdapter(_activity, menuList);
        recyclerView.setAdapter(adapter);

        txtItemCount = findViewById(R.id.txtItemCount);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtTaxAndFees = findViewById(R.id.txtTaxAndFees);
        txtTotal = findViewById(R.id.txtTotal);

        updatePrice(menuList);
    }


    private boolean confirmPin(final int totalItems, final float totalPrice, final float totalTax) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("Book This Appointment\nPurchase PIN required");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        final Button cancel = alertLayout.findViewById(R.id.pin_cancel);
        submit.setText("Purchase Now");

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alert.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumber = pin.getText().toString().trim();
                boolean pinTrue = false;
                hideKeyboard();
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    pinTrue = false;
                } else {
                    pinTrue = true;
                }

                AppSettings appSettings = new AppSettings(mContext);
                String userPin = appSettings.getPIN().trim();
                if (pinTrue && appSettings.getPIN().trim().equalsIgnoreCase(pinNumber)) {
                    dialog.dismiss();

                    Intent checkoutintent = new Intent(_activity, CheckoutActivity.class);
                    checkoutintent.putExtra("menus", menuList);
                    checkoutintent.putExtra("restaurant", restaurantInfo);
                    checkoutintent.putExtra("datetime", dateTime);
                    checkoutintent.putExtra("tot_items", totalItems);
                    checkoutintent.putExtra("tot_price", totalPrice);
                    checkoutintent.putExtra("tot_tax", totalTax);

                    startActivity(checkoutintent);

                    /*showSuccessDialog(mContext, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            new android.support.v7.app.AlertDialog.Builder(mContext)
                                    .setTitle("Appointment")
                                    .setMessage("You will receive confirmation of Appointment.")
                                    .setCancelable(false)
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            // payMoney();
                                            Intent intent = new Intent(mContext, ActivityDashBoard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setIcon(R.drawable.splash_logo)
                                    .show();
                        }
                    });*/
                } else {
                    /*pin.setError("Wrong PIN");
                    pinTrue = false;
                    dialog.dismiss();
                    Intent intent = new Intent(mContext,ActivityDashBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();*/

                    showMessage(mContext, "Wrong PIN");
                    dialog.dismiss();
                }
            }
        });
        return false;
    }

    public void updatePrice(ArrayList<MenuItem> menuList) {

        double subtotal = 0.0;
        if (menuList != null && !menuList.isEmpty()) {
            for (MenuItem item : menuList) {

                if (totalItems == 0) {
                    // Only use one item
                    subtotal += Double.parseDouble(String.valueOf(item.get_quantity())) * Double.parseDouble(item.get_price().replace("$", ""));
                   /* subTotal = Float.parseFloat(item.get_subTotal().replace("$", ""));
                    totalTax = Float.parseFloat(item.get_taxfees());
                    totalPrice = Float.parseFloat(item.get_totPrice().replace("$", ""));*/
                    // todo: calculate tax for each item?

                }
                // int itemQty = item.get_quantity();

            }
        }

        // todo: calculate fees
        double totalFees = 0;
        // todo: calculate total tax
        double taxAndFees = totalTax + totalFees;
        double finalTotalPrice = subtotal + taxAndFees;

        totalItems = menuList.size();
        txtItemCount.setText(String.format("Total Items: %d", totalItems));
        txtSubTotal.setText(String.format("Sub-Total: $%.2f", subtotal));
        txtTaxAndFees.setText(String.format("Tax and Fees: $%.2f", taxAndFees));
        txtTotal.setText(String.format("Total: $%.2f", finalTotalPrice));
        totalPrice = Float.parseFloat(String.valueOf(finalTotalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                String pinNumber = pin.getText().toString().trim();
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

    private void payNow() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "PayInvoice",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "getSMS" +
                            "&Amt=" + String.valueOf(totalPrice) +
                            "&totPrice=" + String.valueOf(totalPrice) +
                            "&OrderID=" + orderId +
                            "&fCOA=" + String.valueOf(0) +
                            "&tCOA=" + String.valueOf(0) +
                            "&invoiceID=" + orderId +
                            "&moveOutOfBlockChain=" + "false" +
                            "&Note=" + "" +
                            "&VenderMLID=" + String.valueOf(VenderMLID) +
                            "&SupplierMLID=" + String.valueOf(VenderMLID) +
                            "&ourOrderID=" + orderId +
                            "&aLev=" + String.valueOf(0);
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

                    Log.e("PayInvoice", response);
                    Intent resultIntent = new Intent();

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                resultIntent.putExtra("status", true);
                                resultIntent.putExtra("msg", "Payment success!");
                            } else {
                                resultIntent.putExtra("status", false);
                                resultIntent.putExtra("msg", jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resultIntent.putExtra("status", false);
                            resultIntent.putExtra("msg", "An exception occurred");
                            //showAlert(e.getMessage());
                        }
                    }

                    setResult(RESULT_OK, resultIntent);
                    finish();
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
}
