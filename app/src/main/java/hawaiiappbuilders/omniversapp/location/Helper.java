package hawaiiappbuilders.omniversapp.location;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.utils.K;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";

    public static final int MY_SOCKET_TIMEOUT_MS = 5000;

    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon) {
        return Helper.DIRECTION_API + originLat + "," + originLon + "&destination=" + destinationLat + "," + destinationLon + "&key=" + K.gKy(BuildConfig.D);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
