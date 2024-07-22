package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class PushMoneyActivity extends BaseActivity {

    //TextView tvParams;
    //TextView tvFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushmoney);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_money);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();

        /* tvParams = findViewById(R.id.tvParams);
        tvFrom = findViewById(R.id.tvFrom);
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        // Show Params and Values
        tvParams.setText(String.format("Message : %s", message));
        tvFrom.setText("From : " + title); */
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
