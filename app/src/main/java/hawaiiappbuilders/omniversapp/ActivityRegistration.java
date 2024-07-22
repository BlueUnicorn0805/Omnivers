package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

/**
 * Created by RahulAnsari on 25-09-2018.
 */

public class ActivityRegistration extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityRegistration.class.getSimpleName();
    EditText edtFN;
    EditText edtLN;
    EditText edtPhone;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtConfirm;

    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int GENDER_OTHER = 3;
    int genderOption = GENDER_MALE;

    private static final int MARITAL_MARRIED = 1;
    private static final int MARITAL_SINGLE = 2;
    private static final int MARITAL_DEVORCED = 3;
    int maritalOption = MARITAL_SINGLE;

    EditText edtDOB;
    EditText edtStreetNumber;
    EditText edtStreet;
    EditText edtSte;
    EditText edtState;
    EditText edtCity;
    EditText edtZip;

    String extraParams;

    private String PIN = "";

    String strDOB = "";
    Calendar calendarDOB;
    DatePickerDialog.OnDateSetListener dobListener;

    String phoneNumber;
    String email;
    String handleText;


    LinearLayout parentLayout;
    FrameLayout congratsLayout;

    Button btnCancel;
    Button btnConfirm;

    PhonenumberUtils phonenumberUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        extraParams = "";
        Intent intent = getIntent();
        phonenumberUtils = new PhonenumberUtils(this);
        phoneNumber = intent.getStringExtra("phoneNumber");
        email = intent.getStringExtra("email");
        handleText = intent.getStringExtra("handle");

        parentLayout = findViewById(R.id.layout_registration);
        congratsLayout = findViewById(R.id.layout_congratulations);
        edtFN = (EditText) findViewById(R.id.edtFirstName);
        edtLN = (EditText) findViewById(R.id.edtLastName);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirm = (EditText) findViewById(R.id.edtConfirm);

        edtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = edtPassword.getText().toString();
                String confirmPassword = s.toString();
                if (!password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.contentEquals(confirmPassword)) {
                        hideKeyboard();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //edtPhone.setText(String.format("%s%s", countryCode, phoneNumber));
        edtPhone.setText(phoneNumber);
        edtEmail.setText(email);

        ((RadioGroup) findViewById(R.id.groupGender)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioMale) {
                    genderOption = GENDER_MALE;
                } else if (i == R.id.radioFemale) {
                    genderOption = GENDER_FEMALE;
                } else {
                    genderOption = GENDER_OTHER;
                }
            }
        });

        ((RadioGroup) findViewById(R.id.groupMarital)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioMarried) {
                    maritalOption = MARITAL_MARRIED;
                } else if (i == R.id.radioSingle) {
                    maritalOption = MARITAL_SINGLE;
                } else if (i == R.id.radioDivorced) {
                    maritalOption = MARITAL_DEVORCED;
                }
            }
        });

        edtDOB = (EditText) findViewById(R.id.edtDOB);
        edtDOB.setKeyListener(null);
        edtDOB.setOnClickListener(this);
        calendarDOB = Calendar.getInstance();
        calendarDOB.add(Calendar.YEAR, -12);
        strDOB = DateUtil.toStringFormat_14(calendarDOB.getTime());
        edtDOB.setText(strDOB);
        dobListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarDOB.set(Calendar.YEAR, year);
                calendarDOB.set(Calendar.MONTH, monthOfYear);
                calendarDOB.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strDOB = DateUtil.toStringFormat_14(calendarDOB.getTime());
                edtDOB.setText(strDOB);
            }
        };

        edtStreetNumber = (EditText) findViewById(R.id.edtStreetNumber);
        edtStreet = (EditText) findViewById(R.id.edtStreet);
        edtSte = (EditText) findViewById(R.id.edtSte);

        edtState = (EditText) findViewById(R.id.edtState);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtZip = (EditText) findViewById(R.id.edtZip);

        btnConfirm = findViewById(R.id.btnRegister);
        btnConfirm.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnCancel) {
            finish();
        } else if (viewId == R.id.btnRegister) {
            registerUser();
        } else if (viewId == R.id.edtDOB) {
            Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.add(Calendar.YEAR, -12);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext,
                            dobListener,
                            calendarDOB.get(Calendar.YEAR),
                            calendarDOB.get(Calendar.MONTH),
                            calendarDOB.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        }

    }

    private void registerUser() {
        // Go to Location Permission
        /*if (appSettings.getLocationPermission() != 1 || checkLocationPermission() == false) {
            startActivity(new Intent(mContext, ActivityPermission.class));
            return;
        }
        getLocation();*/

        hideKeyboard(edtFN);
        hideKeyboard(edtLN);
        hideKeyboard(edtEmail);
        hideKeyboard(edtPhone);
        hideKeyboard(edtPassword);
        hideKeyboard(edtConfirm);

        hideKeyboard(edtStreetNumber);
        hideKeyboard(edtStreet);
        hideKeyboard(edtSte);

        hideKeyboard(edtCity);
        hideKeyboard(edtState);
        hideKeyboard(edtZip);

        final String firstName = edtFN.getText().toString().trim();
        final String lastName = edtLN.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String confirm = edtConfirm.getText().toString().trim();

        final String streetnumber = edtStreetNumber.getText().toString().trim();
        final String street = edtStreet.getText().toString().trim();
        final String ste = edtSte.getText().toString().trim();

        final String city = edtCity.getText().toString().trim();
        final String state = edtState.getText().toString().trim();
        final String zip = edtZip.getText().toString().trim();

        // Check Names
        if (TextUtils.isEmpty(firstName)) {
            edtFN.setError(getText(R.string.error_invalid_userame));
            showAlert(R.string.error_invalid_userame);
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            edtLN.setError(getText(R.string.error_invalid_userame));
            showAlert(R.string.error_invalid_userame);
            return;
        }

        // Check mail
        if (!isValidEmail(email)) {
            edtEmail.setError(getText(R.string.error_invalid_email));
            showAlert(R.string.error_invalid_email);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError(getText(R.string.error_password));
            showAlert(R.string.error_password);
            return;
        }

        if (password.length() < 5) {
            edtPassword.setError(getText(R.string.error_invalid_password));
            showAlert(R.string.error_invalid_password);
            return;
        }

        /*if (!isValidPassword(password)) {
            showAlert(R.string.error_invalid_password);
            return;
        }*/

        if (!password.equals(confirm)) {
            edtConfirm.setError(getText(R.string.error_password_not_match));
            showAlert(R.string.error_password_not_match);
            return;
        }

        /*// Check Address
        if (TextUtils.isEmpty(streetnumber)) {
            edtStreetNumber.setError(getText(R.string.error_address));
            showAlert(R.string.error_address);
            return;
        }

        // Check Address
        if (TextUtils.isEmpty(street)) {
            edtStreet.setError(getText(R.string.error_address));
            showAlert(R.string.error_address);
            return;
        }

        if (TextUtils.isEmpty(city)) {
            edtCity.setError(getText(R.string.error_address));
            showAlert(R.string.error_address);
            return;
        }

        if (TextUtils.isEmpty(state) || state.length() != 2) {
            edtState.setError("State should be 2 characters");
            showAlert(R.string.error_address);
            return;
        }*/

        if (!phonenumberUtils.validateZipCode(zip)) {
            edtZip.setError(getText(R.string.error_zip_code));
            showAlert(R.string.error_zip_code);
            return;
        }

        final AppSettings appSettings = new AppSettings(mContext);
        String stateValue = state.length() > 0 ? state.substring(0, 2) : state;
        // No need to set again, but set again here:)
        extraParams += "&email=" + email +
                "&un=" + email +
                "&pw=" + password +
                "&marital=" + String.valueOf(maritalOption) +
                "&gender=" + String.valueOf(genderOption) +
                "&fn=" + firstName +
                "&ln=" + lastName +
                "&phone=" + phone +
//                "&dob=" + strDOB +
                "&industryID=" + "0" +
                "&street_number=" + streetnumber +
                "&street=" + street +
                "&ste=" + ste +
                "&city=" + city +
                "&state=" + stateValue +
                "&zip=" + zip +
                "&editCID=" + appSettings.getUserId() +
                "&editstoreid=" + "0" +
                "&srvPrv=" + 0 +
                "&handle=" + handleText +
                "&token=" + appSettings.getDeviceToken() +
                "&countryCode=" + "+" + appSettings.getCountryCode();

        // Show Pin Input Dialog
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView dialog_title = alertLayout.findViewById(R.id.dialog_title);
        dialog_title.setText("Create a PIN that you will use for your purchases");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        pin.setHint("Enter PIN with at least 4 digits");
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Create PIN");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumber = pin.getText().toString().trim();
                String confirmPinNumber = cpin.getText().toString().trim();

                // Validate confirm pin
                if (confirmPinNumber.isEmpty() || confirmPinNumber.length() < 4) {
                    cpin.setError("Please enter confirm pin");
                    return;
                }

                // Validate pin
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    return;
                }

                // If PIN matches
                if (pinNumber.equalsIgnoreCase(confirmPinNumber)) {
                    PIN = pinNumber;
                    extraParams += "&pin=" + PIN;
                    dialog.dismiss();
                    if (isOnline(mContext)) {
                        hideKeyboard();
                        // checkEmail();
                        callRegister();
                    }
                } else {
                    cpin.setText("");
                    cpin.setError("PINs don't match");
                }
            }
        });
    }

    private void callRegister() {
        HashMap<String, String> params = new HashMap<>();
        KTXApplication mMyApp = (KTXApplication) getApplication();
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "AddPer",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        baseUrl += extraParams;
        Log.e("Request", baseUrl);


        showProgressDlg(mContext, "Registering...");
        RequestQueue queue = Volley.newRequestQueue(mContext);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(mContext);

        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDlg();

                Log.e("AddPer", response);

                appSettings.setPIN(PIN);

                if (response != null || !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {

                            parentLayout.setVisibility(View.GONE);
                            congratsLayout.setVisibility(View.VISIBLE);
                            Animation bounceAnimation = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
                            congratsLayout.startAnimation(bounceAnimation);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent dataIntent = getIntent();
                                    dataIntent.putExtra("email", "");
                                    dataIntent.putExtra("password", "");
                                    setResult(RESULT_OK, dataIntent);
                                    finish();
                                }
                            }, 4000);


                        } else {
                            showAlert(jsonObject.getString("msg"));
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
                hideProgressDlg();
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

        sr.setShouldCache(false);
        queue.add(sr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }
}
