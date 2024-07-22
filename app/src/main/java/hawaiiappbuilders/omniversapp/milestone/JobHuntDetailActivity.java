package hawaiiappbuilders.omniversapp.milestone;

import static hawaiiappbuilders.omniversapp.milestone.JobDescAdapter.formatAmount;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class JobHuntDetailActivity extends BaseActivity {

    JobDesc jobDesc;

    TextView textJobTitle;
    TextView textPostedBy;
    TextView textTimestamp;
    TextView textJobDescription;
    TextView textPayRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_hunt_item_details);

        jobDesc = getIntent().getParcelableExtra("jobDesc");
        textJobTitle = findViewById(R.id.textJobTitle);
        textPostedBy = findViewById(R.id.textPostedBy);
        textTimestamp = findViewById(R.id.textTimestamp);
        textJobDescription = findViewById(R.id.textJobDescription);
        textPayRate = findViewById(R.id.textPayRate);

        textJobTitle.setText(jobDesc.getJobTitle());
        textPostedBy.setText("Posted By: " + jobDesc.getPostedBy());
        textTimestamp.setText(jobDesc.getTimestamp());
        textJobDescription.setText(jobDesc.getJobDescription());
        textPayRate.setText(formatAmount(jobDesc.getJobCost()));

        findViewById(R.id.btnSubmitProposal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastMessage("Your proposal has been submitted!");
            }
        });
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
    }
}
