package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ActivityTransIntro extends BaseActivity {
    public static final String TAG = ActivityTransIntro.class.getSimpleName();
    Toolbar toolbar;
    VideoView videoView;
    ImageView imageView;
    Button btnGoTo;

    ImageLoader imageLoader;
    DisplayImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferintro);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MahaloPay Account Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Goto Button
        btnGoTo = findViewById(R.id.btnGoTo);

        videoView = (VideoView) findViewById(R.id.VideoView);

        // Local video
        videoView.setAlpha(0);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/raw/zintaintro");
        //videoView.setVideoURI(video);
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

        // Remote Video
        // videoView.setVideoPath("https://yestaurants.com/ZintaIntro.mp4");
        // videoView.start();
        // Or
        // videoView.setVideoURI(Uri.parse("https://yestaurants.com/ZintaIntro.mp4"));
        // videoView.requestFocus();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                /*gotoMainScreen();*/
                btnGoTo.setVisibility(View.VISIBLE);
            }
        });

        imageView = findViewById(R.id.imageView);
        // Initialize the ImageLoader
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.loadImage("https://yestaurants.com/ZintaIntro.jpg", new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });


        btnGoTo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ActivityBank.class));
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar.setVisibility(View.VISIBLE);
        }*/
    }
}