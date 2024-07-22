package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityGroupsAddContact extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityGroupsAddContact.class.getSimpleName();
    MessageDataManager dm;
    GroupInfo groupInfo;

    ContactInfo contactInfo;

    EditText tvCoFamily;
    EditText tvName;
    EditText tvEmail;

    Button btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addcontact);
        Intent intent = getIntent();

        groupInfo = intent.getParcelableExtra("group_info");
        contactInfo = intent.getParcelableExtra("contact_info");

        // Check Data
        if (groupInfo == null) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dm = new MessageDataManager(this);

        tvCoFamily = findViewById(R.id.tvCoFamily);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        findViewById(R.id.btnCancel).setOnClickListener(this);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        Button btnUpdate = findViewById(R.id.btnUpdate);

        if (contactInfo != null) {
            tvCoFamily.setText(contactInfo.getCo());
            tvName.setText(contactInfo.getName());
            tvEmail.setText(contactInfo.getEmail());
        } else {
            btnUpdate.setText("Add");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnCancel) {
            finish();
        } else if (viewId == R.id.btnUpdate) {
            String family = tvCoFamily.getText().toString().trim();
            String name = tvName.getText().toString().trim();
            String email = tvEmail.getText().toString().trim();

            if (!isValidEmail(email)) {
                showToastMessage(this, "Please input valid email");
                return;
            }

            if (contactInfo != null) {
                boolean needsUpdateOnline = email.equals(contactInfo.getEmail());

                contactInfo.setName(name);
                contactInfo.setEmail(email);
                contactInfo.setCo(family);
                contactInfo.setGroupInfo("|" + groupInfo.getId() + "|");
                dm.updateContact(contactInfo);

                showToastMessage(this, "Updated contact info");

                if (needsUpdateOnline) {
                    importContact(contactInfo, false);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            } else {
                ContactInfo newContact = new ContactInfo();
                newContact.setName(name);
                newContact.setEmail(email);
                newContact.setCo(family);
                newContact.setGroupInfo("|" + groupInfo.getId() + "|");
                dm.addContact(newContact);

                showToastMessage(this, "Added new contact info");

                importContact(newContact, true);
            }
        }
    }

    private void importContact(ContactInfo contactInfo, boolean isAdd) {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "importContacts",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "0" +
                            "&email=" + contactInfo.getEmail() +
                            "&LDBMLID=" + String.valueOf(contactInfo.getMlid()) +
                            "&LDBID=" + String.valueOf(contactInfo.getId());
            baseUrl += extraParams;
            Log.e("Request", baseUrl);


            RequestQueue queue = Volley.newRequestQueue(this);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(this);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // hideProgressDialog();

                    Log.e("importContacts", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            // Refresh Data

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject responseStatus = jsonArray.getJSONObject(0);

                            if (responseStatus.has("MLID")) {
                                int fMLID = responseStatus.getInt("MLID");
                                int ldbID = 0;
                                try {
                                    ldbID = fMLID;
                                } catch (Exception e) {e.printStackTrace();}

                                contactInfo.setMlid(fMLID);
                                dm.updateContact(contactInfo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }
                    }

                    setResult(RESULT_OK);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // hideProgressDialog();

                    setResult(RESULT_OK);
                    finish();
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
