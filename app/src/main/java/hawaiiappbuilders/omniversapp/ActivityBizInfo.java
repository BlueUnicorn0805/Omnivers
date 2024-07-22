package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityBizInfo extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityBizInfo.class.getSimpleName();
    EditText edtEmail;

    Spinner spinnerIndustries;
    private List<IndustryInfo> industries = new ArrayList<>();
    ArrayAdapter industryAdapter;
    private DataUtil dataUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bizinfo);
        dataUtil = new DataUtil(this, ActivityBizInfo.class.getSimpleName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Business Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinnerIndustries = findViewById(R.id.spinnerIndustry);
        industryAdapter = new ArrayAdapter<IndustryInfo>(mContext, android.R.layout.simple_spinner_item, industries);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIndustries.setAdapter(industryAdapter);

        findViewById(R.id.btnSave).setOnClickListener(this);

        getIndustries();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getIndustries() {
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
                        "&mode=" + "ListIndustries" +
                                "&misc=" + "700000" +
                                "&industry=" + "123";
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

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                // Refresh Data
                                industries.clear();

                                JSONArray jsonArray = new JSONArray(response);
                                int itemCnt = jsonArray.length();
                                for (int i = 0; i < itemCnt; i++) {

                                    // Null Check
                                    if (jsonArray.getString(i).equals("null"))
                                        break;

                                    JSONObject itemObj = jsonArray.getJSONObject(i);

                                    IndustryInfo newRes = new IndustryInfo();
                                    newRes.setIndustryID(itemObj.getString("IndustID"));
                                    newRes.setTypeDesc(itemObj.getString("CatName"));

                                    industries.add(newRes);

                                    // Get Child List
                                    if (itemObj.has("items") && !itemObj.isNull("items")) {
                                        ArrayList<IndustryInfo> childItems = new ArrayList<>();
                                        JSONArray childArray = itemObj.getJSONArray("items");

                                        // Parse Child Industry items
                                        for (int j = 0; j < childArray.length(); j++) {

                                            // Null Check
                                            if (childArray.getString(j).equals("null"))
                                                break;

                                            JSONObject childObj = childArray.getJSONObject(j);
                                            IndustryInfo child = new IndustryInfo();
                                            child.setIndustryID(childObj.getString("IndustID"));
                                            child.setTypeDesc(childObj.getString("Name"));

                                            childItems.add(child);
                                        }
                                        newRes.setChildIndustryInfo(childItems);
                                    }
                                }

                                industryAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showAlert(e.getMessage());
                            }
                        } else {
                            showAlert("Server Error");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert("Server error!");
                        } else {
                            showAlert(error.getMessage());
                        }
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
                    dataUtil.setActivityName(ActivityBizInfo.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLGet");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.btnSubmit) {

        } else if (viewID == R.id.btnBusinessOnwer) {
            startActivity(new Intent(mContext, ActivityMyWorkSmallBiz.class));
        }
    }
}
