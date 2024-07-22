package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.adapters.CustomContactList;
import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.messaging.OnGetTokenListener;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;

public class MyCarActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    FusedLocationProviderClient mFusedLocationProviderClient;
    boolean mLocationPermissionGranted;
    GoogleMap mMap;
    Location mLastKnownLocation;
    Location mCarLocation;
    int DEFAULT_ZOOM = 18;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Marker carMarker;

    Button btnShareLocation;

    // for Contacts List
    MessageDataManager dm;
    CustomContactList contactsListAdapter;
    CustomContactModel selectedContact;
    ArrayList<CustomContactModel> contactModels;
    private ArrayList<ContactInfo> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Find My Car");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dm = new MessageDataManager(mContext);
        Button btnSetLocation = (Button) findViewById(R.id.btnSetLocation);

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLastKnownLocation != null) {
                    SharedPreferences settings = getSharedPreferences("CarLocation", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putFloat("lat", (float) mLastKnownLocation.getLatitude());
                    editor.putFloat("long", (float) mLastKnownLocation.getLongitude());
                    editor.commit();
                    if (carMarker != null) {
                        carMarker.remove();
                    }
                    MarkerOptions mo = new MarkerOptions()
                            .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                            .title("My Car");
                    carMarker = mMap.addMarker(mo);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }
            }
        });

        Button btnValetCar = (Button) findViewById(R.id.btnValetCar);
        btnValetCar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showToastMessage("Sent, Your car will be waiting for you at front door");
                //postRateDialog();
            }
        });

        btnShareLocation = findViewById(R.id.btnShareCarLocation);
        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactsList();
            }
        });

        if (TextUtils.isEmpty(appSettings.getValetOrder())) {
            btnValetCar.setEnabled(false);
        }

        Button btnScanValet = (Button) findViewById(R.id.btnScanValet);
        btnScanValet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent qrintent = new Intent(mContext, QRCodeActivity.class);
                qrintent.putExtra("scan_valet", true);
                startActivity(qrintent);
            }
        });

        Button btnDirectionsToMyCar = (Button) findViewById(R.id.btnDirectionsToMyCar);
        btnDirectionsToMyCar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLastKnownLocation != null) {
                    //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=w", ((int) (mLastKnownLocation.getLatitude() * 100)) / 100.f, mLastKnownLocation.getLongitude()));

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    //if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                    //}
                }
            }
        });

        SharedPreferences carLocation = getSharedPreferences("CarLocation", 0);
        if (carLocation != null) {
            mCarLocation = new Location("");
            mCarLocation.setLatitude(carLocation.getFloat("lat", 0));
            mCarLocation.setLongitude(carLocation.getFloat("long", 0));
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.carMap);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //hitServer(FITSERVER_SideWalk, 10);

    }

    private void getContactsList() {
        contactList = dm.getAlLContacts(0);
        contactModels = new ArrayList<>();

        // populate
        for (int i = 0; i < contactList.size(); i++) {
            CustomContactModel model = new CustomContactModel();
            model.isSelected = false;
            model.id = contactList.get(i).getId();
            model.type = 1;
            if (contactList.get(i).getCo() != null) {
                model.company = contactList.get(i).getCo();
            } else {
                model.company = "";
            }
            if (contactList.get(i).getName() != null) {
                model.name = contactList.get(i).getName();
            } else {
                model.name = "";
            }
            if (contactList.get(i).getFname() != null) {
                model.fname = contactList.get(i).getFname();
            } else {
                model.fname = "";
            }

            if (contactList.get(i).getLname() != null) {
                model.lname = contactList.get(i).getLname();
            } else {
                model.lname = "";
            }

            if (contactList.get(i).getEmail() != null) {
                model.email = contactList.get(i).getEmail();
            } else {
                model.email = "";
            }
            if (contactList.get(i).getCp() != null) {
                model.phone = contactList.get(i).getCp();
            } else {
                model.phone = "";
            }
            if (contactList.get(i).getWp() != null) {
                model.wp = contactList.get(i).getWp();
            } else {
                model.wp = "";
            }
            model.mlid = contactList.get(i).getMlid();
            contactModels.add(model);
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_contact, null);
        Spinner spinnerContact = dialogView.findViewById(R.id.spinnerContact);
        contactsListAdapter = new CustomContactList(this, R.layout.spinner_list_item, contactModels);
        spinnerContact.setAdapter(contactsListAdapter);
        spinnerContact.setSelection(0); // set 1st contact as default value
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedContact = (CustomContactModel) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        final AlertDialog contactsDialog = new AlertDialog.Builder(mContext)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPushToContact();
                contactsDialog.dismiss();
            }
        });
        contactsDialog.show();
        contactsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void sendPushToContact() {
        try {
            JSONObject payloadsData = new JSONObject();
            SharedPreferences carLocation = getSharedPreferences("CarLocation", 0);
            if (carLocation != null) {
                mCarLocation = new Location("");
                mCarLocation.setLatitude(carLocation.getFloat("lat", 0));
                mCarLocation.setLongitude(carLocation.getFloat("long", 0));

                if(mCarLocation.getLatitude() == 0.0 && mCarLocation.getLongitude() == 0) {
                    showToastMessage("Please update your car's location");
                } else {
                    payloadsData.put("lat", mCarLocation.getLatitude());
                    payloadsData.put("lon", mCarLocation.getLongitude());
                    payloadsData.put("zoom", mMap.getCameraPosition().zoom);
                    NotificationHelper notificationHelper = new NotificationHelper(selectedContact.mlid, mContext, (BaseActivity) mContext);
                    notificationHelper.getToken(PayloadType.PT_Share_Location, payloadsData, new OnGetTokenListener() {
                        @Override
                        public void onSuccess(String response) {
                        }

                        @Override
                        public void onVolleyError(VolleyError error) {
                        }

                        @Override
                        public void onEmptyResponse() {
                        }

                        @Override
                        public void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList) {
                            // Do nothing
                        }

                        @Override
                        public void onJsonArrayEmpty() {

                        }

                        @Override
                        public void onJsonException() {
                        }

                        @Override
                        public void onTokenListEmpty() {

                        }
                    });
                }
            } else {
                // No car location data
                showToastMessage("An error occurred getting car location data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        getLocationPermission();

        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        if (mCarLocation != null) {

            MarkerOptions mo = new MarkerOptions()
                    .position(new LatLng(mCarLocation.getLatitude(), mCarLocation.getLongitude()))
                    .title("My Car");
            carMarker = mMap.addMarker(mo);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {

                locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(30 * 1000)
                        .setFastestInterval(5 * 1000);


                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        //Location received
                        mLastKnownLocation = locationResult.getLastLocation();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        mMap.setMyLocationEnabled(true);
                    }
                };

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

}
