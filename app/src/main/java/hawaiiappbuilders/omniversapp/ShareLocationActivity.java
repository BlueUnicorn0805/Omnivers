package hawaiiappbuilders.omniversapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.messaging.PayloadType;
import hawaiiappbuilders.omniversapp.messaging.NotificationHelper;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.model.Restaurant;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;

public class ShareLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    Restaurant restaurant;

    Toolbar toolbar;
    MapFragment mapFragment;
    GoogleMap googleMap;
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sharelocation);

        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra("restaurant");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.restMap);
        mapFragment.getMapAsync(this);

        Button bthShareResDir = findViewById(R.id.btnShareStoreLocation);
        Button bthShareMeDir = findViewById(R.id.btnShareMyLocation);

        bthShareResDir.setOnClickListener(this);
        bthShareMeDir.setOnClickListener(this);

        if (restaurant == null) {
            bthShareResDir.setVisibility(View.GONE);
        }

        edtEmail = findViewById(R.id.edtEmail);
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

        LatLng resPos;
        if (restaurant == null) {
            if (getLocation()) {
                resPos = new LatLng(Double.parseDouble(getUserLat()), Double.parseDouble(getUserLon()));
                map.addMarker(new MarkerOptions().position(resPos).title("You're here"));

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(resPos)
                        .zoom(11)
                        .bearing(0)
                        .build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
            }
        } else {
            resPos = new LatLng(restaurant.get_lattiude(), restaurant.get_longitude());
            map.addMarker(new MarkerOptions().position(resPos).title(restaurant.get_name()));

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(resPos)
                    .zoom(11)
                    .bearing(0)
                    .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void shareLocation(boolean shareStore) {
        String email = edtEmail.getText().toString().trim();
        hideKeyboard(edtEmail);

        if (TextUtils.isEmpty(email)) {
            shareViaEmail(shareStore, email);
        } else {
            Log.e("tokenFromEmail-My", appSettings.getDeviceToken());

            HashMap<String, String> params = new HashMap<>();
            String baseUrl = BaseFunctions.getBaseUrl(this,
                    "tokenFromEmail",
                    BaseFunctions.MAIN_FOLDER,
                    getUserLat(),
                    getUserLon(),
                    mMyApp.getAndroidId());
            String extraParams =
                    "&mode=" + "0" +
                            "&email=" + email +
                            "&promoID=" + "8";
            baseUrl += extraParams;
            Log.e("Request", baseUrl);

            showProgressDialog();
            RequestQueue queue = Volley.newRequestQueue(mContext);

            //HttpsTrustManager.allowAllSSL();
            GoogleCertProvider.install(mContext);

            StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgressDialog();

                    Log.e("tokenFromEmail", response);

                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                //showToastMessage("No Token Found");
                                //return;
                            }
                            JSONObject jsonObject = jsonArray.getJSONObject(0)/*new JSONObject(response)*/;
                            if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                                // showToastMessage(jsonObject.optString("msg"));
                            } else {
                                //tokenList.add("dy50Tx0D2kfroWYKIRDqv8:APA91bH1JkzXHJfF5_UTHrV04nUPHw1AckUXBZa1dGSwe6u9pA1ibl_TXM-_UDUX6wXJKIV_w_0fsC2IficzVRlA3aBH8r1jtD25UrxE6uMWlUgnncSLHeX85XK8cqkqdZGS60OIDWJR");
                                JSONObject payloadsData = new JSONObject();
                                NotificationHelper notificationHelper = new NotificationHelper(appSettings.getUserId(), mContext, (BaseActivity) mContext);
                                ArrayList<FCMTokenData> tokenList = notificationHelper.getTokenList(jsonArray);
                                // XiaoPOS
                                // tokenList.add(new FCMTokenData("f4RDiMTwSG8:APA91bEIPNcUk3oSjrzraQY4nf_Vc4xK0Pjvm8Ku2iSDYa6QNm1Xd2XvPw_08WD3ejBeBt80Qk9Y4Y5OC1PC4EzcYDFYOtpq-XBDob-MK0UObDsM9X1hXpLgEeq1xLyKVJIXxbAttL68", FCMTokenData.OS_UNKNOWN));
                                if (!tokenList.isEmpty()) {
                                    JSONObject payload = new JSONObject();
                                    String message = "Invited you to the current location.";
                                    if (shareStore) {
                                        message = "Invited you to the restaurant.";
                                        payloadsData.put("lat", restaurant.get_lattiude());
                                        payloadsData.put("lon", restaurant.get_longitude());
                                    } else {
                                        payloadsData.put("lat", getUserLat()); /*"41.806928"*/
                                        payloadsData.put("lon", getUserLon()); /*"123.384994"*/
                                    }

                                    payload.put("message", message);
                                    payloadsData.put("zoom", googleMap.getCameraPosition().zoom);
                                    payload.put("SenderName", appSettings.getFN() + " " + appSettings.getLN());
                                    payload.put("SenderID", appSettings.getUserId());
                                    notificationHelper.sendPushNotification(mContext, tokenList, PayloadType.PT_Share_Location, payload);
                                } else {
                                    shareViaEmail(shareStore, email);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(e.getMessage());
                        }
                    } else {
                        shareViaEmail(shareStore, email);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressDialog();
                    if (TextUtils.isEmpty(error.getMessage())) {
                        showAlert(R.string.error_conn_error);
                    } else {
                        showAlert(error.getMessage());
                    }
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
    }

    private void shareViaEmail(boolean storeLocaion, String email) {
        String mapLocation = "";
        if (storeLocaion) {
            mapLocation = "https://www.google.com/maps/@?api=1&map_action=map&center=" + restaurant.get_lattiude() + "%2C" + restaurant.get_longitude() + "&zoom=15&basemap=roadmap";
        } else {
            mapLocation = "https://www.google.com/maps/@?api=1&map_action=map&center=" + getUserLat() + "%2C" + getUserLon() + "&zoom=15&basemap=roadmap";
        }
        String suffix = "\n\nFor more information use link below:\nwww.YEStaurants.com";
        mapLocation += suffix;

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent == null) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            if (!TextUtils.isEmpty(email)) {
                String[] supportTeamAddrs = {email};
                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
            }
            intent.putExtra(Intent.EXTRA_SUBJECT, "Location");
            intent.putExtra(Intent.EXTRA_TEXT, mapLocation);
            try {
                /*if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    showToastMessage("Please install Email App to use function");
                }*/
                startActivity(intent);
            } catch (Exception e) {
                showToastMessage("Please install Email App to use function");
            }
        } else {
            intent=new Intent(Intent.ACTION_SEND);
            if (!TextUtils.isEmpty(email)) {
                String[] supportTeamAddrs = {email};
                intent.putExtra(Intent.EXTRA_EMAIL, supportTeamAddrs);
            }

            intent.putExtra(Intent.EXTRA_SUBJECT,"Location");
            intent.putExtra(Intent.EXTRA_TEXT,mapLocation);
            //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send Location Mail"));
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btnShareStoreLocation) {
            shareLocation(true);
        } else if (viewId == R.id.btnShareMyLocation) {
            shareLocation(false);
        }
    }
}
