package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.milestone.Professional;
import hawaiiappbuilders.omniversapp.milestone.ProfessionalDetailActivity;

public class ActivityConfirmMilestoneWork extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityConfirmMilestoneWork.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm_milestone_work);

        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnYes).setOnClickListener(this);
        findViewById(R.id.btnNo).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnYes) {
            /*startActivity(new Intent(mContext, MilestoneActivity.class));*/
            //startActivity(new Intent(mContext, ProfessionalsActivity.class));
            Professional professional1 = new Professional();
            professional1.setStatus("Available");
            professional1.setName("John Doe");
            professional1.setTitle("Researcher/Writer");
            professional1.setReviews(239);
            professional1.setEmail("johndoe@z99.io");
            professional1.setServicesOffered("SERVICES I OFFER\n1 RESEARCH WRITER\n2 Journalistic Writing\n\n3 times a week availability");
            professional1.setStartingPayment(90.50);
            Intent intent = new Intent(mContext, ProfessionalDetailActivity.class);
            intent.putExtra("professional", professional1);
            startActivity(intent);

            finish();
        } else if (viewId == R.id.btnNo) {
            startActivity(new Intent(mContext, ActivityWorkContact.class));
            finish();
        }
    }
}
