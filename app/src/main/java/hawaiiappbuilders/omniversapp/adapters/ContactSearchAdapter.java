package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.ContactInfo;

import java.util.ArrayList;

public class ContactSearchAdapter extends ArrayAdapter<ContactInfo> {
    private ArrayList<ContactInfo> items;
    private ArrayList<ContactInfo> itemsAll;
    private ArrayList<ContactInfo> suggestions;
    private int viewResourceId;

    private boolean isForEmail;

    @SuppressWarnings("unchecked")
    public ContactSearchAdapter(Context context, int viewResourceId,
                                ArrayList<ContactInfo> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<ContactInfo>) items.clone();
        this.suggestions = new ArrayList<ContactInfo>();
        this.viewResourceId = viewResourceId;
    }

    public String getSuggestionItemValue(int position) {
        if (isForEmail) {
            return suggestions.get(position).getEmailData();
        } else {
            return suggestions.get(position).getPhoneData();
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_spinner_contact, null);
        }

        ContactInfo contactInfo = items.get(position);
        if (contactInfo != null) {
            TextView productLabel = (TextView) v.findViewById(R.id.tvName);
            TextView contactLabel = (TextView) v.findViewById(R.id.tvContact);
            productLabel.setText(contactInfo.getName());
            contactLabel.setText(isForEmail ? contactInfo.getEmailData() : contactInfo.getPhoneData());
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ContactInfo contactInfo = (ContactInfo) resultValue;

            if (isForEmail) {
                return contactInfo.getEmailData();
            } else {
                return contactInfo.getPhoneData();
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                String searchKey = constraint.toString();

                if (searchKey.matches("[0-9]+") && searchKey.length() > 1) {
                    isForEmail = false;
                } else {
                    isForEmail = true;
                }

                suggestions.clear();

                for (ContactInfo contactInfo : itemsAll) {
                    if (isForEmail && contactInfo.getEmailData().contains(constraint)) {
                        suggestions.add(contactInfo);
                    } else if(!isForEmail && (contactInfo.getPhoneData().contains(constraint) || contactInfo.getPhoneMetaData().contains(constraint) )) {
                        suggestions.add(contactInfo);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<ContactInfo> filteredList = (ArrayList<ContactInfo>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ContactInfo c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
