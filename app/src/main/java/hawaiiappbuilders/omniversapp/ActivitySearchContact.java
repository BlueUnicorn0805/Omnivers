package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.adapters.SearchContactAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivitySearchContact extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivitySearchContact.class.getSimpleName();
    EditText edtSearch;

    RecyclerView rcvContactList;
    // ArrayList<ContactInfo> contactInfos = new ArrayList<>();

    ArrayList<CustomContactModel> contactInfos = new ArrayList<>();

    ArrayList<CustomContactModel> searchList = new ArrayList<>();
    SearchContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchcontact);

        Intent intent = getIntent();
        contactInfos = intent.getParcelableArrayListExtra("contacts");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = edtSearch.getText().toString().trim().toLowerCase();
                searchList.clear();
                if (!TextUtils.isEmpty(keyword)) {
                    for (int i = 1; i < contactInfos.size(); i++) {
                        CustomContactModel contactInfo = contactInfos.get(i);
                        if (contactInfo.type == 1 && (contactInfo.company.toLowerCase().contains(keyword) ||
                                contactInfo.fname.toLowerCase().contains(keyword) ||
                                contactInfo.lname.toLowerCase().contains(keyword) ||
                                contactInfo.phone.toLowerCase().contains(keyword) ||
                                contactInfo.email.toLowerCase().contains(keyword))) {
                            searchList.add(contactInfo);
                        }
                    }
                }

                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        rcvContactList = findViewById(R.id.rcvContactList);
        contactAdapter = new SearchContactAdapter(mContext, searchList, new SearchContactAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomContactModel contactInfo = searchList.get(position);

                int selectedPosition = -1;
                for (int i = 0; i < contactInfos.size(); i++) {
                    if (contactInfo.getId() == contactInfos.get(i).getId()) {
                        selectedPosition = i;
                        
                        Intent intent = new Intent();
                        intent.putExtra("position", selectedPosition);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
        rcvContactList.setAdapter(contactAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == 0) {
        }
    }
}
