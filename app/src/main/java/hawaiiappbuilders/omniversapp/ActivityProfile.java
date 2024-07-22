package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.fragment.StateSelectBottomSheetFragment;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;

/**
 * Created by RahulAnsari on 25-09-2018.
 */

public class ActivityProfile extends BaseActivity implements OnClickListener, StateSelectBottomSheetFragment.SelectStateListener {
    public static final String TAG = ActivityProfile.class.getSimpleName();

    TextView tvHandle;

    Spinner spinnerTimeZone;
    String[] timezoneNames;
    Float[] timezoneValues;
    Float selectedUTC = 0.0F;

    Spinner spinnerLanguage;
    String[] languages;
    String selectedLanguage = "";

    Spinner spinnerRace;
    String[] races;
    String selectedRace = "";

    EditText videoURL;
    EditText edtFname;
    EditText edtLname;
    EditText edtEmail;
    EditText edtPhone;

    // Gender
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int GENDER_OTHER = 3;
    PhonenumberUtils phonenumberUtils;
    RadioGroup groupGender;
    RadioButton radioMale;
    RadioButton radioFemale;
    RadioButton radioOther;

    // Marital
    private static final int MARITAL_MARRIED = 1;
    private static final int MARITAL_SINGLE = 2;
    private static final int MARITAL_DEVORCED = 3;
    RadioGroup groupMarital;
    RadioButton radioMarried;
    RadioButton radioSingle;
    RadioButton radioDivorced;

    EditText edtStreetAddr;
    EditText edtCity;
    TextView edtState;
    EditText edtZip;

    TextView tvDOB;
    Calendar calendarDOB;
    String strDOB;
    DatePickerDialog.OnDateSetListener dobListener;

    EditText edtTitle;
    Spinner spinnerTitles;

    EditText edtWeight;
    EditText edtHeight;
    EditText edtMedicaid;
    EditText edtMedicare;

    EditText edtYoutube;
    EditText edtFaceBook;
    EditText edtTwitter;
    EditText edtLinkedIn;
    EditText edtPintrest;
    EditText edtSnapchat;
    EditText edtInstagram;
    EditText edtWhatsApp;

    Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        phonenumberUtils = new PhonenumberUtils(this);
        initViews();

