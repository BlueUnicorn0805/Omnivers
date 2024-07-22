package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.appointment.ApptUtil;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.model.CalendarData;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class CalTimeAgendaAdapter extends RecyclerView.Adapter<CalTimeAgendaAdapter.ItemViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    CalTimeAgendaAdapter.RecyclerViewClickListener listener;

    ArrayList<CalendarData.Data> mapData;
    private String TAG = CalTimeAgendaAdapter.class.getSimpleName();

    public CalTimeAgendaAdapter(Context context, CalTimeAgendaAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }

        notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View view) {
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            listener.onClick(view, clickItemID);
        }

        return true;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mMainLinearLayout;
        public TextView tvTime;
        public TextView tvContents;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);
            tvTime = itemView.findViewById(R.id.tvTime);

            tvContents = itemView.findViewById(R.id.tvContents);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appt_timeline_item_agenda, parent, false);
        return new ItemViewHolder(v);
    }

    private String getTimeTitle(int position) {
        int hour = position + 1;

        String timeTile = String.format("%d %s",
                (hour <= 12) ? hour : hour - 12,
                (hour < 12) ? "AM" : "PM");
        return timeTile;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CalendarData.Data itemData = mapData.get(position);
        Date date = DateUtil.parseDataFromFormat12(itemData.getStartDate().replace("T", " "));
        //String dateString = String.format("%s%s\n%s", date.getDate(), getDayOfMonthSuffix(date.getDate()), DateUtil.toStringFormat_10(date));
        String dateString = String.format("%s\n%s", DateUtil.dateToString(date, "EEE dd"), DateUtil.toStringFormat_10(date));

        holder.tvTime.setText(dateString);

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);


        AppSettings appSettings = new AppSettings(holder.itemView.getContext());
        ApptUtil apptUtil = new ApptUtil(holder.itemView.getContext());

//        if (itemData.getAttendeeMLID() == 0) {
//        if (itemData.getSellerId() == 0) {
//            holder.tvContents.setText(itemData.getCalId() + ": " + itemData.getTitle());
//        } else {
        if (apptUtil.isApptForPatient(itemData)) {
            holder.tvContents.setText(itemData.getAttendeeName());
            Log.e("loggingggg", "true(1) -> " + itemData.getCalId() + ": " + itemData.getAttendeeName());
        } else if (apptUtil.isApptCreatedByDoctor(itemData)) {
            holder.tvContents.setText(itemData.getTitle());
            Log.e("loggingggg", "true(2) -> " + itemData.getCalId() + ": " + itemData.getAttendeeName());
        } else {
            holder.tvContents.setText(itemData.getTitle());
            Log.e("loggingggg", "false -> " + itemData.getCalId() + ": " + itemData.getAttendeeName());
        }
//        }

        ApptUtil.colorItemByStatus(holder.itemView.getContext(), itemData.getApptStatusID(), holder.tvContents, itemData);
    }

    private String getDayOfMonthSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @Override
    public int getItemCount() {
        if (mapData == null) return 0;
        return mapData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);

        void onApptClick(View view, int groupPos, int position, CalendarData.Data calData);

        void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData);
    }

    public void notifyData(ArrayList<CalendarData.Data> mapData) {
        this.mapData = mapData;
        notifyDataSetChanged();
    }
}