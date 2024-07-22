package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.ConnectionActivity.REQUEST_ADD_AS_NEW_CONTACT;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.ACCEPT_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.CANCEL_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.DECLINE_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.RESCHEDULE_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.model.FCMTokenData.OS_UNKNOWN;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import hawaiiappbuilders.omniversapp.appointment.ApptUtil;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.meeting.models.User;
import hawaiiappbuilders.omniversapp.meeting.utilities.PreferenceManager;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.AlarmMeetDataManager;
import hawaiiappbuilders.omniversapp.model.CalendarData;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;

public class ViewMeetingActivity extends BaseActivity implements OnClickListener {

    CalendarData.Data apptData;
    ContactInfo contactInfo;

    DataUtil dataUtil;

    Button btnUpdate;
    View btnEdit;
    View btnMenu;

    EditText tvTitle;
    SwitchCompat switchAllDay;

    TextView tvStartDate;
    TextView tvStartTime;

    Button btnDirections;
    Button btnCallPhone;
    Button btnVideoCall;
    TextView tvEndDate;
    TextView tvEndTime;

    View panelDetails;
    Spinner spinnerTimeZone;
    String[] timezoneNames;
    Float[] timezoneValues;
    Float selectedUTC = 0.0F;

    EditText edtLocation;

    LinearLayout layoutLocation;
    TextView tvNotification;
    View btnRemoveNoti;
    long timeNotification = 0;
    TextView tvAlarmRepeat;
    int repeatOption = 0;

    LinearLayout layoutZoom;
    EditText textZoomMeetingUrl;
    LinearLayout layoutCp;
    private static final long MILS_5MINS = 5 * 60 * 1000;
    private static final long MILS_10MINS = 10 * 60 * 1000;
    private static final long MILS_15MINS = 15 * 60 * 1000;
    private static final long MILS_30MINS = 30 * 60 * 1000;
    private static final long MILS_1HOUR = 3600 * 1000;
    private static final long MILS_1DAY = 24 * 3600 * 1000;
    private final String[] optionsNotification = new String[]{"5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before", "1 day before"};
    final String[] optionsRepeat = new String[]{"Does not repeat", "Every day", "Every week", "Every month", "Every year"};

    EditText edtEmail;
    EditText edtPhone;

    Calendar calStartTime = Calendar.getInstance();
    Calendar calEndTime = Calendar.getInstance();
    Calendar start;
    Calendar end;

    Button btnAccept;

    Button btnCancel;

    Button btnDecline;
    Button btnInAppVideoCall;

    Button btnAddContact;

    CardView layoutProposalTime;
    TextView textDescription;
    TextView textProposedTime;

    int newStatusID = 0;

    public static final int REQUEST_UPDATE_STATUS_ID = 979;
    ApptUtil apptUtil;

    PreferenceManager preferenceManager;

