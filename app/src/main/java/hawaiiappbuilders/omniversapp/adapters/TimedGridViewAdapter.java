package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;

public class TimedGridViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater=null;

    public TimedGridViewAdapter(Context context) {
        this.context = context;
        this.inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ;
    }

    @Override
    public int getCount() {
        return 14 * 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();
        View rowView = inflater.inflate(R.layout.add_appointment_grid_item, null);
        viewHolder.timeTextView =(TextView) rowView.findViewById(R.id.aa_grid_time);

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

        timeString = String.format(" %02d:%02d %s", time, mins, AM_PM);

        viewHolder.timeTextView.setText(timeString);

        return rowView;
    }

    private class ViewHolder {
        TextView timeTextView;
    }
}