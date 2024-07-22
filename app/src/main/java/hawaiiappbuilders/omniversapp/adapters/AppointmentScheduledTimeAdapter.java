package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentScheduledTimeAdapter extends RecyclerView.Adapter<AppointmentScheduledTimeAdapter.ItemViewHolder> {

    Context context;

    public AppointmentScheduledTimeAdapter(Context context, AppointmentScheduledTimeAdapter.RecyclerViewClickListener listener) {
        this.context = context;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public TextView mDayOfWeek, mTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mDayOfWeek = itemView.findViewById(R.id.day_of_week);
            mTime  = itemView.findViewById(R.id.time);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_appointment_schedule_list_item,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
    }

    @Override
    public int getItemCount()
    {
        return 10;
    }

    private String convertDate(String dateString){
        try {
            //create SimpleDateFormat object with source string date format
            SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy/MM/dd");

            //parse the string into Date object
            Date date = sdfSource.parse(dateString);

            //create SimpleDateFormat object with desired date format
            SimpleDateFormat sdfDestination = new SimpleDateFormat("MM-dd-yyyy");

            //parse the date into another format
            dateString = sdfDestination.format(date);
        } catch(ParseException pe) {
            System.out.println("Parse Exception : " + pe);
        }
        return dateString;
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
