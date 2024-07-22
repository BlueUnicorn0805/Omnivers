package hawaiiappbuilders.omniversapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.adapters.TimedGridViewAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectTimeDailog extends Dialog implements View.OnClickListener {

    public interface TimeSelectListener {
        void onTimeSelected(String datetime, String eventTitle);
    }

    Context mContext;
    BaseActivity mActivity;

    String mSelectedDateTime = "";

    TimeSelectListener mTimeSelectListener;

    private static final String DAY_OF_WEEK_PATTERN = "EEE";
    private static final String DAY_OF_MONTH_PATTERN = "dd";
    private static final String MONTH_PATTERN = "MMM";
    private static final String YEAR_PATTERN = "yyyy";

    private GridView mTimesGridView;
    TimedGridViewAdapter timedGridViewAdapter;

    String selectedDate = "";

    private List<DateViewHolder> dateViewHolderList = new ArrayList<>();
    private int weekCount = 0;
    private final int weekCountIncrementDecrement = 7;

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

    int whiteColor;
    int greyColor;
    int greyMediumColor;
    int blackColor;
    // --------------------------------------------------------------

    CardView layoutDates;
    LinearLayout layoutTimes;

    EditText edtEventTitle;

    String fn;
    public SelectTimeDailog(@NonNull Activity context, String fn,String selectedDateTime, TimeSelectListener timeSelectListener) {
        super(context);

        mContext = context;
        mActivity = (BaseActivity) context;

        mSelectedDateTime = selectedDateTime;
        this.fn = fn;
        mTimeSelectListener = timeSelectListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_change_appointment);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        whiteColor = mContext.getResources().getColor(R.color.white);
        greyColor = mContext.getResources().getColor(R.color.app_grey);
        greyMediumColor = mContext.getResources().getColor(R.color.app_grey_medium);
        blackColor = mContext.getResources().getColor(R.color.black);

        // Show Init Date
        TextView tvDateTime = findViewById(R.id.tvApptTitle);
        if (TextUtils.isEmpty(mSelectedDateTime)) {
            tvDateTime.setVisibility(View.GONE);
        } else {
            tvDateTime.setText(mSelectedDateTime);
        }

        mTimesGridView = (GridView) findViewById(R.id.aa_time_grid_layout);
        layoutDates = findViewById(R.id.layout_dates);
        layoutTimes = findViewById(R.id.layout_times);
        edtEventTitle = findViewById(R.id.edtApptTitle);
        edtEventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    layoutDates.setVisibility(View.VISIBLE);
                    layoutTimes.setVisibility(View.VISIBLE);
                } else {
                    layoutDates.setVisibility(View.GONE);
                    layoutTimes.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtEventTitle.setText(fn);
        timedGridViewAdapter = new TimedGridViewAdapter(mContext);
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

                timeString = selectedDate + " " + year + String.format(" %02d:%02d %s", time, mins, AM_PM);

                // Show Dialog for items
                showConfirmationAlertDialog(timeString);
            }
        });

        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_first));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_second));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_third));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fourth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_fifth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_sixth));
        dateViewHolderList.add(initDatesItemViews(R.id.aa_dates_seventh));

        Calendar calendar = Calendar.getInstance();

        isCurrentDateSetForReschedule = false;
        for (int i = 0; i < dateViewHolderList.size(); i++) {
            setDatesItem(calendar, i);
        }

        findViewById(R.id.aa_date_previous).setOnClickListener(this);
        findViewById(R.id.aa_date_next).setOnClickListener(this);
        findViewById(R.id.btnCloseApptTimePanel).setOnClickListener(this);

        setCancelable(false);
    }

    private DateViewHolder initDatesItemViews(int resourceId) {
        View view = (View) findViewById(resourceId);
        return new DateViewHolder(view);
    }

    boolean isCurrentDateSetForReschedule = false;

    String year;
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

            year = weekDate.split(" ")[3];
            selectedDate = getWeekDate(calendar,
                    weekCount + i,
                    MONTH_PATTERN
                            + " " + DAY_OF_MONTH_PATTERN);

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

                         year = weekDate.split(" ")[3];

                        selectedDate = getWeekDate(calendar,
                                weekCount + i,
                                MONTH_PATTERN
                                        + " " + DAY_OF_MONTH_PATTERN);

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

    private Calendar getTodayCalendar() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d");
        return cal;
    }

    private void paintSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(whiteColor);
        dateViewHolder.dayOfTheMonthTextView.setTextColor(whiteColor);
        dateViewHolder.monthTextView.setTextColor(whiteColor);
        dateViewHolder.yearTextView.setTextColor(whiteColor);

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(greyColor);
    }

    private void paintUnSelected(DateViewHolder dateViewHolder) {
        dateViewHolder.dayOfTheWeekTextView.setTextColor(greyMediumColor);
        dateViewHolder.dayOfTheMonthTextView.setTextColor(blackColor);
        dateViewHolder.monthTextView.setTextColor(greyMediumColor);
        dateViewHolder.yearTextView.setTextColor(greyMediumColor);

        dateViewHolder.aaDateCellLinearLayout.setBackgroundColor(whiteColor);
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

    int productIdx = -1;

    private void showConfirmationAlertDialog(final String dateTimeString) {
        if (mTimeSelectListener != null) {
            mTimeSelectListener.onTimeSelected(dateTimeString, edtEventTitle.getText().toString());
        }
        /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);

        builder.setTitle(dateTimeString + "\n" + "Sending schedule change request")
                .setCancelable(false)
                // Set the action buttons
                .setPositiveButton("Ok", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (mTimeSelectListener != null) {
                            mTimeSelectListener.onTimeSelected(dateTimeString);
                        }
                        dialog.dismiss();

                        SelectTimeDailog.this.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();*/
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnCloseApptTimePanel) {
            dismiss();
        } else if (viewId == R.id.aa_date_previous) {
            weekCount -= weekCountIncrementDecrement;
            setDatesItem();
        } else if (viewId == R.id.aa_date_next) {
            weekCount += weekCountIncrementDecrement;
            setDatesItem();
        }
    }
}
