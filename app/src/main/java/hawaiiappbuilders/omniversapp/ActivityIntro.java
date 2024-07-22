package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityIntro extends BaseActivity {
    public static final String TAG = ActivityIntro.class.getSimpleName();
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introvideo);

        // appSettings.setUserId("1");
        // appSettings.setLoggedIn();
        // appSettings.logOut();
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

        VideoView videoView = (VideoView) findViewById(R.id.VideoView);
        videoView.setAlpha(0);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/raw/intro_new");
        videoView.setVideoURI(video);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoView.setAlpha(1);
                    }
                }, 200);
            }
        });

        isRunning = true;
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                /*gotoMainScreen();*/
            }
        });

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
            startActivity(new Intent(mContext, ActivityIFareDashBoard.class));
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