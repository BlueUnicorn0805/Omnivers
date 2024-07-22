package hawaiiappbuilders.omniversapp.depositcheck.checks;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;


import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityScanCheck extends BaseActivity {

    private TextView textCheckFrontBack;
    private ImageView viewBoundingBox;
    private ImageView closeBtn;

    Context context;

    private int mode = 1; // 1- front, 2- back, 3- review

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scanner);
        mode = 1;
        context = this;
        closeBtn = (ImageView) findViewById(R.id.btnClose);
        textCheckFrontBack = findViewById(R.id.textCheckFrontBack);
        viewBoundingBox = findViewById(R.id.viewBoundingBox);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewBoundingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == 1) {
                    mode = 2;
                    textCheckFrontBack.setText("BACK");
                } else if(mode == 2) {
                    // todo: extract check details
                    Check check1 = new Check();
                    check1.setTransactionId(101010);
                    check1.setTransactionDate("03-25-2023");
                    check1.setName("Jane Doe");
                    check1.setAmount(124335032.50);
                    check1.setBankName("Your Financial Institution");
                    check1.setAccountNumber("123124");
                    check1.setCheckNumber("09000348");
                    check1.setRoutingNumber("1369");
                    check1.setFrontImage("");
                    check1.setBackImage("");
                    Intent intent = new Intent(context, ActivityReviewCheck.class);
                    intent.putExtra("check", check1);
                    intent.putExtra("mode", 2);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }
}
