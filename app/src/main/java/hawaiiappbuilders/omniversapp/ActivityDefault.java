package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

/**
 * Created by RahulAnsari on 26-09-2018.
 */

public class ActivityDefault extends BaseActivity {
    public static final String TAG = ActivityDefault.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToLoginIfUserIsLoggedOut();
    }
}
