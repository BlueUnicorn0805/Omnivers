package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.GroupAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class OrderConfirmationActivity extends BaseActivity {

    MessageDataManager dm;

    Restaurant restaurant;
    String orderID;
    int totItems;
    float totPrice;
    float totTax;

    ArrayList<MenuItem> menuItems;

    String orderType;

    long orderDueAt = 0;

    AppCompatActivity _activity;

    TextView tvOrderNumber;
    TextView tvOrderTotal;

    TextView tvOrderTItle;
    TextView tvOrderDetails;

    RecyclerView rcvGroups;
    ArrayList<GroupInfo> groupInfos = new ArrayList<>();
    GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderconfirmation);

        dm = new MessageDataManager(mContext);

        _activity = this;

        Intent data = getIntent();
        restaurant = data.getParcelableExtra("restaurant");
        orderID = data.getStringExtra("order_number");
        totItems = data.getIntExtra("tot_items", 0);
        totPrice = data.getFloatExtra("tot_price", 0);
        totTax = data.getFloatExtra("tot_tax", 0);
        menuItems = data.getParcelableArrayListExtra("menus");
        orderDueAt = data.getLongExtra("date", 0);
        orderType = data.getStringExtra("type");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Confirm Order");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        tvOrderNumber = findViewById(R.id.tvGroupTitle);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvOrderNumber.setText(String.format("Transaction Number: %s", orderID));
        tvOrderTotal.setText(String.format("Transaction Total: $%.2f", totPrice + totTax));

        // Show Order Summary
        tvOrderTItle = findViewById(R.id.textView14);
        tvOrderTItle.setText(restaurant.get_name());

        // Show Details
        tvOrderDetails = findViewById(R.id.textView15);
        String summary = "";
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).get_quantity() > 0) {
                summary += String.format("%s - %d\n", menuItems.get(i).get_name(), menuItems.get(i).get_quantity());
            }
        }
        tvOrderDetails.setText(summary.trim());

        Button btnInviteFriend = (Button) findViewById(R.id.btnInviteFriend);
        Button btnInvoice = (Button) findViewById(R.id.btnSeeInvoice);
        Button btnOCDone = (Button) findViewById(R.id.btnOCDone);

        btnInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent invite = new Intent(_activity, InviteActivity.class);
                Intent invite = new Intent(_activity, ShareLocationActivity.class);
                invite.putExtra("restaurant", restaurant);
                startActivity(invite);
            }

        });

        btnInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityInvoice.class);
                intent.putExtra("orderid", orderID);
                intent.putExtra("datetime", DateUtil.toStringFormat_7(new Date()));
                intent.putExtra("restaurant", restaurant);
                intent.putExtra("menus", menuItems);
                intent.putExtra("type", orderType);

                startActivity(intent);
            }
        });
        btnInvoice.setVisibility(View.GONE);

        btnOCDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivityHomeMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }

        });

        findViewById(R.id.btnAddGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityManageGroups.class);
                startActivity(intent);
            }
        });

        // Groups
        rcvGroups = findViewById(R.id.rcvGroups);
        rcvGroups.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcvGroups.setHasFixedSize(true);
        groupAdapter = new GroupAdapter(mContext, groupInfos, new GroupAdapter.GroupItemListener() {
            @Override
            public void onItemClicked(int position) {
                final GroupInfo groupInfo = groupInfos.get(position);
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.title_invite)
                        .setMessage(R.string.prompt_invite)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            dialog.dismiss();

                            inviteGroup(groupInfo);
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.cancel();
                        })
                        .create()
                        .show();
            }
        });
        rcvGroups.setAdapter(groupAdapter);
    }

    private void inviteGroup(GroupInfo groupInfo) {
        ArrayList<ContactInfo> groupContacts = dm.getContacts(groupInfo.getId());
        if (!groupContacts.isEmpty()) {

            Intent intent = new Intent(mContext, ActivityInviteGroupDetails.class);
            intent.putExtra("group_info", groupInfo);
            intent.putExtra("contact_info", groupContacts);
            intent.putExtra("restaurant", restaurant);
            intent.putExtra("date", orderDueAt);
            startActivity(intent);

            if (true) {
                return;
            }

            // Keep the following code, but currently not used.
            String csvEmails = "";

            String FNs = "";
            String LNs = "";
            for (ContactInfo contact : groupContacts) {
                if (TextUtils.isEmpty(csvEmails)) {
                    csvEmails = "" + contact.getEmail();
                    FNs = "" + contact.getFname();
                    LNs = "" + contact.getLname();
                } else {
                    csvEmails = csvEmails + "," + contact.getEmail();
                    FNs = FNs + "," + contact.getFname();
                    LNs = LNs + "," + contact.getLname();
                }
            }

            if (getLocation()) {

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "inviteGroup",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "0" +
                                "&userID=" + appSettings.getUserId() +
                                "&CSVemails=" + csvEmails +
                                "&FN=" + appSettings.getFN() +
                                "&LN=" + appSettings.getLN() +
                                "&orderID=" + orderID;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("inviteGroup", response);

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                // Refresh Data

                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject responseStatus = jsonArray.getJSONObject(0);

                                if (responseStatus.has("msg")) {
                                    showToastMessage(responseStatus.getString("msg"));
                                }
                                //showToastMessage(R.string.msg_invite_success);
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
        } else {
            showToastMessage(R.string.msg_no_contact_in_group);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        groupInfos.clear();

        groupInfos.addAll(dm.getAlLGroups());
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
