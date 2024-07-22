package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class AboutZintaActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutzinta);

        //edtEmail.setText("testfreelancerbd@gmail.com");
        //edtPassword.setText("abcdEF1234##");

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnClose).setOnClickListener(this);
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