        getProfileData();
    }

    private void initViews() {

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnDeleteAccount).setOnClickListener(this);

        tvHandle = findViewById(R.id.tvHandle);
        tvHandle.setText(appSettings.getHandle());

        // Spinner
        spinnerTimeZone = findViewById(R.id.spinnerTimeZone);
        int defaultSpinnerIndex = 0;
        timezoneNames = getResources().getStringArray(R.array.spinner_timezone);
        timezoneValues = new Float[timezoneNames.length];
        for (int i = 0; i < timezoneNames.length; i++) {
            String[] spliteValues = timezoneNames[i].split("=");
            timezoneNames[i] = spliteValues[0];
            timezoneValues[i] = Float.parseFloat(spliteValues[1]);

           /*if (timezoneValues[i].equals(appSettings.getUTC())) {
                defaultSpinnerIndex = i;
            }*/

            if (Math.abs(timezoneValues[i] - appSettings.getUTC()) < 0.000000001) {
                defaultSpinnerIndex = i;
            }
        }

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, R.layout.layout_spinner_timezone,
                timezoneNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        languages = getResources().getStringArray(R.array.spinner_language);
        int defaultLanguageSpinnerIndex = 0;

        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equalsIgnoreCase(appSettings.getLanguage())) {
                defaultLanguageSpinnerIndex = i;
            }
        }

        ArrayAdapter languageSpinnerArrayAdapter = new ArrayAdapter(this, R.layout.layout_spinner_timezone,
                languages);
        languageSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageSpinnerArrayAdapter);
        spinnerLanguage.setSelection(defaultLanguageSpinnerIndex);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = languages[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLanguage = languages[spinnerLanguage.getSelectedItemPosition()];
            }
        });

        spinnerRace = findViewById(R.id.spinnerRace);
        races = getResources().getStringArray(R.array.spinner_race);
        int defaultRaceSpinnerIndex = 0;

        for (int i = 0; i < races.length; i++) {
            if (races[i].equalsIgnoreCase(appSettings.getRace())) {
                defaultRaceSpinnerIndex = i;
            }
        }

        ArrayAdapter raceSpinnerArrayAdapter = new ArrayAdapter(this, R.layout.layout_spinner_timezone,
                races);
        raceSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRace.setAdapter(raceSpinnerArrayAdapter);
        spinnerRace.setSelection(defaultRaceSpinnerIndex);

        spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRace = races[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRace = races[spinnerRace.getSelectedItemPosition()];
            }
        });

        // Owner Information
        videoURL = findViewById(R.id.videoURL);
        edtFname = findViewById(R.id.fName);
        edtLname = findViewById(R.id.lName);
        edtEmail = findViewById(R.id.eMail);
        edtPhone = findViewById(R.id.pNumber);

        // Gender Options
        groupGender = findViewById(R.id.groupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        radioOther = findViewById(R.id.radioOther);

        // Marital Options
        groupMarital = findViewById(R.id.groupMarital);
        radioMarried = findViewById(R.id.radioMarried);
        radioSingle = findViewById(R.id.radioSingle);
        radioDivorced = findViewById(R.id.radioDivorced);

        edtStreetAddr = findViewById(R.id.streetAddress);
        edtCity = findViewById(R.id.city);
        edtState = findViewById(R.id.st);
        edtState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StateSelectBottomSheetFragment stateSelectBottomSheetFragment = new StateSelectBottomSheetFragment(edtState.getText().toString(), ActivityProfile.this);
                stateSelectBottomSheetFragment.show(getSupportFragmentManager(), "SelectState");
            }
        });

        edtZip = findViewById(R.id.zip);

        // DOB
        tvDOB = findViewById(R.id.tvDOB);
        tvDOB.setOnClickListener(this);

        // Calendar DOB
        calendarDOB = Calendar.getInstance();


        if (isValidFormat("yyyy-MM-dd'T'HH:mm:ss", appSettings.getDOB())) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                calendarDOB.setTime(format.parse(appSettings.getDOB()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isValidFormat("yyyy-MM-dd", appSettings.getDOB())) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                calendarDOB.setTime(format.parse(appSettings.getDOB()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isValidFormat("MM/dd/yyyy", appSettings.getDOB())) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                calendarDOB.setTime(format.parse(appSettings.getDOB()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        strDOB = DateUtil.toStringFormat_1(calendarDOB.getTime());
        if (strDOB.startsWith("1900")) {
            strDOB = "";
        }
        tvDOB.setText(strDOB);

        dobListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarDOB.set(Calendar.YEAR, year);
                calendarDOB.set(Calendar.MONTH, monthOfYear);
                calendarDOB.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strDOB = DateUtil.toStringFormat_1(calendarDOB.getTime());
                tvDOB.setText(strDOB);
            }
        };

        edtTitle = findViewById(R.id.tvTitle);
        spinnerTitles = findViewById(R.id.spinnerTitles);
        int defaultTitleSpinnerIndex = 0;
        String[] titles = getResources().getStringArray(R.array.spinner_titles);
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];

            if (title.equals(appSettings.getTitle())) {
                defaultTitleSpinnerIndex = i;
            }
        }
        ArrayAdapter titleSpinnerArrayAdapter = new ArrayAdapter(this, R.layout.layout_spinner_timezone,
                titles);
        titleSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTitles.setAdapter(titleSpinnerArrayAdapter);
        spinnerTitles.setSelection(defaultTitleSpinnerIndex);
        spinnerTitles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                btnSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        edtWeight = findViewById(R.id.weight);
        edtHeight = findViewById(R.id.height);
        edtMedicaid = findViewById(R.id.medicaid);
        edtMedicare = findViewById(R.id.medicare);
        edtWeight.setText(String.valueOf(appSettings.getWeight()));
        edtHeight.setText(String.valueOf(appSettings.getHeight()));
        edtMedicaid.setText(appSettings.getMedicaid());
        edtMedicare.setText(appSettings.getMedicare());
