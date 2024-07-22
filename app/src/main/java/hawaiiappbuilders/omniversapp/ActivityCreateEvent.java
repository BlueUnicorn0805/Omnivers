package hawaiiappbuilders.omniversapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.PredictionsAdapter;
import hawaiiappbuilders.omniversapp.autocomplete.Candidate;
import hawaiiappbuilders.omniversapp.autocomplete.CandidateModel;
import hawaiiappbuilders.omniversapp.autocomplete.Geometry;
import hawaiiappbuilders.omniversapp.autocomplete.Prediction;
import hawaiiappbuilders.omniversapp.autocomplete.PredictionsModel;
import hawaiiappbuilders.omniversapp.autocomplete.StateFromZipModel;
import hawaiiappbuilders.omniversapp.autocomplete.StateResult;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.SwitchPlus;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;
import hawaiiappbuilders.omniversapp.utils.UrlUtil;

public class ActivityCreateEvent extends BaseActivity implements View.OnClickListener {
    // GeocodeAddressResultReceiver.OnReceiveGeocodeListener

    // todo:  do this in every activity
    public static final String TAG = ActivityCreateEvent.class.getSimpleName();
    BaseFunctions baseFunctions;

    TextView edtTitle;
    TextView edtHeading;
    TextView edtDetails;

    SwitchPlus switchAdmissionTickets;
    SwitchPlus switchChildren;
    EditText edtVideoURL;
    AutoCompleteTextView edtFullAddress;
    LinearLayout layoutStreetAddress;
    EditText edtZipCode;
    EditText edtCity;
    EditText edtState;
    LinearLayout admissionLayout;

    EditText edtNumberTickets;
    EditText edtCostPerTicket;
    EditText edtSellThruDate;

    EditText etStartDate;
    EditText etStopDate;

    EditText edtTime1;
    EditText edtTime2;
    EditText edtTime3;

    Calendar calSellDate = Calendar.getInstance();
    Calendar calStartTime = Calendar.getInstance();
    Calendar calEndTime = Calendar.getInstance();

    String startDate, stopDate, sellDate;

    // GeocodeAddressResultReceiver mResultReceiver;
    int childrenValue;
    int admissionTickets;

    int qty;
    String costValue;

    Double lat;
    Double lng;

    int limitLeft = 1000;

    Handler mCheckNameHandler;

    String[] categories;
    Spinner categorySpinner;

    boolean geocodingIsDone = true;

    TextView descriptionLimit;

