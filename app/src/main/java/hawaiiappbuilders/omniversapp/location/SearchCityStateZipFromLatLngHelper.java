package hawaiiappbuilders.omniversapp.location;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;
import hawaiiappbuilders.omniversapp.utils.K;

public class SearchCityStateZipFromLatLngHelper {

    public interface SearchCityStateZipCallback {
        void onFailed(String message);

        void onSuccess(String formattedAddress, String city, String state, String zip);
    }

    BaseActivity activity;
    SearchCityStateZipCallback callback;
    String googleAPI = "";

    String formattedAddress = "";
    String city = "";
    String state = "";
    String zip = "";

    public SearchCityStateZipFromLatLngHelper(BaseActivity _activity, String lat, String lng, SearchCityStateZipCallback _callback) {
        this.activity = _activity;
        this.callback = _callback;
        this.googleAPI = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&location_type=ROOFTOP&result_type=street_address&key=%s", lat, lng, K.gKy(BuildConfig.G));
    }

    public void execute() {

        Log.e("Request", googleAPI);

        activity.showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(activity);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(activity);

        StringRequest sr = new StringRequest(Request.Method.GET, googleAPI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                activity.hideProgressDialog();

                Log.e("CityState", response);

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
                                formattedAddress = jsonAddrObj.getString("formatted_address");
                                // edtStreetAddr.setText(formattedAddress);
                                JSONObject locationObject = geometry.getJSONObject("location");
                                //latValue = locationObject.getDouble("lat");
                                //longValue = locationObject.getDouble("lng");

                                // Get City and State
                                JSONArray jsonAddressComponentArray = jsonAddrObj.getJSONArray("address_components");
                                for (int i = 0; i < jsonAddressComponentArray.length(); i++) {
                                    JSONObject jsonAddrComponent = jsonAddressComponentArray.getJSONObject(i);
                                    String longName = jsonAddrComponent.getString("long_name");
                                    String shortName = jsonAddrComponent.getString("short_name");
                                    String types = jsonAddrComponent.getString("types");

                                    if (types.contains("administrative_area_level_1")) {
                                        // This means state info
                                        state = shortName;
                                    } else if (types.contains("\"locality\"")) {
                                        city = longName;
                                    } else if (types.contains("postal_code")) {
                                        zip = longName;
                                    }
                                }

                                // Get Location
                                JSONObject jsonGeometryObj = jsonAddrObj.getJSONObject("geometry");
                                JSONObject jsonLocationObj = jsonGeometryObj.getJSONObject("location");
                            }
                        }

                        sendResult(formattedAddress, city, state, zip);
                    } else {
                        sendError("Google API ERROR!");
                    }
                } catch (JSONException e) {
                    sendError("Google API ERROR!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.hideProgressDialog();
                sendError(error.getMessage());
            }
        });

        sr.setShouldCache(false);
        sr.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void sendResult(String formattedAddress, String city, String state, String zip) {
        if (callback != null) {
            callback.onSuccess(formattedAddress, city, state, zip);
        }
    }

    private void sendError(String error) {
        if (callback != null) {
            callback.onFailed(error);
        }
    }
}
