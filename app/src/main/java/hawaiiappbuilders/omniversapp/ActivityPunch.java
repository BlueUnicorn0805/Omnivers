package hawaiiappbuilders.omniversapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.CountDownBroadcastService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ActivityPunch extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ActivityPunch.class.getSimpleName();

    private AppSettings appSettings;

    private TextView punch_in_button;
    private TextView punch_lunch_button;
    private TextView punch_out_button;
    private View punchInView;
    private View punchOutView;
    private RecyclerView punch_rcv;
    private LinearLayout shift_infoLL;
    private TextView in_indicator;
    private TextView out_indicator;
    private TextView punch_in_time;

    private TextView emp_greet;
    private TextView emp_name;
    private TextClock emp_punch_in_time;
    private TextView emp_remaining_time;

    private LinearLayout show_times;
    private TextView show_times_in;
    private TextView show_times_lunch;
    private TextView show_times_out;

    private View layout_time_shift_list;
    private View layout_in_time_list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        appSettings = new AppSettings(this);

        initView();
        setValues();
    }

    private void initView() {
        punch_in_button = (TextView) findViewById(R.id.emp_punch_in_button);
        punch_lunch_button = (TextView) findViewById(R.id.punch_lunch_button);
        punch_out_button = (TextView) findViewById(R.id.punch_out_button);
        punchInView = (View) findViewById(R.id.punch_in);
        punchOutView = (View) findViewById(R.id.punch_out);
        punch_rcv = (RecyclerView) findViewById(R.id.punch_rcv);
        shift_infoLL = (LinearLayout) findViewById(R.id.shift_infoLL);
        in_indicator = (TextView) findViewById(R.id.in_indicator);
        out_indicator = (TextView) findViewById(R.id.out_indicator);
        punch_in_time = (TextView) findViewById(R.id.punch_in_time);

        show_times = (LinearLayout) findViewById(R.id.show_times);
        show_times_in = (TextView) findViewById(R.id.show_times_in);
        show_times_lunch = (TextView) findViewById(R.id.show_times_lunch);
        show_times_out = (TextView) findViewById(R.id.show_times_out);


        emp_greet = (TextView) findViewById(R.id.emp_greet);
        emp_name = (TextView) findViewById(R.id.emp_name);
        emp_punch_in_time = (TextClock) findViewById(R.id.emp_punch_in_time);

        layout_time_shift_list = (View) findViewById(R.id.layout_time_shift_list);
        layout_in_time_list = (View) findViewById(R.id.layout_in_time_list);

        emp_remaining_time = (TextView) findViewById(R.id.emp_remaining_time);
        emp_remaining_time.setText("00:00:00");
        if (appSettings.isClockedIn()) {
            punchInView.setVisibility(View.INVISIBLE);
            punchOutView.setVisibility(View.VISIBLE);
            punch_rcv.setVisibility(View.GONE);
            layout_time_shift_list.setVisibility(View.GONE);
            in_indicator.setVisibility(View.VISIBLE);
            out_indicator.setVisibility(View.INVISIBLE);

            layout_in_time_list.setVisibility(View.VISIBLE);

            if (!appSettings.isLunchTimeOver()) {
                startCountDownTimer();
            }
        } else {
            punchInView.setVisibility(View.VISIBLE);
            punchOutView.setVisibility(View.INVISIBLE);
            punch_rcv.setVisibility(View.GONE);
            layout_time_shift_list.setVisibility(View.VISIBLE);
            in_indicator.setVisibility(View.INVISIBLE);
            out_indicator.setVisibility(View.VISIBLE);

            layout_in_time_list.setVisibility(View.GONE);
        }

        if (appSettings.isHavingLunch()) {
            emp_remaining_time.setText("00:00:00");
        } else {
            emp_remaining_time.setText("00:30:00");
        }


    }

    private void setValues() {
        punch_in_time.setText(appSettings.getInTime());
        show_times_in.setText(appSettings.getInTime());
        show_times_lunch.setText(appSettings.getLunchInTime());
        show_times_out.setText(appSettings.getLunchEndTime());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emp_punch_in_button:
                punchInView.setVisibility(View.INVISIBLE);
                punchOutView.setVisibility(View.VISIBLE);

                punch_rcv.setVisibility(View.VISIBLE);
                layout_time_shift_list.setVisibility(View.GONE);

                in_indicator.setVisibility(View.VISIBLE);
                out_indicator.setVisibility(View.INVISIBLE);

                layout_in_time_list.setVisibility(View.VISIBLE);

                appSettings.setClockedIn();
                appSettings.setInTime(getTime());
                setValues();
                break;
            case R.id.punch_lunch_button:
                punchInView.setVisibility(View.INVISIBLE);
                punchOutView.setVisibility(View.VISIBLE);
                startCountDownTimer();
                appSettings.setLunchInTime(getTime());
                appSettings.setLunchEndTime(getLunchEndTime());
                appSettings.setIsHavingLunch();
                appSettings.setLunchTimeOver(false);
                setValues();
                break;
            case R.id.punch_out_button:
                punchInView.setVisibility(View.VISIBLE);
                punchOutView.setVisibility(View.INVISIBLE);

                punch_rcv.setVisibility(View.GONE);
                layout_time_shift_list.setVisibility(View.VISIBLE);

                in_indicator.setVisibility(View.INVISIBLE);
                out_indicator.setVisibility(View.VISIBLE);
                layout_in_time_list.setVisibility(View.GONE);

                appSettings.setClockedOut();
                appSettings.resetRemainingTime();
                appSettings.setCompletedHavingLunch();


                appSettings.setLunchInTime("12:00 PM");
                appSettings.setLunchEndTime("12:45 PM");

                stopCountDownTimer();
                break;
            case R.id.ftf_cancel:
                finish();
                break;
        }
    }

    private String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        return df.format(cal.getTime());
    }

    private String getLunchEndTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

        cal.add(Calendar.MINUTE,45);

        return df.format(cal.getTime());
    }

    private String getTime(long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);

        return hms;
    }

    private void startCountDownTimer() {
        if (appSettings.isClockedIn()) {
            startService(new Intent(this, CountDownBroadcastService.class));
        }
    }

    private void stopCountDownTimer() {
        appSettings.setClockedOut();
        stopService(new Intent(this, CountDownBroadcastService.class));
        emp_remaining_time.setText("00:30:00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(CountDownBroadcastService.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            boolean is_complete = intent.getBooleanExtra("is_complete",false);

            if (is_complete) {
                stopService(new Intent(this, CountDownBroadcastService.class));
                emp_remaining_time.setText("00:00:00");
                appSettings.setLunchTimeOver(true);
            } else {
                emp_remaining_time.setText(""+getTime(millisUntilFinished));
            }
        }
    }
}
