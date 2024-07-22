package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.model.FCMTokenData.OS_UNKNOWN;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.ContactSearchAdapter;
import hawaiiappbuilders.omniversapp.adapters.ScheduleAdapter;
import hawaiiappbuilders.omniversapp.adapters.TimedGridViewAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.localdb.ContactsDataSource;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.CalendarData;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.Note;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;

public class ActivityAppointmentSetting extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ActivityAppointmentSetting.class.toString();
    private static final int REQUEST_ADD_CALENDAR = 1010;

    private TextView appointmentSelectedDateTextView;
    private RecyclerView appointmentRecyclerView;
    private Spinner appointmentWeekSelector;
    private List<String> appointmentWeekSelectorStringList = new ArrayList<>();
    private List<Calendar> calendarObject = new ArrayList<>();
    private Calendar selectedCalendarObject = Calendar.getInstance();
    private boolean isFirst = true;

    private final List<TextView> dateTextViews = new ArrayList<>();
    private final List<ImageView> selectedImageViews = new ArrayList<>();
    private ScheduleAdapter adapter;
    private List<CalendarData.Data> mCalendarDataList = new ArrayList<>();

    private View panelNotes;
    TextView tvNotes;
    private EditText edtNote;

    View panelMessages;
    ScrollView svMessages;
    TextView tvReceivedMessages;
    EditText edtMsgSend;

    // ---------------------------- Appointment ------------------------------
    View panelAppointment;

    boolean isNewAppt = false;
    TextView tvRescheduleTitle;

    TextView tvApptTitle;

    View panelNewApptInfo;
    AutoCompleteTextView edtApptContact;
    ContactSearchAdapter contactSearchAdapter;
    ArrayList<ContactInfo> contactInfoArrayList;
    EditText edtApptTitle;

    private static final String DAY_OF_WEEK_PATTERN = "EEE";
    private static final String DAY_OF_MONTH_PATTERN = "dd";
    private static final String MONTH_PATTERN = "MMM";
    private static final String YEAR_PATTERN = "yyyy";
    private GridView mTimesGridView;
    private RecyclerView mScheduledTimeRecyclerView;
    private ImageView previousDateImageView;
    private ImageView nextDateImageView;

    String selectedDate = "";

    private List<DateViewHolder> dateViewHolderList = new ArrayList<>();
    private int weekCount = 0;
    private final int weekCountIncrementDecrement = 7;

    private class DateViewHolder {
        private final LinearLayout aaDateCellLinearLayout;
        private final TextView dayOfTheWeekTextView;
        private final TextView dayOfTheMonthTextView;
        private final TextView monthTextView;
        private final TextView yearTextView;

        public DateViewHolder(View view) {
            aaDateCellLinearLayout = (LinearLayout) view;//(LinearLayout) view.findViewById(R.id.aa_date_cell_ll);
            dayOfTheWeekTextView = (TextView) view.findViewById(R.id.aa_day_of_week);
            dayOfTheMonthTextView = (TextView) view.findViewById(R.id.aa_day_of_month);
            monthTextView = (TextView) view.findViewById(R.id.aa_month);
            yearTextView = (TextView) view.findViewById(R.id.aa_year);
        }
    }
    // --------------------------------------------------------------

    private final int PANEL_HOME = 0;
    private final int PANEL_MESSAGE = 1;
    private final int PANEL_NOTES = 2;
    private final int PANEL_APPOINTMENT = 3;
    private final int PANEL_APPOINTMENT_ADD = 4;

    private DataUtil dataUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment_settings);
        dataUtil = new DataUtil(this, ActivityAppointmentSetting.class.getSimpleName());
        initViews();
        week();
        dateOfWeek();
        setSpinner();

        setButtons();

        mCalendarDataList.clear();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_REQUEST_LOCATION_STRING, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getSchedule();
        }
    }

    private void getSchedule() {
        // Get From Server
        if (getLocation()) {

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CalGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "GetMonth" +
                            "&SetByID=" + appSettings.getWorkid() +
                            "&ApptWithMLID=" + "0" +
                            "&ApptID=" + "0" +
                            "&LocLat=" + "0" +
                            "&LocLon=" + "0" +
                            "&DateStart=" + "2021-01-01 00:00:00" +
                            "&DateEnd=" + "2021-12-31 00:00:00";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();

            GoogleCertProvider.install(mContext);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();
                    Log.e(TAG, "onSuccess: " + response);
                    try {

                        JSONArray responseArray = new JSONArray(response);

                        JSONObject responseObject = responseArray.getJSONObject(0);
                        if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                            showAlert(responseObject.getString("msg"));
                        } else {
                            ArrayList<CalendarData.Data> scheduleData = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < responseArray.length(); i++) {
                                CalendarData.Data newItem = gson.fromJson(responseArray.getString(i), CalendarData.Data.class);
                                scheduleData.add(newItem);
                            }

                            CalendarData.setCalendarDataList(scheduleData);

                            mCalendarDataList.clear();
                            for (CalendarData.Data calendarData2 : CalendarData.getCalendarDataList()) {
                                /*if (isSameDate(getSelectedTodayDateCalendar(selectedCalendarObject, 0).getTimeInMillis(),
                                getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd HH:mm:ss"))) {
                                    mCalendarDataList.add(calendarData2);
                                }*/

                                if (isSameDate(getSelectedTodayDateCalendar(selectedCalendarObject, 0).getTimeInMillis(),
                                        getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd"))) {
                                    mCalendarDataList.add(calendarData2);
                                }
                            }
                            setDates(Calendar.getInstance());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
        }
    }

    private long getTimeInMilliseconds(String dateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isSameDate(long selectedDate, long positionDate) {

        Calendar selectedCal = Calendar.getInstance();
        Calendar positionCal = Calendar.getInstance();

        selectedCal.setTimeInMillis(selectedDate);
        positionCal.setTimeInMillis(positionDate);

        if (selectedCal.get(Calendar.YEAR) == positionCal.get(Calendar.YEAR)
                && selectedCal.get(Calendar.MONTH) == positionCal.get(Calendar.MONTH)
                && selectedCal.get(Calendar.DAY_OF_MONTH) == positionCal.get(Calendar.DAY_OF_MONTH)
        ) {
            return true;
        } else {
            return false;
        }
    }

    private void initViews() {
        appointmentWeekSelector = (Spinner) findViewById(R.id.appointmentWeekSelector);
        appointmentSelectedDateTextView = (TextView) findViewById(R.id.appointmentSelectedDate);
        appointmentRecyclerView = (RecyclerView) findViewById(R.id.appointment_recyclerview);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        appointmentRecyclerView.setHasFixedSize(true);
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityAppointmentSetting.this));

        mCalendarDataList.clear();

        // Clear Form
        CalendarData.resetCalendarDataList();

        for (CalendarData.Data calendarData2 : CalendarData.getCalendarDataList()) {
            /*if (isSameDate(getSelectedTodayDateCalendar(selectedCalendarObject, 0).getTimeInMillis(),
                    getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd HH:mm:ss"))) {
                mCalendarDataList.add(calendarData2);
            }*/

            if (isSameDate(getSelectedTodayDateCalendar(selectedCalendarObject, 0).getTimeInMillis(),
                    getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd"))) {
                mCalendarDataList.add(calendarData2);
            }
        }
        adapter = new ScheduleAdapter(ActivityAppointmentSetting.this, new ScheduleAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == -1) {
                    appointmentSelectedDateTextView.setText("");
                } else {
                    CalendarData.Data eventData = mCalendarDataList.get(position);
                    String infoString = String.format("%s", eventData.getTitle());
                    appointmentSelectedDateTextView.setText(infoString);
                }
            }
        });
        appointmentRecyclerView.setAdapter(adapter);
        adapter.notifyDataChanged(mCalendarDataList);
    }

    private void setSpinner() {
        for (int i = 1; i < 7 * 53; i += 7) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_YEAR, i);

            calendarObject.add(cal);
            appointmentWeekSelectorStringList.add(getAllWeekRange(cal));
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, R.layout.z_spinner_item/*android.R.layout.simple_spinner_item*/,
                appointmentWeekSelectorStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appointmentWeekSelector.setAdapter(adapter);

        final Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < appointmentWeekSelectorStringList.size(); i++) {
            if (getAllWeekRange(calendar).trim().toString().equalsIgnoreCase(appointmentWeekSelectorStringList.get(i).trim().toString())) {
                appointmentWeekSelector.setSelection(i);
                break;
            }
        }

        appointmentWeekSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setDates(calendarObject.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setButtons() {
        findViewById(R.id.btnMessage).setOnClickListener(this);
        findViewById(R.id.btnCall).setOnClickListener(this);
        // findViewById(R.id.btnEmail).setOnClickListener(this);
        findViewById(R.id.btnLocation).setOnClickListener(this);
        findViewById(R.id.btnReschedule).setOnClickListener(this);
        findViewById(R.id.btnPay).setOnClickListener(this);
        findViewById(R.id.btnNote).setOnClickListener(this);
        findViewById(R.id.btnPhone).setOnClickListener(this);
        findViewById(R.id.btnWebsite).setOnClickListener(this);

        panelNotes = findViewById(R.id.panelNotes);
        tvNotes = findViewById(R.id.tvNotes);
        edtNote = findViewById(R.id.edtNotes);
        findViewById(R.id.btnCancelNote).setOnClickListener(this);
        findViewById(R.id.btnAddNote).setOnClickListener(this);
        panelNotes.setVisibility(View.GONE);

        panelMessages = findViewById(R.id.panelMessage);
        tvReceivedMessages = findViewById(R.id.tvReceivedMessages);
        svMessages = findViewById(R.id.svMessages);

        edtMsgSend = findViewById(R.id.edtMsgSend);
        findViewById(R.id.btnDoneMsg).setOnClickListener(this);
        findViewById(R.id.btnSendMsg).setOnClickListener(this);
        panelMessages.setVisibility(View.GONE);

        panelAppointment = findViewById(R.id.panelAppointment);
        tvRescheduleTitle = findViewById(R.id.tvRescheduleTitle);
        tvApptTitle = findViewById(R.id.tvApptTitle);

        panelNewApptInfo = findViewById(R.id.panelNewApptInfo);

        // Read All contact information
        ContactsDataSource contactsDataSource = new ContactsDataSource(mContext);
        contactsDataSource.open();
        contactInfoArrayList = contactsDataSource.getAllUserInfo();
        contactsDataSource.close();

        edtApptContact = findViewById(R.id.edtApptContact);
        contactSearchAdapter = new ContactSearchAdapter(mContext, R.layout.layout_spinner_contact, contactInfoArrayList);
        edtApptContact.setAdapter(contactSearchAdapter);
        /*edtApptContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtApptContact.setText(contactSearchAdapter.getSuggestionItemValue(position));
            }
        });*/

        edtApptTitle = findViewById(R.id.edtApptTitle);


        mTimesGridView = (GridView) findViewById(R.id.aa_time_grid_layout);
        previousDateImageView = (ImageView) findViewById(R.id.aa_date_previous);
        nextDateImageView = (ImageView) findViewById(R.id.aa_date_next);

        TimedGridViewAdapter timedGridViewAdapter = new TimedGridViewAdapter(mContext);
        mTimesGridView.setAdapter(timedGridViewAdapter);

        mTimesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get Date & Time
                String timeString = "";
                String AM_PM = "AM";
                int time = 9 + position / 4;
                int mins = (position % 4) * 15;

                if (time >= 12) {
                    AM_PM = "PM";
                    if (time > 12) {
                        time -= 12;
                    }
                }

                timeString = selectedDate + String.format(" %02d:%02d %s", time, mins, AM_PM);

                // Show Dialog for items
                showConfirmationAlertDialog(timeString);
            }
        });

        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_first));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_second));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_third));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fourth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fifth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_sixth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_seventh));

        Calendar calendar = Calendar.getInstance();
        isCurrentDateSetForReschedule = false;
        for (int i = 0; i < dateViewHolderList.size(); i++) {
            setDatesItem(calendar, i);
        }

        panelAppointment.setVisibility(View.GONE);

        // Register Receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter("hawaiiappbuilders.Omni.newmessage"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister Receiver
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(msgReceiver);
    }

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("from");
            String message = intent.getStringExtra("message");

            String txtMsg = tvReceivedMessages.getText().toString().trim();

            StringBuilder msgBuilder = new StringBuilder();

            if (!TextUtils.isEmpty(txtMsg)) {
                msgBuilder.append(txtMsg);
                msgBuilder.append("\n");
            }

            msgBuilder.append("From: " + from);
            msgBuilder.append("\n");
            msgBuilder.append(message);

            tvReceivedMessages.setText(msgBuilder.toString());
        }
    };

    private DateViewHolder initDatesItemViews(int resourceId) {
        View view = (View) findViewById(resourceId);
        return new DateViewHolder(view);
    }

    boolean isCurrentDateSetForReschedule = false;

    private void setDatesItem() {
        Calendar calendar = Calendar.getInstance();
        isCurrentDateSetForReschedule = false;
        for (int i = 0; i < dateViewHolderList.size(); i++) {
            setDatesItem(calendar, i);
        }
    }

    private void setDatesItem(final Calendar calendar, final int i) {
        dateViewHolderList.get(i).dayOfTheWeekTextView.setText(getWeekDate(calendar, weekCount + i, DAY_OF_WEEK_PATTERN));
        dateViewHolderList.get(i).dayOfTheMonthTextView.setText(getWeekDate(calendar, weekCount + i, DAY_OF_MONTH_PATTERN));
        dateViewHolderList.get(i).monthTextView.setText(getWeekDate(calendar, weekCount + i, MONTH_PATTERN));
        dateViewHolderList.get(i).yearTextView.setText(getWeekDate(calendar, weekCount + i, YEAR_PATTERN));

        if (getWeekDateCalendar(calendar, weekCount + i).get(Calendar.YEAR) == getTodayCalendar().get(Calendar.YEAR)
                && getWeekDateCalendar(calendar, weekCount + i).get(Calendar.MONTH) == getTodayCalendar().get(Calendar.MONTH)
                && getWeekDateCalendar(calendar, weekCount + i).get(Calendar.DAY_OF_MONTH) == getTodayCalendar().get(Calendar.DAY_OF_MONTH)) {

            for (int k = 0; k < i; k++) {
                paintUnSelected(dateViewHolderList.get(k));
            }
            paintSelected(dateViewHolderList.get(i));

            String weekDate = getWeekDate(calendar,
                    weekCount + i,
                    DAY_OF_WEEK_PATTERN
                            + " " + DAY_OF_MONTH_PATTERN
                            + " " + MONTH_PATTERN
                            + " " + YEAR_PATTERN);

            selectedDate = getWeekDate(calendar,
                    weekCount + i,
                    MONTH_PATTERN
                            + " " + DAY_OF_MONTH_PATTERN);

            isCurrentDateSetForReschedule = true;
        } else {
            if (!isCurrentDateSetForReschedule && i == dateViewHolderList.size() - 1) {
                dateViewHolderList.get(0).dayOfTheMonthTextView.performClick();
            }

        }

        dateViewHolderList.get(i).dayOfTheMonthTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String weekDate = getWeekDate(calendar,
                                weekCount + i,
                                DAY_OF_WEEK_PATTERN
                                        + " " + DAY_OF_MONTH_PATTERN
                                        + " " + MONTH_PATTERN
                                        + " " + YEAR_PATTERN
                        );

                        selectedDate = getWeekDate(calendar,
                                weekCount + i,
                                MONTH_PATTERN
                                        + " " + DAY_OF_MONTH_PATTERN);

                        for (int j = 0; j < dateViewHolderList.size(); j++) {
                            if (j == i) {
                                paintSelected(dateViewHolderList.get(j));
                            } else {
                                paintUnSelected(dateViewHolderList.get(j));
                            }
                        }
                    }
                });
    }

    private void paintSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.dayOfTheMonthTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.monthTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.yearTextView.setTextColor(getResources().getColor(R.color.white));

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(getResources().getColor(R.color.app_grey));
        /*dateViewHolder.dayOfTheWeekTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.dayOfTheMonthTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.monthTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.yearTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));*/
    }

    private void paintUnSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));
        dateViewHolder.dayOfTheMonthTextView.setTextColor(getResources().getColor(R.color.black));
        dateViewHolder.monthTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));
        dateViewHolder.yearTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        /*dateViewHolder.dayOfTheWeekTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.dayOfTheMonthTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.monthTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.yearTextView.setBackgroundColor(getResources().getColor(R.color.white));*/
    }

    private String getWeekDate(Calendar calendar, int addNext, String pattern) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(last.getTime());
    }

    private Calendar getWeekDateCalendar(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        return last;
    }

    // ------------------------------------------------------------------------------------------------------------------------------------

    private void week() {
        View view = (View) findViewById(R.id.symbolOfWeek);
        TextView sun = (TextView) view.findViewById(R.id.sun);
        TextView mon = (TextView) view.findViewById(R.id.mon);
        TextView tue = (TextView) view.findViewById(R.id.tue);
        TextView wed = (TextView) view.findViewById(R.id.wed);
        TextView thu = (TextView) view.findViewById(R.id.thu);
        TextView fri = (TextView) view.findViewById(R.id.fri);
        TextView sat = (TextView) view.findViewById(R.id.sat);


        ImageView sunSelected = (ImageView) view.findViewById(R.id.sunSelected);
        ImageView monSelected = (ImageView) view.findViewById(R.id.monSelected);
        ImageView tueSelected = (ImageView) view.findViewById(R.id.tueSelected);
        ImageView wedSelected = (ImageView) view.findViewById(R.id.wedSelected);
        ImageView thuSelected = (ImageView) view.findViewById(R.id.thuSelected);
        ImageView friSelected = (ImageView) view.findViewById(R.id.friSelected);
        ImageView satSelected = (ImageView) view.findViewById(R.id.satSelected);

        sunSelected.setVisibility(View.GONE);
        monSelected.setVisibility(View.GONE);
        tueSelected.setVisibility(View.GONE);
        wedSelected.setVisibility(View.GONE);
        thuSelected.setVisibility(View.GONE);
        friSelected.setVisibility(View.GONE);
        satSelected.setVisibility(View.GONE);
    }

    private void dateOfWeek() {
        View view = (View) findViewById(R.id.dateOfWeek);

        TextView sun = (TextView) view.findViewById(R.id.sun);
        TextView mon = (TextView) view.findViewById(R.id.mon);
        TextView tue = (TextView) view.findViewById(R.id.tue);
        TextView wed = (TextView) view.findViewById(R.id.wed);
        TextView thu = (TextView) view.findViewById(R.id.thu);
        TextView fri = (TextView) view.findViewById(R.id.fri);
        TextView sat = (TextView) view.findViewById(R.id.sat);

        dateTextViews.add(sun);
        dateTextViews.add(mon);
        dateTextViews.add(tue);
        dateTextViews.add(wed);
        dateTextViews.add(thu);
        dateTextViews.add(fri);
        dateTextViews.add(sat);

        ImageView sunSelected = (ImageView) view.findViewById(R.id.sunSelected);
        ImageView monSelected = (ImageView) view.findViewById(R.id.monSelected);
        ImageView tueSelected = (ImageView) view.findViewById(R.id.tueSelected);
        ImageView wedSelected = (ImageView) view.findViewById(R.id.wedSelected);
        ImageView thuSelected = (ImageView) view.findViewById(R.id.thuSelected);
        ImageView friSelected = (ImageView) view.findViewById(R.id.friSelected);
        ImageView satSelected = (ImageView) view.findViewById(R.id.satSelected);

        sunSelected.setVisibility(View.INVISIBLE);
        monSelected.setVisibility(View.INVISIBLE);
        tueSelected.setVisibility(View.INVISIBLE);
        wedSelected.setVisibility(View.INVISIBLE);
        thuSelected.setVisibility(View.INVISIBLE);
        friSelected.setVisibility(View.INVISIBLE);
        satSelected.setVisibility(View.INVISIBLE);

        selectedImageViews.add(sunSelected);
        selectedImageViews.add(monSelected);
        selectedImageViews.add(tueSelected);
        selectedImageViews.add(wedSelected);
        selectedImageViews.add(thuSelected);
        selectedImageViews.add(friSelected);
        selectedImageViews.add(satSelected);

        setDates(Calendar.getInstance());
    }

    boolean isCurrentDateSet = false;

    private void setDates(final Calendar calendar) {
        isCurrentDateSet = false;
        for (int i = 0; i < dateTextViews.size(); i++) {
            dateTextViews.get(i).setText(getWeekRange(calendar, i));
            dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);

            if (getWeekRangeCalendar(calendar, i).get(Calendar.YEAR) == getTodayCalendar().get(Calendar.YEAR)
                    && getWeekRangeCalendar(calendar, i).get(Calendar.MONTH) == getTodayCalendar().get(Calendar.MONTH)
                    && getWeekRangeCalendar(calendar, i).get(Calendar.DAY_OF_MONTH) == getTodayCalendar().get(Calendar.DAY_OF_MONTH)) {
                dateTextViews.get(i).setTextColor(getResources().getColor(R.color.colorPrimary));
                selectedImageViews.get(i).setVisibility(View.VISIBLE);
                dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_date_circle_white);
                // appointmentSelectedDateTextView.setText(getSelectedTodayDate(calendar, i));

                mCalendarDataList.clear();
                for (CalendarData.Data calendarData2 : CalendarData.getCalendarDataList()) {
                    /*if (isSameDate(getSelectedTodayDateCalendar(calendar, i).getTimeInMillis(),
                            getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd HH:mm:ss"))) {
                        mCalendarDataList.add(calendarData2);
                    }*/

                    if (isSameDate(getSelectedTodayDateCalendar(calendar, i).getTimeInMillis(),
                            getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd"))) {
                        mCalendarDataList.add(calendarData2);
                    }
                }
                adapter.notifyDateChanged(getSelectedTodayDateCalendar(calendar, i), mCalendarDataList);

                isCurrentDateSet = true;
            } else {
                dateTextViews.get(i).setTextColor(getResources().getColor(R.color.white));
                selectedImageViews.get(i).setVisibility(View.INVISIBLE);
                dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);
                if (!isCurrentDateSet && i == dateTextViews.size() - 1) {
                    dateTextViews.get(0).performClick();
                }
            }
            selectedCalendarObject = calendar;
            final int finalI = i;
            dateTextViews.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintSelected(selectedCalendarObject, dateTextViews, selectedImageViews, 7, finalI);
                }
            });

        }
    }

    private void paintSelected(Calendar calendar, List<TextView> textView, List<ImageView> imageView, int size, int selected) {
        for (int i = 0; i < size; i++) {
            textView.get(i).setText(getWeekRange(calendar, i));
            textView.get(i).setTextColor(getResources().getColor(R.color.white));
            textView.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);
            imageView.get(i).setVisibility(View.INVISIBLE);

            if (i == selected) {
                textView.get(i).setTextColor(getResources().getColor(R.color.colorPrimary));
                imageView.get(i).setVisibility(View.VISIBLE);
                textView.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_date_circle_white);
                // appointmentSelectedDateTextView.setText(getSelectedTodayDate(calendar, i));

                mCalendarDataList.clear();
                for (CalendarData.Data calendarData2 : CalendarData.getCalendarDataList()) {
                    /*if (isSameDate(getSelectedTodayDateCalendar(calendar, i).getTimeInMillis(),
                            getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd HH:mm:ss"))) {
                        mCalendarDataList.add(calendarData2);
                    }*/
                    if (isSameDate(getSelectedTodayDateCalendar(calendar, i).getTimeInMillis(),
                            getTimeInMilliseconds(calendarData2.getStartDate(), "yyyy-MM-dd"))) {
                        mCalendarDataList.add(calendarData2);
                    }
                }
                adapter.notifyDateChanged(getSelectedTodayDateCalendar(calendar, i), mCalendarDataList);
            }
        }
    }

    private String getWeekRange(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(last.getTime());
    }

    private Calendar getWeekRangeCalendar(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        SimpleDateFormat df = new SimpleDateFormat("d");
        return last;
    }

    private String getToday() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(cal.getTime());
    }

    private Calendar getTodayCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        return cal;
    }

    private String getSelectedTodayDate(Calendar calendar, int selectedDay) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, selectedDay);

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd");
        return df.format(last.getTime());
    }

    private Calendar getSelectedTodayDateCalendar(Calendar calendar, int selectedDay) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, selectedDay);
        return last;
    }

    private String getAllWeekRange(int addNext) {
        Calendar cal = Calendar.getInstance();
        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(last.getTime());
    }

    private String getAllWeekRange(Calendar cal) {
        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        // print the result
        SimpleDateFormat df = new SimpleDateFormat("MMM dd");
        System.out.println(df.format(first.getTime()) + "-" +
                df.format(last.getTime()));
        return df.format(first.getTime()) + "-" +
                df.format(last.getTime());
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();

        if (viewId == R.id.appointment_back) {
            finish();
            return;
        } else if (viewId == R.id.appointment_add) {
            showPanel(PANEL_APPOINTMENT_ADD);
            return;
        } else if (viewId == R.id.aa_date_previous) {
            weekCount -= weekCountIncrementDecrement;
            setDatesItem();
            return;
        } else if (viewId == R.id.aa_date_next) {
            weekCount += weekCountIncrementDecrement;
            setDatesItem();
            return;
        } else if (viewId == R.id.btnCloseApptTimePanel) {
            showPanel(PANEL_HOME);
            return;
        } else if (viewId == R.id.panelApptment || viewId == R.id.panelAppointment) {
            // This is for masking the buttons action.
            return;
        }

        // Other buttons, all action should be done after user select an item
        if (adapter.getSelectedItem() == null) {
            showToastMessage("Please Select an Item First");
            return;
        }

        if (viewId == R.id.btnMessage) {
            CalendarData.Data selectedItem = adapter.getSelectedItem();
            if (selectedItem.getOrderId() == 0) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", selectedItem.getCp(), null));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            } else {
                showPanel(PANEL_MESSAGE);
            }
        } else if (viewId == R.id.btnCall) {
            dialPhoneNumber();
        } else if (viewId == R.id.btnLocation) {
            gotoMap();
        } else if (viewId == R.id.btnReschedule) {
            showPanel(PANEL_APPOINTMENT);
        } else if (viewId == R.id.btnPay) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_pay, null);

            final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            final EditText edtAmt = (EditText) dialogView.findViewById(R.id.edtAmt);

            // Button Actions
            dialogView.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    float emt = 0;
                    String strEmt = edtAmt.getText().toString().trim();
                    try {
                        emt = Float.parseFloat(strEmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (emt == 0) {
                        edtAmt.setError("Please input correct amount");
                        return;
                    }

                    inputDlg.dismiss();

                    makePayment(emt);
                }
            });

            // Button Actions
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                }
            });

            inputDlg.show();
            inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } else if (viewId == R.id.btnNote) {
            showPanel(PANEL_NOTES);
        } else if (viewId == R.id.btnPhone) {
            selectContact();
        } else if (viewId == R.id.btnWebsite) {
            openBrowser();
        } else if (viewId == R.id.btnCancelNote) {
            showPanel(PANEL_HOME);
        } else if (viewId == R.id.btnAddNote) {
            addNotes();
        } else if (viewId == R.id.btnDoneMsg) {
            showPanel(PANEL_HOME);
        } else if (viewId == R.id.btnSendMsg) {
            sendMsg();
        }
    }

    // Show relevant panel
    private void showPanel(int panelID) {
        if (panelID == PANEL_MESSAGE) {
            panelMessages.setVisibility(View.VISIBLE);
            panelNotes.setVisibility(View.GONE);
            panelAppointment.setVisibility(View.GONE);
        } else if (panelID == PANEL_NOTES) {
            panelMessages.setVisibility(View.GONE);
            panelNotes.setVisibility(View.VISIBLE);
            panelAppointment.setVisibility(View.GONE);

            getNotes();
        } else if (panelID == PANEL_APPOINTMENT) {
            panelMessages.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelAppointment.setVisibility(View.VISIBLE);

            isNewAppt = false;
            tvRescheduleTitle.setVisibility(View.VISIBLE);

            CalendarData.Data selectedItem = adapter.getSelectedItem();
            if (selectedItem == null) {
                tvApptTitle.setText("Event Title");
            } else {
                String infoString = String.format("%s", selectedItem.getTitle());
                tvApptTitle.setText(infoString);
            }

            tvApptTitle.setVisibility(View.VISIBLE);
            panelNewApptInfo.setVisibility(View.GONE);
        } else if (panelID == PANEL_APPOINTMENT_ADD) {
            panelMessages.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelAppointment.setVisibility(View.VISIBLE);

            isNewAppt = true;
            tvRescheduleTitle.setVisibility(View.GONE);

            panelNewApptInfo.setVisibility(View.VISIBLE);
            tvApptTitle.setVisibility(View.GONE);
        } else {
            panelMessages.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelAppointment.setVisibility(View.GONE);
        }
    }

    public void dialPhoneNumber() {
        CalendarData.Data selectedItem = adapter.getSelectedItem();
        if (selectedItem == null) {
            showToastMessage("Please select item in the list");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + selectedItem.getCp()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void gotoMap() {
        CalendarData.Data selectedAppt = adapter.getSelectedItem();

        String mapUrl = "";
        // "geo:37.7749,-122.4194?z=12"

        if (selectedAppt.getApptLat() != 0 || selectedAppt.getApptLon() != 0) {
            mapUrl = String.format("geo:%f,%f?q=%f,%f&z=12", selectedAppt.getApptLat(), selectedAppt.getApptLon(), selectedAppt.getApptLat(), selectedAppt.getApptLon());
        } else if (selectedAppt.getSellerLat() != 0 || selectedAppt.getSellerLon() == 0) {
            mapUrl = String.format("geo:%f,%f?q=%f,%f&z=12", selectedAppt.getSellerLat(), selectedAppt.getSellerLon(), selectedAppt.getSellerLat(), selectedAppt.getSellerLon());
        } else {
            mapUrl = String.format("geo:%s,%s?z=12", getUserLat(), getUserLon());
        }

        // Creates an Intent that will load a map of San Francisco
        Uri gmmIntentUri = Uri.parse(mapUrl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        //if (mapIntent.resolveActivity(getPackageManager()) != null) {
        startActivity(mapIntent);
        //} else {
        //    showToastMessage("Please install Google Map to open location");
        //}
    }

    private void openBrowser() {
        openLink("http://www.google.com");
    }

    private void openMailBox() {
        CalendarData.Data selectedItem = adapter.getSelectedItem();
        if (selectedItem == null) {
            showToastMessage("Please select item in the list");
            return;
        }

        String[] supportTeamAddrs = {selectedItem.getEmail()};
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent == null) {
            intent = new Intent(Intent.ACTION_SENDTO);
        }
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mahalo Pay");
        intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nPowered by Mahalo Pay");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToastMessage("Please install Email App to use function");
        }
    }

    static final int REQUEST_SELECT_CONTACT = 100;
    String[] permissionsForContact = new String[]{Manifest.permission.READ_CONTACTS};
    static final int PERMISSION_REQUEST_CONTACT = 500;

    public void selectContact() {
        if (checkPermissions(mContext, permissionsForContact, true, PERMISSION_REQUEST_CONTACT)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_SELECT_CONTACT);
            }

            /*ContactsDataSource contactsDataSource = new ContactsDataSource(mContext);
            contactsDataSource.open();
            long cntRecords = contactsDataSource.getContactRecordsCount();
            contactsDataSource.close();*/

            if (contactInfoArrayList.isEmpty()) {
                mMyApp.getContactList(contactInfoArrayList);
            }
        }
    }

    public void viewContact(Uri contactUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, contactUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private String displayName(Uri contactUri) {

        StringBuilder stringBuilder = new StringBuilder();

        Log.d("ITEM", contactUri.toString());
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor queryCursor = getContentResolver()
                .query(contactUri, null, null, null, null);
        queryCursor.moveToFirst();
        String name = queryCursor.getString(queryCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String id = queryCursor.getString(
                queryCursor.getColumnIndex(ContactsContract.Contacts._ID));

        stringBuilder.append(String.format("User name : %s\n", name));

        if (Integer.parseInt(queryCursor.getString(queryCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            Cursor pCur = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);

            int cntPhone = pCur.getCount();
            int currentIndex = 1;
            while (pCur.moveToNext()) {
                int number = pCur.getInt(pCur.getColumnIndex("data1"));
                Log.d("Contact Name: ", number + "");

                if (cntPhone == 1) {
                    stringBuilder.append(String.format("Phone : %s", number));
                } else {
                    stringBuilder.append(String.format("Phone%d : %s\n", currentIndex, number));
                    currentIndex++;
                }
            }
            pCur.close();
        }

        return stringBuilder.toString();
    }

    private void getNotes() {

        CalendarData.Data selectedItem = adapter.getSelectedItem();
        if (selectedItem == null) {
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
                String extraParams =
                        "&mode=" + "getNotes" +
                                "&tiedToMLID=" + selectedItem.getBuyerId() +
                                "&tiedtoLDBID=" + "-1" +
                                "&note=" + "";
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

                        Log.e("getNotes", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert(jsonObject.getString("msg"));
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();

                                    Gson gson = new Gson();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Note newItem = gson.fromJson(jsonArray.getString(i), Note.class);
                                        stringBuilder.append(DateUtil.toStringFormat_21(DateUtil.parseDataFromFormat20(newItem.getCreateDate()))).append("\n");
                                        stringBuilder.append(newItem.getNote()).append("\n");
                                    }

                                    tvNotes.setText(stringBuilder.toString());
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
                    dataUtil.setActivityName(ActivityAppointmentSetting.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "allNotes");
                }
            }
        }
    }

    private void addNotes() {

        CalendarData.Data selectedItem = adapter.getSelectedItem();
        if (selectedItem == null) {
            showToastMessage("Please select item in the list");
            return;
        }

        String notes = edtNote.getText().toString().trim();
        hideKeyboard(edtNote);

        if (TextUtils.isEmpty(notes)) {
            showToastMessage("Please input notes");
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
                String extraParams =
                        "&mode=" + "addNote" +
                                "&tiedToMLID=" + selectedItem.getBuyerId() +
                                "&tiedtoLDBID=" + "-1" +
                                "&note=" + notes;
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

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert(jsonObject.getString("msg"));
                                } else {
                                    showToastMessage("Successfully added your notes");
                                    edtNote.setText("");
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
                    dataUtil.setActivityName(ActivityAppointmentSetting.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "allNotes");
                }
            }
        }
    }

    private void sendMsg() {
        CalendarData.Data selectedItem = adapter.getSelectedItem();
        if (selectedItem == null) {
            showToastMessage("Please select item in the list");
            return;
        }

        final String message = edtMsgSend.getText().toString().trim();
        hideKeyboard(edtMsgSend);

        if (TextUtils.isEmpty(message)) {
            showToastMessage("Please input message");
            return;
        }

        if (getLocation()) {

            try {
                String mode;
                if (selectedItem.getOrderId() > 0) {
                    mode = "POS";
                } else {
                    mode = "AVA";
                }

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLGetToken",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + mode +
                                "&TokenMLID=" + selectedItem.getApptWithMLID();
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

                        Log.e("Token", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                if (jsonArray.length() == 0) {
                                    showToastMessage("No TOKENs");
                                    return;
                                }

                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showToastMessage("No TOKENs");
                                } else {
                                    // XiaoPOS
                                    // tokenList.add("eC3BilkANcA:APA91bEHuVn-wDDZyuPwFWgpsFHOJmbVZC99IJiggALUJEJFxgFhQfO1SmDFw9tNKgc7ny151n1j1jgqdZPjH5QhpHTxqCwkJLmPJG43cPYkdYpKrGiIGPgA_VYJEnrjUZdvdZ7qrOgC");

                                    // XiaoAVA
                                    // tokenList.add("eksXBxmVKWk:APA91bECYgmcjo4xrwc0MV8-dprQrBWCQy7rUeDsYkOf2a6owxdTX6bWaAzT4shksUtfGIsMlX0FApcdf0R_gtOomfrFvsorou_7mlIZpo6aAiEHVzsnyfIBbscdEVHk0DhsT_SvHgNh");

                                    // ChuckAVA
                                    // tokenList.add("cPEUwdOq6bA:APA91bGxiVpjqbObNAKrVQ0G5BM_COpdm6-xJoilcKsKRJ_V5Ne6UAEehKzN_EreEMXTns1zo-F1SBx3k2XT80FGmsMfBKuY-vm66N4GO44-JFHzfQ59j28o0pFbqR1kGn_m7KWGJFzk");
                                    NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                    ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                    if (!tokenList.isEmpty()) {
                                        JSONObject payload = new JSONObject();
                                        // payload.put("orderId", "1");
                                        // payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("CALID", selectedItem.getCalId());
                                        payload.put("SenderCP", appSettings.getCP());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Text_Message, payload);
                                    }
                                    showToastMessage("Sent Message!");
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

                sr.setRetryPolicy(new DefaultRetryPolicy(
                        25000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                sr.setShouldCache(false);
                queue.add(sr);
            }catch (Exception e){
                if (dataUtil!=null) {
                    dataUtil.setActivityName(ActivityAppointmentSetting.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLGetToken");
                }
            }
        }
    }

    private void makePayment(final float amt) {
        if (getLocation()) {
            CalendarData.Data calcData = adapter.getSelectedItem();

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CalPay",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "" +
                            "&calID=" + calcData.getCalId() +
                            "&SetByID=" + appSettings.getUserId() +
                            "&Amt=" + String.valueOf(amt) +
                            "&payID=" + calcData.getApptWithMLID();
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

                    Log.e("CalPay", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert(jsonObject.getString("msg"));
                                } else {

                                    showToastMessage("Success your payment!");

                                    // [{"CP":"(719) 963-3121","Token":"cPEUwdOq6bA:APA91bGxiVpjqbObNAKrVQ0G5BM_COpdm6-xJoilcKsKRJ_V5Ne6UAEehKzN_EreEMXTns1zo-F1SBx3k2XT80FGmsMfBKuY-vm66N4GO44-JFHzfQ59j28o0pFbqR1kGn_m7KWGJFzk"}]

                                    // XiaoPOS
                                    //tokenList.add("eC3BilkANcA:APA91bEHuVn-wDDZyuPwFWgpsFHOJmbVZC99IJiggALUJEJFxgFhQfO1SmDFw9tNKgc7ny151n1j1jgqdZPjH5QhpHTxqCwkJLmPJG43cPYkdYpKrGiIGPgA_VYJEnrjUZdvdZ7qrOgC");

                                    // XiaoAVA
                                    // tokenList.add("eksXBxmVKWk:APA91bECYgmcjo4xrwc0MV8-dprQrBWCQy7rUeDsYkOf2a6owxdTX6bWaAzT4shksUtfGIsMlX0FApcdf0R_gtOomfrFvsorou_7mlIZpo6aAiEHVzsnyfIBbscdEVHk0DhsT_SvHgNh");

                                    NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                    ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                    if (!tokenList.isEmpty()) {
                                        JSONObject payload = new JSONObject();
                                        payload.put("message", "You got payment");
                                        payload.put("orderId", "1");
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Funds_Sent, payload);
                                    }
                                }
                            } else {
                                // Show Alert
                                showAlert("Not able to contact the Attendee using Notifications.\n" +
                                        "You might want to call them.");
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
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_invalid_credentials);
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
        }

        /*// Show Pin Input Dialog
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView dialog_title = alertLayout.findViewById(R.id.dialog_title);
        dialog_title.setText("Payment PIN required");

        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        cpin.setVisibility(View.GONE);

        final Button cancel = alertLayout.findViewById(R.id.pin_cancel);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Make Payment");



        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumber = pin.getText().toString().trim();
                hideKeyboard();
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    return;
                }

                AppSettings appSettings = new AppSettings(mContext);
                String userPin = appSettings.getPIN().trim();
                if (userPin.equalsIgnoreCase(pinNumber)) {
                    dialog.dismiss();


                } else {
                    pin.setError("Wrong PIN");
                }
            }
        });*/
    }

    private void showConfirmationAlertDialog(final String timeString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

        String message = "";
        if (isNewAppt) {
            message = timeString + "\n" + "Sending schedule change request";
        } else {
            CalendarData.Data selectedItem = adapter.getSelectedItem();
            message = "Are you requesting to \n";
            message += "rescheduling your appointment?\n";
            message += "From " + DateUtil.toStringFormat_19(DateUtil.parseDataFromFormat20(selectedItem.getStartDate())) + "\n";
            message += "To " + timeString;
        }

        builder.setTitle("Please confirm")
                .setMessage(message)
                .setCancelable(false)
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        reschedule(timeString);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showPanel(PANEL_HOME);
                    }
                });

        builder.create().show();
    }

    private void reschedule(String time) {
        // MMM dd hh:mm a => yyyy mm dd HH:mm
        Date apptDate = DateUtil.parseDataFromFormat19(time);
        Calendar calAppt = Calendar.getInstance();
        calAppt.setTime(apptDate);

        // Match Appointment year
        Calendar nowTime = Calendar.getInstance();
        calAppt.set(Calendar.YEAR, nowTime.get(Calendar.YEAR));


        time = DateUtil.toStringFormat_7(calAppt.getTime());

        hideKeyboard(edtApptContact);
        hideKeyboard(edtApptTitle);
        String eventContact = edtApptContact.getText().toString().trim();
        String eventTitle = edtApptTitle.getText().toString().trim();

        ContactInfo selectedContactInfo = null;
        // Search Contact
        boolean isForEmail = false;

        if (isNewAppt) {
            if (TextUtils.isEmpty(eventContact)) {
                showToastMessage("Please input attendee information");
                return;
            }

            if (TextUtils.isEmpty(eventTitle)) {
                showToastMessage("Please input event title");
                return;
            }

            String userAttendee = eventContact.replace(" ", "");
            userAttendee = userAttendee.replace("-", "");
            userAttendee = userAttendee.replace("+", "");
            userAttendee = userAttendee.replace("(", "");
            userAttendee = userAttendee.replace(")", "");

            // Check Attendee is email or not
            if (userAttendee.matches("[0-9]+") && userAttendee.length() > 1) {
                isForEmail = false;
            } else {
                isForEmail = true;
            }

            for (ContactInfo contactInfo : contactInfoArrayList) {
                if (isForEmail && contactInfo.getEmailData().contains(eventContact)) {
                    selectedContactInfo = contactInfo;
                    break;
                } else if (!isForEmail && (contactInfo.getPhoneData().contains(eventContact) || contactInfo.getPhoneMetaData().contains(eventContact))) {
                    selectedContactInfo = contactInfo;
                    break;
                }
            }

            /*if (selectedContactInfo != null && TextUtils.isEmpty(selectedContactInfo.getMlid())) {
                selectedContactInfo.setMlid(0);
            }*/
        }

        if (getLocation()) {
            // TODO: to be converted to setAppt
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CalResch",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String paramTitle, paramEmail, paramCp;
            long paramCalId;
            int paramAttendeeMlid;
            if (isNewAppt) {
                paramTitle = eventTitle;
                if (isForEmail) {
                    paramEmail = eventContact;
                    paramCp = "";
                } else {
                    paramEmail = "";
                    paramCp = eventContact;
                }

                if (selectedContactInfo != null) {
                    paramAttendeeMlid = selectedContactInfo.getMlid();
                } else {
                    paramAttendeeMlid = 0;
                }
                paramCalId = 0;
            } else {
                CalendarData.Data calcData = adapter.getSelectedItem();
                paramTitle = calcData.getTitle();
                paramEmail = calcData.getEmail();
                paramCp = calcData.getCp();
                paramAttendeeMlid = calcData.getApptWithMLID();
                paramCalId = calcData.getCalId();
            }

            String extraParams =
                    "&mode=" + "" +
                            "&mins=" + "0" +
                            "&DateStart=" + time +  // MMM dd h:mm a
                            "&title=" + paramTitle +
                            "&email=" + paramEmail +
                            "&cp=" + paramCp +
                            "&attendeeMLID=" + paramAttendeeMlid +
                            "&calID=" + paramCalId;
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            ContactInfo finalSelectedContactInfo = selectedContactInfo;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("CalResch", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showAlert(jsonObject.getString("msg"));
                            } else {
                                edtApptContact.setText("");
                                edtApptTitle.setText("");

                                showPanel(PANEL_HOME);

                                // Get new MLID and update it in local db
                                if (finalSelectedContactInfo != null && jsonObject.has("MLID")) {
                                    int mlid = jsonObject.getInt("MLID");
                                    finalSelectedContactInfo.setMlid(mlid);

                                    ContactsDataSource contactsDataSource = new ContactsDataSource(mContext);
                                    contactsDataSource.open();
                                    contactsDataSource.updateUserInfo(finalSelectedContactInfo);
                                    contactsDataSource.close();
                                }

                                if (isNewAppt) {
                                    showToastMessage("Successfully added new Appointment");
                                } else {
                                    showToastMessage("Success Reschedule!");

                                    // Get PM Tokens and send PM
                                    String token = jsonObject.optString("Token");
                                    if (jsonObject.has("oToken") && !jsonObject.isNull("oToken")) {
                                        String oToken = jsonObject.getString("oToken");
                                        if (!TextUtils.isEmpty(oToken)) {
                                            token = oToken;
                                        }
                                    }

                                    if (!TextUtils.isEmpty(token)) {
                                        NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                        tokenList.add(new FCMTokenData(token, OS_UNKNOWN));
                                        JSONObject payload = new JSONObject();
                                        payload.put("message", "Important ! Reschedule Requested");
                                        payload.put("orderId", "1");
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Reschedule_Appointment, payload);
                                        //tokenList.add("eksXBxmVKWk:APA91bECYgmcjo4xrwc0MV8-dprQrBWCQy7rUeDsYkOf2a6owxdTX6bWaAzT4shksUtfGIsMlX0FApcdf0R_gtOomfrFvsorou_7mlIZpo6aAiEHVzsnyfIBbscdEVHk0DhsT_SvHgNh");
                                    }
                                }
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
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_invalid_credentials);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_CALENDAR && resultCode == RESULT_OK) {
            getSchedule();
        } else if (requestCode == GpsTracker.REQUEST_LOCATION && resultCode == RESULT_OK) {
            getSchedule();
        } else if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();

            msg("Contact Info", displayName(contactUri));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Check All Permission was granted
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
                getSchedule();
            } else if (requestCode == PERMISSION_REQUEST_CONTACT) {
                selectContact();
            }
        } else {
            showAlert("Need permissions to use function.");
        }
    }
}