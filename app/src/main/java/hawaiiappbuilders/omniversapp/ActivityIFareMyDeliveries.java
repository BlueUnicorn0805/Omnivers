package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hawaiiappbuilders.omniversapp.adapters.BidsListAdapter;
import hawaiiappbuilders.omniversapp.adapters.ReviewListAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.circularprogressindicator.CircularProgressIndicator;
import hawaiiappbuilders.omniversapp.location.Helper;
import hawaiiappbuilders.omniversapp.location.LocationUtil;
import hawaiiappbuilders.omniversapp.location.SharedPreferenceManager;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.BidsInfoOriginal;
import hawaiiappbuilders.omniversapp.model.DeliveryItem;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.ReviewInfo;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.waydirections.DirectionObject;
import hawaiiappbuilders.omniversapp.waydirections.GsonRequest;
import hawaiiappbuilders.omniversapp.waydirections.LegsObject;
import hawaiiappbuilders.omniversapp.waydirections.PolylineObject;
import hawaiiappbuilders.omniversapp.waydirections.RouteObject;
import hawaiiappbuilders.omniversapp.waydirections.StepsObject;
import hawaiiappbuilders.omniversapp.waydirections.VolleySingleton;

public class ActivityIFareMyDeliveries extends BaseActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationUtil.LocationListener {
    public static final String TAG = ActivityIFareMyDeliveries.class.getSimpleName();
    DeliveryItem currentDeliveryItem;

    private GoogleMap mMap;
    private LocationUtil mLocationUtil;
    LatLng mUserLatLng;
    String mUserAddress;
    Marker currentPositionMarker;
    ArrayList<Marker> deliveryMarkers = new ArrayList<>();

    // Current Deliveries
    ArrayList<DeliveryItem> deliveriesList = new ArrayList<>();
    Handler mHandlerUpdateBidList;
    Runnable mRunnableUpdateBidList;

