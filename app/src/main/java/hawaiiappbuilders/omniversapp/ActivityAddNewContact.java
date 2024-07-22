package hawaiiappbuilders.omniversapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hawaiiappbuilders.omniversapp.adapters.EmailSearchAdapter;
import hawaiiappbuilders.omniversapp.fragment.StateSelectBottomSheetFragment;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;

/**
 * Created by Xiao on 25-09-2018.
 */

public class ActivityAddNewContact extends BaseActivity implements OnClickListener, StateSelectBottomSheetFragment.SelectStateListener {
    public static final String TAG = ActivityAddNewContact.class.getSimpleName();
    private MessageDataManager dm;
    private ArrayList<ContactInfo> contactList;
    EmailSearchAdapter emailSearchAdapter;
    EmailSearchAdapter nameSearchAdapter;
    // Gender
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int GENDER_OTHER = 3;
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
    Calendar calendarDOB;
    String strDOB;
    DatePickerDialog.OnDateSetListener dobListener;
    ContactInfo existingContactInfo;
    CheckBox secureBackupChkBx;


    Double latValue;
    Double longValue;
    String formattedAddress;

    int addAsNewContact;
    Toolbar toolbar;

    EditText edtContactId;
    TextView labelCompany;
    EditText edtCo;
    AutoCompleteTextView edtFname;
    EditText edtLname;
    AutoCompleteTextView edtEmail;
    TextView labelVideoLink;
    EditText eVideo;
    TextView labelNickname;
    EditText lNickName;
    EditText edtPhone;
    TextView labelStreetAddress;
    TextView labelSubStreetAddress;
    EditText edtStreetAddr;
    LinearLayout layoutSuite;
    EditText edtSuite;
    LinearLayout layoutZip;
    EditText edtZip;
    LinearLayout layoutCityState;
    EditText edtCity;
    TextView edtState;
    TextView labelDob;
    TextView tvDOB;
    LinearLayout layoutLinks;
    EditText edtYoutube;
    EditText edtHandle;
    EditText edtFaceBook;
    EditText edtTwitter;
    EditText edtLinkedIn;
    EditText edtPintrest;
    EditText edtSnapchat;
    EditText edtInstagram;
    EditText edtWhatsApp;
    TextView buttonMoreText;
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewcontact);
        dataUtil = new DataUtil(this, ActivityAddNewContact.class.getSimpleName());
        Intent intent = getIntent();
        existingContactInfo = intent.getParcelableExtra("contact");
        addAsNewContact = intent.getIntExtra("mode", 0);

        dm = new MessageDataManager(mContext);
        contactList = dm.getAlLContacts();
        initViews();
        setDefaults();

        if (existingContactInfo != null) {
            toolbar.setTitle("Edit Contact");
            // edtContactId.setText(existingContactInfo.getId());
            // Remove button action
            findViewById(R.id.btnRemove).setOnClickListener(this);
        } else {
            edtContactId.setText("0");
            toolbar.setTitle("Add New Contact");
            formattedAddress = "";
            latValue = 0.0;
            longValue = 0.0;
            // Hide remove button
            findViewById(R.id.btnRemove).setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (addAsNewContact == 1) {
            edtContactId.setText("0");
            String company = existingContactInfo.getCo();
            String email = existingContactInfo.getEmail();
            String address = existingContactInfo.getAddress();
            String cp = existingContactInfo.getCp();
            String meetingUrl = existingContactInfo.getVideoMeetingUrl();
            edtCo.setText(company);
            edtEmail.setText(email);
            edtStreetAddr.setText(address);
            edtPhone.setText(cp);
            eVideo.setText(meetingUrl);

            toolbar.setTitle("Add as New Contact");
            formattedAddress = "";
            latValue = 0.0;
            longValue = 0.0;
            // Hide remove button
            findViewById(R.id.btnRemove).setVisibility(View.GONE);
        }
    }

    public void setDefaults() {
        ViewUtil.setGone(edtContactId);
        ViewUtil.setGone(labelCompany);
        ViewUtil.setGone(edtCo);
        ViewUtil.setVisible(edtFname);
        ViewUtil.setVisible(edtLname);
        ViewUtil.setVisible(edtEmail);
        ViewUtil.setGone(labelVideoLink);
        ViewUtil.setGone(eVideo);
        ViewUtil.setGone(labelNickname);
        ViewUtil.setGone(lNickName);
        ViewUtil.setVisible(edtPhone);
        ViewUtil.setGone(labelStreetAddress);
        ViewUtil.setGone(labelSubStreetAddress);
        ViewUtil.setGone(edtStreetAddr);
        ViewUtil.setGone(layoutSuite);
        ViewUtil.setGone(edtSuite);
        ViewUtil.setGone(layoutZip);
        ViewUtil.setGone(edtZip);
        ViewUtil.setGone(layoutCityState);
        ViewUtil.setGone(edtCity);
        ViewUtil.setGone(edtState);
        ViewUtil.setGone(labelDob);
        ViewUtil.setGone(tvDOB);
        ViewUtil.setGone(layoutLinks);
        ViewUtil.setGone(edtYoutube);
        ViewUtil.setGone(edtFaceBook);
        ViewUtil.setGone(edtTwitter);
        ViewUtil.setGone(edtLinkedIn);
        ViewUtil.setGone(edtPintrest);
        ViewUtil.setGone(edtSnapchat);
        ViewUtil.setGone(edtInstagram);
        ViewUtil.setGone(edtWhatsApp);
        ViewUtil.setVisible(buttonMoreText);
    }

    private void showAllFields() {
        ViewUtil.setGone(edtContactId);
        ViewUtil.setVisible(labelCompany);
        ViewUtil.setVisible(edtCo);
        ViewUtil.setVisible(edtFname);
        ViewUtil.setVisible(edtLname);
        ViewUtil.setVisible(edtEmail);
        ViewUtil.setVisible(labelVideoLink);
        ViewUtil.setVisible(eVideo);
        ViewUtil.setVisible(labelNickname);
        ViewUtil.setVisible(lNickName);
        ViewUtil.setVisible(edtPhone);
        ViewUtil.setVisible(labelStreetAddress);
        ViewUtil.setVisible(labelSubStreetAddress);
        ViewUtil.setVisible(edtStreetAddr);
        ViewUtil.setVisible(layoutSuite);
        ViewUtil.setVisible(edtSuite);
        ViewUtil.setVisible(layoutZip);
        ViewUtil.setVisible(edtZip);
        ViewUtil.setVisible(layoutCityState);
        ViewUtil.setVisible(edtCity);
        ViewUtil.setVisible(edtState);
        ViewUtil.setVisible(labelDob);
        ViewUtil.setVisible(tvDOB);
        ViewUtil.setGone(layoutLinks);
        ViewUtil.setVisible(edtYoutube);
        ViewUtil.setVisible(edtFaceBook);
        ViewUtil.setVisible(edtTwitter);
        ViewUtil.setVisible(edtLinkedIn);
        ViewUtil.setVisible(edtPintrest);
        ViewUtil.setVisible(edtSnapchat);
        ViewUtil.setVisible(edtInstagram);
        ViewUtil.setVisible(edtWhatsApp);
        ViewUtil.setGone(buttonMoreText);
    }

    private void initViews() {
        edtHandle = findViewById(R.id.lHandle);
        labelCompany = findViewById(R.id.labelCompany);
        labelVideoLink = findViewById(R.id.labelVideoLink);
        labelNickname = findViewById(R.id.labelNickname);
        lNickName = findViewById(R.id.lNickName);
        labelStreetAddress = findViewById(R.id.labelStreetAddress);
        labelSubStreetAddress = findViewById(R.id.labelSubStreetAddress);
        layoutSuite = findViewById(R.id.layoutSuite);
        layoutZip = findViewById(R.id.layoutZip);
        layoutCityState = findViewById(R.id.layoutCityState);
        labelDob = findViewById(R.id.labelDob);
        layoutLinks = findViewById(R.id.layoutLinks);


        buttonMoreText = findViewById(R.id.buttonMoreText);
        buttonMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllFields();
            }
        });
        secureBackupChkBx = findViewById(R.id.checkbox_secure_backup);
        edtContactId = (EditText) findViewById(R.id.edtContactId);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        eVideo = findViewById(R.id.eVideo);
        edtCo = findViewById(R.id.fCo);
        edtCo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        // edtCoEmail = findViewById(R.id.fCoEmail);
