package hawaiiappbuilders.omniversapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.VolleySingleton;
import hawaiiappbuilders.omniversapp.model.NearBySearchItem;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.K;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivitySettings extends BaseActivity implements OnMapReadyCallback {
    public static final String TAG = ActivitySettings.class.getSimpleName();
    private AppSettings appSettings;

    private GoogleMap mMap;
    private GpsTracker gpsTracker;
    Marker currentPositionMarker;
    boolean bFirstShowMark = true;

    BitmapDescriptor userMarkerDescriptor;
    BitmapDescriptor restaurantMarkerDescriptor;

    private static final int INITIAL_ZOOM_LEVEL = 8;
    private static final int MAX_ZOOM_LEVEL = 14;

    boolean mRequestData = false;
    ArrayList<NearBySearchItem> mDataList = new ArrayList<>();
    private ArrayList<Marker> itemMarkers = new ArrayList<>();
    double dataCenterLat = 0;
    double dataCenterLon = 0;
    Handler mDataUpdateHandler;
    Runnable mDataUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMap != null) {
                CameraPosition mCameraPosition = mMap.getCameraPosition();
                LatLng location1 = mCameraPosition.target;
                LatLng location2 = new LatLng(dataCenterLat, dataCenterLon);
                if (getDistanceMiles(location1, location2) > 50) {
                    getNearByRestaurants(false, location1.latitude, location1.longitude);
                }
            }
        }
    };

    SeekBar seekbarListings;
    SeekBar seekbarDailySpecials;
    SeekBar seekbarMinTimeBetweens;
    SeekBar seekbarMaxWait;
    SeekBar seekbarPushVolume;

    SwitchCompat switchAllNotification;
    SwitchCompat switchListings;
    SwitchCompat switchDailySpecs;
    SwitchCompat switchMinTime;
    SwitchCompat switchMaxWait;
    SwitchCompat switchPushVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
    }

    private void initViews() {
        appSettings = new AppSettings(mContext);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userMarkerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location_pin);
        restaurantMarkerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_mark_restraurant);

        mDataUpdateHandler = new Handler();

        // Seekbar
        seekbarListings = findViewById(R.id.seekbarListings);
        seekbarDailySpecials = findViewById(R.id.seekbarDailySpecials);
        seekbarMinTimeBetweens = findViewById(R.id.seekbarMinTimeBetweens);
        seekbarMaxWait = findViewById(R.id.seekbarMaxWait);
        seekbarPushVolume = findViewById(R.id.seekbarPushVolume);

        seekbarListings.setOnSeekBarChangeListener(seekbarListener);
        seekbarDailySpecials.setOnSeekBarChangeListener(seekbarListener);
        seekbarMinTimeBetweens.setOnSeekBarChangeListener(seekbarListener);
        seekbarMaxWait.setOnSeekBarChangeListener(seekbarListener);
        seekbarPushVolume.setOnSeekBarChangeListener(seekbarListener);
        seekbarPushVolume.incrementProgressBy(50);

        // Init Values here
        //...

        // Switch
        switchAllNotification = findViewById(R.id.switchAllNotification);
        switchListings = findViewById(R.id.switchListings);
        switchDailySpecs = findViewById(R.id.switchDailySpecs);
        switchMinTime = findViewById(R.id.switchMinTime);
        switchMaxWait = findViewById(R.id.switchMaxWait);
        switchPushVolume = findViewById(R.id.switchPushVolume);

        // Init values here
        //...

        // Add Listeners
        switchListings.setOnCheckedChangeListener(switchChangedListener);
        switchDailySpecs.setOnCheckedChangeListener(switchChangedListener);
        switchMinTime.setOnCheckedChangeListener(switchChangedListener);
        switchMaxWait.setOnCheckedChangeListener(switchChangedListener);
        switchAllNotification.setOnCheckedChangeListener(switchChangedListener);
        switchPushVolume.setOnCheckedChangeListener(switchChangedListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                updateUserPosistion(location.getLatitude(), location.getLongitude());
            }
        });

        updateMyLocation();

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mDataUpdateHandler.removeCallbacks(mDataUpdateRunnable);
                mDataUpdateHandler.postDelayed(mDataUpdateRunnable, 800);
            }
        });
    }

    private void updateMyLocation() {
        if (checkLocationPermission()) {
            try{
                // Enable my location
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }

                // Show My Location
                if(getLocation()) {
                    updateUserPosistion(Double.parseDouble(userLat), Double.parseDouble(userLon));
                }
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataUpdateHandler != null) {
            mDataUpdateHandler.removeCallbacks(mDataUpdateRunnable);
        }
    }

    private void updateUserPosistion(double lat, double lon) {
        if (currentPositionMarker != null) {
            currentPositionMarker.remove();
        }

        if (mMap == null)
            return;

        LatLng latLng = new LatLng(lat, lon);
        currentPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("You're here!")
                .icon(userMarkerDescriptor));

        if (bFirstShowMark) {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM_LEVEL);
            mMap.animateCamera(location, 1000, null);

            bFirstShowMark = false;
        } else {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target, mMap.getCameraPosition().zoom);
            mMap.animateCamera(location, 1000, null);
        }

        if (!mRequestData) {
            mRequestData = true;
            // Start to load Restaurants data
            getNearByRestaurants(true, latLng.latitude, latLng.longitude);
        }
    }

    private static final byte[] baseUrl = {0x41, 0x49, 0x7a, 0x61, 0x53, 0x79, 0x43, 0x59, 0x4f, 0x63, 0x41, 0x39, 0x35, 0x34, 0x69, 0x74, 0x67, 0x73, 0x62, 0x4d, 0x5f, 0x75, 0x63, 0x33, 0x4a, 0x4b, 0x76, 0x74, 0x58, 0x30, 0x55, 0x49, 0x54, 0x67, 0x5f, 0x34, 0x71, 0x64, 0x38};
    public static String getHashCode() {
        String hashString = "";
        try {
            hashString = new String(baseUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hashString;
    }

    private void getNearByRestaurants(final boolean bShowProgressDlg, final double lat, final double lon) {

        if (bShowProgressDlg) {
            showProgressDlg(mContext, "Please wait while loading data...");
        }

        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + K.gKy(BuildConfig.P) +
                String.valueOf(lat) + "," + String.valueOf(lon) + "&radius=3000000&type=restaurant&key=" + getHashCode();

        HashMap<String, String> params = new HashMap<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (bShowProgressDlg) {
                    hideProgressDlg();
                }

                // Save Current data geolocation
                dataCenterLat = lat;
                dataCenterLon = lon;

                try {
                    JSONObject result = new JSONObject(response);

                    if (result.getString("status").equalsIgnoreCase("OK")) {
                        JSONArray jsonArray = new JSONArray(result.getString("results"));
                        mDataList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject place = jsonArray.getJSONObject(i);

                            NearBySearchItem newItem = new NearBySearchItem();

                            newItem.id = place.optString("id");
                            Log.e("xls", "id:" + newItem.id);

                            newItem.place_id = place.optString("place_id");
                            Log.e("xls", "place_id:" + newItem.place_id);

                            if (place.has("rating") && !place.isNull("rating")) {
                                newItem.rating = (float) place.getDouble("rating");
                            }
                            Log.e("xls", "rating:" + newItem.rating);

                            newItem.name = "Restaurant";
                            if (!place.isNull("name")) {
                                newItem.name = place.getString("name");
                            }
                            Log.e("xls", "name:" + newItem.name);

                            newItem.vicinity = "Vicinity";
                            if (!place.isNull("vicinity")) {
                                newItem.vicinity = place.getString("vicinity");
                            }
                            Log.e("xls", "vicinity:" + newItem.vicinity);

                            newItem.latitude = place.getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            Log.e("xls", "latitude:" + newItem.latitude);

                            newItem.longitude = place.getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            Log.e("xls", "longitude:" + newItem.longitude);

                            newItem.reference = place.getString("reference");
                            Log.e("xls", "reference:" + newItem.reference);

                            newItem.icon = place.getString("icon");

                            String photoReference = "";
                            if (place.has("photos") && !place.isNull("photos")) {
                                JSONArray photoArray = place.getJSONArray("photos");
                                if (photoArray.length() > 0) {
                                    photoReference = photoArray.getJSONObject(0).getString("photo_reference");
                                }
                            }

                            newItem.imageUrl = "";
                            if (!TextUtils.isEmpty(photoReference)) {
                                newItem.imageUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference=%s&key=%s", photoReference, getHashCode());
                            }
                            Log.e("xls", "imageUrl:" + newItem.imageUrl);
                            Log.e("xls", "-------------------------------------------------------------------------------------------");

                            mDataList.add(newItem);
                        }

                        addMarkers();
                    } else if (result.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                        showMessage(mContext, "No data in your area.");
                    }
                } catch (JSONException e) {
                    if (bShowProgressDlg) {
                        hideProgressDlg();
                    }
                    showMessage(mContext, e.getLocalizedMessage());
                    e.printStackTrace();
                    Log.e("searchrestaurant", "parseLocationResult: Error=" + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (bShowProgressDlg) {
                    hideProgressDlg();
                }
                baseFunctions.handleVolleyError(mContext, error, TAG, "nearbysearch");
                networkErrorHandle(mContext, error);
            }
        });

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    private void addMarkers() {

        // Remove old markers
        for (Marker marker : itemMarkers) {
            marker.remove();
        }

        itemMarkers.clear();

        for (NearBySearchItem nearByItem : mDataList) {
            LatLng latLng = new LatLng(nearByItem.latitude, nearByItem.longitude);
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(String.format("%s", nearByItem.name))
                    .icon(restaurantMarkerDescriptor));

            marker.setTag(nearByItem);
            itemMarkers.add(marker);
        }
    }

    private double getDistanceMiles(LatLng fro, LatLng to) {
        final double MILES_PER_KILO = 0.621371;

        double distance = calculationByDistance(fro.latitude, fro.longitude, to.latitude, to.longitude);
        return distance * MILES_PER_KILO;
    }

    // Haversine Distance Calulator
    public double calculationByDistance(double initialLat, double initialLong,
                                        double finalLat, double finalLong) {
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat - initialLat);
        double dLon = toRadians(finalLong - initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI / 180);
    }


    SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (seekBar == seekbarListings) {
                if (mMap != null) {
                    int zoomLevelFactor = seekbarListings.getProgress();
                    float kZoom = (MAX_ZOOM_LEVEL - INITIAL_ZOOM_LEVEL) / ((float)seekbarListings.getMax());
                    float zoomLevel = INITIAL_ZOOM_LEVEL + kZoom * zoomLevelFactor;
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target, zoomLevel);
                    mMap.animateCamera(location, 100, null);
                }
            } else if(seekBar == seekbarDailySpecials) {
                if (mMap != null) {
                    int distanceLevelFactor = seekbarDailySpecials.getProgress();
                    int rangeMiles = 13 - distanceLevelFactor; //  0 => 13 Miles, 12 => 1 Mile
                    double rangeMeters = rangeMiles * 20;//1609.344 / 0.5;
                    LatLngBounds bounds = toBounds(mMap.getCameraPosition().target, rangeMeters);
                    int routePadding = 0;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, routePadding));
                }
            } else if(seekBar == seekbarMinTimeBetweens) {

            } else if(seekBar == seekbarMaxWait) {

            } else if(seekBar == seekbarPushVolume) {

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    CompoundButton.OnCheckedChangeListener switchChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton.getId() == R.id.switchListings) {
                seekbarListings.setEnabled(b);
            } else if(compoundButton.getId() == R.id.switchDailySpecs) {
                seekbarDailySpecials.setEnabled(b);
            } else if(compoundButton.getId() == R.id.switchMinTime) {
                seekbarMinTimeBetweens.setEnabled(b);
            } else if(compoundButton.getId() == R.id.switchMaxWait) {
                seekbarMaxWait.setEnabled(b);
            } else if(compoundButton.getId() == R.id.switchPushVolume) {
                seekbarPushVolume.setEnabled(b);
            } else if(compoundButton.getId() == R.id.switchAllNotification) {
                switchListings.setChecked(b);
                switchDailySpecs.setChecked(b);
                switchMinTime.setChecked(b);
                switchMaxWait.setChecked(b);
                seekbarPushVolume.setEnabled(b);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateMyLocation();
                } else {
                    showMessage(mContext, "You need to grant permission");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GpsTracker.REQUEST_LOCATION) {
            if (checkLocationPermission()) {
                try{
                    mMap.setMyLocationEnabled(true);
                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}