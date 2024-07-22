package hawaiiappbuilders.omniversapp.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import java.text.DecimalFormat;

public class UpdateCashBroadcast {
    private static final String TAG = UpdateCashBroadcast.class.getSimpleName();

    public static void bc(Context context, final String lat, final String lon, final TextView textview) {
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "onReceive: ===============================" );
                //getInstaCash(context,lat,lon,textview);
            }
        }, new IntentFilter("my-custom-event"));
    }

    public static void sendBroadcast(Context context) {
        Intent intent = new Intent("my-custom-event");
        intent.putExtra("foo", "bar");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

   /* public static void getInstaCash(final Context context, String lat, String lon, final TextView textView) {
        AppSettings appSettings = new AppSettings(context);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("devid", K.gKy(BuildConfig.DevID));
            jsonObject.put("appid","seekurandroid");
            jsonObject.put("lat",lat);
            jsonObject.put("lon",lon);
            jsonObject.put("mlid",appSettings.getUserId());
            jsonObject.put("uuid",appSettings.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String encoded = jsonObject.toString().trim().replaceAll("\\s+","");
        try {
            encoded = URLEncoder.encode(encoded,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://chuck.com/CJL/"+"ICcjl.php?".concat("data="+encoded);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.e("BaseActivity", "onResponse: "+response );
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        JSONArray data = jsonObject.getJSONArray("data");
                        String instaCash = data.getJSONObject(0).getString("InstaCash");

                        textView.setText("Balance : $ "+formatMoney(instaCash));
                    }
                    else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
*/
    public static String formatMoney(String number) {
        try {
            double amount = Double.parseDouble(number);
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            System.out.println(formatter.format(amount));
            return formatter.format(amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }
}
