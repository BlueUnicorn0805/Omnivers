package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityMyWorkSmallBiz extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityMyWorkSmallBiz.class.getSimpleName();
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_myworksmallbiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("My Work");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.btnSetupBiz).setOnClickListener(this);
        findViewById(R.id.btnStartNewProp).setOnClickListener(this);
        findViewById(R.id.btnAcceptProposal).setOnClickListener(this);
        findViewById(R.id.btnStartJob).setOnClickListener(this);
        findViewById(R.id.btnCreateInvoice).setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.btnSetupBiz) {
            startActivity(new Intent(mContext, ActivityBizInfo.class));
        } else if (viewID == R.id.btnStartNewProp) {
            showToastMessage("Not Setup");
        }else if (viewID == R.id.btnAcceptProposal) {
            showToastMessage("Not Setup");
        }else if (viewID == R.id.btnStartJob) {
            showToastMessage("Not Setup");
        }else if (viewID == R.id.btnCreateInvoice) {
            showToastMessage("Not Setup");
        }
    }
}