//        edtHandle.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//               appSettings.setHandle(s.toString());
//            }
//        });


        edtEmail = findViewById(R.id.eMail);
        emailSearchAdapter = new EmailSearchAdapter(mContext, R.layout.layout_spinner_contact, contactList, true);
        edtEmail.setAdapter(emailSearchAdapter);
        edtEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                existingContactInfo = emailSearchAdapter.getItem(position);

                edtEmail.setText(existingContactInfo.getEmail());
                edtFname.setText(existingContactInfo.getFname());
                edtLname.setText(existingContactInfo.getLname());

                edtPhone.setText(existingContactInfo.getCp());
                edtHandle.setText(existingContactInfo.getHandle());

                lNickName.setText(existingContactInfo.getTitle());

                edtStreetAddr.setText(existingContactInfo.getAddress());
                edtZip.setText(existingContactInfo.getZip());
                edtState.setText(existingContactInfo.getState());
                edtCity.setText(existingContactInfo.getCity());

                tvDOB.setText(existingContactInfo.getDob());
            }
        });

        // Owner Information
        edtFname = findViewById(R.id.fName);
        nameSearchAdapter = new EmailSearchAdapter(mContext, R.layout.layout_spinner_contact, contactList, false);
        edtFname.setAdapter(nameSearchAdapter);
        edtFname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                existingContactInfo = nameSearchAdapter.getItem(position);
                edtEmail.setText(existingContactInfo.getEmail());
                edtFname.setText(existingContactInfo.getFname());
                edtLname.setText(existingContactInfo.getLname());
                edtHandle.setText(existingContactInfo.getHandle());

                edtPhone.setText(existingContactInfo.getCp());

                lNickName.setText(existingContactInfo.getTitle());

                edtStreetAddr.setText(existingContactInfo.getAddress());
                edtZip.setText(existingContactInfo.getZip());
                edtState.setText(existingContactInfo.getState());
                edtCity.setText(existingContactInfo.getCity());

                tvDOB.setText(existingContactInfo.getDob());
            }
        });

        edtLname = findViewById(R.id.lName);
        edtPhone = findViewById(R.id.pNumber);

        lNickName = findViewById(R.id.lNickName);

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
                StateSelectBottomSheetFragment stateSelectBottomSheetFragment = new StateSelectBottomSheetFragment(edtState.getText().toString(), ActivityAddNewContact.this);
                stateSelectBottomSheetFragment.show(getSupportFragmentManager(), "SelectState");
            }
        });

        edtSuite = findViewById(R.id.suite);
        edtZip = findViewById(R.id.zip);

        // DOB
        tvDOB = findViewById(R.id.tvDOB);
        tvDOB.setOnClickListener(this);

        // Calendar DOB
        calendarDOB = Calendar.getInstance();
        strDOB = DateUtil.toStringFormat_1(calendarDOB.getTime());
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

        // Social Channels
        edtYoutube = findViewById(R.id.edtYoutube);
        edtFaceBook = findViewById(R.id.edtFaceBook);
        edtTwitter = findViewById(R.id.edtTwitter);
        edtLinkedIn = findViewById(R.id.edtLinkedIn);
        edtPintrest = findViewById(R.id.edtPintrest);
        edtSnapchat = findViewById(R.id.edtSnapchat);
        edtInstagram = findViewById(R.id.edtInstagram);
        edtWhatsApp = findViewById(R.id.edtWhatsApp);

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

        // Submit Button
        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);

        // Is Edit Mode?
        if (existingContactInfo != null) {
            edtContactId.setText(String.valueOf(existingContactInfo.getId()));
            edtCo.setText(existingContactInfo.getCo());
            edtEmail.setText(existingContactInfo.getEmail());
            edtFname.setText(existingContactInfo.getFname());
            edtLname.setText(existingContactInfo.getLname());
            edtHandle.setText(existingContactInfo.getHandle());

            lNickName.setText(existingContactInfo.getTitle());

            edtPhone.setText(existingContactInfo.getCp());

            // todo: streetNum + street
            edtStreetAddr.setText(existingContactInfo.getAddress());
            edtSuite.setText(existingContactInfo.getSuite());
            edtZip.setText(existingContactInfo.getZip());
            edtState.setText(existingContactInfo.getState());
            edtCity.setText(existingContactInfo.getCity());
            eVideo.setText(existingContactInfo.getVideoMeetingUrl());
            tvDOB.setText(existingContactInfo.getDob());

            if (existingContactInfo.getAddress() != null) {
                formattedAddress = existingContactInfo.getAddress();
            }
            if (existingContactInfo.getLat() != null) {
                latValue = Double.parseDouble(existingContactInfo.getLat());
            }

            if (existingContactInfo.getLon() != null) {
                longValue = Double.parseDouble(existingContactInfo.getLon());
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.tvDOB) {
            Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.add(Calendar.YEAR, -12);
            String dateSet = tvDOB.getText().toString();
            // MM/dd/yyyy
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date date = null;
            try {
                date = format.parse(dateSet);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTime());
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(mContext,
                                dobListener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(minDateCalendar.getTime().getTime());
                datePickerDialog.show();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (viewId == R.id.btnSubmit) {
            hideKeyboard();

            register();
        } else if (viewId == R.id.btnCancel) {
            hideKeyboard();

            confirmCancel();
        } else if (viewId == R.id.btnRemove) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setMessage("Are you sure you want to remove contact?")
                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();

                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());
                            executor.execute(() -> {
                                dm.removeUser(existingContactInfo);
                                handler.post(() -> {
                                    showToastMessage("Removed user information");
                                    setResult(RESULT_OK);
                                    finish();
                                });
                            });
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void confirmCancel() {
        final String fName = edtFname.getText().toString().trim();
        final String lName = edtLname.getText().toString().trim();
        final String lHandle = edtHandle.getText().toString().trim();

        final String email = edtEmail.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();

        final String nickName = lNickName.getText().toString().trim();

        final String cityValue = edtCity.getText().toString().trim();
        final String stateValue = edtState.getText().toString().trim();
        final String zipValue = edtZip.getText().toString().trim();

        if (!TextUtils.isEmpty(fName) || !TextUtils.isEmpty(lName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(phone)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setMessage("Are you sure you want to discard the data?")
                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            finish();
        }
    }

    private void getCityStateFromZip() {
        try {
            String zipCode = edtZip.getText().toString().trim();
            if (zipCode.length() != 5) {
                return;
            }

            hideKeyboard(edtZip);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s|country:US&region=us&key=%s", zipCode, K.gKy(BuildConfig.G));
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

                                    JSONObject geometry = jsonAddrObj.getJSONObject("geometry");
                                    formattedAddress = jsonAddrObj.getString("formatted_address");
                                    // edtStreetAddr.setText(formattedAddress);
                                    JSONObject locationObject = geometry.getJSONObject("location");
                                    latValue = locationObject.getDouble("lat");
                                    longValue = locationObject.getDouble("lng");

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
        }catch (Exception e){
            if (dataUtil != null) {
                dataUtil.setActivityName(ActivityAddNewContact.class.getSimpleName());
                dataUtil.zzzLogIt(e, "geocode");
            }
        }
    }

    private void register() {

        final String co = edtCo.getText().toString().trim();
        // final String fCoEmail = edtCoEmail.getText().toString().trim();

        final String fName = edtFname.getText().toString().trim();
        final String lName = edtLname.getText().toString().trim();
        final String handle = edtHandle.getText().toString().trim();

        final String videoLink = eVideo.getText().toString().trim();

        final String nickName = lNickName.getText().toString().trim();

        final String email = edtEmail.getText().toString().trim();
        final String phone = edtPhone.getText().toString().trim();
        final String dob = tvDOB.getText().toString().trim();

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
        final String suiteValue = edtSuite.getText().toString().trim();
        final String zipValue = edtZip.getText().toString().trim();
        int zipIntValue = 0;
        if (!edtZip.getText().toString().isEmpty()) {
            zipIntValue = Integer.parseInt(edtZip.getText().toString().trim());
        }

        final String youtube = edtYoutube.getText().toString().trim();
        final String facebook = edtFaceBook.getText().toString().trim();
        final String twitter = edtTwitter.getText().toString().trim();
        final String linkedin = edtLinkedIn.getText().toString().trim();
        final String pintrest = edtPintrest.getText().toString().trim();
        final String snapchat = edtSnapchat.getText().toString().trim();
        final String instagram = edtInstagram.getText().toString().trim();
        final String whatsapp = edtWhatsApp.getText().toString().trim();

        /*if (existingContactInfo != null && !existingContactInfo.getMlid().equals("0")) {

            // If no changes in
            if (email.equals(existingContactInfo.getEmail())) {
                // Update Current Contact
                existingContactInfo.setCo(fCo);
                existingContactInfo.setwEmail(fCoEmail);

                existingContactInfo.setFname(fName);
                existingContactInfo.setLname(lName);
                existingContactInfo.setEmail(email);

                existingContactInfo.setTitle(nickName);

                existingContactInfo.setCp(phone);
                existingContactInfo.setDob(strDOB);
                existingContactInfo.setAddress(streetInformation);
                existingContactInfo.setZip(zipValue);
                existingContactInfo.setState(stateValue);
                existingContactInfo.setCity(cityValue);

                dm.updateContact(existingContactInfo);
                showToastMessage("Updated User info");
                gotoBoard();
            }
        }*/

        // Add New Contact
        ContactInfo newContact = new ContactInfo();
        newContact.setCo(co);
        newContact.setFname(fName);
        newContact.setLname(lName);
        newContact.setHandle(handle);
        newContact.setEmail(email);
        newContact.setTitle(nickName);
        newContact.setCp(phone);
        newContact.setDob(strDOB);
        newContact.setAddress(streetInformation);
        newContact.setStreetNum(streetNum);
        newContact.setStreet(streetAddr);
        newContact.setSuite(suiteValue);
        newContact.setZip(String.valueOf(zipIntValue));
        newContact.setState(stateValue);
        newContact.setCity(cityValue);
        newContact.setVideoMeetingUrl(videoLink);

        if (latValue == null) {
            newContact.setLat("0.0");
        } else {
            newContact.setLat(String.valueOf(latValue));
        }

        if (longValue == null) {
            newContact.setLon("0.0");
        } else {
            newContact.setLon(String.valueOf(longValue));
        }

        if (existingContactInfo == null && addAsNewContact == 0) {
            dm.addContact(newContact);
            newContact.setPri(0);
        } else {
            if (addAsNewContact == 1) {
                dm.addContact(newContact);
                newContact.setPri(0);
            } else {
                // Update Current Contact
                newContact.setId(existingContactInfo.getId());
                newContact.setPri(existingContactInfo.getPri());
                newContact.setMlid(existingContactInfo.getMlid());

                existingContactInfo.setCo(newContact.getCo());
                existingContactInfo.setFname(newContact.getFname());
                existingContactInfo.setHandle(newContact.getHandle());
                existingContactInfo.setLname(newContact.getLname());
                existingContactInfo.setEmail(newContact.getEmail());
                existingContactInfo.setTitle(newContact.getTitle());
                existingContactInfo.setCp(newContact.getCp());
                existingContactInfo.setDob(newContact.getDob());
                existingContactInfo.setAddress(newContact.getAddress());
                existingContactInfo.setStreetNum(newContact.getStreetNum());
                existingContactInfo.setStreet(newContact.getStreet());
                existingContactInfo.setSuite(newContact.getSuite());
                existingContactInfo.setZip(newContact.getZip());
                existingContactInfo.setState(newContact.getState());
                existingContactInfo.setCity(newContact.getCity());
                existingContactInfo.setLat(String.valueOf(latValue));
                existingContactInfo.setLon(String.valueOf(longValue));
                existingContactInfo.setVideoMeetingUrl(videoLink);
                dm.updateContact(existingContactInfo);
            }
        }

        // Correct, what is causing the error?
        // Api is cjlget(rtnMLID), right?
        // When app save button, it calls the above to get MLID, you set MLID = response.MLID and save that to db
        // don’t miss this
        // misc=LDBID)

        if (getLocation()) {

            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CJLGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());

                // double check parameters
                //nick,
                // string co,
                // string fn,
                // string ln,
                // string stNum,
                // string street,
                // string city,
                // string state,
                // string zip,
                // string wp, string cp, string DOB, 
                //
                //        string coEmail, string email, string misc, string LdbID 
                // Send contact details to server if secure backup is checked
                String extraParams;


                // did not hit server
                if (secureBackupChkBx.isChecked() && isEmailValid(email)) {
                    extraParams =
                            "&mode=" + "rtnMLID" +
                                    "&co=" + co +
                                    "&FN=" + fName +
                                    "&LN=" + lName +
                                    "&handle=" + handle +
                                    "&nick=" + nickName +
                                    "&cp=" + phone +
                                    "&wp=" + phone +
                                    "&streetNum=" + streetNum +
                                    "&street=" + streetAddr +
                                    "&city=" + cityValue +
                                    "&state=" + stateValue +
                                    "&suite=" + suiteValue +
                                    "&zip=" + zipIntValue +
                                    "&dob=" + dob +
                                    "&LDBID=" + String.valueOf(newContact.getId()) +
                                    "&LDBMLID=" + String.valueOf(newContact.getMlid()) +
                                    "&email=" + email +
                                    "&videoMeetingURL=" + videoLink;

                } else {
                    extraParams =
                            "&mode=" + "rtnMLID" +
                                    "&LDBID=" + String.valueOf(newContact.getId()) +
                                    "&industry=0" +
                                    "&FN=" + fName +
                                    "&LN=" + lName +
                                    "&LN=" + handle +
                                    "&LDBMLID=" + String.valueOf(newContact.getMlid()) +
                                    "&email=" + email;
                }

                baseUrl += extraParams;
                Log.e("Request", baseUrl);


                // TODO: if secureBackup is checked and if email is valid, call api and save to local database

                // Requires valid email to save in server
                if (isEmailValid(email)) {
                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);
                    String finalBaseUrl = baseUrl;
                    StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgressDialog();

                            Log.e("cjlrtnMLID", response);

                            if (!TextUtils.isEmpty(response)) {
                                try {
                                    // Refresh Data

                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject responseStatus = jsonArray.getJSONObject(0);

                                    if (responseStatus.has("MLID")) {
                                        int fMLID = responseStatus.getInt("MLID");
                                    /*if (fMLID.equals("0")) {
                                        showToastMessage(responseStatus.optString("msg"));
                                    } else {
                                    }*/

                                        if (fMLID > 0) {
                                            newContact.setMlid(fMLID);
                                            dm.updateContact(newContact);
                                            showToastMessage(mContext, "Successful");
                                            Intent output = new Intent();
                                            output.putExtra("contact", newContact);
                                            setResult(RESULT_OK, output);
                                            finish();
                                        } else {
                                            showToastMessage(mContext, responseStatus.optString("msg"));
                                            hideProgressDialog();
                                            finish();
                                        }
                                    } else { // if it's add new contact, and mlid is not returned
                                        gotoBoard();
                                        //showToastMessage("");
                                    }
                                } catch (JSONException e) {
                                    if (dataUtil != null) {
                                        dataUtil.setActivityName(ActivityAddNewContact.class.getSimpleName());
                                        dataUtil.zzzLogIt(e, "CJLGet");
                                    }
                                    e.printStackTrace();
                                    showAlert(e.getMessage());
                                }
                            } else {
                                //showAlert("Server Error");
                                gotoBoard();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));

                            gotoBoard();
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
                } else {
                    showToastMessage("Successful!");
                }
            }catch (Exception e){
                if (dataUtil != null) {
                    dataUtil.setActivityName(ActivityAddNewContact.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CJLGet");
                }
            }
        }
    }

    @Override
    public void onStateSelected(String statePrefix) {
        edtState.setText(statePrefix);
    }

    private void gotoBoard() {
        setResult(RESULT_OK);
        finish();
    }
}
