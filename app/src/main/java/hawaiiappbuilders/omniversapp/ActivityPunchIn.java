package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityPunchIn extends BaseActivity {

    private static final String TAG = ActivityPunchIn.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_punch_in);
    }
}