    private void updateAppointmentStatus(int action) {
        apptUtil.setNewAppointmentStatus(apptData, action, new ApptUtil.OnUpdateApptListener() {
            @Override
            public void onAppointmentDeclined(int newStatusId) {
                updateAppointment(newStatusId);
            }

            @Override
            public void onAppointmentAccepted(int newStatusId) {
                updateAppointment(newStatusId);
            }

            @Override
            public void onAppointmentRescheduled(int newStatusId) {
                updateAppointment(newStatusId);
            }

            @Override
            public void onAppointmentCancelled(int newStatusId) {
                updateAppointment(newStatusId);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmeeting);
        dataUtil = new DataUtil(this, ViewMeetingActivity.class.getSimpleName());
        Intent intent = getIntent();
        contactInfo = intent.getParcelableExtra("contact");

        apptUtil = new ApptUtil(this);
        // Check Data
        String data = intent.getStringExtra("appt_data");

        apptData = new Gson().fromJson(data, CalendarData.Data.class);
        String str = new Gson().toJson(apptData);

        if (apptData == null) {
            finish();
            return;
        }

        findViewById(R.id.btnBack).setOnClickListener(this);

        preferenceManager = new PreferenceManager(getApplicationContext());

        // Accept
        btnAccept = findViewById(R.id.button_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointmentStatus(ACCEPT_APPOINTMENT);
            }
        });

        // Cancel
        btnCancel = findViewById(R.id.button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointmentStatus(CANCEL_APPOINTMENT);
            }
        });

        // Decline
        btnDecline = findViewById(R.id.button_decline);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointmentStatus(DECLINE_APPOINTMENT);
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
                String meetingUrl = textZoomMeetingUrl.getText().toString();
                apptUtil.addNewContactForAttendee((BaseActivity) mContext, company, email, address, cp, meetingUrl);
            }
        });

        btnUpdate = findViewById(R.id.btnUpdate);
        btnEdit = findViewById(R.id.btnEdit);
        btnMenu = findViewById(R.id.btnMenu);

        layoutProposalTime = findViewById(R.id.layout_proposal);
        textDescription = findViewById(R.id.text_status_details);
        textProposedTime = findViewById(R.id.text_proposed_time);

        apptUtil.initializeActionButtons(
                apptData,
                btnAddContact,
                btnAccept,
                btnCancel,
                btnDecline,
                btnUpdate,
                btnEdit,
                btnMenu);

        btnUpdate.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnMenu.setOnClickListener(this);

        tvTitle = findViewById(R.id.tvTitle);
        //tvTitle.setEnabled(false);

        switchAllDay = findViewById(R.id.switchAllDay);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);

        layoutCp = findViewById(R.id.layout_cp);

        // Phone Call
        btnCallPhone = findViewById(R.id.btnCallPhone);
        btnCallPhone.setOnClickListener(this);
        if (TextUtils.isEmpty(apptData.getCp())) {
            btnCallPhone.setVisibility(View.GONE);
            layoutCp.setVisibility(View.GONE);
        } else {
            btnCallPhone.setVisibility(View.VISIBLE);
            layoutCp.setVisibility(View.VISIBLE);
        }

        // Video URL
        layoutZoom = findViewById(R.id.layout_zoom);
        btnVideoCall = findViewById(R.id.btnVideoCall);
        btnVideoCall.setOnClickListener(this);
        btnInAppVideoCall = findViewById(R.id.btnInAppVideoCall);
        btnInAppVideoCall.setOnClickListener(this);
        textZoomMeetingUrl = findViewById(R.id.textZoomMeetingUrl);
        btnVideoCall.setVisibility(View.VISIBLE);
        layoutZoom.setVisibility(View.VISIBLE);
        textZoomMeetingUrl.setVisibility(View.VISIBLE);
        if (!apptData.getVideoMeetingURL().isEmpty()) {
            textZoomMeetingUrl.setText(apptData.getVideoMeetingURL());
        }
        /*if (TextUtils.isEmpty(apptData.getCp()) || apptData.getCp().length() <= 5) {
            layoutCp.setVisibility(View.GONE);
            btnCallPhone.setVisibility(View.GONE);
        } else {
            layoutCp.setVisibility(View.VISIBLE);
            btnCallPhone.setVisibility(View.VISIBLE);
        }*/

        btnVideoCall.setOnClickListener(v -> {
            if (!apptData.getVideoMeetingURL().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apptData.getVideoMeetingURL()));
                startActivity(browserIntent);
            }
        });