    int inputLength;
    Button createEventBtn;
    View.OnClickListener sellDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(mContext,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                    calSellDate.set(Calendar.YEAR, year);
                                    calSellDate.set(Calendar.MONTH, monthOfYear);
                                    calSellDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    String strDate = DateUtil.toStringFormat_29(calSellDate.getTime());
                                    sellDate = DateUtil.toStringFormat_13(calSellDate.getTime());
                                    edtSellThruDate.setText(strDate);
                                }
                            },
                            calSellDate.get(Calendar.YEAR),
                            calSellDate.get(Calendar.MONTH),
                            calSellDate.get(Calendar.DAY_OF_MONTH));
            //datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        }
    };

    View.OnClickListener dateClickListener = new View.OnClickListener() {
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
//                                        String strDate = DateUtil.toStringFormat_29(calStartTime.getTime());
//                                        startDate = DateUtil.toStringFormat_13(calStartTime.getTime());
//                                        etStartDate.setText(strDate);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        String strDate = dateFormat.format(calStartTime.getTime());
                                        showTimePickerDialog(etStartDate, strDate);
                                    } else {
                                        calEndTime.set(Calendar.YEAR, year);
                                        calEndTime.set(Calendar.MONTH, monthOfYear);
                                        calEndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                                        String strDate = DateUtil.toStringFormat_29(calEndTime.getTime());
//                                        stopDate = DateUtil.toStringFormat_13(calEndTime.getTime());
//                                        etStopDate.setText(strDate);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        String strDate = dateFormat.format(calStartTime.getTime());
                                        showTimePickerDialog(etStopDate, strDate);
                                    }
                                }
                            },
                            calendarDate.get(Calendar.YEAR),
                            calendarDate.get(Calendar.MONTH),
                            calendarDate.get(Calendar.DAY_OF_MONTH));
            //datePickerDialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
            datePickerDialog.show();
        }
    };

    Activity createEventActivity;
    /*View.OnClickListener timeClickListener = new View.OnClickListener() {
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
                                        startTime = DateUtil.toStringFormat_10(calStartTime.getTime());
                                        etStartTime.setText(strDate);
                                    } else {
                                        calEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calEndTime.set(Calendar.MINUTE, minute);
                                        String strDate = DateUtil.toStringFormat_10(calEndTime.getTime());
                                        stopTime = DateUtil.toStringFormat_10(calEndTime.getTime());
                                        etStopTime.setText(strDate);
                                    }
                                }
                            },
                            calendarDate.get(Calendar.HOUR_OF_DAY),
                            calendarDate.get(Calendar.MINUTE),
                            false);
            timePickerDialog.show();
        }
    };*/

    private DataUtil dataUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_event);
        dataUtil = new DataUtil(this, ActivityCreateEvent.class.getSimpleName());
        createEventActivity = this;
        baseFunctions = new BaseFunctions(this, TAG);
        initView();
        setDefaultValues();
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    String state = "";
    String city = "";

    /**
     * Get state from zip code
     * Uses Geocode API
     */
    private void getCityStateFromZip() {
        try {
            String zipCode = edtZipCode.getText().toString().trim();
            String urlFormat = "https://maps.googleapis.com/maps/api/geocode/json?address=%s|country:US&region=us&key=%s";
            String apiUrl = String.format(urlFormat, zipCode, K.gKy(BuildConfig.G));

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("StateFromZip", response);
                    //  "formatted_address" : "Glendale, CA 91203, USA",
                    try {
                        StateFromZipModel stateFromZipModel = new Gson().fromJson(response, StateFromZipModel.class);
                        if (stateFromZipModel.status.contains("OK")) {
                            StateResult result = stateFromZipModel.results.get(0);
                            String formattedAddress = result.formatted_address;
                            String[] extractedAddress = formattedAddress.split(",");

                            String[] stateZip;
                            if (extractedAddress.length > 2) {
                                city = extractedAddress[0];
                                stateZip = extractedAddress[1].split(" ");
                                state = stateZip[1];
                                edtCity.setText(city);
                                edtState.setText(state);
                                layoutStreetAddress.setVisibility(View.VISIBLE);
                                edtFullAddress.requestFocus();
                            } else {
                                state = "";
                                city = "";
                                edtState.setText("");
                                edtCity.setText("");
                                edtFullAddress.setText("");
                                layoutStreetAddress.setVisibility(View.GONE);
                                showToastMessage("Invalid zip code " + zipCode);
                            }
                        } else {
                            state = "";
                            city = "";
                            edtState.setText("");
                            edtCity.setText("");
                            edtFullAddress.setText("");
                            layoutStreetAddress.setVisibility(View.GONE);
                            showToastMessage("Couldn't get state from zip " + zipCode);
                        }
                    } catch (Exception e) {
                        state = "";
                        city = "";
                        edtState.setText("");
                        edtCity.setText("");
                        edtFullAddress.setText("");
                        layoutStreetAddress.setVisibility(View.GONE);
                        showToastMessage("Couldn't get state from zip " + zipCode);
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
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(ActivityCreateEvent.class.getSimpleName());
                dataUtil.zzzLogIt(e, "geo - cccc");
            }
        }
    }


    PredictionsAdapter predictionsAdapter;
    ArrayList<Prediction> allPredictions = new ArrayList<>();

    /**
     * Fetch list of predictions from user entered street address
     * Populate list of predictions in AutocompleteTextView
     * Uses Place API - autocomplete
     */
    private void getPredictions() {
        try {
            String zipCode = edtZipCode.getText().toString().trim();
            String input = edtFullAddress.getText().toString().replace(" ", "%20") + "," + state + "," + zipCode;
            String urlFormat = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&types=geocode&key=%s";
            String apiUrl = String.format(urlFormat, input, K.gKy(BuildConfig.P));

            // showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            // HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);
            StringRequest sr = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // hideProgressDialog();

                    Log.e("Predictions", response);

                    try {
                        PredictionsModel predictionsModel = new Gson().fromJson(response, PredictionsModel.class);
                        if (predictionsModel.status.contains("OK")) {
                            allPredictions.clear();
                            allPredictions.addAll(predictionsModel.predictions);
                            predictionsAdapter = new PredictionsAdapter(mContext, R.layout.spinner_list_item, predictionsModel.predictions);
                            edtFullAddress.setAdapter(predictionsAdapter);
                            // predictionsAdapter.setNotifyOnChange(true);
                            edtFullAddress.showDropDown();
                        } else {
                            // showToastMessage("Couldn't get address information");
                        }
                    } catch (Exception e) {
                        // showToastMessage("Couldn't get address information");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    baseFunctions.handleVolleyError(mContext, error, TAG, "autocomplete");
                }
            });

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            sr.setShouldCache(false);
            queue.add(sr);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(ActivityCreateEvent.class.getSimpleName());
                dataUtil.zzzLogIt(e, "geo - cccc");
            }
        }
    }

    /**
     * Get coordinates from selected prediction
     * Get lat and long values from address
     * Uses Place API - findplacefromtext
     *
     * @param position The selected prediction item
     */
    private void getCoordinatesFromSelectedPrediction(int position) {
        String input = allPredictions.get(position).description.replace(" ", "%20");
        String urlFormat = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?fields=formatted_address,name,geometry&input=%s&inputtype=textquery&key=%s";
        String apiUrl = String.format(urlFormat, input, K.gKy(BuildConfig.P));

        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                Log.e("Coordinates", response);

                try {
                    CandidateModel candidatesModel = new Gson().fromJson(response, CandidateModel.class);
                    if (candidatesModel.status.contains("OK")) {
                        geocodingIsDone = false;
                        Candidate candidate = candidatesModel.candidates.get(0);
                        String formattedAddress = candidate.formatted_address;
                        Geometry geometry = candidate.geometry;
                        String name = candidate.name;
                        lat = geometry.location.lat;
                        lng = geometry.location.lng;

                        hideKeyboard(edtFullAddress);
                        edtFullAddress.dismissDropDown();
                        edtFullAddress.setText(formattedAddress);
                        // edtFullAddress.setSelection(inputLength);
                        Log.e("To", String.format("%f, %f", lat, lng));
                    } else {
                        geocodingIsDone = true;
                        showToastMessage("Couldn't get address information");
                    }
                } catch (Exception e) {
                    geocodingIsDone = true;
                    showToastMessage("Couldn't get address information");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, "findplacefromtext");
            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        sr.setShouldCache(false);
        queue.add(sr);
    }

    private void setDefaultValues() {
        childrenValue = 0;
        admissionTickets = 0;
        qty = 0;
        costValue = "";

        // Set Categories
        ArrayList<String> categoryItems = new ArrayList<>();

        categoryItems.addAll(Arrays.asList(getResources().getStringArray(R.array.array_categories)));
        categoryItems.remove(0);

        ArrayAdapter<CharSequence> adapterCategory = new ArrayAdapter(this, R.layout.spinner_list_item, categoryItems.toArray());
        adapterCategory.setDropDownViewResource(R.layout.spinner_list_item);
        categorySpinner.setAdapter(adapterCategory);

        // Sell Thru Date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        String strDate = DateUtil.toStringFormat_29(calendar.getTime());
        sellDate = DateUtil.toStringFormat_13(calendar.getTime());
        edtSellThruDate.setText(strDate);
    }

    private void initView() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        descriptionLimit = findViewById(R.id.text_desc_limit);
        edtTitle = findViewById(R.id.edtTitle);
        categorySpinner = findViewById(R.id.spinnerCategory);
        edtHeading = findViewById(R.id.edtHeading);
        edtDetails = findViewById(R.id.edtDetails);
        descriptionLimit.setText(limitLeft + " characters left");
        edtDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String details = edtDetails.getText().toString();
                if (!details.isEmpty()) {
                    int length = details.length();
                    limitLeft = 1000 - length;
                } else {
                    limitLeft = 1000;
                }
                if (limitLeft <= 1) {
                    descriptionLimit.setText(limitLeft + " character left");
                } else {
                    descriptionLimit.setText(limitLeft + " characters left");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtVideoURL = findViewById(R.id.edtVideoURL);
        switchChildren = findViewById(R.id.switch_children);

        etStartDate = findViewById(R.id.edtStartDate);
        etStopDate = findViewById(R.id.edtStopDate);

        edtTime1 = findViewById(R.id.edtTime1);
        edtTime2 = findViewById(R.id.edtTime2);
        edtTime3 = findViewById(R.id.edtTime3);

        layoutStreetAddress = findViewById(R.id.layout_street_address);
        edtZipCode = findViewById(R.id.edtZip);
        edtFullAddress = findViewById(R.id.edtFullAddress);

        etStartDate.setTag(0);
        etStopDate.setTag(1);
        etStartDate.setOnClickListener(dateClickListener);
        etStopDate.setOnClickListener(dateClickListener);

        admissionLayout = findViewById(R.id.layout_admission_tickets_inputs);
        switchAdmissionTickets = findViewById(R.id.switch_admission);
        edtNumberTickets = findViewById(R.id.edtNumberTickets);
        edtCostPerTicket = findViewById(R.id.edtCostPerTicket);
        edtSellThruDate = findViewById(R.id.edtSellThruDate);
        edtSellThruDate.setOnClickListener(sellDateClickListener);
       /* mCheckNameHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);
                //
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Geocode
                String fullAddress = edtFullAddress.getText().toString();
                String zipCode = edtZipCode.getText().toString();
                Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                // intent.putExtra(Constants.RECEIVER, mResultReceiver);
                intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
                intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, fullAddress + ", " + zipCode);
                intent.putExtra(Constants.REQUEST_CODE, 0);
                startService(intent);

                // 2500 Campus Road, 96822
            }
        };*/

        edtFullAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                edtFullAddress.setText(((TextView) view).getText().toString());
                getCoordinatesFromSelectedPrediction(position);
            }
        });
        edtFullAddress.setThreshold(1);

        switchChildren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    childrenValue = 1;
                } else {
                    childrenValue = 0;
                }
            }
        });

        switchAdmissionTickets.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    admissionTickets = 1;
                    admissionLayout.setVisibility(View.VISIBLE);
                } else {
                    admissionTickets = 0;
                    admissionLayout.setVisibility(View.GONE);
                }
            }
        });

        edtCity = findViewById(R.id.edtCity);
        edtState = findViewById(R.id.edtState);

        edtZipCode.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        /*edtZipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // When next button is clicked
                validateZipCode();
                return false;
            }
        });*/

        edtZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // onBlur
                    validateZipCode();
                }
            }
        });

        edtFullAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPredictions();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        createEventBtn = findViewById(R.id.btnSend);
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoId = "";
                validateFields();
            }
        });
    }

    private void validateZipCode() {
        String zipCode = edtZipCode.getText().toString().trim();
        int zipCodeValue = 0;
        if (!zipCode.isEmpty()) {
            zipCodeValue = Integer.parseInt(zipCode);
        }
        if (zipCodeValue > 500 && zipCodeValue < 100000) {
            getCityStateFromZip();
        } else {
            showToastMessage("Enter a valid zip code");
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnBack) {
            finish();
        } else if (viewId == R.id.btnToolbarHome) {
            backToHome();
        }
    }

    String videoId;

    private void validateFields() {
        hideKeyboard();

        String _edtTitle = edtTitle.getText().toString().trim();
        if (_edtTitle.isEmpty()) {
            showToastMessage("Please enter a title");
            return;
        }

        String _edtHeading = edtHeading.getText().toString().trim();
        if (_edtHeading.isEmpty()) {
            showToastMessage("Please enter a heading");
            return;
        }

        String _edtDetails = edtDetails.getText().toString().trim();
        if (_edtDetails.isEmpty()) {
            showToastMessage("Please enter details");
            return;
        }

        String _edtVideoURL = edtVideoURL.getText().toString().trim();

        boolean isYoutubeUrl = UrlUtil.isValidYouTubeUrl(_edtVideoURL);
        if (!isYoutubeUrl) {
            showToastMessage("Please enter a valid youtube url");
            return;
        } else {
            videoId = UrlUtil.extractVideoId(_edtVideoURL);
            if (videoId.length() < 11) {
                showToastMessage("Please enter a valid youtube url");
                return;
            }
        }

        String _edtZipCode = edtZipCode.getText().toString().trim();
        if (_edtZipCode.isEmpty()) {
            showToastMessage("Please enter a zip code");
            return;
        }

        String _edtFullAddress = edtFullAddress.getText().toString().trim();
        if (_edtFullAddress.isEmpty()) {
            showToastMessage("Please enter an address");
            return;
        }

        // Location is optional

        String _edtStartDate = etStartDate.getText().toString().trim();
        if (_edtStartDate.isEmpty()) {
            showToastMessage("Please enter a start date");
            return;
        }

        String _edtStopDate = etStopDate.getText().toString().trim();
        if (_edtStopDate.isEmpty()) {
            showToastMessage("Please enter a stop date");
            return;
        }

        String time1 = edtTime1.getText().toString();
        String time2 = edtTime2.getText().toString();
        String time3 = edtTime3.getText().toString();

        if (!time1.isEmpty() && !time2.isEmpty() && !time3.isEmpty()) {

        } else if (!time1.isEmpty() && !time2.isEmpty()) {

        } else if (!time1.isEmpty() && !time3.isEmpty()) {

        } else if (!time2.isEmpty() && !time3.isEmpty()) {

        } else if (!time1.isEmpty()) {

        } else {
            showToastMessage("Enter at least 1 time description");
            return;
        }

        if (switchAdmissionTickets.isChecked()) {
            if (edtNumberTickets.getText() == null) {
                return;
            }

            if (edtCostPerTicket.getText() == null) {
                return;
            }
        }


        if (!isOnline(mContext)) {
            showToastMessage(mContext, "Please check your internet connection");
            return;
        }

        askForPIN(0);
    }

    private void askForPIN(double amtValue) {
        LayoutInflater inflater = getLayoutInflater();
        View pinLayout = inflater.inflate(R.layout.dialog_ad_pin, null);
        final TextView title = pinLayout.findViewById(R.id.dialog_title);
        final TextView amount = pinLayout.findViewById(R.id.dialog_amount);
        amount.setVisibility(View.GONE);
        final EditText pin = pinLayout.findViewById(R.id.pin);
        final ImageView grey_line = pinLayout.findViewById(R.id.grey_line);
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        String formattedAmt = "Amount: $" + formatter.format(amtValue);
        amount.setText(formattedAmt);
        grey_line.setVisibility(View.GONE);
        pin.requestFocus();
        final Button submit = pinLayout.findViewById(R.id.pin_submit);
        final Button cancel = pinLayout.findViewById(R.id.pin_cancel);
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setView(pinLayout);
        alert.setCancelable(true);
        final android.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPIN = appSettings.getPIN();
                String pinNumber = pin.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                    if (getLocation()) {
                        createEvent();
                    }
                    dialog.dismiss();
                } else {
                    showToastMessage(mContext, "Incorrect PIN");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Calls setEvent API
     */
    private void createEvent() {
        String _edtTitle = edtTitle.getText().toString().trim();
        int categoryPosition = categorySpinner.getSelectedItemPosition() + 1;
        String _edtHeading = edtHeading.getText().toString().trim();
        String _edtDetails = edtDetails.getText().toString().trim();
        String _edtVideoURL = edtVideoURL.getText().toString().trim();

        String zipCode = edtZipCode.getText().toString().trim();
        int zipCodeValue = 0;
        if (!zipCode.isEmpty()) {
            zipCodeValue = Integer.parseInt(zipCode);
        }
        String _edtFullAddress = edtFullAddress.getText().toString().trim();

        // Create timeDescription value
        StringBuilder timeDescriptionValue = new StringBuilder();
        String time1 = edtTime1.getText().toString();
        String time2 = edtTime2.getText().toString();
        String time3 = edtTime3.getText().toString();
        if (!time1.isEmpty() && !time2.isEmpty() && !time3.isEmpty()) { // all entered
            timeDescriptionValue.append(time1);
            timeDescriptionValue.append("{}");
            timeDescriptionValue.append(time2);
            timeDescriptionValue.append("{}");
            timeDescriptionValue.append(time3);
        } else if (!time1.isEmpty() && !time2.isEmpty()) { // time1 and time2 is not empty, time3 is empty
            timeDescriptionValue.append(time1);
            timeDescriptionValue.append("{}");
            timeDescriptionValue.append(time2);
        } else if (!time1.isEmpty() && !time3.isEmpty()) {
            timeDescriptionValue.append(time1);
            timeDescriptionValue.append("{}");
            timeDescriptionValue.append(time3);
        } else if (!time2.isEmpty() && !time3.isEmpty()) {
            timeDescriptionValue.append(time2);
            timeDescriptionValue.append("{}");
            timeDescriptionValue.append(time3);
        } else if (!time1.isEmpty()) {
            timeDescriptionValue.append(time1);
        }

        int evUtc = 0; // set it to 0 for now

        // Qty
        if (!edtNumberTickets.getText().toString().isEmpty()) {
            qty = Integer.parseInt(edtNumberTickets.getText().toString().trim());
        }

        // Cost
        if (!edtCostPerTicket.getText().toString().isEmpty()) {
            DecimalFormat formatter = new DecimalFormat("##0.00");
            costValue = formatter.format(Double.parseDouble(edtCostPerTicket.getText().toString().trim()));
        }

        // Check qty
        if (qty == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 14);
            String strDate = DateUtil.toStringFormat_29(calendar.getTime());
            sellDate = DateUtil.toStringFormat_13(calendar.getTime());
            edtSellThruDate.setText(strDate);
        }

        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "setEvent",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());


            double costDoubleValue = 0.0;
            if (!costValue.isEmpty()) {
                costDoubleValue = Double.parseDouble(costValue);
            }

            String extraParams =
                    "&evIndustryID=" + 0 +
                            "&evCatID=" + categoryPosition +
                            "&evUtc=" + evUtc +
                            "&editEVID=" + 0 + // must change to id if editing event
                            "&evlat=" + lat +
                            "&evlon=" + lng +
                            "&evTitle=" + _edtTitle +
                            "&evHeading=" + _edtHeading +
                            "&evDetails=" + _edtDetails.replace(" ", "%20") +
