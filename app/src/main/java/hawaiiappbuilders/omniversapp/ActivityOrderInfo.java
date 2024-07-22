package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.adapters.OrderInfoAdapter;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.PickupAndApptInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;

import java.util.ArrayList;

public class ActivityOrderInfo extends BaseActivity {
    public static final String TAG = ActivityOrderInfo.class.getSimpleName();
    String dateTime;
    Restaurant restaurantInfo;
    ArrayList<MenuItem> menuList;

    // Store Information
    TextView tvStoreName;
    TextView tvStoreInfo1;
    TextView tvStoreInfo2;
    TextView tvStoreInfo3;

    // Order Information
    TextView tvOrderDate;
    TextView tvOrderId;

    RecyclerView recyclerView;
    AppCompatActivity _activity;

    TextView txtItemCount;
    TextView txtSubTotal;
    TextView txtTaxAndFees;
    TextView txtTotal;

    PickupAndApptInfo pickupAndApptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_info);

        _activity = this;

        Intent data = getIntent();
        dateTime = data.getStringExtra("datetime");
        restaurantInfo = data.getParcelableExtra("restaurant");
        menuList = data.getParcelableArrayListExtra("menus");

        // Show Store Info
        tvStoreName = findViewById(R.id.storeName);
        tvStoreInfo1 = findViewById(R.id.storeInfo1);
        tvStoreInfo2 = findViewById(R.id.storeInfo2);
        tvStoreInfo3 = findViewById(R.id.storeInfo3);
        tvStoreName.setText(restaurantInfo.get_name());
        tvStoreInfo1.setText(restaurantInfo.get_address());
        tvStoreInfo2.setText(restaurantInfo.getStZipCity());
        //tvStoreInfo3.setText(restaurantInfo.getRes());

        // Show Order Information
        tvOrderDate = findViewById(R.id.orderDate);
        tvOrderId = findViewById(R.id.valueOrderId);
        tvOrderDate.setText(dateTime);
        tvOrderId.setText(String.valueOf(0000));

        TextView tvStoreInfo = findViewById(R.id.tvStoreInfo);
        tvStoreInfo.setText(String.format("%d - %s", restaurantInfo.get_id(), restaurantInfo.get_name()));

        for (MenuItem item : menuList) {
            item.setSelected(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Preview");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvDateTime = findViewById(R.id.tvDateTime);
        tvDateTime.setText(dateTime);

        Button btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalItems = 0;
                float totalPrice = 0;
                float totalTax = 0;

                if (menuList != null && !menuList.isEmpty()) {
                    for (MenuItem item : menuList) {
                        if (!item.isSelected())
                            continue;

                        String itemPrice = item.get_price();
                        float fItemPrice = 1;
                        try {
                            fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
                        } catch (Exception e) {
                        }

                        totalPrice += fItemPrice * 1/*item.get_quantity()*/;
                        totalTax += fItemPrice * item.get_taxable() * restaurantInfo.getTaxRate();
                        totalItems += 1/*item.get_quantity()*/;
                    }
                }

                if (totalPrice == 0 && restaurantInfo.get_industryID() != 487) {
                    showAlert("Please select service items");
                    return;
                }

                confirmPin(totalItems, totalPrice, totalTax);

                /*LayoutInflater inflater = getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
                        final TextView title = alertLayout.findViewById(R.id.dialog_title);
                        final TextView subTitle = alertLayout.findViewById(R.id.dialog_sub_title);
                        final EditText pin = alertLayout.findViewById(R.id.pin);
                        pin.setVisibility(View.GONE);
                        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
                        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
                        cpin.setVisibility(View.GONE);
                        grey_line.setVisibility(View.GONE);

                        title.setText(getString(R.string.estimated_price));
                        String priceInfo = String.format("Amount: $%.02f", totalPrice);
                        subTitle.setText(priceInfo);
                        subTitle.setVisibility(View.VISIBLE);

                        final Button submit = alertLayout.findViewById(R.id.pin_submit);
                        submit.setText("Ok");
                        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setView(alertLayout);
                        alert.setCancelable(false);

                        final AlertDialog confirmdialog = alert.create();
                        confirmdialog.show();
                final int finalTotalItems = totalItems;
                final float finalTotalPrice = totalPrice;
                submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmdialog.dismiss();
                                confirmPin(finalTotalItems, finalTotalPrice, totalTax);
                            }
                        });*/

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

        if (restaurantInfo != null && restaurantInfo.get_industryID() == 487) {
            Intent intent = new Intent(mContext, ActivityPickupInfo.class);
            intent.putExtra("res_info", restaurantInfo);
            intent.putExtra("datetime", dateTime);
            startActivityForResult(intent, 100);
        }
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

                    checkoutintent.putExtra("pickup_appt_info", pickupAndApptInfo);

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
        float totalPrice = 0;
        float totalTax = 0;

        if (menuList != null && !menuList.isEmpty()) {
            for (MenuItem item : menuList) {
                if (!item.isSelected())
                    continue;

                String itemPrice = item.get_price();
                float fItemPrice = 0;
                try {
                    fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
                } catch (Exception e) {
                }

                totalPrice += fItemPrice;
                totalTax += fItemPrice * item.get_taxable() * restaurantInfo.getTaxRate();
                totalItems += 1;
            }
        }

        txtItemCount.setText(String.format("Total Items: %d", totalItems));
        txtSubTotal.setText(String.format("Sub-Total: $%.2f", totalPrice));
        txtTaxAndFees.setText(String.format("Tax and Fees: $%.2f", totalTax));
        txtTotal.setText(String.format("Total: $%.2f", (totalPrice + totalTax)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            pickupAndApptInfo = data.getParcelableExtra("pickup_appt_info");
        } else {
            finish();
        }
    }
}
