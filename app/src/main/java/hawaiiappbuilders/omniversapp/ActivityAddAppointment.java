package hawaiiappbuilders.omniversapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.adapters.AppointmentScheduledTimeAdapter;
import hawaiiappbuilders.omniversapp.adapters.TimedGridViewAdapter;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.MenuItem;
import hawaiiappbuilders.omniversapp.model.Restaurant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityAddAppointment extends BaseActivity implements View.OnClickListener {
    private String TAG = ActivityAddAppointment.class.getSimpleName();
    private static final String DAY_OF_WEEK_PATTERN = "EEE";
    private static final String DAY_OF_MONTH_PATTERN = "dd";
    private static final String MONTH_PATTERN = "MMM";
    private static final String YEAR_PATTERN = "yyyy";

    private GridView mTimesGridView;
    private RecyclerView mScheduledTimeRecyclerView;
    private ImageView previousDateImageView;
    private ImageView nextDateImageView;

    private TextView appointmentSelectedDateTextView;
    private TextView orderDate;

    private TextView storeName;

    String selectedDate = "";

    private List<DateViewHolder> dateViewHolderList = new ArrayList<>();
    private int weekCount = 0;
    private final int weekCountIncrementDecrement = 7;

    Restaurant restaurantInfo;
    ArrayList<MenuItem> menuItemArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_appointment);

        Intent data = getIntent();
        restaurantInfo = data.getParcelableExtra("restaurant");
        menuItemArrayList = data.getParcelableArrayListExtra("itemmenus");

        if (menuItemArrayList == null || menuItemArrayList.isEmpty()) {
            menuItemArrayList = new ArrayList<>();
            menuItemArrayList.add(new MenuItem(1, "30 Minutes", "$20", "30 Minutes. $20", "Starter", false, R.drawable.db_b));
            menuItemArrayList.add(new MenuItem(2, "60 Minutes", "$40", "60 Minutes. $40", "Starter", false, R.drawable.db_b));
            menuItemArrayList.add(new MenuItem(3, "90 Minutes", "$60", "90 Minutes. $60", "Starter", false, R.drawable.db_b));
            menuItemArrayList.add(new MenuItem(4, "120 Minutes", "$80", "120 Minutes. $80", "Starter", false, R.drawable.db_b));
        }

        initViews();

        TextView storeinfo = findViewById(R.id.storeinfo);
        storeinfo.setText(String.format("%d - %s", restaurantInfo.get_id(), restaurantInfo.get_name()));
    }

    private void initViews() {
        mTimesGridView = (GridView) findViewById(R.id.aa_time_grid_layout);
        mScheduledTimeRecyclerView = (RecyclerView) findViewById(R.id.aa_scheduled_time_rv);

        previousDateImageView = (ImageView) findViewById(R.id.aa_date_previous);
        nextDateImageView = (ImageView) findViewById(R.id.aa_date_next);

        storeName = (TextView) findViewById(R.id.storeName);
        storeName.setText(restaurantInfo.get_name());

        appointmentSelectedDateTextView = (TextView) findViewById(R.id.appointment_selected_date);
        orderDate = (TextView) findViewById(R.id.orderDate);

        setUpGridView();
        setUpRecyclerView();
        initDatesItem();
        setDatesItem();
    }

    private void setUpGridView() {
        TimedGridViewAdapter timedGridViewAdapter = new TimedGridViewAdapter(mContext);
        mTimesGridView.setAdapter(timedGridViewAdapter);

        mTimesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get Date & Time
                String timeString = "";
                String AM_PM = "AM";
                int time = 9 + position / 4;
                int mins = (position % 4) * 15;

                if (time >= 12) {
                    AM_PM = "PM";
                    if (time > 12) {
                        time -= 12;
                    }
                }

                timeString = selectedDate + String.format(" %02d:%02d %s", time, mins, AM_PM);

                showConfirmationAlertDialog(timeString);
            }
        });
    }

    private void setUpRecyclerView() {
        mScheduledTimeRecyclerView.setHasFixedSize(true);
        mScheduledTimeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        AppointmentScheduledTimeAdapter adapter = new AppointmentScheduledTimeAdapter(mContext,
                new AppointmentScheduledTimeAdapter.RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                    }
                });
        mScheduledTimeRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void initDatesItem() {
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_first));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_second));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_third));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fourth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fifth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_sixth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_seventh));
    }

    private DateViewHolder initDatesItemViews(int resourceId) {
        View view = (View) findViewById(resourceId);
        return new DateViewHolder(view);
    }

    boolean isCurrentDateSetForReschedule = false;

    private void setDatesItem() {
        Calendar calendar = Calendar.getInstance();
        isCurrentDateSetForReschedule = false;
        for (int i = 0; i < dateViewHolderList.size(); i++) {
            setDatesItem(calendar, i);
        }
    }

    private void setDatesItem(final Calendar calendar, final int i) {
        dateViewHolderList.get(i).dayOfTheWeekTextView.setText(getWeekDate(calendar, weekCount + i, DAY_OF_WEEK_PATTERN));
        dateViewHolderList.get(i).dayOfTheMonthTextView.setText(getWeekDate(calendar, weekCount + i, DAY_OF_MONTH_PATTERN));
        dateViewHolderList.get(i).monthTextView.setText(getWeekDate(calendar, weekCount + i, MONTH_PATTERN));
        dateViewHolderList.get(i).yearTextView.setText(getWeekDate(calendar, weekCount + i, YEAR_PATTERN));

        if (getWeekDateCalendar(calendar, weekCount + i).get(Calendar.YEAR) == getTodayCalendar().get(Calendar.YEAR)
                && getWeekDateCalendar(calendar, weekCount + i).get(Calendar.MONTH) == getTodayCalendar().get(Calendar.MONTH)
                && getWeekDateCalendar(calendar, weekCount + i).get(Calendar.DAY_OF_MONTH) == getTodayCalendar().get(Calendar.DAY_OF_MONTH)) {

            for (int k = 0; k < i; k++) {
                paintUnSelected(dateViewHolderList.get(k));
            }
            paintSelected(dateViewHolderList.get(i));

            String weekDate = getWeekDate(calendar,
                    weekCount + i,
                    DAY_OF_WEEK_PATTERN
                            + " " + DAY_OF_MONTH_PATTERN
                            + " " + MONTH_PATTERN
                            + " " + YEAR_PATTERN);

            selectedDate = getWeekDate(calendar,
                    weekCount + i,
                    MONTH_PATTERN
                            + " " + DAY_OF_MONTH_PATTERN);

            appointmentSelectedDateTextView.setText(weekDate);
            orderDate.setText(weekDate);

            isCurrentDateSetForReschedule = true;
        } else {
            if (!isCurrentDateSetForReschedule && i == dateViewHolderList.size() - 1) {
                dateViewHolderList.get(0).dayOfTheMonthTextView.performClick();
            }

        }

        dateViewHolderList.get(i).dayOfTheMonthTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String weekDate = getWeekDate(calendar,
                                weekCount + i,
                                DAY_OF_WEEK_PATTERN
                                        + " " + DAY_OF_MONTH_PATTERN
                                        + " " + MONTH_PATTERN
                                        + " " + YEAR_PATTERN
                        );

                        selectedDate = getWeekDate(calendar,
                                weekCount + i,
                                MONTH_PATTERN
                                        + " " + DAY_OF_MONTH_PATTERN);

                        appointmentSelectedDateTextView.setText(weekDate);
                        orderDate.setText(weekDate);

                        for (int j = 0; j < dateViewHolderList.size(); j++) {
                            if (j == i) {
                                paintSelected(dateViewHolderList.get(j));
                            } else {
                                paintUnSelected(dateViewHolderList.get(j));
                            }
                        }
                    }
                });
    }

    private void paintSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.dayOfTheMonthTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.monthTextView.setTextColor(getResources().getColor(R.color.white));
        dateViewHolder.yearTextView.setTextColor(getResources().getColor(R.color.white));

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(getResources().getColor(R.color.app_grey));
        /*dateViewHolder.dayOfTheWeekTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.dayOfTheMonthTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.monthTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));
        dateViewHolder.yearTextView.setBackgroundColor(getResources().getColor(R.color.app_grey));*/
    }

    private void paintUnSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));
        dateViewHolder.dayOfTheMonthTextView.setTextColor(getResources().getColor(R.color.black));
        dateViewHolder.monthTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));
        dateViewHolder.yearTextView.setTextColor(getResources().getColor(R.color.app_grey_medium));

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        /*dateViewHolder.dayOfTheWeekTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.dayOfTheMonthTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.monthTextView.setBackgroundColor(getResources().getColor(R.color.white));
        dateViewHolder.yearTextView.setBackgroundColor(getResources().getColor(R.color.white));*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aa_date_previous:
                weekCount -= weekCountIncrementDecrement;
                setDatesItem();
                break;
            case R.id.aa_date_next:
                weekCount += weekCountIncrementDecrement;
                setDatesItem();
                break;
            case R.id.aa_date_cell_ll:
                break;
        }
    }

    private class DateViewHolder {
        private final LinearLayout aaDateCellLinearLayout;
        private final TextView dayOfTheWeekTextView;
        private final TextView dayOfTheMonthTextView;
        private final TextView monthTextView;
        private final TextView yearTextView;

        public DateViewHolder(View view) {
            aaDateCellLinearLayout = (LinearLayout) view;//(LinearLayout) view.findViewById(R.id.aa_date_cell_ll);
            dayOfTheWeekTextView = (TextView) view.findViewById(R.id.aa_day_of_week);
            dayOfTheMonthTextView = (TextView) view.findViewById(R.id.aa_day_of_month);
            monthTextView = (TextView) view.findViewById(R.id.aa_month);
            yearTextView = (TextView) view.findViewById(R.id.aa_year);
        }
    }

    private String getWeekDate(Calendar calendar, int addNext, String pattern) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(last.getTime());
    }

    private Calendar getWeekDateCalendar(Calendar calendar, int addNext) {
        Calendar first = (Calendar) calendar.clone();
        first.add(Calendar.DAY_OF_WEEK,
                first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, addNext);
        return last;
    }

    private Calendar getTodayCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        return cal;
    }

    private boolean showAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_alert_dialog, null);
        final TextView title = alertLayout.findViewById(R.id.dialog_title);
        title.setText("Book This Appointment");
        final EditText pin = alertLayout.findViewById(R.id.pin);
        final EditText cpin = alertLayout.findViewById(R.id.pin_confirm);
        final ImageView grey_line = alertLayout.findViewById(R.id.grey_line);
        cpin.setVisibility(View.GONE);
        grey_line.setVisibility(View.GONE);

        final Button submit = alertLayout.findViewById(R.id.pin_submit);
        submit.setText("Purchase Now");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.show();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinNumber = pin.getText().toString().trim();
                boolean pinTrue = false;
                hideKeyboard();
                if (pinNumber.isEmpty() || pinNumber.length() < 4) {
                    pin.setError("PIN must be 4 - 10 characters long");
                    pinTrue = false;
                } else {
                    pinTrue = true;
                }

                AppSettings appSettings = new AppSettings(mContext);
                if (pinTrue && appSettings.getPIN().trim().equalsIgnoreCase(pinNumber)) {
                    dialog.dismiss();

                    showSuccessDialog(mContext, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            new AlertDialog.Builder(mContext)
                                    .setTitle("Appointment")
                                    .setMessage("You will receive confirmation of Appointment.")
                                    .setCancelable(false)
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            // payMoney();
                                            Intent intent = new Intent(mContext, ActivityIFareDashBoard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setIcon(R.mipmap.ic_launcher1_foreground)
                                    .show();
                        }
                    });
                } else {
                    /*pin.setError("Wrong PIN");
                    pinTrue = false;
                    dialog.dismiss();
                    Intent intent = new Intent(mContext,ActivityDashBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();*/

                    showMessage(mContext, "Wrong PIN");
                    dialog.dismiss();
                }
            }
        });
        return false;
    }

    /*private void payMoney() {
        if (isOnline(mContext)) {
            showProgressDlg(mContext, "Processing your payment");
            makePayment(mContext, "", "", "", "", new HttpInterface() {
                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess PAYMENT : " + message);
                    hideProgressDlg();
                    //{"status":true,"data":"Done","message":"Completed2018-09-26_13:24:21"}
                    if (message != null || !message.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            if (jsonObject.getBoolean("status")) {

                                String data = jsonObject.getString("data");
                                String msg = jsonObject.getString("message");
                                showMessage(mContext, data + " " + msg);

                            } else {
                                showMessage(mContext, jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(mContext, ActivityIFareDashBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }*/

    int productIdx = -1;

    private void showConfirmationAlertDialog(final String timeString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final String[] serviceTitleArray = new String[menuItemArrayList.size()];
        for (int i = 0; i < menuItemArrayList.size(); i++) {
            serviceTitleArray[i] = menuItemArrayList.get(i).getMenuInfo();
        }
        final ArrayList<MenuItem> selectedMenuItems = new ArrayList<>();

        // Set the dialog title
        //builder.setTitle(String.format("Choose %s Provider", ActivityAppointmentBooking.providerName))
        productIdx = 0;

        builder.setTitle(timeString + "\n" + "Choose services Requested" + "\n" + restaurantInfo.get_name())
                .setCancelable(false)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                /*.setSingleChoiceItems(serviceTitleArray, productIdx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productIdx = which;
                    }
                })*/
                .setMultiChoiceItems(serviceTitleArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedMenuItems.add(menuItemArrayList.get(which));
                        } else {
                            selectedMenuItems.remove(menuItemArrayList.get(which));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                        if (!selectedMenuItems.isEmpty()) {
                            Intent cartIntent = new Intent(getBaseContext(), ActivityOrderInfo.class);
                            cartIntent.putExtra("datetime", timeString);
                            cartIntent.putExtra("menus", selectedMenuItems);
                            cartIntent.putExtra("restaurant", restaurantInfo);
                            startActivity(cartIntent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}