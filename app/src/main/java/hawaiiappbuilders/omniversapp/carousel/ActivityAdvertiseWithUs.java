package hawaiiappbuilders.omniversapp.carousel;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static hawaiiappbuilders.omniversapp.utils.WebViewUtil.YOUTUBE_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.fragment.StateSelectBottomSheetFragment;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.ScreenOrientationHelper;
import hawaiiappbuilders.omniversapp.utils.UrlUtil;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class ActivityAdvertiseWithUs extends BaseActivity implements View.OnClickListener, ScreenOrientationHelper.ScreenOrientationChangeListener, StateSelectBottomSheetFragment.SelectStateListener {
    public static final String TAG = ActivityAdvertiseWithUs.class.getSimpleName();
    Spinner spinnerEvents;

    EditText etVideoUrl;
    EditText etHeadline;

    Spinner spinnerDuration;
    String[] durations;

    // web view
    String videoId;
    ViewGroup clMapVid;
    WebView webView;
    ImageView imgBanner;
    ImageView btnPlayV;

    RadioGroup radioButtons;
    // zips
    TextView labelZips;
    RadioButton rZips;
    EditText edtZips;

    // state
    TextView labelState;
    RadioButton rState;
    EditText spinnerState;

    // country
    TextView labelCountry;
    RadioButton rCountry;
    CountryCodePicker countryCodePicker;
    String countryName;
    String countryCode;
    float total;
    TextView totalText;
    String prodName;
    int prodID;

    Button btnCancel;
    Button btnPublish;

    Context mContext;

    int freq = 1;

    int eventId = -1;


    public class SellerIDVideo {
        int eventID;
        String title;
        String headline;
        String link;

        public SellerIDVideo() {

        }

        public SellerIDVideo(int eventID, String title, String headline, String link) {
            this.eventID = eventID;
            this.title = title;
            this.headline = headline;
            this.link = link;
        }

        public int getEventID() {
            return eventID;
        }

        public void setEventID(int eventID) {
            this.eventID = eventID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHeadline() {
            return headline;
        }

        public void setHeadline(String headline) {
            this.headline = headline;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_with_us);
        mContext = this;
        total = 0.0f;
        initViews();

        displayTotalAmount();
        imgBanner.setVisibility(View.GONE);
        btnPlayV.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
    }

    public void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);

        radioButtons = findViewById(R.id.radioGroup);
        edtZips = findViewById(R.id.edtZips);
        edtZips.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    total = 0.0f;
                    displayTotalAmount();
                } else {
                    if (s.toString().contains(",")) {
                        int zipCodeCount = getZipCodeCount();
                        total = 1.5f * zipCodeCount;
                    } else {
                        total = 1.5f;
                    }
                    displayTotalAmount();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        labelZips = findViewById(R.id.label_spinner_zips);

        spinnerState = findViewById(R.id.spinner_state);
        spinnerState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StateSelectBottomSheetFragment stateSelectBottomSheetFragment = new StateSelectBottomSheetFragment(spinnerState.getText().toString(), ActivityAdvertiseWithUs.this);
                stateSelectBottomSheetFragment.show(getSupportFragmentManager(), "SelectState");
            }
        });
        labelState = findViewById(R.id.label_spinner_state);

        countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                countryName = selectedCountry.getName();
                countryCode = selectedCountry.getIso();
                total = 500.0f;
                displayTotalAmount();
            }
        });
        countryName = countryCodePicker.getDefaultCountryName();
        countryCode = countryCodePicker.getDefaultCountryCode();

        labelCountry = findViewById(R.id.label_spinner_country);
        // radio group

        rZips = findViewById(R.id.rZips);
        rZips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButtons.clearCheck();
                total = 0.0f;
                edtZips.setText("");
                prodID = 387;
                prodName = "Event Ads Zip Codes";
                    /*//rZips.setChecked(true);
                    rState.setChecked(false);
                    rCountry.setChecked(false);*/
                ViewUtil.setVisible(labelZips);
                ViewUtil.setVisible(edtZips);
                ViewUtil.setGone(spinnerState);
                ViewUtil.setGone(labelState);
                ViewUtil.setGone(labelCountry);
                ViewUtil.setGone(countryCodePicker);
                spinnerDuration.setSelection(0);
                displayTotalAmount();
            }
        });


        rState = findViewById(R.id.rState);
        rState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
