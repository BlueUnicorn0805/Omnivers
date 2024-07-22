package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityComment extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityComment.class.getSimpleName();
    Toolbar toolbar;

    EditText edtMessage;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);
        dataUtil = new DataUtil(this, ActivityComment.class.getSimpleName());

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Support");*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        edtMessage = findViewById(R.id.edtMessage);
        findViewById(R.id.btnComment).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if(viewId == R.id.btnToolbarHome) {
          backToHome();
        }else if (viewId == R.id.btnComment) {
            sendMessage();
        }
    }

    private void sendMessage() {

        // Check Message
        String message = edtMessage.getText().toString().trim();
        hideKeyboard(edtMessage);

        if (TextUtils.isEmpty(message)) {
            showToastMessage("Pleaes input message to send");
            return;
        }

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "allNotes",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams = "&mode=addsupport" +
                        "&tiedToMLID=" + 161 +
                        "&tiedtoLDBID=" + "-1" +
                        "&note=" + message +
                        "&fromID=" + appSettings.getUserId();
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

                        Log.e("addNote", response);

                    /*if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)*//*new JSONObject(response)*//*;
                            if(jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showAlert(jsonObject.getString("msg"));
                            } else {
                                showToastMessage("Successfully added your notes");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }
                    }*/

                        showToastMessage("Successfully added your notes");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_server_response);
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
            }catch (Exception e){
                if (dataUtil!=null) {
                    dataUtil.setActivityName(ActivityComment.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "allNotes");
                }
            }
        }
    }
}
