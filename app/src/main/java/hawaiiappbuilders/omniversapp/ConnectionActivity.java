package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.appointment.ApptUtil.NEW_APPOINTMENT;
import static hawaiiappbuilders.omniversapp.dialog.ShareLocationDialog.MY_LOCATION;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_NEW_APPT;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_TEXT_MESSAGE_CHAT_ACTIVE;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.ACTION_UPDATE_APPT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import hawaiiappbuilders.omniversapp.adapters.AddInvoiceItemAdapter;
import hawaiiappbuilders.omniversapp.adapters.AllMessagesAdapter;
import hawaiiappbuilders.omniversapp.adapters.CalTimeAdapter;
import hawaiiappbuilders.omniversapp.adapters.CalTimeAgendaAdapter;
import hawaiiappbuilders.omniversapp.adapters.CallLogAdapter;
import hawaiiappbuilders.omniversapp.adapters.CustomContactList;
import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.adapters.DataAdapter;
import hawaiiappbuilders.omniversapp.adapters.GroupRecyclerAdapter;
import hawaiiappbuilders.omniversapp.adapters.InvoiceAdapter;
import hawaiiappbuilders.omniversapp.adapters.MessageListAdapter;
import hawaiiappbuilders.omniversapp.adapters.PhoneSearchAdapter;
import hawaiiappbuilders.omniversapp.adapters.RecyclerViewListener;
import hawaiiappbuilders.omniversapp.adapters.StoreCatAdapter;
import hawaiiappbuilders.omniversapp.adapters.StoreItemAdapter;
import hawaiiappbuilders.omniversapp.adapters.TasksAdapter;
import hawaiiappbuilders.omniversapp.contacts.AutocompleteContactsAdapter2;
import hawaiiappbuilders.omniversapp.dialog.AboveAddressHistoryDialog;
import hawaiiappbuilders.omniversapp.dialog.ShareLocationDialog;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.localdb.HistoryDataSource;
import hawaiiappbuilders.omniversapp.location.Constants;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressIntentService;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressResultReceiver;
import hawaiiappbuilders.omniversapp.meeting.models.User;
import hawaiiappbuilders.omniversapp.meeting.utilities.PreferenceManager;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.messaging.OnGetTokenListener;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.model.AddInvoiceItem;
import hawaiiappbuilders.omniversapp.model.AlarmMeetDataManager;
import hawaiiappbuilders.omniversapp.model.CalendarData;
import hawaiiappbuilders.omniversapp.model.CallHistory;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.GroupInfo;
import hawaiiappbuilders.omniversapp.model.HistoryData;
import hawaiiappbuilders.omniversapp.model.InvoiceItem;
import hawaiiappbuilders.omniversapp.model.Note;
import hawaiiappbuilders.omniversapp.model.StoreCate;
import hawaiiappbuilders.omniversapp.model.StoreItem;
import hawaiiappbuilders.omniversapp.model.TaskInfo;
import hawaiiappbuilders.omniversapp.notes.NotesAdapter;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.PhonenumberUtils;
import hawaiiappbuilders.omniversapp.utils.RightDrawableOnTouchListener;
import hawaiiappbuilders.omniversapp.utils.ViewUtil;
import hawaiiappbuilders.omniversapp.videocall.MakeCallActivity;
import hawaiiappbuilders.omniversapp.videocall.models.AgoraTokenModel;
import hawaiiappbuilders.omniversapp.videocall.models.StartVideoModel;
import timber.log.Timber;



public class ConnectionActivity extends BaseActivity implements OnClickListener, DataAdapter.RecyclerViewItemClickListener, ShareLocationDialog.ShareLocationButtonClickListener {
    public static final String TAG = ConnectionActivity.class.getSimpleName();
    /*
    const int PM=2359,Advertising = 2300, ZintaDirect = 2325, ZintaExportRequest = 2330, ZintaLoadRequest = 2335, Printing = 2338, Shipping = 2340, Video = 2355, Donation = 2360, Order = 2365, PayStub = 2375, DirectPay = 2377, Survey = 2380, Delivery = 2385, Registrations = 2399, Valet = 2430;
    */

    CustomContactModel selectedModel;
    private MessageDataManager dm = null;
    PhonenumberUtils phonenumberUtils;

    Toolbar toolbar;

    private DrawerLayout mDrawerLayout;

    RecyclerView grpRecycler;
    ArrayList<GroupInfo> grplist;
    GroupRecyclerAdapter groupAdapter;
    private ArrayList<ContactInfo> contactList = new ArrayList<>();
    ArrayAdapter contactAdapter;

    ImageView ivSearchContact;
    AboveAddressHistoryDialog dialog;
    View panelMessage;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();
    private ImageView ivNewMsg, ivVideo, ivEmail;
    private EditText edtChatMessage;
    View panelMessageAll;
    private RecyclerView mAllMessageRecycler;
    private AllMessagesAdapter mAllMessageAdapter;

    private static final long INTERVAL_GET_MSG = 10000;
    Handler handlerMsg = new Handler() {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);

            if (weakReference.get() == null)
                return;

            getMessages(false);
            sendEmptyMessageDelayed(0, INTERVAL_GET_MSG);
        }
    };

    View panelCalls;
    RecyclerView rcvPhoneList;

    AutoCompleteTextView edtPhoneNumber;
    PhoneSearchAdapter phoneSearchAdapter;

    ArrayList<CallHistory> phoneHistorys = new ArrayList<>();
    CallLogAdapter callLogAdapter;

    View panelLocation;
    GoogleMap googleMap;
    Marker markerLocation;
    EditText tvContactAddress;
    GeocodeAddressResultReceiver mResultReceiver;

    View panelPay;
    TextView tabPayReceived;
    TextView tabPayShare;
    View panelPayReceived;

    EditText edtUserName;
    TextView txtPayUserInfo;
    EditText edtPayNotes;
    Handler mCheckNameHandler;
    EditText edtAmount;
    Button btnPayNow;
    AddInvoiceItemAdapter addInvoiceItemAdapter;
    ImageView ivAddInvoice;
    ConstraintLayout layoutCreateInvoice;
    RecyclerView rcvInvoiceItems;

    Button btnCancelCreateInvoice;
    EditText tvInvoiceQTY;
    EditText tvInvoiceDesc;
    EditText tvInvoiceAmt;
    RecyclerView rcvInvoices;
    ArrayList<InvoiceItem> invoiceList = new ArrayList<>();
    InvoiceAdapter adapterInvoice;
    String orderId = "0";

    View panelPayShare;

    View panelNotes;
    TextView tvNotes;
    EditText edtNotes;

    ArrayList<CustomContactModel> contactModels;

    public static final int PANEL_MESSAGE = 0;
    public static final int PANEL_PHONE = 1;
    public static final int PANEL_LOCATION = 2;
    public static final int PANEL_PAY_RECEIVE = 3;
    public static final int PANEL_PAY_SEND = 4;
    public static final int PANEL_NOTES = 5;
    public static final int PANEL_MEET = 6;
    public static final int PANEL_TASKS = 7;

    View panelMeetAddSlider;
    Button btnMeetInfoSave;
    View panelMeetTitle;
    TextView tvMeetTitle;
    TextView tvMeetTime;
    Calendar calStart = Calendar.getInstance();
    Calendar calEnd = Calendar.getInstance();
    Float tz = 0.0F;
    String strLocation = "";
    String strEmail = "";
    String strPhone = "";

    View panelTasks;
    View panelProjectList;

    View panelStoreCategory;
    View btnShareCate;
    Button btnViewProj;
    EditText edtStoreCateName;
    Button btnAddStoreCate;
    RecyclerView recvStoreCate;
    ArrayList<StoreCate> storeCateList = new ArrayList<>();
    StoreCatAdapter storeCateAdapter;

    int selectedCateIndex = 0;
    View panelStoreItem;
    View btnBackCate;
    TextView tvStoreCate;
    View btnSpeechToText;
    TextToSpeech tts;
    EditText edtStoreItemName;
    Button btnAddStoreItem;
    RecyclerView recvStoreItem;
    StoreItemAdapter storeItemAdapter;

    View panelTasksList;
    Button btnViewLists;
    EditText edtItem;
    View btnClearTaskInput;
    CheckBox chkAll;
    ArrayList<TaskInfo> taskList = new ArrayList<>();
    RecyclerView recvTasks;
    CheckBox chkTaskShowCompleted;
    TasksAdapter tasksAdapter;

    RecyclerView rvNotes;

    View panelCal;
    TextView tvMonth;
    String[] monthTitleString;
    private Calendar calSelectedDate = Calendar.getInstance();
    private final List<TextView> dayLabelTextViews = new ArrayList<>();
    private final List<TextView> dateTextViews = new ArrayList<>();
    private final List<ImageView> selectedImageViews = new ArrayList<>();
    View panelWeekDays;
    CalendarView calView;
    RecyclerView recyclerAppts;
    CalTimeAdapter calAdapter;
    private ArrayList<CalendarData.Data> mCalendarDataList = new ArrayList<>();
    private ArrayList<String> mCalendarDataMonths = new ArrayList<>();

    View panelAgenda;
    View ivFilterAgenda;
    boolean isFilterAgenda = false;
    EditText edtAgendaSearch;
    RecyclerView recyclerAgenda;
    CalTimeAgendaAdapter calAgendaAdapter;
    View btnAddAppt;
    private ArrayList<CalendarData.Data> mAgendaDataList = new ArrayList<>();

    private static final int REQUEST_CHANGE_CONTACT = 100;
    private static final int REQUEST_MEET = 200;
    private static final int REQUEST_SEARCH_CONTACT = 300;
    private static final int REQUEST_SEARCH_MEET = 400;

    public static final int REQUEST_ADD_AS_NEW_CONTACT = 500;
    public static final int REQUEST_PAY_INVOICE = 500;

    public static ConnectionActivity connectionActivity = null;
    AutocompleteContactsAdapter2 contactsListAdapter2;

    EditText spinnerContact2;
    TextWatcher contactNameInputWatcuer;
    RecyclerView spinnerContactSearchResults;
    CustomContactList contactsListAdapter;

    private Spinner spinnerContact;


    private int SPINNER_MODE = 1;

    static int uid = 0;
    static int expirationTimeInSeconds = 43200;
    private ContactInfo contactInfo;
    private StartVideoModel videoModel;
    private DataUtil dataUtil;

    private String agoraChannelName = "";

    private String agoraToken = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshContacts() {
        if (SPINNER_MODE == 1) {
            updateContactsList();
        } else {
            updateContactsSearchList();
        }
    }

    public void initializeSpinnerContact() {
        if (SPINNER_MODE == 1) {
            ViewUtil.setVisible(spinnerContact);
            ViewUtil.setGone(spinnerContact2);
        } else {
            ViewUtil.setGone(spinnerContact);
            ViewUtil.setVisible(spinnerContact2);
        }
    }

    public boolean isContactSelected() {
        boolean isContactSelected;
        if (SPINNER_MODE == 1) {
            isContactSelected = spinnerContact.getSelectedItemPosition() > 0;
        } else {
            // if contact text field is empty
            // if contactInfoSelected is not null
            // if contactInfoSelected mlid > 0
            isContactSelected = !spinnerContact2.getText().toString().isEmpty() || (contactInfoSelected != null && contactInfoSelected.getMlid() > 0);
        }
        return isContactSelected;
    }

    public boolean noContactSelected() {
        boolean isContactSelected;
        if (SPINNER_MODE == 1) {
            isContactSelected = spinnerContact.getSelectedItemPosition() == 0;
        } else {
            isContactSelected = spinnerContact2.getText().toString().isEmpty();
        }
        return isContactSelected;
    }

    public ContactInfo getSelectedContact() {
        ContactInfo contactInfo;
        if (SPINNER_MODE == 1) {
            contactInfo = dm.getContactInfoById(contactsListAdapter.getItem(spinnerContact.getSelectedItemPosition()).id);
        } else {
            contactInfo = contactInfoSelected;
        }
        return contactInfo;
    }

    public void setSelectedContactIndex(int i, CustomContactModel model) {
        if (SPINNER_MODE == 1) {
            spinnerContact.setSelection(i);
        } else {
            Spanned name = DataUtil.getCompanyAndName(model);
            if (!name.toString().isEmpty()) {
                showClearIcon();
            }

            Log.e("checkingId", "MLID::: " + model.getMlid());

            ((TextView) findViewById(R.id.txtMLID)).setText("MLID: " + model.getMlid());
            selectedModel = model;
            contactInfoSelected = dm.getContactInfoById(model.getId());

            spinnerContact2.removeTextChangedListener(contactNameInputWatcuer);
            spinnerContact2.setText(name);
            spinnerContact2.addTextChangedListener(contactNameInputWatcuer);

//            update number in call view
            checkContactLikeFavorStatus();
        }
    }

    @Override
    public void onBackPressed() {
        boolean showAllMessages = noContactSelected();
        if (showAllMessages) {
            // super.onBackPressed();
        } else {
            //  spinnerContact.setSelection(0);
            panelMessageAll.setVisibility(View.GONE);
        }
        finish();
    }

    int numAddInvoiceItems = 7; // initial number of items a user can add for MultiLineInvoice
    PreferenceManager preferenceManager;
    ArrayList<AddInvoiceItem> mInvoiceItems;

    private void setupAddInvoiceItemRecyclerView() {
        mInvoiceItems = new ArrayList<>();


        for (int i = 0; i < numAddInvoiceItems; i++) {
            AddInvoiceItem newItem = new AddInvoiceItem();
            newItem.setQty(0);
            newItem.setAmt(0);
            newItem.setDesc("");
            mInvoiceItems.add(newItem);
        }

        rcvInvoiceItems.setLayoutManager(new LinearLayoutManager(mContext));

       /* DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.divider));*/

        // rcvInvoiceItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        addInvoiceItemAdapter = new AddInvoiceItemAdapter(mContext, mInvoiceItems, new AddInvoiceItemAdapter.OnClickInvoiceItemListener() {
            @Override
            public void updateAddInvoiceItem(int index, AddInvoiceItem item) {
                // updates invoice item
                mInvoiceItems.set(index, item);
            }
        });
        rcvInvoiceItems.setAdapter(addInvoiceItemAdapter);
        addInvoiceItemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        try {
            dataUtil = new DataUtil(this, ConnectionActivity.class.getSimpleName());
            phonenumberUtils = new PhonenumberUtils(this);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("My Community");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


            ivAddInvoice = findViewById(R.id.ivAddInvoice);
            layoutCreateInvoice = findViewById(R.id.linearLayout13);
            rcvInvoiceItems = findViewById(R.id.rcvInvoiceItems);
            setupAddInvoiceItemRecyclerView();
            btnCancelCreateInvoice = findViewById(R.id.btnCancelCreateInvoice);
            btnCancelCreateInvoice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInvoiceItems.clear();
                    addInvoiceItemAdapter.notifyDataSetChanged();
                    layoutCreateInvoice.setVisibility(View.GONE);
                }
            });
            ivAddInvoice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAddInvoiceItemRecyclerView();
                    layoutCreateInvoice.setVisibility(View.VISIBLE);
                }
            });
            SPINNER_MODE = 2;
            spinnerContact2 = findViewById(R.id.spinnerContact2);

            spinnerContactSearchResults = findViewById(R.id.rv_search_results);
            // spinnerContact2.setThreshold(1); //will start working from first character
            spinnerContact = findViewById(R.id.spinnerContact);
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerContact2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        });*/
            mContext = this;
            initializeNotesList();

            dm = new MessageDataManager(mContext);
            updateContactsSearchList();

            contactModels = contactsListAdapter2.performFilter("");
            spinnerContactSearchResults.setVisibility(View.VISIBLE);
            contactsListAdapter2.notifyDataSetChanged();

            contactNameInputWatcuer = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().isEmpty()) {
                        contactModels = contactsListAdapter2.performFilter("");
                    } else {
                        contactModels = contactsListAdapter2.performFilter(s.toString());
                    }

                    spinnerContactSearchResults.setVisibility(View.VISIBLE);
                    contactsListAdapter2.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            spinnerContact2.addTextChangedListener(contactNameInputWatcuer);

        /*spinnerContact2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    ContactInfo contactInfo = dm.getContactInfoBySenderId(appSettings.getUserId());
                    if (contactInfo != null) {
                        getMessagesFromLocalDb(0, true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
            initializeSpinnerContact();
            preferenceManager = new PreferenceManager(getApplicationContext());
            // TODO:  Check for battery optimization - for video call:  Should we ask this one?
            //  checkForBatteryOptimizations();

            // Navigation Setup
            mDrawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationViewContact = findViewById(R.id.nav_contact);
            int menuItemSize = navigationViewContact.getMenu().size();
            for (int i = 0; i < menuItemSize; i++) {
                navigationViewContact.getMenu().getItem(i).setCheckable(false);
            }

            navigationViewContact.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.nav_invite) {
                                String contactEmail = "";
                                if (isContactSelected()) {
                                    ContactInfo contactInfo = getSelectedContact();
                                    contactEmail = contactInfo.getEmail();
                                }
                                Intent intent = new Intent(mContext, InviteActivity.class);
                                intent.putExtra("email", contactEmail);
                                startActivity(intent);
                            } else if (menuItem.getItemId() == R.id.nav_edit) {
                                if (isContactSelected()) {
                                    ContactInfo contactInfo = dm.getContactInfoById(contactInfoSelected.getId());
                                    // ContactInfo contactInfo = getSelectedContact();
                                    Intent intent = new Intent(mContext, ActivityAddNewContact.class);
                                    intent.putExtra("contact", contactInfo);
                                    intent.putExtra("mode", 0);
                                    startActivityForResult(intent, REQUEST_CHANGE_CONTACT);
                                }
                            } else if (menuItem.getItemId() == R.id.nav_addnew) {
                                startActivityForResult(new Intent(mContext, ActivityAddNewContact.class), REQUEST_CHANGE_CONTACT);
                            }/* else if (menuItem.getItemId() == R.id.nav_ftf) {
                            startActivity(new Intent(mContext, ActivityFTFSendReceive.class));
                        } */ else if (menuItem.getItemId() == R.id.nav_importcontacts) {
                                startActivityForResult(new Intent(mContext, ImportContactActivity.class), REQUEST_CHANGE_CONTACT);
                            } /*else if (menuItem.getItemId() == R.id.nav_editgroup) {

                        } */ else if (menuItem.getItemId() == R.id.nav_shareloc) {

                            } else if (menuItem.getItemId() == R.id.nav_settings) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                AppSettings appSettings = new AppSettings(mContext);
                                builder.setTitle("Set Video URL");
                                // Set up the input
                                final EditText input = new EditText(mContext);
                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                if (!appSettings.getVideoUrl().isEmpty() && appSettings.getVideoUrl() != null) {
                                    input.setText(appSettings.getVideoUrl());
                                }


                                builder.setView(input);

                                // Set up the buttons
                                builder.setPositiveButton("Save", (dialog, which) -> {
                                    String videoUrl = input.getText().toString();
                                    appSettings.setVideoUrl(videoUrl);
                                });
                                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                                builder.show();
                            } else if (menuItem.getItemId() == R.id.nav_home_address) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                AppSettings appSettings = new AppSettings(mContext);
                                builder.setTitle("Set Home Address");
                                // Set up the input
                                final EditText input = new EditText(mContext);
                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                if (!appSettings.getHomeAddress().isEmpty() && appSettings.getHomeAddress() != null) {
                                    input.setText(appSettings.getHomeAddress());
                                }


                                builder.setView(input);

                                // Set up the buttons
                                builder.setPositiveButton("Save", (dialog, which) -> {
                                    String homeAddress = input.getText().toString();
                                    appSettings.setHomeAddress(homeAddress);
                                });
                                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                                builder.show();
                            } else if (menuItem.getItemId() == R.id.nav_work_address) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                AppSettings appSettings = new AppSettings(mContext);
                                builder.setTitle("Set Work Address");
                                // Set up the input
                                final EditText input = new EditText(mContext);
                                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                if (!appSettings.getWorkAddress().isEmpty() && appSettings.getWorkAddress() != null) {
                                    input.setText(appSettings.getWorkAddress());
                                }


                                builder.setView(input);

                                // Set up the buttons
                                builder.setPositiveButton("Save", (dialog, which) -> {
                                    String workAddress = input.getText().toString();
                                    appSettings.setWorkAddress(workAddress);
                                });
                                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                                builder.show();
                            } /*else if (menuItem.getItemId() == R.id.nav_txt_msg) {
                            String strPhone = edtPhoneNumber.getText().toString().trim();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", strPhone, null)));

                            // open normal sms
                            *//*try{
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*//*
                        } else if (menuItem.getItemId() == R.id.nav_send_email) {
                            if (contactList.size() > 1 && isContactSelected()) {
                                ContactInfo contactInfo = getSelectedContact();
                                if (TextUtils.isEmpty(contactInfo.getEmail())) {
                                    showToastMessage("No Email Information");
                                } else {
                                    String[] supportTeamAddrs = {contactInfo.getEmail()};
                                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                                    if (intent == null) {
                                        intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                        intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "MahaloPay");
                                        intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nPowered by MahaloPay");
                                        try {
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            showToastMessage("Please install Email App to use function");
                                        }
                                    } else {
                                        intent = new Intent(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "MahaloPay");
                                        intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nPowered by MahaloPay");
                                        //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                                        intent.setType("text/html");
                                        intent.setPackage("com.google.android.gm");
                                        startActivity(Intent.createChooser(intent, "Send Location Mail"));
                                    }
                                }
                            } else {
                                showToastMessage("No Contact Selected");
                            }
                        } */ else if (menuItem.getItemId() == R.id.nav_restore) {

                                restoreUserContacts();

                            }

                            menuItem.setChecked(false);

                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here
                            return true;
                        }
                    });
            findViewById(R.id.btnMsgNav).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            });

            grpRecycler = findViewById(R.id.grpRecycler);
            grplist = dm.getAlLGroups();

            // grplist.add(new GroupInfo(0, "None", 0, 0));
            // grplist.add(new GroupInfo(0, "Friend", 0, 0));
            // grplist.add(new GroupInfo(0, "Groups", 0, 0));

            groupAdapter = new GroupRecyclerAdapter(mContext, grplist, new RecyclerViewListener() {
                @Override
                public void onItemClick(int position) {

                    if (grplist.get(position).getId() > 0) {
                        ContactInfo contactInfo = getSelectedContact();
                        if (contactInfo != null) {
                            contactInfo.setPri(grplist.get(position).getPri());
                            contactInfo.setGroupInfo(String.valueOf(grplist.get(position).getId()));
                            dm.updateContact(contactInfo);

                            groupAdapter.setPriValue(contactInfo.getPri());
                            contactList.clear();
                            refreshContacts();

                            if (spinnerContactSearchResults.getVisibility() == View.VISIBLE) {
                                spinnerContactSearchResults.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onItemDelete(int position) {

                }
            });
            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
            grpRecycler.setLayoutManager(manager);
            grpRecycler.setAdapter(groupAdapter);


            ivSearchContact = findViewById(R.id.ivSearchContact);
            ivSearchContact.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                /*if (contactList.size() > 0) {
                    Intent intent = new Intent(mContext, ActivitySearchContact.class);
                    // intent.putExtra("contacts", contactList);
                    intent.putExtra("contacts", contactModels);
                    startActivityForResult(intent, REQUEST_SEARCH_CONTACT);
                } else {
                    showToastMessage("No contact list");
                }*/
                    // todo: disable onclick of search icon
                    spinnerContactSearchResults.setVisibility(View.GONE);

                    if (spinnerContact2.getText().toString().trim().length() == 0) {
                        ((TextView) findViewById(R.id.txtMLID)).setText("MLID: 0");
                    }
                    hideKeyboard();

                    if (panelAgenda.getVisibility() == View.VISIBLE) {
                        updateAgendaItem(isFilterAgenda);
                    }
                }
            });

            findViewById(R.id.btnMessage).setOnClickListener(this);
            findViewById(R.id.btnCall).setOnClickListener(this);
            // findViewById(R.id.btnEmail).setOnClickListener(this);
            findViewById(R.id.btnLocation).setOnClickListener(this);
            findViewById(R.id.btnReschedule).setOnClickListener(this);
            findViewById(R.id.btnPay).setOnClickListener(this);
            findViewById(R.id.btnNote).setOnClickListener(this);
            findViewById(R.id.btnTasks).setOnClickListener(this);

            panelMessage = findViewById(R.id.panelMessage);
            mMessageRecycler = findViewById(R.id.recycler_gchat);
            mMessageAdapter = new MessageListAdapter(this, messageList);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) mMessageRecycler.getLayoutManager();
                    // findFirstVisibleItemPosition()
                    // findLastVisibleItemPosition()
                    // findFirstCompletelyVisibleItemPosition()
                    // findLastCompletelyVisibleItemPosition()
                    int scrollPosition = myLayoutManager.findLastVisibleItemPosition();
                    if (scrollPosition != messageList.size() - 1) {
                        ivNewMsg.setVisibility(View.VISIBLE);
                    } else {
                        ivNewMsg.setVisibility(View.GONE);
                    }
                }
            });

            ivNewMsg = findViewById(R.id.ivNewMsg);
            ivNewMsg.setVisibility(View.GONE);
            ivNewMsg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMessageRecycler.scrollToPosition(messageList.size() - 1);
                    ivNewMsg.setVisibility(View.GONE);
                }
            });

            ivVideo = findViewById(R.id.ivVideo);
            ivVideo.setVisibility(View.GONE);
            ivVideo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  callSelectedContact();
                    // todo: update jitsi to latest
                    //   startActivity(new Intent(mContext, MeetActivity.class));
                    contactInfo = getSelectedContact();
                    startVideoCallApi(contactInfo);
