package hawaiiappbuilders.omniversapp.adapters;

import android.view.LayoutInflater;
import android.view.View;

public interface ContactListItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}