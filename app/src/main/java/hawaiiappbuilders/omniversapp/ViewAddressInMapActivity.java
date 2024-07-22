package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;

public class ViewAddressInMapActivity extends BaseActivity implements OnMapReadyCallback {

    double lat = 0;
    double lon = 0;
    float zoom = 0;

    Toolbar toolbar;

    MapFragment mapFragment;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_address_in_map);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format("Location from %s"));*/

        /*setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);
        getCoordinates();
    }

    public void getCoordinates() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        GoogleCertProvider.install(mContext);
        String address = getString(R.string.hotel_address);
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s|country:US&region=us&key=%s", address, K.gKy(BuildConfig.G));
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                try {
                    JSONObject returnJSON = new JSONObject(response);
                    if (returnJSON.has("status") && "OK".equals(returnJSON.getString("status"))) {
                        // Save current zipCode
                        JSONObject jsonZipLocationInfo = returnJSON;
                        if (jsonZipLocationInfo.has("results")) {
                            JSONArray jsonArrayResult = jsonZipLocationInfo.getJSONArray("results");
                            if (jsonArrayResult.length() > 0) {
                                JSONObject jsonAddrObj = jsonArrayResult.getJSONObject(0);

                                JSONObject geometry = jsonAddrObj.getJSONObject("geometry");
                                JSONObject locationObject = geometry.getJSONObject("location");
                                lat = locationObject.getDouble("lat");
                                lon = locationObject.getDouble("lng");

                                // Get City and State
                                /*JSONArray jsonAddressComponentArray = jsonAddrObj.getJSONArray("address_components");
                                for (int i = 0; i < jsonAddressComponentArray.length(); i++) {
                                    JSONObject jsonAddrComponent = jsonAddressComponentArray.getJSONObject(i);
                                    String longName = jsonAddrComponent.getString("long_name");
                                    String shortName = jsonAddrComponent.getString("short_name");
                                    String types = jsonAddrComponent.getString("types");

                                    if (types.contains("administrative_area_level_1")) {
                                        // This means state info
                                        edtState.setText(shortName);
                                    } else if (types.contains("locality")) {
                                        edtCity.setText(longName);
                                    }
                                }

                                // Get Location
                                JSONObject jsonGeometryObj = jsonAddrObj.getJSONObject("geometry");
                                JSONObject jsonLocationObj = jsonGeometryObj.getJSONObject("location");*/

                                // update map pin
                                LatLng resPos = new LatLng(lat, lon);
                                googleMap.addMarker(new MarkerOptions().position(resPos).title(address));
                                LatLng userPos;
                                if (getLocation()) {
                                    userPos = new LatLng(Double.parseDouble(getUserLat()), Double.parseDouble(getUserLon()));
                                    googleMap.addMarker(new MarkerOptions().position(userPos).title(address));
                                    CameraPosition googlePlex = CameraPosition.builder()
                                            .target(resPos)
                                            .zoom(15) // 15=Streets level
                                            .bearing(0)
                                            .build();
                                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                                }
                            }
                        }
                    } else {
                        showToastMessage("Couldn't get address information");
                    }
                } catch (JSONException e) {
                    // Error
                    showToastMessage("Couldn't get address information");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                if (TextUtils.isEmpty(error.getMessage())) {
                    showAlert(R.string.error_invalid_credentials);
                } else {
                    showAlert(error.getMessage());
                }

                //showMessage(error.getMessage());
            }
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        sr.setShouldCache(false);
        queue.add(sr);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