//                    goToVideoCallActivity();
//                    sendPushVCall();
//                      startActivity(new Intent(ConnectionActivity.this, VideoCallingAct.class).putExtra("id",contactInfo.getId())
//                               .putExtra("channel_name",channelName) .putExtra("token",token)
//                                 .putExtra("from","user")
//                         );
                }
            });

            ivEmail = findViewById(R.id.ivEmail);
            ivEmail.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contactList.size() > 0/* && isContactSelected()*/) {
                        ContactInfo contactInfo = getSelectedContact();
                        if (TextUtils.isEmpty(contactInfo.getEmail())) {
                            showToastMessage("No Email Information");
                        } else {
                            String[] supportTeamAddrs = {contactInfo.getEmail()};
                            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                            if (intent == null) {
                                intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Mahalo Pay");
                                intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nPowered by Mahalo Pay");
                                try {
                                    startActivity(intent);
                                } catch (Exception e) {
                                    showToastMessage("Please install Email App to use function");
                                }
                            } else {
                                intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Mahalo Pay");
                                intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\nPowered by Mahalo Pay");
                                //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                                intent.setType("text/html");
                                intent.setPackage("com.google.android.gm");
                                startActivity(Intent.createChooser(intent, "Send Location Mail"));
                            }
                        }
                    } else {
                        showToastMessage("No Contact Selected");
                    }
                }
            });

            showActionIcons();
            edtChatMessage = findViewById(R.id.edit_gchat_message);
            findViewById(R.id.button_gchat_send).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = edtChatMessage.getText().toString().trim();
                    if (!TextUtils.isEmpty(message)) {
                        sendMessages(message);
                    }
                }
            });
            panelMessageAll = findViewById(R.id.panelMessageAll);
            mAllMessageRecycler = findViewById(R.id.mAllMessageRecycler);

            mAllMessageAdapter = new AllMessagesAdapter(this, messageList, new AllMessagesAdapter.MessageItemListener() {
                @Override
                public void onItemClicked(int position) {
                    Message message = messageList.get(position);

                    int fromMlid = message.getFromID();
                    if (appSettings.getUserId() == fromMlid) {
                        fromMlid = message.getToID();
                    }

                    // Update Contact Spinner Item Position
                    ContactInfo contactInfo = dm.getContactInfoBySenderId(fromMlid);
                    if (contactInfo != null) {
                        contactInfoSelected = contactInfo;
                        setSelectedContactIndex(0, toModel(contactInfo));
                        getMessages(false);
                    }
                }
            });
            mAllMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
            mAllMessageRecycler.setAdapter(mAllMessageAdapter);

            // Panel Phone
            panelCalls = findViewById(R.id.panelCalls);
            edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
            phoneSearchAdapter = new PhoneSearchAdapter(mContext, R.layout.layout_spinner_contact, contactList);
            edtPhoneNumber.setAdapter(phoneSearchAdapter);
            edtPhoneNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ContactInfo contactInfo = getSelectedContact();
                    edtPhoneNumber.setText(contactInfo.getCp());
                }
            });

            rcvPhoneList = findViewById(R.id.rcvPhoneList);
            rcvPhoneList.setLayoutManager(new LinearLayoutManager(mContext));
            // todo: InvalidSetHasFixedSize
            // rcvPhoneList.setHasFixedSize(true);
            callLogAdapter = new CallLogAdapter(mContext, phoneHistorys, new CallLogAdapter.RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    CallHistory callHistory = phoneHistorys.get(position);

                    edtPhoneNumber.setText(PhonenumberUtils.getFormattedPhoneNumber(callHistory.getPhNumber()));

                /*if (callHistory.getLdbid() != 0) {
                    return;
                }*/

                    // Update Contact Spinner Item Position


                /*if (SPINNER_MODE == 1) {
                    for (int i = 0; i < contactsListAdapter.getCount(); i++) {
                        ContactInfo contactInfo = getSelectedContact();
                        if (contactInfo.getId() == callHistory.getLdbid()) {
                            setSelectedContactIndex(i, contactModels.get(i));
                            getMessages(false);
                            break;
                        }
                    }
                } else {
                    ContactInfo contactInfo = dm.getContactInfoById(callHistory.getLdbid());
                    setSelectedContactIndex(0, toModel(contactInfo));
                    getMessages(false);
                }*/
                    // updateCallHistory();
                }
            });
            rcvPhoneList.setAdapter(callLogAdapter);

            findViewById(R.id.btnPhoneFilter).setOnClickListener(this);
            findViewById(R.id.btnPhoneDot).setOnClickListener(this);
            findViewById(R.id.btnPhoneDial).setOnClickListener(this);

            // Panel Location
            panelLocation = findViewById(R.id.panelLocation);
            tvContactAddress = findViewById(R.id.tvContactAddress);
            tvContactAddress.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    String address = tvContactAddress.getText().toString().trim();
                    if (!TextUtils.isEmpty(address)) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied Text", address);
                        clipboard.setPrimaryClip(clip);

                        showToastMessage("Copied address!");
                    }

                    return false;
                }
            });

            tvContactAddress.setEnabled(true);

            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    showCurrentLocation();
                }
            });

            mResultReceiver = new GeocodeAddressResultReceiver(null, new GeocodeAddressResultReceiver.OnReceiveGeocodeListener() {

                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultCode == Constants.SUCCESS_RESULT) {
                                Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                                int requestCode = resultData.getInt(Constants.REQUEST_CODE);

                                double toLatitude = address.getLatitude();
                                double toLongitude = address.getLongitude();

                                if (markerLocation != null) {
                                    markerLocation.remove();
                                    markerLocation = null;
                                }
                                if (googleMap != null) {
                                    LatLng resPos = new LatLng(toLatitude, toLongitude);
                                    markerLocation = googleMap.addMarker(new MarkerOptions()
                                                    .position(resPos)
                                            //.title("You're here")
                                    );
                                    CameraPosition googlePlex = CameraPosition.builder()
                                            .target(resPos)
                                            .zoom(11)
                                            .bearing(0)
                                            .build();
                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                                }
                            } else {
                                // showToastMessage("Couldn't get geo location for user");
                                showCurrentLocation();
                            }
                        }
                    });
                }
            });

            findViewById(R.id.btnDirectionTo).setOnClickListener(this);
            findViewById(R.id.btnShareLocation).setOnClickListener(this);

            // Panel Pay
            panelPay = findViewById(R.id.panelPay);
            tabPayReceived = findViewById(R.id.tabPayReceived);
            tabPayShare = findViewById(R.id.tabPayShare);

            panelPayReceived = findViewById(R.id.panelPayReceived);
            edtUserName = findViewById(R.id.edtUserName);
            mCheckNameHandler = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(@NonNull android.os.Message msg) {
                    super.handleMessage(msg);

                    checkUserName();
                }
            };
            edtUserName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String userName = edtUserName.getText().toString().trim();
                    if (userName.length() > 2) {
                        mCheckNameHandler.removeMessages(0);
                        mCheckNameHandler.sendEmptyMessageDelayed(0, 2000);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            txtPayUserInfo = findViewById(R.id.txtPayUserInfo);
            //txtPayUserInfo.setVisibility(View.GONE);

            edtPayNotes = findViewById(R.id.edtPayNotes);
            edtAmount = findViewById(R.id.edtAmount);

            btnPayNow = findViewById(R.id.btnPayNow);
            btnPayNow.setOnClickListener(this);

            tvInvoiceQTY = findViewById(R.id.tvInvoiceQTY);
            tvInvoiceDesc = findViewById(R.id.tvInvoiceDesc);
            tvInvoiceAmt = findViewById(R.id.tvInvoiceAmt);
            findViewById(R.id.btnAddThisForContact).setOnClickListener(this);
            findViewById(R.id.btnListInvoices).setOnClickListener(this);

            rcvInvoices = findViewById(R.id.rcvInvoices);
            rcvInvoices.setHasFixedSize(false);
            adapterInvoice = new InvoiceAdapter(mContext, invoiceList, new InvoiceAdapter.OnClickInvoiceItemListener() {
                @Override
                public void onClickInvoiceItem(InvoiceItem invoiceItem) {
                    Intent invoiceIntent = new Intent(mContext, ActivityInvoiceDetails.class);
                    invoiceIntent.putExtra("orderId", invoiceItem.getOrderID());
                    startActivityForResult(invoiceIntent, REQUEST_PAY_INVOICE);
                }
            });
            rcvInvoices.setAdapter(adapterInvoice);

            panelPayShare = findViewById(R.id.panelPayShare);
            findViewById(R.id.tabPayReceived).setOnClickListener(this);
            findViewById(R.id.tabPayShare).setOnClickListener(this);

            tabPayReceived.setSelected(true);
            tabPayShare.setSelected(false);
            panelMessageAll.setVisibility(View.VISIBLE);

            // Panel Notes
            panelNotes = findViewById(R.id.panelNotes);
            tvNotes = findViewById(R.id.tvNotes);
            edtNotes = findViewById(R.id.edtNotes);
            edtNotes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence txt, int start, int before, int count) {
                    if (txt.toString().contains("\n")) {
                        edtNotes.setMaxLines(edtNotes.getMaxLines() + 1);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            findViewById(R.id.btnAddNote).setOnClickListener(this);

            // Panel Calc
            panelCal = findViewById(R.id.panelCal);
            panelCal.setVisibility(View.GONE);

            monthTitleString = getResources().getStringArray(R.array.array_months);


            tvMonth = findViewById(R.id.tvMonth);

            // set current month
            Calendar calendar = DateUtil.getCurrentDate();
            Date currentDate = new Date(calendar.getTimeInMillis());
            calSelectedDate.setTime(currentDate);

            tvMonth.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showHideCalc(30);
                }
            });

            week();
            dateOfWeek();
            updateWeeklyCalendar();

            NavigationView navigationViewCal = findViewById(R.id.nav_calendar);
            navigationViewCal.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.nav_aday) {

                                showHideCalc(1);
                            } else if (menuItem.getItemId() == R.id.nav_threedays) {
                                showHideCalc(3);
                            } else if (menuItem.getItemId() == R.id.nav_week) {
                                showHideCalc(7);
                            } else if (menuItem.getItemId() == R.id.nav_month) {
                                showHideCalc(30);
                            } else if (menuItem.getItemId() == R.id.nav_addappt) {
                                addNewMeet();
                            } else if (menuItem.getItemId() == R.id.nav_agenda) {
                                showAgendaData();
                            }

                            menuItem.setChecked(false);

                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here
                            return true;
                        }
                    });
            findViewById(R.id.btnCalNav).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
            findViewById(R.id.btnCalSearch).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActivitySearchMeet.class);
                    intent.putExtra("calc_list", mCalendarDataList);
                    startActivityForResult(intent, REQUEST_SEARCH_CONTACT);
                }
            });
            findViewById(R.id.btnCalToday).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    calSelectedDate = Calendar.getInstance();

                    getApptData(false);

                    updateWeeklyCalendar();

                    showHideCalc(7);
                }
            });
            findViewById(R.id.btnCalElispe).setOnClickListener(this);

            panelWeekDays = findViewById(R.id.panelWeekDays);
            calView = findViewById(R.id.calView);
            calView.setFirstDayOfWeek(Calendar.MONDAY);
            calView.setVisibility(View.GONE);
            calView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int monthOfYear, int dayOfMonth) {

                    calSelectedDate.set(Calendar.YEAR, year);
                    calSelectedDate.set(Calendar.MONTH, monthOfYear);
                    calSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    updateWeeklyCalendar();
                    showHideCalc(7);

                    getApptData(false);
                }
            });

            findViewById(R.id.btnPrevDay).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    calSelectedDate.add(Calendar.DAY_OF_YEAR, -7);

                    updateWeeklyCalendar();
                    showHideCalc(7);

                    calView.setDate(calSelectedDate.getTimeInMillis());

                    getApptData(false);
                }
            });
            findViewById(R.id.btnNextDay).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    calSelectedDate.add(Calendar.DAY_OF_YEAR, 7);

                    updateWeeklyCalendar();
                    showHideCalc(7);

                    calView.setDate(calSelectedDate.getTimeInMillis());

                    getApptData(false);
                }
            });

            recyclerAppts = findViewById(R.id.recyclerAppts);
            // todo: InvalidSetHasFixedSize
            // recyclerAppts.setHasFixedSize(true);
            recyclerAppts.setLayoutManager(new LinearLayoutManager(mContext));
            calAdapter = new CalTimeAdapter(mContext,
                    new CalTimeAdapter.RecyclerViewClickListener() {
                        @Override
                        public void onTimeClick(View view, int position) {
                            calSelectedDate.set(Calendar.HOUR_OF_DAY, position + 1);
                            calSelectedDate.set(Calendar.MINUTE, 0);

                            // Show Initical Time
                            calStart.setTime(calSelectedDate.getTime());
                            calEnd.setTime(calStart.getTime());
                            calEnd.add(Calendar.MINUTE, 30);

                            tvMeetTime.setText(String.format("%s", DateUtil.toStringFormat_7(calStart.getTime())));

                            addNewMeet();
                        }

                        @Override
                        public void onTimelineClick(View view, int position) {
                            calSelectedDate.set(Calendar.HOUR_OF_DAY, position + 1);
                            calSelectedDate.set(Calendar.MINUTE, 0);

                            openMeetSlider();

                            // Show Initical Time
                            calStart.setTime(calSelectedDate.getTime());
                            calEnd.setTime(calStart.getTime());
                            calEnd.add(Calendar.MINUTE, 30);
                            //tvMeetTime.setText(String.format("%s - %s", DateUtil.toStringFormat_7(calStart.getTime()), DateUtil.toStringFormat_7(calEnd.getTime())));
                            tvMeetTime.setText(String.format("%s", DateUtil.toStringFormat_7(calStart.getTime())));
                        }

                        @Override
                        public void onApptClick(View view, int groupPos, int position, CalendarData.Data calData) {
                        /*for (int i = 0; i < contactModels.size(); i++) {
                            if (contactModels.get(i).getId() == calData.getLdbID()) {
                                setSelectedContactIndex(i, contactModels.get(i));
                                break;
                            }
                        }*/

//                            ContactInfo attendeeMlid = dm.getContactInfoBySenderId(calData.getAttendeeMLID());
//                            if (calData.getAttendeeMLID() != 0) {
//                                setSelectedContactIndex(0, toModel(attendeeMlid));
//                            }

                            Log.e("checkingId", "sellerId: " + calData.getSellerId());

                            ContactInfo contactInfoFromSellerID = dm.getContactInfoBySenderId(calData.getSellerId());
//                            if (calData.getSellerId() != 0) {
                            setSelectedContactIndex(0, toModel(contactInfoFromSellerID));
//                            }

                            Intent intent = new Intent(mContext, ViewMeetingActivity.class);

                            String data = new Gson().toJson(calData);
                            intent.putExtra("appt_data", data);

                            if (isContactSelected()) {
                                ContactInfo contactInfo = getSelectedContact();
                                if (contactInfo != null) {
                                    intent.putExtra("contact", contactInfo);
                                }
                            }
                            startActivityForResult(intent, REQUEST_MEET);

                        /*if (!TextUtils.isEmpty(calData.getAddress())) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                            alertDialogBuilder.setMessage("Please choose your action")
                                    .setCancelable(true).setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    removeCalendarData(calData);
                                }
                            }).setNegativeButton("Go to Location", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                    //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                    Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s&mode=d", calData.getAddress()));

                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    try{
                                        startActivity(mapIntent);
                                    } catch (Exception e) {
                                        showToastMessage("Please install google map");
                                    }
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
                            alertDialogBuilder.setTitle("Please confirm");
                            alertDialogBuilder.setMessage("Would you like to remove item?")
                                    .setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    removeCalendarData(calData);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }*/
                        }

                        @Override
                        public void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData) {
                            int detailByStatusId = calData.getApptStatusID();
                        }
                    });
            recyclerAppts.setAdapter(calAdapter);

            panelMeetAddSlider = findViewById(R.id.panelMeetAddSlider);
            panelMeetAddSlider.setVisibility(View.GONE);
            btnMeetInfoSave = findViewById(R.id.btnMeetInfoSave);
            panelMeetTitle = findViewById(R.id.panelMeetTitle);
            tvMeetTitle = findViewById(R.id.tvMeetTitle);
            tvMeetTime = findViewById(R.id.tvMeetTime);

            findViewById(R.id.btnCloseMeetSlider).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeMeetSlider();
                }
            });

            panelMeetTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    addNewMeet();
                }
            });

            panelAgenda = findViewById(R.id.panelAgenda);
            edtAgendaSearch = findViewById(R.id.edtAgendaSearch);
            edtAgendaSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //updateAgendaItem(false);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            findViewById(R.id.ivFilterAgenda).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFilterAgenda = !isFilterAgenda;
                    updateAgendaItem(isFilterAgenda);
                }
            });

            recyclerAgenda = findViewById(R.id.recyclerAgenda);
            calAgendaAdapter = new CalTimeAgendaAdapter(mContext, new CalTimeAgendaAdapter.RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    CalendarData.Data calData = mCalendarDataList.get(position);

                    Intent intent = new Intent(mContext, ViewMeetingActivity.class);
                    if (isContactSelected()) {
                        ContactInfo contactInfo = getSelectedContact();
                        if (contactInfo != null) {
                            intent.putExtra("contact", contactInfo);
                        }
                    }

                    long testCalId = calData.getCalId();
                    int testSellerId = calData.getSellerId();

                    ContactInfo sellerContactInfo = dm.getContactInfoBySenderId(testSellerId);
//                    if (testSellerId != 0) {
                    setSelectedContactIndex(0, toModel(sellerContactInfo));
//                    }

                    String data = new Gson().toJson(calData);
                    intent.putExtra("appt_data", data);

                    startActivityForResult(intent, REQUEST_MEET);
                }

                @Override
                public void onApptClick(View view, int groupPos, int position, CalendarData.Data calData) {

                }

                @Override
                public void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData) {
                }
            });
            // todo: InvalidSetHasFixedSize
            // recyclerAgenda.setHasFixedSize(true);
            recyclerAgenda.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerAgenda.setAdapter(calAgendaAdapter);
            btnAddAppt = findViewById(R.id.btnAddAppt);
            btnAddAppt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewMeet();
                }
            });

            // Panel Tasks
            panelTasks = findViewById(R.id.panelTasks);

            panelProjectList = findViewById(R.id.panelProjectList);
            retrieveCateData();

            panelStoreCategory = findViewById(R.id.panelStoreCategory);
            btnShareCate = findViewById(R.id.btnShareCat);
            btnShareCate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    msg("Will Share Lists in Full Version.");
                }
            });
            btnViewProj = findViewById(R.id.btnViewProj);
            btnViewProj.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    panelProjectList.setVisibility(View.GONE);
                    panelTasksList.setVisibility(View.VISIBLE);
                }
            });
            edtStoreCateName = findViewById(R.id.edtStoreCateName);
            btnAddStoreCate = findViewById(R.id.btnAddStoreCate);
            btnAddStoreCate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String projName = edtStoreCateName.getText().toString().trim();
                    if (!TextUtils.isEmpty(projName)) {
                        StoreCate newProj = new StoreCate();
                        newProj.setID(String.valueOf(storeCateList.size()));
                        newProj.setDescription(projName);
                        storeCateList.add(newProj);

                        storeCateAdapter.notifyDataSetChanged();

                        edtStoreCateName.setText("");
                    }
                }
            });
            recvStoreCate = findViewById(R.id.recvStoreCate);
            recvStoreCate.setLayoutManager(new LinearLayoutManager(mContext));
            storeCateAdapter = new StoreCatAdapter(mContext, storeCateList, new StoreCatAdapter.ItemSelectListener() {
                @Override
                public void onItemSelected(boolean selected) {
                    if (!selected) {
                    }
                }

                @Override
                public void onItemClicked(int position) {
                    selectedCateIndex = position;
                    StoreCate storeCate = storeCateList.get(position);
                    storeItemAdapter.setData(storeCate.getItems());

                    tvStoreCate.setText(storeCate.getDescription());

                    panelStoreCategory.setVisibility(View.GONE);
                    panelStoreItem.setVisibility(View.VISIBLE);
                }

                @Override
                public void onItemLongClicked(int position) {
                    TaskInfo taskInfo = taskList.get(position);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Task Description", taskInfo.getDescription());
                    clipboard.setPrimaryClip(clip);

                    showToastMessage("Copied description to clipboard!");
                }
            });
            recvStoreCate.setAdapter(storeCateAdapter);

            panelStoreItem = findViewById(R.id.panelStoreItem);
            btnBackCate = findViewById(R.id.btnBackCate);
            btnBackCate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    panelStoreCategory.setVisibility(View.VISIBLE);
                    panelStoreItem.setVisibility(View.GONE);
                }
            });
            tvStoreCate = findViewById(R.id.tvStoreCate);
            btnSpeechToText = findViewById(R.id.btnSpeechToText);
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.US);
                        tts.setPitch(1.0f);
                    }
                }
            });
            btnSpeechToText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<StoreItem> storeItems = storeCateList.get(selectedCateIndex).getItems();

                    StringBuilder stringBuilder = new StringBuilder();
                    for (StoreItem item : storeItems) {
                        stringBuilder.append(item.getDescription());
                        stringBuilder.append("\n\n\n");
                    }
                    String text = stringBuilder.toString();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });

            edtStoreItemName = findViewById(R.id.edtStoreItemName);
            btnAddStoreItem = findViewById(R.id.btnAddStoreItem);
            btnAddStoreItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String projName = edtStoreItemName.getText().toString().trim();
                    if (!TextUtils.isEmpty(projName)) {
                        StoreItem newProj = new StoreItem();
                        newProj.setID(String.valueOf(storeCateList.size()));
                        newProj.setDescription(projName);
                        storeCateList.get(selectedCateIndex).addNewItem(newProj);

                        storeItemAdapter.notifyDataSetChanged();

                        storeCateAdapter.notifyItemChanged(selectedCateIndex);

                        edtStoreItemName.setText("");
                    }
                }
            });
            recvStoreItem = findViewById(R.id.recvStoreItem);
            recvStoreItem.setLayoutManager(new LinearLayoutManager(mContext));
            storeItemAdapter = new StoreItemAdapter(mContext, new StoreItemAdapter.ItemSelectListener() {
                @Override
                public void onItemSelected(boolean selected) {
                    if (!selected) {
                    }
                }

                @Override
                public void onItemClicked(int position) {
                }

                @Override
                public void onItemLongClicked(int position) {
                }
            });
            recvStoreItem.setAdapter(storeItemAdapter);


            panelTasksList = findViewById(R.id.panelTasksList);
            btnViewLists = findViewById(R.id.btnViewLists);
            btnViewLists.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    panelProjectList.setVisibility(View.VISIBLE);
                    panelTasksList.setVisibility(View.GONE);
                }
            });
            edtItem = findViewById(R.id.edtItem);
            btnClearTaskInput = findViewById(R.id.btnClearTaskInput);
            btnClearTaskInput.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtItem.setText("");
                }
            });
            edtItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String strItem = edtItem.getText().toString().trim();
                    if (TextUtils.isEmpty(strItem)) {
                        btnClearTaskInput.setVisibility(View.GONE);
                    } else {
                        btnClearTaskInput.setVisibility(View.VISIBLE);
                    }
                }
            });
            chkAll = findViewById(R.id.chkAll);
            chkAll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean status = chkAll.isChecked();
                    for (TaskInfo taskInfo : taskList) {
                        taskInfo.setSelected(status);
                    }
                    tasksAdapter.notifyDataSetChanged();
                }
            });
            recvTasks = findViewById(R.id.recvTasks);
            recvTasks.setLayoutManager(new LinearLayoutManager(mContext));
            tasksAdapter = new TasksAdapter(mContext, taskList, new TasksAdapter.ItemSelectListener() {
                @Override
                public void onItemSelected(boolean selected) {
                    if (!selected) {
                        chkAll.setChecked(false);
                    }
                }

                @Override
                public void onItemClicked(int position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Would you like to create a meeting for the selected item?")
                            .setCancelable(false)
                            // Set the action buttons
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(mContext, ActivityAddMeet.class);
                                    ContactInfo contactInfo = null;
                                    if (isContactSelected()) {
                                        contactInfo = getSelectedContact();
                                        // intent.putExtra("email", contactInfo.getEmail());
                                        // intent.putExtra("phone", contactInfo.getCp());
                                        // intent.putExtra("title", tvMeetTitle.getText().toString());

                                        if (contactInfo != null) {
                                            intent.putExtra("contact", contactInfo);
                                        }
                                    }

                                    intent.putExtra("time", calSelectedDate.getTimeInMillis());
                                    intent.putExtra("task", taskList.get(position));
                                    startActivityForResult(intent, REQUEST_MEET);

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                }

                @Override
                public void onItemLongClicked(int position) {
                    TaskInfo taskInfo = taskList.get(position);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Task Description", taskInfo.getDescription());
                    clipboard.setPrimaryClip(clip);

                    showToastMessage("Copied description to clipboard!");
                }
            });
            recvTasks.setAdapter(tasksAdapter);
            findViewById(R.id.btnSetPriority).setOnClickListener(this);
            findViewById(R.id.btnCompleted).setOnClickListener(this);
            findViewById(R.id.btnAssigned).setOnClickListener(this);
            findViewById(R.id.btnAddTask).setOnClickListener(this);
            chkTaskShowCompleted = findViewById(R.id.chkTaskShowCompleted);
            chkTaskShowCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    doTask(TASK_COMPLETED, null, null, null);
                }
            });

            panelProjectList.setVisibility(View.VISIBLE);
            panelTasksList.setVisibility(View.GONE);

            boolean openedFromPM = false;
            if (getIntent() != null && getIntent().getAction() != null) {

                openedFromPM = true;

                String action = getIntent().getAction();
                if (action.contentEquals("ConnectionActivity.NEW_TASK")) {
                    // someone shared a new task
                    showPanel(PANEL_TASKS);
                    doTask(TASK_SHARED, null, null, null);
                } else if (action.contentEquals(ACTION_NEW_APPT)) {
                    panelMessage.setVisibility(View.GONE);
                    panelCalls.setVisibility(View.GONE);
                    panelLocation.setVisibility(View.GONE);
                    panelPay.setVisibility(View.GONE);
                    panelNotes.setVisibility(View.GONE);
                    panelCal.setVisibility(View.VISIBLE);
                    panelTasks.setVisibility(View.GONE);

                    panelAgenda.setVisibility(View.GONE);
                    btnAddAppt.setVisibility(View.GONE);

                    if (getIntent().getExtras() != null) {
                        Bundle bundle = getIntent().getExtras();
                        long NewApptID = bundle.getLong("NewApptID");
                        updateDataAndOpenNewApptID(true, NewApptID);
                    }
                } else if (action.contentEquals(ACTION_UPDATE_APPT)) {
                    panelMessage.setVisibility(View.GONE);
                    panelCalls.setVisibility(View.GONE);
                    panelLocation.setVisibility(View.GONE);
                    panelPay.setVisibility(View.GONE);
                    panelNotes.setVisibility(View.GONE);
                    panelCal.setVisibility(View.VISIBLE);
                    panelTasks.setVisibility(View.GONE);
                    panelAgenda.setVisibility(View.GONE);
                    btnAddAppt.setVisibility(View.GONE);

                    if (getIntent().getExtras() != null) {
                        Bundle bundle = getIntent().getExtras();
                        long ApptID = bundle.getLong("ApptID");
                        updateDataAndOpenNewApptID(true, ApptID);
                    }
                } else {
                    if (getIntent().getExtras() != null) {
                        try {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle bundle = getIntent().getExtras();
                                    int SenderID = bundle.getInt("mlid");

                                    //  showToastMessage("MLID is " + SenderID);
                                    showPanel(PANEL_MESSAGE);
                                    int receivedPayloadType = bundle.getInt("payloadtype", 0);
                                    switch (receivedPayloadType) {
                                        case 1: // Open Text Message and set the Sender's name in spinner view
                                            Log.i("ConnectionActivity", SenderID + "");
                                            if (dm.getContactInfoBySenderId(SenderID) != null) {
                                                ContactInfo contactInfo = dm.getContactInfoBySenderId(SenderID);
                                                contactInfoSelected = contactInfo;
                                                CustomContactModel model = toModel(contactInfo);
                                                selectedModel = model;

                                                spinnerContact2.removeTextChangedListener(contactNameInputWatcuer);
                                                spinnerContact2.setText(DataUtil.getCompanyAndName(model));
                                                spinnerContact2.addTextChangedListener(contactNameInputWatcuer);

                                                spinnerContact2.setSelection(spinnerContact2.getText().toString().length());

                                                spinnerContactSearchResults.setVisibility(View.GONE);

                                                if (model.type == 1) { // if it's a contact
                                                    // selectedMLID = model.mlid;
                                                    messageList.clear();
                                                    mMessageAdapter.notifyDataSetChanged();
                                                    getMessages(false);

                                                    if (panelNotes.getVisibility() == View.VISIBLE) {
                                                        // Refresh Notes
                                                        tvNotes.setText("");
                                                        getNotes();
                                                    }

                                                    checkContactLikeFavorStatus();
                                                    checkContactAddress();
                                                } else {
                                                }

                                                showActionIcons();
                                            }
                                            break;
                                        case 8: // Open Tasks
                                            showPanel(PANEL_TASKS);
                                            doTask(TASK_SHOW, null, null, null);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }, 500);
                        } catch (Exception e) {
                            // Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                showPanel(PANEL_MESSAGE);
            }

            // Register Receiver
            LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter(ACTION_TEXT_MESSAGE_CHAT_ACTIVE));
            refreshContacts();
            connectionActivity = ConnectionActivity.this;
            spinnerContact2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // showSearchResults();
                }
            });

            if (getIntent().getExtras() != null) {
                int defaultPage = getIntent().getExtras().getInt("page", PANEL_MESSAGE);
                // Show Message Panel
                showPanel(defaultPage);
                if (defaultPage == PANEL_PAY_SEND) {
                    panelPayReceived.setVisibility(View.GONE);
                    panelPayShare.setVisibility(View.VISIBLE);
                    tabPayReceived.setSelected(false);
                    tabPayShare.setSelected(true);
                } else if (defaultPage == PANEL_PAY_RECEIVE) {
                    panelPayReceived.setVisibility(View.VISIBLE);
                    panelPayShare.setVisibility(View.GONE);
                    tabPayReceived.setSelected(true);
                    tabPayShare.setSelected(false);
                }
            } else {
                if (contactList.size() > 0) {
                    showSearchResults();
                }
            }
        } catch (Exception e) {
            dataUtil.zzzLogIt(e, "Oncreate - aaaa");
        }
    }


    @SuppressLint("LogNotTimber")
    private void startVideoCallApi(ContactInfo contactInfo) {
        try {
            showProgressDialog();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "StartVC",
                    BaseFunctions.APP_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    ((KTXApplication) getApplication()).getAndroidId());
            try {

                agoraChannelName = appSettings.getUserId() + "" + contactInfo.getMlid();

                RequestQueue requestTokenQueue = Volley.newRequestQueue(this);
                String getAgoraUrl = "https://uniway-agora-server.onrender.com/rtcToken?channelName=" + agoraChannelName;
                StringRequest stringAgoraRequest = new StringRequest(Request.Method.GET, getAgoraUrl,
                        tokenResponse -> {
                            // Handle response

                            try {
                                Log.d("@GET AGOToken::: response",  "" + tokenResponse);
                                Gson tokenGson = new Gson();
                                AgoraTokenModel[] listOfTokens = tokenGson.fromJson(tokenResponse, AgoraTokenModel[].class);

                                agoraToken = listOfTokens[0].getCallToken();
                                Log.d("@GET AGOToken::: agoraToken",  "" + agoraToken);

                            } catch (Exception e) {
                                hideProgressDialog();
                                Timber.tag("TAG").e("onApiResponseError: %s", e.getLocalizedMessage());
                                if (dataUtil != null) {
                                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "GetAgoraToken");
                                }
                            }
                        }, error -> {
                    hideProgressDialog();
                    Log.e(TAG, "startVideoCallGetATokenError: " + error);
                    if (dataUtil != null) {
                        dataUtil.zzzLogIt(error, "GetAgoraToken");
                    }
                    // Handle error
                });

                requestTokenQueue.add(stringAgoraRequest);
            } catch (Exception ignored){
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(ignored, "GetAgoraToken");
                }
            }
            Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
            builder.appendQueryParameter("callingMLID", String.valueOf(contactInfo.getMlid()));
            builder.appendQueryParameter("VCsecurityID", String.valueOf(vCSecurityID));
            builder.appendQueryParameter("callerhandle", appSettings.getHandle());
            builder.appendQueryParameter("callerFN", appSettings.getFN());
            builder.appendQueryParameter("callerLN", appSettings.getLN());
