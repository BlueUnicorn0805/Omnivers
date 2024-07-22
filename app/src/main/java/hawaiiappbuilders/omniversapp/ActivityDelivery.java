package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;


import org.json.JSONException;
import org.json.JSONObject;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.location.Constants;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressIntentService;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressResultReceiver;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ActivityDelivery extends BaseActivity implements View.OnClickListener, GeocodeAddressResultReceiver.OnReceiveGeocodeListener {
    public static final String TAG = ActivityDelivery.class.getSimpleName();
    Restaurant restaurant;

    EditText editReceiverName;
    TextView editDate;
    Calendar calendarDOB;
    String strDOB;
    DatePickerDialog.OnDateSetListener dobListener;

    Spinner spinnerTimeOpts;
    ArrayList<String> timeOptions = new ArrayList<>();
    ArrayList<String> timeValues = new ArrayList<>();

    EditText editAddress;
    EditText editAppt;
    EditText editFloor;
    EditText editCSZ;

    EditText editPhone;
    TextView editDelFee;
    EditText editNotes;

    GeocodeAddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delivery);

        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details of Delivery");
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.tvHistory).setOnClickListener(this);

        editReceiverName = findViewById(R.id.editReceiverName);
        editReceiverName.setText(appSettings.getFN());

        editDate = findViewById(R.id.editDate);
        calendarDOB = Calendar.getInstance();
        strDOB = DateUtil.toStringFormat_13(calendarDOB.getTime());
        editDate.setText(strDOB);
        dobListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarDOB.set(Calendar.YEAR, year);
                calendarDOB.set(Calendar.MONTH, monthOfYear);
                calendarDOB.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strDOB = DateUtil.toStringFormat_13(calendarDOB.getTime());
                editDate.setText(strDOB);
            }
        };

        spinnerTimeOpts = findViewById(R.id.spinnerEatTimeOpts);

        timeOptions.add("ASAP");
        timeValues.add("ASAP");
        long curTimeMils = new Date().getTime();
        long milsPer30Mins = 30 * 60000;
        long milsPerHour = 60 * 60000;
        long milsPer5Mins = 5 * 60000;
        long startTime = (curTimeMils / milsPer30Mins + 1) * milsPer30Mins;
        long endTime = (curTimeMils / milsPerHour + 3) * milsPerHour;
        for (long time = startTime; time <= endTime; time += milsPer5Mins) {
            timeOptions.add(DateUtil.toStringFormat_10(new Date(time)));
            timeValues.add(DateUtil.toStringFormat_23(new Date(time)));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        timeOptions); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinnerTimeOpts.setAdapter(spinnerArrayAdapter);

        editAddress = findViewById(R.id.editAddress);
        editAppt = findViewById(R.id.edtApartment);
        editFloor = findViewById(R.id.edtFloor);
        editCSZ = findViewById(R.id.edtCSZ);

        /* editAddress.setText(String.format("%s %s, %s, %s, %s %s", appSettings.getStreetNum(), appSettings.getStreet(), appSettings.getApt(),
                appSettings.getCity(), appSettings.getSt(), appSettings.getZip())); */

        editAddress.setText(String.format("%s %s", appSettings.getStreetNum(), appSettings.getStreet()).trim());
        editAppt.setText(appSettings.getApt());
        editFloor.setText("");

        String cszInfo = String.format("%s, %s %s", appSettings.getCity(), appSettings.getSt(), appSettings.getZip()).trim();
        if (cszInfo.startsWith(",")) {
            cszInfo = cszInfo.substring(1);
        }
        if (cszInfo.endsWith(",")) {
            cszInfo = cszInfo.substring(0, cszInfo.length() - 1);
        }
        editCSZ.setText(cszInfo);

        editPhone = findViewById(R.id.editPhone);
        editPhone.setText(appSettings.getCP());

        editDelFee = findViewById(R.id.editDelFee);
        editDelFee.setText(String.format("$%.2f Non Refundable", restaurant.getDelFee()));

        editNotes = findViewById(R.id.editNotes);

        editDate.setOnClickListener(this);

        findViewById(R.id.btnRequest).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        mResultReceiver = new GeocodeAddressResultReceiver(null, this);

        // Restore inputs
        try {
            JSONObject jsonDel = new JSONObject(appSettings.getDelInputs());
            editReceiverName.setText(jsonDel.getString("name"));
            editAddress.setText(jsonDel.getString("addr"));
            editAppt.setText(jsonDel.getString("apart"));
            editFloor.setText(jsonDel.getString("floor"));
            editCSZ.setText(jsonDel.getString("csz"));
            editPhone.setText(jsonDel.getString("phone"));
            editNotes.setText(jsonDel.getString("notes"));
        } catch (JSONException e) {
            e.printStackTrace();
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
        if (viewId == R.id.btnRequest) {
            hideKeyboard(editReceiverName);

            hideKeyboard(editAddress);
            hideKeyboard(editFloor);
            hideKeyboard(editAppt);
            hideKeyboard(editCSZ);

            hideKeyboard(editPhone);
            hideKeyboard(editNotes);

            String name = editReceiverName.getText().toString().trim();
            String date = DateUtil.toStringFormat_25(calendarDOB.getTime());
            String dateValue = strDOB;
            String time = timeOptions.get(spinnerTimeOpts.getSelectedItemPosition());
            String timeValue = timeValues.get(spinnerTimeOpts.getSelectedItemPosition());

            String address = editAddress.getText().toString().trim();
            String apart = editAppt.getText().toString().trim();
            String floor = editFloor.getText().toString().trim();
            String csz = editCSZ.getText().toString().trim();

            String phone = editPhone.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(address) || TextUtils.isEmpty(csz)) {
                showToastMessage("Please input fields");
                return;
            }

            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(csz)) {
                showAlert("Address is invalid.");
                return;
            }

            Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, address + " " + csz);
            intent.putExtra(Constants.REQUEST_CODE, 0);
            startService(intent);

            showProgressDialog();
        } else if (viewId == R.id.editDate) {
            Calendar minDateCalendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext,
                            dobListener,
                            calendarDOB.get(Calendar.YEAR),
                            calendarDOB.get(Calendar.MONTH),
                            calendarDOB.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        } else if (viewId == R.id.tvHistory) {
            startActivity(new Intent(mContext, ActivityDeliveryHistory.class));
        } else if(viewId == R.id.btnCancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void submitCatering(double toLat, double toLon) {

        hideKeyboard(editReceiverName);

        hideKeyboard(editAddress);
        hideKeyboard(editFloor);
        hideKeyboard(editAppt);
        hideKeyboard(editCSZ);

        hideKeyboard(editPhone);
        hideKeyboard(editNotes);

        String name = editReceiverName.getText().toString().trim();
        String date = DateUtil.toStringFormat_25(calendarDOB.getTime());
        String dateValue = strDOB;
        String time = timeOptions.get(spinnerTimeOpts.getSelectedItemPosition());
        String timeValue = timeValues.get(spinnerTimeOpts.getSelectedItemPosition());

        String address = editAddress.getText().toString().trim();
        String apart = editAppt.getText().toString().trim();
        String floor = editFloor.getText().toString().trim();
        String csz = editCSZ.getText().toString().trim();

        String phone = editPhone.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        // Save Input
        JSONObject jsonDel = new JSONObject();
        try {
            jsonDel.put("name", name);
            jsonDel.put("addr", address);
            jsonDel.put("apart", apart);
            jsonDel.put("floor", floor);
            jsonDel.put("csz", csz);
            jsonDel.put("phone", phone);
            jsonDel.put("notes", notes);

            appSettings.setDelInputs(jsonDel.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return the inputs
        Intent intent = getIntent();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("date", date);
        paramMap.put("dateValue", dateValue);
        paramMap.put("time", time);
        paramMap.put("timeValue", timeValue);
        paramMap.put("receipt", name);

        paramMap.put("address", address);
        paramMap.put("apt", apart);
        paramMap.put("floor", floor);
        paramMap.put("csz", csz);

        paramMap.put("phone", phone);
        paramMap.put("notes", notes);

        paramMap.put("tolat", String.valueOf(toLat));
        paramMap.put("tolon", String.valueOf(toLon));

        intent.putExtra("param", paramMap);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        hideProgressDialog();

        if (resultCode == Constants.SUCCESS_RESULT) {
            Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
            int requestCode = resultData.getInt(Constants.REQUEST_CODE);

            double toLatitude = address.getLatitude();
            double toLongitude = address.getLongitude();

            Log.e("To", String.format("%f, %f", toLatitude, toLongitude));

            submitCatering(toLatitude, toLongitude);
        } else {
            showToastMessage("Couldn't get geo location for destination");
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
