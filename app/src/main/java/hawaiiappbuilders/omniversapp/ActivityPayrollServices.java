package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.UpdateCashBroadcast;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPayrollServices extends BaseActivity implements View.OnClickListener, HttpInterface {

    private Context context;
    private GpsTracker gpsTracker;
    private TextView instaCashTitleTextView;
    private TextView instaCashTextView;
    private String instaCash = "";
    private String TAG = ActivityPayrollServices.class.getSimpleName();
    private String lat = "";
    private String lon = "";
    private Button mJobConnection;
    private Button mPayRoleServices;
    private Button mInvoicing;
    private Button mDonations;
    private Button mAppointmentSettings;
    private Button mBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_services);

        initViews();
        setOnClickListener();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getUserCashInfo();
        }
    }

    private void initViews() {
        context = this;

        mJobConnection = findViewById(R.id.wr_job_connection);
        mPayRoleServices = findViewById(R.id.wr_pay_role_services);
        mInvoicing = findViewById(R.id.wr_invoicing);
        mDonations = findViewById(R.id.wr_donations);
        mAppointmentSettings = findViewById(R.id.wr_appointment_settings);
        mBudget = findViewById(R.id.wr_dashboard);
        mBudget.setVisibility(View.VISIBLE);

        mJobConnection.setText("Clock In");
        mPayRoleServices.setText("Hours Worked");
        mInvoicing.setText("Pay Stubs");

        mInvoicing.setVisibility(View.VISIBLE);
        mDonations.setVisibility(View.GONE);
        mAppointmentSettings.setVisibility(View.GONE);


        instaCashTitleTextView = (TextView) findViewById(R.id.instaCashTitle);
        instaCashTextView = (TextView) findViewById(R.id.instaCash);
        instaCashTitleTextView.setVisibility(View.INVISIBLE);
        instaCashTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToLoginIfUserIsLoggedOut();
    }

    private void setOnClickListener() {
        mJobConnection.setOnClickListener(this);
        mPayRoleServices.setOnClickListener(this);
        mInvoicing.setOnClickListener(this);
        mDonations.setOnClickListener(this);
        mAppointmentSettings.setOnClickListener(this);
        mBudget.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wr_job_connection:
                Intent intentPI = new Intent(ActivityPayrollServices.this, ActivityPunch.class);
                intentPI.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentPI);
                break;
            case R.id.wr_pay_role_services:
                Intent intentHW = new Intent(ActivityPayrollServices.this, ActivityHoursWorked.class);
                intentHW.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentHW);
                break;
            case R.id.wr_invoicing:
                Intent intentPS = new Intent(ActivityPayrollServices.this, ActivityPayStub.class);
                intentPS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentPS);
                break;
            case R.id.wr_dashboard:
                Intent intent = new Intent(context, ActivityIFareDashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onSuccess(String message) {
        hideProgressDlg();
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONArray jsonArray = new JSONArray(message);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                    showMessage(mContext, jsonObject.getString("msg"));
                } else {
                    String instaCash = jsonObject.getString("instaCash");
                    String instaSaving = jsonObject.getString("instaSavings");

                    instaCashTitleTextView.setVisibility(View.VISIBLE);
                    instaCashTextView.setVisibility(View.VISIBLE);
                    instaCashTextView.setText("Balance : $ " + formatMoney(instaCash));
                    instaCashTitleTextView.setText("Balance : $ " + formatMoney(instaSaving));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showMessage(mContext, e.getMessage());
            }
        }
    }

    private void getUserCashInfo() {
        if (getLocation()) {
            lat = getUserLat();
            lon = getUserLon();
            UpdateCashBroadcast.bc(this, lat, lon, instaCashTextView);
            if (isOnline(mContext)) {
                getInstaCash(mContext, lat, lon);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserCashInfo();
                } else {
                    showMessage(context, "You need to grant permission");
                }
                break;
        }
    }
}