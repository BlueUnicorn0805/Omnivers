package hawaiiappbuilders.omniversapp.global;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class LogoutUserBroadcastReceiver extends BroadcastReceiver {

    private String TAG = LogoutUserBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        /*AppSettings appSettings = new AppSettings(context);
        appSettings.logOut();
        if (isAppOnForeground(context)) {
            Intent startActivityIntent = new Intent();
            startActivityIntent.putExtra("LOGOUT_USER",true);
            startActivityIntent.setClassName("com.ver1.dot", "com.ver1.dot.ActivityLogin");
            startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(startActivityIntent);
        }*/
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}