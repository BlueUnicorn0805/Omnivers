package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import hawaiiappbuilders.omniversapp.adapters.RestaurantAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;

import java.util.ArrayList;

public class QRCodeScanResultActivity extends BaseActivity implements OnMapReadyCallback {

    ArrayList<Restaurant> _restList;

    WebView videoWebView;
    MapFragment mapFragment;
    GoogleMap googleMap;

    RecyclerView _recyclerView;
    String _parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search Results");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        _restList = new ArrayList<Restaurant>();

        _restList.add(new Restaurant(1,"Zombie Burgers", "9AM to 5PM", ".2 mi","This is a description of the restaurant to view and get details from",41.614699, -93.696915, R.mipmap.ic_launcher1_foreground, "1550 Court Pl, Denver, CO 80202, USA"));
        /*_restList.add(new Restaurant(2,"Chucks Pizza", "12pM to 10PM", ".5 mi","This is a description of the restaurant to view and get details from", 41.615182, -93.710958, "stop" ));
        _restList.add(new Restaurant(3,"Northern Lights Pizza", "9AM to 5PM", ".7 mi","This is a description of the restaurant to view and get details from",41.597486,-93.651462, "yes"));
        _restList.add(new Restaurant(4,"Centro", "6PM to 11PM", "1.2 mi","This is a description of the restaurant to view and get details from",41.572204,-93.708780, "groovy" ));
        _restList.add(new Restaurant(5,"B-Bops", "9AM to 9PM", "3.4 mi","This is a description of the restaurant to view and get details from",41.580597,-93.721832,"fish"));*/

        // Top banner
        videoWebView = (WebView) findViewById(R.id.wvYouTube);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);

        _recyclerView = (RecyclerView) findViewById(R.id.rcvRestaurantList);
        _recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        RestaurantAdapter adapter = new RestaurantAdapter(this, _restList);
        _recyclerView.setAdapter(adapter);

    }

    @Override
    public void onMapReady(GoogleMap map){

        googleMap = map;

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(41.584778,-93.681738))
                .zoom(11)
                .bearing(0)
                .build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

        for (Restaurant rest: _restList) {

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(rest.get_lattiude(), rest.get_longitude()))
                    .title(rest.get_name())
            );
        }
    }

    public void zoomMapPin(int position) {
        videoWebView.setVisibility(View.GONE);
        mapFragment.getView().setVisibility(View.VISIBLE);

        if (googleMap != null) {
            Restaurant restaurantInfo = _restList.get(position);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(restaurantInfo.get_lattiude(), restaurantInfo.get_longitude()))
                    .zoom(15)
                    .bearing(0)
                    .build();
            //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