// if (spinnerState.getText() != null && !spinnerState.getText().toString().isEmpty()) {
                radioButtons.clearCheck();
                total = 50.0f;
                prodID = 388;
                prodName = "Event Ads By State";
                  /*  rZips.setChecked(false);
                    // rState.setChecked(true);
                    rCountry.setChecked(false);*/
                ViewUtil.setGone(labelZips);
                ViewUtil.setGone(edtZips);
                ViewUtil.setVisible(spinnerState);
                ViewUtil.setVisible(labelState);
                ViewUtil.setGone(labelCountry);
                ViewUtil.setGone(countryCodePicker);
                spinnerDuration.setSelection(0);
                displayTotalAmount();

                // }
            }
        });
        rCountry = findViewById(R.id.rCountry);
        rCountry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButtons.clearCheck();
                total = 500.0f;
                prodID = 389;
                prodName = "Event Ads By Country";
              /*  rZips.setChecked(false);
                rState.setChecked(false);
                //  rCountry.setChecked(true);*/
                ViewUtil.setGone(labelZips);
                ViewUtil.setGone(edtZips);
                ViewUtil.setGone(spinnerState);
                ViewUtil.setGone(labelState);
                ViewUtil.setVisible(labelCountry);
                ViewUtil.setVisible(countryCodePicker);
                spinnerDuration.setSelection(0);
                displayTotalAmount();
            }
        });

        // web view
        clMapVid = findViewById(R.id.clMapVid);
        etVideoUrl = findViewById(R.id.edtVideoURL);
        etVideoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    showVideo();
                } else {
                    imgBanner.setVisibility(View.GONE);
                    btnPlayV.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        imgBanner = findViewById(R.id.imgBanner);
        btnPlayV = findViewById(R.id.btnPlayV);
        webView = findViewById(R.id.webView);
        etHeadline = findViewById(R.id.edtTitle);

        rZips = findViewById(R.id.rZips);
        rState = findViewById(R.id.rState);
        rCountry = findViewById(R.id.rCountry);


        // events
        spinnerEvents = findViewById(R.id.spinnerEvents);

        String baseUrl = BaseFunctions.getBaseUrl(this,
                "cjlGet",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "videosBySellerID";
        baseUrl += extraParams;

        Log.e("request", "request -> " + baseUrl);

        // populate events
        new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {

                Log.e("response", "response -> " + response);

                if (response != null && !response.isEmpty()) {
                    JSONArray baseJsonArray = null;
                    try {
                        baseJsonArray = new JSONArray(response);
                        if (baseJsonArray.length() > 0) {
                            Gson gson = new Gson();
                            ArrayList<SellerIDVideo> sellerIDVideos = new ArrayList<>();
                            String[] sellerVideoNames = new String[baseJsonArray.length()];
                            for (int i = 0; i < baseJsonArray.length(); i++) {
                                SellerIDVideo newItem = gson.fromJson(baseJsonArray.getString(i), SellerIDVideo.class);
                                sellerIDVideos.add(newItem);
                                sellerVideoNames[i] = newItem.getTitle();
                            }

                            ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list_item, sellerVideoNames);
                            spinnerEvents.setAdapter(eventsAdapter);
                            spinnerEvents.setSelection(0);

                            spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    eventId = sellerIDVideos.get(position).getEventID();

                                    String link = sellerIDVideos.get(position).getLink();

                                    if (!link.isEmpty()) {
                                        showVideoLink(link);
                                    } else {
                                        imgBanner.setVisibility(View.GONE);
                                        btnPlayV.setVisibility(View.GONE);
                                        webView.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    showToastMessage("empty");
                }
            }

            @Override
            public void onResponseError(String msg) {
                Log.e("onResponseError", "onResponseError -> " + msg);
                showToastMessage(msg);
            }

            @Override
            public void onServerError() {
            }
        });

        // duration
        spinnerDuration = findViewById(R.id.spinnerDuration);
        durations = new String[]{"Weekly", "Monthly", "Annually"};
        ArrayAdapter<String> paymentsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item, durations);
        spinnerDuration.setAdapter(paymentsAdapter);
        spinnerDuration.setSelection(0);
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // weekly - 7
                // monthly - 30
                // annually - 365
                int pos = spinnerDuration.getSelectedItemPosition();
                switch (pos) {
                    case 0:
                        freq = 7;
                        break;
                    case 1:
                        freq = 30;
                        break;
                    case 2:
                        freq = 365;
                        break;
                }
                displayTotalAmount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        totalText = findViewById(R.id.text_total);
        totalText.setText("");
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPublish = findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

    }

    public void purchaseAd() {

        // todo: get final value for tottax
        float tottax = 0.0f;

        String headline = etHeadline.getText().toString().trim();

        String zips = edtZips.getText().toString();
        String state = spinnerState.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sellerid", 182);
            jsonObject.put("totprice", total);
            jsonObject.put("tottax", tottax); // 0 for now
            jsonObject.put("nickid", 0);
            jsonObject.put("youtubeid", videoId);
            jsonObject.put("headline", headline); // user defined
            jsonObject.put("duration", freq);
            jsonObject.put("addlocation", 0); // where ad will show up (events page currently)
            jsonObject.put("zips", zips.isEmpty() ? "none" : zips);
            jsonObject.put("state", state.isEmpty() ? "none" : state);
            jsonObject.put("prodid", prodID);
            jsonObject.put("prodname", prodName);
            jsonObject.put("nationwide", countryCode);
            jsonObject.put("nationname", countryName);
            jsonObject.put("serviceusedid", 1);
            jsonObject.put("phonetime", DateUtil.toStringFormat_7(new Date()));
            jsonObject.put("eventid", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                "PurchaseAd", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

        Log.e(TAG, "request -> " + baseUrl);
        Log.e(TAG, "request -> enscapped -> " + BaseFunctions.decodeBaseURL(baseUrl));

        showProgressDialog();
        new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                hideProgressDialog();
                Log.e(TAG, "response -> " + response);

                showSuccessDialog(mContext, v -> {
                    finish();
                });
            }

            @Override
            public void onResponseError(String msg) {
                hideProgressDialog();
                showToastMessage(msg);
            }

            @Override
            public void onServerError() {
                hideProgressDialog();
                showToastMessage("An error occurred");
            }
        });
    }

    public void showVideoLink(String videoId) {
        if (videoId != null) {
            btnPlayV.setVisibility(View.INVISIBLE);
            imgBanner.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(String.format("http://img.youtube.com/vi/%s/0.jpg", videoId))
                    .centerCrop()
                    .into(imgBanner);
            WebViewUtil.initialize(mContext, webView).loadUrl(YOUTUBE_URL + videoId);
        } else {
            imgBanner.setVisibility(View.GONE);
            btnPlayV.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            showToastMessage("Please enter a valid youtube url");
        }
    }

    public void showVideo() {
        String edtVideoURL = etVideoUrl.getText().toString().trim();
        boolean isYoutubeUrl = UrlUtil.isYoutubeUrl(edtVideoURL);
        if (!isYoutubeUrl) {
            showToastMessage("Please enter a valid youtube url");
        } else {
            String videoId = UrlUtil.getVideoIdFromUrl(edtVideoURL);
            if (videoId != null) {
                btnPlayV.setVisibility(View.INVISIBLE);
                imgBanner.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
                WebViewUtil.initialize(mContext, webView).loadUrl(YOUTUBE_URL + videoId);
            } else {
                imgBanner.setVisibility(View.GONE);
                btnPlayV.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                showToastMessage("Please enter a valid youtube url");
            }
        }

        if (edtVideoURL.isEmpty()) {
            showToastMessage("Please enter youtube url");
        } else {
            try {
                Uri uri = Uri.parse(edtVideoURL);
                videoId = uri.getQueryParameter("v");
            } catch (Exception e) {

            }
        }

    }

    private int getZipCodeCount() {
        if (edtZips.getText() != null && !edtZips.getText().toString().isEmpty()) {
            // regex: https://regex101.com/r/0nWgtJ/1
            String allZipCodes = edtZips.getText().toString().replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

            String[] zipArray = allZipCodes.split(",");

            int finalCount = 0;
            for (String s : zipArray) {
                if (!s.isEmpty()) {
                    finalCount++;
                }
            }
            return finalCount;
        }
        return 0;
    }


    private void displayTotalAmount() {
        totalText.setVisibility(View.VISIBLE);
        float finalTotal = total * freq;
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        try {
            String formattedAmt = "$" + formatter.format(finalTotal);
            totalText.setText(formattedAmt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = appSettings.getAppOrientation();

        // Checks the orientation of the screen
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = MATCH_PARENT;
            clMapVid.setLayoutParams(params);
            hideStatusBar();
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = dpToPx(mContext, 204);
            clMapVid.setLayoutParams(params);

            showStatusBar();
        }
    }

    private void askForPIN() {

        float finalTotal = total * freq;
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");

        LayoutInflater inflater = getLayoutInflater();
        View pinLayout = inflater.inflate(R.layout.dialog_ad_pin, null);
        final TextView title = pinLayout.findViewById(R.id.dialog_title);
        final TextView amount = pinLayout.findViewById(R.id.dialog_amount);
        final EditText pin = pinLayout.findViewById(R.id.pin);
        final ImageView grey_line = pinLayout.findViewById(R.id.grey_line);

        String formattedAmt = "Amount: $" + formatter.format(finalTotal);
        amount.setText(formattedAmt);
        grey_line.setVisibility(View.GONE);
        pin.requestFocus();
        final Button submit = pinLayout.findViewById(R.id.pin_submit);
        final Button cancel = pinLayout.findViewById(R.id.pin_cancel);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(pinLayout);
        alert.setCancelable(true);
        final AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPIN = appSettings.getPIN();
                String pinNumber = pin.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                    if (getLocation()) {
                        purchaseAd();
                    }
                    dialog.dismiss();
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

    public void validateFields() {
        String headline = etHeadline.getText().toString().trim();

        if (headline.isEmpty()) {
            showToastMessage("Please enter headline");
            return;
        }

        if (rZips.isChecked()) {
            if (edtZips.getText().toString().isEmpty()) {
                showToastMessage("Please enter at least 1 zip code");
                return;
            }
        }

        /*if (total == 0) {
            showToastMessage("Please select ad duration");
            return;
        }*/

        if (rState.isChecked()) {
            if (spinnerState.getText().toString().isEmpty()) {
                showToastMessage("Please select a state");
                return;
            }
        }

        if (rCountry.isChecked()) {
            if (countryCode.isEmpty() && countryName.isEmpty()) {
                showToastMessage("Please select a country");
                return;
            }
        }

        if (eventId != -1) {
            askForPIN();
        } else {
            showToastMessage("Please select an event");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            finish();
        } else if (v.getId() == R.id.btnToolbarHome) {
            backToHome();
        }
    }

    @Override
    public void onScreenOrientationChanged(int orientation) {
        // Checks the orientation of the screen
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = MATCH_PARENT;
            clMapVid.setLayoutParams(params);
            hideStatusBar();
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = dpToPx(mContext, 204);
            clMapVid.setLayoutParams(params);

            showStatusBar();
        }
    }

    private void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showStatusBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onStateSelected(String statePrefix) {
        spinnerState.setText(statePrefix);
        total = 50.0f;
        displayTotalAmount();
    }
}
