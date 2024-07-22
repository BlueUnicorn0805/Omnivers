package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityFTFReceiveSuccessfull extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityFTFReceiveSuccessfull.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_success);
        TextView l1 = (TextView) findViewById(R.id.l1);
        TextView l2 = (TextView) findViewById(R.id.l2);
        TextView l3 = (TextView) findViewById(R.id.l3);

        l1.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);

        Button dashBoard = (Button) findViewById(R.id.payment_success_done);
        dashBoard.setText("Dashboard");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_success_done:
                finish();
                break;
        }
    }
}
