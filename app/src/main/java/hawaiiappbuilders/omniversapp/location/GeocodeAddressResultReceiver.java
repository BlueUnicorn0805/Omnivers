package hawaiiappbuilders.omniversapp.location;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class GeocodeAddressResultReceiver extends ResultReceiver {

    OnReceiveGeocodeListener mReceiveGeocodeListener;

    public GeocodeAddressResultReceiver(Handler handler, OnReceiveGeocodeListener geoResultListener) {
        super(handler);
        mReceiveGeocodeListener = geoResultListener;
    }

    @Override
    protected void onReceiveResult(int resultCode, final Bundle resultData) {
        if (mReceiveGeocodeListener != null) {
            mReceiveGeocodeListener.onReceiveResult(resultCode, resultData);
        }
    }

    public interface OnReceiveGeocodeListener {

        // This call back is not in the UI thread.
        void onReceiveResult(int resultCode, final Bundle resultData);
    }
}
