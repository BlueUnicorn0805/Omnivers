package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.CalendarData;

public class CalTimeAdapter extends RecyclerView.Adapter<CalTimeAdapter.ItemViewHolder> implements View.OnClickListener,
        View.OnLongClickListener {

    public interface RecyclerViewClickListener {
        void onTimeClick(View view, int position);

        void onTimelineClick(View view, int position);

        void onApptClick(View view, int groupPos, int position, CalendarData.Data calData);

        void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData);
    }


    private Context context;
    CalTimeAdapter.RecyclerViewClickListener listener;

    Map<String, ArrayList<CalendarData.Data>> mapData;
    private String TAG = CalTimeAdapter.class.getSimpleName();

    private CalendarData.Data selectedItem;
    private int selectedGroupPosition = -1;

    public CalTimeAdapter(Context context, CalTimeAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            if (viewID == R.id.tvTime) {
                listener.onTimeClick(view, clickItemID);
            } else {
                listener.onTimelineClick(view, clickItemID);
            }
        }

        //notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View view) {
        int clickItemID = (int) view.getTag();

        if (listener != null) {
            listener.onTimelineClick(view, clickItemID);
        }

        return true;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    RecyclerViewClickListener recyclerViewClickListener = new CalTimeAdapter.RecyclerViewClickListener() {

        @Override
        public void onApptClick(View view, int groupPos, int position, CalendarData.Data calData) {
            listener.onApptClick(view, groupPos, position, calData);

            int originaGroupPosition = selectedGroupPosition;

            selectedItem = calData;
            selectedGroupPosition = groupPos;

            /*// Update Original Timeline
            if (originaGroupPosition >= 0) {
                notifyItemChanged(originaGroupPosition);
            }

            if (selectedGroupPosition != originaGroupPosition) {
                notifyItemChanged(originaGroupPosition);
            }*/
            notifyDataSetChanged();
        }

        @Override
        public void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData) {
            listener.onApptLongClick(view, groupPos, position, calData);

            int originaGroupPosition = selectedGroupPosition;

            selectedItem = calData;
            selectedGroupPosition = groupPos;

            /*// Update Original Timeline
            if (originaGroupPosition >= 0) {
                notifyItemChanged(originaGroupPosition);
            }

            if (selectedGroupPosition != originaGroupPosition) {
                notifyItemChanged(originaGroupPosition);
            }*/
            notifyDataSetChanged();

        }

        @Override
        public void onTimeClick(View view, int position) {
        }

        @Override
        public void onTimelineClick(View view, int position) {
        }
    };

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mMainLinearLayout;
        public TextView tvTime;
        public RecyclerView rcvAppts;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mMainLinearLayout = itemView.findViewById(R.id.main_ll);
            tvTime = itemView.findViewById(R.id.tvTime);

            rcvAppts = itemView.findViewById(R.id.rcvAppts);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_appt_timeline_item, parent, false);
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

        String timeTitle = getTimeTitle(position);
        holder.tvTime.setText(timeTitle);

        ArrayList<CalendarData.Data> hourItems = null;

        if (mapData != null) {
            hourItems = mapData.get(timeTitle);
        }

        CalApptAdapter calApptAdapter;
        if (holder.rcvAppts.getTag() != null) {
            calApptAdapter = (CalApptAdapter) holder.rcvAppts.getTag();
            calApptAdapter.setGroupPosition(position);
        } else {
            calApptAdapter = new CalApptAdapter(context, position, recyclerViewClickListener);
        }
        calApptAdapter.setActiveList(selectedItem);
        calApptAdapter.setApptList(hourItems);
        holder.rcvAppts.setAdapter(calApptAdapter);
        holder.rcvAppts.setTag(calApptAdapter);

        holder.tvTime.setTag(position);
        holder.tvTime.setOnClickListener(this);

        holder.mMainLinearLayout.setTag(position);
        holder.mMainLinearLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return 23;
    }

    public void notifyData(Map<String, ArrayList<CalendarData.Data>> mapData) {
        this.mapData = mapData;
        notifyDataSetChanged();
    }
}