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

import hawaiiappbuilders.omniversapp.adapters.CalTimeAgendaAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.CalendarData;

public class ActivitySearchMeet extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivitySearchMeet.class.getSimpleName();
    EditText edtSearch;

    RecyclerView rcvContactList;
    ArrayList<CalendarData.Data> calcInfos = new ArrayList<>();
    ArrayList<CalendarData.Data> searchList = new ArrayList<>();
    CalTimeAgendaAdapter calcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchmeet);

        Intent intent = getIntent();
        calcInfos = intent.getParcelableArrayListExtra("calc_list");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = edtSearch.getText().toString().trim().toLowerCase();
                searchList.clear();
                if (!TextUtils.isEmpty(keyword)) {
                    for (int i = 0; i < calcInfos.size(); i++) {
                        CalendarData.Data contactInfo = calcInfos.get(i);
                        if (contactInfo.getTitle().toLowerCase().contains(keyword)) {
                            searchList.add(contactInfo);
                        }
                    }
                }
                calcAdapter.notifyData(searchList);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        rcvContactList = findViewById(R.id.rcvContactList);
        calcAdapter = new CalTimeAgendaAdapter(mContext, new CalTimeAgendaAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                CalendarData.Data contactInfo = searchList.get(position);

                Intent intent = new Intent();
                intent.putExtra("meet_info", contactInfo);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onApptClick(View view, int groupPos, int position, CalendarData.Data calData) {}

            @Override
            public void onApptLongClick(View view, int groupPos, int position, CalendarData.Data calData) {}
        });
        rcvContactList.setAdapter(calcAdapter);
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
