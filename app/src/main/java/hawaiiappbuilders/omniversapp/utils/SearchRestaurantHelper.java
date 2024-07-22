package hawaiiappbuilders.omniversapp.utils;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchRestaurantHelper {

    public interface SearchRestaurantCallback {
        void onFailed(String message);

        void onSuccess(ArrayList<Restaurant> restaurants, int mode);
    }

    public static final String TAG = SearchRestaurantHelper.class.getSimpleName();
    BaseFunctions baseFunctions;
    /*----- mode ------
    1=close
    2=BLD
    3=fav
    4=ID
    5=type
    7=random
    8=by city*/

    public static final int MODE_NEARBY = 1;
    public static final int MODE_BLD = 2;
    public static final int MODE_FAV = 3;
    public static final int MODE_ID = 4;
    public static final int MODE_TYPE = 5;
    public static final int MODE_RANDOM = 7;
    public static final int MODE_BYCITY = 8;

    BaseActivity activity;
    String extraParams;
    SearchRestaurantCallback callback;

    int mode;

    public SearchRestaurantHelper(BaseActivity _activity, String extraParams, SearchRestaurantCallback _callback, int _mode) {
        this.activity = _activity;
        this.extraParams = extraParams;
        this.callback = _callback;
        this.mode = _mode;
        this.baseFunctions = new BaseFunctions(this.activity, TAG);
    }

    public void execute() {
        // Test Url
        //String urlSearchRest = URLResolver.getHashCode() + "cjl?DevID=1&AppName=nemtandroid&Lat=-93.801870&Lon=41.571519&UUID=3&MLID=1&Company=";
        /*String urlGetRes = String.format(URLResolver.getHashCode() + "cjl?DevID=1434741&AppName=nemtandroid&Lat=%s&Lon=%s&UUID=%s&MLID=1&Company=",
                getUserLat(), getUserLong(), mMyApp.getAndroidId(), "1");*/
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(activity,
                "cjl",
                BaseFunctions.MAIN_FOLDER,
                activity.getUserLat(),
                activity.getUserLon(),
                ((KTXApplication) activity.getApplication()).getAndroidId());
        baseUrl += extraParams;
        Log.e("Request", baseUrl);

        activity.showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(activity);

        //HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(activity);

        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("SearchRes", response);

                activity.hideProgressDialog();

                if (!TextUtils.isEmpty(response)) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        if (jsonArray.length() > 0) {
                            JSONObject firstObj = jsonArray.getJSONObject(0);
                            if (firstObj.has("status") && !firstObj.getBoolean("status")) {
                                sendError(firstObj.getString("msg"));
                                return;
                            }
                        }

                        int itemCnt = jsonArray.length();
                        ArrayList<Restaurant> restaurants = new ArrayList<>();
                        for (int i = 0; i < itemCnt; i++) {
                            JSONObject itemObj = jsonArray.getJSONObject(i);

                            Restaurant newRes = new Restaurant();

                            String rate = "0";
                            if (itemObj.has("Rating") && !itemObj.isNull("Rating")) {
                                rate = itemObj.getString("Rating");
                            }
                            newRes.set_rating(Float.parseFloat(rate));

                            newRes.set_id(itemObj.getInt("StoreID"));
                            newRes.set_industryID(itemObj.getInt("IndustryID"));

                            newRes.set_name(itemObj.getString("Co"));
                            newRes.set_wp(itemObj.optString("WP"));
                            newRes.set_address(itemObj.getString("address"));
                            newRes.set_ste(itemObj.getString("STE"));
                            newRes.set_city(itemObj.getString("City"));
                            newRes.set_st(itemObj.getString("St"));
                            newRes.set_zip(itemObj.getString("Zip"));
                            newRes.set_lattiude(itemObj.getDouble("Lat"));
                            newRes.set_longitude(itemObj.getDouble("Lon"));

                            newRes.set_dist(itemObj.getString("dist"));

                            newRes.setLink(itemObj.getString("Link"));

                            newRes.setMenuStyle(itemObj.getString("MenuStyle"));
                            newRes.setWelcomeMsg(itemObj.getString("WelcomeMsg"));
                            newRes.setTaxRate((float) itemObj.optDouble("TaxRate"));

                            newRes.setCurrTakingOrders(itemObj.optInt("CurrTakingOrders"));
                            newRes.setOrders(itemObj.optInt("DoingEatIn"));
                            newRes.setParty(itemObj.optInt("DoingParty"));
                            newRes.setCater(itemObj.optInt("DoingCater"));
                            newRes.setRes(itemObj.optInt("DoingTableRes"));
                            newRes.setDel(itemObj.optInt("DoingDel"));
                            newRes.setAppt(itemObj.optInt("DoingAppts"));
                            newRes.setCurb(itemObj.optInt("DoingCurb"));

                            newRes.setOnTable(itemObj.optInt("DoingOnTable"));
                            newRes.setPu(itemObj.optInt("DoingPU"));
                            newRes.setInternalDel(itemObj.optInt("DoingInternalDel"));

                            newRes.setMonB(itemObj.getString("MonB"));
                            newRes.setMonE(itemObj.getString("MonE"));
                            newRes.setTueB(itemObj.getString("TueB"));
                            newRes.setTueE(itemObj.getString("TueE"));
                            newRes.setWedB(itemObj.getString("WedB"));
                            newRes.setWedE(itemObj.getString("WedE"));
                            newRes.setThuB(itemObj.getString("ThuB"));
                            newRes.setThuE(itemObj.getString("ThuE"));
                            newRes.setFriB(itemObj.getString("FriB"));
                            newRes.setFriE(itemObj.getString("FriE"));
                            newRes.setSatB(itemObj.getString("SatB"));
                            newRes.setSatE(itemObj.getString("SatE"));
                            newRes.setSunB(itemObj.getString("SunB"));
                            newRes.setSunE(itemObj.getString("SunE"));

                            newRes.setClosed(itemObj.getInt("closed"));

                            newRes.setTableFee(itemObj.optDouble("onTableFee"));
                            newRes.setPartyFee(itemObj.optDouble("PartyFee"));
                            newRes.setDelFee(itemObj.optDouble("DelFee"));
                            newRes.setResFee(itemObj.optDouble("ResFee"));
                            newRes.setCatFee(itemObj.optDouble("CatFee"));
                            newRes.setApptFee(itemObj.optDouble("ApptFee"));

                            newRes.setSeekIT(itemObj.optInt("seekIT"));  //{"dist":9256.0276274040934,"WP":"","ownerID":4,"advertised":0,"Rating":0,"UTID":0,"vid":0,"IndustryID":123,"StoreID":1383766,"Co":"Eagle Lake Sporting Camps","address":"T16 R6 23, Furlong Road","STE":"","City":"Eagle Lake","St":"ME","Zip":"","Lat":47.0547050000,"Lon":-68.4731080000,"TaxRate":0.07,"MenuStyle":0,"WelcomeMsg":"","CurrWait":0,"CurrWaitTable":0,"PartyFee":0.00,"DelFee":0.00,"ResFee":0.00,"CatFee":0.00,"ApptFee":0.00,"LoyaltyMthlyFee":0.00,"LoyaltyEachFee":0.00,"DoingTableRes":0,"DoingOnTable":0,"DoingEatIn":0,"DoingPU":0,"DoingPU":0,"DoingCurb":0,"CurrTakingOrders":0,"DoingParty":0,"DoingCater":0,"DoingDel":0,"DoingAppts":0,"MonB":"11:00:00","MonE":"23:00:00","TueB":"11:00:00","TueE":"23:00:00","WedB":"11:00:00","WedE":"23:00:00","ThuB":"11:00:00","ThuE":"23:00:00","FriB":"11:00:00","FriE":"23:00:00","SatB":"11:00:00","SatE":"23:00:00","SunB":"11:00","SunE":"23:00","Title":null,"Link":null,"closed":0,"seekIT":0}
                            newRes.setUTID(itemObj.optInt("UTID"));

                            restaurants.add(newRes);
                        }

                        sendResult(restaurants);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        sendError(e.getMessage());
                    }
                } else {
                    sendError("Server Error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.hideProgressDialog();
                baseFunctions.handleVolleyError(activity, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        sr.setShouldCache(false);
        sr.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void sendResult(ArrayList<Restaurant> restaurants) {
        if (callback != null) {
            callback.onSuccess(restaurants, mode);
        }
    }

    private void sendError(String error) {
        if (callback != null) {
            callback.onFailed(error);
        }
    }
}
