package hawaiiappbuilders.omniversapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityDoorAccess extends BaseActivity {
    public static final String TAG = ActivityDoorAccess.class.getSimpleName();
    Context mContext;
    ImageView keyLock;
    ImageView keyUnlock;
    TextView textTap;

    CardView pulse;

    TextView textWelcome;
    TextView textHotelName;

    int access;

    public static final int ACCESS_CAR = 1;
    public static final int ACCESS_HOTEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_access);
        mContext = this;
        access = ACCESS_HOTEL;
        textTap = findViewById(R.id.textTap);
        textWelcome = findViewById(R.id.textWelcome);
        textHotelName = findViewById(R.id.textHotelName);
        pulse = findViewById(R.id.cv_round);
        keyLock = findViewById(R.id.keyLock);
        keyLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTheShake(keyLock, keyUnlock, "Lock");
            }
        });
        keyUnlock = findViewById(R.id.keyUnlock);
        keyUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTheShake(keyUnlock, keyLock, "Unlock");
            }
        });

        if (getIntent().getExtras() != null) {
            access = getIntent().getExtras().getInt("access");
        }

        if (access == ACCESS_CAR) {
            // TODO:  Think of a better title
            textWelcome.setText("This feature is to provide you a secure way to access your car door");
            textHotelName.setVisibility(View.GONE);
            textTap.setText("Tap to Unlock car door");
        }
    }

    public void doTheShake(ImageView keyfrom, ImageView keyto, String text) {
        //pulse.setVisibility(View.VISIBLE);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pulse.animate().scaleYBy(50f).scaleXBy(50f);
            }
        }, 1000);*/

        Animation bounceAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shake_error);
        keyfrom.startAnimation(bounceAnimation);
        keyfrom.postDelayed(new Runnable() {
            @Override
            public void run() {
                keyfrom.setVisibility(View.GONE);
                keyto.setVisibility(View.VISIBLE);
                if (access == ACCESS_CAR) {
                    textTap.setText(String.format("Tap to %s car door", text));
                } else {
                    textTap.setText(String.format("Tap to %s", text));
                }
                //pulse.setVisibility(View.GONE);
            }
        }, 5000);

    }
}
