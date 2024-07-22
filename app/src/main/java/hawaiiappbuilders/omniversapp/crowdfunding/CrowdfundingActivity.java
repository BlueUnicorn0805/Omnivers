package hawaiiappbuilders.omniversapp.crowdfunding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class CrowdfundingActivity extends BaseActivity {

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdfunding);
        mContext = this;
        findViewById(R.id.btnAskForFunds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, HowMuchFundsActivity.class));
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
