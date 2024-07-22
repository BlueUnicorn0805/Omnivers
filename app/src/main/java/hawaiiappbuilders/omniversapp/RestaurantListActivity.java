package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import hawaiiappbuilders.omniversapp.adapters.RestaurantAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.IndustryInfo;
import hawaiiappbuilders.omniversapp.model.Restaurant;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class RestaurantListActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    ArrayList<Restaurant> _restList = new ArrayList<>();

    Toolbar toolbar;

    View viewTopMenu;

    ViewGroup clMapVid;

    TextView titleSocialMap;

    WebView wvYouTube;
    MapFragment mapFragment;
    GoogleMap googleMap;
    ArrayList<Marker> markerList = new ArrayList<>();
    TileOverlay overlayHeapMap;
    boolean heapMode = false;

    RecyclerView _recyclerView;
    String actionName;
    IndustryInfo mIndustryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_list);

        Intent data = getIntent();
        actionName = data.getStringExtra("parent");
        mIndustryInfo = data.getParcelableExtra("industry_info");

        ArrayList<Restaurant> restaurants = data.getParcelableArrayListExtra("restaurants");
        _restList.addAll(restaurants);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Choose %s Provider", mIndustryInfo.getTypeDesc()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        viewTopMenu = findViewById(R.id.btnMenu2);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btnToolbarHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        // Top banner
        clMapVid = findViewById(R.id.clMapVid);

        titleSocialMap = findViewById(R.id.titleSocialMap);
        titleSocialMap.setVisibility(View.GONE);

        wvYouTube = (WebView) findViewById(R.id.wvYouTube);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);

        _recyclerView = (RecyclerView) findViewById(R.id.rcvRestaurantList);
        _recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 1));
        RestaurantAdapter adapter = new RestaurantAdapter(this, _restList);
        _recyclerView.setAdapter(adapter);
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

        // Hide status bar
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar.setVisibility(View.GONE);*/

        viewTopMenu.setVisibility(View.GONE);
    }

    private void showStatusBar() {
        // Show status bar
        /*getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar.setVisibility(View.VISIBLE);*/

        viewTopMenu.setVisibility(View.VISIBLE);
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
        googleMap.setOnMarkerClickListener(this);
        showRestaurantMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int listPos = (int) marker.getTag();
        _recyclerView.smoothScrollToPosition(listPos);

        return false;
    }

    private void showRestaurantMarkers() {
        if (googleMap == null)
            return;

        if (!markerList.isEmpty()) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }

        // For HeapMap
        List<LatLng> latLngs = new ArrayList<>();

        // For Normal Map
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < _restList.size(); i++) {
            Restaurant rest = _restList.get(i);
            LatLng resPos = new LatLng(rest.get_lattiude(), rest.get_longitude());

            latLngs.add(resPos);

            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(resPos)
                    .title(rest.get_name())
            );
            newMarker.setTag(i);
            markerList.add(newMarker);
            builder.include(resPos);
        }

        if (_restList.isEmpty()) {
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(41.584778, -93.681738))
                    .zoom(11)
                    .bearing(0)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        } else {
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = dpToPx(mContext, 204);
            int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            googleMap.moveCamera(cu);
        }

        // Create the gradient.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        // Create the tile provider.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(latLngs)
                .gradient(gradient)
                .radius(45)
                .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        overlayHeapMap = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    public void pausePlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayer();
    }

    private void destroyPlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onDestroy", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyPlayer();
    }

    private void resumePlayer() {
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onResume", (Class[]) null)
                    .invoke(wvYouTube, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
        } catch (NoSuchMethodException nsme) {
        } catch (InvocationTargetException ite) {
        } catch (IllegalAccessException iae) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePlayer();
    }

    public void zoomMapPin(int position) {
        wvYouTube.setVisibility(View.GONE);
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

    public void heatMap(int position) {
        wvYouTube.setVisibility(View.GONE);
        pausePlayer();
        mapFragment.getView().setVisibility(View.VISIBLE);

        if (googleMap != null) {
            Restaurant restaurantInfo = _restList.get(position);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(restaurantInfo.get_lattiude(), restaurantInfo.get_longitude()))
                    .zoom(8)
                    .bearing(0)
                    .build();
            //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

            heapMode = !heapMode;
            if (heapMode) {
                for (int i = 0; i < markerList.size(); i++) {
                    //markerList.get(i).setTitle(String.valueOf(restaurantInfo.getSeekIT()));
                    markerList.get(i).showInfoWindow();
                    markerList.get(i).setVisible(true);
                }
                overlayHeapMap.setVisible(true);
                titleSocialMap.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < markerList.size(); i++) {
                    //markerList.get(i).setTitle(restaurantInfo.get_name());
                    markerList.get(i).showInfoWindow();
                    markerList.get(i).setVisible(true);
                }
                overlayHeapMap.setVisible(false);
                titleSocialMap.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
