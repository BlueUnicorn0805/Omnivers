package hawaiiappbuilders.omniversapp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

import java.util.Calendar;
import java.util.Date;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class ActivityAccessibleDoors extends BaseActivity {

    Context mContext;
    ConstraintLayout suitRoom;
    ConstraintLayout pool;
    ConstraintLayout elevators;
    ConstraintLayout restrooms;
    ConstraintLayout fitnessRoom;

    ImageView ivRight;

    ImageView ivLeft;

    TextView tvCheckIn;

    TextView tvCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessible_rooms);
        mContext = this;

        suitRoom = findViewById(R.id.constraintLayout6);
        suitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHotelKeyAccess();
            }
        });

        pool = findViewById(R.id.constraintLayout7);
        pool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHotelKeyAccess();
            }
        });

        elevators = findViewById(R.id.constraintLayout8);
        elevators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHotelKeyAccess();
            }
        });


        restrooms = findViewById(R.id.constraintLayout9);
        restrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHotelKeyAccess();
            }
        });

        fitnessRoom = findViewById(R.id.constraintLayout10);
        fitnessRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHotelKeyAccess();
            }
        });

        ivLeft = findViewById(R.id.ivLeft);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions(mContext, PERMISSION_REQUEST_PHONE_STRING, false, 109)) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    phoneIntent.setData(Uri.parse(String.format("tel:%s", "8084507683")));
                    startActivity(phoneIntent);
                }
            }
        });

        ivRight = findViewById(R.id.ivRight);
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ViewAddressInMapActivity.class));
            }
        });

        // CHECK-IN\nJun 20 2022 06:30
        Calendar calendar = Calendar.getInstance();
        Date checkInDate = new Date(calendar.getTimeInMillis());
        String checkInDateStr = DateUtil.toStringFormat_31(checkInDate);
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        Date checkOutDate = new Date(calendar.getTimeInMillis());
        String checkOutDateStr = DateUtil.toStringFormat_31(checkOutDate);

        tvCheckIn = findViewById(R.id.tvCheckIn);
        tvCheckOut = findViewById(R.id.tvCheckOut);

        tvCheckIn.setText(HtmlCompat.fromHtml(String.format("<b>CHECK-IN<b><br>%s", checkInDateStr), HtmlCompat.FROM_HTML_MODE_COMPACT));
        tvCheckOut.setText(HtmlCompat.fromHtml(String.format("<b>CHECK-OUT<b><br>%s", checkOutDateStr), HtmlCompat.FROM_HTML_MODE_COMPACT));
    }

    private void openHotelKeyAccess() {
        startActivity(new Intent(mContext, ActivityDoorAccess.class));
    }
}
