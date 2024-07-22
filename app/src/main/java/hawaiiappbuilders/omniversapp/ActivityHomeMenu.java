package hawaiiappbuilders.omniversapp;

import static hawaiiappbuilders.omniversapp.ActivityPayCart.MODE_PAY_SEND;
import static hawaiiappbuilders.omniversapp.messaging.AppFirebaseMessagingService.UPDATE_STATUS_ID;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_INCOMING_VIDEO_CALL;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.orders.OrderStatus;
import hawaiiappbuilders.omniversapp.services.RestartService;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.videocall.IncomingVideoCallActivity;

public class ActivityHomeMenu extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityHomeMenu.class.getSimpleName();
    TextView tvTemperature;
    Intent mServiceIntent;

    ImageView ivProgress;

    Context mContext;
    AppSettings appSettings;

    TextView textOrderStatus;

    public static int activityStatus = -1; // if it's < 0, this activity is destroyed or not created

    BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // If app receives status ID
            if (intent.getAction().contentEquals("receivedstatusid")) {
                int statusId = intent.getExtras().getInt("statusID");
                updateStatusId(statusId, false);
            }
        }
    };


    @Override
    protected void onBroadcastReceived(Intent intent) {
        // super.onBroadcastReceived(intent);
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

    @Override
    protected void onPause() { // page is created, but not visible
        super.onPause();
        activityStatus = 1;
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        Bundle bundle = new Bundle();
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "firebase intent payload "+getIntent().getExtras().keySet());
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.e(TAG, "onCreate: " + key + "  value " + value);
//                // Handle the data here (e.g., save it to a local database)
                if (value instanceof String) {
                    if (key.contentEquals("payloadtype") || key.contentEquals("SenderID")) {
                        bundle.putInt(key, Integer.parseInt(String.valueOf(value)));
                    } else {
                        bundle.putString(key, (String) value);
                    }
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Double) {
                    bundle.putDouble(key, (Double) value);
                } else if (value instanceof Float) {
                    bundle.putFloat(key, (Float) value);
                } else if (value instanceof Long) {
                    bundle.putLong(key, (Long) value);
                }
            }
            if (bundle.getInt("payloadtype") == PT_INCOMING_VIDEO_CALL) {
                Intent intent = new Intent(this, IncomingVideoCallActivity.class);
                intent.putExtra(Constants.FIRST_NAME, bundle.getString(Constants.FIRST_NAME));
                intent.putExtra(Constants.LAST_NAME, bundle.getString(Constants.LAST_NAME));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if (getIntent().getExtras().getString(Constants.CAll_ID) != null) {
                    intent.putExtra(Constants.CAll_ID, bundle.getString(Constants.CAll_ID));
                }
                startActivity(intent);
            }
        }
        mContext = this;
        activityStatus = 2;
        appSettings = new AppSettings(mContext);
        ivProgress = findViewById(R.id.ivProgress);
        ivProgress.setImageResource(R.drawable.statusbar0);
        textOrderStatus = findViewById(R.id.text_order_status);
        textOrderStatus.setText("");
        initViews();
        try {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(msgReceiver, new IntentFilter("receivedstatusid"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize last saved status id
        // Check today's date
        String today = DateUtil.dateToString(new Date(Calendar.getInstance().getTimeInMillis()), DateUtil.DATE_FORMAT_22);
        if (!appSettings.getCurrStatusIdDate().contentEquals(today)) {
            ivProgress.setVisibility(View.GONE);
            textOrderStatus.setVisibility(View.GONE);
            // updateStatusId(JustOrdered.statusId, true);
        } else {
            textOrderStatus.setVisibility(View.GONE);
            updateStatusId(appSettings.getCurrStatusId(), true);
        }

        /*CustomFCMListener fcmListener = new CustomFCMListener();
        mServiceIntent = new Intent(this, fcmListener.getClass());
        if (!isMyServiceRunning(fcmListener.getClass())) {
            startService(mServiceIntent);
        }*/


        // should only be called once activity is created
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().contentEquals("receivedstatusidfromotherpage")) {
                int statusId = getIntent().getExtras().getInt("statusID");
                updateStatusId(statusId, false);
            }
        }
    }

    private void updateStatusId(int statusId, boolean isInitializing) {
        if (statusId > 2000) {
            ivProgress.setVisibility(View.VISIBLE);
            textOrderStatus.setVisibility(View.VISIBLE);
            OrderStatus orderStatus = OrderStatus.getOrderStatusEnum(statusId);
            if (orderStatus != null) {
                if (!isInitializing) {
                    playChinChin(statusId);
                }
                appSettings.setCurrStatusId(statusId);

                String timeReceived = DateUtil.dateToString(new Date(Calendar.getInstance().getTimeInMillis()), DateUtil.DATE_FORMAT_22);
                appSettings.setCurrStatusIdDate(timeReceived);

                textOrderStatus.setText(orderStatus.getOrderStatus());
                ivProgress.setImageResource(orderStatus.imageResource);
            } else {
                textOrderStatus.setText("");
                ivProgress.setImageResource(R.drawable.statusbar0);
            }
        } else {
            textOrderStatus.setText("");
            ivProgress.setImageResource(R.drawable.statusbar0);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    private void initViews() {

        tvTemperature = findViewById(R.id.tvTemperature);

        //findViewById(R.id.btnQRCode).setOnClickListener(this);

        findViewById(R.id.btnVault).setOnClickListener(this);

        findViewById(R.id.btnProfile).setOnClickListener(this);
        findViewById(R.id.btnShop).setOnClickListener(this);
        findViewById(R.id.btnCommunity).setOnClickListener(this);

        findViewById(R.id.btnCamera).setOnClickListener(this);
        findViewById(R.id.btnMyCar).setOnClickListener(this);
        findViewById(R.id.btnShipReceive).setOnClickListener(this);

        findViewById(R.id.btnJobs).setOnClickListener(this);
        findViewById(R.id.btnEvents).setOnClickListener(this);

        // Bottom Navigation Action
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_paysend:
                                /*Intent intentSend = new Intent(mContext, ConnectionActivity.class);
                                intentSend.putExtra("page", ConnectionActivity.PANEL_PAY_SEND);
                                startActivity(intentSend);*/
                                Intent intent = new Intent(mContext, ActivityPayCart.class);
                                intent.putExtra("mode", MODE_PAY_SEND);
                                startActivity(intent);
                                break;
                            case R.id.menu_request:
                                /*Intent intentRequest = new Intent(mContext, ConnectionActivity.class);
                                intentRequest.putExtra("page", ConnectionActivity.PANEL_PAY_RECEIVE);
                                startActivity(intentRequest);*/
                                startActivity(new Intent(mContext, ActivityPayRequest.class));
                                break;
                        }
                        return true;
                    }
                });

        findViewById(R.id.btnQRCode).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        activityStatus = -2;

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(msgReceiver);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("RestartService");
        broadcastIntent.setClass(this, RestartService.class);
        // this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityStatus = 2;
        hideKeyboard();

        getTemperature(tvTemperature);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnVault) {
            //startActivity(new Intent(mContext, ActivityMenuMoney.class));
            startActivity(new Intent(mContext, ActivityMenuVault.class));
        } else if (viewId == R.id.btnQRCode) {
            //startActivity(new Intent(mContext, ActivityAppointmentSetting.class));
            // QR
            startActivity(new Intent(mContext, QRCodeActivity.class));
        } else if (viewId == R.id.btnProfile) {
            startActivity(new Intent(mContext, ActivityMenuProfile.class));
        } else if (viewId == R.id.btnShop) {

            startActivity(new Intent(mContext, ActivityMenuPurchase.class));
        } else if (viewId == R.id.btnCommunity) {
            // Balance
            startActivity(new Intent(mContext, ConnectionActivity.class));
        } else if (viewId == R.id.btnCamera) {
            // Camera
            if (checkPermissions(mContext, PERMISSION_REQUEST_CAMERA_STRING, false, PERMISSION_REQUEST_CODE_CAMERA)) {
                startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            }
        } else if (viewId == R.id.btnMyCar) {
            /*Intent carintent = new Intent(this, MyCarActivity.class);
            startActivity(carintent);*/

            Intent carintent = new Intent(this, ActivityMenuLocation.class);
            startActivity(carintent);
        } else if (viewId == R.id.btnShipReceive) {
            // Money Button
            startActivity(new Intent(mContext, ActivityIFareDashBoard.class));
        } else if (viewId == R.id.btnJobs) {
            startActivity(new Intent(mContext, ActivityMyWork.class));
        } else if (viewId == R.id.btnEvents) {
            // Watch TV
            startActivity(new Intent(this, ActivityHomeEvents.class));
        } else if (viewId == R.id.tabPayReceived) {
            Intent intent = new Intent(mContext, ConnectionActivity.class);
            intent.putExtra("page", ConnectionActivity.PANEL_PAY_RECEIVE);
            startActivity(intent);
        } else if (viewId == R.id.tabPayShare) {
            Intent intent = new Intent(mContext, ConnectionActivity.class);
            intent.putExtra("page", ConnectionActivity.PANEL_PAY_SEND);
            startActivity(intent);
        } else if (viewId == R.id.btnQRCode) {
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

}