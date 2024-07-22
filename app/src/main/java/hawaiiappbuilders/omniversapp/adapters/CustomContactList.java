package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.utils.DataUtil;

public class CustomContactList extends ArrayAdapter<CustomContactModel> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CustomContactModel> spinnerList;

    public CustomContactList(@NonNull Context context, int resource, @NonNull ArrayList<CustomContactModel> spinnerList) {
        super(context, resource, spinnerList);
        this.context = context;
        this.spinnerList = spinnerList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return spinnerList.get(position).type == 1 || position == 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = null;
        CustomContactModel model = spinnerList.get(position);
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
                name.setText(DataUtil.getCompanyAndName(model));
                break;
        }
        return row;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = null;
        CustomContactModel model = spinnerList.get(position);
        int type = model.type;

        switch (type) {
            case 0:
                row = inflater.inflate(R.layout.contact_header, parent, false);
                TextView header = row.findViewById(R.id.headerName);
                header.setText(model.name);
                break;
            case 1:
                row = inflater.inflate(R.layout.spinner_list_item, parent, false);
                TextView name = row.findViewById(R.id.name);
                String companyValue;
                if(model.company.isEmpty()) {
                    companyValue = "";
                } else {
                    int limit = 15;
                    if(model.company.length() > limit) {
                        companyValue = model.company.trim().substring(0, limit);
                    } else {
                        companyValue = model.company.trim();
                    }
                }

                if(companyValue.length() > 0) {
                    companyValue += ", ";
                }

                Spanned itemText = HtmlCompat.fromHtml("<b>" + companyValue + "</b>" + model.name, HtmlCompat.FROM_HTML_MODE_COMPACT);
                name.setText(itemText);
                break;
        }
        return row;
    }
}