//            builder.appendQueryParameter("agoraToken", agoraToken);
//            builder.appendQueryParameter("agoraChannel", agoraChannelName);
            builder.appendQueryParameter("callingHandle", contactInfo.getHandle());
            builder.appendQueryParameter("callingFN", contactInfo.getFname());
            builder.appendQueryParameter("callingLN", contactInfo.getLname());
            String urlWithParams = builder.build().toString();
            Log.d( "@Start vc request: ", "" + urlWithParams);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                    response -> {
                        // Handle response
                        Log.d("@StartVideoCallApi::: response",  "" + response);
                        try {
                            Gson gson = new Gson();
                            StartVideoModel[] listOfModels = gson.fromJson(response, StartVideoModel[].class);
                            Log.d( "@StartVideoCallApi::: listofModels" , "" + gson.toJson(listOfModels));
                            hideProgressDialog();
                            if (listOfModels != null && listOfModels.length > 0) {
                                videoModel = listOfModels[0];
                                Log.e(TAG, "startVideoCallApi: callId =>  " + videoModel.getCallId());
                                if (videoModel != null && videoModel.isStatus()) {

                                    checkCameraPermission();
                                } else {
                                    Toast.makeText(mContext, videoModel.getMsg(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (Exception e) {
                            hideProgressDialog();
                            Timber.tag("TAG").e("onApiResponseError: %s", e.getLocalizedMessage());
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "StartVC");
                            }
                        }
                    }, error -> {
                        hideProgressDialog();
                        Log.e(TAG, "startVideoCallApiError: " + error);
                        if (dataUtil != null) {
                            dataUtil.zzzLogIt(error, "StartVC");
                        }
                        // Handle error
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "StartVC");
            }
        }


    }

    public void checkCameraPermission() {
        // Checking if permission is not granted
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            goToVideoCallActivity();
        }
    }

    private void goToVideoCallActivity() {
//        Intent intent = new Intent(this, InCallActivityAgora.class);
        Intent intent = new Intent(this, MakeCallActivity.class);
        intent.putExtra("callingMLID", String.valueOf(contactInfo.getMlid()));
        intent.putExtra("agoraToken", agoraToken);
        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.FIRST_NAME, String.valueOf(contactInfo.getFname()));
        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.LAST_NAME, String.valueOf(contactInfo.getLname()));
        intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.COMING_FROM, hawaiiappbuilders.omniversapp.meeting.utilities.Constants.OUTGOING_SCREEN);
        if (videoModel.getCallId() != null) {
            intent.putExtra(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.CAll_ID, String.valueOf(videoModel.getCallId()));
        }
        Log.d("@INtent:::", "" + intent);
        startActivity(intent);
    }

    private int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 1;

    public static final int TASK_SHARED = 1;
    public static final int TASK_COMPLETED = 2;
    public static final int TASK_SHOW = 3;
    public static final int TASK_PRIORITY = 4;
    public static final int TASK_SET_COMPLETED = 5;
    public static final int TASK_ASSIGN = 6;
    public static final int TASK_ADD_NEW = 7;

    public void doTask(int category, String contactId, String edtText, String selectedIds) {
        switch (category) {
            case TASK_SHARED:
                callTasksApi(category, 0, "0", "0", "");
                break;
            case TASK_COMPLETED:
                callTasksApi(category, 0, "0", "0", "");
                break;
            case TASK_SHOW:
                callTasksApi(category, 0, "0", "0", "");
                break;
            case TASK_PRIORITY:
                callTasksApi(category, 1, contactId, edtText, selectedIds);
                break;
            case TASK_SET_COMPLETED:
                callTasksApi(category, 3, "0", "0", selectedIds);
                break;
            case TASK_ASSIGN:
                callTasksApi(category, 2, contactId, "0", selectedIds);
                pushTaskAssignments(Integer.parseInt(contactId));
                break;
            case TASK_ADD_NEW:
                callTasksApi(category, 0, "0", edtText, "");
                break;
        }
    }

    private void callSelectedContact() {
        ContactInfo contactInfo = getSelectedContact();

        User selectedContact = new User();
        selectedContact.firstName = contactInfo.getFname();
        selectedContact.lastName = contactInfo.getLname();
        selectedContact.mlid = contactInfo.getMlid();

        // TODO: Show progress bar, Check user if already exists in firestore
        AtomicBoolean isFound = new AtomicBoolean(false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String signedInUserId = preferenceManager.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            int currMlid = Integer.parseInt(String.valueOf(documentSnapshot.getLong(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID)));
                            if (currMlid == selectedContact.getMlid()) {
                                isFound.set(true);
                                selectedContact.token = documentSnapshot.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FCM_TOKEN);
                                break;
                            }
                        }
                    }
                    if (isFound.get()) {
                        // TODO:  Retrieve user data from firestore
                        getSelectedContactToken(selectedContact);
                    } else {
                        // TODO:  User doesn't exist.
                        showToastMessage("User is not logged in");
                    }
                });
    }

    private void restoreUserContacts() {
        MessageDataManager dm = new MessageDataManager(mContext);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle("Restore Contacts");
        alertDialogBuilder.setMessage("This will remove any existing Contact.")
                .setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (getLocation()) {
                            try {
                                HashMap<String, String> params = new HashMap<>();
                                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                                        "CJLGet",
                                        BaseFunctions.MAIN_FOLDER,
                                        getUserLat(),
                                        getUserLon(),
                                        mMyApp.getAndroidId());
                                String userName = appSettings.getFN() + " " + appSettings.getLN();
                                userName = userName.trim();

                                String extraParams =
                                        "&mode=" + "restoreContacts";

                                baseUrl += extraParams;
                                Log.e("Request", baseUrl);

                                new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                                    @Override
                                    public void onSuccess(String response) {
                                        if (response != null && !response.isEmpty()) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                // if there are contacts returned, truncate contacts table
                                                if (jsonArray.length() > 0) {
//                                                    dm.removeAllContacts();
                                                    dm.removeAllContactsWithMLIDGT0();

                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject data = jsonArray.getJSONObject(i);

                                                        Log.e("restoreContacts", data.toString());

                                                        ContactInfo contactInfo = new ContactInfo();

                                                        String UN = data.optString("UN");
                                                        String email = data.optString("Email");
                                                        String co = data.optString("Co");
                                                        String handle = data.optString("handle");

                                                        String streetNum = "";
                                                        String streetAddr = "";
                                                        if (!data.optString("Address").isEmpty()) {
                                                            String streetInformation = data.getString("Address").trim();
                                                            if (streetInformation.contains(" ")) {
                                                                int separator = streetInformation.indexOf(" ");

                                                                streetNum = streetInformation.substring(0, separator).trim();
                                                                streetAddr = streetInformation.substring(separator + 1).trim();
                                                            } else {
                                                                streetAddr = streetInformation;
                                                            }
                                                        }
                                                        contactInfo.setName(UN);
                                                        contactInfo.setPri(57); // None
                                                        contactInfo.setEmail(email);
                                                        contactInfo.setHandle(handle);
                                                        contactInfo.setMlid(data.optInt("MLID"));
                                                        contactInfo.setCo(co);
                                                        contactInfo.setFname(data.optString("FN"));
                                                        contactInfo.setLname(data.optString("LN"));
                                                        contactInfo.setHandle(data.optString("handle"));
                                                        contactInfo.setStreetNum(streetNum);
                                                        contactInfo.setStreet(streetAddr);
                                                        contactInfo.setCity(data.optString("City"));
                                                        contactInfo.setState(data.optString("St"));
                                                        contactInfo.setZip(data.optString("Zip"));
                                                        contactInfo.setWp(data.optString("WP"));
                                                        contactInfo.setCp(data.optString("CP"));
                                                        contactInfo.setAddress(data.optString("Address"));
                                                        contactInfo.setDob(data.optString("DOB"));

                                                        if (TextUtils.isEmpty(contactInfo.getCp()) || contactInfo.getCp().length() < 10) {
                                                            contactInfo.setCp(contactInfo.getWp());
                                                        }

                                                    /*if (TextUtils.isEmpty(contactInfo.getFname()) && TextUtils.isEmpty(contactInfo.getLname())) {
                                                        if (!TextUtils.isEmpty(co)) {
                                                            contactInfo.setFname(co);
                                                        } else if (!TextUtils.isEmpty(UN)) {
                                                            contactInfo.setFname(UN);
                                                        } else {
                                                            contactInfo.setFname(email);
                                                        }
                                                    }*/
                                                        dm.addContact(contactInfo);
                                                    }
                                                    // todo: launch connection activity again
                                                    finish();
                                                    startActivity(new Intent(mContext, ConnectionActivity.class));
                                                }
                                            } catch (Exception e) {
                                                showAlert(e.getMessage());
                                            }
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
                            } catch (Exception e) {
                                if (dataUtil != null) {
                                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "CJLGet");
                                }
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void getSelectedContactToken(hawaiiappbuilders.omniversapp.meeting.models.User user) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_MLID, user.mlid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        // TODO:  get token and initiate call

                        String token = documentSnapshot.getString(hawaiiappbuilders.omniversapp.meeting.utilities.Constants.KEY_FCM_TOKEN);
                        if (token == null || token.trim().isEmpty()) {
                            Toast.makeText(mContext, user.firstName + " " + user.lastName + " is not available for meeting", Toast.LENGTH_SHORT).show();
                        } else {
                            user.token = token;


                            //startVideoCall(user);
                        }

                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(mContext, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /*private void startVideoCall(User user) {
        Intent videoCallIntent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
        videoCallIntent.putExtra("user", new Gson().toJson(user));
        videoCallIntent.putExtra("type", "video"); // video, audio
        startActivity(videoCallIntent);
    }*/

    private void pushTaskAssignments(int mlid) {
        try {
            JSONObject payloadsData = new JSONObject();
            JSONObject payload = new JSONObject();
            payload.put("message", "You have a new task");
            payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
            payload.put("SenderID", appSettings.getUserId());
            NotificationHelper notificationHelper = new NotificationHelper(mlid, mContext, (BaseActivity) mContext);
            notificationHelper.getToken(PayloadType.PT_Share_Task, payloadsData, new OnGetTokenListener() {
                @Override
                public void onSuccess(String response) {
                }

                @Override
                public void onVolleyError(VolleyError error) {
                }

                @Override
                public void onEmptyResponse() {
                }

                @Override
                public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
                    // Do nothing
                }

                @Override
                public void onJsonArrayEmpty() {

                }

                @Override
                public void onJsonException() {
                }

                @Override
                public void onTokenListEmpty() {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void callTasksApi(int category, int mode, String dval, String newTxt, String tList) {
        hideKeyboard(edtItem);

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "Tasks",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + mode +
                                "&sellerID=" + appSettings.getWorkid() +
                                "&aLev=" + "0" +
                                "&dVal=" + dval +
                                "&newTXT=" + newTxt +
                                "&tList=" + tList;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                //showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideProgressDialog();

                        Log.e("Tasks", response);

                        taskList.clear();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") /*&& !jsonObject.getBoolean("status")*/) {
                                //showAlert(jsonObject.getString("msg"));
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (jsonArray.getString(i).equals("null"))
                                        break;

                                    JSONObject dataObj = jsonArray.getJSONObject(i);
                                    TaskInfo customPro = new TaskInfo();

                                    customPro.setID(dataObj.getString("ID"));
                                    customPro.setPriority(Float.parseFloat(dataObj.getString("Priority")));
                                    customPro.setDescription(dataObj.getString("Description"));
                                    customPro.setAssigned(dataObj.getString("Assigned To"));
                                    customPro.setCompleted(dataObj.getString("Completed"));


                                    if (category == TASK_COMPLETED) {
                                        if (chkTaskShowCompleted.isChecked()) {
                                            if (!"Not".equalsIgnoreCase(customPro.getCompleted())) {
                                                taskList.add(customPro);
                                            }
                                        } else {
                                            if ("Not".equalsIgnoreCase(customPro.getCompleted())) {
                                                taskList.add(customPro);
                                            }
                                        }
                                    } else {
                                        if ("Not".equalsIgnoreCase(customPro.getCompleted())) {
                                            taskList.add(customPro);
                                        }
                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }

                        tasksAdapter.notifyDataSetChanged();
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
                sr.setRetryPolicy(new DefaultRetryPolicy(
                        25000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                sr.setShouldCache(false);
                queue.add(sr);
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "Tasks");
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // set new extras
    }

    ContactInfo contactInfoSelected;

    private CustomContactModel toModel(ContactInfo contactInfo) {
        CustomContactModel model = new CustomContactModel();
        model.isSelected = false;
        model.type = 1;
        model.id = contactInfo.getId();
        if (contactInfo.getCo() != null) {
            model.company = contactInfo.getCo();
        } else {
            model.company = "";
        }
        if (contactInfo.getName() != null) {
            model.name = contactInfo.getName();
        } else {
            model.name = "";
        }
        if (contactInfo.getFname() != null) {
            model.fname = contactInfo.getFname();
        } else {
            model.fname = "";
        }

        if (contactInfo.getLname() != null) {
            model.lname = contactInfo.getLname();
        } else {
            model.lname = "";
        }

        if (contactInfo.getAddress() != null) {
            model.address = contactInfo.getAddress();
        } else {
            model.address = "";
        }

        if (contactInfo.getEmail() != null) {
            model.email = contactInfo.getEmail();
        } else {
            model.email = "";
        }
        if (contactInfo.getCp() != null) {
            model.phone = contactInfo.getCp();
        } else {
            model.phone = "";
        }
        if (contactInfo.getWp() != null) {
            model.wp = contactInfo.getWp();
        } else {
            model.wp = "";
        }
        model.mlid = contactInfo.getMlid();
        model.pri = contactInfo.getPri();
        return model;
    }

    private void updateContactsSearchList() {
        contactList.clear();
        contactList.addAll(dm.getAlLContacts(0));
        if (contactList.size() == 0) {
            spinnerContactSearchResults.setVisibility(View.GONE);
        } else {
            spinnerContactSearchResults.setVisibility(View.VISIBLE);
        }
        int currentPri = 57;
        contactModels = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            CustomContactModel model = new CustomContactModel();
            model.isSelected = false;
            if (contactList.get(i).getPri() != currentPri) {
                model.type = 0;
                int iPri = currentPri;
                if (i > 0) {
                    iPri = contactList.get(i).getPri();
                }

                if (iPri == 57 || iPri <= 100 && iPri >= 95) {
                    switch (iPri) {
                        case 57:
                            model.name = "All";
                            contactModels.add(model);
                            break;
                        case 100:
                            model.name = "Pinned";
                            contactModels.add(model);
                            break;
                        case 99:
                            model.name = "Favs";
                            model.setPri(iPri);
                            contactModels.add(model);
                            break;
                        case 98:
                            model.name = "Family";
                            contactModels.add(model);
                            break;
                        case 97:
                            model.name = "Friends";
                            contactModels.add(model);
                            break;
                        case 96:
                            model.name = "Business";
                            contactModels.add(model);
                            break;
                        case 95:
                            model.name = "Groups";
                            contactModels.add(model);
                            break;
                    }
                } else {
                    // todo: get group names
                    ArrayList<GroupInfo> groups = dm.getAlLUserGroups();
                    for (GroupInfo groupInfo : groups) {
                        if (groupInfo.getPri() == iPri) {
                            model.name = groupInfo.getGrpname();
                            contactModels.add(model);
                            break;
                        }
                    }
                }

                currentPri = contactList.get(i).getPri();
                model = new CustomContactModel();
            }
            model.id = contactList.get(i).getId();
            model.type = 1;
            if (contactList.get(i).getCo() != null) {
                model.company = contactList.get(i).getCo();
            } else {
                model.company = "";
            }
            if (contactList.get(i).getName() != null) {
                model.name = contactList.get(i).getName();
            } else {
                model.name = "";
            }
            if (contactList.get(i).getFname() != null) {
                model.fname = contactList.get(i).getFname();
            } else {
                model.fname = "";
            }

            if (contactList.get(i).getLname() != null) {
                model.lname = contactList.get(i).getLname();
            } else {
                model.lname = "";
            }

            if (contactList.get(i).getAddress() != null) {
                model.address = contactList.get(i).getAddress();
            } else {
                model.address = "";
            }

            if (contactList.get(i).getEmail() != null) {
                model.email = contactList.get(i).getEmail();
            } else {
                model.email = "";
            }
            if (contactList.get(i).getCp() != null) {
                model.phone = contactList.get(i).getCp();
            } else {
                model.phone = "";
            }
            if (contactList.get(i).getWp() != null) {
                model.wp = contactList.get(i).getWp();
            } else {
                model.wp = "";
            }
            if (contactList.get(i).getHandle() != null) {
                model.handle = contactList.get(i).getHandle();
            } else {
                model.handle = "";
            }
            model.mlid = contactList.get(i).getMlid();
            model.pri = currentPri; // add contact
            contactModels.add(model);
        }
        setContactListAdapter(contactModels);
    }

    private void setContactListAdapter(ArrayList<CustomContactModel> contacts) {
        contactsListAdapter2 = new AutocompleteContactsAdapter2(this, contacts);
        spinnerContactSearchResults.setLayoutManager(new LinearLayoutManager(mContext));
        spinnerContactSearchResults.setAdapter(contactsListAdapter2);
        contactsListAdapter2.setFilterListeners(new AutocompleteContactsAdapter2.FilterListeners() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void showClearIcon() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinnerContact2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_resized, 0);
                        spinnerContact2.setOnTouchListener(new RightDrawableOnTouchListener(spinnerContact2) {

                            @Override
                            public boolean onDrawableTouch(MotionEvent event) {
                                spinnerContact2.setText("");
                                ivVideo.setVisibility(View.GONE);
                                edtPhoneNumber.setText("");
                                updateCallHistory();

                                if (spinnerContact2.getText().toString().trim().length() == 0) {
                                    ((TextView) findViewById(R.id.txtMLID)).setText("MLID: 0");
                                }

                                contactInfoSelected = null;

                                return true;
                            }
                        });
                    }
                });

            }

            @Override
            public void hideClearIcon() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinnerContact2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                });
            }

            @Override
            public void setToAll() {
                contactInfoSelected = null;
            }

            @Override
            public void showDropdown(boolean show) {

            }

            @Override
            public void onClickContact(CustomContactModel model, int position) {
                contactInfoSelected = dm.getContactInfoById(model.getId());
                selectedModel = model;
                hideKeyboard();
                setSelectedContactIndex(position, model);

                spinnerContact2.removeTextChangedListener(contactNameInputWatcuer);
                spinnerContact2.setText(DataUtil.getCompanyAndName(model));
                spinnerContact2.addTextChangedListener(contactNameInputWatcuer);

                spinnerContact2.setSelection(spinnerContact2.getText().toString().length());
                if (model.type == 1) { // if it's a contact
                    // selectedMLID = model.mlid;
                    messageList.clear();
                    mMessageAdapter.notifyDataSetChanged();
                    getMessages(false);

                    if (panelNotes.getVisibility() == View.VISIBLE) {
                        // Refresh Notes
                        if (tvNotes != null) tvNotes.setText("");
                        getNotes();
                    }

                    checkContactLikeFavorStatus();
                    checkContactAddress();
                } else {
                }
                showActionIcons();

                if (panelAgenda.getVisibility() == View.VISIBLE) {
                    updateAgendaItem(isFilterAgenda);
                }

                spinnerContactSearchResults.setVisibility(View.GONE);
            }
        });
        contactsListAdapter2.notifyDataSetChanged();
    }

    private void updateContactsList() {
        contactList = dm.getAlLContacts(0);

        /*ContactInfo selectInfo = new ContactInfo();
        selectInfo.setFname("All");
        selectInfo.setPri(-2);
        contactList.add(0, selectInfo);*/

        // Collections.sort(contactList, new ContactInfo());

//        ContactInfo selectInfo = new ContactInfo();
//        selectInfo.setFname("All");
//        contactList.add(0, selectInfo);

//        contactAdapter = new ArrayAdapter<ContactInfo>(mContext, R.layout.spinner_list_item, contactList);
//        // contactAdapter.setDropDownViewResource(R.layout.spinner_list_item);
//        spinnerContact.setAdapter(contactAdapter);


        int currentPri = 57;
        contactModels = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            CustomContactModel model = new CustomContactModel();
            model.isSelected = false;
            if (contactList.get(i).getPri() != currentPri) {
                model.type = 0;
                int iPri = currentPri;
                if (i > 0) {
                    iPri = contactList.get(i).getPri();
                }
                switch (iPri) {
                    case 57:
                        model.name = "All";
                        contactModels.add(model);
                        break;
                    case 100:
                        model.name = "Pinned";
                        contactModels.add(model);
                        break;
                    case 99:
                        model.name = "Favs";
                        contactModels.add(model);
                        break;
                    case 98:
                        model.name = "Family";
                        contactModels.add(model);
                        break;
                    case 97:
                        model.name = "Friends";
                        contactModels.add(model);
                        break;
                    case 96:
                        model.name = "Business";
                        contactModels.add(model);
                        break;
                    case 95:
                        model.name = "Groups";
                        contactModels.add(model);
                        break;
                    default:
                        // todo: get group names
                        ArrayList<GroupInfo> groups = dm.getAlLUserGroups();
                        for (GroupInfo groupInfo : groups) {
                            if (groupInfo.getPri() == iPri) {
                                model.name = groupInfo.getGrpname();
                                contactModels.add(model);
                                break;
                            }
                        }
                }
                currentPri = contactList.get(i).getPri();
                model = new CustomContactModel();
            }
            model.id = contactList.get(i).getId();
            model.type = 1;
            if (contactList.get(i).getCo() != null) {
                model.company = contactList.get(i).getCo();
            } else {
                model.company = "";
            }
            if (contactList.get(i).getName() != null) {
                model.name = contactList.get(i).getName();
            } else {
                model.name = "";
            }
            if (contactList.get(i).getFname() != null) {
                model.fname = contactList.get(i).getFname();
            } else {
                model.fname = "";
            }

            if (contactList.get(i).getLname() != null) {
                model.lname = contactList.get(i).getLname();
            } else {
                model.lname = "";
            }

            if (contactList.get(i).getAddress() != null) {
                model.address = contactList.get(i).getAddress();
            } else {
                model.address = "";
            }

            if (contactList.get(i).getEmail() != null) {
                model.email = contactList.get(i).getEmail();
            } else {
                model.email = "";
            }
            if (contactList.get(i).getCp() != null) {
                model.phone = contactList.get(i).getCp();
            } else {
                model.phone = "";
            }
            if (contactList.get(i).getWp() != null) {
                model.wp = contactList.get(i).getWp();
            } else {
                model.wp = "";
            }
            if (contactList.get(i).getHandle() != null) {
                model.handle = contactList.get(i).getHandle();
            } else {
                model.handle = "";
            }
            model.mlid = contactList.get(i).getMlid();
            model.pri = currentPri; // add contact
            contactModels.add(model);
        }
        contactsListAdapter = new CustomContactList(this, R.layout.spinner_list_item, contactModels);
        spinnerContact.setAdapter(contactsListAdapter);

        // spinnerContact.setSelection(0);
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // When change the contact, refresh Notes
                //if (panelMessage.getVisibility() == View.VISIBLE) {
                CustomContactModel model = (CustomContactModel) adapterView.getSelectedItem();
                if (model.type == 1) { // if it's a contact
                    // selectedMLID = model.mlid;
                    messageList.clear();
                    mMessageAdapter.notifyDataSetChanged();
                    getMessages(false);
                    //}


                    if (panelNotes.getVisibility() == View.VISIBLE) {
                        // Refresh Notes
                        tvNotes.setText("");
                        getNotes();
                    }

                    checkContactLikeFavorStatus();
                    checkContactAddress();

                } else {
                }
                showActionIcons();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //playChinChin(0);
            getMessages(false);
        }
    };

    public void showActionIcons() {
        if (isContactSelected()) {
            // Hide this button for now.
            ivVideo.setVisibility(View.VISIBLE);
            ivEmail.setVisibility(View.VISIBLE);
        } else {
            ivVideo.setVisibility(View.GONE);
            ivEmail.setVisibility(View.GONE);
        }
    }

    public boolean isActiveChatOpened(int mlid) {
        if (panelMessage.getVisibility() != View.VISIBLE) {
            return false;
        }

        int activeMLID = 0;
        if (isContactSelected()) {
            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo != null) {
                activeMLID = contactInfo.getMlid();
            }
        }

        return activeMLID == mlid;
    }

    public static boolean isMsgForCurrentContact(int mlid) {
        if (connectionActivity == null || connectionActivity.isFinishing()) {
            return false;
        }

        return connectionActivity.isActiveChatOpened(mlid);
    }

    private void showCurrentLocation() {
        if (markerLocation != null) {
            markerLocation.remove();
            markerLocation = null;
        }

        if (googleMap != null && getLocation()) {
            LatLng resPos = new LatLng(Double.parseDouble(getUserLat()), Double.parseDouble(getUserLon()));
            markerLocation = googleMap.addMarker(new MarkerOptions()
                    .position(resPos)
                    .title("You're here")
            );

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(resPos)
                    .zoom(11)
                    .bearing(0)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        }
    }

    // Check Current Contact item Status For Like And Favor
    private void checkContactLikeFavorStatus() {
        // int position = spinnerContact.getSelectedItemPosition();

        if (isContactSelected()) {
            // ContactInfo contactInfo = contactList.get(position);
            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo != null) {
                groupAdapter.setPriValue(contactInfo.getPri());
                edtPhoneNumber.setText(PhonenumberUtils.getFormattedPhoneNumber(contactInfo.getCp()));
            }
        } else {
            groupAdapter.setPriValue(-100);
            edtPhoneNumber.setText("");
        }

        updateCallHistory();
    }

    private void checkContactAddress() {
        // int position = spinnerContact.getSelectedItemPosition();
        if (isContactSelected()) {
            // ContactInfo contactInfo = contactList.get(position);
            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo != null) {
                String address = contactInfo.getAddress();
                String zip = contactInfo.getZip();
                if (TextUtils.isEmpty(address) && TextUtils.isEmpty(zip)) {
                    tvContactAddress.setText("");
                } else if (TextUtils.isEmpty(address)) {
                    tvContactAddress.setText(zip);
                } else if (TextUtils.isEmpty(zip)) {
                    tvContactAddress.setText(address);
                } else {
                    address = address + ", " + zip;
                    tvContactAddress.setText(address.trim());
                }
                // Try to get geo location
                Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                intent.putExtra(Constants.RECEIVER, mResultReceiver);
                intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
                intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, contactInfo.getAddress() + " " + contactInfo.getCSZ());
                intent.putExtra(Constants.REQUEST_CODE, 0);
                startService(intent);
            }
        } else {
            tvContactAddress.setText("");
        }
    }

    private void updateCallHistory() {

        // int position = spinnerContact.getSelectedItemPosition();
        phoneHistorys.clear();
        int contactID = 0;
        if (isContactSelected()) {
            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo != null) {
                contactID = contactInfo.getId();
                ArrayList<CallHistory> callHistories = dm.getAlLHistory(contactID);
                Map<String, List<CallHistory>> result = groupItemsByDate(callHistories);

                phoneHistorys.addAll(callHistories);
                callLogAdapter.notifyDataSetChanged();
            }
        } else {
            ArrayList<CallHistory> callHistories = dm.getAlLHistory(contactID);
            Map<String, List<CallHistory>> result = groupItemsByDate(callHistories);

            phoneHistorys.addAll(callHistories);
            callLogAdapter.notifyDataSetChanged();
        }
        //STRUCTURE THE GROUPED DATA
       /* if(result !=null) {
            for (Map.Entry<String, List<CallHistory>> parentItem : result.entrySet()) {
                System.out.println(parentItem.getKey() + " : "); // item.team value

                for (CallHistory childItem : parentItem.getValue()) {
                    System.out.println(childItem.getCallDate());
                }
                System.out.println("-------------------------------------------");
            }
        }*/
    }

    public static Map<String, List<CallHistory>> groupItemsByDate(Collection<CallHistory> itemsList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return itemsList.stream().collect(Collectors.groupingBy(w -> DateUtil.toStringFormat_31(w.getCallDate())));
        }
        return null;
    }

    private void week() {
        View view = (View) findViewById(R.id.symbolOfWeek);
        TextView mon = (TextView) view.findViewById(R.id.mon);
        TextView tue = (TextView) view.findViewById(R.id.tue);
        TextView wed = (TextView) view.findViewById(R.id.wed);
        TextView thu = (TextView) view.findViewById(R.id.thu);
        TextView fri = (TextView) view.findViewById(R.id.fri);
        TextView sat = (TextView) view.findViewById(R.id.sat);
        TextView sun = (TextView) view.findViewById(R.id.sun);

        dayLabelTextViews.add(mon);
        dayLabelTextViews.add(tue);
        dayLabelTextViews.add(wed);
        dayLabelTextViews.add(thu);
        dayLabelTextViews.add(fri);
        dayLabelTextViews.add(sat);
        dayLabelTextViews.add(sun);

        // Set Font Size
        for (TextView textView : dayLabelTextViews) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        }

        ImageView monSelected = (ImageView) view.findViewById(R.id.monSelected);
        ImageView tueSelected = (ImageView) view.findViewById(R.id.tueSelected);
        ImageView wedSelected = (ImageView) view.findViewById(R.id.wedSelected);
        ImageView thuSelected = (ImageView) view.findViewById(R.id.thuSelected);
        ImageView friSelected = (ImageView) view.findViewById(R.id.friSelected);
        ImageView satSelected = (ImageView) view.findViewById(R.id.satSelected);
        ImageView sunSelected = (ImageView) view.findViewById(R.id.sunSelected);

        monSelected.setVisibility(View.GONE);
        tueSelected.setVisibility(View.GONE);
        wedSelected.setVisibility(View.GONE);
        thuSelected.setVisibility(View.GONE);
        friSelected.setVisibility(View.GONE);
        satSelected.setVisibility(View.GONE);
        sunSelected.setVisibility(View.GONE);
    }

    private void dateOfWeek() {
        View view = findViewById(R.id.dateOfWeek);

        TextView mon = view.findViewById(R.id.mon);
        TextView tue = view.findViewById(R.id.tue);
        TextView wed = view.findViewById(R.id.wed);
        TextView thu = view.findViewById(R.id.thu);
        TextView fri = view.findViewById(R.id.fri);
        TextView sat = view.findViewById(R.id.sat);
        TextView sun = view.findViewById(R.id.sun);

        dateTextViews.add(mon);
        dateTextViews.add(tue);
        dateTextViews.add(wed);
        dateTextViews.add(thu);
        dateTextViews.add(fri);
        dateTextViews.add(sat);
        dateTextViews.add(sun);

        ImageView monSelected = view.findViewById(R.id.monSelected);
        ImageView tueSelected = view.findViewById(R.id.tueSelected);
        ImageView wedSelected = view.findViewById(R.id.wedSelected);
        ImageView thuSelected = view.findViewById(R.id.thuSelected);
        ImageView friSelected = view.findViewById(R.id.friSelected);
        ImageView satSelected = view.findViewById(R.id.satSelected);
        ImageView sunSelected = view.findViewById(R.id.sunSelected);

        monSelected.setVisibility(View.INVISIBLE);
        tueSelected.setVisibility(View.INVISIBLE);
        wedSelected.setVisibility(View.INVISIBLE);
        thuSelected.setVisibility(View.INVISIBLE);
        friSelected.setVisibility(View.INVISIBLE);
        satSelected.setVisibility(View.INVISIBLE);
        sunSelected.setVisibility(View.INVISIBLE);


        selectedImageViews.add(monSelected);
        selectedImageViews.add(tueSelected);
        selectedImageViews.add(wedSelected);
        selectedImageViews.add(thuSelected);
        selectedImageViews.add(friSelected);
        selectedImageViews.add(satSelected);
        selectedImageViews.add(sunSelected);
    }

    boolean isCurrentDateSet = false;

    private Calendar getTodayCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        return cal;
    }

    private void showHideCalc(int showDays) {
        Calendar today = Calendar.getInstance();
        int todayIndex = 0/*today.get(Calendar.DAY_OF_WEEK) - today.getFirstDayOfWeek()*/;

        if (showDays == 1) {
            calView.setVisibility(View.GONE);
            panelWeekDays.setVisibility(View.VISIBLE);


            for (int i = 0; i < dateTextViews.size(); i++) {
                if (i == todayIndex) {
                    dayLabelTextViews.get(i).setVisibility(View.VISIBLE);
                    dateTextViews.get(i).setVisibility(View.VISIBLE);
                    selectedImageViews.get(i).setVisibility(View.VISIBLE);

                    dateTextViews.get(i).setTextColor(getResources().getColor(R.color.white));
                    selectedImageViews.get(i).setVisibility(View.VISIBLE);
                    dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_date_circle_white);
                } else {
                    dayLabelTextViews.get(i).setVisibility(View.INVISIBLE);
                    dateTextViews.get(i).setVisibility(View.INVISIBLE);
                    selectedImageViews.get(i).setVisibility(View.INVISIBLE);

                    dateTextViews.get(i).setTextColor(getResources().getColor(R.color.app_grey_dark));
                    selectedImageViews.get(i).setVisibility(View.INVISIBLE);
                    dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);
                }
            }
        } else if (showDays == 7) {
            calView.setVisibility(View.GONE);
            panelWeekDays.setVisibility(View.VISIBLE);


            for (int i = 0; i < dateTextViews.size(); i++) {
                dayLabelTextViews.get(i).setVisibility(View.VISIBLE);
                dateTextViews.get(i).setVisibility(View.VISIBLE);
            }
        } else if (showDays == 30) {
            calView.setVisibility(View.VISIBLE);
            panelWeekDays.setVisibility(View.GONE);
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

    }

    private void updateWeeklyCalendar() {
        // Show month
        tvMonth.setText(monthTitleString[calSelectedDate.get(Calendar.MONTH) + 1]);

        // Update Weekly Calendar
        for (int i = 0; i < dateTextViews.size(); i++) {
            dayLabelTextViews.get(i).setVisibility(View.VISIBLE);
            dateTextViews.get(i).setVisibility(View.VISIBLE);

            dateTextViews.get(i).setText(getWeekRange(calSelectedDate, i));
            dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);

            Calendar calItemDate = getWeekRangeCalendar(calSelectedDate, i);
            if (isSameDay(calItemDate, calSelectedDate)) {
                dateTextViews.get(i).setTextColor(getResources().getColor(R.color.white));
                selectedImageViews.get(i).setVisibility(View.VISIBLE);
                dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_date_circle_white);
            } else {
                dateTextViews.get(i).setTextColor(getResources().getColor(R.color.app_grey_dark));
                selectedImageViews.get(i).setVisibility(View.INVISIBLE);
                dateTextViews.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);
            }

            dateTextViews.get(i).setTag(i);
            dateTextViews.get(i).setOnClickListener(weeklyCalendarItemClickListener);
        }
    }

    OnClickListener weeklyCalendarItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            int dayIndexInWeek = (int) view.getTag();
            paintSelected(calSelectedDate, dateTextViews, selectedImageViews, 7, dayIndexInWeek);
            calSelectedDate = getWeekRangeCalendar(calSelectedDate, dayIndexInWeek);

            getApptData(false);

            calView.setDate(calSelectedDate.getTimeInMillis());
        }
    };

    private void paintSelected(Calendar calendar, List<TextView> textView, List<ImageView> imageView, int size, int selected) {
        for (int i = 0; i < size; i++) {
            //textView.get(i).setText(getWeekRange(calendar, i));
            textView.get(i).setTextColor(getResources().getColor(R.color.app_grey_dark));
            textView.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.rectangle);
            imageView.get(i).setVisibility(View.INVISIBLE);

            if (i == selected) {
                textView.get(i).setTextColor(getResources().getColor(R.color.white));
                imageView.get(i).setVisibility(View.VISIBLE);
                textView.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.selector_date_circle_white);
            }
        }
    }

    private String getWeekRange(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            last.add(Calendar.DAY_OF_YEAR, addNext - 7 + 1);
        } else {
            last.add(Calendar.DAY_OF_YEAR, addNext + 1);
        }

        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(last.getTime());

       /* Calendar first = (Calendar) calendar.clone();
        first.set(Calendar.DAY_OF_WEEK,
                (addNext + 2) % 7);
        SimpleDateFormat df = new SimpleDateFormat("d");
        return df.format(first.getTime());*/
    }

    private Calendar getWeekRangeCalendar(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            last.add(Calendar.DAY_OF_YEAR, addNext - 7 + 1);
        } else {
            last.add(Calendar.DAY_OF_YEAR, addNext + 1);
        }

        return last;

        /*Calendar first = (Calendar) calendar.clone();
        first.set(Calendar.DAY_OF_WEEK,
                (addNext + 2) % 7);
        return first;*/
    }

    private void addNewMeet() {
        closeMeetSlider();

        Intent intent = new Intent(mContext, ActivityAddMeet.class);
        ContactInfo contactInfo = null;
        if (isContactSelected()) {
            contactInfo = getSelectedContact();
            // intent.putExtra("email", contactInfo.getEmail());
            // intent.putExtra("phone", contactInfo.getCp());
            // intent.putExtra("title", tvMeetTitle.getText().toString());
            if (contactInfo != null) {
                intent.putExtra("contact", contactInfo);
            }
        }
        intent.putExtra("time", calSelectedDate.getTimeInMillis());
        startActivityForResult(intent, REQUEST_MEET);


    }

    private void showAgendaData() {
        panelAgenda.setVisibility(View.VISIBLE);
        updateAgendaItem(isFilterAgenda);

        btnAddAppt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //saveCateData();

        handlerMsg.removeMessages(0);

        connectionActivity = null;

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(msgReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), ActivityHomeMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        return true;
    }

    boolean useLocalDB = true;

    private void getMessages(boolean showProgress) {
        ContactInfo contactInfo = getSelectedContact();
        boolean showAllMessages = noContactSelected();
        int mlid = 0;
        if (contactInfo != null) {
            if (isContactSelected()) {
                mlid = contactInfo.getMlid();
            }
        }
        if (spinnerContact2.getText().toString().isEmpty()) {
            contactList.clear();
            // refreshContacts();
            getMessagesFromLocalDb(0, true);

        } else {
            if (!useLocalDB && getLocation()) {
                try {
                    HashMap<String, String> params = new HashMap<>();
                    String baseUrl = BaseFunctions.getBaseUrl(this,
                            "CJLGet",
                            BaseFunctions.MAIN_FOLDER,
                            getUserLat(),
                            getUserLon(),
                            mMyApp.getAndroidId());
                    String extraParams =
                            "&mode=" + "getSMS" +
                                    "&misc=" + mlid;
                    baseUrl += extraParams;
                    Log.e("Request", baseUrl);

                    if (showProgress) {
                        showProgressDialog();
                    }

                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);

                    String finalBaseUrl = baseUrl;
                    StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (showProgress) {
                                hideProgressDialog();
                            }

                            Log.e("GetSMS", response);

                            int originalMsgs = messageList.size();
                            //messageList.clear();

                            if (!TextUtils.isEmpty(response)) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                    if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                        //showToastMessage(jsonObject.getString("msg"));
                                    } else {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject data = jsonArray.getJSONObject(i);

                                            Message newItem = new Message();
                                            newItem.setID(data.optInt("ID"));
                                            newItem.setStatusID(data.optString("StatusID"));
                                            newItem.setFromID(data.optInt("fromID"));
                                            newItem.setToID(data.optInt("toID"));
                                            newItem.setEmployerID(data.optInt("employerID"));
                                            newItem.setMsg(data.optString("Msg"));
                                            newItem.setCreateDate(data.optString("CreateDate"));
                                            newItem.setName(data.optString("name"));

                                            messageList.add(newItem);
                                        }

                                        if (showAllMessages) {
                                            panelMessageAll.setVisibility(View.VISIBLE);
                                            mAllMessageAdapter.notifyDataSetChanged();
                                        } else {
                                            panelMessageAll.setVisibility(View.GONE);
                                            mMessageAdapter.notifyDataSetChanged();
                                            if (messageList.size() > 0) {
                                                // Go to bottom in case of first call
                                                mMessageRecycler.scrollToPosition(messageList.size() - 1);
                                            }
                                        }
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
                            if (showProgress) {
                                hideProgressDialog();
                            }
                            baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));

                /*if (TextUtils.isEmpty(error.getMessage())) {
                    showAlert(R.string.error_conn_error);
                } else {
                    showAlert(error.getMessage());
                }*/
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
                } catch (Exception e) {
                    if (dataUtil != null) {
                        dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                        dataUtil.zzzLogIt(e, "CJLGet");
                    }
                }
            } else {
                getMessagesFromLocalDb(mlid, showAllMessages);
            }

        }
    }

    private void getMessagesFromLocalDb(int mlid, boolean showAllMessages) {
        // Get Messages from Local DB
        messageList.clear();

        int myMLID = appSettings.getUserId();
        messageList.addAll(dm.getMessageList(mlid, myMLID));
        if (showAllMessages) {
            if (messageList.size() > 0) {
                for (int i = 0; i < messageList.size(); i++) {
                    Message message = messageList.get(i);

                    int msgMLID = message.getToID();
                    // In case of me
                    if (message.getMLID() == myMLID) {
                        ArrayList<Message> myToMyMessages = dm.getMessageList(message.getMLID(), myMLID);
                        if (myToMyMessages.size() > 0) {
                            messageList.set(i, myToMyMessages.get(myToMyMessages.size() - 1));
                        } else {
                            messageList.remove(i);
                        }
                        break;
                    }
                }
            }

            panelMessageAll.setVisibility(View.VISIBLE);
            mAllMessageAdapter.notifyDataSetChanged();
        } else {
            panelMessageAll.setVisibility(View.GONE);
            if (mlid == 0) {
                messageList.clear();
            }

            mMessageAdapter.notifyDataSetChanged();
            if (messageList.size() > 0) {
                // Go to bottom in case of first call
                mMessageRecycler.scrollToPosition(messageList.size() - 1);
            }
        }
    }

    private void sendMessages(String msg) {
        ContactInfo contactInfo = getSelectedContact();
        Log.d("@SendMessage::::contactInfo", "" + contactInfo);
        int mlid = 0;
        if (contactInfo != null) {
            mlid = contactInfo.getMlid();
        }
        double amt = 0;
        if (!edtAmount.getText().toString().isEmpty()) {
            amt = Double.parseDouble(edtAmount.getText().toString().trim());
        }

        if (noContactSelected()) {
            showToastMessage("No Contact Info");
            return;
        }

        String nMsg = msg.replace("&", "|||");

        if (!useLocalDB && getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "addSMS",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String email = contactInfo.getEmail();
                if (TextUtils.isEmpty(email)) {
                    email = "";
                }

                String extraParams =
                        "&mode=" + "161" +
                                //  "&misc=" + appSettings.getUserId() +
                                //  "&employerID=" + appSettings.getEmpId() +
                                "&toMLID=" + mlid +
                                "&note=" + nMsg +
                                "&amt=" + amt +
                                "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                                //"&recUTC=" + "0" +
                                "&email=" + email;
                baseUrl += extraParams;
                Log.e("@Request", baseUrl);

                // New Message
                Message newItem = new Message();
                newItem.setName(appSettings.getFN() + " " + appSettings.getLN());
                newItem.setMsg(msg);
                newItem.setStatusID("0");
                newItem.setFromID(appSettings.getUserId());
                newItem.setToID(contactInfo.getMlid());
                newItem.setEmployerID(0);
                newItem.setCreateDate(DateUtil.toStringFormat_12(new Date()));
                messageList.add(newItem);
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.scrollToPosition(messageList.size() - 1);

                //Log.e("listCater", params.toString());

                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("@addSMS", response);

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showToastMessage(jsonObject.getString("msg"));
                                } else {
                                    getMessages(false);
                                    edtChatMessage.setText("");

                                    // Send push
                                    sendPush(msg);
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "addSMS");
                }
            }
        } else {
            // Add Message tp Local DB
            Message newMsg = new Message();
            newMsg.setFromID(appSettings.getUserId());
            newMsg.setToID(mlid);
            newMsg.setMsg(msg);
            newMsg.setCreateDate(DateUtil.toStringFormat_20(new Date()));
            newMsg.setName(String.format("%s %s", appSettings.getFN(), appSettings.getLN()));
            dm.addMessage(newMsg);

            getMessages(false);
            edtChatMessage.setText("");

            // Send push
            sendPush(msg);
        }
    }

    private int currUserMLID = 0;
    ArrayList<FCMTokenData> currUserTokens = new ArrayList<>();

    private void sendPush(final String msg) {
        ContactInfo contactInfo = getSelectedContact();
        Log.d("@SendPush:::: contactInfo", "" + contactInfo);

        int mlid = contactInfo.getMlid();
        Log.d("@SendPush:::: mlid", "" + mlid);
        if (getLocation()) {
            try {
                NotificationHelper notificationHelper = new NotificationHelper(mlid, mContext, (BaseActivity) mContext);

                String timesent = DateUtil.dateToString(new Date(), DateUtil.DATE_FORMAT_38);
                String name = appSettings.getFN() + " " + appSettings.getLN();
                JSONObject payloadsData = new JSONObject();
                payloadsData.put("message", String.format("%s\n%s", name, timesent));
                payloadsData.put("subject", msg);   // This is required for msg

                // Already got token, send push directly using available token/s
                if (currUserMLID == mlid) {
                    notificationHelper.sendPushNotification(mContext, currUserTokens, PayloadType.PT_Text_Message, payloadsData);
                    return;
                }
                // Retrieve token and send push
                notificationHelper.getToken(1, payloadsData, new OnGetTokenListener() {
                    @Override
                    public void onSuccess(String response) {
                        currUserMLID = mlid;
                        currUserTokens.clear();
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                    }

                    @Override
                    public void onEmptyResponse() {
                    }

                    @Override
                    public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
                        currUserTokens.addAll(tokenList);
                        Log.d("@SendPush:::: currUserTokens", "" + currUserTokens);
                    }

                    @Override
                    public void onJsonArrayEmpty() {

                    }

                    @Override
                    public void onJsonException() {
                    }

                    @Override
                    public void onTokenListEmpty() {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPushVCall() {
        ContactInfo contactInfo = getSelectedContact();
        Log.d("@SendPushVCall:::: contactInfo", "" + contactInfo);

        int mlid = contactInfo.getMlid();
        Log.d("@SendPush:::: mlid", "" + mlid);
        if (getLocation()) {
            try {
                NotificationHelper notificationHelper = new NotificationHelper(mlid, mContext, (BaseActivity) mContext);

                String timesent = DateUtil.dateToString(new Date(), DateUtil.DATE_FORMAT_38);
                String name = appSettings.getFN() + " " + appSettings.getLN();
                JSONObject payloadsData = new JSONObject();
                payloadsData.put("message", String.format("%s%s", name, timesent));
                payloadsData.put("subject", "vcalling");   // This is required for msg
//                payloadsData.put("agoraId", agoraAppId);   // This is required for msg
                payloadsData.put("agoraToken", agoraToken);   // This is required for msg
                payloadsData.put("fromFcmToken", appSettings.getDeviceToken());   // This is required for msg

                // Already got token, send push directly using available tokens
                if (currUserMLID == mlid) {
                    notificationHelper.sendPushNotification(mContext, currUserTokens, PayloadType.PT_INCOMING_VIDEO_CALL, payloadsData);
                    return;
                }
                // Retrieve token and send push
                notificationHelper.getToken(310, payloadsData, new OnGetTokenListener() {
                    @Override
                    public void onSuccess(String response) {
                        currUserMLID = mlid;
                        currUserTokens.clear();
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                    }

                    @Override
                    public void onEmptyResponse() {
                    }

                    @Override
                    public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
                        currUserTokens.addAll(tokenList);
                    }

                    @Override
                    public void onJsonArrayEmpty() {

                    }

                    @Override
                    public void onJsonException() {
                    }

                    @Override
                    public void onTokenListEmpty() {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    /*private void sendPushData(ArrayList<FCMTokenData> tokenList, String mlid, String msg) throws JSONException {

        if (!tokenList.isEmpty()) {
            JSONObject jAdditionalData = new JSONObject();
            jAdditionalData.put("name", appSettings.getFN() + " " + appSettings.getLN());
            jAdditionalData.put("SenderID", appSettings.getUserId());
            jAdditionalData.put("email", appSettings.getEmail());
            jAdditionalData.put("fn", appSettings.getFN());
            jAdditionalData.put("ln", appSettings.getLN());
            jAdditionalData.put("co", appSettings.getCompany());


            String title = "Message from " + appSettings.getFN();
            String message = msg;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new SendPushTaskToAVA(tokenList, 1, title, message, jAdditionalData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
            } else {
                new SendPushTaskToAVA(tokenList, 1, title, message, jAdditionalData).execute("");
            }
        }
    }*/

    private void refreshContactInfo() {
        contactList.clear();
        refreshContacts();

        //spinnerContact.setSelection(0);
        checkContactLikeFavorStatus();
        checkContactAddress();
    }

    int selectedMLID = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                    appSettings.setAvatarImage(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS) {
            // checkForBatteryOptimizations();
        } else if (requestCode == REQUEST_CHANGE_CONTACT) {
            contactList.clear();
            refreshContacts();

            if (data != null) {
                ContactInfo contactSelected = data.getExtras().getParcelable("contact");
                if (contactSelected != null) {
                    setSelectedContactIndex(0, toModel(contactSelected));
                    edtPhoneNumber.setText(PhonenumberUtils.getFormattedPhoneNumber(contactSelected.getCp()));
                }
            }
        } else if (requestCode == REQUEST_MEET && resultCode == RESULT_OK) {
            long calID = 0;
            int ldbid = 0;
            if (data != null) {
                if (data.getIntExtra("attendeeMLID", 0) == -1) {
                    calID = data.getLongExtra("calID", 0);
                    ldbid = data.getIntExtra("ldbID", 0);
                }
                tvMeetTitle.setText(data.getStringExtra("title"));
                calStart.setTimeInMillis(data.getLongExtra("startDate", 0));
                calEnd.setTimeInMillis(data.getLongExtra("endDate", 0));

                tz = data.getFloatExtra("tz", 0.0F);
                strLocation = data.getStringExtra("location");
                strEmail = data.getStringExtra("email");
                strPhone = data.getStringExtra("phone");

                //tvMeetTime.setText(String.format("%s - %s", DateUtil.toStringFormat_7(calStart.getTime()), DateUtil.toStringFormat_7(calEnd.getTime())));
                tvMeetTime.setText(String.format("%s", DateUtil.toStringFormat_7(calStart.getTime())));
                tvMeetTime.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvMeetTime.setSelected(true);
            }
            updateAgendaItem(isFilterAgenda);
            if (calID == 0) {
                getApptData(true);
            } else {
                updateAttendeeMLID(calID, ldbid);
            }
        } else if (requestCode == REQUEST_SEARCH_CONTACT && resultCode == RESULT_OK) {
            int selectedPos = data.getIntExtra("position", 0);
            if (selectedPos > 0) {
                setSelectedContactIndex(selectedPos, selectedModel);
            }
        } else if (requestCode == REQUEST_SEARCH_MEET && resultCode == RESULT_OK) {
            CalendarData.Data contactInfo = data.getParcelableExtra("meet_info");
        } else if (requestCode == REQUEST_PAY_INVOICE && resultCode == RESULT_OK) {
            if (data != null) {
                showToastMessage(mContext, data.getStringExtra("msg"));
                if (data.getBooleanExtra("status", false)) {
                    getInvoices();
                }
            }
        }
    }

    private void showPanel(int panelID) {
        Log.e("testt", "" + panelID);
        if (panelID == PANEL_MESSAGE) {
            panelMessage.setVisibility(View.VISIBLE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.GONE);

            //spinnerContact.setSelection(0, true);

            if (getIntent().getExtras() != null) {

            } else {
                getMessages(false);
            }
        } else if (panelID == PANEL_PHONE) {
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.VISIBLE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.GONE);
        } else if (panelID == PANEL_LOCATION) {
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.VISIBLE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.GONE);
        } else if (panelID == PANEL_PAY_SEND || panelID == PANEL_PAY_RECEIVE) {
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.VISIBLE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.GONE);
        } else if (panelID == PANEL_NOTES) {
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.VISIBLE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.GONE);

            getNotes();
        } else if (panelID == PANEL_MEET) {
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.VISIBLE);
            panelTasks.setVisibility(View.GONE);

            panelAgenda.setVisibility(View.GONE);
            btnAddAppt.setVisibility(View.GONE);

            getApptData(false);
        } else if (panelID == PANEL_TASKS) {
            chkTaskShowCompleted.setChecked(false);
            panelMessage.setVisibility(View.GONE);
            panelCalls.setVisibility(View.GONE);
            panelLocation.setVisibility(View.GONE);
            panelPay.setVisibility(View.GONE);
            panelNotes.setVisibility(View.GONE);
            panelCal.setVisibility(View.GONE);
            panelTasks.setVisibility(View.VISIBLE);
        }

        /*if (panelID == PANEL_MESSAGE) {
            handlerMsg.sendEmptyMessage(0);
        } else {
            handlerMsg.removeMessages(0);
        }*/
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        hideKeyboard(edtItem);
        String item = edtItem.getText().toString().trim();
        String selectedIDs = getSelectedList();

        Log.e("testt", "" + viewId);

        if (viewId == R.id.btnMessage) {
            showPanel(PANEL_MESSAGE);
        } else if (viewId == R.id.btnCall) {

            /*if (contactList.size() > 1 && isContactSelected()) {
                ContactInfo contactInfo = contactList.get(spinnerContact.getSelectedItemPosition());
                if (TextUtils.isEmpty(contactInfo.getCp())) {
                    showToastMessage("No Phone Information");
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactInfo.getCp()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            } else {
                showToastMessage("No Contact Selected");
            }*/

            if (noContactSelected()) {
                edtPhoneNumber.setText("");
            }
            updateCallHistory();

            showPanel(PANEL_PHONE);
        } else if (viewId == R.id.btnLocation) {
            showPanel(PANEL_LOCATION);
        } else if (viewId == R.id.btnReschedule) {
            showPanel(PANEL_MEET);
        } else if (viewId == R.id.btnPay) {
            getInvoices();
            showPanel(PANEL_PAY_RECEIVE);
        } else if (viewId == R.id.btnNote) {
            showPanel(PANEL_NOTES);
            getNotes();
        } else if (viewId == R.id.btnTasks) {
            showPanel(PANEL_TASKS);
            doTask(TASK_SHOW, null, null, null);
        } else if (viewId == R.id.btnPayNow) {
            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo != null) {
                askForPIN();
            } else {
                showToastMessage("Please select a contact");
            }
        } else if (viewId == R.id.btnAddThisForContact) {
            addOrder();
        } else if (viewId == R.id.btnListInvoices) {
            getInvoices();
        } else if (viewId == R.id.btnAddNote) {
            addNotes();
        } else if (viewId == R.id.btnSetPriority) {
            float itemVal = -1;
            try {
                itemVal = Float.parseFloat(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (itemVal < 0.0f || itemVal > 100.0f) {
                showToastMessage("Please input value between 0 and 100");
                return;
            }

            if (TextUtils.isEmpty(selectedIDs)) {
                showToastMessage("Please select item.");
                return;
            }

            doTask(TASK_PRIORITY, item, item, selectedIDs);
        } else if (viewId == R.id.btnCompleted) {
            if (TextUtils.isEmpty(selectedIDs)) {
                showToastMessage("Please select item.");
                return;
            }
            doTask(TASK_SET_COMPLETED, "0", "0", selectedIDs);

        } else if (viewId == R.id.btnAssigned) {
            if (isContactSelected()) {
                if (TextUtils.isEmpty(selectedIDs)) {
                    showToastMessage("Please select item.");
                    return;
                }
                ContactInfo contactInfo = getSelectedContact();
                doTask(TASK_ASSIGN, contactInfo.getMlid() + "", "0", selectedIDs);
            }
        } else if (viewId == R.id.btnAddTask) {
            /*if (TextUtils.isEmpty(item)) {
                showToastMessage("Please input field.");
                return;
            }*/
            doTask(TASK_ADD_NEW, "0", item, "");
        } else if (viewId == R.id.btnPhoneFilter) {
            String[] colors = {"All calls", "Missed calls", "Rejected calls", "Outgoing calls", "Incoming calls"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filter calls");
            builder.setSingleChoiceItems(colors, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else if (viewId == R.id.btnPhoneDot) {
            /*if (1 == appSettings.getUserId()) {

                // Check User
                if (noContactSelected()) {
                    showToastMessage("Please select contact.");
                    return;
                }

                PopupMenu popup = new PopupMenu(mContext, view);
                //makePopupMenuIconVisible(popup);

                popup.getMenuInflater().inflate(R.menu.menu_nda, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_nda) {

                            ContactInfo contactInfo = getSelectedContact();
                            if (getLocation()) {

                                HashMap<String, String> params = new HashMap<>();
                                String baseUrl = BaseFunctions.getBaseUrl(connectionActivity,
                                        "sendEmailViaContact",
                                        BaseFunctions.APP_FOLDER,
                                        getUserLat(),
                                        getUserLon(),
                                        mMyApp.getAndroidId());
                                String extraParams =
                                        "&email=" + contactInfo.getEmail() +
                                                "&co=" + contactInfo.getCo() +
                                                "&emailID=" + "72" +
                                                "&recvrMLID=" + contactInfo.getMlid();
                                baseUrl += extraParams;
                                Log.e("Request", baseUrl);

                                showProgressDialog();
                                RequestQueue queue = Volley.newRequestQueue(mContext);

                                //HttpsTrustManager.allowAllSSL();
                                GoogleCertProvider.install(mContext);

                                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideProgressDialog();

                                        Log.e("sendEmailViaContact", response);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        hideProgressDialog();

                                        if (TextUtils.isEmpty(error.getMessage())) {
                                            showAlert(R.string.error_invalid_credentials);
                                        } else {
                                            showAlert("No Connection");
                                        }
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
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }*/
        } else if (viewId == R.id.btnPhoneDial) {
            String contactPhone = edtPhoneNumber.getText().toString().trim();
            if (!TextUtils.isEmpty(contactPhone)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactPhone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                CallHistory callHistory = new CallHistory();
                // int position = spinnerContact.getSelectedItemPosition();
                int contactID = 0;
                if (isContactSelected()) {
                    ContactInfo contactInfo = getSelectedContact();
                    contactID = contactInfo.getId();
                }
                callHistory.setLdbid(contactID);
                callHistory.setPhNumber(contactPhone);
                callHistory.setCallDate(new Date());
                callHistory.setCallType(CallLog.Calls.OUTGOING_TYPE);

                dm.addCallHistory(callHistory);

                updateCallHistory();
            } else {
                showToastMessage(R.string.error_invalid_phone);
            }
        }
//        else if (viewId == R.id.ivVideo) {
//            String contactPhone = edtPhoneNumber.getText().toString().trim();
//            if (!TextUtils.isEmpty(contactPhone)) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:" + contactPhone));
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//
//                CallHistory callHistory = new CallHistory();
//                // int position = spinnerContact.getSelectedItemPosition();
//                int contactID = 0;
//                if (isContactSelected()) {
//                    ContactInfo contactInfo = getSelectedContact();
//                    contactID = contactInfo.getId();
//                }
//                callHistory.setLdbid(contactID);
//                callHistory.setPhNumber(contactPhone);
//                callHistory.setCallDate(new Date());
//                callHistory.setCallType(CallLog.Calls.OUTGOING_TYPE);
//
//                dm.addCallHistory(callHistory);
//
//                updateCallHistory();
//            } else {
//                showToastMessage(R.string.error_invalid_phone);
//            }
//        }
        else if (viewId == R.id.btnDirectionTo) {
            /*  if (contactList.size() > 1 && isContactSelected()) {*/
            String address = tvContactAddress.getText().toString().trim();
            // String mlId = contactList.get(spinnerContact.getSelectedItemPosition()).getMlid();
            HistoryDataSource historyDataSource = new HistoryDataSource(mContext);
            historyDataSource.open();
            ArrayList<HistoryData> histories = historyDataSource.getAllLocationHistory();
            historyDataSource.close();
            DataAdapter dataAdapter = new DataAdapter(histories, this);
            ContactInfo selectedContact = getSelectedContact();
            dialog = new AboveAddressHistoryDialog(this, address, selectedContact, dataAdapter);
            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams windowLp = dialog.getWindow().getAttributes();
            windowLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            dialog.getWindow().setAttributes(windowLp);
            dialog.setCanceledOnTouchOutside(true);
            /*  }else {
                showToastMessage("Please select contact");
            }*/
        } else if (viewId == R.id.btnShareLocation) {
            if (contactList.size() > 0/* && isContactSelected()*/) {
                ContactInfo selectedContact = getSelectedContact();

                if (selectedContact == null) {
                    showToastMessage("Please select contact");
                    return;
                }
                ShareLocationDialog dialog = new ShareLocationDialog(this, selectedContact, this);
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams windowLp = dialog.getWindow().getAttributes();
                windowLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                dialog.getWindow().setAttributes(windowLp);
                dialog.setCanceledOnTouchOutside(true);
            } else {
                showToastMessage("Please select contact");
            }
            /*PopupMenu popup = new PopupMenu(mContext, findViewById(R.id.btnShareLocation));
            //makePopupMenuIconVisible(popup);
            popup.getMenuInflater().inflate(R.menu.menu_share_location, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_inapp) {
                        shareLocation();
                    } else if (itemId == R.id.menu_inemail) {
                        if (contactList.size() > 1 && isContactSelected()) {
                            String userLat = getUserLat();
                            String userLon = getUserLon();

                            String email = contactList.get(spinnerContact.getSelectedItemPosition()).getEmail();
                            if (isValidEmail(email)) {
                                sendLocationEmail();
                            } else {
                                showToastMessage("Email is invalid!");
                            }
                        }
                    }
                    return true;
                }
            });

            popup.show();//showing popup menu*/
        } else if (viewId == R.id.tabPayReceived) {
            getInvoices();
            panelPayReceived.setVisibility(View.VISIBLE);
            panelPayShare.setVisibility(View.GONE);
            tabPayReceived.setSelected(true);
            tabPayShare.setSelected(false);
        } else if (viewId == R.id.tabPayShare) {
            panelPayReceived.setVisibility(View.GONE);
            panelPayShare.setVisibility(View.VISIBLE);
            tabPayReceived.setSelected(false);
            tabPayShare.setSelected(true);
        }
    }

    private void makePopupMenuIconVisible(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLocationEmail() {
        ContactInfo contactInfo = getSelectedContact();
        String email = contactInfo.getEmail();

        if (getLocation()) {
            String mapLocation = "https://www.google.com/maps/@?api=1&map_action=map&center=" + getUserLat() + "%2C" + getUserLon() + "&zoom=15&basemap=roadmap";

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (intent == null) {
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                if (!TextUtils.isEmpty(email)) {
                    String[] supportTeamAddrs = {email};
                    intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                }
                intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
                intent.putExtra(Intent.EXTRA_TEXT, mapLocation);
                try {
                /*if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    showToastMessage("Please install Email App to use function");
                }*/
                    startActivity(intent);
                } catch (Exception e) {
                    showToastMessage("Please install Email App to use function");
                }
            } else {
                intent = new Intent(Intent.ACTION_SEND);
                if (!TextUtils.isEmpty(email)) {
                    String[] supportTeamAddrs = {email};
                    intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
                }

                intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
                intent.putExtra(Intent.EXTRA_TEXT, mapLocation);
                //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send Location Mail"));
            }
        } else {
            showToastMessage("Please allow GPS to share your location.");
        }
    }

    private void askForPIN() {
        String amt = edtAmount.getText().toString().trim();

        hideKeyboard(edtAmount);

        if (TextUtils.isEmpty(amt)) {
            showToastMessage("Please input amount");
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View pinLayout = inflater.inflate(R.layout.dialog_pin_w_amount, null);
        final TextView title = pinLayout.findViewById(R.id.dialog_title);
        final TextView amount = pinLayout.findViewById(R.id.dialog_amount);
        title.setText("Enter PIN to Complete Payment");
        final EditText pin = pinLayout.findViewById(R.id.pin);

        final ImageView grey_line = pinLayout.findViewById(R.id.grey_line);
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        String formattedAmt = "Amount: $" + formatter.format(Double.parseDouble(amt));
        amount.setText(formattedAmt);


        grey_line.setVisibility(View.GONE);
        pin.requestFocus();

        final Button submit = pinLayout.findViewById(R.id.pin_submit);
        submit.setText("Continue");

        final Button cancel = pinLayout.findViewById(R.id.pin_cancel);
        cancel.setText("Cancel");

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setView(pinLayout);
        alert.setCancelable(true);
        final android.app.AlertDialog dialog = alert.create();
        dialog.show();

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPIN = appSettings.getPIN();
                String pinNumber = pin.getText().toString().trim();
                if (!TextUtils.isEmpty(appPIN) && appPIN.equals(pinNumber)) {
                    if (getLocation()) {
                        payNow();
                    }
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void checkUserName() {

        String userName = edtUserName.getText().toString().trim();

        if (phonenumberUtils.isValidPhoneNumber(userName)) {
            // Phone number and no need change
        } else if (isValidEmail(userName)) {
            // Email and no need change
        } else if (userName.contains("@")) {
            userName = userName.replace("@", "");
        }

        if (TextUtils.isEmpty(userName)) {
            showToastMessage("Please input username.");
            return;
        }

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(connectionActivity,
                        "cjlGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "testUN" +
                                "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                                "&misc=" + userName;
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("testUN", response);


                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray baseJsonArray = new JSONArray(response);
                                if (baseJsonArray.length() > 0) {
                                    if (baseJsonArray.length() == 1) {
                                        JSONObject statusObject = baseJsonArray.getJSONObject(0);
                                        if (statusObject.has("status") && statusObject.get("status") instanceof Boolean && !statusObject.getBoolean("status")) {
                                            showToastMessage("No info was returned");
                                            hideKeyboard();
                                        }
                                        return;
                                    }
                                    JSONArray baseInfoArray = baseJsonArray.getJSONArray(1);
                                    JSONObject baseInfoObject = (JSONObject) baseInfoArray.get(0);
                                    if (baseInfoObject.getInt("status") == 0) {
                                        showToastMessage("No info was returned");
                                    } else {
                                        if (baseInfoObject.has("name")) {
                                            String foundName = baseInfoObject.getString("name");
                                            txtPayUserInfo.setText(HtmlCompat.fromHtml("<b>Found:</b> " + foundName, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                            txtPayUserInfo.setVisibility(View.VISIBLE);
                                            String toMLID = baseInfoObject.getString("toMLID");
                                            btnPayNow.setTag(Integer.parseInt(toMLID));
                                            btnPayNow.setEnabled(true);
                                        } else {
                                            txtPayUserInfo.setVisibility(View.GONE);
                                        }
                                        String toMLID = baseInfoObject.getString("toMLID");
                                    }
                                } else {
                                    showAlert("Not able to contact the Attendee using Notifications.\n" +
                                            "You might want to call them.");
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
                            showAlert(R.string.error_invalid_credentials);
                        } else {
                            showAlert("No Connection");
                        }

                        //showMessage(error.getMessage());
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "cjlGet");
                }
            }
        }
    }

    private void payNow() {

        // String userName = edtUserName.getText().toString().trim();
        String amt = edtAmount.getText().toString().trim();
        String notes = edtPayNotes.getText().toString().trim();

        hideKeyboard(edtAmount);

        if (TextUtils.isEmpty(amt)) {
            showToastMessage("Please input amount");
            return;
        }

        ContactInfo contactInfo = getSelectedContact();
        if (contactInfo != null) {
            notes = notes.replace("&", "|||");
            if (contactInfo.getMlid() > 0 || isValidEmail(contactInfo.getEmail())) {
                String username = contactInfo.getEmail();
                //String payNotes = edtPayNotes.getText().toString().trim();

                if (getLocation()) {
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        String baseUrl = BaseFunctions.getBaseUrl(this,
                                "addSMS",
                                BaseFunctions.MAIN_FOLDER,
                                getUserLat(),
                                getUserLon(),
                                mMyApp.getAndroidId());
                        String extraParams =
                                "&mode=" + "161" +
                                        "&toMLID=" + String.valueOf(contactInfo.getMlid()) +
                                        "&Amt=" + amt +
                                        "&email=" + username +
                                        "&phoneTime=" + DateUtil.toStringFormat_12(new Date()) +
                                        "&note=" + notes;
                        baseUrl += extraParams;
                        Log.e("Request", baseUrl);

                        showProgressDialog();
                        RequestQueue queue = Volley.newRequestQueue(mContext);

                        //HttpsTrustManager.allowAllSSL();
                        GoogleCertProvider.install(mContext);

                        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                hideProgressDialog();

                                Log.e("2377", response);

                                if (response != null || !response.isEmpty()) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        if (jsonArray.length() > 0) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                                showAlert(jsonObject.getString("msg"));
                                            } else {
                                                showToastMessage(jsonObject.getString("msg")/*"Success your payment!"*/);
                                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                                ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                                String token = jsonObject.optString("Token");
                                                if (!TextUtils.isEmpty(token)) {
                                                    tokenList.add(new FCMTokenData(token, FCMTokenData.OS_UNKNOWN));
                                                }
                                                if (!tokenList.isEmpty()) {
                                                    JSONObject payload = new JSONObject();
                                                    String senderName = appSettings.getFN() + " " + appSettings.getLN();
                                                    String message = String.format(" Tap to see your Instant Funds that %s has sent you. \nThey are available NOW!", senderName);
                                                    payload.put("message", message);
                                                    payload.put("orderId", "1");
                                                    payload.put("SenderName", senderName);
                                                    payload.put("SenderID", appSettings.getUserId());
                                                    notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Funds_Sent, payload);
                                                }
                                            }
                                        } else {
                                            // Show Alert
                                            showAlert("Not able to contact the Attendee using Notifications.\n" +
                                                    "You might want to call them.");
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
                                    showAlert(R.string.error_invalid_credentials);
                                } else {
                                    showAlert("No Connection");
                                }

                                //showMessage(error.getMessage());
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
                    } catch (Exception e) {
                        if (dataUtil != null) {
                            dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                            dataUtil.zzzLogIt(e, "addSMS");
                        }
                    }
                }

            } else {
                showToastMessage("Please select a contact");
            }
        } else {
            showToastMessage("Please select a contact");
        }
    }

    private void addOrder() {

        if (noContactSelected()) {
            showToastMessage("Please select contact");
        } else {
            if (mInvoiceItems.isEmpty()) {
                showToastMessage("Please add at least 1 invoice item");
                return;
            }

            ContactInfo contactInfo = getSelectedContact();
            if (contactInfo.getMlid() == 0) {
                try {
                    HashMap<String, String> params = new HashMap<>();
                    String baseUrl = BaseFunctions.getBaseUrl(this,
                            "CJLGet",
                            BaseFunctions.MAIN_FOLDER,
                            getUserLat(),
                            getUserLon(),
                            mMyApp.getAndroidId());
                    String email;
                    if (!isEmailValid(contactInfo.getEmail()) && !TextUtils.isEmpty(contactInfo.getCo())) {
                        email = contactInfo.getwEmail();
                    } else {
                        email = contactInfo.getEmail();
                    }
                    String extraParams =
                            "&mode=" + "rtnMLID" +
                                    "&LDBID=" + String.valueOf(contactInfo.getId()) +
                                    "&misc=" + "" +
                                    "&industry=" + "0" +
                                    "&email=" + email;
                    baseUrl += extraParams;
                    Log.e("Request", baseUrl);

                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);

                    //HttpsTrustManager.allowAllSSL();
                    GoogleCertProvider.install(mContext);

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

                                        contactInfo.setMlid(fMLID);
                                        dm.updateContact(contactInfo);

                                        createInvoice();
                                    } else {
                                        showToastMessage(responseStatus.getString("msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showAlert(e.getMessage());
                                }
                            } else {
                                //showAlert("Server Error");
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgressDialog();
                            if (TextUtils.isEmpty(error.getMessage())) {
                                //showAlert("Server error!");
                            } else {
                                //showAlert(error.getMessage());
                            }

                            finish();
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
                } catch (Exception e) {
                    if (dataUtil != null) {
                        dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                        dataUtil.zzzLogIt(e, "CJLGet");
                    }
                }
            } else {
                createInvoice();
            }
        }

    }


    private void createInvoice() {
        hideKeyboard();


        if (getLocation()) {
            ContactInfo contactInfo = getSelectedContact();

            String orderDueDate = DateUtil.toStringFormat_7(new Date());
            String orderDueAt = DateUtil.toStringFormat_13(new Date());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serviceusedid", 2387);
                jsonObject.put("promoid", 76);

                jsonObject.put("orderdueat", orderDueAt);
                jsonObject.put("industryID", 0);

                jsonObject.put("nickid", "0");

                jsonObject.put("totship", "0");
                jsonObject.put("totlabor", "0");

                jsonObject.put("orname", "");
                jsonObject.put("oraddr", "");
                jsonObject.put("orph", "");
                jsonObject.put("delname", "");
                jsonObject.put("deladdr", "");
                jsonObject.put("delzip", "");
                jsonObject.put("delph", "");
                jsonObject.put("deldir", "");

                jsonObject.put("sellerid", appSettings.getUserId());
                jsonObject.put("buyerid", contactInfo.getMlid());

                jsonObject.put("email", contactInfo.getEmail());
                /*if (!isEmailValid(contactInfo.getEmail()) && !TextUtils.isEmpty(contactInfo.getCo())) {
                    jsonObject.put("email", contactInfo.getwEmail());
                } else {

                }*/

                //jsonObject.put("PaidWith", "IC");

                JSONArray menuItemsArray = new JSONArray();

                double fTotalPrice = 0;
                for (int i = 0; i < mInvoiceItems.size(); i++) {
                    AddInvoiceItem item = mInvoiceItems.get(i);

                    if (item.getQty() != 0 && item.getAmt() != 0.0 && !item.getDesc().isEmpty()) {
                        JSONObject itemObj = new JSONObject();
                        itemObj.put("prodid", 0);
                        itemObj.put("name", "");
                        itemObj.put("des", mInvoiceItems.get(i).getDesc());
                        itemObj.put("price", mInvoiceItems.get(i).getAmt());
                        itemObj.put("size", 1);
                        itemObj.put("quantity", mInvoiceItems.get(i).getQty());
                        itemObj.put("oz", "0");
                        itemObj.put("gram", "0");

                        fTotalPrice += mInvoiceItems.get(i).getAmt() * mInvoiceItems.get(i).getQty();
                        menuItemsArray.put(itemObj);
                    }
                }
                jsonObject.put("menus", menuItemsArray);

                jsonObject.put("totprice", fTotalPrice);
                jsonObject.put("tottax", 0);
                jsonObject.put("token", "server will get it");
                jsonObject.put("paynow", false);

                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "AddOrder", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());


                // Try to Creat Transaction
                showProgressDialog();

                RequestQueue queue = Volley.newRequestQueue(mContext);
                String finalOrderDueDate = orderDueDate;
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("AddOrder", response);

                        hideProgressDialog();

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    orderId = jsonObject.getString("OrderID");
                                    if (jsonObject.has("token") && !jsonObject.getString("token").isEmpty()) {
                                        String token = jsonObject.getString("token");
                                        NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                                        tokenList.add(new FCMTokenData(token, FCMTokenData.OS_UNKNOWN));
                                        JSONObject payload = new JSONObject();
                                        payload.put("orderId", orderId);
                                        payload.put("CALID", 0);
                                        payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                        payload.put("SenderID", appSettings.getUserId());
                                        notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Invoice_Sent, payload);
                                    }
                                    showToastMessage(jsonObject.getString("msg"));

                                    getInvoices();
                                    mInvoiceItems.clear();
                                    addInvoiceItemAdapter.notifyDataSetChanged();
                                    layoutCreateInvoice.setVisibility(View.GONE);

                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    if (jsonObject.optInt("OrderID") == -2) {
                                        showAlert("Insufficient Funds");
                                    } else {
                                        showAlert(jsonObject.getString("msg"));
                                    }
                                }
                            } catch (JSONException e) {
                                if (dataUtil != null) {
                                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "AddOrder");
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_invalid_credentials);
                        } else {
                            showAlert("No Connection");
                        }
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "AddOrder");
                }
                e.printStackTrace();
            }
        }
    }

    private void getInvoices() {
        try {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "getInvoices" +
                            "&misc=" + "0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();
                    Log.e("getInvoices", response);
                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                            invoiceList.clear();

                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject dataObj = jsonArray.getJSONObject(i);
                                    InvoiceItem invoiceItem = new InvoiceItem();
                                    invoiceItem.setOrderID(dataObj.getInt("OrderID"));
                                    invoiceItem.setNameDesc(dataObj.getString("NameDesc"));
                                    invoiceItem.setDt(dataObj.getString("dt"));
                                    invoiceItem.setBuyerName(dataObj.optString("BuyerName"));
                                    invoiceItem.setAmt(String.format("$%.2f", dataObj.getDouble("Amt")));
                                    invoiceItem.setToID(dataObj.optString("ToID"));
                                    invoiceItem.setFromID(dataObj.optString("FromID"));
                                    invoiceItem.setStatus(dataObj.getString("Status"));
                                    invoiceList.add(invoiceItem);
                                }
                            }
                            adapterInvoice.notifyDataSetChanged();
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CJLGet");
                            }
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

            stringRequest.setShouldCache(false);
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            if (dataUtil != null) {
                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "CJLGet");
            }
        }
    }

    NotesAdapter notesAdapter;
    ArrayList<Note> notesList;

    private void getNotes() {
        int ldbID = 0;
        int MLID = 0;
        if (isContactSelected()) {
            ContactInfo contactInfo = getSelectedContact();
            ldbID = contactInfo.getId();
            MLID = contactInfo.getMlid();
        }

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "allNotes",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "getNotes" +
                                "&tiedToMLID=" + MLID +
                                "&tiedToLDBID=" + String.valueOf(ldbID) +
                                "&note=" + "";
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                //showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideProgressDialog();

                        Log.e("getNotes", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    //showAlert(jsonObject.getString("msg"));
                                } else {
                                    Gson gson = new Gson();
                                    ArrayList<Note> notes = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Note newItem = gson.fromJson(jsonArray.getString(i), Note.class);
                                        notes.add(newItem);
                                    }

                                    // Sort Notes
                                    Collections.sort(notes, new Comparator<Note>() {
                                        public int compare(Note o1, Note o2) {
                                            Date date1 = DateUtil.parseDataFromFormat20(o1.getCreateDate());
                                            Date date2 = DateUtil.parseDataFromFormat20(o1.getCreateDate());
                                            return date2.compareTo(date1);
                                        }
                                    });


                                    notesList.clear();
                                    notesList.addAll(notes);
                                    notesAdapter.notifyDataSetChanged();

                                    // Make the Notes string
                                /*StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < notes.size(); i++) {
                                    Note newItem = notes.get(i);

                                    String dateText = "<small><b><font color='#BBBBBB'>" + DateUtil.toStringFormat_21(DateUtil.parseDataFromFormat20(newItem.getCreateDate())) + "</font></b></small>";

                                    stringBuilder.append(dateText).append("<br>");
                                    stringBuilder.append(newItem.getNote().replace("\n", "<br>")).append("<br><br>");
                                }
                                tvNotes.setPaintFlags(0);
                                tvNotes.setText(HtmlCompat.fromHtml(stringBuilder.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT));
                                */


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
                        //hideProgressDialog();
                        if (TextUtils.isEmpty(error.getMessage())) {
                            showAlert(R.string.error_server_response);
                        } else {
                            showAlert("No Connection");
                        }
                        //showMessage(error.getMessage());
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "allNotes");
                }
            }
        }
    }

    private void initializeNotesList() {
        notesList = new ArrayList<>();
        rvNotes = findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(mContext));
        notesAdapter = new NotesAdapter(mContext, notesList);
        rvNotes.setAdapter(notesAdapter);
    }

    private void addNotes() {
        int mlid = 0;
        int ldbid = 0;
        if (isContactSelected()) {
            ContactInfo contactInfo = getSelectedContact();
            mlid = contactInfo.getMlid();
            ldbid = contactInfo.getId();
        }

        String notes = edtNotes.getText().toString().trim();

        hideKeyboard(edtNotes);

        if (TextUtils.isEmpty(notes)) {
            showToastMessage("Please input notes");
            return;
        }

        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "allNotes",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "addNote" +
                                "&tiedToMLID=" + mlid +
                                "&tiedToLDBID=" + ldbid +
                                "&note=" + notes.replace("\n", "\\r\\n");
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                //params.put("sellerID", "0");

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("addNote", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert(jsonObject.getString("msg"));
                                } else {
                                    showToastMessage("Successfully added your notes");
                                    edtNotes.setText("");

                                    getNotes();
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
                            showAlert(R.string.error_server_response);
                        } else {
                            showAlert("No Connection");
                        }
                        //showMessage(error.getMessage());
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "allNotes");
                }
            }
        }

    }

    private String getSelectedList() {
        ArrayList<String> selectedIdList = new ArrayList<>();
        for (TaskInfo item : taskList) {
            if (item.isSelected()) {
                selectedIdList.add(item.getID());
            }
        }

        return TextUtils.join(",", selectedIdList);
    }

    private void openMeetSlider() {
        hideKeyboard();
        panelMeetAddSlider.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0f, 0f, panelMeetAddSlider.getHeight(), 0f);
        animate.setDuration(500);
        animate.setFillAfter(true);
        panelMeetAddSlider.startAnimation(animate);
    }

    private void closeMeetSlider() {
        panelMeetAddSlider.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(0f, 0f, 0f, panelMeetAddSlider.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        panelMeetAddSlider.startAnimation(animate);
    }

    private void saveMeeting() {
        closeMeetSlider();

        String eventTitle = tvMeetTitle.getText().toString().trim();
        if (TextUtils.isEmpty(eventTitle)) {
            return;
        }

        int mlid = 0;
        int ldbid = 0;
        if (contactList.size() > 0/* && isContactSelected()*/) {
            ContactInfo contactInfo = getSelectedContact();
            ldbid = contactInfo.getId();
            try {
                mlid = contactInfo.getMlid();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        if (getLocation()) {
            String userLat = getUserLat();
            String userLon = getUserLon();

            try {

                jsonObject.put("promoid", 0);
                jsonObject.put("industryID", 0);
                jsonObject.put("attendeeMLID", mlid);

                jsonObject.put("workID", appSettings.getWorkid());
                jsonObject.put("LDBID", ldbid);

                jsonObject.put("meetingID", 0);
                jsonObject.put("sellerID", 0);
                jsonObject.put("orderID", 0);
                jsonObject.put("mode", 0);
                jsonObject.put("amt", 0);
//                jsonObject.put("TZ", stringToDouble(tz.replace("+", ""))); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("TZ", tz); // PTZ, MTZ, CTZ, ETZ
                jsonObject.put("apptTime", DateUtil.toStringFormat_12(calStart.getTime()));
                jsonObject.put("apptTimeEnd", DateUtil.toStringFormat_12(calEnd.getTime()));
                jsonObject.put("eventTitle", eventTitle);
                jsonObject.put("address", strLocation);
                jsonObject.put("apptLon", 0);
                jsonObject.put("apptLat", 0);
                jsonObject.put("cp", strPhone);
                jsonObject.put("email", strEmail);
                jsonObject.put("mins", 0);
                jsonObject.put("videoMeetingURL", "");
                jsonObject.put("videoMeetingID", "");
                jsonObject.put("videoPascode", "");
                jsonObject.put("videoAutoPhoneDial", "");
                jsonObject.put("miscUN", "");
                jsonObject.put("miscPW", "");
                jsonObject.put("utc", appSettings.getUTC());
                jsonObject.put("qty", 0);
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

                String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                        "setAppt", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                GoogleCertProvider.install(mContext);
                StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("setAppt", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                                    // final String orderId = jsonObject.getString("OrderID");
                                } else if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert("Appt not entered. Please try again.");
                                }
                            } catch (JSONException e) {
                                if (dataUtil != null) {
                                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "setAppt");
                                }
                                e.printStackTrace();
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
                            showAlert("No Connection");
                        }
                    }
                });

                sr.setShouldCache(false);
                queue.add(sr);
            } catch (JSONException e) {
                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                dataUtil.zzzLogIt(e, "setAppt");
                e.printStackTrace();
            }
        }
    }

    private void updateDataAndOpenNewApptID(final boolean updateData, long newApptID) {

        if (updateData) {
            mCalendarDataMonths.clear();
        }

        final String monthString = DateUtil.dateToString(calSelectedDate.getTime(), "yyyy-MM");
        // Already loaded month date, then doesn't reload month date
        if (mCalendarDataMonths.contains(monthString)) {
            updateItemList();
            return;
        }

        // Loade new month date
        Calendar calMonthStart = (Calendar) calSelectedDate.clone();
        calMonthStart.set(Calendar.DAY_OF_MONTH, calMonthStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        Calendar calMonthEnd = (Calendar) calSelectedDate.clone();
        calMonthEnd.set(Calendar.DAY_OF_MONTH, calMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Get From Server
        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CalGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "GetMonth" +
                                "&SetByID=" + appSettings.getWorkid() +
                                "&ApptWithMLID=" + "0" +
                                "&ApptID=" + "0" +
                                "&LocLat=" + "0" +
                                "&LocLon=" + "0" +
                                "&DateStart=" + DateUtil.toStringFormat_12(calMonthStart.getTime()) +
                                "&DateEnd=" + DateUtil.toStringFormat_12(calMonthEnd.getTime());
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                GoogleCertProvider.install(mContext);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideProgressDialog();

                        Log.e("GetMonth", "onSuccess: updateDataAndOpenNewApptID: " + response);
                        try {

                            JSONArray responseArray = new JSONArray(response);

                            JSONObject responseObject = responseArray.getJSONObject(0);
                            if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                //showAlert(responseObject.getString("msg"));
                            } else {
                                if (updateData) {
                                    mCalendarDataList.clear();
                                }
                                Gson gson = new Gson();
                                for (int i = 0; i < responseArray.length(); i++) {
                                    CalendarData.Data newItem = gson.fromJson(responseArray.getString(i), CalendarData.Data.class);

                                    if (!mCalendarDataList.contains(newItem)) {
                                        mCalendarDataList.add(newItem);
                                    }
                                }

                                CalendarData.setCalendarDataList(mCalendarDataList);

                                updateItemList();

                                mCalendarDataMonths.add(monthString);

                                // TODO: Open new appt

                                Intent intent = new Intent(mContext, ViewMeetingActivity.class);

                                for (CalendarData.Data calendarData : CalendarData.getCalendarDataList()) {
                                    if (calendarData.getCalId() == newApptID) {
                                        String data = new Gson().toJson(calendarData);
                                        intent.putExtra("appt_data", data);
                                        ContactInfo contactInfo = dm.getContactInfoById(calendarData.getLdbID());
                                        intent.putExtra("contact", contactInfo);
                                        startActivityForResult(intent, REQUEST_MEET);
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CalGet");
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkErrorHandle(mContext, error);
                        //hideProgressDialog();
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CalGet");
                }
            }
        }
    }

    private void updateAttendeeMLID(long calID, int ldbid) {

        mCalendarDataMonths.clear();

        final String monthString = DateUtil.dateToString(calSelectedDate.getTime(), "yyyy-MM");
        // Already loaded month date, then doesn't reload month date
        if (mCalendarDataMonths.contains(monthString)) {
            updateItemList();
            return;
        }

        // Loade new month date
        Calendar calMonthStart = (Calendar) calSelectedDate.clone();
        calMonthStart.set(Calendar.DAY_OF_MONTH, calMonthStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        Calendar calMonthEnd = (Calendar) calSelectedDate.clone();
        calMonthEnd.set(Calendar.DAY_OF_MONTH, calMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Get From Server
        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CalGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "GetMonth" +
                                "&SetByID=" + appSettings.getWorkid() +
                                "&ApptWithMLID=" + "0" +
                                "&ApptID=" + "0" +
                                "&LocLat=" + "0" +
                                "&LocLon=" + "0" +
                                "&DateStart=" + DateUtil.toStringFormat_12(calMonthStart.getTime()) +
                                "&DateEnd=" + DateUtil.toStringFormat_12(calMonthEnd.getTime());
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                GoogleCertProvider.install(mContext);
                final long calendarID = calID;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideProgressDialog();

                        Log.e("GetMonth", "onSuccess: updateAttendeeMLID: " + response);
                        try {

                            JSONArray responseArray = new JSONArray(response);

                            JSONObject responseObject = responseArray.getJSONObject(0);
                            if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                //showAlert(responseObject.getString("msg"));
                            } else {
                                mCalendarDataList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < responseArray.length(); i++) {
                                    CalendarData.Data newItem = gson.fromJson(responseArray.getString(i), CalendarData.Data.class);
                                    if (newItem.getCalId() == calID) {
                                        newItem.setAttendeeMLID(-1);
                                    }
                                    if (!mCalendarDataList.contains(newItem)) {
                                        mCalendarDataList.add(newItem);
                                    }
                                }

                                CalendarData.setCalendarDataList(mCalendarDataList);
                                updateItemList();
                                mCalendarDataMonths.add(monthString);


                                Intent intent = new Intent(mContext, ViewMeetingActivity.class);

                                for (CalendarData.Data calendarData : CalendarData.getCalendarDataList()) {
                                    if (calendarData.getCalId() == calID) {
                                        String data = new Gson().toJson(calendarData);
                                        intent.putExtra("appt_data", data);
                                        ContactInfo contactInfo = dm.getContactInfoById(ldbid);
                                        intent.putExtra("contact", contactInfo);
                                        startActivityForResult(intent, REQUEST_MEET);
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CalGet");
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkErrorHandle(mContext, error);
                        //hideProgressDialog();
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CalGet");
                }
            }
        }
    }

    private void getApptData(final boolean updateData) {

        if (updateData) {
            mCalendarDataMonths.clear();
        }

        final String monthString = DateUtil.dateToString(calSelectedDate.getTime(), "yyyy-MM");
        // Already loaded month date, then doesn't reload month date
        if (mCalendarDataMonths.contains(monthString)) {
            updateItemList();
            return;
        }

        // Loade new month date
        Calendar calMonthStart = (Calendar) calSelectedDate.clone();
        calMonthStart.set(Calendar.DAY_OF_MONTH, calMonthStart.getActualMinimum(Calendar.DAY_OF_MONTH));

        Calendar calMonthEnd = (Calendar) calSelectedDate.clone();
        calMonthEnd.set(Calendar.DAY_OF_MONTH, calMonthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Get From Server
        if (getLocation()) {
            try {
                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(this,
                        "CalGet",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&mode=" + "GetMonth" +
                                "&SetByID=" + appSettings.getWorkid() +
                                "&ApptWithMLID=" + "0" +
                                "&ApptID=" + "0" +
                                "&LocLat=" + "0" +
                                "&LocLon=" + "0" +
                                "&DateStart=" + DateUtil.toStringFormat_12(calMonthStart.getTime()) +
                                "&DateEnd=" + DateUtil.toStringFormat_12(calMonthEnd.getTime());
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                GoogleCertProvider.install(mContext);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideProgressDialog();

                        Log.e("GetMonth", "onSuccess: getApptData: " + response);
                        try {

                            JSONArray responseArray = new JSONArray(response);

                            JSONObject responseObject = responseArray.getJSONObject(0);
                            if (responseObject.has("status") && !responseObject.getBoolean("status")) {
                                //showAlert(responseObject.getString("msg"));
                            } else {
                                if (updateData) {
                                    mCalendarDataList.clear();
                                }
                                Gson gson = new Gson();
                                for (int i = 0; i < responseArray.length(); i++) {
                                    CalendarData.Data newItem = gson.fromJson(responseArray.getString(i), CalendarData.Data.class);

                                    if (!mCalendarDataList.contains(newItem)) {
                                        mCalendarDataList.add(newItem);
                                    }
                                }

                                CalendarData.setCalendarDataList(mCalendarDataList);

                                updateItemList();

                                mCalendarDataMonths.add(monthString);
                            }
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CalGet");
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkErrorHandle(mContext, error);
                        //hideProgressDialog();
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "CalGet");
                }
            }
        }
    }

    private void updateItemList() {
        //[{"CALID":1,"CALOrderID":0,"CALAmt":0.00,"CALSetByID":1434741,"Title":"Good ","ApptStartTime":"2021-08-26T21:00:00","ApptEndTime":"2021-08-26T21:30:00","apptLat":0.0000000000,"apptLon":0.0000000000,"address":"","meetingID":0,"noteID":0,"BuyerID":0,"Co":"","DetailID":0,"DetailStatusID":0,"Qty":0,"Name":"","Price":0.00,"TotPrice":0.00},{"CALID":4,"CALOrderID":0,"CALAmt":0.00,"CALSetByID":1434741,"Title":"Meet for work ","ApptStartTime":"2021-08-27T07:00:00","ApptEndTime":"2021-08-27T07:30:00","apptLat":0.0000000000,"apptLon":0.0000000000,"address":"Df","meetingID":0,"noteID":0,"BuyerID":0,"Co":"","DetailID":0,"DetailStatusID":0,"Qty":0,"Name":"","Price":0.00,"TotPrice":0.00}]

        Map<String, ArrayList<CalendarData.Data>> mapData = new HashMap<>();
        for (CalendarData.Data item : mCalendarDataList) {
            Date dateItem = DateUtil.parseDataFromFormat12(item.getStartDate().replace("T", " "));
            Calendar calItem = Calendar.getInstance();
            calItem.setTime(dateItem);

            if (isSameDay(calSelectedDate, calItem)) {
                String hourInfo = DateUtil.toStringFormat_30(dateItem).toUpperCase();

                ArrayList<CalendarData.Data> hourItems = mapData.get(hourInfo);

                if (hourItems == null) {
                    hourItems = new ArrayList<>();
                    mapData.put(hourInfo, hourItems);
                }

                hourItems.add(item);
            }
        }

        calAdapter.notifyData(mapData);
    }

    private void updateAgendaItem(boolean filter) {
        int mlid = -1;
//        if (isContactSelected()) {
        ContactInfo contactInfo = getSelectedContact();
        if (contactInfo != null) {
            mlid = contactInfo.getMlid();
        }
//        }

        // Filter Data here with keyword
        String keyword = edtAgendaSearch.getText().toString().trim().toLowerCase();

        mAgendaDataList.clear();

        // Show debug message
        //String debugMsg = String.format("Filter %s, mlid=%s, ldbID=%d", filter ? "ON":"OFF", mlid, ldbID);
        //showToastMessage(debugMsg);

        if (filter) {
            int mlidVal = 0;
            try {
                mlidVal = mlid;
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < mCalendarDataList.size(); i++) {
                CalendarData.Data calData = mCalendarDataList.get(i);
                //if (TextUtils.isEmpty(keyword) || contactInfo.getTitle().toLowerCase().contains(keyword)) {
//                if (/*TextUtils.isEmpty(mlid) || *//*mlid.equals(contactInfo.getAttendeeMLID()) || */ldbID == contactInfo.getLdbID() || (mlidVal > 0 && mlid == contactInfo.getAttendeeMLID())) {
                if (mlidVal == calData.getSellerId()) {
                    mAgendaDataList.add(calData);
                }
            }
        } else {
            mAgendaDataList.addAll(mCalendarDataList);
        }

        calAgendaAdapter.notifyData(mAgendaDataList);
    }

    private void removeCalendarData(CalendarData.Data calData) {
        if (getLocation()) {
            showProgressDialog();

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CalGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "delete" +
                            "&misc=" + "" +
                            "&ApptID=" + calData.getCalId() +
                            "&SetByID=" + "0" +
                            "&ApptWithMLID=" + "0" +
                            "&LocLat=" + "0" +
                            "&loclon=" + "0" +
                            "&DateStart=" + "1-1-2021" +
                            "&DateEnd=" + "1-1-2021";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            GoogleCertProvider.install(mContext);
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("delete", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {

                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                AlarmMeetDataManager.getInstance(mContext).removeAlarm(mContext, Long.parseLong(calData.getMeetingId()));
                                getApptData(true);
                            }
                        } catch (JSONException e) {
                            if (dataUtil != null) {
                                dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                dataUtil.zzzLogIt(e, "CalGet");
                            }
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    networkErrorHandle(mContext, error);
                    hideProgressDialog();
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


    String[] permissionsForContact = new String[]{Manifest.permission.READ_CALL_LOG};
    static final int PERMISSION_REQUEST_CONTACT = 100;
    static final int PERMISSION_REQUEST_CAMERA = 101;

    private void getCallHistory() {

        if (checkPermissions(mContext, permissionsForContact, true, PERMISSION_REQUEST_CONTACT)) {
            //this help you to get recent call
            Uri contacts = CallLog.Calls.CONTENT_URI;
            Cursor managedCursor = mContext.getContentResolver().query(contacts, null, null,
                    null, android.provider.CallLog.Calls.DATE + " DESC");

            while (managedCursor.moveToNext()) {
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

                StringBuffer sb = new StringBuffer();
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);    // https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE
                String callDate = managedCursor.getString(date);    // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                int callDuration = managedCursor.getInt(duration);
                managedCursor.close();

                int dircode = Integer.parseInt(callType);
                sb.append("Phone Number:--- " + phNumber + " ,Call Date:--- " + callDayTime + " ,Call duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
                Log.d("calllogs", "getLastNumber: " + "Phone Number:--- " + phNumber + " ,Call Date:--- " + callDayTime + " ,Call duration in sec :--- " + callDuration);
            }

            managedCursor.close();
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
            if (requestCode == PERMISSION_REQUEST_CONTACT) {
                //
            }
        } else {
            showToastMessage(R.string.error_permission);
        }
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openCamera();
                goToVideoCallActivity();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showSearchResults() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    spinnerContact2.setText("");
                }
            }, 1000);
        } catch (Exception e) {
            // Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clickOnItem(HistoryData data) {
        String address = tvContactAddress.getText().toString().trim();
        // String address = data.getStreetAddress() + " " +  data.getZip() + " " + data.getCity() + " " + data.getState();
        //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%s&mode=d", address));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void clickOnItem(ContactInfo contactInfo, int inAppOption, int wLocation) {
        if (inAppOption == 0) {
            if (!getLocation()) {
                showAlert("Warning!\n\nLocation must be turned on to continue");
                return;
            }

            /*new SearchCityStateZipFromLatLngHelper(ConnectionActivity.this, getUserLat(), getUserLon(), new SearchCityStateZipFromLatLngHelper.SearchCityStateZipCallback() {
                @Override
                public void onFailed(String message) {
                    showToastMessage(message);
                }

                @Override
                public void onSuccess(String formattedAddress, String city, String state, String zip) {

                }
            }).execute();*/
            // try to get elevation in metric, int elevation
            // for now set userMsg hardcode;
            try {
                String userMsg = "help,I am stuck & arm broke";
                userMsg = userMsg.replace("&", "|||");

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                        "emergency",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&elevation=" + "0" +
                                "&temp=" + appSettings.getTemperatureLastValue() +
                                "&status=" + "0" +
                                "&FN=" + appSettings.getFN() +
                                "&LN=" + appSettings.getLN() +
                                "&currCity=" + "" +
                                "&currSt=" + "" +
                                "&currZip=" + "" +
                                "&currAddr=" + "" +
                                "&CP=" + appSettings.getCP() +
                                "&timeOnPhone=" + DateUtil.toStringFormat_12(new Date()) +
                                "&userMsg=" + "" +
                                "&weather=" + appSettings.getTemperatureLastString().replace("&", "|||");
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                GoogleCertProvider.install(mContext);
                RequestQueue queue = Volley.newRequestQueue(mContext);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("emergency", response);

                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                    showAlert("Be aware you are on your own,\nthe message did not go through.");
                                    toolbar.setBackgroundColor(Color.parseColor("#FF000000"));
                                } else {
                                    showAlert(jsonObject.getString("msg"));
                                    toolbar.setBackgroundColor(Color.parseColor("#FFFF0000"));
                                }
                            } catch (JSONException e) {
                                if (dataUtil != null) {
                                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                                    dataUtil.zzzLogIt(e, "emergency");
                                }
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        networkErrorHandle(mContext, error);
                        hideProgressDialog();
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
            } catch (Exception e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "emergency");
                }
            }
        } else if (inAppOption == 1) {              // Send Via Email
            String email = contactInfo.getEmail();
            if (isValidEmail(email)) {
                if (wLocation == MY_LOCATION) {
                    if (getLocation()) {
                        sendLocationViaEmail(contactInfo, getUserLat(), getUserLon());
                    } else {
                        showToastMessage("Please allow GPS to share your location.");
                    }
                } else { // to map location
                    if (googleMap != null) {
                        String lat = String.valueOf(googleMap.getCameraPosition().target.latitude);
                        String lnt = String.valueOf(googleMap.getCameraPosition().target.longitude);
                        sendLocationViaEmail(contactInfo, lat, lnt);
                    }
                }
            } else {
                showToastMessage("Email is invalid!");
            }
        } else if (inAppOption == 2) {              // Send Via InApp
            if (wLocation == MY_LOCATION) {
                if (getLocation()) {
                    shareInApp(contactInfo, getUserLat(), getUserLon());
                } else {
                    showToastMessage("Please allow GPS to share your location.");
                }
            } else { // to map location
                if (googleMap != null) {
                    String lat = String.valueOf(googleMap.getCameraPosition().target.latitude);
                    String lnt = String.valueOf(googleMap.getCameraPosition().target.longitude);
                    shareInApp(contactInfo, lat, lnt);
                }
            }
        }
    }

    private void sendLocationViaEmail(ContactInfo contactInfo, String lat, String lon) {
        String email = contactInfo.getEmail();

        String mapLocation = "https://www.google.com/maps/@?api=1&map_action=map&center=" + lat + "%2C" + lon + "&zoom=15&basemap=roadmap";

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent == null) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            if (!TextUtils.isEmpty(email)) {
                String[] supportTeamAddrs = {email};
                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
            }
            intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
            intent.putExtra(Intent.EXTRA_TEXT, mapLocation);
            try {
                /*if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    showToastMessage("Please install Email App to use function");
                }*/
                startActivity(intent);
            } catch (Exception e) {
                showToastMessage("Please install Email App to use function");
            }
        } else {
            intent = new Intent(Intent.ACTION_SEND);
            if (!TextUtils.isEmpty(email)) {
                String[] supportTeamAddrs = {email};
                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
            }

            intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
            intent.putExtra(Intent.EXTRA_TEXT, mapLocation);
            //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send Location Mail"));
        }

    }

    private void shareInApp(ContactInfo contactInfo, String lat, String lon) {
        int mlid = contactInfo.getMlid();
        if (mlid != 0) {
            try {
                JSONObject payloadsData = new JSONObject();
                payloadsData.put("lat", lat); /*"41.806928"*/
                payloadsData.put("lon", lon); /*"123.384994"*/
                payloadsData.put("zoom", googleMap.getCameraPosition().zoom);
                NotificationHelper notificationHelper = new NotificationHelper(mlid, mContext, (BaseActivity) mContext);
                notificationHelper.getToken(PayloadType.PT_Share_Location, payloadsData, new OnGetTokenListener() {
                    @Override
                    public void onSuccess(String response) {
                    }

                    @Override
                    public void onVolleyError(VolleyError error) {
                    }

                    @Override
                    public void onEmptyResponse() {
                    }

                    @Override
                    public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
                        // Do nothing
                    }

                    @Override
                    public void onJsonArrayEmpty() {
                        sendLocationViaEmail(contactInfo, lat, lon);
                    }

                    @Override
                    public void onJsonException() {
                    }

                    @Override
                    public void onTokenListEmpty() {
                        sendLocationViaEmail(contactInfo, lat, lon);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            sendLocationViaEmail(contactInfo, lat, lon);
        }
    }


    public void showClearIcon() {
        runOnUiThread(() -> {
            spinnerContact2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_resized, 0);
            spinnerContact2.setOnTouchListener(new RightDrawableOnTouchListener(spinnerContact2) {
                @Override
                public boolean onDrawableTouch(MotionEvent event) {
                    spinnerContact2.setText("");
                    contactInfoSelected = null;
                    getNotes();
                    groupAdapter.setPriValue(-100);
                    edtPhoneNumber.setText("");
                    updateCallHistory();
                    tvContactAddress.setEnabled(true);
                    hideKeyboard(spinnerContact2);
                    getMessagesFromLocalDb(0, true);

                    if (spinnerContact2.getText().toString().trim().length() == 0) {
                        ((TextView) findViewById(R.id.txtMLID)).setText("MLID: 0");
                    }

                    return true;
                }
            });
        });
    }

    private void retrieveCateData() {
        String cateArrayStr = appSettings.getCategoryListData();
        if (!cateArrayStr.equals("") || !cateArrayStr.isEmpty()) {
            try {
                Log.e(TAG, "retrieveCateData: " + new Gson().toJson(cateArrayStr));
                JSONArray cateArray = new JSONArray(cateArrayStr);
                for (int i = 0; i < cateArray.length(); i++) {
                    JSONObject catObj = cateArray.getJSONObject(i);
                    JSONArray itemArray = catObj.optJSONArray("items");

                    StoreCate storeCate = new StoreCate();
                    storeCate.setDescription(catObj.getString("name"));
                    storeCate.setRemoved(catObj.getBoolean("removed"));

                    if (itemArray != null && itemArray.length() > 0) {

                        for (int j = 0; j < itemArray.length(); j++) {
                            JSONObject itemObj = itemArray.getJSONObject(j);

                            StoreItem storeItem = new StoreItem();
                            storeItem.setDescription(itemObj.getString("name"));
                            storeItem.setRemoved(itemObj.getBoolean("removed"));

                            storeCate.addNewItem(storeItem);
                        }
                    }
                    storeCateList.add(storeCate);
                }
            } catch (JSONException e) {
                if (dataUtil != null) {
                    dataUtil.setActivityName(ConnectionActivity.class.getSimpleName());
                    dataUtil.zzzLogIt(e, "retrieveCatchData-aaaa");
                }
                e.printStackTrace();
            }
        }
    }

    private void saveCateData() {
        try {
            JSONArray cateArray = new JSONArray();
            for (int i = 0; i < storeCateList.size(); i++) {
                JSONObject catObj = new JSONObject();
                JSONArray itemArray = new JSONArray();

                StoreCate storeCate = storeCateList.get(i);
                catObj.put("name", storeCate.getDescription());
                catObj.put("removed", storeCate.isRemoved());

                ArrayList<StoreItem> storeItems = storeCate.getItems();

                if (storeItems != null && storeItems.size() > 0) {

                    for (int j = 0; j < storeItems.size(); j++) {
                        StoreItem storeItem = storeItems.get(j);

                        JSONObject itemObj = new JSONObject();
                        itemObj.put("name", storeItem.getDescription());
                        itemObj.put("removed", storeItem.isRemoved());

                        itemArray.put(itemObj);
                    }
                }
                catObj.put("items", itemArray);

                cateArray.put(catObj);
            }

            appSettings.setCategoryListData(cateArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
