package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityInvoicing extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityInvoicing.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoicing);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generate_invoice:
                finish();
                break;
        }
    }
}
