package hawaiiappbuilders.omniversapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.GroupAdapter;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityInviteGroup extends BaseActivity {
    public static final String TAG = ActivityInviteGroup.class.getSimpleName();
    MessageDataManager dm;

    RecyclerView rcvGroups;
    ArrayList<GroupInfo> groupInfos = new ArrayList<>();
    GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_group);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Group Invites");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        rcvGroups = findViewById(R.id.rcvGroups);

        findViewById(R.id.btnBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        dm = new MessageDataManager(mContext);

        setupGroups();

        findViewById(R.id.btnAddGroup).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityManageGroups.class);
                startActivity(intent);
            }
        });
    }

    public void setupGroups() {
        // Groups
        rcvGroups.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcvGroups.setHasFixedSize(true);
        groupAdapter = new GroupAdapter(mContext, groupInfos, new GroupAdapter.GroupItemListener() {
            @Override
            public void onItemClicked(int position) {
                final GroupInfo groupInfo = groupInfos.get(position);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_group_actions, null);
                final android.app.AlertDialog actionsDialog = new android.app.AlertDialog.Builder(mContext)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();
                dialogView.findViewById(R.id.btnInviteGroup).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionsDialog.dismiss();
                        inviteGroupWithJson(groupInfo);
                    }
                });
                dialogView.findViewById(R.id.btnEditGroupName).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionsDialog.dismiss();
                        editGroupName(groupInfo);
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actionsDialog.cancel();
                    }
                });
                actionsDialog.show();
                actionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });
        rcvGroups.setAdapter(groupAdapter);
    }

    private void editGroupName(GroupInfo groupInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AppSettings appSettings = new AppSettings(mContext);
        builder.setTitle("Edit Group Name");
        // Set up the input
        final EditText input = new EditText(mContext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if(!groupInfo.getGrpname().isEmpty()) {
            input.setText(groupInfo.getGrpname());
            input.setSelection(input.length());
        }
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText() != null && !input.getText().toString().isEmpty()) {
                    groupInfo.setGrpname(input.getText().toString().trim());
                    dm.updateGroup(groupInfo);
                    refreshGroupList();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGroupList();
    }

    private void refreshGroupList() {
        groupInfos.clear();
        groupInfos.addAll(dm.getAlLUserGroups());
        groupAdapter.notifyDataSetChanged();
    }

    private void inviteGroup(GroupInfo groupInfo) {
        ArrayList<ContactInfo> groupContacts = dm.getContacts(groupInfo.getId());
        if (!groupContacts.isEmpty()) {
            StringBuilder csvEmails = new StringBuilder();
            StringBuilder FNs = new StringBuilder();
            StringBuilder LNs = new StringBuilder();
            for (ContactInfo contact : groupContacts) {
                if (TextUtils.isEmpty(csvEmails)) {
                    csvEmails.append("");
                    csvEmails.append(contact.getEmail());

                    FNs.append("");
                    FNs.append(contact.getFname());

                    LNs.append("");
                    LNs.append(contact.getLname());
                } else {
                    csvEmails.append(csvEmails);
                    csvEmails.append(",");
                    csvEmails.append(contact.getEmail());

                    FNs.append(FNs);
                    FNs.append(",");
                    FNs.append(contact.getFname());

                    LNs.append(LNs);
                    LNs.append(",");
                    LNs.append(contact.getLname());
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
                                "&CSVemails=" + csvEmails.toString() +
                                "&FN=" + appSettings.getFN() +
                                "&LN=" + appSettings.getLN() +
                                "&orderID=" + "0";
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
        } else {
            showToastMessage(R.string.msg_no_contact_in_group);
        }
    }

    private void inviteGroupWithJson(GroupInfo groupInfo) {

        // Check Current Order - irrelevant? commented it for now
       /* String strOrderID = appSettings.getOrderID();
        int orderID = 0;
        try {
            orderID = Integer.parseInt(strOrderID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (orderID <= 1) {
            msg("Please place the order first.");
            return;
        }*/

        ArrayList<ContactInfo> groupContacts = dm.getContacts(groupInfo.getId());
        if (!groupContacts.isEmpty()) {

            Intent intent = new Intent(mContext, ActivityInviteGroupDetails.class);
            intent.putExtra("group_info", groupInfo);
            intent.putExtra("contact_info", groupContacts);
            startActivity(intent);

            if (true) {
                return;
            }

            // Keep the following code, but currently not used.
            String csvEmails = "";
            if (getLocation()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mode", "0");

                   // jsonObject.put("userID", appSettings.getUserId());


                    JSONArray arrayEmails = new JSONArray();
                    for (ContactInfo contact : groupContacts) {
                        JSONObject jsonContact = new JSONObject();

                        jsonContact.put("FN", contact.getFname());
                        jsonContact.put("LN", contact.getLname());
                        jsonContact.put("Email", contact.getEmail());

                        arrayEmails.put(jsonContact);
                    }

                    jsonObject.put("CSVemails", arrayEmails);
                    jsonObject.put("FN", appSettings.getFN());
                    jsonObject.put("LN", appSettings.getLN());
                    jsonObject.put("GName", groupInfo.getGrpname());


                    jsonObject.put("orderID", "0");


                    String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                            "inviteGroup", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);

                    StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
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
                                        msg(responseStatus.getString("msg"));
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
                            baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
                        }
                    });

                    sr.setRetryPolicy(new DefaultRetryPolicy(
                            25000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    sr.setShouldCache(false);
                    queue.add(sr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showToastMessage(R.string.msg_no_contact_in_group);
        }
    }
}
