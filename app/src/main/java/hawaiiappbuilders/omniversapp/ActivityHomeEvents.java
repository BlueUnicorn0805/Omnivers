package hawaiiappbuilders.omniversapp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static hawaiiappbuilders.omniversapp.ActivityHomeMenu.activityStatus;
import static hawaiiappbuilders.omniversapp.ActivityPayCart.MODE_CART;
import static hawaiiappbuilders.omniversapp.ActivityPayCart.MODE_PAY_SEND;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.DATA_EXTRAS_BACKGROUND;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.DATA_EXTRAS_BACKGROUND_VIDEO_CALL;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.UPDATE_STATUS_ID;
import static hawaiiappbuilders.omniversapp.utils.WebViewUtil.YOUTUBE_URL;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import hawaiiappbuilders.omniversapp.adapters.HomeEventsAdapter;
import hawaiiappbuilders.omniversapp.carousel.ActivityAdvertiseWithUs;
import hawaiiappbuilders.omniversapp.carousel.CarouselVRAdapter;
import hawaiiappbuilders.omniversapp.carousel.HorizontalMarginItemDecoration;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService;
import hawaiiappbuilders.omniversapp.model.Videos;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.ScreenOrientationHelper;
import hawaiiappbuilders.omniversapp.utils.WebViewUtil;

