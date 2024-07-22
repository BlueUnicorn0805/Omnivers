package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import hawaiiappbuilders.omniversapp.adapters.IndustryContactAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityWorkSearchByIndustry extends BaseActivity {
    public static final String TAG = ActivityWorkSearchByIndustry.class.getSimpleName();

    Spinner spinnerJobType;
    String[] industryNames;
    RecyclerView rvContacts;
    IndustryContactAdapter industryContactAdapter;
    ArrayList<String> contactList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksearchbyindustry);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinnerJobType = findViewById(R.id.spinnerJobType);
        String[] jobs = getResources().getStringArray(R.array.job_types);

        ArrayAdapter<String> paymentsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item, Arrays.asList(jobs));
        spinnerJobType.setAdapter(paymentsAdapter);

        rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        contactList.add("John Doe");
        contactList.add("Jane Doe");
        contactList.add("Michel Mario");
        contactList.add("Tom Jack");
        contactList.add("Xian Ji");

        industryContactAdapter = new IndustryContactAdapter(mContext, contactList, new IndustryContactAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(mContext, ActivityWorkProjects.class);
                intent.putExtra("name", contactList.get(position));
                startActivity(intent);
            }
        });
        rvContacts.setAdapter(industryContactAdapter);

        findViewById(R.id.btnIHaveContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ActivityWorkContact.class));
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