//                            "&evStartDate=" + startDate +
                            "&evStartDate=" + etStartDate.getText().toString() +
//                            "&evStopDate=" + stopDate +
                            "&evStopDate=" + etStopDate.getText().toString() +
                            "&timeDesc=" + timeDescriptionValue +
                            "&evSwitchAdmissionTickets=" + admissionTickets +
                            "&evSwitchChildren=" + childrenValue +
                            "&evVideoURL=" + videoId +
                            "&evNumberTickets=" + qty +
                            "&evCostPerTicket=" + costDoubleValue +
                            "&evSellThruDate=" + sellDate +
                            "&evEmail=" + appSettings.getEmail() +
                            "&evAddressFULL=" + _edtFullAddress +
                            "&evCity=" + city +
                            "&evState=" + state +
                            "&evZip=" + zipCodeValue +
                            "&evProdID=" + 390;

            baseUrl += extraParams;

            Log.e("Request", baseUrl);
            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.e(TAG, "response -> " + response);

                    hideProgressDialog();

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                                finish();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                                alertDialogBuilder
                                        .setTitle("Congratulations!")
                                        .setMessage(jsonObject.getString("msg"))
                                        .setPositiveButton("Get the word out!", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                onBackPressed();
                                            }
                                        }).create().show();
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

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    //    public void showDatePickerDialog(View view) {
//        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,
//                (DatePicker view1, int year1, int month1, int dayOfMonth1) -> {
//                    calendar.set(Calendar.YEAR, year1);
//                    calendar.set(Calendar.MONTH, month1);
//                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);
//
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                    String selectedDate = dateFormat.format(calendar.getTime());
//                    editTextDate.setText(selectedDate);
//                },
//                year, month, dayOfMonth);
//        datePickerDialog.show();
//    }
//
    public void showTimePickerDialog(EditText view, String date) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view1, int hourOfDay, int minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute1);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String selectedTime = timeFormat.format(calendar.getTime());
                    view.setText(date + " " + selectedTime);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    /*@Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // hideKeyboard();
                    hideProgressDialog();
                }
            });
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (resultCode == Constants.SUCCESS_RESULT) {
            geocodingIsDone = false;
            Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);

            // int requestCode = resultData.getInt(Constants.REQUEST_CODE);
            String addressLine = address.getAddressLine(0);
            lat = address.getLatitude();
            lng = address.getLongitude();
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        edtFullAddress.setText(addressLine);
                        edtFullAddress.setSelection(inputLength);
                        edtLocation.setText(address.getFeatureName());
                    }
                });
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e("To", String.format("%f, %f", lat, lng));
        } else {
            geocodingIsDone = true;
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToastMessage("Couldn't get geo location for destination");
                    }
                });
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mCheckNameHandler.removeMessages(0);
    }*/

}
