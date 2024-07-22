package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.adapters.OrderInfoAdapter;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

import java.util.ArrayList;

public class ActivityInvoice extends BaseActivity {
    public static final String TAG = ActivityInvoice.class.getSimpleName();
    String orderID;
    String dateTime;
    Restaurant restaurantInfo;
    ArrayList<MenuItem> menuList;
    String orderType;
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

    TextView tvNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoice);

        _activity = this;

        Intent data = getIntent();
        orderID = data.getStringExtra("orderid");
        dateTime = data.getStringExtra("datetime");
        restaurantInfo = data.getParcelableExtra("restaurant");
        menuList = data.getParcelableArrayListExtra("menus");
        orderType = data.getStringExtra("type");
        statusID = data.getIntExtra("statusID", 0);


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
        tvOrderId.setText(orderID);

        tvNote = findViewById(R.id.tvNote);
        if (statusID != 2000) {
            tvNote.setVisibility(View.GONE);
        }

        TextView tvStoreInfo = findViewById(R.id.tvStoreInfo);
        tvStoreInfo.setText(String.format("%d - %s", restaurantInfo.get_id(), restaurantInfo.get_name()));

        for (MenuItem item : menuList) {
            item.setSelected(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Receipt");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDateTime = findViewById(R.id.tvDateTime);
        tvDateTime.setText(dateTime);

        Button btnRateTips = (Button) findViewById(R.id.btnRateTips);
        btnRateTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateDlg();
            }
        });

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

        updatePrice();
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

    public void updatePrice() {
        int totalItems = 0;
        float subTotal = 0;
        float totalPrice = 0;
        float totalTax = 0;

        if (menuList != null && !menuList.isEmpty()) {
            for (MenuItem item : menuList) {

                if (totalItems == 0) {
                    // Only use one item

                    subTotal += Float.parseFloat(item.get_subTotal().replace("$", ""));
                    totalTax += Float.parseFloat(item.get_taxfees());
                    totalPrice += Float.parseFloat(item.get_totPrice().replace("$", ""));
                }
                int itemQty = item.get_quantity();
                totalItems += itemQty;
            }
        }

        txtItemCount.setText(String.format("Total Items: %d", totalItems));
        txtSubTotal.setText(String.format("Sub-Total: $%.2f", subTotal));
        txtTaxAndFees.setText(String.format("Tax and Fees: $%.2f", totalTax));
        txtTotal.setText(String.format("Total: $%.2f", totalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showRateDlg() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating_from_user, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Questions
        final RadioGroup groupOnTime = (RadioGroup) dialogView.findViewById(R.id.groupOnTime);
        final RadioGroup groupComplete = (RadioGroup) dialogView.findViewById(R.id.groupComplete);
        final RadioGroup groupAddFav = (RadioGroup) dialogView.findViewById(R.id.groupAddFav);

        // Ratings
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        final TextView tvErrorRate = (TextView) dialogView.findViewById(R.id.tvErrorRate);
        tvErrorRate.setVisibility(View.GONE);

        final EditText edtTips = (EditText) dialogView.findViewById(R.id.edtTips);

        // Button Actions
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDlg.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                inputDlg.dismiss();

                /*// Check current rating
                if (ratingBar.getRating() == 0) {
                    tvErrorRate.setVisibility(View.VISIBLE);
                    return;
                }

                float tipsValue = 0;
                try{
                    tipsValue = Float.parseFloat(edtTips.getText().toString());
                } catch(NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("rate", ratingBar.getRating());
                    jsonObject.put("tip", (int) tipsValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                inputDlg.dismiss();

                // Request Rating Api
                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);
                StringRequest sr = new StringRequest(Request.Method.GET, URLResolver.apiDelStatusRateDriv(jsonObject), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status");
                                if (jsonObject.getBoolean("status")) {
                                                *//*showAlert("Completed delivery! Good job.", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showFragment(FRAGMENT_SENDER_DELIVERY_MAP);
                                                    }
                                                });*//*
                                    showFragment(FRAGMENT_SENDER_DELIVERY_MAP);
                                } else {
                                    showToastMessage(jsonObject.getString("message"));
                                    showRelevantFragment();
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
                        showToastMessage(error.getMessage());

                        showRelevantFragment();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        params.put("Accept", "application/json");
                        return params;
                    }
                };
                queue.add(sr);*/
            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
