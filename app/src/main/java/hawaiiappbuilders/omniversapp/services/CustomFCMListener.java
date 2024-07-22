package hawaiiappbuilders.omniversapp.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

import hawaiiappbuilders.omniversapp.global.AppSettings;

public class CustomFCMListener extends Service {
    public int counter = 0;

    FCMReceiver myReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
     // startMyOwnForeground();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getPackageName() + "com.google.android.c2dm.intent.RECEIVE");
        myReceiver = new FCMReceiver();
        registerReceiver(myReceiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        channelName,
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("App is running in background")
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground((int) System.currentTimeMillis(), notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        restartService();
    }


    private Timer timer;

    public void startTimer() {
        timer = new Timer();
        // Initialize the TimerTask's job
        TimerTask timerTask = new TimerTask() {
            public void run() {
                // Log.i("FCMListen", "=========  " + (counter++));
            }
        };
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000);
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // This is called if the service is currently running and
    // the user has removed a task that comes from the service's application.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(getClass().getName(), "App just got removed from Recents!");

        AppSettings appSettings = new AppSettings(this);
        appSettings.setIsAppKilled(true);
        // restartService();
    }

    public void restartService() {
        // stopTimerTask();
        unregisterReceiver(myReceiver);
        Log.i("EXIT", "onDestroy");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("RestartService");
        broadcastIntent.setClass(this, RestartService.class);
        // this.sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}