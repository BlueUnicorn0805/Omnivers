package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityServiceProvider extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityServiceProvider.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_provider);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.static_image_view:
                startActivity(new Intent(this,ActivityAddAppointment.class));
                break;
        }
    }
}
