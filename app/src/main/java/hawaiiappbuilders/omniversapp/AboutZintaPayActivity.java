package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class AboutZintaPayActivity extends BaseActivity implements View.OnClickListener {

    Handler mTimeOutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutzintapay);

        //edtEmail.setText("testfreelancerbd@gmail.com");
        //edtPassword.setText("abcdEF1234##");

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);

        mTimeOutHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                finish();
            }
        };

        mTimeOutHandler.sendEmptyMessageDelayed(0, 12);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnBack || viewId == R.id.btnClose) {
            finish();
            overridePendingTransition( R.anim.push_bottom_in, R.anim.push_top_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition( R.anim.push_bottom_in, R.anim.push_top_out);
    }
}
