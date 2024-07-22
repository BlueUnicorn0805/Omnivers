package hawaiiappbuilders.omniversapp.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.regex.Pattern;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.model.GroupInfo;

public class AutocompleteContactsAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private FilterListeners filterListeners;
    MessageDataManager messageDataManager;
    private static final int MENU_HEADER = 0;
    private static final int MENU_CONTACT = 1;

    // original list
    private final ArrayList<CustomContactModel> spinnerListAll;

    // final search results
    public ArrayList<CustomContactModel> spinnerResults;

    private LayoutInflater inflater;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView texName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            texName = itemView.findViewById(R.id.name);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public TextView texName;

        public HeaderHolder(View itemView) {
            super(itemView);
            texName = itemView.findViewById(R.id.headerName);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MENU_HEADER) {
            v = inflater.inflate(R.layout.contact_header, parent, false);
            return new HeaderHolder(v);
        }
        v = inflater.inflate(R.layout.spinner_list_item_2, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (spinnerResults.get(position).getType() == 0) {
            return MENU_HEADER;
        } else {
            return MENU_CONTACT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CustomContactModel model = spinnerResults.get(position);
        int itemViewType = getItemViewType(position);
        if (itemViewType == MENU_HEADER) {
            HeaderHolder headerViewHolder = (HeaderHolder) holder;
            headerViewHolder.texName.setText(model.name);
            headerViewHolder.texName.setEnabled(false);
        } else {
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

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.texName.setText(itemText);
            itemViewHolder.texName.setEnabled(true);

            itemViewHolder.texName.setTag(position);
            itemViewHolder.texName.setOnClickListener(nameClickListener);
        }
    }

    View.OnClickListener nameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();

            CustomContactModel model = spinnerResults.get(position);
            if (filterListeners != null) {
                filterListeners.onClickContact(model, position);
            }
        }
    };

    @Override
    public int getItemCount() {
        return spinnerResults.size();
    }

    public interface FilterListeners {

        void showClearIcon();

        void hideClearIcon();

        void setToAll();

        void showDropdown(boolean show);

        void onClickContact(CustomContactModel model, int position);
    }

    public void setFilterListeners(FilterListeners filterFinishedListener) {
        filterListeners = filterFinishedListener;
    }

    public AutocompleteContactsAdapter2(Context context, @NonNull ArrayList<CustomContactModel> spinnerList) {
        this.mContext = context;
        this.spinnerListAll = spinnerList;
        this.spinnerResults = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.messageDataManager = new MessageDataManager(this.mContext);
    }

    public ArrayList<CustomContactModel> performFilter(String constraint) {
        spinnerResults.clear();
        ArrayList<CustomContactModel> queryResults = new ArrayList<>();
        if (constraint != null) {
            if (filterListeners != null) {
                if (constraint.isEmpty()) {
                    filterListeners.setToAll();
                    filterListeners.hideClearIcon();
                } else {
                    filterListeners.showClearIcon();
                }
            }
            String searchQuery = constraint.toLowerCase();
            for (CustomContactModel CustomContactModel : spinnerListAll) {
                if (CustomContactModel.getType() == 1) {
                    boolean isNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getName()).find();
                    boolean isPhoneFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getPhone()).find();
                    boolean isCompanyFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getCompany()).find();
                    boolean isFNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getFname()).find();
                    boolean isLNameFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getLname()).find();
                    boolean isAddressFound = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(CustomContactModel.getAddress()).find();

                    if (isNameFound || isPhoneFound || isCompanyFound || isFNameFound || isLNameFound || isAddressFound) {
                        queryResults.add(CustomContactModel);
                    }
                }
            }
        } else {
            queryResults.addAll(spinnerListAll);
        }

        if (queryResults.size() > 0) {
            ArrayList<CustomContactModel> sorted = sort(queryResults);
            ArrayList<CustomContactModel> results = addHeaders(sorted);
            if (results != null) {
                if (results.size() > 0) {
                    spinnerResults.addAll(results);
                }
            }
        }
        return spinnerResults;
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

        this.sortedData = addHeaders(sortedData);
        notifyAll();
    }


    public ArrayList<CustomContactModel> sort(ArrayList<CustomContactModel> list) {
        ArrayList<CustomContactModel> values = new ArrayList<>(list.size());
        try {
            for (int i = 0; i < list.size(); i++) {
                values.add(i, list.get(i));
            }
            CustomContactModel temp;
            for (int i = 0; i < values.size(); i++) {
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
                CustomContactModel currentResult = searchResults.get(i);
                CustomContactModel model = new CustomContactModel();
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
}