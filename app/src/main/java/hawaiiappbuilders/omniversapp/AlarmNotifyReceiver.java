package hawaiiappbuilders.omniversapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import hawaiiappbuilders.omniversapp.model.AlarmMeetDataManager;

public class AlarmNotifyReceiver extends BroadcastReceiver {

    MediaPlayer chinchinPlayer;
    private static int channelID = 0;
    int originalUserVolumn = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction()) ||
                "android.intent.action.LOCKED_BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
            // Boot Completed, Restore Alarms
            Log.e("qix", "Reboot!!!");
            AlarmMeetDataManager.getInstance(context).restoreAlarms(context);
        } else {
            // Normal Alarm
            // Play Sound
            try {
                // Save original volumn and update new volumn
                try {
                    AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    originalUserVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                    // Calc with percentage
                    int alarmVolumn = (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.7);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVolumn, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (chinchinPlayer != null && chinchinPlayer.isPlaying()) {
                    chinchinPlayer.stop();
                    chinchinPlayer.release();
                }
                chinchinPlayer = new MediaPlayer();

                String chinechineFile = "chinchin2.mp3";

                AssetFileDescriptor descriptor = context.getAssets().openFd(chinechineFile);
                chinchinPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
                chinchinPlayer.prepare();
                chinchinPlayer.setVolume(1f, 1f);
                chinchinPlayer.setLooping(false);
                chinchinPlayer.start();
                chinchinPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        try {
                            AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalUserVolumn, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }

            // Show Notification
            String title = intent.getStringExtra("title");
            String messageBody = intent.getStringExtra("msg");

            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, new Intent(),
                        PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, new Intent(),
                        PendingIntent.FLAG_IMMUTABLE);
            }

            String channelId = "HawaiiAppsAlarmMahalo";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_fcm_pm)
                            .setColor(ContextCompat.getColor(context,R.color.golden))
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "OmniVers App Alarm",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(channelID++, notificationBuilder.build());
        }

    }
}
