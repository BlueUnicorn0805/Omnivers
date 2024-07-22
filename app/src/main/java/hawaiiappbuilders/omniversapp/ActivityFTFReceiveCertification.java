package hawaiiappbuilders.omniversapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

import java.util.Calendar;

public class ActivityFTFReceiveCertification extends BaseActivity implements View.OnClickListener {
    public static final String TAG = ActivityFTFReceiveCertification.class.getSimpleName();
    TextView tvDate;
    TextView tvTime;
    TextView ftf_fnln;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ftf_receive_certification);
        String scannedData = getIntent().getStringExtra("SCANNED_DATA");

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        ftf_fnln = findViewById(R.id.ftf_fnln);

        Calendar calendar = Calendar.getInstance();
        tvDate.setText(DateUtil.toStringFormat_22(calendar.getTime()));
        tvTime.setText(DateUtil.toStringFormat_10(calendar.getTime()));

        String userName = appSettings.getFN() + " " + appSettings.getLN();
        ftf_fnln.setText(userName.trim());

        /*try {
            JSONObject jsonObject = new JSONObject(scannedData);

            String userName = jsonObject.optString("fn", "") + " " + jsonObject.optString("ln", "");
            ftf_fnln.setText(userName.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.ivCertBronze).setOnClickListener(this);
        findViewById(R.id.ivCertSilver).setOnClickListener(this);
        findViewById(R.id.ivCertGold).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ivCertBronze) {
            hitServer(FITSERVER_CERTBRONZE, "", 0);
        } else if (viewId == R.id.ivCertSilver) {
            hitServer(FITSERVER_CERTSILVER, "", 0);
        } else if (viewId == R.id.ivCertGold) {
            hitServer(FITSERVER_CERTGOLD, "", 0);
        }
    }
}


