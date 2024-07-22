package hawaiiappbuilders.omniversapp.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationMonitoring extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();

    public LocationMonitoring() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        Log.d("TAG19", "Connected to Google API");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
            if (location != null)
            {
                Log.d("TAG19", String.valueOf(location.getLatitude()));
                Log.d("TAG19", String.valueOf(location.getLongitude()));
            }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(5);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();


        return START_STICKY;
    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d("TAG19", "Sending info...");

//        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
//        intent.putExtra(EXTRA_LATITUDE, lat);
//        intent.putExtra(EXTRA_LONGITUDE, lng);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
