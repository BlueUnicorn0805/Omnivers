package hawaiiappbuilders.omniversapp;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static hawaiiappbuilders.omniversapp.ActivityPayCart.MODE_CART;
import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.NEW_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.utils.WebViewUtil.YOUTUBE_URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.model.Videos;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.ScreenOrientationHelper;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class ActivityEventDetails extends BaseActivity implements View.OnClickListener, ScreenOrientationHelper.ScreenOrientationChangeListener {
    public static final String TAG = ActivityEventDetails.class.getSimpleName();
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    WebView webView;

    ImageView imgBanner;
    ImageView btnPlayV;
    TextView text_event_title;
    TextView text_headline;
    TextView tvMessages;


    TextView tvDate;
    TextView tvTimeframes;
    TextView tvLocation;
    TextView tvAdmission;

    ViewGroup clMapVid;

    Button btnPurchase;
    String url;

    Videos bodyVideoData;

    ImageView btnEventSchedule;
    ImageView btnShare;

    Context mContext;
    DataUtil dataUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        dataUtil = new DataUtil(this, ActivityEventDetails.class.getSimpleName());
        mContext = this;
        initView();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                /*.showImageOnFail(R.drawable.ic_login_logo)
                .showImageOnLoading(R.drawable.ic_login_logo)
                .showImageForEmptyUri(R.drawable.ic_login_logo)*/
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        if (getIntent().getExtras() != null) {
            bodyVideoData = getIntent().getExtras().getParcelable("eventDetails");
            updateEventDetails(bodyVideoData);
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

    @SuppressLint("SetJavaScriptEnabled")
    private void updateEventDetails(Videos bodyVideo) {
        url = getVideLink(bodyVideo.getLink());
        text_event_title.setText(bodyVideo.getTitle());
        text_headline.setText(bodyVideo.getHeadLine());
        tvMessages.setText(bodyVideo.getDescript());


        Date parseStartDate = DateUtil.parseDataFromFormat28(bodyVideo.getStartDate());
        Date parseEndDate = DateUtil.parseDataFromFormat28(bodyVideo.getStopDate());
        tvDate.setText(DateUtil.toStringFormat_32(parseStartDate) + " - " + DateUtil.toStringFormat_32(parseEndDate));

        if (!bodyVideo.getTimeDesc().isEmpty()) {
            String timeDescription = bodyVideo.getTimeDesc().replace("{}", "<br>");
            tvTimeframes.setText(Html.fromHtml(timeDescription));
        }

        tvLocation.setText(bodyVideo.getLocation());
        String fullLocationDetails = String.format("%s\n%s\n%s, %s %s",
                bodyVideo.getLocation(),
                bodyVideo.getAddressFULL(),
                bodyVideo.getCity(),
                bodyVideo.getState(),
                bodyVideo.getZip());


        tvLocation.setText(fullLocationDetails);

        if (bodyVideo.getTkReq() == 0) {
            btnPurchase.setVisibility(View.GONE);
        } else {
            btnPurchase.setVisibility(View.VISIBLE);
        }

        Double genAd = bodyVideo.getGenAdmission();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        tvAdmission.setText("General Admission $" + formatter.format(genAd) + " each");
        mImageLoader.displayImage(getVideoImageLink(bodyVideo.getLink()), imgBanner, mImageOptions);

        WebViewUtil.initialize(mContext, webView).loadUrl(url);
    }


    public String getVideoImageLink(String link) {
        if (TextUtils.isEmpty(link)) {
            return "";
        } else {
            return String.format("http://img.youtube.com/vi/%s/0.jpg", link);
        }
    }

    public String getVideLink(String link) {
        if (TextUtils.isEmpty(link)) {
            return "";
        } else {
            return "https://www.youtube.com/embed/" + link;
        }
    }


    private void initView() {
        clMapVid = findViewById(R.id.clMapVid);
        webView = findViewById(R.id.webView);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        text_event_title = findViewById(R.id.text_event_title);
        text_headline = findViewById(R.id.text_headline);
        tvMessages = findViewById(R.id.tvMessages);
        btnPurchase = findViewById(R.id.btnPurchase);

        btnEventSchedule = findViewById(R.id.btnEventSchedule);
        btnEventSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Set a Reminder")
                        .setMessage("Add this event to your Calendar.")
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addAppointment(bodyVideoData);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();


            }
        });
        tvDate = findViewById(R.id.tvDate);
        tvTimeframes = findViewById(R.id.tvTimeframes);
        tvLocation = findViewById(R.id.tvLocation);
        tvAdmission = findViewById(R.id.tvAdmission);
        imgBanner = findViewById(R.id.imgBanner);
        btnPlayV = findViewById(R.id.btnPlayV);
        btnPlayV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewUtil.initialize(mContext, webView).loadUrl(YOUTUBE_URL + bodyVideoData.getLink());
                webView.loadUrl(getVideLink(bodyVideoData.getLink()));
            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityPayCart.class);
                intent.putExtra("mode", MODE_CART);
                intent.putExtra("event", bodyVideoData);
                /*intent.putExtra("co", bodyVideoData.getPOC());
                intent.putExtra("prodID", bodyVideoData.getProdID());
                intent.putExtra("amt", bodyVideoData.getGenAdmission());*/
                startActivity(intent);
            }
        });
        btnPlayV.setVisibility(View.INVISIBLE);
        imgBanner.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
        btnShare = findViewById(R.id.btnEventShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InviteFriendToEventActivity.class);
                intent.putExtra("event", bodyVideoData);
                startActivity(intent);
            }
        });
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

    public void addAppointment(Videos event) {
        BaseActivity activity = ((BaseActivity) mContext);
        if (activity.getLocation()) {
            Date eventStartDate = DateUtil.parseDataFromFormat20(event.getStartDate());
            Date eventStopDate = DateUtil.parseDataFromFormat20(event.getStopDate());

            Calendar cStart = Calendar.getInstance();
            Calendar cStop = Calendar.getInstance();

            // Add 8 hrs to start date
            cStart.setTime(eventStartDate);
            cStart.add(Calendar.HOUR_OF_DAY, 8);

            // Get hours of start date and add 30 mins
            cStop.setTime(eventStopDate);
            cStop.add(Calendar.HOUR_OF_DAY, cStart.get(Calendar.HOUR_OF_DAY));
            cStop.add(Calendar.MINUTE, 30);

            Date newStartDate = new Date(cStart.getTimeInMillis());
            Date newStopDate = new Date(cStop.getTimeInMillis());

            long timeDiff = eventStopDate.getTime() - eventStartDate.getTime();
            long minuteDiff = (timeDiff / (1000 * 60)) % 60;
            AppSettings appSettings = new AppSettings(mContext);
            HashMap<String, String> params = new HashMap<>();
            /*String baseUrl = BaseFunctions.getBaseUrl(context,
                    "setAppt",
                    BaseFunctions.MAIN_FOLDER,
                    activity.getUserLat(),
                    activity.getUserLon(),
                    ((KTXApplication)activity.getApplication()).getAndroidId());

            String extraParams =
                    "&mode=" + "0" +
                    "&SetByID=" + appSettings.getUserId() +
                    "&tolat=" + event.getLat() +
                    "&tolon=" + event.getLon() +
                    "&industryID=" + appSettings.getIndustryid() +
                    "&ApptWithMLID=" + "0" +
                    "&meetingID=" + "0" +
                    "&CP=" + "" +
                    "&Email=" + event.getEmail() +
                    "&EventTitle=" + event.getTitle() +
                    "&promoid=" + "420" +
                    "&Amt=" + event.getGenAdmission() +
                    "&OrderID=" + "0" +
                    "&qty=" + "0" +
                    "&apptTime=" + event.getStartDate() +
                    "&mins=" + minuteDiff +
                    "&buyerID=" + "0" +
                    "&NoteID=" + "0" +
                    "&FN=" + "" +
                    "&LN=" + "";*/

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("promoid", 420);
                jsonObject.put("industryID", 0);
                jsonObject.put("attendeeMLID", 0);
                jsonObject.put("LDBID", 0);
                jsonObject.put("meetingID", 0);
                jsonObject.put("sellerID", 0);
                jsonObject.put("orderID", 0);
                jsonObject.put("mode", 0);
                jsonObject.put("amt", event.getGenAdmission());
                jsonObject.put("TZ", appSettings.getUTC()); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTime", DateUtil.toStringFormat_12(newStartDate));
                jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(newStopDate));
                jsonObject.put("eventTitle", event.getTitle());
                jsonObject.put("address", event.getLocation());
                jsonObject.put("apptLon", event.getLon());
                jsonObject.put("apptLat", event.getLat());
                jsonObject.put("cp", "");
                jsonObject.put("email", event.getEmail());
                jsonObject.put("mins", 0);
                jsonObject.put("videoMeetingURL", "");
                jsonObject.put("videoMeetingID", "");
                jsonObject.put("videoPasscode", "");
                jsonObject.put("videoAutoPhoneDial", "");
                jsonObject.put("miscUN", "");
                jsonObject.put("miscPW", "");
                jsonObject.put("qty", 0);
                jsonObject.put("editApptID", 0);
                jsonObject.put("share", "");
                jsonObject.put("newStatusID", NEW_APPOINTMENT);
                String company;
                if (appSettings.getCompany().isEmpty() && appSettings.getCompany() == null) {
                    company = "-";
                } else {
                    company = appSettings.getCompany();
                }

                String name = appSettings.getFN() + " " + appSettings.getLN();
                String senderName;
                if (company.contentEquals("-")) {
                    senderName = name;
                } else {
                    senderName = company + "\n" + name;
                }

                jsonObject.put("senderName", senderName);

                String baseUrl = BaseFunctions.getBaseData(jsonObject, activity.getApplicationContext(),
                        "setAppt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

                Log.e("Request", baseUrl);

                GoogleCertProvider.install(mContext);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("setAppt", response);
                        try {
                            JSONArray responseArray = new JSONArray(response);
                            JSONObject responseObject = responseArray.getJSONObject(0);
                            if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                activity.showToastMessage("An error occurred");
                            } else {
                                activity.showToastMessage("Event added to your calendar");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
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
                VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
            } catch (JSONException e) {
                dataUtil.setActivityName(ActivityEventDetails.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setAppt");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onScreenOrientationChanged(int orientation) {
        Log.e("Screen", "" + orientation);

        // Checks the orientation of the screen
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e("ResList", "Landscape");
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = MATCH_PARENT;
            clMapVid.setLayoutParams(params);
            hideStatusBar();
        } else {
            Log.e("ResList", "Portrait");
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) clMapVid.getLayoutParams();
            params.height = dpToPx(mContext, 204);
            clMapVid.setLayoutParams(params);

            showStatusBar();
        }
    }

    private void hideStatusBar() {
        /*View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // toolbar.setVisibility(View.GONE);

    }

    private void showStatusBar() {
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}
