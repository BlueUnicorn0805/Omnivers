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
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.UpdateCashBroadcast;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityWorkRelated extends BaseActivity implements View.OnClickListener, HttpInterface {
    public static final String TAG = ActivityWorkRelated.class.getSimpleName();
    private Context context;
    private GpsTracker gpsTracker;
    private TextView instaCashTitleTextView;
    private TextView instaCashTextView;
    private String instaCash = "";
    private String lat = "";
    private String lon = "";
    private Button mJobConnection;
    private Button mPayRoleServices;
    private LinearLayout imrtwLinearLayout;
    private Button mReadyToWorkButton;
    private int isReadyToWork = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        initViews();
        setOnClickListener();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getUserLocation();
        }
    }

    private void initViews() {
        context = this;

        mJobConnection = findViewById(R.id.wr_job_connection);
        mPayRoleServices = findViewById(R.id.wr_pay_role_services);
        imrtwLinearLayout = findViewById(R.id.imrtw);
        mReadyToWorkButton = findViewById(R.id.ready_to_work);
        mReadyToWorkButton.setVisibility(View.VISIBLE);

        instaCashTitleTextView = (TextView) findViewById(R.id.instaCashTitle);
        instaCashTextView = (TextView) findViewById(R.id.instaCash);
        instaCashTitleTextView.setVisibility(View.INVISIBLE);
        instaCashTextView.setVisibility(View.INVISIBLE);
        imrtwLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToLoginIfUserIsLoggedOut();
    }

    private void setOnClickListener() {
        mJobConnection.setOnClickListener(this);
        mPayRoleServices.setOnClickListener(this);
        mReadyToWorkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wr_job_connection:
                Intent intent = new Intent(ActivityWorkRelated.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.wr_pay_role_services:
                Intent intentPS = new Intent(ActivityWorkRelated.this, ActivityPayrollServices.class);
                intentPS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentPS);
                break;
            case R.id.wr_invoicing:
                Intent intentINVC = new Intent(ActivityWorkRelated.this, ActivityInvoicing.class);
                intentINVC.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentINVC);
                break;
            case R.id.wr_donations:
                Intent intentDNS = new Intent(ActivityWorkRelated.this, ActivityDonation.class);
                intentDNS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentDNS);
                break;
            case R.id.wr_appointment_settings:
                Intent intentAPNTMNTS = new Intent(ActivityWorkRelated.this, ActivityAppointmentSetting.class);
                intentAPNTMNTS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentAPNTMNTS);
                break;
            case R.id.ready_to_work:
                startActivityForResult(new Intent(ActivityWorkRelated.this, ReadyToWorkActivty.class), 1030);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1030) {
            if (resultCode == RESULT_OK) {
                isReadyToWork = 1;
            }
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

    private void getUserLocation() {
        gpsTracker = new GpsTracker(context);
        if (gpsTracker.canGetLocation()) {
            lat = String.valueOf(gpsTracker.getLatitude());
            lon = String.valueOf(gpsTracker.getLongitude());
            UpdateCashBroadcast.bc(this, lat, lon, instaCashTextView);
            if (isOnline(context)) {
                showProgressDlg(context, "");
                getInstaCash(context, lat, lon);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();
                } else {
                    showMessage(context, "You need to grant permission");
                }
                break;
        }
    }
}