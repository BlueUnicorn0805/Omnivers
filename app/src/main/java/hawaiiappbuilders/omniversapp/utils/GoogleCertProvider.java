package hawaiiappbuilders.omniversapp.utils;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

public class GoogleCertProvider {
    public static void install(Context context) {
        try {
            ProviderInstaller.installIfNeeded(context);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();

            HttpsTrustManager.allowAllSSL();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();

            HttpsTrustManager.allowAllSSL();
        }
    }
}
