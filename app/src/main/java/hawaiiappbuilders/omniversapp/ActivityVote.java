package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import android.view.View;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityVote extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        init();
        initClicks();

    }

    private void init() {

    }

    private void initClicks() {
        findViewById(R.id.btnToolbarHome).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.btnToolbarHome:
            case R.id.btnBack:
                finish();
                break;
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
                msg("Thanks for your vote", v -> finish());
                break;
        }
    }
}
