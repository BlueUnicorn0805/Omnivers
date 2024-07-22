package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.GifView;

public class ActivityIntroGif extends BaseActivity {
    public static final String TAG = ActivityIntroGif.class.getSimpleName();
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introgif);

        // Firebase
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String newToken = task.getResult();
                    appSettings.setDeviceToken(newToken);
                }
            }
        });

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(getVersionName());

        isRunning = true;

        //gotoMainScreen();

        GifView gifView = findViewById(R.id.splashlogo);
        gifView.setGifResource(R.raw.ava_intro);
        gifView.play();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMainScreen();
            }
        }, SPLASH_DELAY_TIME);
    }

    private void gotoMainScreen() {
        if (!isRunning) {
            return;
        }

        if (appSettings.isLoggedIn()) {
            startActivity(new Intent(mContext, ActivityHomeMenu.class));
            finish();
        } else {
            startActivity(new Intent(mContext, ActivityLogin.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.isRunning) {
            this.isRunning = false;
        }
        finish();
    }
}

// https://developer.squareup.com/docs/in-app-payments-sdk/what-it-does