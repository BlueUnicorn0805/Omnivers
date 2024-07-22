package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.ContactAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.localdb.ContactsDataSource;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

// TODO: Not called in the app
public class ContactListActivity extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;

    RecyclerView rcvContactList;
    ArrayList<ContactInfo> contactInfoArrayList;
    ContactAdapter contactAdapter;

    EditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sendmsg);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Send Message");

        rcvContactList = (RecyclerView) findViewById(R.id.rcvContactList);
        rcvContactList.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));

        ContactsDataSource contactsDataSource = new ContactsDataSource(mContext);
        contactsDataSource.open();
        contactInfoArrayList = contactsDataSource.getAllUserInfo();
        contactsDataSource.close();

        Collections.sort(contactInfoArrayList, new Comparator<ContactInfo>() {
            public int compare(ContactInfo o1, ContactInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        contactAdapter = new ContactAdapter(mContext, contactInfoArrayList, new ContactAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }
        });
        rcvContactList.setAdapter(contactAdapter);

        edtMessage = findViewById(R.id.edtMessage);
        findViewById(R.id.btnSendMsg).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnSendMsg) {
            sendMessage();
        }
    }

    private void sendMessage() {

        // Check Contact Information
        ContactInfo contactInfo = contactAdapter.getSelectedItem();
        if (contactInfo == null) {
            showToastMessage("Pleaes select contact info");
            return;
        }

        // Check MLID
        if (contactInfo.getMlid() == 0) {
            showToastMessage("No Valid MLID");
            return;
        }

        // Check Message
        String message = edtMessage.getText().toString().trim();
        hideKeyboard(edtMessage);

        if (TextUtils.isEmpty(message)) {
            showToastMessage("Pleaes input message to send");
            return;
        }

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGetToken",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&TokenMLID=" + contactInfo.getMlid();
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

                    Log.e("Token", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                showToastMessage("No TOKENs");
                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage("No TOKENs");
                            } else {
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                if (!tokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    payload.put("message", message);
                                    payload.put("orderId", "1");
                                    payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                    payload.put("SenderID", appSettings.getUserId());
                                    // tokenGetter.sendPushNotification(mContext, tokenList, PayloadType.PT_Text_Message, payload);
                                }
                                showToastMessage("Sent Message!");
                                edtMessage.setText("");
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

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            sr.setShouldCache(false);
            queue.add(sr);
        }
    }
}