    // Bid List
    TextView tvDelsInfo;
    ListView lvData;
    ArrayList<BidsInfoOriginal> bidsInfoArray = new ArrayList<>();
    BidsListAdapter bidsInfoAdapter;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senderdelsmap);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("My Deliveries");
        }

        Intent intent = getIntent();
        currentDeliveryItem = intent.getParcelableExtra("delivery");
        mContext = this;
        tvDelsInfo = (TextView) findViewById(R.id.tvDelsInfo);

        lvData = (ListView) findViewById(R.id.lvDataList);
        bidsInfoAdapter = new BidsListAdapter(mContext, bidsInfoArray);
        lvData.setAdapter(bidsInfoAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {

                BidsInfoOriginal bidsInfo = bidsInfoArray.get(index);

                // Check green pin, this delivery already assigned to any driver
                String driverIDChoosen = bidsInfoArray.get(index).getDriverID();

                if (!"0".equals(bidsInfoAdapter.getDriverIDChoosen())) {
                    showAlert("Already selected driver for this delivery.");
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                        "CJLGetRating",
                        BaseFunctions.MAIN_FOLDER,
                        getUserLat(),
                        getUserLon(),
                        mMyApp.getAndroidId());
                String extraParams =
                        "&driverID=" + driverIDChoosen +
                                "&RatingOfMLID=" + bidsInfo.getMlID();
                baseUrl += extraParams;
                Log.e("Request", baseUrl);

                showProgressDialog();
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //HttpsTrustManager.allowAllSSL();
                GoogleCertProvider.install(mContext);

                String finalBaseUrl = baseUrl;
                StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgressDialog();

                        Log.e("CJLGetRating", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                int completed = jsonObject.getInt("completed");
                                int onTime = jsonObject.getInt("onTime");

                                int repeat = 0;
                                if(jsonObject.has("repeat")) {
                                    repeat = jsonObject.getInt("repeat");
                                }
                                // int repeat = jsonObject.getInt("repeat");
                                float stars = (float) jsonObject.getDouble("stars");
                                String fn = jsonObject.getString("FN");

                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_driver_profile, null);
                                final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                                        .setView(dialogView)
                                        .setCancelable(false)
                                        .create();

                                CircularProgressIndicator progressCompleted = (CircularProgressIndicator) dialogView.findViewById(R.id.progressCompleted);
                                CircularProgressIndicator progressOnTime = (CircularProgressIndicator) dialogView.findViewById(R.id.progressOnTime);
                                CircularProgressIndicator progressRepeat = (CircularProgressIndicator) dialogView.findViewById(R.id.progressRepeat);
                                progressCompleted.setProgress(completed, 100);
                                progressOnTime.setProgress(onTime, 100);
                                progressRepeat.setProgress(repeat, 100);

                                //RatingBar ratingBar = dialogView.findViewById()

                                ReviewListAdapter reviewListAdapter;
                                ArrayList<ReviewInfo> reviewInfos = new ArrayList<>();
                                ListView lvProfiles = (ListView) dialogView.findViewById(R.id.lvProfileDate);

                                // reviewInfos.add(new ReviewInfo("3", "Shu Lian", 5.0f, "\"Great Service, On-Time, Repeat hire again.\"", "", "2018-11-20"));
                                // reviewInfos.add(new ReviewInfo("4", "Anna", 5.0f, "\"Very good person. Always deliveried package in time and provided great service.\"", "", "2018-11-20"));
                                reviewInfos.add(new ReviewInfo("4", fn, stars, "\"Very good person. Always deliveried package in time and provided great service.\"", "", "2018-11-20"));

                                reviewListAdapter = new ReviewListAdapter(mContext, reviewInfos);
                                lvProfiles.setAdapter(reviewListAdapter);

                                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        inputDlg.dismiss();
                                    }
                                });
                                dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        inputDlg.dismiss();
                                        //sendMessage(bidsInfoArray.get(index));
                                        acceptBid(bidsInfoArray.get(index));
                                    }
                                });

                                inputDlg.show();
                                inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressDialog();
                        baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };

                sr.setRetryPolicy(new DefaultRetryPolicy(
                        25000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                sr.setShouldCache(false);
                queue.add(sr);


            }
        });

        // Update Bid List Process
        mHandlerUpdateBidList = new Handler();
        mRunnableUpdateBidList = new Runnable() {
            @Override
            public void run() {
                if (currentDeliveryItem != null) {
                    getDelBidsList(currentDeliveryItem, false);
                }
            }
        };

        // Init Maps
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mLocationUtil = new LocationUtil(ActivityIFareMyDeliveries.this, SharedPreferenceManager.getInstance());

        getDeliveries(true);
        getDelBidsList(currentDeliveryItem, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationUtil != null) {
            mLocationUtil.onDestroy();
        }

        if (mHandlerUpdateBidList != null) {
            mHandlerUpdateBidList.removeCallbacks(mRunnableUpdateBidList);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Disable the Toolbar in the map
        mMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

            }
        });
        if (checkPermissions(mContext, PERMISSION_REQUEST_LOCATION_STRING, false, PERMISSION_REQUEST_CODE_LOCATION)) {
            try {
                //mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        mMap.setOnMarkerClickListener(this);

        mLocationUtil.fetchApproximateLocation(this);
        mLocationUtil.fetchPreciseLocation(this);
    }

    @Override
    public void onLocationReceived(@NonNull LatLng location, @NonNull String addressString, Address address) {
        mUserLatLng = new LatLng(location.latitude, location.longitude);
        mUserAddress = addressString;
        // Remove Old marker
        if (currentPositionMarker != null) {
            currentPositionMarker.remove();
        }

        currentPositionMarker = mMap.addMarker(new MarkerOptions().position(mUserLatLng)
                .title(getString(R.string.you_are_here))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location_pin)));

        if (deliveriesList.isEmpty()) {
            updateCamera(mUserLatLng);
        }
    }

    private static final int INITIAL_ZOOM_LEVEL = 8;

    public void updateCamera(@Nullable LatLng latLng) {
        if (latLng == null) {
            return;
        }
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM_LEVEL);
        mMap.animateCamera(location, 1000, null);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        /*if(viewId == R.id.btnAddNew) {
            //parentActivity.showFragment(MainActivity.FRAGMENT_SENDER_NEW_DELIVERY);
        }*/
    }

    private void getDeliveries(final boolean bShowProgress) {

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(this,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                getUserLat(),
                getUserLon(),
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "DelsBySenderID" +
                        "&industryID=" + "80" +
                        "&sellerID=" + "0" +
                        "&misc=" + appSettings.getUserId();
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        if (bShowProgress) {
            showProgressDialog();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (bShowProgress) {
                    hideProgressDialog();
                }

                Log.e("DelsBySenderID", response);

                if (response != null || !response.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.has("status") && jsonObject.getBoolean("status") == false) {
                            showToastMessage(jsonObject.getString("msg"));
                        } else {
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject favDataObj = jsonArray.getJSONObject(i);
                                DeliveryItem newDelInfo = gson.fromJson(favDataObj.toString(), DeliveryItem.class);
                                deliveriesList.add(newDelInfo);
                            }

                            showDeliveries();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                checkDeliveryStatus();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (bShowProgress) {
                    hideProgressDialog();
                }
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                checkDeliveryStatus();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Accept", "application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        sr.setShouldCache(false);
        queue.add(sr);
    }

    private void checkDeliveryStatus() {
        if (deliveriesList.isEmpty()) {
            // Go to New Delivery
            // parentActivity.showFragment(FRAGMENT_SENDER_NEW_DELIVERY);
        }
    }

    private void showDeliveries() {
        if (mMap == null)
            return;

        if (deliveriesList == null || deliveriesList.isEmpty())
            return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int boundsCounter = 0;

        if (!deliveryMarkers.isEmpty()) {
            for (Marker marker : deliveryMarkers) {
                marker.remove();
            }
            deliveryMarkers.clear();
        }

        for (DeliveryItem delInfo : deliveriesList) {
            LatLng latLng = new LatLng(delInfo.getLat(), delInfo.getLon());

            if (TextUtils.isEmpty(delInfo.getDriverID()) || "0".equals(delInfo.getDriverID())) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Tap"/*String.format("%s", delInfo.gettAdd())*/)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                marker.setTag(delInfo);
                marker.showInfoWindow();

                deliveryMarkers.add(marker);
                boundsCounter++;
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.format("%s", delInfo.getDelToAdd()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.setTag(delInfo);

                deliveryMarkers.add(marker);
                boundsCounter++;
            }

            boundsBuilder.include(latLng);
        }

        LatLngBounds latLngBounds = boundsBuilder.build();
        if (boundsCounter == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 14.0f));

        } else {
            int routePadding = 200;
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object markerObject = marker.getTag();
        if (markerObject instanceof DeliveryItem) {

            currentDeliveryItem = (DeliveryItem) markerObject;
            // Start Find the Waypoint
            if (mPolyline != null) {
                mPolyline.remove();
            }

            // Disable the route
            /*String directionApiPath = Helper.getUrl(String.valueOf(deliveryInfo.getLat()), String.valueOf(deliveryInfo.getLon()),
                    String.valueOf(deliveryInfo.getToLat()), String.valueOf(deliveryInfo.getToLon()));
            Log.d("waypoint", "Path " + directionApiPath);
            getDirectionFromDirectionApiServer(directionApiPath);*/

            // Remove Current Callback and update current Delivery Item
            mHandlerUpdateBidList.removeCallbacks(mRunnableUpdateBidList);


            if (TextUtils.isEmpty(currentDeliveryItem.getDriverID()) || "0".equals(currentDeliveryItem.getDriverID())) {
                // Orange Pin
                getDelBidsList(currentDeliveryItem, true);
            } else {
                // Get Bids list
                getDelBidsList(currentDeliveryItem, true);
            }
        }
        return false;
    }

    private void getDirectionFromDirectionApiServer(String url) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }

    private Response.Listener<DirectionObject> createRequestSuccessListener() {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if (response.getStatus().equals("OK")) {
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(mMap, mDirections);
                    } else {
                        showToastMessage("Couldn't find the Waypoints!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    Polyline mPolyline;

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(8).color(Color.BLUE).geodesic(true);
        options.addAll(positions);

        mPolyline = map.addPolyline(options);

        /*// Wrap the Waypoints
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng point : positions) {
            boundsBuilder.include(point);
        }

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));*/
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void getDelBidsList(final DeliveryItem deliveryInfo, final boolean showProgress) {

        if(currentDeliveryItem != null) {
            tvDelsInfo.setText(String.format("Choose a Bid(DelID:%s)", currentDeliveryItem.getDelID()));
        }

        if(deliveryInfo != null) {
            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "CJLGet",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "DelBidsByDelID" +
                            "&misc=" + deliveryInfo.getDelID() +
                            "&industryID=" + "80" +
                            "&sellerID=" + "0";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            if (showProgress) {
                showProgressDialog();
            }

            RequestQueue queue = Volley.newRequestQueue(mContext);
            String finalBaseUrl = baseUrl;
            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (showProgress) {
                        hideProgressDialog();
                    }

                    Log.e("DelBidsByDelID", response);

                    if (response != null || !response.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            bidsInfoArray.clear();
                            bidsInfoAdapter.setDriverIDChoosen(deliveryInfo.getDriverID());

                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                showToastMessage(jsonObject.getString("msg"));
                            } else {
                                Gson gson = new Gson();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject favDataObj = jsonArray.getJSONObject(i);
                                    bidsInfoArray.add(gson.fromJson(favDataObj.toString(), BidsInfoOriginal.class));
                                }
                            }

                            bidsInfoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (showProgress) {
                        hideProgressDialog();
                    }
                    baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                    checkDeliveryStatus();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Accept", "application/json");
                    return params;
                }
            };

            sr.setRetryPolicy(new DefaultRetryPolicy(
                    25000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            sr.setShouldCache(false);
            queue.add(sr);
        }
    }

    private void sendMessage(final BidsInfoOriginal favUser) {

        if (currentDeliveryItem == null) {
            return;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            // jsonObject.put("userid", appSettings.getUserId());
            String msgContent = String.format("You have bee requested from %s to %s.", currentDeliveryItem.getFromAddress(), currentDeliveryItem.getDelToAdd());
            jsonObject.put("msg", msgContent);
            jsonObject.put("msgto", favUser.getDriverID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();

        String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                "Beep.php", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());


        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                if (response != null || !response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            showToastMessage(jsonObject.getString("message"));
                        } else {
                            showToastMessage(jsonObject.getString("message"));
                        }

                        sendDeliveryRequestMessage(favUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(baseUrl));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(sr);
    }

    private void acceptBid(final BidsInfoOriginal bidsInfo) {

        // Check current delivery information
        /*if (currentDeliveryItem == null) {
            return;
        }*/

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setTitle("Confirm accept");
        alertDialogBuilder.setMessage("Do you want to accept this driver?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        HashMap<String, String> params = new HashMap<>();
                        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                                "BidAccept",
                                BaseFunctions.MAIN_FOLDER,
                                String.valueOf(mUserLatLng.latitude),
                                String.valueOf(mUserLatLng.longitude),
                                mMyApp.getAndroidId());
                        String extraParams =
                                "&DelID=" + bidsInfo.getDelID() +
                                        "&DriverID=" + bidsInfo.getDriverID() +
                                        "&acceptTime=" + DateUtil.toStringFormat_7(new Date());
                        baseUrl += extraParams;
                        Log.e("Request", baseUrl);

                        showProgressDialog();

                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        String finalBaseUrl = baseUrl;
                        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("BidAccept", response);

                                hideProgressDialog();

                                if (response != null || !response.isEmpty()) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                                        if (jsonObject.optBoolean("status")) {
                                            getDeliveries(false);

                                            currentDeliveryItem.setDriverID(bidsInfo.getDriverID());
                                            bidsInfoAdapter.setDriverIDChoosen(bidsInfo.getDriverID());

                                            //sendDeliveryRequestMessage(bidsInfo);

                                            JSONArray dataArray = jsonObject.getJSONArray("token");
                                            NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                            ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(dataArray);
                                            // Xiao UPDel
                                            tokenList.add(new FCMTokenData("dAyya7gPd5o:APA91bHoizSGBl0PNm3DpIbgBmGH14r81-FmVup5xMCTYJ1xw_4PePS88U8SCUdwtW9E62EanKzWNt3tp5ylW1Lb5FdW7G2N5NLgaQ4FLpj5aEFDXrVKpBrZ7xN-oMY63oFHMHMfGAeo", FCMTokenData.OS_UNKNOWN));

                                            JSONObject payload = new JSONObject();
                                            payload.put("message", "Your bid was accepted!");
                                            payload.put("DELID", "1");
                                            payload.put("SenderCP", appSettings.getCP());
                                            payload.put("SenderID", appSettings.getUserId());
                                            notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Text_Message, payload);
                                            showToastMessage("Sent Message!");
                                        } else {
                                            showToastMessage(jsonObject.getString("message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                hideProgressDialog();
                                baseFunctions.handleVolleyError(mContext, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/x-www-form-urlencoded");
                                params.put("Accept", "application/json");
                                return params;
                            }
                        };

                        sr.setRetryPolicy(new DefaultRetryPolicy(
                                25000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        sr.setShouldCache(false);
                        queue.add(sr);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(wlp);
        alertDialog.show();
    }

    private void sendDeliveryRequestMessage(BidsInfoOriginal bidInfo) {
        // Check data
        if (currentDeliveryItem == null || bidInfo == null)
            return;

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            // jsonObject.put("userid", appSettings.getUserId());
            jsonObject.put("driverid", bidInfo.getDriverID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();

        String baseUrl = BaseFunctions.getBaseData(jsonObject, getApplicationContext(),
                "DelGetDriverTokensByDriverID.php", BaseFunctions.MAIN_FOLDER, getUserLat(), getUserLon(), mMyApp.getAndroidId());

        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();

                if (response != null || !response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                            ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(dataArray);
                            if (tokenList.isEmpty()) {
                                showAlert("Driver has no any device to contact.");
                            } else {
                                String curUserName = String.format("%s %s", appSettings.getFN(), appSettings.getLN()).trim();
                                String title = String.format("%s selected you for the delivery", curUserName);
                                String addressInfo = String.format("From %s to %s", currentDeliveryItem.getFromAddress(), currentDeliveryItem.getDelToAdd());
                                if (!tokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    payload.put("title", title);
                                    payload.put("message", addressInfo);
                                    payload.put("delID", currentDeliveryItem.getDelID());
                                    payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                    payload.put("SenderID", appSettings.getUserId());
                                    // TODO:  Push notification call is commented and is not called
                                    // tokenGetter.sendPushNotification(mContext, tokenList, PayloadType.PT_Orders, payload);
                                }
                            }
                        } else {
                            hideProgressDialog();
                            showToastMessage(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        hideProgressDialog();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                showToastMessage("Request Error!, Please check network.");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(sr);
    }
}