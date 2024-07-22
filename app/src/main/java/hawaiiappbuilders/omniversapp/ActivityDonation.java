package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityDonation extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityDonation.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.donate_amount:
                finish();
                break;
        }
    }
}
