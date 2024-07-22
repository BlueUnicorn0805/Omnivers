package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
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

import hawaiiappbuilders.omniversapp.adapters.CartItemAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.model.SideDish;

import java.util.ArrayList;

public class CartListActivity extends BaseActivity {

    Restaurant restaurant;
    ArrayList<MenuItem> menuList;
    int tableID;

    RecyclerView recyclerView;
    AppCompatActivity _activity;

    TextView txtItemCount;
    TextView txtSubTotal;
    TextView txtTaxAndFees;
    TextView txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cart_list);

        _activity = this;

        Intent data = getIntent();
        restaurant = data.getParcelableExtra("restaurant");
        menuList = data.getParcelableArrayListExtra("menus");
        tableID = data.getIntExtra("table_id", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Transaction");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPIN();
            }
        });

        /*ArrayList<CartItem> cartList;
        cartList = new ArrayList<CartItem>();
        cartList.add(new CartItem(1,"Steak 1", "$15.99", "This is a description of the Menu Item that will make people want to purchase this item",1,15.99, false));
        cartList.add(new CartItem(2,"The best steak west of the Mississippi", "$35.99", "This is a description of the Menu Item that will make people want to purchase this item", 2,35.99, true));*/

        ArrayList<SideDish> sides = new ArrayList<>();
        sides.add(new SideDish(1, "Mashed Potatos", 0));
        sides.add(new SideDish(1, "Cottage Cheese", 0));
        sides.add(new SideDish(1, "Fries", 0));
        sides.add(new SideDish(1, "House Salad", 0));
        sides.add(new SideDish(1, "Baked Potato", 0));
        sides.add(new SideDish(1, "Loaded Baked Potato", 2));
        sides.add(new SideDish(1, "Ceaser Salad", 1));
        sides.add(new SideDish(1, "Soup of the Day", 0));

        recyclerView = (RecyclerView) findViewById(R.id.rcvMenuList);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        CartItemAdapter adapter = new CartItemAdapter(_activity, menuList, sides);
        recyclerView.setAdapter(adapter);

        txtItemCount = findViewById(R.id.txtItemCount);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtTaxAndFees = findViewById(R.id.txtTaxAndFees);
        txtTotal = findViewById(R.id.txtTotal);

        updatePrice();
    }

    public void updatePrice() {
        int totalItems = 0;
        float totalFoodPrice = 0;
        float totalTax = 0;

        if (menuList != null && !menuList.isEmpty()) {
            for (MenuItem item : menuList) {
                String itemPrice = item.get_price();
                float fItemPrice = 1;
                try {
                    fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
                } catch (Exception e) {
                }

                totalFoodPrice += fItemPrice * item.get_quantity();
                totalItems += item.get_quantity();
                totalTax += fItemPrice * item.get_quantity() * restaurant.getTaxRate() * item.get_taxable();
            }
        }

        // Show Item and Price information
        txtItemCount.setText(String.format("Total Items: %d", totalItems));
        txtSubTotal.setText(String.format("Sub-Total: $%.2f", totalFoodPrice));
        txtTaxAndFees.setText(String.format("Tax and Fees: $%.2f", totalTax));
        txtTotal.setText(String.format("Total: $%.2f", (totalFoodPrice + totalTax)));
    }

    private void checkPIN() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("PIN is required to make purchase");
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

                    int totalItems = 0;
                    float totalFoodPrice = 0;
                    float totalTax = 0;

                    if (menuList != null && !menuList.isEmpty()) {
                        for (MenuItem item : menuList) {
                            String itemPrice = item.get_price();
                            float fItemPrice = 1;
                            try {
                                fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
                            } catch (Exception e) {
                            }

                            totalFoodPrice += fItemPrice * item.get_quantity();
                            totalItems += item.get_quantity();
                            totalTax += fItemPrice * item.get_quantity() * restaurant.getTaxRate() * item.get_taxable();
                        }
                    }

                    if (totalItems == 0) {
                        showToastMessage("No items to checkout");
                        return;
                    }

                    Intent checkoutintent = new Intent(_activity, FoodCheckoutActivity.class);
                    checkoutintent.putExtra("restaurant", restaurant);
                    checkoutintent.putExtra("menus", menuList);
                    checkoutintent.putExtra("tot_items", totalItems);
                    checkoutintent.putExtra("tot_food_price", totalFoodPrice);
                    checkoutintent.putExtra("tot_tax", totalTax);

                    checkoutintent.putExtra("table_id", tableID);

                    startActivity(checkoutintent);
                } else {
                    showToastMessage("Wrong PIN");
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
