package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.ConnectionActivity.REQUEST_ADD_AS_NEW_CONTACT;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.NEW_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.model.FCMTokenData.OS_UNKNOWN;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;

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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.EmailSearchAdapter;
import hawaiiappbuilders.omniversapp.appointment.ApptUtil;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.OpenSansAutoCompleteTextView;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.AlarmMeetDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.TaskInfo;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityAddMeet extends BaseActivity implements OnClickListener {

    public static final String TAG = ActivityAddMeet.class.getSimpleName();
    ContactInfo contactInfo;
    TaskInfo taskInfo;

    EditText tvTitle;
    SwitchCompat switchAllDay;

    private MessageDataManager dm;
    private ArrayList<ContactInfo> contactList;
    OpenSansAutoCompleteTextView eMail;
    EmailSearchAdapter emailSearchAdapter;
    ContactInfo selectedContact;

    TextView tvStartDate;
    TextView tvStartTime;

    TextView tvEndDate;
    TextView tvEndTime;

    Spinner spinnerTimeZone;

    Button btnAddContact;
    EditText edtLocation;
    EditText edtZoomMeeting;

    TextView tvNotification;
    View btnRemoveNoti;
    TextView tvAlarmRepeat;
    int repeatOption = 0;

    ImageView imgVideoCamera;

    private static final long MILS_5MINS = 5 * 60 * 1000;
    private static final long MILS_10MINS = 10 * 60 * 1000;
    private static final long MILS_15MINS = 15 * 60 * 1000;
    private static final long MILS_30MINS = 30 * 60 * 1000;
    private static final long MILS_1HOUR = 3600 * 1000;
    private static final long MILS_1DAY = 24 * 3600 * 1000;
    long timeNotification = MILS_1HOUR;

    final String[] optionsNotification = new String[]{"5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before", "1 day before"};
    final String[] optionsRepeat = new String[]{"Does not repeat", "Every day", "Every week", "Every month", "Every year"};

    EditText edtEmail;
    EditText edtPhone;

    Calendar calStartTime = Calendar.getInstance();
    Calendar calEndTime = Calendar.getInstance();
    ApptUtil apptUtil;
    String[] timezoneNames;
    Float[] timezoneValues;

    View panelShare;
    CheckBox chkboxShare;
    TextView labelShare;
    EditText tvHandleOf3rdParty;
    DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmeetingtitle);

        dataUtil = new DataUtil(this, ActivityAddMeet.class.getSimpleName());
        panelShare = findViewById(R.id.panelShare);
        chkboxShare = findViewById(R.id.chkbox_share);
        labelShare = findViewById(R.id.label_share);
        tvHandleOf3rdParty = findViewById(R.id.tvHandleOf3rdParty);

        apptUtil = new ApptUtil(this);
        Intent intent = getIntent();
        contactInfo = intent.getParcelableExtra("contact");
        selectedContact = contactInfo;

        calStartTime.setTimeInMillis(intent.getLongExtra("time", Calendar.getInstance().getTimeInMillis()));
        calStartTime.set(Calendar.SECOND, 0);
        calStartTime.set(Calendar.MILLISECOND, 0);

        calEndTime.setTime(calStartTime.getTime());
        calEndTime.add(Calendar.MINUTE, 30);

        taskInfo = intent.getParcelableExtra("task");

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSave).setOnClickListener(this);

        dm = new MessageDataManager(mContext);
        contactList = dm.getAlLContacts();
        dm.close();

        tvTitle = findViewById(R.id.tvTitle);
        switchAllDay = findViewById(R.id.switchAllDay);
        imgVideoCamera = findViewById(R.id.img_video_camera);

        edtZoomMeeting = findViewById(R.id.edtZoomMeeting);
        imgVideoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettings appSettings = new AppSettings(mContext);
                String videoUrl = appSettings.getVideoUrl();
                edtZoomMeeting.setText(videoUrl);
            }
        });
        eMail = findViewById(R.id.eMail);
        emailSearchAdapter = new EmailSearchAdapter(mContext, R.layout.layout_spinner_contact, contactList, true);
        eMail.setAdapter(emailSearchAdapter);
        eMail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedContact = emailSearchAdapter.getItem(position);

                eMail.setText(selectedContact.getEmail());
            }
        });

        btnAddContact = findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String company = tvTitle.getText().toString().trim();
                String email = edtEmail.getText().toString();
                String address = edtLocation.getText().toString();
                String cp = edtPhone.getText().toString();
                String meetingUrl = edtZoomMeeting.getText().toString();

                apptUtil.addNewContactForAttendee((BaseActivity) mContext, company, email, address, cp, meetingUrl);
            }
        });

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);

        tvStartDate.setTag(0);
        tvEndDate.setTag(1);
        tvStartDate.setOnClickListener(dateClickListener);
        tvEndDate.setOnClickListener(dateClickListener);

        tvStartTime.setTag(0);
        tvEndTime.setTag(1);
        tvStartTime.setOnClickListener(timeClickListener);
        tvEndTime.setOnClickListener(timeClickListener);

        tvStartDate.setText(DateUtil.toStringFormat_29(calStartTime.getTime()));
        tvEndDate.setText(DateUtil.toStringFormat_29(calEndTime.getTime()));

        tvStartTime.setText(DateUtil.toStringFormat_10(calStartTime.getTime()));
        tvEndTime.setText(DateUtil.toStringFormat_10(calEndTime.getTime()));

        int defaultSpinnerIndex = 0;
        spinnerTimeZone = findViewById(R.id.spinnerTimeZone);
        timezoneNames = getResources().getStringArray(R.array.spinner_timezone);
        timezoneValues = new Float[timezoneNames.length];
        for (int i = 0; i < timezoneNames.length; i++) {
            String timezoneInfo = timezoneNames[i];
            String[] spliteValues = timezoneInfo.split("=");
            timezoneNames[i] = spliteValues[0];
            timezoneValues[i] = Float.parseFloat(spliteValues[1]);

            if (Math.abs(timezoneValues[i] - appSettings.getUTC()) < 0.000000001) {
                defaultSpinnerIndex = i;
            }
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                timezoneNames);
        spinnerTimeZone.setAdapter(spinnerArrayAdapter);
        spinnerTimeZone.setSelection(defaultSpinnerIndex);

        edtLocation = findViewById(R.id.edtLocation);

        tvNotification = findViewById(R.id.tvNotification);
        btnRemoveNoti = findViewById(R.id.btnRemoveNoti);
        tvNotification.setOnClickListener(this);
        btnRemoveNoti.setOnClickListener(this);
        btnRemoveNoti.setVisibility(View.GONE);

        tvAlarmRepeat = findViewById(R.id.tvAlarmRepeat);
        tvAlarmRepeat.setOnClickListener(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        reviewContactDetails();

        if (taskInfo != null) {
            String currTitle = tvTitle.getText().toString().trim();
            if (TextUtils.isEmpty(currTitle)) {
                currTitle = taskInfo.getDescription();
            } else {
                currTitle += " - " + taskInfo.getDescription();
            }
            tvTitle.setText(currTitle);
        }

        // set to 1 hr prior for alarm default setting
        tvNotification.setText(optionsNotification[4]);
        btnRemoveNoti.setVisibility(View.VISIBLE);
    }

    private void reviewContactDetails() {
        if (contactInfo != null && contactInfo.getMlid() > 0) {
            btnAddContact.setVisibility(View.GONE);

            String title = String.format("%s %s", contactInfo.getCo().trim(), contactInfo.getName()).trim();
            tvTitle.setText(title);

            edtEmail.setText(contactInfo.getEmail());
            edtPhone.setText(contactInfo.getCp());
            edtZoomMeeting.setText(contactInfo.getVideoMeetingUrl());

            String contactAddr = contactInfo.getAddress().trim();
            String zip = contactInfo.getZip().trim();
            if (!TextUtils.isEmpty(contactAddr) && !TextUtils.isEmpty(zip)) {
                edtLocation.setText(contactAddr + ", " + zip);
            } else if (!TextUtils.isEmpty(contactAddr)) {
                edtLocation.setText(contactAddr);
            } else if (!TextUtils.isEmpty(zip)) {
                edtLocation.setText(zip);
            }
        } else {
            btnAddContact.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int tag = (int) view.getTag();

            Calendar calendarDate = tag == 0 ? calStartTime : calEndTime;
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                    if (tag == 0) {
                                        calStartTime.set(Calendar.YEAR, year);
                                        calStartTime.set(Calendar.MONTH, monthOfYear);
                                        calStartTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        String strDate = DateUtil.toStringFormat_29(calStartTime.getTime());

                                        tvStartDate.setText(strDate);
                                    } else {
                                        calEndTime.set(Calendar.YEAR, year);
                                        calEndTime.set(Calendar.MONTH, monthOfYear);
                                        calEndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        String strDate = DateUtil.toStringFormat_29(calEndTime.getTime());

                                        tvEndDate.setText(strDate);
                                    }
                                }
                            },
                            calendarDate.get(Calendar.YEAR),
                            calendarDate.get(Calendar.MONTH),
                            calendarDate.get(Calendar.DAY_OF_MONTH));
            //datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        }
    };

    View.OnClickListener timeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int tag = (int) view.getTag();

            Calendar calendarDate = tag == 0 ? calStartTime : calEndTime;

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(mContext,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                    if (tag == 0) {
                                        calStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calStartTime.set(Calendar.MINUTE, minute);
                                        String strDate = DateUtil.toStringFormat_10(calStartTime.getTime());

                                        tvStartTime.setText(strDate);
                                    } else {
                                        calEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calEndTime.set(Calendar.MINUTE, minute);
                                        String strDate = DateUtil.toStringFormat_10(calEndTime.getTime());

                                        tvEndTime.setText(strDate);
                                    }
                                }
                            },
                            calendarDate.get(Calendar.HOUR_OF_DAY),
                            calendarDate.get(Calendar.MINUTE),
                            false);
            timePickerDialog.show();
        }
    };

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            Intent i = new Intent(mContext, ActivityHomeEvents.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else if (viewId == R.id.tvNotification) {
            // AlertDialog builder instance to build the alert dialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setIcon(R.drawable.ic_nav_alarms);
            alertDialog.setTitle("");

            int currSel = -1;
            if (timeNotification == MILS_5MINS) {
                currSel = 0;
            } else if (timeNotification == MILS_10MINS) {
                currSel = 1;
            } else if (timeNotification == MILS_15MINS) {
                currSel = 2;
            } else if (timeNotification == MILS_30MINS) {
                currSel = 3;
            } else if (timeNotification == MILS_1HOUR) {
                currSel = 4;
            } else if (timeNotification == MILS_1DAY) {
                currSel = 5;
            }

            alertDialog.setSingleChoiceItems(optionsNotification, currSel, new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvNotification.setText(optionsNotification[which]);
                    btnRemoveNoti.setVisibility(View.VISIBLE);

                    if (which == 0) {
                        timeNotification = MILS_5MINS;
                    } else if (which == 1) {
                        timeNotification = MILS_10MINS;
                    } else if (which == 2) {
                        timeNotification = MILS_15MINS;
                    } else if (which == 3) {
                        timeNotification = MILS_30MINS;
                    } else if (which == 4) {
                        timeNotification = MILS_1HOUR;
                    } else if (which == 5) {
                        timeNotification = MILS_1DAY;
                    }

                    // when selected an item the dialog should be closed with the dismiss method
                    dialog.dismiss();
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog customAlertDialog = alertDialog.create();
            customAlertDialog.show();
        } else if (viewId == R.id.btnRemoveNoti) {
            timeNotification = 0;
            tvNotification.setText("");
            btnRemoveNoti.setVisibility(View.GONE);
        } else if (viewId == R.id.tvAlarmRepeat) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setIcon(R.drawable.ic_nav_alarms);
            alertDialog.setTitle("");

            alertDialog.setSingleChoiceItems(optionsRepeat, (int) repeatOption, new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvAlarmRepeat.setText(optionsRepeat[which]);
                    repeatOption = which;

                    // when selected an item the dialog should be closed with the dismiss method
                    dialog.dismiss();
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog customAlertDialog = alertDialog.create();
            customAlertDialog.show();
        } else if (viewId == R.id.btnSave) {
            boolean emailExistsInDb = false;
            if (!edtEmail.getText().toString().isEmpty()) {
                String email = edtEmail.getText().toString().trim();

                ArrayList<ContactInfo> allContacts = dm.getAlLContacts();
                for (int i = 0; i < allContacts.size(); i++) {
                    ContactInfo currContact = allContacts.get(i);
                    if (currContact.getEmail().equalsIgnoreCase(email)) {
                        emailExistsInDb = true;
                    }
                }

                if (!emailExistsInDb) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("New email found")
                            .setMessage("Would you like to add it to your contacts?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveMeetInfo();
                                    startActivity(new Intent(ActivityAddMeet.this, ActivityAddNewContact.class));
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveMeetInfo();
                                }
                            }).show();
                } else {
                    saveMeetInfo();
                }
            } else {
                edtEmail.setText("");
                saveMeetInfo();
            }
        }
    }

    private void saveNewContact(long newId, String email) {
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "rtnMLID" +
                        "&co=" + "" +
                        "&coEmail=" + "" +
                        "&FN=" + "" +
                        "&LN=" + "" +
                        "&nick=" + "" +
                        "&cp=" + "" +
                        "&wp=" + "" +
                        "&streetNum=" + "" +
                        "&street=" + "" +
                        "&city=" + "" +
                        "&state=" + "" +
                        "&zip=" + "" +
                        "&dob=" + "" +
                        "&LDBID=" + newId +
                        "&email=" + email +
                        "&videoMeetingURL=" + "";
        baseUrl += extraParams;
        Log.e("Request", baseUrl);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        GoogleCertProvider.install(mContext);
        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.e("cjlrtnMLID", response);

                if (!TextUtils.isEmpty(response)) {
                    try {
                        // Refresh Data
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject responseStatus = jsonArray.getJSONObject(0);
                        if (responseStatus.has("MLID")) {
                            showToastMessage("Saved successfully!");
                        } else {
                            finish();
                            //showToastMessage("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showAlert(e.getMessage());
                    }
                } else {
                    //showAlert("Server Error");
                    finish();
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

    private void saveMeetInfo() {
        String eventTitle = tvTitle.getText().toString().trim();
        if (TextUtils.isEmpty(eventTitle)) {
            showToastMessage("Please input title");
            return;
        }

        String shareEmail = eMail.getText().toString().trim();
        String handleOf3rdParty = tvHandleOf3rdParty.getText().toString().trim();

        Float tz = timezoneValues[spinnerTimeZone.getSelectedItemPosition()];
        String location = edtLocation.getText().toString().trim();
        String zoomMeetingURL = edtZoomMeeting.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Check MLID
        int shareMLID = 0;
        if (selectedContact != null) {
            //if (email.equals(selectedContact.getEmail())) {
            shareMLID = selectedContact.getMlid();
            //}
        }

        final Intent intent = new Intent();

        intent.putExtra("title", eventTitle);
        intent.putExtra("tz", tz);
        intent.putExtra("location", location);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("startDate", calStartTime.getTimeInMillis());
        intent.putExtra("endDate", calEndTime.getTimeInMillis());

        int mlid = 0;
        int ldbid = 0;
        int allMLID = 0;
        if (contactInfo != null) {
            allMLID = contactInfo.getMlid();
            ldbid = contactInfo.getId();
            if (chkboxShare.isChecked()) {
                mlid = contactInfo.getMlid();
            }
        }

        JSONObject jsonObject = new JSONObject();
        if (getLocation()) {
            String userLat = getUserLat();
            String userLon = getUserLon();

            try {
                /*
                jsonObject.put("appname", "nemtandroid");
                jsonObject.put("lon", userLon);
                jsonObject.put("lat", userLat);
                jsonObject.put("uuid", appSettings.getDeviceId());
                jsonObject.put("cID", appSettings.getUserId());*/
                jsonObject.put("promoid", 0);
                jsonObject.put("industryID", 0);
                jsonObject.put("attendeeMLID", mlid);
                // jsonObject.put("workID", appSettings.getWorkid());
                jsonObject.put("LDBID", ldbid);
                jsonObject.put("meetingID", 0);
                jsonObject.put("sellerID", allMLID);
                jsonObject.put("orderID", 0);
                //jsonObject.put("empID", Integer.parseInt(appSettings.getEmpId()));
                jsonObject.put("mode", 0);
                jsonObject.put("amt", 0);
//                jsonObject.put("TZ", stringToDouble(tz.replace("+", ""))); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("TZ", tz); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTime", DateUtil.toStringFormat_12(calStartTime.getTime()));
                jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(calEndTime.getTime()));
                jsonObject.put("eventTitle", eventTitle);
                jsonObject.put("address", location);
                jsonObject.put("apptLon", 0);
                jsonObject.put("apptLat", 0);
                jsonObject.put("cp", phone);
                jsonObject.put("email", email);
                jsonObject.put("mins", 0);
                jsonObject.put("videoMeetingURL", zoomMeetingURL);
                jsonObject.put("videoMeetingID", "");
                jsonObject.put("videoPasscode", "");
                jsonObject.put("videoAutoPhoneDial", "");
                jsonObject.put("miscUN", "");
                jsonObject.put("miscPW", "");
                //jsonObject.put("utc", appSettings.getUTC());
                jsonObject.put("qty", 0);
                jsonObject.put("editApptID", 0);
                jsonObject.put("newStatusID", NEW_APPOINTMENT);

                String company;
                if (appSettings.getCompany().isEmpty() && appSettings.getCompany() == null) {
                    company = "-";
                } else {
                    company = appSettings.getCompany();
                }

                String name = appSettings.getFN() + " " + appSettings.getLN();
                String senderName;
                if (company.contentEquals("-")) {
                    senderName = name;
                } else {
                    senderName = company + "\n" + name;
                }

                jsonObject.put("senderName", senderName);

                // group (comma separated mlids)
                if (chkboxShare.isChecked()) {
                    jsonObject.put("shareMLID", shareMLID > 0 ? String.valueOf(shareMLID) : "");
                } else {
                    jsonObject.put("shareMLID", "");
                }
                jsonObject.put("share", handleOf3rdParty);
                jsonObject.put("token", appSettings.getDeviceToken());

                Log.e("setAppt", jsonObject.toString());

                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "setAppt", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                // String urlSetAppt = URLResolver.apiSetAppt(jsonObject);

                // Log.e("setAppt", urlSetAppt);


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    Log.e("ActivityAddMeet", "Request:" + str);
                Log.e("ActivityAddMeet", "Request:" + baseUrl);
                try {
                    Log.e("ActivityAddMeet", "Request:" + URLDecoder.decode(baseUrl, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
//                }

//                httpClient.addInterceptor(CurlLoggerInterceptor());

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                GoogleCertProvider.install(mContext);
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, response -> {
                    hideProgressDialog();

                    Log.e("setAppt", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            final JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            if (jsonObject1.has("status") && jsonObject1.getBoolean("status")) {


                                long newApptId = jsonObject1.optLong("NewApptID");
                                // if (newApptId == 0) {
                                // newApptId = 0; // (int) (System.currentTimeMillis() / 1000);
                                // }
                                // Add Alarm

                                if (chkboxShare.isChecked()) { // shared
                                    if (jsonObject1.has("token") && !jsonObject1.getString("token").isEmpty()) {
                                        String token = jsonObject1.getString("token");
                                        NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                        tokenList.add(new FCMTokenData(token, OS_UNKNOWN));
                                        JSONObject payload = new JSONObject();
                                        payload.put("NewApptID", newApptId);
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_New_Appointment, payload);
                                    }
                                }

                                addAlarm(newApptId, chkboxShare.isChecked());

                                setResult(RESULT_OK, intent);
                                finish();
                            } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
//                                showAlert(jsonObject.getString("msg")); // "Appt not entered. Please try again."

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                alertDialogBuilder.setMessage(jsonObject.getString("msg"));
                                alertDialogBuilder.setPositiveButton("Cancel", (dialog, id) -> dialog.dismiss());
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ActivityAddMeet.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "setAppt");
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ActivityAddMeet.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "setAppt");
                }
                e.printStackTrace();
            }
        }
    }

    private void addAlarm(long apptID, boolean checked) {
        // Remove old Alarm
        if (timeNotification > 0) {
            AlarmMeetDataManager.getInstance(mContext).removeAlarm(mContext, apptID);
            long priorTime = timeNotification; // 1 hr prior
            long alarmTime = calStartTime.getTimeInMillis() - priorTime;
            String title = String.format("Appt: %s", tvTitle.getText().toString().trim());

            // Add to Alarm Data Set to manage in reboot
            AlarmMeetDataManager.getInstance(mContext).addNewAlaramData(apptID, alarmTime, title, timeNotification, repeatOption);

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, AlarmNotifyReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("msg", DateUtil.toStringFormat_38(calStartTime.getTime()));

            PendingIntent alarmIntent;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmIntent = PendingIntent.getBroadcast(mContext, (int) apptID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                alarmIntent = PendingIntent.getBroadcast(mContext, (int) apptID, intent, PendingIntent.FLAG_IMMUTABLE);
            }

            if (repeatOption == 0) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            } else {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            }*/
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            } else {
                // "Does not repeat", "Every day", "Every week", "Every month", "Every year"
                long repeatInterval = 0;
                if (repeatOption == 1) {
                    repeatInterval = AlarmManager.INTERVAL_DAY;
                } else if (repeatOption == 2) {
                    repeatInterval = AlarmManager.INTERVAL_DAY * 7;
                } else if (repeatOption == 3) {
                    repeatInterval = AlarmManager.INTERVAL_DAY * 30;
                } else if (repeatOption == 4) {
                    repeatInterval = AlarmManager.INTERVAL_DAY * 365;
                }
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, repeatInterval, alarmIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_AS_NEW_CONTACT) {
            if (data != null) {
                ContactInfo contactSelected = data.getExtras().getParcelable("contact");
                if (contactSelected != null) {
                    contactInfo = contactSelected;
                    btnAddContact.setVisibility(View.GONE);
                    reviewContactDetails();
                }
            }
        }
    }
}
