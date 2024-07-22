package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

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

import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class InviteActivity extends BaseActivity {

    EditText edtEmail;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite);
        dataUtil = new DataUtil(this, InviteActivity.class.getSimpleName());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Invite A Friend");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        edtEmail = findViewById(R.id.edtEmail);
        findViewById(R.id.btnInvite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriends();
            }
        });

        // Put current email
        if (!TextUtils.isEmpty(email)) {
            edtEmail.setText(email);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void inviteFriends() {

        String email = edtEmail.getText().toString().trim();
        hideKeyboard(edtEmail);

        if (!isEmailValid(email)) {
            showAlert("Please input valid email");
            return;
        }

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
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "invite" +
                                "&misc=" + email;
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
                                if(dataUtil!=null){
                                    dataUtil.setActivityName(InviteActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e,"CJLGet");
                                }
                                e.printStackTrace();
                                showAlert(e.getMessage());
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();

                        //showSuccessDlg();
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
                sr.setShouldCache(false);
                queue.add(sr);
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(InviteActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLGet");
                }
            }

        }
    }

    private void showSuccessDlg() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.viewSuccess).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errorDlg.dismiss();
            }
        });

        errorDlg.show();
        errorDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
