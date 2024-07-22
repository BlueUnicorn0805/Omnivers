package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityFundTransfer extends BaseActivity {
    public static final String TAG = ActivityFundTransfer.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_hours_worked);
    }
}
