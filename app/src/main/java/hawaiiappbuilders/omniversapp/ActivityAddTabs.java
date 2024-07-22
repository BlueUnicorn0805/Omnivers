package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityAddTabs extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityAddTabs.class.getSimpleName();
    Restaurant restaurant;

    TextView restName;
    TextView restAddress;
    EditText edtTabName;
    EditText edtAmt;
    TextView edtDate;
    TextView edtTime;

    RadioGroup radioGroup;

    Calendar calendarTab;
    String strDateOfTab;
    DatePickerDialog.OnDateSetListener dateListener;
    TimePickerDialog.OnTimeSetListener timeListener;

    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_tabs);
        dataUtil = new DataUtil(this, ActivityAddTabs.class.getSimpleName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create Tab");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra("restaurant");

        restName = findViewById(R.id.restName);
        restAddress = findViewById(R.id.restAddress);

        restName.setText(restaurant.get_name());
        restAddress.setText(restaurant.getFullAddress());
        restAddress.setOnClickListener(this);

        edtTabName = findViewById(R.id.edtTabName);
        edtAmt = findViewById(R.id.edtAmt);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        radioGroup = findViewById(R.id.radioGroup);

        edtDate.setOnClickListener(this);
        edtTime.setOnClickListener(this);

        calendarTab = Calendar.getInstance();
        strDateOfTab = DateUtil.toStringFormat_12(calendarTab.getTime());
        dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarTab.set(Calendar.YEAR, year);
                calendarTab.set(Calendar.MONTH, monthOfYear);
                calendarTab.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                strDateOfTab = DateUtil.toStringFormat_12(calendarTab.getTime());
                edtDate.setText(DateUtil.toStringFormat_13(calendarTab.getTime()));
            }
        };
        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarTab.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarTab.set(Calendar.MINUTE, minute);

                strDateOfTab = DateUtil.toStringFormat_12(calendarTab.getTime());
                edtTime.setText(DateUtil.toStringFormat_23(calendarTab.getTime()));
            }
        };


        findViewById(R.id.btnOpenTab).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.btnCancel) {
            finish();
        } else if (viewID == R.id.btnOpenTab) {
            openTab();
        } else if (viewID == R.id.edtDate) {
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext, dateListener,
                            calendarTab.get(Calendar.YEAR),
                            calendarTab.get(Calendar.MONTH),
                            calendarTab.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.show();
        } else if (viewID == R.id.edtTime) {
            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(mContext, timeListener,
                            calendarTab.get(Calendar.HOUR_OF_DAY),
                            calendarTab.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        } else if (viewID == R.id.restAddress) {
            Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", restaurant.get_lattiude(), restaurant.get_longitude()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    private void openTab() {
        hideKeyboard(edtTabName);
        hideKeyboard(edtAmt);

        String tabName = edtTabName.getText().toString().trim();
        float amt = 0;
        try {
            amt = Float.parseFloat(edtAmt.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String date = edtDate.getText().toString().trim();
        String time = edtTime.getText().toString().trim();

        if (TextUtils.isEmpty(tabName) || amt <= 0 || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            showToastMessage(R.string.error_input_fields);
            return;
        }

        if (Calendar.getInstance().after(calendarTab)) {
            showToastMessage("Date must be in the future.");
            return;
        }

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLSet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String forBiz = radioGroup.getCheckedRadioButtonId() == R.id.radioBiz ? "1" : "0";
                String extraParams =
                        "&mode=" + "888" +
                                "&tabID=" + "0" +
                                "&sellerID=" + String.valueOf(restaurant.get_id()) +
                                "&buyerid=" + appSettings.getUserId() +
                                "&industryID=" + String.valueOf(restaurant.get_industryID()) +
                                "&promoID=" + "0" +
                                "&Amt=" + String.valueOf(amt) +
                                "&QTY=" + "1" +
                                "&OrderID=" + "0" +
                                "&mins=" + "0" +
                                "&tolat=" + "0" +
                                "&tolon=" + "0" +
                                "&SELLERID=" + String.valueOf(restaurant.get_id()) +
                                "&time=" + DateUtil.toStringFormat_12(calendarTab.getTime()) +
                                "&eventDate=" + DateUtil.toStringFormat_12(calendarTab.getTime()) +
                                "&tabID=" + "0" +
                                "&tableNum=" + "0" +
                                "&forBiz=" + forBiz +
                                "&tabName=" + tabName;
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

                        Log.e("888", response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            String status = jsonObject.getString("status");
                            if (jsonObject.getBoolean("status")) {
                                showToastMessage("Success to Add Tab!");

                                finish();
                            } else {
                                showAlert(jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            if (dataUtil!=null) {
                                dataUtil.setActivityName(ActivityAddTabs.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CJLSet");
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_invalid_response);
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
                    dataUtil.setActivityName(ActivityAddTabs.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLSet");
                }
            }
        }
    }
}
