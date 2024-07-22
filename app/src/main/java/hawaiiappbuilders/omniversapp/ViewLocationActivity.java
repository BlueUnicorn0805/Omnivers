package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class ViewLocationActivity extends BaseActivity implements OnMapReadyCallback {

    String senderName = "";
    double lat = 0;
    double lon = 0;
    float zoom = 0;

    Toolbar toolbar;

    MapFragment mapFragment;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewlocation);

        Intent data = getIntent();
        senderName = data.getStringExtra("SenderName");
        lat = data.getDoubleExtra("lat", 0.00f);
        lon = data.getDoubleExtra("lon", 0.00f);
        zoom = data.getIntExtra("zoom", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Location from %s", senderName));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btnDirection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f", mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", lat, lon));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        LatLng resPos = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(resPos).title(senderName));
        LatLng userPos;
        if (getLocation()) {
            userPos = new LatLng(Double.parseDouble(getUserLat()), Double.parseDouble(getUserLon()));
            map.addMarker(new MarkerOptions().position(userPos).title(""));
        }
        CameraPosition googlePlex = CameraPosition.builder()
                .target(resPos)
                .zoom(15) // 15=Streets level
                .bearing(0)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
