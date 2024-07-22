package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import hawaiiappbuilders.omniversapp.adapters.FavoriteAdapter;
import hawaiiappbuilders.omniversapp.adapters.ServiceAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.services.GpsTracker;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ServiceListActivity extends BaseActivity implements OnMapReadyCallback {

    String actionName;
    IndustryInfo mIndustryInfo;

    Toolbar toolbar;

    ArrayList<Restaurant> _restList = new ArrayList<>();

    RecyclerView _recyclerView;

    ViewGroup clMapVid;
    WebView videoWebView;
    MapFragment mapFragment;
    GoogleMap googleMap;

    ImageLoader imageLoader;
    DisplayImageOptions imageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_list);

        Intent data = getIntent();
        actionName = data.getStringExtra("parent");
        mIndustryInfo = data.getParcelableExtra("industry_info");

        // Initialize the ImageLoader
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        imageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                /*.showImageOnLoading(R.drawable.logo_halopay_notg_white)
                .showImageOnFail(R.drawable.logo_halopay_notg_white)
                .showImageForEmptyUri(R.drawable.logo_halopay_notg_white)*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        ArrayList<Restaurant> restaurants = data.getParcelableArrayListExtra("restaurants");

        // Add Store list in the List
        _restList.addAll(restaurants);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (actionName) {
            case "search":
                toolbar.setTitle("Search Results");
                break;
            case "craving":
                toolbar.setTitle("Craving Search Results");
                break;
            case "favorites":
                toolbar.setTitle("My Favorites");
                break;
            case "nearby":
                toolbar.setTitle("Stores Near Me");
                break;
            case "industry":
                toolbar.setTitle(String.format("Choose %s Provider", mIndustryInfo.getTypeDesc()));
                break;
            case "NEMT" :
                toolbar.setTitle("Book your NEMT now");
                break;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Top banner
        clMapVid = findViewById(R.id.clMapVid);
        videoWebView = (WebView) findViewById(R.id.wvYouTube);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);

        _recyclerView = (RecyclerView) findViewById(R.id.rcvRestaurantList);
        _recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        FavoriteAdapter adapter = new FavoriteAdapter(this, _restList, mIndustryInfo, imageLoader, imageOptions);
        _recyclerView.setAdapter(adapter);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)clMapVid.getLayoutParams();
            params.height = MATCH_PARENT;
            clMapVid.setLayoutParams(params);
            hideStatusBar();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)clMapVid.getLayoutParams();
            params.height = dpToPx(mContext, 204);
            clMapVid.setLayoutParams(params);

            showStatusBar();
        }
    }

    private void hideStatusBar() {
        /*View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar.setVisibility(View.GONE);
    }

    private void showStatusBar() {
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbar.setVisibility(View.VISIBLE);
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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Restaurant rest : _restList) {
            LatLng resPos = new LatLng(rest.get_lattiude(), rest.get_longitude());
            map.addMarker(new MarkerOptions()
                    .position(resPos)
                    .title(rest.get_name())
            );
            builder.include(resPos);
        }

        if (_restList.isEmpty()) {
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(41.584778, -93.681738))
                    .zoom(11)
                    .bearing(0)
                    .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        } else {
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = dpToPx(mContext, 204);
            int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.moveCamera(cu);
        }

        /*if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_LOCATION);
        } else {
            showMyLocation();
        }*/
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

    @SuppressLint("MissingPermission")
    private void showMyLocation() {
        if (googleMap != null) {
            GpsTracker gpsTracker = new GpsTracker(mContext);
            if (gpsTracker.canGetLocation()) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                        .zoom(12)
                        .bearing(0)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMyLocation();
                } else {
                    showMessage(mContext, "You need to grant permission");
                }
                break;
        }
    }
}
