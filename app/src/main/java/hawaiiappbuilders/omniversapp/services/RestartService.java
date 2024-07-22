package hawaiiappbuilders.omniversapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import hawaiiappbuilders.omniversapp.global.AppSettings;

public class RestartService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        AppSettings appSettings = new AppSettings(context);
        appSettings.setIsAppKilled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // context.startForegroundService(new Intent(context, CustomFCMListener.class));
        } else {
            // context.startService(new Intent(context, CustomFCMListener.class));
        }
    }
}