package hawaiiappbuilders.omniversapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityMyWork extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMyWork.class.getSimpleName();
    EditText edtEmail;
    Spinner chooseJobTypeSpinner;

    private List<IndustryInfo> industries = new ArrayList<>();
    ArrayAdapter industryAdapter;

    private DataUtil dataUtil;

    int valOnSite = 0;
    int valRemote = 0;

    int iAM = 1;

    RadioGroup radioTypesSearch;
    CheckBox onSite, remote;
    int industryId = 0;

    LinearLayout bottomView;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mywork);

        dataUtil = new DataUtil(this, ActivityMyWork.class.getSimpleName());

        getIndustries();

        bottomView = findViewById(R.id.bottomView);
        tableLayout = findViewById(R.id.tableLayout);

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnGoBack).setOnClickListener(this);

        chooseJobTypeSpinner = findViewById(R.id.spinnerJobType);
        // TODO: Get job types
        // ArrayList<String> jobTypes = new ArrayList<>();
        // jobTypes.add("Manager");
//        String[] jobs = getResources().getStringArray(R.array.job_types);

        industryAdapter = new ArrayAdapter<IndustryInfo>(mContext, android.R.layout.simple_spinner_item, industries);
        industryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseJobTypeSpinner.setAdapter(industryAdapter);

        chooseJobTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                industryId = Integer.parseInt(((IndustryInfo) parent.getItemAtPosition(position)).getIndustryID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnBusinessOnwer).setOnClickListener(this);

        radioTypesSearch = findViewById(R.id.radioTypesSearch);

        radioTypesSearch.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.optionFindJob) {
                iAM = 1;
            } else if (checkedId == R.id.optionFindWorker) {
                iAM = 2;
            }
        });

        onSite = findViewById(R.id.chkOnSite);
        remote = findViewById(R.id.chkRemote);

        onSite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                valOnSite = 1;
            else
                valOnSite = 0;
        });

        remote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                valRemote = 1;
            else
                valRemote = 0;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSuccessDlg(String message) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        final AlertDialog errorDlg = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setMessage(message)
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ActivityBizInfo.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLGet");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();

        if (viewID == R.id.btnBack || viewID == R.id.btnGoBack) {
            finish();
        } else if (viewID == R.id.btnSubmit) {
            if (((valOnSite + valRemote) > 0) && industryId > 0) {
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "setJobLooking",
                        BaseFunctions.APP_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                HashMap<String, String> params = new HashMap<>();
                String extraParams =
                        "&industryID=" + industryId +
                                "&iam=" + iAM +
                                "&onsite=" + valOnSite +
                                "&remote=" + valRemote;

                baseUrl += extraParams;
                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;

                Log.e("Request", baseUrl);
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        try {
                            JSONArray jsonArray = new JSONArray(response); // Assuming jsonString contains your JSON data

                            JSONObject objAt0 = jsonArray.getJSONObject(0);

                            if (objAt0.has("status")) {
                                showAlertMessage(ActivityMyWork.this, objAt0.optString("msg"));
                                return;
                            }

                            bottomView.setVisibility(View.GONE);
                            findViewById(R.id.tableContainer).setVisibility(View.VISIBLE);

                            setHeaderToTable(tableLayout, true);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fullName = jsonObject.getString("Fullname");
                                String csz = jsonObject.getString("CSZ");
                                String phone = jsonObject.getString("Phone");
//
//                                // Create a new table row
//                                TableRow tableRow = new TableRow(ActivityMyWork.this);
//                                tableRow.setLayoutParams(new TableLayout.LayoutParams(
//                                        TableLayout.LayoutParams.MATCH_PARENT,
//                                        TableLayout.LayoutParams.WRAP_CONTENT
//                                ));
//
//                                // Add cells to the row
//                                TextView indexTextView = new TextView(ActivityMyWork.this);
//                                indexTextView.setText(String.valueOf(i + 1));
//                                indexTextView.setTextColor(Color.WHITE);
//                                indexTextView.setBackground(getDrawable(R.drawable.table_border));
//                                indexTextView.setPadding(16, 16, 16, 16);
//                                tableRow.addView(indexTextView);
//
//                                TextView fullNameTextView = new TextView(ActivityMyWork.this);
//                                fullNameTextView.setText(fullName);
//                                fullNameTextView.setTextColor(Color.WHITE);
//                                fullNameTextView.setBackground(getDrawable(R.drawable.table_border));
//                                fullNameTextView.setPadding(16, 16, 16, 16);
//                                tableRow.addView(fullNameTextView);
//
//                                TextView cszTextView = new TextView(ActivityMyWork.this);
//                                cszTextView.setText(csz);
//                                cszTextView.setTextColor(Color.WHITE);
//                                cszTextView.setBackground(getDrawable(R.drawable.table_border));
//                                cszTextView.setPadding(16, 16, 16, 16);
//                                tableRow.addView(cszTextView);
//
//                                TextView phoneTextView = new TextView(ActivityMyWork.this);
//                                phoneTextView.setText(phone);
//                                phoneTextView.setTextColor(Color.WHITE);
//                                phoneTextView.setBackground(getDrawable(R.drawable.table_border));
//                                phoneTextView.setPadding(16, 16, 16, 16);
//                                tableRow.addView(phoneTextView);
//
//                                // Add the row to the table layout
//                                tableLayout.addView(tableRow);

                                TableRow tableRow = new TableRow(ActivityMyWork.this);
                                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                                        TableLayout.LayoutParams.MATCH_PARENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT
                                ));

                                TextView headerView = new TextView(ActivityMyWork.this);
                                headerView.setText(/*"Fullname: " + */fullName
                                        + "\nCSZ: " + csz
                                        + /*"\nPhone: " +*/ phone);
                                headerView.setTextColor(Color.WHITE);
                                headerView.setGravity(Gravity.START);
                                headerView.setBackground(getDrawable(R.drawable.table_border));
                                headerView.setPadding(16, 16, 16, 16);
                                tableRow.addView(headerView);

                                tableLayout.addView(tableRow);
                                if (i < jsonArray.length() - 1)
                                    setHeaderToTable(tableLayout, false);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
            } else if (valOnSite + valRemote <= 0) {
                Toast.makeText(this, "Please select OnSite/Remote", Toast.LENGTH_SHORT).show();
            } else if (industryId <= 0) {
                Toast.makeText(this, "Please select Job type", Toast.LENGTH_SHORT).show();
            }
        } else if (viewID == R.id.btnBusinessOnwer) {
            startActivity(new Intent(mContext, ActivityMyWorkSmallBiz.class));
        }
    }

    private void setHeaderToTable(TableLayout tableLayout, boolean header) {
//        // Create a new table row
//        TableRow tableRow = new TableRow(ActivityMyWork.this);
//        tableRow.setLayoutParams(new TableLayout.LayoutParams(
//                TableLayout.LayoutParams.MATCH_PARENT,
//                TableLayout.LayoutParams.WRAP_CONTENT
//        ));
//
//        // Add cells to the row
//        TextView indexTextView = new TextView(ActivityMyWork.this);
//        indexTextView.setText("");
//        indexTextView.setTextColor(Color.WHITE);
//        indexTextView.setBackground(getDrawable(R.drawable.table_border));
//        indexTextView.setPadding(16, 16, 16, 16);
//        tableRow.addView(indexTextView);
//
//        TextView fullNameTextView = new TextView(ActivityMyWork.this);
//        fullNameTextView.setText("Fullname");
//        fullNameTextView.setTextColor(Color.WHITE);
//        fullNameTextView.setBackground(getDrawable(R.drawable.table_border));
//        fullNameTextView.setPadding(16, 16, 16, 16);
//        tableRow.addView(fullNameTextView);
//
//        TextView cszTextView = new TextView(ActivityMyWork.this);
//        cszTextView.setText("CSZ");
//        cszTextView.setTextColor(Color.WHITE);
//        cszTextView.setBackground(getDrawable(R.drawable.table_border));
//        cszTextView.setPadding(16, 16, 16, 16);
//        tableRow.addView(cszTextView);
//
//        TextView phoneTextView = new TextView(ActivityMyWork.this);
//        phoneTextView.setText("Phone");
//        phoneTextView.setTextColor(Color.WHITE);
//        phoneTextView.setBackground(getDrawable(R.drawable.table_border));
//        phoneTextView.setPadding(16, 16, 16, 16);
//        tableRow.addView(phoneTextView);
//
//        // Add the row to the table layout
//        tableLayout.addView(tableRow);

        if (header) {
            TableRow tableRow = new TableRow(ActivityMyWork.this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView headerView = new TextView(ActivityMyWork.this);
            headerView.setText("Contact Info");
            headerView.setTextColor(Color.WHITE);
            headerView.setGravity(Gravity.CENTER);
            headerView.setBackground(getDrawable(R.drawable.table_border));
            headerView.setPadding(16, 16, 16, 16);
            tableRow.addView(headerView);

            tableLayout.addView(tableRow);
        } else {
            TableRow tableRow = new TableRow(ActivityMyWork.this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView headerView = new TextView(ActivityMyWork.this);
            headerView.setText("------------------");
            headerView.setTextColor(Color.WHITE);
            headerView.setGravity(Gravity.CENTER);
            headerView.setPadding(16, 16, 16, 16);
            tableRow.addView(headerView);

            tableLayout.addView(tableRow);
        }
    }

}
