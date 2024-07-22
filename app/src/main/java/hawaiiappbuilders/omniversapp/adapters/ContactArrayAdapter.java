package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;

public class ContactArrayAdapter extends ArrayAdapter<ContactListItem> {
    private LayoutInflater mInflater;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public ContactArrayAdapter(Context context, List<ContactListItem> items) {
        super(context, 0, items);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);
        View View;
        if (convertView == null) {
            holder = new ViewHolder();

            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.spinner_list_item, null);
                    holder.View = getItem(position).getView(mInflater, convertView);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.contact_header, null);
                    holder.View = getItem(position).getView(mInflater, convertView);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }


    public static class ViewHolder {
        public View View;
    }

}