//android:text="Problems receiving the code?"
        // Social Channels
        edtYoutube = findViewById(R.id.edtYoutube);
        edtFaceBook = findViewById(R.id.edtFaceBook);
        edtTwitter = findViewById(R.id.edtTwitter);
        edtLinkedIn = findViewById(R.id.edtLinkedIn);
        edtPintrest = findViewById(R.id.edtPintrest);
        edtSnapchat = findViewById(R.id.edtSnapchat);
        edtInstagram = findViewById(R.id.edtInstagram);
        edtWhatsApp = findViewById(R.id.edtWhatsApp);


        // Restore Values from Setting
        videoURL.setText(appSettings.getVideoUrl());
        edtFname.setEnabled(false);
        edtLname.setEnabled(false);
        edtEmail.setEnabled(false);
        edtFname.setText(appSettings.getFN());
        edtLname.setText(appSettings.getLN());
        edtPhone.setText(appSettings.getCP());
        edtEmail.setText(appSettings.getEmail());

        edtStreetAddr.setText(String.format("%s %s", appSettings.getStreetNum(), appSettings.getStreet()));

        edtCity.setText(appSettings.getCity());
        edtZip.setText(appSettings.getZip());
        edtState.setText(appSettings.getSt());
        edtZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                getCityStateFromZip();
            }
        });

        // Gender
        if ("M".equalsIgnoreCase(appSettings.getGendar())) {
            radioMale.setChecked(true);
        } else if ("F".equalsIgnoreCase(appSettings.getGendar())) {
            radioFemale.setChecked(true);
        } else {
            radioOther.setChecked(true);
        }

        // Marital
        if ("M".equalsIgnoreCase(appSettings.getMarital())) {
            radioMarried.setChecked(true);
        } else if ("S".equalsIgnoreCase(appSettings.getMarital())) {
            radioSingle.setChecked(true);
        } else if ("D".equalsIgnoreCase(appSettings.getMarital())) {
            radioDivorced.setChecked(true);
        }

        edtTitle.setText(appSettings.getTitle());

        edtYoutube.setText(appSettings.getYoutube());
        edtFaceBook.setText(appSettings.getFacebook());
        edtTwitter.setText(appSettings.getTwitter());
        edtLinkedIn.setText(appSettings.getLinkedIn());
        edtPintrest.setText(appSettings.getPintrest());
        edtSnapchat.setText(appSettings.getSnapchat());
        edtInstagram.setText(appSettings.getInstagram());
        edtWhatsApp.setText(appSettings.getWhatsApp());

        // Submit Button
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnSave.setVisibility(View.GONE);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnSave.setVisibility(View.VISIBLE);
            }
        };

        // Only show Save button When change the values in the UI.
        videoURL.addTextChangedListener(textWatcher);

        edtFname.addTextChangedListener(textWatcher);
        edtLname.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtPhone.addTextChangedListener(textWatcher);

        edtStreetAddr.addTextChangedListener(textWatcher);
        edtCity.addTextChangedListener(textWatcher);
        edtState.addTextChangedListener(textWatcher);
        edtZip.addTextChangedListener(textWatcher);

        tvDOB.addTextChangedListener(textWatcher);
        edtTitle.addTextChangedListener(textWatcher);
        edtMedicaid.addTextChangedListener(textWatcher);
        edtMedicare.addTextChangedListener(textWatcher);

        edtYoutube.addTextChangedListener(textWatcher);
        edtFaceBook.addTextChangedListener(textWatcher);
        edtTwitter.addTextChangedListener(textWatcher);
        edtLinkedIn.addTextChangedListener(textWatcher);
        edtPintrest.addTextChangedListener(textWatcher);
        edtSnapchat.addTextChangedListener(textWatcher);
        edtInstagram.addTextChangedListener(textWatcher);
        edtWhatsApp.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getProfileData() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "SMLinks" +
                            "&sellerID=" + "0" +
                            "&industryID=" + "0" +
                            "&misc=" + "0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            //showProgressDialog();

            RequestQueue queue = Volley.newRequestQueue(mContext);
            String finalBaseUrl = baseUrl;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //hideProgressDialog();

                    Log.e("SMLinks", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                            //showToastMessage(jsonObject.getString("msg"));
                        } else {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);

                                String smTitle = jsonObject.optString("SMTitle");
                                String smLink = jsonObject.optString("SMLink");

                                if (jsonObject.has("YouTube") && smTitle.equals("YouTube")) {
                                    edtYoutube.setText(smLink);
                                }

                                if (jsonObject.has("FB") && smTitle.equals("FB")) {
                                    edtFaceBook.setText(smLink);
                                }

                                if (jsonObject.has("Twitter") && smTitle.equals("Twitter")) {
                                    edtTwitter.setText(smLink);
                                }

                                if (jsonObject.has("LinkedIn") && smTitle.equals("LinkedIn")) {
                                    edtLinkedIn.setText(smLink);
                                }

                                if (jsonObject.has("Pintrest") && smTitle.equals("Pintrest")) {
                                    edtPintrest.setText(smLink);
                                }

                                if (jsonObject.has("Snapchat") && smTitle.equals("Snapchat")) {
                                    edtSnapchat.setText(smLink);
                                }

                                if (jsonObject.has("Instagram") && smTitle.equals("Instagram")) {
                                    edtInstagram.setText(smLink);
                                }

                                if (jsonObject.has("WhatsApp") && smTitle.equals("WhatsApp")) {
                                    edtWhatsApp.setText(smLink);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToastMessage(e.getMessage());
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            queue.add(stringRequest);
        }
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        } else if (viewId == R.id.btnDeleteAccount) {
            startActivity(new Intent(mContext, ActivityDeleteAccount.class));
        } else if (viewId == R.id.tvDOB) {
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
        } else if (viewId == R.id.btnSave) {
            hideKeyboard();

            register();
        } else if (viewId == R.id.btnCancel) {
            hideKeyboard();

            finish();
        }
    }


    private void getCityStateFromZip() {
        String zipCode = edtZip.getText().toString().trim();
        if (zipCode.length() != 5) {
            return;
        }

        hideKeyboard(edtZip);

        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(mContext);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(mContext);

        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&region=us&key=%s", zipCode, K.gKy(BuildConfig.G));
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.e("CityState", response);
                try {
                    JSONObject returnJSON = new JSONObject(response);
                    if (returnJSON.has("status") && "OK".equals(returnJSON.getString("status"))) {
                        // Save current zipCode
                        JSONObject jsonZipLocationInfo = returnJSON;
                        if (jsonZipLocationInfo.has("results")) {
                            JSONArray jsonArrayResult = jsonZipLocationInfo.getJSONArray("results");
                            if (jsonArrayResult.length() > 0) {
                                JSONObject jsonAddrObj = jsonArrayResult.getJSONObject(0);

                                // Get City and State
                                JSONArray jsonAddressComponentArray = jsonAddrObj.getJSONArray("address_components");
                                for (int i = 0; i < jsonAddressComponentArray.length(); i++) {
                                    JSONObject jsonAddrComponent = jsonAddressComponentArray.getJSONObject(i);
                                    String longName = jsonAddrComponent.getString("long_name");
                                    String shortName = jsonAddrComponent.getString("short_name");
                                    String types = jsonAddrComponent.getString("types");

                                    if (types.contains("administrative_area_level_1")) {
                                        // This means state info
                                        edtState.setText(shortName);
                                    } else if (types.contains("locality")) {
                                        edtCity.setText(longName);
                                    }
                                }

                                // Get Location
                                JSONObject jsonGeometryObj = jsonAddrObj.getJSONObject("geometry");
                                JSONObject jsonLocationObj = jsonGeometryObj.getJSONObject("location");
                            }
                        }
                    } else {
                        showToastMessage("Couldn't get address information");
                    }
                } catch (JSONException e) {
                    // Error
                    showToastMessage("Couldn't get address information");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, "geocode");
            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        sr.setShouldCache(false);
        queue.add(sr);
    }

    private void register() {

        final String fName = edtFname.getText().toString().trim();
        final String lName = edtLname.getText().toString().trim();

        final String email = edtEmail.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();

        String streetNum = "";
        String streetAddr = "";
        String streetInformation = edtStreetAddr.getText().toString().trim();
        if (streetInformation.contains(" ")) {
            int separator = streetInformation.indexOf(" ");

            streetNum = streetInformation.substring(0, separator).trim();
            streetAddr = streetInformation.substring(separator + 1).trim();
        } else {
            streetAddr = streetInformation;
        }

        final String cityValue = edtCity.getText().toString().trim();
        final String stateValue = edtState.getText().toString().trim();
        final String zipValue = edtZip.getText().toString().trim();

//        if (TextUtils.isEmpty(fName) ||
//                TextUtils.isEmpty(lName) ||
//                TextUtils.isEmpty(email) ||
//                TextUtils.isEmpty(phone) ||
//                TextUtils.isEmpty(edtMedicare.getText().toString()) ||
//                TextUtils.isEmpty(edtMedicaid.getText().toString())/*||
//                TextUtils.isEmpty(streetNum) ||
//                TextUtils.isEmpty(streetAddr) ||
//                TextUtils.isEmpty(cityValue) ||
//                TextUtils.isEmpty(stateValue) ||
//                TextUtils.isEmpty(zipValue)*/) {
//
//            showToastMessage("Please input main fields");
//            return;
//        }

        if (!isEmailValid(email)) {
            edtEmail.setError("Invalid Email");
            return;
        }

//        if (!phonenumberUtils.isValidPhoneNumber(phone)) {
//            edtPhone.setError("Invalid Phone number");
//            return;
//        }

        /* if (zipValue.length() != 5) {
            edtZip.setError("Zip should be 5 digits");
            return;
        }

        if (stateValue.length() != 2) {
            edtState.setError("State should be 2 characters");
            return;
        }*/

        final String title = edtTitle.getText().toString().trim();
        final String spinnerTitle = spinnerTitles.getSelectedItem().toString();
        final String strWeight = edtWeight.getText().toString().trim();
        final String strHeight = edtHeight.getText().toString().trim();

        float weight = 0;
        if (!strWeight.isEmpty()) {
            weight = Float.parseFloat(strWeight);
        }
        float finalWeight = weight;

        float height = 0;
        if (!strHeight.isEmpty()) {
            height = Float.parseFloat(strHeight);
        }
        float finalHeight = height;

        final String strMedicaid = edtMedicaid.getText().toString().trim();
        final String strMedicare = edtMedicare.getText().toString().trim();

        final String youtube = edtYoutube.getText().toString().trim();
        final String facebook = edtFaceBook.getText().toString().trim();
        final String twitter = edtTwitter.getText().toString().trim();
        final String linkedin = edtLinkedIn.getText().toString().trim();
        final String pintrest = edtPintrest.getText().toString().trim();
        final String snapchat = edtSnapchat.getText().toString().trim();
        final String instagram = edtInstagram.getText().toString().trim();
        final String whatsapp = edtWhatsApp.getText().toString().trim();

        final String finalStreetNum = streetNum;
        final String finalStreetAddr = streetAddr;

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView tvtitle = alertLayout.findViewById(R.id.dialog_title);
        tvtitle.setText("PIN is required to make changes.");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        TextInputLayout panelConfirm = alertLayout.findViewById(R.id.panelConfirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        panelConfirm.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Continue");

        final Button cancel = alertLayout.findViewById(R.id.pin_cancel);
        cancel.setText("Cancel");

        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alert.create();
        dialog.show();

        // Removed asking of PIN
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLocation()) {

                    String pinInput = pin.getText().toString().trim();
                    Log.d("testing", appSettings.getPIN());

                    if (!pinInput.equals(appSettings.getPIN())) {
                        showToastMessage("PIN is not matched!");
                        return;
                    }

                    hideKeyboard();

                    appSettings.setUTC(selectedUTC);

                    HashMap<String, String> params = new HashMap<>();
                    String baseUrl = BaseFunctions.getBaseUrl(mContext,
                            "AddPer",
                            BaseFunctions.MAIN_FOLDER,
                            getUserLat(),
                            getUserLon(),
                            mMyApp.getAndroidId());

                    String gender;
                    if (groupGender.getCheckedRadioButtonId() == R.id.radioMale) {
                        gender = "M";
                    } else if (groupGender.getCheckedRadioButtonId() == R.id.radioFemale) {
                        gender = "F";
                    } else {
                        gender = "O";
                    }

                    String marital;
                    if (groupMarital.getCheckedRadioButtonId() == R.id.radioMarried) {
                        marital = "M";
                    } else if (groupMarital.getCheckedRadioButtonId() == R.id.radioSingle) {
                        marital = "S";
                    } else {
                        marital = "D";
                    }
                    String extraParams =
                            "&gender=" + gender +
                                    "&marital=" + marital +
                                    "&industryID=" + "-1" +
                                    "&editCID=" + appSettings.getUserId() +
                                    "&email=" + email +
                                    "&un=" + email +
                                    "&pw=" + "dontupdateTHIS" +
                                    "&fn=" + fName +
                                    "&ln=" + lName +
                                    "&phone=" + PhonenumberUtils.getFilteredPhoneNumber(phone) +
                                    "&dob=" + DateUtil.toStringFormat_28(calendarDOB.getTime()) +
                                    "&street_number=" + finalStreetNum +
                                    "&street=" + finalStreetAddr +
                                    "&ste=" + "0" +
                                    "&city=" + cityValue +
                                    "&state=" + stateValue +
                                    "&zip=" + zipValue +
                                    "&UPDATEDutc=" + selectedUTC +
                                    //"&pin=" + appSettings.getPIN() +
//                                    "&cID=" + appSettings.getUserId() +
                                    "&srvPrv=" + "-1" +
                                    "&title=" + spinnerTitle + //title +
                                    "&medicaid=" + strMedicaid +
                                    "&medicare=" + strMedicare +
                                    "&countryCode=" + appSettings.getCountryCode() +
//                                    "&weight=" + finalWeight +
//                                    "&height=" + finalHeight +
                                    "&KG=" + finalWeight +
                                    "&CM=" + finalHeight +
                                    "&Lang=" + selectedLanguage +
                                    "&Race=" + selectedRace +
                                    "&YouTube=" + youtube +
                                    "&FB=" + facebook +
                                    "&Twitter=" + twitter +
                                    "&LinkedIn=" + linkedin +
                                    "&Pintrest=" + pintrest +
                                    "&Snapchat=" + snapchat +
                                    "&Instagram=" + instagram +
                                    "&WhatsApp=" + whatsapp;
                    baseUrl += extraParams;
                    Log.e("Request", baseUrl);

                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);

                    String finalBaseUrl = baseUrl;
                    StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgressDialog();

                            Log.e("AddPerRes", response);

                            if (response != null || !response.isEmpty()) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                    String status = jsonObject.getString("status");
                                    if (jsonObject.getBoolean("status")) {
                                        showToastMessage("Successfully saved owner information.");

                                        appSettings.setVideoUrl(videoURL.getText().toString().trim());

                                        appSettings.setFN(fName);
                                        appSettings.setLN(lName);

                                        appSettings.setEmail(email);
                                        appSettings.setCP(PhonenumberUtils.getFilteredPhoneNumber(phone));

                                        appSettings.setStreetNum(finalStreetNum);
                                        appSettings.setStreet(finalStreetAddr);

                                        // Save as Home address
                                        String homeAddr = String.format("%s %s, %s", finalStreetNum, finalStreetAddr, zipValue).trim();
                                        homeAddr = homeAddr.trim().replaceAll(" +", " ");
                                        homeAddr = homeAddr.replace(" ,", ",");
                                        appSettings.setHomeAddress(homeAddr);

                                        appSettings.setPIN(appSettings.getPIN());

                                        appSettings.setCity(cityValue);
                                        appSettings.setSt(stateValue);
                                        appSettings.setZip(zipValue);

                                        appSettings.setDOB(DateUtil.toStringFormat_28(calendarDOB.getTime()));

                                        //appSettings.setTitle(title);
                                        appSettings.setTitle(spinnerTitle);

                                        appSettings.setWeight(finalWeight);
                                        appSettings.setHeight(finalHeight);
                                        appSettings.setLanguage(selectedLanguage);
                                        appSettings.setRace(selectedRace);
                                        appSettings.setMedicaid(strMedicaid);
                                        appSettings.setMedicare(strMedicare);

                                        if (groupGender.getCheckedRadioButtonId() == R.id.radioMale) {
                                            appSettings.setGendar("M");
                                        } else if (groupGender.getCheckedRadioButtonId() == R.id.radioFemale) {
                                            appSettings.setGendar("F");
                                        } else {
                                            appSettings.setGendar("O");
                                        }

                                        if (groupMarital.getCheckedRadioButtonId() == R.id.radioMarried) {
                                            appSettings.setMarital("M");
                                        } else if (groupMarital.getCheckedRadioButtonId() == R.id.radioSingle) {
                                            appSettings.setMarital("S");
                                        } else {
                                            appSettings.setMarital("D");
                                        }

                                        appSettings.setYoutube(youtube);
                                        appSettings.setFacebook(facebook);
                                        appSettings.setTwitter(twitter);
                                        appSettings.setLinkedIn(linkedin);
                                        appSettings.setPintrest(pintrest);
                                        appSettings.setSnapchat(snapchat);
                                        appSettings.setInstagram(instagram);
                                        appSettings.setWhatsApp(whatsapp);

                                        int newMLID = 0;
                                        if (jsonObject.has("newCID")) {
                                            newMLID = jsonObject.getInt("newCID");
                                        } else if (jsonObject.has("ownerID")) {
                                            newMLID = jsonObject.getInt("ownerID");
                                        } else {
                                            newMLID = Integer.parseInt(jsonObject.getString("msg").replace("NewOwnerID:", ""));
                                        }

                                        finish();
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
                            hideProgressDialog();
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
                /*String pinNumber = pin.getText().toString().trim();
                boolean pinTrue = false;
                hideKeyboard();
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    pinTrue = false;
                } else {
                    pinTrue = true;
                }

                String userPin = appSettings.getPIN().trim();
                if (pinTrue && userPin.equalsIgnoreCase(pinNumber)) {
                    dialog.dismiss();

                    // Input PIN
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
                    TextView dialog_title = alertLayout.findViewById(R.id.dialog_title);
                    dialog_title.setText("Update PIN");

                    final EditText pin = alertLayout.findViewById(R.id.pin);
                    final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
                    final Button submit = alertLayout.findViewById(R.id.pin_submit);
                    submit.setText("Submit");

                    final Button cancel = alertLayout.findViewById(R.id.pin_cancel);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(ActivityProfile.this);
                    alert.setView(alertLayout);
                    alert.setCancelable(false);
                    final AlertDialog dialog = alert.create();
                    dialog.show();

                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String pinNumber = pin.getText().toString().trim();
                            String confirmPinNumber = cpin.getText().toString().trim();

                            boolean pinTrue = false;
                            boolean cpinTrue = false;
                            boolean cTrue = false;


                            if (confirmPinNumber.isEmpty() || confirmPinNumber.length() < 4) {
                                cpin.setError("Please, confirm your pin");
                                cpinTrue = false;
                            } else {
                                cpinTrue = true;
                            }

                            if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                                pin.setError("PIN must be 4 - 10 characters long");
                                pinTrue = false;
                            } else {
                                pinTrue = true;
                            }


                            if (cpinTrue && pinTrue && pinNumber.equalsIgnoreCase(confirmPinNumber)) {
                                cTrue = true;
                            } else {
                                cpin.setText("");
                                cpin.setError("Please, confirm your pin");
                                cTrue = false;
                            }

                            dialog.dismiss();
                            hideKeyboard();

                            // PIN is correct
                            if (cTrue) {

                            }
                        }
                    });
                } else {
                    showToastMessage("Wrong PIN");
                    dialog.dismiss();
                }*/
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStateSelected(String statePrefix) {
        edtState.setText(statePrefix);
    }
}