public class ActivityHomeEvents extends BaseActivity implements HomeEventsAdapter.YoutubeVideoListener, ScreenOrientationHelper.ScreenOrientationChangeListener, StateSelectBottomSheetFragment.SelectStateListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = ActivityHomeEvents.class.getSimpleName();
    View panelFilterOptions;

    EditText spinnerLocation;
    Spinner spinnerDistance;
    EditText spinnerDateRange;
    Spinner spinnerCategory;
    Spinner spinnerTickets;
    Spinner spinnerPrice;

    RecyclerView rcvEvents;
    HomeEventsAdapter eventAdapter;

    GoogleMap googleMap;
    ArrayList<Marker> markerList = new ArrayList<>();
    ArrayList<Videos> bodyVideosList = new ArrayList<>();
    TileOverlay overlayHeapMap;
    MapFragment mapFragment;

    ViewGroup clMapVid;
    WebView wvYouTube;

    ImageView scanEventBtn;
    LinearLayout btnShowFilter;
    TextView resetText;
    TextView btnPaySend;
    TextView btnPayRequest;
    Context mContext;
    LinearLayout panelPayOptions;

    NestedScrollView scrollView;
    TextView tvResults;

    TextView tvHeadline;
    HomeEventsAdapter.YoutubeVideoListener youtubeVideoListener;

    CarouselVRAdapter carouselVRAdapter;
    RecyclerView carousel;
    public ImageView imgBanner;
    public WebView webView;
    public ImageView btnPlayV;

    String selectedAdId;
    TopAdVideo selectedTopAdVideo;
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    LinearLayout carouselLayout;

    ArrayList<String> topAdsVideoIDs = new ArrayList<>();
    ArrayList<TopAdVideo> topAdVideoArrayList;
    Handler handler;
    Runnable Update;

    public class TopAdVideo {
        String HeadLine;
        String Link;
        int evID;
        int prodID;
        int sellerID;

        public TopAdVideo() {
        }

        public TopAdVideo(String headLine, String link, int evID, int prodID, int sellerID) {
            HeadLine = headLine;
            Link = link;
            this.evID = evID;
            this.prodID = prodID;
            this.sellerID = sellerID;
        }

        public String getHeadLine() {
            return HeadLine;
        }

        public void setHeadLine(String headLine) {
            HeadLine = headLine;
        }

        public String getLink() {
            return Link;
        }

        public void setLink(String link) {
            Link = link;
        }

        public int getEvID() {
            return evID;
        }

        public void setEvID(int evID) {
            this.evID = evID;
        }

        public int getProdID() {
            return prodID;
        }

        public void setProdID(int prodID) {
            this.prodID = prodID;
        }

        public int getSellerID() {
            return sellerID;
        }

        public void setSellerID(int sellerID) {
            this.sellerID = sellerID;
        }
    }

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // If app receives status ID
            if (intent.getAction().equals(UPDATE_STATUS_ID)) {
                if (intent.getExtras().getInt("statusID") > 2000) {
                    int statusId = intent.getExtras().getInt("statusID");
                    // not visible, destroyed, minimized
                    if (activityStatus <= 1) {
                        Intent i = new Intent("receivedstatusidfromotherpage");
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("statusID", statusId);
                        startActivity(i);
                    } else {
                        Intent localMsg = new Intent("receivedstatusid");
                        localMsg.putExtra("statusID", statusId);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(localMsg);
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_events_2);
        mContext = this;
        initViews();

        youtubeVideoListener = this;
        tvHeadline = findViewById(R.id.tvHeadLine);
        ImageView ivBack = findViewById(R.id.iv_home);
        ivBack.setOnClickListener(v -> finish());

        try {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter("receivedstatusid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        topAdVideoArrayList = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                /*.showImageOnFail(R.drawable.ic_app_logo_white)
                .showImageOnLoading(R.drawable.ic_app_logo_white)
                .showImageForEmptyUri(R.drawable.ic_app_logo_white)*/
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();



        /*topAdsVideoIDs.add("bDxEQY7wFlg");
        //topAdsVideoIDs.add("84bPknTEML8"); // private
        topAdsVideoIDs.add("scWp9mnv8ng");
        topAdsVideoIDs.add("aKdghV813IE");
        //topAdsVideoIDs.add("oauQu9Wm14U"); // unavailable, can't be embedded
        //topAdsVideoIDs.add("AOYACk7m7Fk"); // unavailable, can't be embedded
        topAdsVideoIDs.add("kgTrK1oO8MU");
        topAdsVideoIDs.add("NehLVNL3uI4");
        topAdsVideoIDs.add("uMYx5gRQlxQ");
        //topAdsVideoIDs.add("xjPi6IcSH_Q"); // unavailable, can't be embedded*/


        /*handler = new Handler();
        Update = new Runnable() {
            public void run() {
                PERIOD_MS = 5000;
                if (currentPage == topAdsVideoIDs.size() - 1) {
                    currentPage = 0;
                }
                selectedAdId = topAdsVideoIDs.get(currentPage);
                if (!TextUtils.isEmpty(selectedAdId)) {
                    imgBanner.setVisibility(View.VISIBLE);
                    btnPlayV.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.INVISIBLE);
                    mImageLoader.displayImage(String.format("http://img.youtube.com/vi/%s/0.jpg", selectedAdId), imgBanner, mImageOptions);
                }
                carousel.setCurrentItem(currentPage++, true);
            }
        };*/

        imgBanner.setVisibility(View.VISIBLE);
        btnPlayV.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("payloadtype") != null) {
                int payloadType = Integer.parseInt(getIntent().getExtras().getString("payloadtype"));

                // showToastMessage("" + getIntent().getExtras().getInt("message"));
                StringBuilder str = new StringBuilder();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    Set<String> keys = bundle.keySet();
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        str.append(key);
                        str.append(":");
                        str.append(bundle.get(key));
                        str.append("\n\r");
                    }
                        /*new AlertDialog.Builder(mContext).setTitle("data").setMessage(str.toString())
                                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {

                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }).show();*/

                    try {
                        Bundle b = new Bundle();
                        String type = getIntent().getExtras().getString(Constants.REMOTE_MSG_TYPE);
                        b.putString(Constants.REMOTE_MSG_TYPE, type);
                        if (type != null) {
                            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                                b.putString(Constants.REMOTE_MSG_MEETING_TYPE, getIntent().getExtras().getString(Constants.REMOTE_MSG_MEETING_TYPE));
                                b.putString(Constants.KEY_FIRST_NAME, getIntent().getExtras().getString(Constants.KEY_FIRST_NAME));
                                b.putString(Constants.KEY_LAST_NAME, getIntent().getExtras().getString(Constants.KEY_LAST_NAME));
                                b.putInt(Constants.KEY_MLID, getIntent().getExtras().getInt(Constants.KEY_MLID));
                                b.putString(Constants.REMOTE_MSG_INVITER_TOKEN, getIntent().getExtras().getString(Constants.REMOTE_MSG_INVITER_TOKEN));
                                b.putString(Constants.REMOTE_MSG_MEETING_ROOM, getIntent().getExtras().getString(Constants.REMOTE_MSG_MEETING_ROOM));
                            } else {
                                b.putString(Constants.REMOTE_MSG_INVITATION_RESPONSE, getIntent().getExtras().getString(Constants.REMOTE_MSG_INVITATION_RESPONSE));
                                b.putString(Constants.KEY_FIRST_NAME, getIntent().getExtras().getString(Constants.KEY_FIRST_NAME));
                                b.putString(Constants.KEY_LAST_NAME, getIntent().getExtras().getString(Constants.KEY_LAST_NAME));
                                b.putInt(Constants.KEY_MLID, getIntent().getExtras().getInt(Constants.KEY_MLID));
                            }
                            AppFirebaseMessagingService.receivePushData(mContext, b.getInt(Constants.KEY_MLID), bundle, -1, DATA_EXTRAS_BACKGROUND_VIDEO_CALL, b);
                        } else {
                            int SenderID = Integer.parseInt(getIntent().getExtras().getString("SenderID"));
                            AppFirebaseMessagingService.receivePushData(mContext, SenderID, bundle, payloadType, DATA_EXTRAS_BACKGROUND, null);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (getIntent().getExtras().getString("phone") != null) {
                String phone = getIntent().getExtras().getString("phone");
                if (!phone.isEmpty()) {
                    if (checkPermissions(this, PERMISSION_REQUEST_PHONE_STRING, false, 109)) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                        phoneIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        phoneIntent.setData(Uri.parse(String.format("tel:%s", phone)));
                        startActivity(phoneIntent);
                    }
                }
            }
        }

    }


    public static boolean distance(double currLat, double currLon, double targetLat,
                                   double targetLong, int miles) {
        // WHERE(3959 *
        // ACOS(COS(RADIANS(" + Lat + ")) *
        // COS(RADIANS(Rep.Lat)) *
        // COS(RADIANS(Rep.Lon) -
        // RADIANS(" + Lon + ")) +
        // SIN(RADIANS(" + Lat + ")) *
        // SIN(RADIANS(Rep.Lat))) " + "< 20) ORDER BY dist";
        /*currLat = 33;
        currLon = -112;*/
        double lonDistance = Math.toRadians(targetLong) - Math.toRadians(currLon);
        double distance = 3959 * Math.acos(Math.cos(Math.toRadians(currLat)) * Math.cos(Math.toRadians(targetLat)) *
                Math.cos(lonDistance) +
                Math.sin(Math.toRadians(currLat)) * Math.sin(Math.toRadians(targetLat)));
        return distance < miles;
    }

    private void initViews() {
        carouselLayout = findViewById(R.id.layout_carousel);
        carousel = findViewById(R.id.carousel);


        if (carousel != null) {
            carousel.setLayoutManager(new LinearLayoutManager(mContext));
        }
        if (findViewById(R.id.btnAdvertiseWithUs) != null) {
            findViewById(R.id.btnAdvertiseWithUs).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }

        imgBanner = findViewById(R.id.imgBanner);
        webView = findViewById(R.id.webView);
        btnPlayV = (ImageView) findViewById(R.id.btnplayv);
        btnPlayV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                btnPlayV.setVisibility(View.INVISIBLE);
                imgBanner.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);

                webView.setTag(selectedAdId);
                if (!TextUtils.isEmpty(selectedAdId)) {
                    WebViewUtil.initialize(mContext, webView).loadUrl(YOUTUBE_URL + selectedAdId);
                    tvHeadline.setText(selectedTopAdVideo.getHeadLine());
                }

            }
        });
        tvResults = findViewById(R.id.text_results);


        resetText = findViewById(R.id.textReset);
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset filters
                spinnerLocation.setText("");
                spinnerCategory.setSelection(0);
                // setCurrentDateRange();
                spinnerDateRange.setText("");
                spinnerDistance.setSelection(0);
                spinnerTickets.setSelection(0);
                spinnerPrice.setSelection(0);

                populateListView(bodyVideosList);
            }
        });
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);


        panelPayOptions = findViewById(R.id.panelPayOptions);

        // Filter
        btnShowFilter = findViewById(R.id.btnShowFilter);
        btnShowFilter.setOnClickListener(this);

        panelFilterOptions = findViewById(R.id.panelFilterOptions);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        spinnerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(FILTER_LOCATION, 0);
            }
        });

        spinnerDistance = findViewById(R.id.spinnerDistance);
        ArrayAdapter<CharSequence> adapterDistance = ArrayAdapter.createFromResource(this, R.array.array_filter_distances, R.layout.layout_spinner_filter);
        adapterDistance.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerDistance.setAdapter(adapterDistance);
        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter(FILTER_DISTANCE, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDateRange = findViewById(R.id.spinnerDateRange);
        // setCurrentDateRange();
        spinnerDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(FILTER_DATE_RANGE, 0);
            }
        });

        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this, R.array.array_categories, R.layout.layout_spinner_filter);
        adapterCategory.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter(FILTER_CATEGORIES, position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTickets = findViewById(R.id.spinnerTickets);
        ArrayAdapter<CharSequence> adapterTickets = ArrayAdapter.createFromResource(this, R.array.array_filter_tickets, R.layout.layout_spinner_filter);
        adapterTickets.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerTickets.setAdapter(adapterTickets);
        spinnerTickets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter(FILTER_TICKETS, position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPrice = findViewById(R.id.spinnerPrice);
        ArrayAdapter<CharSequence> adapterPrice = ArrayAdapter.createFromResource(this, R.array.array_filter_prices, R.layout.layout_spinner_filter);
        adapterPrice.setDropDownViewResource(R.layout.layout_spinner_filter);
        spinnerPrice.setAdapter(adapterPrice);
        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter(FILTER_PRICE, position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clMapVid = findViewById(R.id.clMapVid);

        wvYouTube = (WebView) findViewById(R.id.wvYouTube);
        wvYouTube.getSettings().setPluginState(WebSettings.PluginState.ON);
        //wvYouTube.setWebChromeClient(new WebChromeClient());
        wvYouTube.setWebChromeClient(new MyChrome());    // here
        wvYouTube.getSettings().setJavaScriptEnabled(true);
        // wvYouTube.getSettings().setAppCacheEnabled(true);
        wvYouTube.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        wvYouTube.setInitialScale(1);
        wvYouTube.getSettings().setLoadWithOverviewMode(true);
        wvYouTube.getSettings().setUseWideViewPort(true);

        rcvEvents = findViewById(R.id.rcvEvents);
        rcvEvents.setLayoutManager(new GridLayoutManager(mContext, 1));
        // rcvEvents.setHasFixedSize(true);

        btnPaySend = findViewById(R.id.tabPaySend);
        btnPaySend.setOnClickListener(this);
        btnPayRequest = findViewById(R.id.tabPayRequest);
        btnPayRequest.setOnClickListener(this);
        rcvEvents.setNestedScrollingEnabled(false);
        scanEventBtn = findViewById(R.id.btnScanEvent);
        scanEventBtn.setOnClickListener(this);

        getEvents();
    }

    private void setCurrentDateRange() {
        Calendar current = Calendar.getInstance();
        int maximumDayOfMonth = current.getActualMaximum(Calendar.DAY_OF_MONTH);
        int year = current.get(Calendar.YEAR);
        int day = current.get(Calendar.DAY_OF_MONTH);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        String currentMonthStr = String.format("%02d", currentMonth);
        String currentDayStr = String.format("%02d", day);
        String calendarStartDateStr = String.format("%s-%s-%s", currentMonthStr, currentDayStr, year);
        String calendarStopDateStr = String.format("%s-%s-%s", currentMonthStr, maximumDayOfMonth, year);
        Date calendarStartDate = DateUtil.parseDateToMonthFullName(calendarStartDateStr);
        Date calendarStopDate = DateUtil.parseDateToMonthFullName(calendarStopDateStr);
        spinnerDateRange.setText(String.format("%s to %s", DateUtil.toStringFormatMMMddyyyy(calendarStartDate), DateUtil.toStringFormatMMMddyyyy(calendarStopDate)));
    }


    public static final int FILTER_LOCATION = 1;
    public static final int FILTER_DISTANCE = 2;
    public static final int FILTER_DATE_RANGE = 3;
    public static final int FILTER_CATEGORIES = 4;
    public static final int FILTER_TICKETS = 5;
    public static final int FILTER_PRICE = 6;

    public void filter(int filterType, int position) {
        resetText.setVisibility(View.VISIBLE);
        switch (filterType) {
            case FILTER_LOCATION: // position not applicable
                StateSelectBottomSheetFragment stateSelectBottomSheetFragment = new StateSelectBottomSheetFragment(spinnerLocation.getText().toString(), ActivityHomeEvents.this);
                stateSelectBottomSheetFragment.show(getSupportFragmentManager(), "SelectState");
                break;
            case FILTER_DISTANCE:
                if (position == 0) {
                    populateListView(bodyVideosList);
                } else {
                    if (getLocation()) {
                        double currLat = Double.parseDouble(getUserLat());
                        double currLng = Double.parseDouble(getUserLon());
                        ArrayList<Videos> filteredList = new ArrayList<>();
                        if (bodyVideosList.size() > 0) {
                            for (int i = 0; i < bodyVideosList.size(); i++) {
                                Videos bodyVideo = bodyVideosList.get(i);
                                double targetLat = bodyVideo.getLat();
                                double targetLon = bodyVideo.getLon();

                                boolean isWithinSelectedDistance = distance(currLat, currLng, targetLat, targetLon, Integer.parseInt(spinnerDistance.getSelectedItem().toString().split(" ")[0]));
                                if (isWithinSelectedDistance) {
                                    filteredList.add(bodyVideosList.get(i));
                                }
                            }
                        }
                        populateListView(filteredList);
                    }
                }
                break;
            case FILTER_DATE_RANGE: // position not applicable
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                // Makes only dates from today forward selectable.
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now());
                builder.setCalendarConstraints(constraintsBuilder.build());
                builder.setTitleText("Select Event Date");
                builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen);
                MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
                datePicker.show(getSupportFragmentManager(), "DatePicker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Pair<Long, Long> dates = (Pair<Long, Long>) selection;
                        Date eventStartDateFilter = new Date(dates.first);
                        Date eventStopDateFilter = new Date(dates.second);
                        String eventStartDateStr = DateUtil.toStringFormatMMMddyyyy(eventStartDateFilter);
                        String eventStopDateStr = DateUtil.toStringFormatMMMddyyyy(eventStopDateFilter);

                        spinnerDateRange.setText(String.format("%s to %s", eventStartDateStr, eventStopDateStr));
                        ArrayList<Videos> filteredList = new ArrayList<>();
                        if (bodyVideosList.size() > 0) {
                            for (int i = 0; i < bodyVideosList.size(); i++) {
                                Date eventStartDate = DateUtil.parseDataFromFormat20(bodyVideosList.get(i).getStartDate());
                                Calendar cal = Calendar.getInstance(); // locale-specific
                                cal.setTime(eventStartDate);
                                cal.set(Calendar.HOUR_OF_DAY, 8);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                if (isWithinRange(eventStartDateFilter, eventStopDateFilter, new Date(cal.getTimeInMillis()))) {
                                    filteredList.add(bodyVideosList.get(i));
                                }
                            }
                        }
                        populateListView(filteredList);
                    }
                });

                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker.dismiss();
                    }
                });

                datePicker.setCancelable(true);
                break;
            case FILTER_CATEGORIES:
                if (position == 0) {
                    populateListView(bodyVideosList);
                } else {
                    ArrayList<Videos> filteredList = new ArrayList<>();
                    if (bodyVideosList.size() > 0) {
                        for (int i = 0; i < bodyVideosList.size(); i++) {
                            if (bodyVideosList.get(i).getCatID() == position) {
                                filteredList.add(bodyVideosList.get(i));
                            }
                        }
                    }
                    populateListView(filteredList);
                }
                break;
            case FILTER_TICKETS:
                if (position == 0) { // Either
                    populateListView(bodyVideosList);
                } else {
                    String selected = spinnerTickets.getSelectedItem().toString();
                    int required = 0;
                    if (selected.contentEquals("Required")) {
                        required = 1;
                    }
                    ArrayList<Videos> filteredList = new ArrayList<>();
                    if (bodyVideosList.size() > 0) {
                        for (int i = 0; i < bodyVideosList.size(); i++) {
                            if (bodyVideosList.get(i).getTkReq() == required) {
                                filteredList.add(bodyVideosList.get(i));
                            }
                        }
                    }
                    populateListView(filteredList);
                }
                break;
            case FILTER_PRICE:
                String selected = spinnerPrice.getSelectedItem().toString();
                if (spinnerPrice.getSelectedItemPosition() == 0) {
                    populateListView(bodyVideosList);
                } else {
                    if (selected.equalsIgnoreCase("FREE")) {
                        ArrayList<Videos> filteredList = new ArrayList<>();
                        if (bodyVideosList.size() > 0) {
                            for (int i = 0; i < bodyVideosList.size(); i++) {
                                double genAd = bodyVideosList.get(i).getGenAdmission();
                                if (genAd == 0.0) {
                                    filteredList.add(bodyVideosList.get(i));
                                }
                            }
                        }
                        populateListView(filteredList);
                    } else if (selected.equalsIgnoreCase("MORE")) {
                        ArrayList<Videos> filteredList = new ArrayList<>();
                        if (bodyVideosList.size() > 0) {
                            for (int i = 0; i < bodyVideosList.size(); i++) {
                                double genAd = bodyVideosList.get(i).getGenAdmission();
                                if (genAd > 50) {
                                    filteredList.add(bodyVideosList.get(i));
                                }
                            }
                        }
                        populateListView(filteredList);
                    } else {
                        double selectedPrice = Double.parseDouble(spinnerPrice.getSelectedItem().toString().split(" ")[2].substring(1));
                        ArrayList<Videos> filteredList = new ArrayList<>();
                        if (bodyVideosList.size() > 0) {
                            for (int i = 0; i < bodyVideosList.size(); i++) {
                                double genAd = bodyVideosList.get(i).getGenAdmission();
                                if (genAd <= selectedPrice && genAd > 0.0) {
                                    filteredList.add(bodyVideosList.get(i));
                                }
                            }
                        }
                        populateListView(filteredList);
                    }
                }
                break;
        }
    }

    public boolean isWithinRange(Date from, Date until, Date event) {
        return (event.after(from) || event.equals(from)) && (event.equals(until) || event.before(until));
    }

    @Override
    public void onVideoPlay(String videoID) {
        if (TextUtils.isEmpty(videoID) || "null".equalsIgnoreCase(videoID) || "0".equalsIgnoreCase(videoID)) {
            // showAlert(R.string.msg_no_offer_service_thistime);
            return;
        }

        // Show Youtube
        if (wvYouTube.getVisibility() == View.GONE) {
            wvYouTube.setVisibility(View.VISIBLE);
            resumePlayer();
        }
        mapFragment.getView().setVisibility(View.GONE);

        String videoUrl = "https://www.youtube.com/embed/";
        videoUrl += videoID;
        wvYouTube.loadUrl(videoUrl);
    }

    private void destroyPlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onDestroy", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

    private void resumePlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onResume", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
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
        btnShowFilter.setVisibility(View.GONE);
        btnPaySend.setVisibility(View.GONE);
        btnPayRequest.setVisibility(View.GONE);
        scanEventBtn.setVisibility(View.GONE);
        panelPayOptions.setVisibility(View.GONE);
    }

    private void showStatusBar() {
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnShowFilter.setVisibility(View.VISIBLE);
        btnPaySend.setVisibility(View.VISIBLE);
        btnPayRequest.setVisibility(View.VISIBLE);
        scanEventBtn.setVisibility(View.VISIBLE);
        panelPayOptions.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.event:
                        startActivity(new Intent(mContext, ActivityCreateEvent.class));
                        return true;
                    case R.id.advertise:
                        Intent intent = new Intent(mContext, ActivityAdvertiseWithUs.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.menu_event);
        popupMenu.show();
    }

    @Override
    public void onStateSelected(String statePrefix) {
        spinnerLocation.setText(statePrefix);
        ArrayList<Videos> filteredList = new ArrayList<>();
        if (statePrefix.contentEquals("All")) {
            populateListView(bodyVideosList);
        } else {
            if (bodyVideosList.size() > 0) {
                for (int i = 0; i < bodyVideosList.size(); i++) {
                    String State = bodyVideosList.get(i).getState();
                    if (State.contentEquals(statePrefix)) {
                        filteredList.add(bodyVideosList.get(i));
                    }
                }
            }
            populateListView(filteredList);
        }
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    final long DELAY_MS = 100; // This is the delay in milliseconds before task is to be executed.
    long PERIOD_MS = 8000; // This is the time in milliseconds between successive task executions.

    int currentPage = 0;
    Timer timer;
    int currPosition = 0;
    boolean end = false;

    // private Handler headerHandler = new Handler();
    private class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            if (currPosition == topAdsVideoIDs.size() - 1) {
                end = true;
            } else if (currPosition == 0) {
                end = false;
            }
            if (!end) {
                currPosition++;
            } else {
                currPosition = 0;
            }
            carousel.smoothScrollToPosition(currPosition);
            selectedAdId = topAdsVideoIDs.get(currPosition);
            selectedTopAdVideo = topAdVideoArrayList.get(currPosition);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext != null) {
                        tvHeadline.setText(selectedTopAdVideo.getHeadLine());
                        Glide.with(mContext)
                                .load(String.format("http://img.youtube.com/vi/%s/0.jpg", topAdsVideoIDs.get(currPosition)))
                                .centerCrop()
                                .into(imgBanner);
                    }
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void getAds() {

        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());

        String extraParams =
                "&mode=" + "getAds";
        baseUrl += extraParams;
        new ApiUtil(this).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        topAdsVideoIDs.clear();
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bodyVideoObject = jsonArray.getJSONObject(i);
                            TopAdVideo video = gson.fromJson(bodyVideoObject.toString(), TopAdVideo.class);
                            topAdVideoArrayList.add(video);
                            topAdsVideoIDs.add(video.getLink());
                        }

                        if (timer == null) {
                            timer = new Timer();
                            // timer = new Timer("Timer-Ads"); // This will create a new Thread
                        }
                        timer.scheduleAtFixedRate(new AutoScrollTask(), 100, PERIOD_MS);

                        // Load first index
                        selectedAdId = topAdsVideoIDs.get(0);
                        selectedTopAdVideo = topAdVideoArrayList.get(0);
                        if (!TextUtils.isEmpty(selectedAdId)) {
                            imgBanner.setVisibility(View.VISIBLE);
                            btnPlayV.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.INVISIBLE);
                            // mImageLoader.displayImage(String.format("http://img.youtube.com/vi/%s/0.jpg", selectedAdId), imgBanner, mImageOptions);
                            Glide.with(mContext)
                                    .load(String.format("http://img.youtube.com/vi/%s/0.jpg", topAdsVideoIDs.get(currPosition)))
                                    .centerCrop()
                                    .into(imgBanner);
                            tvHeadline.setText(topAdVideoArrayList.get(currPosition).getHeadLine());
                        }

                        // The ItemDecoration gives the current (centered) item horizontal margin so that
                        // it doesn't occupy the whole screen width. Without it the items overlap
                        HorizontalMarginItemDecoration itemDecoration = new HorizontalMarginItemDecoration(mContext, R.dimen.viewpager_current_item_horizontal_margin);
                        carousel.addItemDecoration(itemDecoration);
                        carousel.setAdapter(new CarouselVRAdapter(mContext, topAdsVideoIDs, new CarouselVRAdapter.CarouselItemListener() {
                            @Override
                            public void onItemClicked(int position) {
                                selectedAdId = topAdsVideoIDs.get(position);
                                selectedTopAdVideo = topAdVideoArrayList.get(position);
                                if (!TextUtils.isEmpty(selectedAdId)) {
                                    imgBanner.setVisibility(View.VISIBLE);
                                    btnPlayV.setVisibility(View.VISIBLE);
                                    webView.setVisibility(View.INVISIBLE);
                                    Glide.with(mContext)
                                            .load(String.format("http://img.youtube.com/vi/%s/0.jpg", selectedAdId))
                                            .centerCrop()
                                            .into(imgBanner);
                                    tvHeadline.setText(selectedTopAdVideo.getHeadLine());
                                }
                            }
                        }));
                    } else {
                        showToastMessage("No ads found");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onResponseError(String msg) {
                showToastMessage(msg);
            }

            @Override
            public void onServerError() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideKeyboard();

        resumePlayer();

        //getTemperature(tvTemperature);

        getAds();
        //headerHandler.postDelayed(headerRunnable, 3000); // Slide duration 3 seconds
    }

    private void getEvents() {
        if (getLocation()) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());

            String extraParams =
                    "&mode=" + "bodyVideos";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            // GoogleCertProvider.install(mContext);
            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("temp", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            /*JSONObject jsonObject = jsonArray.getJSONObject(0)*//*new JSONObject(response)*//*;
                            if (!jsonObject.isNull("temp")) {

                                if (appSettings.getTemperatureUnitStatus() == 0) {
                                    tvTemperature.setText(String.format("%d °F", jsonObject.optInt("temp")));
                                } else {
                                    tvTemperature.setText(String.format("%d °C", (int) fahrenheitToCelsius(jsonObject.optInt("temp"))));
                                }
                            }*/

                            // List<String> topAdsVideoIDs = Arrays.asList("84bPknTEML8", "oauQu9Wm14U", "AOYACk7m7Fk", "8XGUHITLymI", "uMYx5gRQlxQ", "xjPi6IcSH_Q");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showMessage(mContext, jsonObject.getString("msg"));
                            } else {
                                bodyVideosList.clear();
                                ArrayList<Videos> bodyVideos = new ArrayList<>();
                                Gson gson = new Gson();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject bodyVideoObject = jsonArray.getJSONObject(i);
                                    Videos video = gson.fromJson(bodyVideoObject.toString(), Videos.class);
                                    bodyVideos.add(video);
                                }

                                /*Videos test1 = new Videos();
                                test1.setCreator("Jane Doe");
                                test1.setGenAdmission(23.99);
                                test1.setLocation("Hope Lutheran");
                                test1.setPOC("Bryan Webber");
                                test1.setLat(41.581805);
                                test1.setLon(-93.8078198);
                                test1.setStartDate("2023-01-28T00:00:00");
                                test1.setStopDate("2023-01-31T00:00:00");
                                test1.setAddressFULL("Hope Lutheran, 925 Jordan Creek Pkwy, West Des Moines, IA 50266, USA");
                                test1.setProdID(0);
                                test1.setSellerID(1434505);
                                test1.setTimeDesc("Opening Saturday 10:30am thru 6pm{}Opening Sunday 9:30am thru 5pm");
                                test1.setZip(50266);
                                test1.setEmail("bryanlwebber@gmail.com");
                                test1.setState("US");
                                test1.setCatID(3);
                                test1.setTkReq(1);
                                test1.setLink("HGl75kurxok");
                                test1.setTitle("Studio Ghibli Chill Collection Pt. 1");
                                test1.setHeadLine("Sleep Piano Music");
                                test1.setDescript("As the melody sings a tune to your heart\n" +
                                        "Like a drug it Soothes the pain");
                                bodyVideos.add(0, test1);

                                Videos test2 = new Videos();
                                test2.setCreator("Rob Smith");
                                test2.setGenAdmission(2953.23);
                                test2.setLocation("Hope Lutheran");
                                test2.setPOC("Bryan Webber");
                                test2.setLat(41.581805);
                                test2.setLon(-93.8078198);
                                test2.setStartDate("2023-02-14T00:00:00");
                                test2.setStopDate("2023-02-15T00:00:00");
                                test2.setAddressFULL("Hope Lutheran, 925 Jordan Creek Pkwy, West Des Moines, IA 50266, USA");
                                test2.setProdID(0);
                                test2.setSellerID(1434505);
                                test2.setTimeDesc("Opening Saturday 10:30am thru 6pm{}Opening Sunday 9:30am thru 5pm");
                                test2.setZip(50266);
                                test2.setEmail("bryanlwebber@gmail.com");
                                test2.setState("US");
                                test2.setTkReq(0);
                                test2.setLink("p02aYkdc0a4");
                                test2.setTitle("Studio Ghibli Chill Collection Pt. 2");
                                test2.setDescript("Closing your eyes to vision peacefulness\n" +
                                        "Leaving your troubled day behind you\n" +
                                        "Your mental lays in another world");
                                bodyVideos.add(0, test2);*/
                                bodyVideosList.addAll(bodyVideos);
                                populateListView(bodyVideos);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                }
            });

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    private void populateListView(ArrayList<Videos> bodyVideos) {
        showEventMarkers(bodyVideos);
        if (bodyVideos.size() > 0) {
            tvResults.setVisibility(View.VISIBLE);
            if (bodyVideos.size() == 1) {
                tvResults.setVisibility(View.VISIBLE);
                tvResults.setText(String.format("1 Event", bodyVideos.size()));
            } else {
                tvResults.setText(String.format("%s Events", bodyVideos.size()));
            }
        } else {
            tvResults.setVisibility(View.VISIBLE);
            tvResults.setText("No Events");
        }

        eventAdapter = new HomeEventsAdapter(mContext, bodyVideos, youtubeVideoListener, new HomeEventsAdapter.RecyclerViewClickListener() {
            @Override
            public void onShare(View view, int position) {

                Intent intent = new Intent(mContext, InviteFriendToEventActivity.class);
                intent.putExtra("event", bodyVideos.get(position));
                /*intent.putExtra("co", bodyVideos.get(position).getPOC());
                intent.putExtra("prodID", bodyVideos.get(position).getProdID());
                intent.putExtra("amt", bodyVideos.get(position).getGenAdmission());*/
                startActivity(intent);
            }

            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(mContext, ActivityPayCart.class);
                intent.putExtra("mode", MODE_CART);
                intent.putExtra("event", bodyVideos.get(position));
                /*intent.putExtra("co", bodyVideos.get(position).getPOC());
                intent.putExtra("prodID", bodyVideos.get(position).getProdID());
                intent.putExtra("amt", bodyVideos.get(position).getGenAdmission());*/
                startActivity(intent);
            }

            @Override
            public void onDetails(View view, int position) {
                Intent intent = new Intent(mContext, ActivityEventDetails.class);
                intent.putExtra("eventDetails", bodyVideos.get(position));
                startActivity(intent);
            }

            @Override
            public void onRemoveItem(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to remove event '" + bodyVideosList.get(position).getTitle() + "'?")
                        .setCancelable(false)
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                bodyVideosList.remove(position);
                                populateListView(bodyVideosList);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }

            @Override
            public void onClickDonate(View view, int position) {
                doDonate();
            }
        });
        rcvEvents.setAdapter(eventAdapter);
    }

    private void doDonate() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_donate, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        final CheckBox chkAnonymous = dialogView.findViewById(R.id.chkAnonymous);
        final EditText edtTips = (EditText) dialogView.findViewById(R.id.edtTips);

        // Button Actions
        dialogView.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isAnonymous = chkAnonymous.isChecked();
                showToastMessage("Thank you");
                inputDlg.dismiss();
            }
        });

        // Button Actions
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputDlg.dismiss();
            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void pausePlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(msgReceiver);
        destroyPlayer();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public void zoomMapPin(int position) {
        wvYouTube.setVisibility(View.GONE);
        pausePlayer();
        mapFragment.getView().setVisibility(View.VISIBLE);

        if (googleMap != null) {
            Videos bodyVideo = bodyVideosList.get(position);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(bodyVideo.getLat(), bodyVideo.getLon()))
                    .zoom(15)
                    .bearing(0)
                    .build();
            //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnShowFilter) {
            if (panelFilterOptions.getVisibility() == View.GONE) {
                panelFilterOptions.setVisibility(View.VISIBLE);
            } else {
                panelFilterOptions.setVisibility(View.GONE);
            }
        } else if (viewId == R.id.tabPaySend) {
           /* Intent intent = new Intent(mContext, ConnectionActivity.class);
            intent.putExtra("page", ConnectionActivity.PANEL_PAY_SEND);
            startActivity(intent);*/

            Intent intent = new Intent(mContext, ActivityPayCart.class);
            intent.putExtra("mode", MODE_PAY_SEND);
            startActivity(intent);
        } else if (viewId == R.id.tabPayRequest) {
            /*Intent intent = new Intent(mContext, ConnectionActivity.class);
            intent.putExtra("page", ConnectionActivity.PANEL_PAY_RECEIVE);
            startActivity(intent);*/

            startActivity(new Intent(mContext, ActivityPayRequest.class));
        } else if (viewId == R.id.btnScanEvent) {
            startActivity(new Intent(mContext, QRCodeActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted) {
            if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {

            } else if (requestCode == PERMISSION_REQUEST_CODE_GALLERY) {

            }
        } else {
            showAlert(R.string.request_permission_hint);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        showEventMarkers(bodyVideosList);
    }

    private void showEventMarkers(ArrayList<Videos> bodyVideosList) {
        if (googleMap == null)
            return;

        if (!markerList.isEmpty()) {
            for (Marker marker : markerList) {
                marker.remove();
            }
        }
        markerList.clear();

        // For HeapMap
        List<LatLng> latLngs = new ArrayList<>();

        // For Normal Map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < bodyVideosList.size(); i++) {
            Videos rest = bodyVideosList.get(i);

            LatLng resPos = new LatLng(rest.getLat(), rest.getLon());

            latLngs.add(resPos);

            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(resPos)
                    .title(rest.getTitle())
            );
            newMarker.setTag(i);
            markerList.add(newMarker);
            builder.include(resPos);
        }

        if (bodyVideosList.isEmpty()) {
            if (!getUserLat().isEmpty() && !getUserLon().isEmpty()) {
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(Double.parseDouble(getUserLat()), Double.parseDouble(getUserLon())))
                        .zoom(11)
                        .bearing(0)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            }
        } else {
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = dpToPx(mContext, 204);
            int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            try {
                googleMap.moveCamera(cu);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create the gradient.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        if (!latLngs.isEmpty()) {
            // Create the tile provider.
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .gradient(gradient)
                    .radius(45)
                    .build();

            // Add a tile overlay to the map, using the heat map tile provider.
            overlayHeapMap = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int listPos = (int) marker.getTag();
        rcvEvents.smoothScrollToPosition(listPos);
        return false;
    }
}