//        textZoomMeetingUrl.setOnClickListener(v -> {
//            if (!apptData.getVideoMeetingURL().isEmpty() && textZoomMeetingUrl.getText().toString().startsWith("https")) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(apptData.getVideoMeetingURL()));
//                startActivity(browserIntent);
//            }
//        });
        textZoomMeetingUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                apptData.setVideoMeetingURL(s.toString());
            }
        });


        btnInAppVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactInfo != null) {
                    callSelectedContact();
                }
            }
        });

        // Directions
        layoutLocation = findViewById(R.id.layout_location);
        btnDirections = findViewById(R.id.btnGoAddress);
        btnDirections.setOnClickListener(this);
        /*if (TextUtils.isEmpty(apptData.getAddress())) {
            layoutLocation.setVisibility(View.GONE);
            btnDirections.setVisibility(View.GONE);
        } else {
            layoutLocation.setVisibility(View.VISIBLE);
            btnDirections.setVisibility(View.VISIBLE);
        }*/

        tvStartDate.setTag(0);
        tvEndDate.setTag(1);
        tvStartDate.setOnClickListener(dateClickListener);
        tvEndDate.setOnClickListener(dateClickListener);

        tvStartTime.setTag(0);
        tvEndTime.setTag(1);
        tvStartTime.setOnClickListener(timeClickListener);
        tvEndTime.setOnClickListener(timeClickListener);

        // Details
        panelDetails = findViewById(R.id.panelDetails);

        int defaultSpinnerIndex = 0;
        spinnerTimeZone = findViewById(R.id.spinnerTimeZone);
        timezoneNames = getResources().getStringArray(R.array.spinner_timezone);
        timezoneValues = new Float[timezoneNames.length];
        for (int i = 0; i < timezoneNames.length; i++) {
            String timezoneInfo = timezoneNames[i];
            String[] spliteValues = timezoneInfo.split("=");
            timezoneNames[i] = spliteValues[0];
            timezoneValues[i] = Float.parseFloat(spliteValues[1]);

            if (Math.abs(timezoneValues[i] - apptData.getTZ()) < 0.000000001) {
                defaultSpinnerIndex = i;
            }
        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                timezoneNames);
        spinnerTimeZone.setAdapter(spinnerArrayAdapter);
        spinnerTimeZone.setSelection(defaultSpinnerIndex);

        spinnerTimeZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUTC = timezoneValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUTC = timezoneValues[spinnerTimeZone.getSelectedItemPosition()];
            }
        });

        edtLocation = findViewById(R.id.edtLocation);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        tvTitle.setText(apptData.getTitle());
        edtEmail.setText(apptData.getEmail());
        edtPhone.setText(apptData.getCp());
        edtLocation.setText(apptData.getAddress());
        textZoomMeetingUrl.setText(apptData.getVideoMeetingURL());

        // TODO: Store reference of current appointment date
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        if (apptData.getStartDate() != null) {
            calStartTime.setTimeInMillis(DateUtil.parseDataFromFormat20(apptData.getStartDate()).getTime());
            start.setTime(DateUtil.parseDataFromFormat20(apptData.getStartDate()));
        }

        if (apptData.getEndDate() != null) {
            calEndTime.setTimeInMillis(DateUtil.parseDataFromFormat20(apptData.getEndDate()).getTime());
            end.setTime(DateUtil.parseDataFromFormat20(apptData.getEndDate()));
        }

        tvStartDate.setText(DateUtil.toStringFormat_29(calStartTime.getTime()));
        tvStartTime.setText(DateUtil.toStringFormat_10(calStartTime.getTime()));

        tvEndDate.setText(DateUtil.toStringFormat_29(calEndTime.getTime()));
        tvEndTime.setText(DateUtil.toStringFormat_10(calEndTime.getTime()));

        //panelDetails.setVisibility(View.GONE);
        //btnUpdate.setVisibility(View.GONE);

        // Alarm Settings
        tvNotification = findViewById(R.id.tvNotification);
        btnRemoveNoti = findViewById(R.id.btnRemoveNoti);
        tvNotification.setOnClickListener(this);
        btnRemoveNoti.setOnClickListener(this);
        btnRemoveNoti.setVisibility(View.GONE);

        tvAlarmRepeat = findViewById(R.id.tvAlarmRepeat);
        tvAlarmRepeat.setOnClickListener(this);

        Log.e("CalInfo", apptData.getCalId() + "");


        AlarmMeetDataManager.AlarmMeetData alarmMeetData = AlarmMeetDataManager.getInstance(mContext).getAlarm(apptData.getCalId());
        if (alarmMeetData != null) {

            timeNotification = alarmMeetData.priorTime;
            String titleNotification = "";
            if (timeNotification == MILS_5MINS) {
                titleNotification = optionsNotification[0];
            } else if (timeNotification == MILS_10MINS) {
                titleNotification = optionsNotification[1];
            } else if (timeNotification == MILS_15MINS) {
                titleNotification = optionsNotification[2];
            } else if (timeNotification == MILS_30MINS) {
                titleNotification = optionsNotification[3];
            } else if (timeNotification == MILS_1HOUR) {
                titleNotification = optionsNotification[4];
            } else if (timeNotification == MILS_1DAY) {
                titleNotification = optionsNotification[5];
            }

            tvNotification.setText(titleNotification);
            btnRemoveNoti.setVisibility(View.VISIBLE);

            if (alarmMeetData.repeatOption < optionsRepeat.length) {
                tvAlarmRepeat.setText(optionsRepeat[alarmMeetData.repeatOption]);
            }
        }

        if (apptUtil.isApptForPatient(apptData)) {
            tvTitle.setText(apptData.getAttendeeName());
        } else {
            tvTitle.setText(apptData.getTitle());
        }
    }

    private void callSelectedContact() {
        User selectedContact = new User();
        selectedContact.firstName = contactInfo.getFname();
        selectedContact.lastName = contactInfo.getLname();
        selectedContact.mlid = contactInfo.getMlid();

        // TODO: Show progress bar, Check user if already exists in firestore
        AtomicBoolean isFound = new AtomicBoolean(false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String signedInUserId = preferenceManager.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            int currMlid = Integer.parseInt(String.valueOf(documentSnapshot.getLong(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID)));
                            if (currMlid == selectedContact.mlid) {
                                isFound.set(true);
                                selectedContact.token = documentSnapshot.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FCM_TOKEN);
                                break;
                            }
                        }
                    }
                    if (isFound.get()) {
                        // TODO:  Retrieve user data from firestore
                        getSelectedContactToken(selectedContact);
                    } else {
                        // TODO:  User doesn't exist.
                        showToastMessage("User is not logged in");
                    }
                });
    }

    // TODO:  TRANSFER THIS ON A COMMON HELPER CLASS
    private void getSelectedContactToken(hawaiiappbuilders.omniversapp.meeting.models.User user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID, user.mlid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        // TODO:  get token and initiate call

                        String token = documentSnapshot.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FCM_TOKEN);
                        if (token == null || token.trim().isEmpty()) {
                            Toast.makeText(mContext, user.firstName + " " + user.lastName + " is not available for meeting", Toast.LENGTH_SHORT).show();
                        } else {
                            user.token = token;
                          /*  Intent videoCallIntent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                            videoCallIntent.putExtra("user", new Gson().toJson(user));
                            videoCallIntent.putExtra("type", "video"); // video, audio
                            startActivity(videoCallIntent);*/
                        }

                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    OnClickListener dateClickListener = new OnClickListener() {
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

                                        // Update end time as well
                                        calEndTime = calStartTime;
                                        calEndTime.add(Calendar.MINUTE, 30);

                                        tvEndDate.setText(DateUtil.toStringFormat_29(calEndTime.getTime()));
                                    } else {
                                        calEndTime.set(Calendar.YEAR, year);
                                        calEndTime.set(Calendar.MONTH, monthOfYear);
                                        calEndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        String strDate = DateUtil.toStringFormat_29(calEndTime.getTime());


                                        tvEndDate.setText(strDate);
                                    }

                                    checkIfDateUpdated();
                                }
                            },
                            calendarDate.get(Calendar.YEAR),
                            calendarDate.get(Calendar.MONTH),
                            calendarDate.get(Calendar.DAY_OF_MONTH));
            //datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        }
    };

    OnClickListener timeClickListener = new OnClickListener() {
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

    private void updateAppointment(int newStatusID) {
        String eventTitle = tvTitle.getText().toString().trim();
        if (TextUtils.isEmpty(eventTitle)) {
            showToastMessage("Please input title");
            return;
        }

        String location = edtLocation.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        final Intent intent = new Intent();

        intent.putExtra("title", eventTitle);
        intent.putExtra("tz", selectedUTC);
        intent.putExtra("location", location);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("startDate", calStartTime.getTimeInMillis());
        intent.putExtra("endDate", calEndTime.getTimeInMillis());

        int mlid = 0;
        int ldbid = 0;
        if (contactInfo != null) {
            ldbid = contactInfo.getId();
            try {
                mlid = contactInfo.getMlid();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        if (getLocation()) {
            String userLat = getUserLat();
            String userLon = getUserLon();


            // if mode == 3, update appointment details regardless of statusID
            // if mode == 1, update status id (except reschedule)
            // if mode == 2, reschedule (accept changes in date time)

            int mode = 0;
            switch (newStatusID) {
                case -1:
                    newStatusID = apptData.getApptStatusID();
                    mode = 3;
                    break;
                case RESCHEDULE_APPOINTMENT:
                    // update date and time
                    // and the newStatusID to 2225
                    mode = 2;
                    break;
                case ACCEPT_APPOINTMENT:
                case CANCEL_APPOINTMENT:
                case DECLINE_APPOINTMENT:
                    mode = 1;
                    break;
            }


            MessageDataManager dm = new MessageDataManager(mContext);

            ContactInfo contact = dm.getContactInfoById(mlid);
            String videoMeetingUrl = apptData.getVideoMeetingURL();

            try {
                jsonObject.put("promoid", 0);
                jsonObject.put("industryID", 0);
                jsonObject.put("attendeeMLID", mlid);
                jsonObject.put("LDBID", ldbid);
                jsonObject.put("meetingID", 0);
                jsonObject.put("sellerID", 0);
                jsonObject.put("orderID", 0);
                jsonObject.put("mode", mode);
                jsonObject.put("amt", 0);
//                jsonObject.put("TZ", stringToDouble(tz.replace("+", ""))); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTime", DateUtil.toStringFormat_12(calStartTime.getTime()));
                jsonObject.put("TZ", selectedUTC); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(calEndTime.getTime()));
                jsonObject.put("eventTitle", eventTitle);
                jsonObject.put("address", location);
                jsonObject.put("apptLon", 0);
                jsonObject.put("apptLat", 0);
                jsonObject.put("cp", phone);
                jsonObject.put("email", email);
                jsonObject.put("mins", 0);
                jsonObject.put("videoMeetingURL", videoMeetingUrl);
                jsonObject.put("videoMeetingID", "");
                jsonObject.put("videoPascode", "");
                jsonObject.put("videoAutoPhoneDial", "");
                jsonObject.put("miscUN", "");
                jsonObject.put("miscPW", "");
                jsonObject.put("qty", 0);
                jsonObject.put("editApptID", apptData.getCalId());
                jsonObject.put("newStatusID", newStatusID);
                jsonObject.put("senderName", apptData.getAttendeeName());

                Log.e("setAppt", "request -> " + jsonObject.toString());

                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "setAppt", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                Log.e("request", "request -> " + baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                GoogleCertProvider.install(mContext);
                int finalMlid = mlid;
                int finalNewStatusID = newStatusID;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("setAppt", "response -> " + response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    long newApptId = jsonObject.optLong("NewApptID");
                                    if (newApptId == 0) {
                                        newApptId = apptData.getCalId();
                                    }
                                    // Add Alarm

                                    //  todo: no need to set an alarm every status update.
                                    //  todo: only update it when it's a reschedule since date and time will be changed
                                    addAlarm(newApptId);

                                    setResult(RESULT_OK, intent);

                                    finish();

                                    if (!apptUtil.isApptCreatedByDoctorAndYouAreTheAttendee(apptData)) {
                                        // TODO: handle newstatus id and send push
                                        int payloadType = 0;
                                        if (apptUtil.isApptForPatient(apptData)) {
                                            switch (finalNewStatusID) {
                                                case RESCHEDULE_APPOINTMENT:
                                                    payloadType = PayloadType.PT_Reschedule_Appointment;
                                                    break;
                                                case ACCEPT_APPOINTMENT:
                                                    if (apptData.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                                                        payloadType = PayloadType.PT_Reschedule_Accepted;
                                                    } else {
                                                        payloadType = PayloadType.PT_Accept_Appointment;
                                                    }
                                                    break;
                                                case CANCEL_APPOINTMENT:
                                                    payloadType = PayloadType.PT_Cancel_Appointment;
                                                    break;
                                                case DECLINE_APPOINTMENT:
                                                    if (apptData.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                                                        payloadType = PayloadType.PT_Reschedule_Declined;
                                                    } else {
                                                        payloadType = PayloadType.PT_Decline_New_Appointment;
                                                    }
                                                    break;
                                            }
                                        } else if (apptUtil.isApptCreatedByDoctor(apptData)) {
                                            switch (finalNewStatusID) {
                                                case RESCHEDULE_APPOINTMENT:
                                                    payloadType = PayloadType.PT_Propose_Reschedule_Appointment;
                                                    break;
                                                case ACCEPT_APPOINTMENT:
                                                    if (apptData.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                                                        payloadType = PayloadType.PT_Reschedule_Accepted;
                                                    } else {
                                                        payloadType = PayloadType.PT_Accept_Appointment;
                                                    }
                                                    break;
                                                case CANCEL_APPOINTMENT:
                                                    payloadType = PayloadType.PT_Cancel_Appointment;
                                                    break;
                                                case DECLINE_APPOINTMENT:
                                                    if (apptData.getApptStatusID() == RESCHEDULE_APPOINTMENT) {
                                                        payloadType = PayloadType.PT_Reschedule_Declined;
                                                    } else {
                                                        payloadType = PayloadType.PT_Decline_New_Appointment;
                                                    }
                                                    break;
                                            }
                                        }

                                        if (jsonObject.has("token") && !jsonObject.getString("token").isEmpty()) {
                                            String token = jsonObject.getString("token");
                                            NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                            ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                            tokenList.add(new FCMTokenData(token, OS_UNKNOWN));
                                            JSONObject payload = new JSONObject();
                                            payload.put("ApptID", apptData.getCalId());
                                            payload.put("attendeeMLID", finalMlid);
                                            payload.put("CALSetByID", apptData.getCalId());
                                            payload.put("ApptStatusID", finalNewStatusID);
                                            payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                            payload.put("SenderID", appSettings.getUserId());
                                            notificationHelper.sendPushNotification(mContext, tokenList, payloadType, payload);
                                        }
                                    }
                                    finish();
                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                    alertDialogBuilder.setMessage(jsonObject.getString("msg"));
                                    alertDialogBuilder.setPositiveButton("Cancel", (dialog, id) -> dialog.dismiss());
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_conn_error);
                        } else {
//                            showAlert(error.getMessage());
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                            alertDialogBuilder.setMessage(error.getMessage());
                            alertDialogBuilder.setPositiveButton("Cancel", (dialog, id) -> dialog.dismiss());
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                dataUtil.setActivityName(ViewMeetingActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setAppt");
                e.printStackTrace();
            }
        }
    }

    public void checkIfDateUpdated() {
        if (apptUtil.isDateTimeUpdated(start, end, calStartTime, calEndTime)) {
            ViewUtil.setVisible(btnUpdate);

            btnUpdate.setText("Reschedule");

            String changedStartDate = DateUtil.toStringFormat_12(calStartTime.getTime());
            String changedEndDate = DateUtil.toStringFormat_12(calEndTime.getTime());

            start = Calendar.getInstance();
            end = Calendar.getInstance();
            start.setTime(DateUtil.parseDataFromFormat20(changedStartDate));
            end.setTime(DateUtil.parseDataFromFormat20(changedEndDate));

            Date startDateTime = DateUtil.parseDataFromFormat12(changedStartDate);
            calStartTime.setTime(startDateTime);

            Date endDateTime = DateUtil.parseDataFromFormat12(changedEndDate);
            calEndTime.setTime(endDateTime);

        } else {
            ViewUtil.setGone(btnUpdate);
            btnUpdate.setText("Update");
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnUpdate) {
            if (apptUtil.isDateTimeUpdated(start, end, calStartTime, calEndTime)) {
                if (apptData.getAttendeeMLID() == 0) {
                    updateAppointment(-1);
                } else {
                    updateAppointmentStatus(RESCHEDULE_APPOINTMENT);
                }
            } else {
                updateAppointment(-1);
            }
        } else if (viewId == R.id.btnEdit) {
            panelDetails.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);

            btnDirections.setVisibility(View.GONE);

            btnEdit.setVisibility(View.GONE);
            btnMenu.setVisibility(View.GONE);
        } else if (viewId == R.id.btnMenu) {
            PopupMenu popup = new PopupMenu(mContext, view);
            makePopupMenuIconVisible(popup);
            popup.getMenuInflater().inflate(R.menu.menu_meet, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_delete) {
                        removeCalendarData();
                    } else if (itemId == R.id.menu_dup) {

                    } else if (itemId == R.id.menu_directions) {
                        gotoLocation();
                    }

                    return true;
                }
            });
            popup.show();//showing popup menu
        } else if (viewId == R.id.btnGoAddress) {
            gotoLocation();
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
        } else if (viewId == R.id.btnCallPhone) {
            if (!TextUtils.isEmpty(apptData.getCp())) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + apptData.getCp()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
    }

    private void removeCalendarData() {
        if (getLocation()) {
            showProgressDialog();

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CalGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "delete" +
                            "&misc=" + "" +
                            "&ApptID=" + apptData.getCalId() +
                            "&SetByID=" + "0" +
                            "&ApptWithMLID=" + "0" +
                            "&LocLat=" + "0" +
                            "&loclon=" + "0" +
                            "&DateStart=" + "1-1-2021" +
                            "&DateEnd=" + "1-1-2021";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            GoogleCertProvider.install(mContext);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("delete", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                AlarmMeetDataManager.getInstance(mContext).removeAlarm(mContext, apptData.getCalId()/*apptData.getMeetingId()*/);

                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkErrorHandle(mContext, error);
                    hideProgressDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }
    }

    private void gotoLocation() {
        if (!TextUtils.isEmpty(apptData.getAddress())) {
            //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s&mode=d", apptData.getAddress()));

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                showToastMessage("Please install google map");
            }
        } else {
            showToastMessage("No Address Info");
        }
    }

    private void makePopupMenuIconVisible(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    private void addAlarm(long apptID) {
        // Remove old Alarm
        AlarmMeetDataManager.getInstance(mContext).removeAlarm(mContext, apptID);

        if (apptUtil.isApptForPatient(apptData)) {
            // todo: set notification to 1 hr
            timeNotification = MILS_1HOUR;
        }

        long priorTime = timeNotification; // 30 mins prior
        long alarmTime = calStartTime.getTimeInMillis() - priorTime;
        String title = tvTitle.getText().toString().trim();

        // Add to Alarm Data Set to manage in reboot
        AlarmMeetDataManager.getInstance(mContext).addNewAlaramData(apptID, alarmTime, title, timeNotification, repeatOption);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmNotifyReceiver.class);
        //intent.putExtra("title", title);
        //intent.putExtra("msg", "It's almost time for your meeting");
        intent.putExtra("title", "Appt:" + title);
        intent.putExtra("msg", DateUtil.toStringFormat_38(calStartTime.getTime()));

        PendingIntent alarmIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            alarmIntent = PendingIntent.getBroadcast(mContext, (int) apptID, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            alarmIntent = PendingIntent.getBroadcast(mContext, (int) apptID, intent, PendingIntent.FLAG_IMMUTABLE);
        }

        if (repeatOption == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            } else {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_AS_NEW_CONTACT) {
            if (data != null) {
                ContactInfo contactSelected = data.getExtras().getParcelable("contact");
                if (contactSelected != null) {
                    contactInfo = contactSelected;
                    apptData.setAttendeeMLID(-1);
                    apptUtil.initializeActionButtons(
                            apptData,
                            btnAddContact,
                            btnAccept,
                            btnCancel,
                            btnDecline,
                            btnUpdate,
                            btnEdit,
                            btnMenu);

                    final Intent intent = new Intent();
                    intent.putExtra("attendeeMLID", -1);
                    intent.putExtra("calID", apptData.getCalId());
                    intent.putExtra("ldbID", contactInfo.getId());

                    String eventTitle = tvTitle.getText().toString().trim();
                    if (TextUtils.isEmpty(eventTitle)) {
                        showToastMessage("Please input title");
                        return;
                    }

                    float tz = selectedUTC;
                    String location = edtLocation.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    intent.putExtra("title", eventTitle);
                    intent.putExtra("tz", tz);
                    intent.putExtra("location", location);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    intent.putExtra("startDate", calStartTime.getTimeInMillis());
                    intent.putExtra("endDate", calEndTime.getTimeInMillis());

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}
