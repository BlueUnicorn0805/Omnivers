package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ItemViewHolder> {

    private Context context;
    private long selectedDate = 0;
    private List<CalendarData.Data> mCalendarDataList = new ArrayList<>();
    private String TAG = AppointmentAdapter.class.getSimpleName();

    public AppointmentAdapter(Context context, AppointmentAdapter.RecyclerViewClickListener listener) {
        this.context = context;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout mMainLinearLayout;
        public TextView mAppointmentStart, mAppointmentEnd, mAppointmentTitle, mAppointmentVenue;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);
            mAppointmentStart = itemView.findViewById(R.id.appointment_start);
            mAppointmentEnd  = itemView.findViewById(R.id.appointment_end);
            mAppointmentTitle = itemView.findViewById(R.id.appointment_title);
            mAppointmentVenue = itemView.findViewById(R.id.appointment_venue);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list_item,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.mAppointmentStart.setText(convertTime(mCalendarDataList.get(position).getStartDate()));
        holder.mAppointmentEnd.setText(convertDate(mCalendarDataList.get(position).getStartDate()));
        holder.mAppointmentTitle.setText(mCalendarDataList.get(position).getTitle());
        holder.mAppointmentVenue.setText(mCalendarDataList.get(position).getAddress());
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
        notifyDataSetChanged();
    }

    public void notifyDataChanged(List<CalendarData.Data> calendarDataList) {
        this.mCalendarDataList = calendarDataList;
        notifyDataSetChanged();
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }
}