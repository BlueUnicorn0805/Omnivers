package hawaiiappbuilders.omniversapp.messaging;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BroadcastUtility {
    public static LocalBroadcastManager Manager(Context context) {
        return LocalBroadcastManager.getInstance(context);
    }

    public static void Register(Application context, BroadcastReceiver receiver, String intentName) {
        Manager(context).registerReceiver(receiver, new IntentFilter(intentName));
    }

    public static void Unregister(Application context, BroadcastReceiver receiver) {
        Manager(context).unregisterReceiver(receiver);
    }

}
