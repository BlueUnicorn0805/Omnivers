package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.GroupInfo;

// todo: show up all items on load
public class AutocompleteContactsAdapter extends ArrayAdapter<CustomContactModel> {
    private final Context mContext;
    private final int mLayoutResourceId;
    private FilterListeners filterListeners;

    MessageDataManager messageDataManager;

    ArrayList<CustomContactModel> sortedWithHeaders;

    public interface FilterListeners {

        void showClearIcon();

        void hideClearIcon();

        void filteringFinished(int filteredItemsCount);

        void setToAll();

        void showSearchResults(ArrayList<CustomContactModel> spinnerResults);

        void onClickContact(CustomContactModel model, int position);
    }

    public void setFilterListeners(FilterListeners filterFinishedListener) {
        filterListeners = filterFinishedListener;
    }

    private ArrayList<CustomContactModel> spinnerList;
    private ArrayList<CustomContactModel> spinnerListAll;

    public ArrayList<CustomContactModel> spinnerResults;

    private LayoutInflater inflater;

    @Override
    public boolean isEnabled(int position) {
        return spinnerResults.get(position).getType() == 1;
    }

    public ArrayList<CustomContactModel> getResults() {
        return spinnerResults;
    }

    public AutocompleteContactsAdapter(Context context, int resource, @NonNull ArrayList<CustomContactModel> spinnerList) {
        super(context, resource, spinnerList);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.spinnerList = spinnerList;
        this.spinnerListAll = spinnerList;
        this.spinnerResults = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.messageDataManager = new MessageDataManager(this.mContext);
        sortedWithHeaders = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return spinnerResults.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        try {
            if (spinnerResults.size() > 0) {
                CustomContactModel model;
                try {
                    model = spinnerResults.get(position);
                    int type = model.type;
                    switch (type) {
                        case 0:
                            row = inflater.inflate(R.layout.contact_header, parent, false);
                            TextView header = row.findViewById(R.id.headerName);
                            header.setText(model.name);
                            header.setEnabled(false);
                            break;
                        case 1:
                            row = inflater.inflate(R.layout.spinner_list_item, parent, false);
                            TextView name = row.findViewById(R.id.name);
                            String companyValue;
                            if (model.company.isEmpty()) {
                                companyValue = "";
                            } else {
                                int limit = 15;
                                if (model.company.length() > limit) {
                                    companyValue = model.company.trim().substring(0, limit);
                                } else {
                                    companyValue = model.company.trim();
                                }
                            }

                            if (companyValue.length() > 0) {
                                companyValue += ", ";
                            }

                            Spanned itemText = HtmlCompat.fromHtml("<b>" + companyValue + "</b>" + model.name, HtmlCompat.FROM_HTML_MODE_COMPACT);
                            name.setText(itemText);
                            break;
                    }
                    if (row != null) {
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (filterListeners != null) {
                                    filterListeners.onClickContact(model, position);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Autocomplete", "an error occurred here: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("Autocomplete", "an error occurred here: " + e.getMessage());
        }


        return row;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((CustomContactModel) resultValue).name;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<CustomContactModel> queryResults = new ArrayList<>();

                if (constraint != null) {
                    if (constraint.toString().isEmpty()) {
                        filterListeners.setToAll();
                        filterListeners.hideClearIcon();
                    } else {
                        filterListeners.showClearIcon();
                    }
                    String searchQuery = constraint.toString().toLowerCase();
                    for (CustomContactModel CustomContactModel : spinnerListAll) {
                        if (CustomContactModel.getType() == 1) {
                            boolean isNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getName()).find();
                            boolean isPhoneFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getPhone()).find();
                            boolean isCompanyFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getCompany()).find();
                            boolean isFNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getFname()).find();
                            boolean isLNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getLname()).find();

                            if (isNameFound || isPhoneFound || isCompanyFound || isFNameFound || isLNameFound) {
                                queryResults.add(CustomContactModel);
                            }
                        }
                    }
                } else {
                    queryResults.addAll(spinnerListAll);
                }
                if (queryResults.size() > 0) {
                    ArrayList<CustomContactModel> sorted = sort(queryResults);

                    send(sorted);
                    // wait
                    sortedWithHeaders.clear();
                    try {
                        sortedWithHeaders.addAll(receive());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    filterResults.count = sortedWithHeaders.size();
                    filterResults.values = sortedWithHeaders;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                spinnerResults.clear();
                if (results != null) {
                    if (results.count > 0) {
                        // avoids unchecked cast warning when using mCustomContactModels.addAll((ArrayList<CustomContactModel>) results.values);
                        ArrayList<CustomContactModel> tempList = new ArrayList<>();
                        for (Object object : (List<?>) results.values) {
                            if (object instanceof CustomContactModel) {
                                tempList.add((CustomContactModel) object);
                            }
                        }
                        spinnerResults.addAll(tempList);
                        notifyDataSetChanged();
                    } else {
                        // when filter results return nothing
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }

    private ArrayList<CustomContactModel> sortedData;
    // True if receiver should wait
    // False if sender should wait
    private boolean transfer = true;

    public synchronized ArrayList<CustomContactModel> receive() throws InterruptedException {
        while (transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Thread interrupted
            }
        }
        transfer = true;

        ArrayList<CustomContactModel> returnSortedData = sortedData;
        notifyAll();
        return returnSortedData;
    }


    public synchronized void send(ArrayList<CustomContactModel> sortedData) {
        while (!transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Thread interrupted
            }
        }
        transfer = false;

        this.sortedData = sortedData;
        notifyAll();
    }


    public ArrayList<CustomContactModel> sort(ArrayList<CustomContactModel> list) {
        ArrayList<CustomContactModel> values = new ArrayList<>(list.size());
        try {
            for (int i = 0; i < list.size(); i++) {
                values.add(i, list.get(i));
            }
            CustomContactModel temp;
            for (int i = 0; i <= values.size(); i++) {
                for (int j = i + 1; j < values.size(); j++) {
                    if (values.get(i).getPri() < values.get(j).getPri()) {
                        temp = values.get(j);
                        values.set(j, values.get(i));
                        values.set(i, temp);
                    }
                }
            }
            return values;
        } catch (Exception e) {
            // Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }
        return values;
    }

    public ArrayList<CustomContactModel> addHeaders(ArrayList<CustomContactModel> searchResults) {
        ArrayList<CustomContactModel> finalResults = new ArrayList<>();
        try {
            int searchSize = searchResults.size();
            int currentPri = searchResults.get(0).getPri(); // first item pri

            for (int i = 0; i < searchSize; i++) {
                Log.e("ADDHEADERS", "HERE1:" + i);
                CustomContactModel currentResult = searchResults.get(i);
                Log.e("ADDHEADERS", "HERE2:" + i);
                CustomContactModel model = new CustomContactModel();
                model.isSelected = false;
                int iPri;
                if (i == 0) {
                    iPri = currentPri;
                    addHeader(finalResults, model, iPri);
                    currentPri = currentResult.getPri();
                    model = new CustomContactModel();
                } else {
                    if (currentResult.getPri() != currentPri) {
                        iPri = currentResult.getPri();
                        addHeader(finalResults, model, iPri);
                        currentPri = currentResult.getPri();
                        model = new CustomContactModel();
                    }
                }
                fillUpContactDetails(currentResult, model);
                model.pri = currentPri;
                finalResults.add(model);
            }

        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return finalResults;
    }

    public void fillUpContactDetails(CustomContactModel searchResultItem, CustomContactModel model) {
        model.id = searchResultItem.getId();
        model.type = 1;
        if (searchResultItem.getCompany() != null) {
            model.company = searchResultItem.getCompany();
        } else {
            model.company = "";
        }
        if (searchResultItem.getName() != null) {
            model.name = searchResultItem.getName();
        } else {
            model.name = "";
        }
        if (searchResultItem.getFname() != null) {
            model.fname = searchResultItem.getFname();
        } else {
            model.fname = "";
        }

        if (searchResultItem.getLname() != null) {
            model.lname = searchResultItem.getLname();
        } else {
            model.lname = "";
        }

        if (searchResultItem.getEmail() != null) {
            model.email = searchResultItem.getEmail();
        } else {
            model.email = "";
        }
        if (searchResultItem.getPhone() != null) {
            model.phone = searchResultItem.getPhone();
        } else {
            model.phone = "";
        }
        if (searchResultItem.getWp() != null) {
            model.wp = searchResultItem.getWp();
        } else {
            model.wp = "";
        }
        model.mlid = searchResultItem.getMlid();
    }

    public void addHeader(ArrayList<CustomContactModel> finalResults, CustomContactModel model, int iPri) {
        model.type = 0;
        if (iPri == 57 || iPri <= 100 && iPri >= 95) {
            switch (iPri) {
                case 100:
                    model.name = "Pinned";
                    finalResults.add(model);
                    break;
                case 99:
                    model.name = "Favs";
                    finalResults.add(model);
                    break;
                case 98:
                    model.name = "Family";
                    finalResults.add(model);
                    break;
                case 97:
                    model.name = "Friends";
                    finalResults.add(model);
                    break;
                case 96:
                    model.name = "Business";
                    finalResults.add(model);
                    break;
                case 95:
                    model.name = "Groups";
                    finalResults.add(model);
                    break;
                case 57:
                    model.name = "All";
                    finalResults.add(model);
                    break;

            }
        } else {
            // todo: get group names
            ArrayList<GroupInfo> groups = messageDataManager.getAlLUserGroups();
            for (GroupInfo groupInfo : groups) {
                if (groupInfo.getPri() == iPri) {
                    model.name = groupInfo.getGrpname();
                    finalResults.add(model);
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "AutocompleteContactsAdapter{" +
                "mContext=" + mContext +
                ", mLayoutResourceId=" + mLayoutResourceId +
                ", filterListeners=" + filterListeners +
                ", spinnerList=" + spinnerList +
                ", spinnerListAll=" + spinnerListAll +
                ", spinnerResults=" + spinnerResults +
                ", inflater=" + inflater +
                '}';
    }
}