package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
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

import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ActivityParty extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityParty.class.getSimpleName();
    Restaurant restaurant;

    EditText editName;
    EditText editInParty;
    TextView editDate;
    Calendar calendarDOB;
    String strDOB;
    DatePickerDialog.OnDateSetListener dobListener;

    CheckBox chkNeedsPrivate;

    Spinner spinnerTimeOpts;
    ArrayList<String> timeOptions = new ArrayList<>();
    ArrayList<String> timeValues = new ArrayList<>();

    TextView editResFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_party);

        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            finish();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details of Party");
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.tvHistory).setOnClickListener(this);

        editName = findViewById(R.id.editName);
        editInParty = findViewById(R.id.editInParty);
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

        chkNeedsPrivate = findViewById(R.id.chkNeedsPrivate);

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

        editResFee = findViewById(R.id.editResFee);
        editResFee.setText(String.format("$%.2f Non Refundable", restaurant.getPartyFee()));

        editDate.setOnClickListener(this);
        findViewById(R.id.btnRequest).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        // Party inputs
        try {
            JSONObject jsonCater = new JSONObject(appSettings.getPartyInputs());
            editName.setText(jsonCater.getString("name"));
            editInParty.setText(jsonCater.getString("party"));
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
            submitCatering();
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
        } else if(viewId == R.id.tvHistory) {
            startActivity(new Intent(mContext, ActivityPartyHistory.class));
        } else if(viewId == R.id.btnCancel) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void submitCatering() {
        hideKeyboard(editName);
        hideKeyboard(editInParty);

        String name = editName.getText().toString().trim();
        String party = editInParty.getText().toString().trim();
        String date = DateUtil.toStringFormat_25(calendarDOB.getTime());
        String dateValue = strDOB;
        String time = timeOptions.get(spinnerTimeOpts.getSelectedItemPosition());
        String timeValue = timeValues.get(spinnerTimeOpts.getSelectedItemPosition());

        if (TextUtils.isEmpty(party) || TextUtils.isEmpty(name)) {
            showToastMessage("Pleaes input fields");
            return;
        }

        // Save Input
        JSONObject jsonCater = new JSONObject();
        try {

            jsonCater.put("name", name);
            jsonCater.put("party", party);
            appSettings.setPartyInputs(jsonCater.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        HashMap<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("date", date);
        paramMap.put("dateValue", dateValue);
        paramMap.put("time", time);
        paramMap.put("timeValue", timeValue);
        paramMap.put("name", name);
        paramMap.put("inparty", party);

        paramMap.put("privateRm", chkNeedsPrivate.isChecked() ? "1" : "0");


        intent.putExtra("param", paramMap);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
}
