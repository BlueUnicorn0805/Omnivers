package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.UpdateCashBroadcast;
import hawaiiappbuilders.omniversapp.interfaces.HttpInterface;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityDisplayPriceFTFRequest extends BaseActivity implements HttpInterface, View.OnClickListener {

    private String TAG = ActivityDisplayPriceFTFRequest.class.getSimpleName();
    private Context context;
    private TextView mScannedStoreNameTextView;
    private TextView mScannedTokenNoTextView;
    private TextView mScannedAmountTextView;
    private TextView instaCashTitleTextView;
    private TextView instaCashTextView;
    private Spinner paymentMethodSpinner;
    private GpsTracker gpsTracker;
    private String lat = "", lon = "";
    private String instaCash = "";
    private String selectedPaymentMethod = "InstaCash";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_price);

        context = this;
        initViews();
        setToolBar();
        setValues();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            getUserCashInfo();
        }
    }

    private void setToolBar() {
        ImageView back = (ImageView) findViewById(R.id.tpb_back);
        TextView title = (TextView) findViewById(R.id.tpb_title);
        TextView done = (TextView) findViewById(R.id.tpb_done);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("Local Store Purchase");
        done.setVisibility(View.INVISIBLE);
    }

    private void initViews() {
        mScannedStoreNameTextView = (TextView) findViewById(R.id.scanned_store_name);
        mScannedTokenNoTextView = (TextView) findViewById(R.id.scanned_token_no);
        mScannedAmountTextView = (TextView) findViewById(R.id.scanned_amount);

        instaCashTitleTextView = (TextView) findViewById(R.id.instaCashTitle);
        instaCashTextView = (TextView) findViewById(R.id.instaCash);
        paymentMethodSpinner = (Spinner) findViewById(R.id.paymentMethodSpinner);
        instaCashTitleTextView.setVisibility(View.INVISIBLE);
        instaCashTextView.setVisibility(View.INVISIBLE);

        setSpinner();
    }

    private void setValues() {
        Intent intent = getIntent();

        try {
            String scannedData = intent.getStringExtra("SCANNED_DATA");
            String[] parts = scannedData.split(":", 2);
            String js = "{" + parts[1] + "}";
            JSONObject jsonObject = new JSONObject(js);
            String Name = jsonObject.getString("Name");
            String Token = jsonObject.getString("Token");
            String Amt = jsonObject.getString("Amt");
            mScannedStoreNameTextView.setText(Name);
            mScannedTokenNoTextView.setText(Token);
            mScannedAmountTextView.setText("$" + formatMoney(Amt));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void setSpinner() {
        final List<String> paymentMethodsString = new ArrayList<>();
        paymentMethodsString.add("InstaCash");
        paymentMethodsString.add("Family CC");
        paymentMethodsString.add("Business CC");
        paymentMethodsString.add("Add New Credit Card");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, R.layout.layout_spinner_payment_method_item/*android.R.layout.simple_spinner_item*/,
                paymentMethodsString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        paymentMethodSpinner.setSelection(0);

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = paymentMethodsString.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_amount:
                Intent intent = new Intent(ActivityDisplayPriceFTFRequest.this, ActivityPaymentSuccessfullFTFRequest.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
                break;
        }
    }
}
