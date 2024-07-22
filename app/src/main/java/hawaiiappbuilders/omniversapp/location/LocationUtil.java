package hawaiiappbuilders.omniversapp.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import hawaiiappbuilders.omniversapp.R;

import java.lang.ref.WeakReference;

/**
 * Created by Ankush Grover(ankushgrover02@gmail.com) on 25/4/18.
 */
public class LocationUtil {


    private static final int LOCATION_SETTINGS_REQUEST_CODE = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private final Geocoder mGeocoder;
    private boolean mIsLocationCallbackAdded;
    private WeakReference<AppCompatActivity> mActivity;
    private WeakReference<LocationListener> mListener;
    /**
     * if false: City/locality is returned.
     * if true: Complete Address is returned.
     */
    private boolean mIsAddressRequired;
    private SharedPreferenceManager mPreferenceManager;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    public LocationUtil(AppCompatActivity activity, SharedPreferenceManager sharedPreferenceManager) {
        mActivity = new WeakReference<>(activity);


        mPreferenceManager = sharedPreferenceManager;
        mGeocoder = new Geocoder(activity);

        mIsLocationCallbackAdded = false;

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    new FetchPlaceTask(mGeocoder, latLng, mIsAddressRequired, new FetchPlaceTask.AddressParseListener() {
                        @Override
                        public void onAddressParsed(String addressString, Address address) {
                            if (mListener.get() != null && mActivity.get() != null && !mActivity.get().isFinishing()) {
                                mListener.get().onLocationReceived(latLng, addressString, address);
                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                mIsLocationCallbackAdded = false;
                            }
                        }
                    });
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity.get());
    }

    /**
     * Method to fetch location for sign up. This type of request returns accurate location with
     * city/locality string.
     */
    public void fetchApproximateLocation(LocationListener listener) {

        mListener = new WeakReference<>(listener);

        String neverAskMessage = "";
        if (mActivity.get() != null) {
            neverAskMessage = mActivity.get().getString(R.string.msg_request_location_message);
        }
        String rationaleMessage = mActivity.get().getString(R.string.msg_never_ask_sharing);

        fetchLocation(neverAskMessage, rationaleMessage, false);

    }

    /**
     * Method to fetch location for creating blood request. This type of request returns a really
     * precise location with complete address.
     */
    public void fetchPreciseLocation(LocationListener listener) {

        mListener = new WeakReference<>(listener);

        String neverAskMessage = mActivity.get().getString(R.string.msg_never_ask_sharing);
        String rationaleMessage = mActivity.get().getString(R.string.msg_request_location_message);

        fetchLocation(neverAskMessage, rationaleMessage, true);

    }

    public void fetchPlaceName(LocationListener listener, final LatLng location) {
        mListener = new WeakReference<>(listener);

        new FetchPlaceTask(mGeocoder, location, true, new FetchPlaceTask.AddressParseListener() {
            @Override
            public void onAddressParsed(String addressString, Address address) {
                mListener.get().onLocationReceived(location, addressString, address);
            }
        });
    }

    public void fetchLocation(String neverAskMessage, String rationaleMessage, boolean isCompleteAddressRequired) {
        mIsAddressRequired = isCompleteAddressRequired;
        //Permission is granted.
        if (AppPermissionsUtil.checkIfLocationPermissionIsGiven(mActivity.get()))
            createLocationRequest();
        else {
            // Never Ask scenario
            if (mPreferenceManager.getBoolean(SharedPrefConstants.IS_LOCATION_PERMISSION_DIALOG_SHOWN)
                    && !AppPermissionsUtil.shouldShowPermissionRationaleForLocation(mActivity.get())) {


                new AlertDialog.Builder(mActivity.get())
                        .setTitle(R.string.msg_location_permission_title)
                        .setMessage(neverAskMessage)
                        .setPositiveButton(mActivity.get().getString(R.string.txt_open_settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", mActivity.get().getPackageName(), null);
                                intent.setData(uri);
                                mActivity.get().startActivity(intent);
                            }
                        }).setNegativeButton(mActivity.get().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

            } else if (AppPermissionsUtil.shouldShowPermissionRationaleForLocation(mActivity.get())) {
                // Permission has been denied once.
                new AlertDialog.Builder(mActivity.get())
                        .setTitle(R.string.msg_location_permission_title)
                        .setMessage(rationaleMessage)
                        .setPositiveButton(mActivity.get().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AppPermissionsUtil.requestForLocationPermission(mActivity.get(), LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton(mActivity.get().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            } else //Ask for permission
                AppPermissionsUtil.requestForLocationPermission(mActivity.get(), LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(mActivity.get());
        settingsClient.checkLocationSettings(builder.build()).
                addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        getLocation();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.

                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;

                            try {
                                resolvable.startResolutionForResult(mActivity.get(), LOCATION_SETTINGS_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e1) {
                                e1.printStackTrace();
                            } catch (Exception error) {
                                error.printStackTrace();
                            }
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        if (!mIsLocationCallbackAdded) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            mIsLocationCallbackAdded = true;
        }
    }

    public void onResolutionResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            getLocation();
    }


    public void onPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            mPreferenceManager.put(SharedPrefConstants.IS_LOCATION_PERMISSION_DIALOG_SHOWN, true);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                createLocationRequest();
        }
    }

    public void onDestroy() {
        if (mIsLocationCallbackAdded) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mIsLocationCallbackAdded = false;
        }
    }

    public interface LocationListener {
        void onLocationReceived(@NonNull LatLng location, @NonNull String addressString, Address address);
    }

}
