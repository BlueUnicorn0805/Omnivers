package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.CalendarData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ItemViewHolder> implements View.OnClickListener {

    private Context context;
    ScheduleAdapter.RecyclerViewClickListener listener;

    private long selectedDate = 0;
    private List<CalendarData.Data> mCalendarDataList = new ArrayList<>();
    private String TAG = ScheduleAdapter.class.getSimpleName();

    private int selectedItemID = -1;

    public ScheduleAdapter(Context context, ScheduleAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        if (clickItemID == selectedItemID) {
            selectedItemID = -1;
        } else {
            selectedItemID = clickItemID;
        }

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }

        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout mMainLinearLayout;
        public TextView mAppointmentStart, mAppointmentTitle;
        public ImageView mItemSelected;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);
            mAppointmentStart = itemView.findViewById(R.id.appointment_start);
            mAppointmentTitle = itemView.findViewById(R.id.appointment_title);
            mItemSelected = itemView.findViewById(R.id.ivSelected);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        CalendarData.Data dataItem = mCalendarDataList.get(position);

        holder.mAppointmentStart.setText(convertTime(dataItem.getStartDate()));
        holder.mAppointmentTitle.setText(dataItem.getTitle());

        int statusId = dataItem.getStatusId();

        if (statusId >= 2003) {     // Green
            holder.mAppointmentTitle.setBackgroundResource(R.drawable.greenround);
        } else {                    // Yellow
            holder.mAppointmentTitle.setBackgroundResource(R.drawable.yellowround);
        }

        if (selectedItemID == position) {
            holder.mItemSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mItemSelected.setVisibility(View.GONE);
        }

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mCalendarDataList.size();
    }

    private String convertDate(String dateString){
        try {
            SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdfSource.parse(dateString);
            SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
            dateString = sdfDestination.format(date);
        } catch(ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return dateString;
    }

    private String convertTime(String dateString){
        try {
            dateString = dateString.replace("T", " ");
            SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdfSource.parse(dateString);
            SimpleDateFormat sdfDestination = new SimpleDateFormat("hh:mm a");
            dateString = sdfDestination.format(date);
        } catch(ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return dateString;
    }

    public void notifyDateChanged(Calendar selectedDate,List<CalendarData.Data> calendarDataList) {
        this.selectedDate = selectedDate.getTimeInMillis();
        this.mCalendarDataList = calendarDataList;

        selectedItemID = -1;

        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<CalendarData.Data> calendarDataList) {
        this.mCalendarDataList = calendarDataList;
        selectedItemID = -1;
        notifyDataSetChanged();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public CalendarData.Data getSelectedItem() {
        if (selectedItemID == -1) {
            return null;
        }

        try {
            return mCalendarDataList.get(selectedItemID);
        } catch (Exception e) {
            return null;
        }
    }
}