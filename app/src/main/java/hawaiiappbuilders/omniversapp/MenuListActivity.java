package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import hawaiiappbuilders.omniversapp.adapters.RecyclerMenuGroupAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuHeader;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.model.SideDish;

import java.util.ArrayList;

public class MenuListActivity extends BaseActivity {

    Restaurant restaurant;

    ArrayList<MenuHeader> menuHeaderList;
    RecyclerView recyclerView;

    TextView tvPriceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_list);

        Intent data = getIntent();
        restaurant = data.getParcelableExtra("restaurant");

        menuHeaderList = data.getParcelableArrayListExtra("headermenus");

        if (restaurant == null) {
            finish();
            return;
        }

        if (menuHeaderList == null || menuHeaderList.isEmpty()) {
            finish();
            return;
        }
        menuHeaderList.add(0, new MenuHeader(restaurant.getLink(), "img"));

        Button btnToCheckout = (Button) findViewById(R.id.btnToCheckout);
        btnToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (appSettings.getUserId() == 0) {

                    showToastMessage("Must be Registered to proceed.");
                    return;
                }

                ArrayList<MenuItem> menuItems = new ArrayList<>();
                for (MenuHeader menuHeader : menuHeaderList) {
                    ArrayList<MenuItem> menuList = menuHeader.getMenuList();
                    if (menuList != null) {
                        for (MenuItem item : menuList) {
                            if (item.get_quantity() > 0) {
                                menuItems.add(item);
                            }
                        }
                    }
                }

                if (menuItems.isEmpty()) {
                    showAlert("Not taking orders at this time.");
                } else {
                    Intent cartIntent = new Intent(getBaseContext(), CartListActivity.class);
                    cartIntent.putExtra("menus", menuItems);
                    cartIntent.putExtra("restaurant", restaurant);
                    startActivity(cartIntent);
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (restaurant != null) {
            toolbar.setTitle(/*"Restaurant X Menu"*/restaurant.get_name());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*menuHeaderList = new ArrayList<>();
        menuHeaderList.add(new MenuHeader("RestImage", "img"));
        menuHeaderList.add(new MenuHeader("Starter","group"));
        menuHeaderList.add(new MenuHeader("Entree", "group"));
        menuHeaderList.add(new MenuHeader("Desert", "group"));

        menuList = new ArrayList<>();
        menuList.add(new MenuItem(1,"Cheese Sticks", "$7.99", "This is a description of the Menu Item that will make people want to purchase this item","Starter", false, R.drawable.aptizer1));
        menuList.add(new MenuItem(2,"Onion Rings", "$8.99", "This is a description of the Menu Item that will make people want to purchase this item","Starter", false, R.drawable.aptizer2));

        menuList.add(new MenuItem(3,"Fillet", "$15.99", "This is a description of the Menu Item that will make people want to purchase this item","Entree", true, R.drawable.fillet1));
        menuList.add(new MenuItem(4,"The best steak west of the Mississippi", "$35.99", "This is a description of the Menu Item that will make people want to purchase this item","Entree", true, R.drawable.fillet2));
        *//*menuList.add(new MenuItem(5,"Another great steak", "$22.50", "This is a description of the Menu Item that will make people want to purchase this item","Entree", true));
        menuList.add(new MenuItem(6,"Holy Cow! A steak!", "$2.15", "This is a description of the Menu Item that will make people want to purchase this item","Entree", true));
        menuList.add(new MenuItem(7,"Yum Yum!", "$75.00", "This is a description of the Menu Item that will make people want to purchase this item","Entree", true));*//*

        menuList.add(new MenuItem(8,"Apple Pie", "$4.99", "This is a description of the Menu Item that will make people want to purchase this item","Desert", false, R.drawable.dessert1));
        menuList.add(new MenuItem(9,"Brownie", "$3.99", "This is a description of the Menu Item that will make people want to purchase this item","Desert", false, R.drawable.dessert2));*/

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        RecyclerMenuGroupAdapter adapter = new RecyclerMenuGroupAdapter(this, menuHeaderList);
        //MenuItemAdapter adapter = new MenuItemAdapter(this, menuList, menuHeaderList, sides);
        recyclerView.setAdapter(adapter);

        // Price Information
        tvPriceInfo = findViewById(R.id.tvPriceInfo);

        updatePrice();
    }

    public void updatePrice() {
        int totalItems = 0;
        float totalPrice = 0;

        for (MenuHeader menuHeader : menuHeaderList) {
            ArrayList<MenuItem> menuList = menuHeader.getMenuList();
            if (menuList != null) {
                for (MenuItem item : menuList) {
                    String itemPrice = item.get_price();
                    float fItemPrice = 1;
                    try {
                        fItemPrice = Float.parseFloat(itemPrice.replace("$", ""));
                    } catch (Exception e) {}

                    totalPrice += fItemPrice * item.get_quantity();
                    totalItems += item.get_quantity();
                }
            }
        }

        String formatString = getString(R.string.menu_total_price);
        String priceInfoString = String.format(formatString, totalItems, totalPrice);

        tvPriceInfo.setText(priceInfoString);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
