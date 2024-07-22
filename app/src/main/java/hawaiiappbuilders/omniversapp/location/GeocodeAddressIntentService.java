package hawaiiappbuilders.omniversapp.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocodeAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;
    private static final String TAG = "GEO_ADDY_SERVICE";

    public GeocodeAddressIntentService() {
        super("GeocodeAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        List<Address> addresses = null;

        int fetchType = intent.getIntExtra(Constants.FETCH_TYPE_EXTRA, 0);
        Log.e(TAG, "fetchType == " + fetchType);

        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, 0);
        Log.e(TAG, "requestCode == " + requestCode);

        if (fetchType == Constants.USE_ADDRESS_NAME) {
            String name = intent.getStringExtra(Constants.LOCATION_NAME_DATA_EXTRA);
            try {
                //addresses = geocoder.getFromLocationName("5306 Cracker Barrel Circle Denver, Co, 80917"/*name*/, 1);
                addresses = geocoder.getFromLocationName(name, 1);
            } catch (IOException e) {
                errorMessage = "Service not available";
                Log.e(TAG, errorMessage, e);
            }
        } else if (fetchType == Constants.USE_ADDRESS_LOCATION) {
            double latitude = intent.getDoubleExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA, 0);
            double longitude = intent.getDoubleExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA, 0);

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {
                errorMessage = "Service Not Available";
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }
        } else {
            errorMessage = "Unknown Type";
            Log.e(TAG, errorMessage);
        }

        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Not Found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(requestCode, Constants.FAILURE_RESULT, errorMessage, null);
        } else {
            for (Address address : addresses) {
                String outputAddress = "";
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " --- " + address.getAddressLine(i);
                }
                Log.e(TAG, outputAddress);
            }
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Address Found");
            deliverResultToReceiver(requestCode, Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments), address);
        }
    }

    private void deliverResultToReceiver(int requestcode, int resultCode, String message, Address address) {
        // Put result and send it
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.REQUEST_CODE, requestcode);
        bundle.putParcelable(Constants.RESULT_ADDRESS, address);
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }

}
