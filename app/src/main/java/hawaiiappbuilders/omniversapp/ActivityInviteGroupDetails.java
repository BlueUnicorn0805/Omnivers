package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Calendar;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ActivityInviteGroupDetails extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityInviteGroupDetails.class.getSimpleName();
    GroupInfo groupInfo;
    ArrayList<ContactInfo> groupContacts;
    Restaurant restaurant;

    EditText editEventName;
    TextView editDate;
    TextView editTime;
    Calendar calendarEvent = Calendar.getInstance();
    String strDate;
    String strTime;

    DatePickerDialog.OnDateSetListener dateListener;
    TimePickerDialog.OnTimeSetListener timeListener;

    SwitchCompat compatRSVP;
    Spinner spinnerAttire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invitegroupdetails);

        Intent intent = getIntent();
        groupInfo = intent.getParcelableExtra("group_info");
        groupContacts = intent.getParcelableArrayListExtra("contact_info");
        restaurant = intent.getParcelableExtra("restaurant");

        long orderDueAt = intent.getLongExtra("date", 0);
        boolean bEditDateTime = true;
        if (orderDueAt > 0) {
            calendarEvent.setTimeInMillis(orderDueAt);
            bEditDateTime = false;
        }

        if (groupInfo == null || groupContacts == null || groupContacts.isEmpty()) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_invite_group);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editEventName = findViewById(R.id.editEventName);

        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);

        strDate = DateUtil.toStringFormat_13(calendarEvent.getTime());
        editDate.setText(strDate);

        strTime = DateUtil.toStringFormat_10(calendarEvent.getTime());
        editTime.setText(strTime);

        dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarEvent.set(Calendar.YEAR, year);
                calendarEvent.set(Calendar.MONTH, monthOfYear);
                calendarEvent.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strDate = DateUtil.toStringFormat_13(calendarEvent.getTime());
                editDate.setText(strDate);
            }
        };
        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendarEvent.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarEvent.set(Calendar.MINUTE, minute);

                strTime = DateUtil.toStringFormat_10(calendarEvent.getTime());
                editTime.setText(strTime);
            }
        };

        if (bEditDateTime) {
            editDate.setOnClickListener(this);
            editTime.setOnClickListener(this);
        }

        compatRSVP = findViewById(R.id.compatRSVP);
        spinnerAttire = findViewById(R.id.spinnerAttire);

        findViewById(R.id.btnRequest).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnRequest) {
            hideKeyboard(editEventName);

            String name = editEventName.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                showToastMessage(R.string.error_input_fields);
                return;
            }

            if (getLocation()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("mode", "0");

                    //jsonObject.put("userID", appSettings.getUserId());


                    jsonObject.put("eventTitle", name);

                    if (restaurant != null) {
                        jsonObject.put("Co", restaurant.get_name());
                        jsonObject.put("fullAddress", restaurant.get_address());
                        jsonObject.put("City", restaurant.get_city());
                        jsonObject.put("St", restaurant.get_st());
                        jsonObject.put("Zip", restaurant.get_zip());
                        jsonObject.put("eventPhone", restaurant.get_wp());
                    } else {
                        jsonObject.put("Co", appSettings.getCompany());
                        jsonObject.put("fullAddress", appSettings.getAddress());
                        jsonObject.put("City", appSettings.getCity());
                        jsonObject.put("St", appSettings.getSt());
                        jsonObject.put("Zip", appSettings.getZip());
                        jsonObject.put("eventPhone", appSettings.getCP());
                    }

                    jsonObject.put("date", strDate);
                    jsonObject.put("time", strTime);
                    jsonObject.put("RSVP", compatRSVP.isChecked() ? "Yes" : "No");
                    jsonObject.put("inviterPhone", appSettings.getCP());
                    jsonObject.put("inviterEmail", appSettings.getEmail());
                    jsonObject.put("attire", spinnerAttire.getSelectedItem().toString());

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

                    jsonObject.put("orderID", TextUtils.isEmpty(appSettings.getOrderID()) ? "0" : appSettings.getOrderID());


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

                                    if (responseStatus.optBoolean("status")) {
                                        finish();
                                    } else {
                                        msg(responseStatus.optString("msg"), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        });
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
        } else if (viewId == R.id.editDate) {
            Calendar minDateCalendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext,
                            dateListener,
                            calendarEvent.get(Calendar.YEAR),
                            calendarEvent.get(Calendar.MONTH),
                            calendarEvent.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        } else if (viewId == R.id.editTime) {
            Calendar minDateCalendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(mContext,
                            timeListener,
                            calendarEvent.get(Calendar.HOUR_OF_DAY),
                            calendarEvent.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        } else if (viewId == R.id.btnCancel) {
            finish();
        }
    }
}
