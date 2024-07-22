package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.MessageListAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

// TODO: Not called in the app (commented)
public class PushMsgActivity extends BaseActivity {
    public static final String TAG = PushMsgActivity.class.getSimpleName();
    /*
    const int PM=2359,Advertising = 2300, ZintaDirect = 2325, ZintaExportRequest = 2330, ZintaLoadRequest = 2335, Printing = 2338, Shipping = 2340, Video = 2355, Donation = 2360, Order = 2365, PayStub = 2375, DirectPay = 2377, Survey = 2380, Delivery = 2385, Registrations = 2399, Valet = 2430;
    */

    private MessageDataManager dm;

    private DrawerLayout mDrawerLayout;

    private Spinner spinnerContact;
    private ArrayList<ContactInfo> contactList = new ArrayList<>();
    ArrayAdapter contactAdapter;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private ImageView ivNewMsg;
    private EditText edtChatMessage;

    private static final long INTERVAL_GET_MSG = 15000;
    Handler handlerMsg = new Handler() {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);

            if (weakReference.get() == null)
                return;

            getMessages(false);
            sendEmptyMessageDelayed(0, INTERVAL_GET_MSG);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushmsg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.title_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        // tvParams = findViewById(R.id.tvParams);
        // tvFrom = findViewById(R.id.tvFrom);

        dm = new MessageDataManager(mContext);

        // Navigation Setup
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_contact);
        int menuItemSize = navigationView.getMenu().size();
        for (int i = 0; i < menuItemSize; i++) {
            navigationView.getMenu().getItem(i).setCheckable(false);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.nav_friend) {
                            startActivityForResult(new Intent(mContext, ActivityAddNewContact.class), 100);
                        }

                        menuItem.setChecked(false);

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
        findViewById(R.id.btnMsgNav).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        // Show Params and Values
        // tvParams.setText(String.format("Message : %s", message));
        // tvFrom.setText("From : " + title);

        spinnerContact = findViewById(R.id.spinnerContact);
        contactList = dm.getAlLContacts();
        contactAdapter = new ArrayAdapter<ContactInfo>(mContext, android.R.layout.simple_spinner_item, contactList);
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContact.setAdapter(contactAdapter);
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                messageList.clear();
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mMessageRecycler = (RecyclerView) findViewById(R.id.recycler_gchat);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        ivNewMsg = findViewById(R.id.ivNewMsg);
        ivNewMsg.setVisibility(View.GONE);
        ivNewMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMessageRecycler.scrollToPosition(messageList.size() - 1);
                ivNewMsg.setVisibility(View.GONE);
            }
        });

        edtChatMessage = findViewById(R.id.edit_gchat_message);
        findViewById(R.id.button_gchat_send).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtChatMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    sendMessages(message);
                }
            }
        });

        // Get Messages
        getMessages(true);
        handlerMsg.sendEmptyMessageDelayed(0, INTERVAL_GET_MSG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handlerMsg.removeMessages(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), ActivityHomeEvents.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        return true;
    }

    private void getMessages(boolean firstCall) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "getSMS" +
                            "&misc=" + appSettings.getUserId();
            baseUrl += extraParams;
            Log.e("Request", baseUrl);
            if (firstCall) {
                showProgressDialog();
            }

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (firstCall) {
                        hideProgressDialog();
                    }

                    Log.e("GetSMS", response);

                    int originalMsgs = messageList.size();
                    //messageList.clear();

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                //showToastMessage(jsonObject.getString("msg"));
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    Message newItem = new Message();
                                    newItem.setID(data.optInt("ID"));
                                    newItem.setStatusID(data.optString("StatusID"));
                                    newItem.setFromID(data.optInt("fromID"));
                                    newItem.setToID(data.optInt("toID"));
                                    newItem.setEmployerID(data.optInt("employerID"));
                                    newItem.setMsg(data.optString("Msg"));
                                    newItem.setCreateDate(data.optString("CreateDate"));
                                    newItem.setName(data.optString("name"));

                                    messageList.add(newItem);
                                }

                                mMessageAdapter.notifyDataSetChanged();
                                if (firstCall && messageList.size() > 0) {
                                    // Go to bottom in case of first call
                                    mMessageRecycler.scrollToPosition(messageList.size() - 1);
                                } else if (messageList.size() != originalMsgs) {
                                    // If new message exists, then notify user about new message
                                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) mMessageRecycler.getLayoutManager();
                                    // findFirstVisibleItemPosition()
                                    // findLastVisibleItemPosition()
                                    // findFirstCompletelyVisibleItemPosition()
                                    // findLastCompletelyVisibleItemPosition()
                                    int scrollPosition = myLayoutManager.findLastVisibleItemPosition();
                                    if (scrollPosition != messageList.size() - 1) {
                                        ivNewMsg.setVisibility(View.VISIBLE);
                                    } else {
                                        ivNewMsg.setVisibility(View.GONE);

                                        mMessageRecycler.scrollToPosition(messageList.size() - 1);
                                    }
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
                    if (firstCall) {
                        hideProgressDialog();
                    }

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

    private void sendMessages(final String msg) {

        if (contactList == null || contactList.isEmpty()) {
            showToastMessage("No Contact Info");
            return;
        }

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "addSMS",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String email = contactList.get(spinnerContact.getSelectedItemPosition()).getEmail();
            if (TextUtils.isEmpty(email)) {
                email = "";
            }
            String extraParams =
                    "&mode=" + "161" +
                            "&misc=" + appSettings.getUserId() +
                            "&employerID=" + appSettings.getEmpId() +
                            "&toMLID=" + contactList.get(spinnerContact.getSelectedItemPosition()).getMlid() +
                            "&Msg=" + msg +
                            "&amt=" + "0" +
                            "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                            "&email=" + email;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            // New Message
            Message newItem = new Message();
            newItem.setName(appSettings.getFN() + " " + appSettings.getLN());
            newItem.setMsg(msg);
            newItem.setStatusID("0");
            newItem.setFromID(appSettings.getUserId());
            newItem.setToID(contactList.get(spinnerContact.getSelectedItemPosition()).getMlid());
            newItem.setEmployerID(0);
            newItem.setCreateDate(DateUtil.toStringFormat_12(new Date()));
            messageList.add(newItem);
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.scrollToPosition(messageList.size() - 1);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("addSMS", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                getMessages(false);
                                edtChatMessage.setText("");

                                // Send push
                                sendPush(msg);
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

    private void sendPush(final String msg) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGetToken",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "0" +
                            "&TokenMLID=" + contactList.get(spinnerContact.getSelectedItemPosition()).getMlid();
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            // showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // hideProgressDialog();

                    Log.e("CJLGetToken", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                //showMsgAndGo2Home(jsonObject.getString("msg"));
                            } else {
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                // XiaoPOS
                                // tokenList.add(new FCMTokenData("f4RDiMTwSG8:APA91bEIPNcUk3oSjrzraQY4nf_Vc4xK0Pjvm8Ku2iSDYa6QNm1Xd2XvPw_08WD3ejBeBt80Qk9Y4Y5OC1PC4EzcYDFYOtpq-XBDob-MK0UObDsM9X1hXpLgEeq1xLyKVJIXxbAttL68", FCMTokenData.OS_UNKNOWN));
                                if (!tokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    payload.put("message", msg);
                                    payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                    payload.put("SenderID", appSettings.getUserId());
                                    // tokenGetter.sendPushNotification(mContext, tokenList, PayloadType.PT_Text_Message, payload);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100) {
            contactList.clear();
            contactList.addAll(dm.getAlLContacts());
            contactAdapter.notifyDataSetChanged();
        }
    }
}
