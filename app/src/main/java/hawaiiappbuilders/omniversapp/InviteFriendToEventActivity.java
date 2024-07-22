package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.CustomContactList;
import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.Videos;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class InviteFriendToEventActivity extends BaseActivity {
    public static final String TAG = InviteFriendToEventActivity.class.getSimpleName();
    Context mContext;
    EditText edtEmail;

    // for Contacts List
    MessageDataManager dm;
    CustomContactList contactsListAdapter;
    CustomContactModel selectedContact;
    ArrayList<CustomContactModel> contactModels;
    private ArrayList<ContactInfo> contactList = new ArrayList<>();
    Videos bodyVideoData;
    Spinner spinnerContact;

    TextView textEventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_to_event);
        mContext = this;
        dm = new MessageDataManager(mContext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Invite A Friend");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        edtEmail = findViewById(R.id.edtEmail);
        spinnerContact = findViewById(R.id.spinnerContact);
        textEventDetails = findViewById(R.id.text_event_details);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            bodyVideoData = extras.getParcelable("event");
            textEventDetails.setText(HtmlCompat.fromHtml("<b>" + bodyVideoData.getTitle() + "</b><br>" +
                    bodyVideoData.getHeadLine() + "<br><br>" +
                    bodyVideoData.getDescript() + "<br><br><tt>" +
                    bodyVideoData.getAddressFULL() + "</tt>", HtmlCompat.FROM_HTML_MODE_COMPACT));

        }

        getContactsList();

        findViewById(R.id.btnInvite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "";
                hideKeyboard(edtEmail);
                if (spinnerContact.getSelectedItemPosition() > 0) {
                    ContactInfo contactInfo = dm.getContactInfoById(contactsListAdapter.getItem(spinnerContact.getSelectedItemPosition()).id);
                    email = contactInfo.getEmail();
                } else {
                    if (edtEmail.getText().toString().isEmpty()) {
                        showToastMessage("Please enter an email address");
                    } else {
                        email = edtEmail.getText().toString().trim();
                    }
                }
                if (!isEmailValid(email)) {
                    showAlert("Please input valid email");
                    return;
                }
                inviteFriends(email);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getContactsList() {
        contactList = dm.getAlLContacts(0);
        contactModels = new ArrayList<>();

        // add to a new email address
        CustomContactModel firstModel = new CustomContactModel();
        firstModel.setName("Enter an email address");
        contactModels.add(firstModel);

        // populate
        for (int i = 0; i < contactList.size(); i++) {
            CustomContactModel model = new CustomContactModel();
            model.isSelected = false;
            model.id = contactList.get(i).getId();
            model.type = 1;
            if (contactList.get(i).getCo() != null) {
                model.company = contactList.get(i).getCo();
            } else {
                model.company = "";
            }
            if (contactList.get(i).getName() != null) {
                model.name = contactList.get(i).getName();
            } else {
                model.name = "";
            }
            if (contactList.get(i).getFname() != null) {
                model.fname = contactList.get(i).getFname();
            } else {
                model.fname = "";
            }

            if (contactList.get(i).getLname() != null) {
                model.lname = contactList.get(i).getLname();
            } else {
                model.lname = "";
            }

            if (contactList.get(i).getEmail() != null) {
                model.email = contactList.get(i).getEmail();
            } else {
                model.email = "";
            }
            if (contactList.get(i).getCp() != null) {
                model.phone = contactList.get(i).getCp();
            } else {
                model.phone = "";
            }
            if (contactList.get(i).getWp() != null) {
                model.wp = contactList.get(i).getWp();
            } else {
                model.wp = "";
            }
            model.mlid = contactList.get(i).getMlid();
            contactModels.add(model);
        }

        contactsListAdapter = new CustomContactList(this, R.layout.spinner_list_item, contactModels);
        spinnerContact.setAdapter(contactsListAdapter);
        spinnerContact.setSelection(0); // set 1st contact as default value
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (spinnerContact.getSelectedItemPosition() > 0) {
                    selectedContact = (CustomContactModel) adapterView.getSelectedItem();
                    edtEmail.setVisibility(View.GONE);
                } else {
                    edtEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void inviteFriends(String email) {
        //Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // add the address, subject and body of the mail
        //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email });
        //emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_body));
        //emailIntent.setType("*/*");
        //emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // finally start the activity
        //startActivity(Intent.createChooser(emailIntent, "Sending email..."));

        if (getLocation()) {
            String misc = bodyVideoData.getTitle().replace("/", "||||") + "<br>" +
                    bodyVideoData.getHeadLine().replace("/", "||||") + "<br>" +
                    bodyVideoData.getDescript().replace("/", "||||") + "<br>" +
                    bodyVideoData.getAddressFULL();

            try {
                JSONObject extraParams = new JSONObject();
                extraParams.put("email", email);
                extraParams.put("eventdetails", misc);

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseData(extraParams, mContext,
                        "sendEventInvite",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                Log.e("Request", baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showToastMessage(jsonObject.getString("msg"));
                                } else {
                                    showSuccessDlg();
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
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showSuccessDlg() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success_lrg, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
                finish();
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
