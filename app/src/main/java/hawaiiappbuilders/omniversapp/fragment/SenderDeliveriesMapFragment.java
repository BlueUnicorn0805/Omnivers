/*
 * Copyright 2018 ShineStar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hawaiiappbuilders.omniversapp.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hawaiiappbuilders.omniversapp.ActivityIFareMyDeliveries;
import hawaiiappbuilders.omniversapp.DeliveryDetailsActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.adapters.ReviewListAdapter;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.circularprogressindicator.CircularProgressIndicator;
import hawaiiappbuilders.omniversapp.location.Constants;
import hawaiiappbuilders.omniversapp.location.GeocodeAddressIntentService;
import hawaiiappbuilders.omniversapp.location.Helper;
import hawaiiappbuilders.omniversapp.location.LocationUtil;
import hawaiiappbuilders.omniversapp.location.SharedPreferenceManager;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.DeliveryItem;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.OpenDeliveryInfo;
import hawaiiappbuilders.omniversapp.model.ReviewInfo;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.waydirections.DirectionObject;
import hawaiiappbuilders.omniversapp.waydirections.GsonRequest;
import hawaiiappbuilders.omniversapp.waydirections.LegsObject;
import hawaiiappbuilders.omniversapp.waydirections.PolylineObject;
import hawaiiappbuilders.omniversapp.waydirections.RouteObject;
import hawaiiappbuilders.omniversapp.waydirections.StepsObject;
import hawaiiappbuilders.omniversapp.waydirections.VolleySingleton;

public class SenderDeliveriesMapFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationUtil.LocationListener {

    private static final String TAG = SenderDeliveriesMapFragment.class.getSimpleName();
    BaseFunctions baseFunctions;
    public static SenderDeliveriesMapFragment newInstance(String text) {
        SenderDeliveriesMapFragment mFragment = new SenderDeliveriesMapFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);

        return mFragment;
    }

    boolean isFullScreen = true;

    TextView btnStatus;
    boolean userStatusIsActive = true;

    FrameLayout panelMap;
    Button btnDetails;
    EditText edtFromAddr;
    EditText edtToAddr;
    EditText edtDistance;

    boolean actionIsForBid; // True: Bid Delivery, False : Complete

    private GoogleMap mMap;
    private LocationUtil mLocationUtil;

    Handler addressUpdateHandler;
    AddressResultReceiver mResultReceiver;
    double toLatitude;
    double toLongitude;
    LatLng mUserLatLng;
    String mUserAddress;

    ArrayList<DeliveryItem> deliveriesInfoBySender = new ArrayList<>();

    ArrayList<Marker> markersInfoInMap = new ArrayList<>();
    ArrayList<Marker> markersInfoByDriver = new ArrayList<>();

    private static final int DELIVERY_ACTION_BID = 1;
    private static final int DELIVERY_ACTION_COMPLETE = 2;
    int curDeliveryAction = 0;

    private static final int INITIAL_ZOOM_LEVEL = 8;

    TextView tabAll;
    TextView tabPickups;
    TextView tabDeliveries;
    int currentTabIdx = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sender_map, container, false);

        init(getArguments());
        baseFunctions = new BaseFunctions(mContext, TAG);

        // Init UI
        panelMap = (FrameLayout) rootView.findViewById(R.id.panelMap);


        // Make full screen at the first
        isFullScreen = true;

        // Driver Status Button
        btnStatus = (TextView) rootView.findViewById(R.id.btnStatus);
        btnStatus.setOnClickListener(this);

        // Delivery Informations
        btnDetails = (Button) rootView.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(this);
        edtFromAddr = (EditText) rootView.findViewById(R.id.edtFromAddr);
        edtFromAddr.setKeyListener(null);
        edtFromAddr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    //        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
                    //startActivity(intent);

                    LatLng latLng = (LatLng) view.getTag();
                    showDirection(latLng);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        edtToAddr = (EditText) rootView.findViewById(R.id.edtToAddr);
        edtToAddr.setKeyListener(null);
        edtToAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                addressUpdateHandler.removeMessages(0);
                addressUpdateHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });

        edtToAddr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LatLng latLng = (LatLng) view.getTag();
                showDirection(latLng);
            }
        });

        mResultReceiver = new AddressResultReceiver(null);
        addressUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == 0) {
                    String strToAddr = edtToAddr.getText().toString();
                    if (!TextUtils.isEmpty(strToAddr)) {
                        Intent intent = new Intent(mContext, GeocodeAddressIntentService.class);
                        intent.putExtra(Constants.RECEIVER, mResultReceiver);
                        intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.USE_ADDRESS_NAME);
                        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, strToAddr);
                        parentActivity.startService(intent);
                    }
                }
            }
        };

        edtDistance = (EditText) rootView.findViewById(R.id.edtDistance);
        edtDistance.setKeyListener(null);

        tabAll = (TextView) rootView.findViewById(R.id.tabAll);
        tabPickups = (TextView) rootView.findViewById(R.id.tabPickups);
        tabDeliveries = (TextView) rootView.findViewById(R.id.tabDeliveries);
        tabAll.setOnClickListener(this);
        tabPickups.setOnClickListener(this);
        tabDeliveries.setOnClickListener(this);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mLocationUtil = new LocationUtil(parentActivity, SharedPreferenceManager.getInstance());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectTab(0);
    }

    private void selectTab(int tabIndex) {
        if (currentTabIdx == tabIndex) {
            return;
        }

        currentTabIdx = tabIndex;
        if (currentTabIdx == 0) {
            tabAll.setSelected(true);
            tabPickups.setSelected(false);
            tabDeliveries.setSelected(false);
        } else if (currentTabIdx == 1) {
            tabAll.setSelected(false);
            tabPickups.setSelected(true);
            tabDeliveries.setSelected(false);
        } else {
            tabAll.setSelected(false);
            tabPickups.setSelected(false);
            tabDeliveries.setSelected(true);
        }

        showDeliveriesInMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationUtil != null) {
            mLocationUtil.onDestroy();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Disable the Toolbar in the map
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

        loadData();
    }

    private void loadData() {

        //getDeliveriesInArea();
        getDeliveriesBySender();

        /*
        Row	MsgID	Msg	Internal	CreateDate
        1	8199	Complete	null	2018/11/23 10:20:09 AM
        2	8190	Driver Complete	null	2018/11/23 10:20:09 AM
        3	8180	Other Location	null	2018/11/23 11:07:03 AM
        4	8174	Neighbor accepted	null	2018/11/23 11:07:03 AM
        5	8172	At Front Door	null	2018/11/23 11:07:03 AM
        6	8170	Signed for	null	2018/11/23 11:07:03 AM
        7	8160	Picked Up	null	2019/1/10 11:59:43 AM
        8	8155	In Route to pickup package	null	2018/11/25 6:38:00 AM
        9	8150	Sender Chose Driver	null	2018/11/23 10:22:25 AM
        8199 is complete
        C
        you might what to copy and keep this for reference

        MsgID	Msg
        8199	Complete
        8190	Driver Complete
        8180	Other Location
        8174	Neighbor accepted
        8172	At Front Door
        8170	Signed for
        8160	Picked Up
        8155	In Route to pickup package
        8150	Sender Chose Driver
        */
    }

    private void getDeliveriesBySender() {
        if (!markersInfoByDriver.isEmpty()) {
            for (Marker marker : markersInfoByDriver) {
                marker.remove();
            }
            markersInfoByDriver.clear();
        }
        deliveriesInfoBySender.clear();

        GpsTracker gpsTracker = new GpsTracker(mContext);
        String lat, lon;
        if(gpsTracker.canGetLocation()) {
            lat = String.valueOf(gpsTracker.getLatitude());
            lon = String.valueOf(gpsTracker.getLongitude());
        } else {
            lat = "0.0";
            lon = "0.0";
        }

        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(mContext,
                "CJLGet",
                BaseFunctions.MAIN_FOLDER,
                lat,
                lon,
                mMyApp.getAndroidId());
        String extraParams =
                "&mode=" + "DelsBySenderID";
                        // "&misc=" + appSettings.getUserId() +
                        // "&industryID=" + "80" +
                        // "&sellerID=" + "0";
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

                Log.e("DelsBySenderID", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                    if (jsonObject.has("status")) {
                        showToastMessage(jsonObject.getString("msg"));
                    } else {
                        deliveriesInfoBySender.clear();

                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject favDataObj = jsonArray.getJSONObject(i);
                            DeliveryItem newDelInfo = gson.fromJson(favDataObj.toString(), DeliveryItem.class);

                            deliveriesInfoBySender.add(newDelInfo);
                        }
                        Log.e("mydels", String.format("There is(are) %d items of driver dels", deliveriesInfoBySender.size()));

                        showDeliveriesInMap();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    synchronized private void showDeliveriesInMap() {
        if (mMap == null)
            return;

        hideDetailLayout();

        // Remove markers
        if (!markersInfoInMap.isEmpty()) {
            for (Marker marker : markersInfoInMap) {
                marker.remove();
            }
            markersInfoInMap.clear();
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int boundsCounter = 0;

        // Show My Deliveries
        if (deliveriesInfoBySender != null) {
            for (DeliveryItem delInfo : deliveriesInfoBySender) {
                LatLng latLng = new LatLng(delInfo.getLat(), delInfo.getLon());
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.format("%s", delInfo.getfAdd()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                marker.setTag(delInfo);

                markersInfoByDriver.add(marker);

                boundsBuilder.include(latLng);
                boundsCounter++;
            }
        }

        if (boundsCounter > 0) {
            try {
                LatLngBounds latLngBounds = boundsBuilder.build();
                if (boundsCounter == 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 14.0f));

                } else if (boundsCounter > 0) {
                    int routePadding = 200;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Show first delivery path
            //DeliveryItem deliveryItem = deliveriesInfoBySender.get(0);
            //LatLng fromLatLng = new LatLng(deliveryItem.getLat(), deliveryItem.getLon());
            //LatLng toLatLng = new LatLng(deliveryItem.getToLat(), deliveryItem.getToLon());
            //getPath(fromLatLng, toLatLng);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnStatus) {
            if (userStatusIsActive) {
                userStatusIsActive = false;
                btnStatus.setText("Off Duty");
                btnStatus.setBackgroundResource(R.color.user_status_off);

                // Remove path
                if (mPolyline != null)
                    mPolyline.remove();

                // Remove all Markers
                if (!markersInfoInMap.isEmpty()) {
                    for (Marker marker : markersInfoInMap) {
                        marker.remove();
                    }
                    markersInfoInMap.clear();
                }

                if (!markersInfoByDriver.isEmpty()) {
                    for (Marker marker : markersInfoByDriver) {
                        marker.remove();
                    }
                    markersInfoByDriver.clear();
                }
            } else {
                userStatusIsActive = true;
                btnStatus.setText("On Duty");
                btnStatus.setBackgroundResource(R.color.user_status_on);

                loadData();
            }
        } else if (viewId == R.id.tabAll) {
            selectTab(0);
        } else if (viewId == R.id.tabPickups) {
            selectTab(1);
        } else if (viewId == R.id.tabDeliveries) {
            selectTab(2);
        } else if (viewId == R.id.btnDetails) {
            DeliveryItem itemInfo = (DeliveryItem) btnDetails.getTag();
            Intent intent = new Intent(mContext, ActivityIFareMyDeliveries.class);
            intent.putExtra("delivery", itemInfo);
            startActivity(intent);
        }
    }

    public void awakeOnDuty() {
        if (!userStatusIsActive) {
            userStatusIsActive = true;
            btnStatus.setText("On Duty");
            btnStatus.setBackgroundResource(R.color.user_status_on);
            loadData();
        } else {
            loadData();
        }
    }

    private void showDetailLayout() {
        /*ViewGroup.LayoutParams scrollContentsParam = panelScrollContents.getLayoutParams();
        scrollContentsParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        panelScrollContents.setLayoutParams(scrollContentsParam);*/

        ViewGroup.LayoutParams mapContentsParam = panelMap.getLayoutParams();
        mapContentsParam.height = dpToPx(mContext, 300);
        panelMap.setLayoutParams(mapContentsParam);

        isFullScreen = false;
    }

    private void hideDetailLayout() {
        /*ViewGroup.LayoutParams scrollContentsParam = panelScrollContents.getLayoutParams();
        scrollContentsParam.height = LinearLayout.LayoutParams.MATCH_PARENT;
        panelScrollContents.setLayoutParams(scrollContentsParam);*/

        /*ViewGroup.LayoutParams mapContentsParam = panelMap.getLayoutParams();
        mapContentsParam.height = LinearLayout.LayoutParams.MATCH_PARENT;
        panelMap.setLayoutParams(mapContentsParam);*/


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != currentPositionMarker) {
            currentActiveMarker = marker;
        }

        // Show the delivery info
        isFullScreen = false;

        Object markerObject = marker.getTag();
        if (markerObject instanceof DeliveryItem) {
            DeliveryItem deliveryInfo = (DeliveryItem) markerObject;

            btnDetails.setTag(deliveryInfo);

            edtFromAddr.setText(deliveryInfo.getfAdd() + " " + deliveryInfo.getfCSZ());
            edtToAddr.setText(deliveryInfo.gettAdd() + " " + deliveryInfo.gettCSZ());

            LatLng fromLatLng = new LatLng(deliveryInfo.getLat(), deliveryInfo.getLon());
            LatLng toLatLng = new LatLng(deliveryInfo.getToLat(), deliveryInfo.getToLon());

            edtFromAddr.setTag(fromLatLng);
            edtToAddr.setTag(toLatLng);

            edtDistance.setText(String.format("%.2f Mile(s)", getDistanceMiles(fromLatLng, toLatLng)));

            actionIsForBid = false;

            // Show Path
            getPath(fromLatLng, toLatLng);

            /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
            alertDialogBuilder.setTitle("Confirm delivery");
            alertDialogBuilder.setMessage("Is Delivery Complete?")
                    .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            Window window = alertDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            wlp.windowAnimations = R.style.DialogAnimation;
            window.setAttributes(wlp);
            alertDialog.show();*/

            // We call this function using the button action
            // completeAndRateDelivery(deliveryInfo);

            // Show Delivery Details
            showDeliveryDetails(markerObject);
        }

        return false;
    }

    private void getPath(LatLng fromLatLng, LatLng toLatLng) {
        // Start Find the Waypoint
        if (mPolyline != null) {
            mPolyline.remove();
        }

        String directionApiPath = Helper.getUrl(String.valueOf(fromLatLng.latitude), String.valueOf(fromLatLng.longitude),
                String.valueOf(toLatLng.latitude), String.valueOf(toLatLng.longitude));
        Log.d("waypoint", "Path " + directionApiPath);
        getDirectionFromDirectionApiServer(directionApiPath);
    }

    private void showDeliveryDetails(Object deliveryDetails) {

        Intent intent = new Intent(mContext, DeliveryDetailsActivity.class);
        intent.putExtra("delivery_info", (Parcelable) deliveryDetails);
        startActivity(intent);
    }

    private void confirmBidOnDelivery(final OpenDeliveryInfo deliveryInfo) {
        if (deliveryInfo == null)
            return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_sender_profile, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        CircularProgressIndicator progressCompleted = (CircularProgressIndicator) dialogView.findViewById(R.id.progressCompleted);
        CircularProgressIndicator progressOnTime = (CircularProgressIndicator) dialogView.findViewById(R.id.progressOnTime);
        CircularProgressIndicator progressRepeat = (CircularProgressIndicator) dialogView.findViewById(R.id.progressRepeat);
        progressCompleted.setProgress(new Random().nextInt(100), 100);
        progressOnTime.setProgress(new Random().nextInt(100), 100);
        progressRepeat.setProgress(new Random().nextInt(100), 100);

        ReviewListAdapter reviewListAdapter;
        ArrayList<ReviewInfo> reviewInfos = new ArrayList<>();
        ListView lvProfiles = (ListView) dialogView.findViewById(R.id.lvProfileDate);

        reviewInfos.add(new ReviewInfo("1", "Xian G", 5.0f, "\"Great Client, Want to be hired again.\"", "", "2018-11-20"));
        reviewInfos.add(new ReviewInfo("2", "Xiao M", 5.0f, "\"Very good client. I want to server for him again.\"", "", "2018-11-21"));
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
                inputBidInfo(deliveryInfo);
            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /*private void completeAndRateDelivery(final DeliveryItem deliveryInfo) {

        if (deliveryInfo == null)
            return;

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("appid", "seekurandroid");
            GpsTracker gpsTracker = new GpsTracker(parentActivity.getApplicationContext());
            if (gpsTracker.canGetLocation()) {
                jsonObject.put("lon", String.valueOf(gpsTracker.getLongitude()));
                jsonObject.put("lat", String.valueOf(gpsTracker.getLatitude()));
            } else {
                jsonObject.put("lon", "0.0");
                jsonObject.put("lat", "0.0");
            }
            jsonObject.put("uuid", appSettings.getDeviceId());
            jsonObject.put("userid", appSettings.getUserId());
            jsonObject.put("delid", deliveryInfo.getDelID());

            jsonObject.put("statusid", "8170");
            jsonObject.put("rate", "0");
            jsonObject.put("q1", "1");
            jsonObject.put("q2", "1");

            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_rating_from_driver, null);

            final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();

            // Questions
            final RadioButton radioSignedFor = (RadioButton) dialogView.findViewById(R.id.radioSignedFor);
            final RadioButton radioAtFrontDoor = (RadioButton) dialogView.findViewById(R.id.radioAtFrontDoor);
            final RadioButton radioNeighborAccepted = (RadioButton) dialogView.findViewById(R.id.radioNeighborAccepted);
            final RadioButton radioOther = (RadioButton) dialogView.findViewById(R.id.radioOther);

            RadioGroup groupOnTime = (RadioGroup) dialogView.findViewById(R.id.groupOnTime);
            RadioGroup groupComplete = (RadioGroup) dialogView.findViewById(R.id.groupComplete);

            View.OnClickListener statusButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int viewID = view.getId();
                    try {
                        if (viewID == R.id.radioSignedFor && radioSignedFor.isChecked()) {
                            jsonObject.put("statusid", "8170");

                            radioSignedFor.setChecked(true);
                            radioAtFrontDoor.setChecked(false);
                            radioNeighborAccepted.setChecked(false);
                            radioOther.setChecked(false);
                        } else if (viewID == R.id.radioAtFrontDoor) {
                            jsonObject.put("statusid", "8172");

                            radioSignedFor.setChecked(false);
                            radioAtFrontDoor.setChecked(true);
                            radioNeighborAccepted.setChecked(false);
                            radioOther.setChecked(false);
                        } else if (viewID == R.id.radioNeighborAccepted) {
                            jsonObject.put("statusid", "8174");

                            radioSignedFor.setChecked(false);
                            radioAtFrontDoor.setChecked(false);
                            radioNeighborAccepted.setChecked(true);
                            radioOther.setChecked(false);
                        } else if (viewID == R.id.radioOther) {
                            jsonObject.put("statusid", "8180");

                            radioSignedFor.setChecked(false);
                            radioAtFrontDoor.setChecked(false);
                            radioNeighborAccepted.setChecked(false);
                            radioOther.setChecked(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            radioSignedFor.setOnClickListener(statusButtonClickListener);
            radioAtFrontDoor.setOnClickListener(statusButtonClickListener);
            radioNeighborAccepted.setOnClickListener(statusButtonClickListener);
            radioOther.setOnClickListener(statusButtonClickListener);

            groupOnTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        if (i == R.id.btnOnTimeYes) {
                            jsonObject.put("q1", "1");
                        } else if (i == R.id.btnOnTimeNo) {
                            jsonObject.put("q1", "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            groupComplete.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        if (i == R.id.btnCompleteYes) {
                            jsonObject.put("q2", "1");
                        } else if (i == R.id.btnCompleteNo) {
                            jsonObject.put("q2", "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Ratings
            final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
            final TextView tvErrorRate = (TextView) dialogView.findViewById(R.id.tvErrorRate);
            tvErrorRate.setVisibility(View.GONE);

            // Button Actions
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    inputDlg.dismiss();
                }
            });
            dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // Check current rating
                    if (ratingBar.getRating() == 0) {
                        tvErrorRate.setVisibility(View.VISIBLE);
                        return;
                    }

                    try {
                        jsonObject.put("rate", ratingBar.getRating());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    inputDlg.dismiss();

                    // Request Rating Api
                    showProgressDialog();
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    String baseUrl = BaseFunctions.getBaseData(jsonObject, parentActivity.getApplicationContext(),
                            "DelStatusRate.php", BaseFunctions.MAIN_FOLDER, parentActivity.getUserLat(), parentActivity.getUserLon(), mMyApp.getAndroidId());


                    StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgressDialog();

                            if (response != null || !response.isEmpty()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String status = jsonObject.getString("status");
                                    if (jsonObject.getBoolean("status")) {
                                        showAlert("Completed delivery! Good job.", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                notifySenderCompleteStatus(deliveryInfo);
                                                loadData();
                                            }
                                        });
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
                            showToastMessage(error.getMessage());
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
            });

            inputDlg.show();
            inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markAsPickupDelivery(final DeliveryItem deliveryInfo) {

        if (deliveryInfo == null)
            return;

        final JSONObject jsonObject = new JSONObject();

        try {
            //jsonObject.put("userid", appSettings.getUserId());
            jsonObject.put("delid", deliveryInfo.getDelID());
            jsonObject.put("statusid", "8160");

            String baseUrl = BaseFunctions.getBaseData(jsonObject, parentActivity.getApplicationContext(),
                    "DelStatusUpdate.php", BaseFunctions.MAIN_FOLDER, parentActivity.getUserLat(), parentActivity.getUserLon(), mMyApp.getAndroidId());


            // Request Rating Api
            showProgressDialog();
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
                                deliveryInfo.setPickuped(true);
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
                    showToastMessage(error.getMessage());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void notifySenderCompleteStatus(final DeliveryItem deliveryInfo) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("delid", deliveryInfo.getDelID());
            //jsonObject.put("senderid", deliveryInfo.getDelID());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(mContext);

        String baseUrl = BaseFunctions.getBaseData(jsonObject, parentActivity.getApplicationContext(),
                "DelSenderTokensByDelID.php", BaseFunctions.MAIN_FOLDER, parentActivity.getUserLat(), parentActivity.getUserLon(), mMyApp.getAndroidId());

        StringRequest sr = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();
                if (response != null || !response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (jsonObject.getBoolean("status")) {
                            ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject tokenObj = dataArray.getJSONObject(i);
                                if (tokenObj.has("Token") && !tokenObj.isNull("Token")) {
                                    // Send push directly
                                    String token = tokenObj.getString("Token");
                                    int osType = tokenObj.optInt("OS");
                                    tokenList.add(new FCMTokenData(token, osType));
                                }
                            }

                            if (tokenList.isEmpty()) {
                                showAlert("Sender has no any device to contact.");
                            } else {
                                String curUserName = String.format("%s %s", appSettings.getFN(), appSettings.getLN()).trim();
                                String title = String.format("%s completed your delivery", curUserName);
                                String addressInfo = String.format("To %s, From %s", deliveryInfo.getDelToAdd(), deliveryInfo.getFromAddress());
                                try {
                                    NotificationHelper notificationHelper = new NotificationHelper(0, mContext, (BaseActivity) mContext);
                                    JSONObject payloadsData = new JSONObject();
                                    payloadsData.put("title", title);
                                    payloadsData.put("message", addressInfo);
                                    payloadsData.put("delID", deliveryInfo.getDelID());
                                    payloadsData.put("driverName", curUserName);
                                    payloadsData.put("driverID", appSettings.getDriverID());
                                    payloadsData.put("userID", appSettings.getUserId());
                                    // TODO:  Send coordinates (NOT CALLED, COMMENTED)
                                    // tokenGetter.sendPushNotification(mContext, tokenList, PayloadType.PT_Share_Location, payloadsData);
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
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
                showToastMessage(error.getMessage());
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
        VolleySingleton.getInstance(parentActivity.getApplicationContext()).addToRequestQueue(serverRequest);
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
                        //showToastMessage("Couldn't find the Waypoints!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

        // Remove old polyline
        if (mPolyline != null)
            mPolyline.remove();

        mPolyline = map.addPolyline(options);

        // Wrap the Waypoints
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng point : positions) {
            boundsBuilder.include(point);
        }

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
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

    private void inputBidInfo(final OpenDeliveryInfo deliveryInfo) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_bid_info, null);

        final android.app.AlertDialog inputDlg = new android.app.AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        final EditText edtPrice = (EditText) dialogView.findViewById(R.id.edtPrice);
        final EditText edtETA = (EditText) dialogView.findViewById(R.id.edtETA);
        dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String price = edtPrice.getText().toString().trim();
                final String eta = edtETA.getText().toString().trim();

                /*InputMethodManager imm = (InputMethodManager)edtPrice.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                imm = (InputMethodManager)edtETA.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/

                hideKeyboard(edtPrice);
                hideKeyboard(edtETA);

                if (TextUtils.isEmpty(price)) {
                    edtPrice.setError("Invalid input");
                    edtPrice.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(eta)) {
                    edtETA.setError("Invalid input");
                    edtETA.requestFocus();
                    return;
                }

                inputDlg.dismiss();

                HashMap<String, String> params = new HashMap<>();

                GpsTracker gpsTracker = new GpsTracker(parentActivity.getApplicationContext());
                String lon, lat;
                if (gpsTracker.canGetLocation()) {
                    lon = String.valueOf(gpsTracker.getLongitude());
                    lat = String.valueOf(gpsTracker.getLatitude());
                } else {
                    lon = "0.0";
                    lat = "0.0";
                }

                String baseUrl = BaseFunctions.getBaseUrl(mContext,
                        "DelBidAdd",
                        BaseFunctions.MAIN_FOLDER,
                        lat,
                        lon,
                        mMyApp.getAndroidId());
                String extraParams =
                        "&driverID=" + appSettings.getDriverID() +
                                "&delID=" + deliveryInfo.getDelID() +
                                "&ETAmins=" + eta +
                                "&bidAmt=" + price;
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

                        Log.e("DelBidAdd", response);

                        if (response != null || !response.isEmpty()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;

                                if (jsonObject.getBoolean("status")) {

                                    showToastMessage("Bid has been offered");

                                    notifySenderBidStatus(deliveryInfo);
                                    loadData();
                                } else {
                                    showToastMessage(jsonObject.getString("msg"));
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
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputDlg.dismiss();
            }
        });

        inputDlg.show();
        inputDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void notifySenderBidStatus(final OpenDeliveryInfo deliveryInfo) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("delid", deliveryInfo.getDelID());
            //jsonObject.put("senderid", deliveryInfo.getDelID());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String baseUrl = BaseFunctions.getBaseData(jsonObject, parentActivity.getApplicationContext(),
                "DelSenderTokensByDelID.php", BaseFunctions.MAIN_FOLDER, parentActivity.getUserLat(), parentActivity.getUserLon(), mMyApp.getAndroidId());
        showProgressDialog();
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
                            NotificationHelper notificationHelper = new NotificationHelper(0, mContext, (BaseActivity) mContext);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(dataArray);
                            if (tokenList.isEmpty()) {
                                showAlert("Sender has no any device to contact.");
                            } else {
                                String curUserName = String.format("%s %s", appSettings.getFN(), appSettings.getLN()).trim();
                                String title = String.format("%s just bid your delivery", curUserName);
                                String addressInfo = String.format("To %s, From %s", deliveryInfo.gettAdd(), deliveryInfo.getfAdd());
                                try {
                                    JSONObject payloadsData = new JSONObject();
                                    payloadsData.put("title", title);
                                    payloadsData.put("message", addressInfo);
                                    payloadsData.put("delID", deliveryInfo.getDelID());
                                    payloadsData.put("driverName", curUserName);
                                    payloadsData.put("driverID", appSettings.getDriverID());
                                    payloadsData.put("userID", appSettings.getUserId());
                                    notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Share_Location, payloadsData);
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                }
                            }
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
                showToastMessage(error.getMessage());
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

    Marker currentPositionMarker;
    Marker currentActiveMarker;
    private ArrayList<Marker> bidderMarkers = new ArrayList<>();

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

        if (deliveriesInfoBySender.isEmpty()) {
            updateCamera(location);
        }
    }

    public void updateCamera(@Nullable LatLng latLng) {
        if (latLng == null) {
            return;
        }
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_ZOOM_LEVEL);
        mMap.animateCamera(location, 1000, null);
    }

    public void removeOldMarkers(ArrayList<Marker> markers) {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toLatitude = address.getLatitude();
                        toLongitude = address.getLongitude();
                        //updateDistance();
                    }
                });
            }
        }
    }

    private void updateDistance() {
        // Check Target Location
        if (toLatitude == 0 && toLatitude == 0) {
            return;
        }

        // Check Start Location
        if (mUserLatLng == null) {
            return;
        }

        final double MILES_PER_KILO = 0.621371;

        double distance = calculationByDistance(mUserLatLng.latitude, mUserLatLng.longitude, toLatitude, toLongitude);
        edtDistance.setText(String.format("%.1f Mile", distance * MILES_PER_KILO));
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

    private void showDirection(LatLng latLng) {
        if (latLng == null)
            return;

        String urlString = String.format("http://maps.google.com/maps?daddr=%f,%f", latLng.latitude, latLng.longitude);
        Log.e("direction", urlString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(urlString));
        //if (isPackageExisted("com.google.android.apps.maps")) {
        intent.setPackage("com.google.android.apps.maps");
        //}
        startActivity(intent);
    }

    private boolean isPackageExisted(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = parentActivity.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationUtil.onPermissionResult(requestCode, permissions, grantResults);

        // Check All Permission was granted
        boolean bAllGranted = true;
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                bAllGranted = false;
                break;
            }
        }

        if (bAllGranted && requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            // Enable Current location pick
            try {
                //mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(R.string.request_permission_hint);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
