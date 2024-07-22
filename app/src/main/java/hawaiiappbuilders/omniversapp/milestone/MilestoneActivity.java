package hawaiiappbuilders.omniversapp.milestone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class MilestoneActivity extends BaseActivity {
    Context mContext;
    RecyclerView rvJobDesc;
    ArrayList<JobDesc> mJobs;

    JobDescAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone);
        mContext = this;
        mJobs = new ArrayList<>();
        rvJobDesc = findViewById(R.id.rv_jobfinds);
        JobDesc jobDesc1 = new JobDesc();
        jobDesc1.setJobTitle("Research Paper about Hawaii - 300 pages");
        jobDesc1.setJobCost(10500.00);
        jobDesc1.setJobDescription("I need a help with a researcher to write a 300 page book about Hawaii");
        jobDesc1.setPostedBy("Bret");
        jobDesc1.setTimestamp("3 hours ago");
        mJobs.add(jobDesc1);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        rvJobDesc.setHasFixedSize(true);
        rvJobDesc.setLayoutManager(new LinearLayoutManager(mContext));
        rvJobDesc.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new JobDescAdapter(mContext, mJobs, new JobDescAdapter.OnClickCheckListener() {
            @Override
            public void onClickJobDesc(JobDesc jobDesc) {
                Intent intent = new Intent(mContext, JobHuntDetailActivity.class);
                intent.putExtra("jobDesc", jobDesc);
                startActivity(intent);
            }
        });
        rvJobDesc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
