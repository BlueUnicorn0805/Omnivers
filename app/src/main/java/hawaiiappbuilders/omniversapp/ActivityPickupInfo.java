package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.PickupAndApptInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class ActivityPickupInfo extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityPickupInfo.class.getSimpleName();

    // Pickup Fields
    TextView tvPUDate;
    TextView tvPUTime;
    Calendar calendarPUTime;

    Spinner spinnerVehicles;
    ArrayList<String> vehicleType = new ArrayList<>();
    ArrayList<String> vehicleIds = new ArrayList<>();
    CheckBox chkOneWayTrip;

    EditText edtPUCompany;
    EditText edtPUStreetNum;
    EditText edtPUStreet;
    EditText edtPUSuite;
    EditText edtPUCity;
    EditText edtPUState;
    EditText edtPUZip;
    EditText edtPUPhone;
    EditText edtPUNote;

    // Appointment Fields
    TextView tvDate;
    TextView tvTime;
    Calendar calendarTime;

    EditText edtCompany;
    EditText edtStreetNum;
    EditText edtStreet;
    EditText edtSuite;
    EditText edtCity;
    EditText edtState;
    EditText edtZip;
    EditText edtPhone;
    EditText edtNote;

    CheckBox chkMon;
    CheckBox chkTue;
    CheckBox chkWed;
    CheckBox chkThu;
    CheckBox chkFri;
    CheckBox chkSat;
    CheckBox chkSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pickupinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pickup & Appointment Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        Restaurant resInfo = intent.getParcelableExtra("res_info");
        String dateTime = intent.getStringExtra("datetime");
        Date initTime = DateUtil.parseDataFromFormat19(dateTime);

        // Pickup Info Fields
        tvPUDate = findViewById(R.id.tvPUDate);
        tvPUTime = findViewById(R.id.tvPUTime);
        calendarPUTime = Calendar.getInstance();

        tvPUDate.setOnClickListener(this);
        tvPUTime.setOnClickListener(this);

        spinnerVehicles = findViewById(R.id.spinnerVehicles);
        String[] vehicleInfoArray = getResources().getStringArray(R.array.spinner_vehicletype);
        for (int i = 0; i < vehicleInfoArray.length; i++) {
            String vehicleInfo = vehicleInfoArray[i];
            String[] spliteValues = vehicleInfo.split("=");
            vehicleType.add(spliteValues[0]);
            vehicleIds.add(spliteValues[1]);
        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, R.layout.layout_spinner_vehicle,
                vehicleType);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicles.setAdapter(spinnerArrayAdapter);

        chkOneWayTrip = findViewById(R.id.chkOneWayTrip);
        edtPUCompany = findViewById(R.id.edtPUCompany);
        edtPUStreetNum = findViewById(R.id.edtPUStreetNum);
        edtPUStreet = findViewById(R.id.edtPUStreet);
        edtPUSuite = findViewById(R.id.edtPUSuite);
        edtPUCity = findViewById(R.id.edtPUCity);
        edtPUState = findViewById(R.id.edtPUState);
        edtPUZip = findViewById(R.id.edtPUZip);
        edtPUPhone = findViewById(R.id.edtPUPhone);
        edtPUNote = findViewById(R.id.edtPUNote);

        edtPUCompany.setText(appSettings.getCompany());
        edtPUStreetNum.setText(appSettings.getStreetNum());
        edtPUStreet.setText(appSettings.getStreet());
        edtPUSuite.setText(appSettings.getSTE());
        edtPUCity.setText(appSettings.getCity());
        edtPUState.setText(appSettings.getSt());
        edtPUZip.setText(appSettings.getZip());
        edtPUPhone.setText(appSettings.getCP());

        // Appointment Fields
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        // Set the Appointment time
        calendarTime = Calendar.getInstance();
        int currYear = calendarTime.get(Calendar.YEAR);
        calendarTime.setTime(initTime);
        calendarTime.set(Calendar.YEAR, currYear);

        // Make 30 mins prior
        calendarPUTime.setTimeInMillis(calendarTime.getTimeInMillis() - 30 * 60 * 1000);

        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);

        edtCompany = findViewById(R.id.edtCompany);
        edtStreetNum = findViewById(R.id.edtStreetNum);
        edtStreet = findViewById(R.id.edtStreet);
        edtSuite = findViewById(R.id.edtSuite);
        edtCity = findViewById(R.id.edtCity);
        edtState = findViewById(R.id.edtState);
        edtZip = findViewById(R.id.edtZip);
        edtPhone = findViewById(R.id.edtPhone);
        edtNote = findViewById(R.id.edtNote);

        if (resInfo != null) {
            edtCity.setText(resInfo.get_city());
            edtState.setText(resInfo.get_st());
            edtZip.setText(resInfo.get_zip());

            String address = resInfo.get_address();
            if (!TextUtils.isEmpty(address)) {
                String[] addrEles = address.split(" ");
                if (addrEles.length == 1) {
                    edtStreet.setText(addrEles[0]);
                } else if (addrEles.length >= 2) {
                    edtStreetNum.setText(addrEles[0]);
                    edtStreet.setText(address.replace("addrEles[0]", "").trim());
                }
            }
        }

        tvPUDate.setText(DateUtil.toStringFormat_1(calendarPUTime.getTime()));
        tvDate.setText(DateUtil.toStringFormat_1(calendarTime.getTime()));
        tvPUTime.setText(DateUtil.toStringFormat_10(calendarPUTime.getTime()));
        tvTime.setText(DateUtil.toStringFormat_10(calendarTime.getTime()));

        chkMon = findViewById(R.id.chkMon);
        chkTue = findViewById(R.id.chkTue);
        chkWed = findViewById(R.id.chkWed);
        chkThu = findViewById(R.id.chkThu);
        chkFri = findViewById(R.id.chkFri);
        chkSat = findViewById(R.id.chkSat);
        chkSun = findViewById(R.id.chkSun);

        findViewById(R.id.btnSubmit).setOnClickListener(this);

        String populatePickupInfo = appSettings.getPopulatePickUp();
        if (!TextUtils.isEmpty(populatePickupInfo)) {
            try {
                JSONObject jsonPickupInfo = new JSONObject(populatePickupInfo);

                spinnerVehicles.setSelection(jsonPickupInfo.optInt("vehicle_type"));
                chkOneWayTrip.setChecked(jsonPickupInfo.optBoolean("oneway_trip"));
                edtPUCompany.setText(jsonPickupInfo.optString("pu_company"));

                edtPUStreetNum.setText(jsonPickupInfo.optString("pu_streetnum"));
                edtPUStreet.setText(jsonPickupInfo.optString("pu_street"));
                edtPUSuite.setText(jsonPickupInfo.optString("pu_suite"));
                edtPUCity.setText(jsonPickupInfo.optString("pu_city"));
                edtPUState.setText(jsonPickupInfo.optString("pu_state"));
                edtPUZip.setText(jsonPickupInfo.optString("pu_zip"));
                edtPUPhone.setText(jsonPickupInfo.optString("pu_phone"));
                edtPUNote.setText(jsonPickupInfo.optString("pu_note"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        String populateAppointment = appSettings.getPopulateAppt();
        if (!TextUtils.isEmpty(populateAppointment)) {
            try {
                JSONObject jsonApptInfo = new JSONObject(populateAppointment);
                edtCompany.setText(jsonApptInfo.optString("company"));

                edtSuite.setText(jsonApptInfo.optString("suite"));
                edtCity.setText(jsonApptInfo.optString("city"));
                edtState.setText(jsonApptInfo.optString("state"));
                edtZip.setText(jsonApptInfo.optString("zip"));
                edtPhone.setText(jsonApptInfo.optString("phone"));
                edtNote.setText(jsonApptInfo.optString("note"));

                chkMon.setChecked(jsonApptInfo.optBoolean("chk_mon"));
                chkTue.setChecked(jsonApptInfo.optBoolean("chk_tue"));
                chkWed.setChecked(jsonApptInfo.optBoolean("chk_wed"));
                chkThu.setChecked(jsonApptInfo.optBoolean("chk_thu"));
                chkFri.setChecked(jsonApptInfo.optBoolean("chk_fri"));
                chkSat.setChecked(jsonApptInfo.optBoolean("chk_sat"));
                chkSun.setChecked(jsonApptInfo.optBoolean("chk_sun"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnSubmit) {
            hideKeyboard(edtPUCompany);
            hideKeyboard(edtPUStreet);
            hideKeyboard(edtPUStreetNum);
            hideKeyboard(edtPUSuite);
            hideKeyboard(edtPUCity);
            hideKeyboard(edtPUState);
            hideKeyboard(edtPUZip);
            hideKeyboard(edtPUPhone);
            hideKeyboard(edtPUNote);

            hideKeyboard(edtCompany);
            hideKeyboard(edtStreet);
            hideKeyboard(edtStreetNum);
            hideKeyboard(edtSuite);
            hideKeyboard(edtCity);
            hideKeyboard(edtState);
            hideKeyboard(edtZip);
            hideKeyboard(edtPhone);
            hideKeyboard(edtNote);

            String puCompany = edtPUCompany.getText().toString().trim();
            String puStreet = edtPUStreet.getText().toString().trim();
            String puStreetNum = edtPUStreetNum.getText().toString().trim();
            String puSuite = edtPUSuite.getText().toString().trim();
            String puCity = edtPUCity.getText().toString().trim();
            String puState = edtPUState.getText().toString().trim();
            String puZip = edtPUZip.getText().toString().trim();
            String puPhone = edtPUPhone.getText().toString().trim();
            String puNotes = edtPUNote.getText().toString().trim();

            if (TextUtils.isEmpty(puStreet) || TextUtils.isEmpty(puStreetNum) || TextUtils.isEmpty(puCity) || TextUtils.isEmpty(puState) || TextUtils.isEmpty(puZip) ) {
                showToastMessage("Please input Pickup fields.");
                return;
            }

            String company = edtCompany.getText().toString().trim();
            String street = edtStreet.getText().toString().trim();
            String streetNum = edtStreetNum.getText().toString().trim();
            String suite = edtSuite.getText().toString().trim();
            String city = edtCity.getText().toString().trim();
            String state = edtState.getText().toString().trim();
            String zip = edtZip.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String notes = edtNote.getText().toString().trim();

            if (TextUtils.isEmpty(street) || TextUtils.isEmpty(streetNum) || TextUtils.isEmpty(city) || TextUtils.isEmpty(state) || TextUtils.isEmpty(zip)) {
                showToastMessage("Please input Appointment fields.");
                return;
            }

            PickupAndApptInfo pickupAndApptInfo = new PickupAndApptInfo();
            pickupAndApptInfo.puDate = DateUtil.toStringFormat_13(calendarPUTime.getTime());
            pickupAndApptInfo.puTime = DateUtil.toStringFormat_10(calendarPUTime.getTime());
            pickupAndApptInfo.puCompany = puCompany;
            pickupAndApptInfo.isOneWayTrip = chkOneWayTrip.isChecked();
            pickupAndApptInfo.puVehicleTypeID = vehicleIds.get(spinnerVehicles.getSelectedItemPosition());
            pickupAndApptInfo.puStreet = puStreet;
            pickupAndApptInfo.puStreetNum = puStreetNum;
            pickupAndApptInfo.puSuite = puSuite;
            pickupAndApptInfo.puCity = puCity;
            pickupAndApptInfo.puState = puState;
            pickupAndApptInfo.puZip = puZip;
            pickupAndApptInfo.puPhone = puPhone;
            pickupAndApptInfo.puNote = puNotes;

            pickupAndApptInfo.date = DateUtil.toStringFormat_13(calendarTime.getTime());
            pickupAndApptInfo.time = DateUtil.toStringFormat_10(calendarTime.getTime());
            pickupAndApptInfo.company = company;
            pickupAndApptInfo.street = street;
            pickupAndApptInfo.streetNum = streetNum;
            pickupAndApptInfo.suite = suite;
            pickupAndApptInfo.city = city;
            pickupAndApptInfo.state = state;
            pickupAndApptInfo.zip = zip;
            pickupAndApptInfo.phone = phone;
            pickupAndApptInfo.note = notes;

            pickupAndApptInfo.repeat = chkMon.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkTue.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkWed.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkThu.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkFri.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkSat.isChecked() ? "1" : "0";
            pickupAndApptInfo.repeat += chkSun.isChecked() ? "1" : "0";


            JSONObject jsonPickUpInfo = new JSONObject();
            try {
                jsonPickUpInfo.put("vehicle_type", spinnerVehicles.getSelectedItem());
                jsonPickUpInfo.put("oneway_trip", chkOneWayTrip.isChecked());
                jsonPickUpInfo.put("pu_company", puCompany);

                jsonPickUpInfo.put("pu_streetnum", puStreetNum);
                jsonPickUpInfo.put("pu_street", puStreet);
                jsonPickUpInfo.put("pu_suite", puSuite);
                jsonPickUpInfo.put("pu_city", puCity);
                jsonPickUpInfo.put("pu_state", puState);
                jsonPickUpInfo.put("pu_zip", puZip);
                jsonPickUpInfo.put("pu_phone", puPhone);
                jsonPickUpInfo.put("pu_note", puNotes);

                appSettings.setPopulatePickUp(jsonPickUpInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject jsonApptInfo = new JSONObject();
            try {
                jsonApptInfo.put("company", company);
                jsonApptInfo.put("suite", suite);
                jsonApptInfo.put("city", city);
                jsonApptInfo.put("state", state);
                jsonApptInfo.put("zip", zip);
                jsonApptInfo.put("phone", phone);
                jsonApptInfo.put("note", notes);

                jsonApptInfo.put("chk_mon", chkMon.isChecked());
                jsonApptInfo.put("chk_tue", chkTue.isChecked());
                jsonApptInfo.put("chk_wed", chkWed.isChecked());
                jsonApptInfo.put("chk_thu", chkThu.isChecked());
                jsonApptInfo.put("chk_fri", chkFri.isChecked());
                jsonApptInfo.put("chk_sat", chkSat.isChecked());
                jsonApptInfo.put("chk_sun", chkSun.isChecked());

                appSettings.setPopulateAppt(jsonApptInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent();
            intent.putExtra("pickup_appt_info", pickupAndApptInfo);
            setResult(RESULT_OK, intent);
            finish();
        } else if(viewId == R.id.tvPUDate) {
            getDate(0);
        } else if(viewId == R.id.tvDate) {
            getDate(1);
        } else if(viewId == R.id.tvPUTime) {
            getTime(0);
        } else if(viewId == R.id.tvTime) {
            getTime(1);
        }
    }

    private void getDate(final int mode) {
        Calendar calendarDate = mode == 0 ? calendarPUTime : calendarTime;
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                if (mode == 0) {
                                    calendarPUTime.set(Calendar.YEAR, year);
                                    calendarPUTime.set(Calendar.MONTH, monthOfYear);
                                    calendarPUTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    String strDate = DateUtil.toStringFormat_1(calendarPUTime.getTime());

                                    tvPUDate.setText(strDate);
                                } else {
                                    calendarTime.set(Calendar.YEAR, year);
                                    calendarTime.set(Calendar.MONTH, monthOfYear);
                                    calendarTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    String strDate = DateUtil.toStringFormat_1(calendarTime.getTime());

                                    tvDate.setText(strDate);
                                }
                            }
                        },
                        calendarDate.get(Calendar.YEAR),
                        calendarDate.get(Calendar.MONTH),
                        calendarDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void getTime(final int mode) {
        Calendar calendarDate = mode == 0 ? calendarPUTime : calendarTime;

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                if (mode == 0) {
                                    calendarPUTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendarPUTime.set(Calendar.MINUTE, minute);
                                    String strDate = DateUtil.toStringFormat_10(calendarPUTime.getTime());

                                    tvPUTime.setText(strDate);
                                } else {
                                    calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendarTime.set(Calendar.MINUTE, minute);
                                    String strDate = DateUtil.toStringFormat_10(calendarTime.getTime());

                                    tvTime.setText(strDate);
                                }
                            }
                        },
                        calendarDate.get(Calendar.HOUR_OF_DAY),
                        calendarDate.get(Calendar.MINUTE),
                        false);
        timePickerDialog.show();
    }
}
