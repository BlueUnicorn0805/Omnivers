package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.ContactInfo;

public class PhoneSearchAdapter extends ArrayAdapter<ContactInfo> {
    private ArrayList<ContactInfo> itemsAll;
    private ArrayList<ContactInfo> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public PhoneSearchAdapter(Context context, int viewResourceId, ArrayList<ContactInfo> items) {
        super(context, viewResourceId);

        this.itemsAll = (ArrayList<ContactInfo>) items.clone();
        this.suggestions = new ArrayList<ContactInfo>();
        this.viewResourceId = viewResourceId;
    }

    public String getSuggestionItemValue(int position) {
        return suggestions.get(position).getEmail();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_spinner_contact, null);
        }

        ContactInfo contactInfo = itemsAll.get(position);
        if (contactInfo != null) {
            TextView productLabel = (TextView) v.findViewById(R.id.tvName);
            TextView contactLabel = (TextView) v.findViewById(R.id.tvContact);
            productLabel.setText(contactInfo.getName());
            contactLabel.setText(contactInfo.getCp());
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
            return contactInfo.getCp();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                String searchKey = constraint.toString();

                suggestions.clear();

                for (ContactInfo contactInfo : itemsAll) {
                    String cp = contactInfo.getCp();
                    String wp = contactInfo.getWp();
                    if ((!TextUtils.isEmpty(cp) && cp.toLowerCase().contains(constraint.toString().toLowerCase())) ||
                            !TextUtils.isEmpty(wp) && wp.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
