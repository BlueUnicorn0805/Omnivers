package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityFTFRequest extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ActivityFTFRequest.class.getSimpleName();
    private static final int PERMISSION_REQUESTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftf_request);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ftf_amt:
                break;
            case R.id.ftf_cancel:
                finish();
                break;
            case R.id.ftf_submit:
                break;
        }
    }
}
