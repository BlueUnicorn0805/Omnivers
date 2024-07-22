package hawaiiappbuilders.omniversapp.utils;


import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;

public class BaseFunctions {
    public static final int MAIN_FOLDER = 1;
    public static final int SEARCH_FOLDER = 2;
    public static final int SIGN_UP_FOLDER = 3;
    public static final int ORDER_FOLDER = 4;
    public static final int APP_FOLDER = 5;
    public static final int MAIL_FOLDER = 6;
    public static final int WP_FOLDER = 7;
    public static final int STORE_FOLDER = 8;
    public static final int R2 = 71024;
    Context context;
    String tag;

    public BaseFunctions(Context context, String TAG) {
        this.context = context;
        this.tag = TAG;
    }

    public static String getApiName(String baseUrl) {
        String apiName = "none";
        if (baseUrl.split("/").length >= 5) {
            apiName = baseUrl.split("/")[4].split(Pattern.quote("?"))[0];
        }
        return apiName;
    }

    public void handleVolleyError(Context context, VolleyError error, String TAG, String apiName) {
        if (context != null) {
            if (!BaseActivity.isOnline(context)) {
                BaseActivity.showAlertMessage(context, "Internet not available");
            } else {
                networkErrorHandle(context, error, TAG, apiName);
            }
        }
    }

    public void networkErrorHandle(Context mContext, VolleyError error, String TAG, String apiName) {

        String message = "An error occurred";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            // This indicates that the request has either time out or there is no connection
            message = "The request has either time out or there is no Connection.";
        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            message = "There was an Authentication Failure.";
        } else if (error instanceof ServerError) {
            // Indicates that the server responded with a error response
            message = "The Server is not responding.";
        } else if (error instanceof NetworkError) {
            // Indicates that there was network error while performing the request
            message = "There was network error, check your network!";
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            message = "The server response could not be parsed.";
        } else {
            message = "An error occurred";
        }
        if (context != null) {
            DataUtil dataUtil = new DataUtil(context, "BaseFunctions");
            dataUtil.setActivityName(TAG);
            BaseActivity.showAlertMessage(context, message);
            dataUtil.zzzLogMessage(1, message, apiName);
        }
    }


    public static int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }

    public static String getBaseData(JSONObject dataParameters, Context context, String apiName, int folderId, String lat, String lon, String uuid) {
        byte[] decodedBytes = Base64.decode(BuildConfig.DevID, Base64.DEFAULT);

        // Convert the bytes to an integer
        int intValue = bytesToInt(decodedBytes);

        // Now intValue holds the decoded integer value
        Log.d("Converted Integer", String.valueOf(intValue));
        final int currVer = intValue;  //1435712;//50922;
        int R1 = currVer;

        byte[] folder;
        switch (folderId) {
            case 1:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6e, 0x2f}; // mainFolder
                break;
            case 2:
                folder = new byte[]{0x73, 0x65, 0x61, 0x72, 0x63, 0x68, 0x2f}; // searchFolder
                break;
            case 3:
                folder = new byte[]{0x73, 0x69, 0x67, 0x6e, 0x75, 0x70, 0x2f}; //signupFolder
                break;
            case 4:
                folder = new byte[]{0x6f, 0x72, 0x64, 0x65, 0x72, 0x2f}; // orderFolder
                break;
            case 5:
                folder = new byte[]{0x61, 0x70, 0x70, 0x2f}; // appFolder
                break;
            case 6:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6c, 0x2f}; // mailFolder
                break;
            case 7:
                folder = new byte[]{0x77, 0x70, 0x2f}; // wpFolder
                break;
            case 8:
                folder = new byte[]{0x73, 0x74, 0x6f, 0x72, 0x65, 0x2f}; // storeFolder
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + folderId);
        }

        byte[] baseUrl = {0x68, 0x74, 0x74, 0x70, 0x73, 0x3a, 0x2f, 0x2f, 0x67, 0x65, 0x74, 0x66, 0x6f, 0x6f, 0x64, 0x2e, 0x61, 0x7a, 0x75, 0x72, 0x65, 0x77, 0x65, 0x62, 0x73, 0x69, 0x74, 0x65, 0x73, 0x2e, 0x6e, 0x65, 0x74, 0x2f};

        // D1
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.hack_date));
        String dateString = dateFormat.format(date);
        String[] hacks = dateString.split("-");
        String D1 = String.valueOf(Integer.parseInt(hacks[2]));

        String H1 = "182";//BuildConfig.H1;


        // P1
        int year = Integer.parseInt(hacks[0]);
        int month = Integer.parseInt(hacks[1]);
        int day = Integer.parseInt(hacks[2]);
        int hour = Integer.parseInt(hacks[3]);
        int hackBase = 0, p1 = 0;

        hackBase = day * day * hour;
        p1 = hackBase + year + month + day + hour;
        String P1 = String.valueOf(p1);

        AppSettings appSettings = new AppSettings(context);

        String baseUrlString = "";
        String folderString = "";
        baseUrlString = new String(baseUrl, StandardCharsets.UTF_8);
        folderString = new String(folder, StandardCharsets.UTF_8);

//        Date date1 = new Date();
//        SimpleDateFormat dateFormat2 = new SimpleDateFormat("Mddyy");
//        String R2 = dateFormat2.format(date1);
//        Log.e("TAG", "getBaseData: " + R2);
        // Create base parameters json object
        JSONObject baseParametersObject = new JSONObject();
        try {
            baseParametersObject.put("P1", P1);
            baseParametersObject.put("R1", R1);
            baseParametersObject.put("R2", R2/*Integer.parseInt(R2)*/);
            baseParametersObject.put("D1", D1);
            baseParametersObject.put("H1", H1);
//            baseParametersObject.put("devid", 1435754);
            baseParametersObject.put("devid", 1436411);
            baseParametersObject.put("appname", ourAppname());
            //
            baseParametersObject.put("utc", appSettings.getUTC());
            baseParametersObject.put("cid", appSettings.getUserId());
            baseParametersObject.put("workid", appSettings.getWorkid());
            baseParametersObject.put("empid", appSettings.getEmpId());
            baseParametersObject.put("lon", lon);
            baseParametersObject.put("lat", lat);
            baseParametersObject.put("uuid", uuid);
            baseParametersObject.put("EricaAndreaAaron", 7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject mergedData = new JSONObject();
        Iterator<String> keys = baseParametersObject.keys(); // iterate keys from json object
        while (keys.hasNext()) {
            String key = keys.next();
            Object nestedObject;
            try {
                nestedObject = baseParametersObject.get(key);
                mergedData.put(key, nestedObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Iterator<String> dataKeys = dataParameters.keys(); // iterate keys from json object
        while (dataKeys.hasNext()) {
            String key = dataKeys.next();
            Object nestedObject;
            try {
                nestedObject = dataParameters.get(key);
                mergedData.put(key, nestedObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String rtn = "";
        String mergedDataString = mergedData.toString().trim();

        Log.e("data=", mergedDataString);
        try {
            mergedDataString = URLEncoder.encode(mergedDataString, "UTF-8");
            String jsonData = String.format("data=%s", mergedDataString.replaceAll("\\+", "%20"));
            rtn = baseUrlString + folderString + apiName + "?" +
                    jsonData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public static String getBaseUrl(Context context, String apiName, int folderId, String lat, String lon, String uuid) {
        byte[] decodedBytes = Base64.decode(BuildConfig.DevID, Base64.DEFAULT);

        // Convert the bytes to an integer
        int intValue = bytesToInt(decodedBytes);
        final int currVer = intValue;  // 50922;
        int R1 = currVer;

        byte[] folder;

        switch (folderId) {
            case 1:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6e, 0x2f}; // mainFolder
                break;
            case 2:
                folder = new byte[]{0x73, 0x65, 0x61, 0x72, 0x63, 0x68, 0x2f}; // searchFolder
                break;
            case 3:
                folder = new byte[]{0x73, 0x69, 0x67, 0x6e, 0x75, 0x70, 0x2f}; //signupFolder
                break;
            case 4:
                folder = new byte[]{0x6f, 0x72, 0x64, 0x65, 0x72, 0x2f}; // orderFolder
                break;
            case 5:
                folder = new byte[]{0x61, 0x70, 0x70, 0x2f}; // appFolder
                break;
            case 6:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6c, 0x2f}; // mailFolder
                break;
            case 7:
                folder = new byte[]{0x77, 0x70, 0x2f}; // wpFolder
                break;
            case 8:
                folder = new byte[]{0x73, 0x74, 0x6f, 0x72, 0x65, 0x2f}; // storeFolder
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + folderId);
        }

        byte[] baseUrl = {0x68, 0x74, 0x74, 0x70, 0x73, 0x3a, 0x2f, 0x2f, 0x67, 0x65, 0x74, 0x66, 0x6f, 0x6f, 0x64, 0x2e, 0x61, 0x7a, 0x75, 0x72, 0x65, 0x77, 0x65, 0x62, 0x73, 0x69, 0x74, 0x65, 0x73, 0x2e, 0x6e, 0x65, 0x74, 0x2f};

        // D1
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.hack_date));
        String dateString = dateFormat.format(date);
        String[] hacks = dateString.split("-");
        String D1 = String.valueOf(Integer.parseInt(hacks[2]));

       /* Date date1 = new Date();
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("Mddyy");
        String R2 = dateFormat2.format(date1);
        Log.e("TAG", "getBaseUrl: " + R2);*/

        String H1 = "182";

        // P1
        int year = Integer.parseInt(hacks[0]);
        int month = Integer.parseInt(hacks[1]);
        int day = Integer.parseInt(hacks[2]);
        int hour = Integer.parseInt(hacks[3]);
        int hackBase = 0, p1 = 0;

        hackBase = day * day * hour;
        p1 = hackBase + year + month + day + hour;
        String P1 = String.valueOf(p1);

        AppSettings appSettings = new AppSettings(context);

        String baseUrlString = "";
        String folderString = "";
        baseUrlString = new String(baseUrl, StandardCharsets.UTF_8);
        folderString = new String(folder, StandardCharsets.UTF_8);

        /*"1435712"*/

        String rtn = baseUrlString + folderString + apiName + "?" +
                "P1=" + P1 +
                "&R1=" + R1 +
                "&R2=" + R2 +
                "&D1=" + D1 +
                "&H1=" + H1 +
//                "&devid=1435754" +
                "&devid=1436411" +
                "&appname=" + ourAppname() +
                "&utc=" + appSettings.getUTC() +
                "&cid=" + appSettings.getUserId() +
                "&workid=" + appSettings.getWorkid() +
                "&empid=" + appSettings.getEmpId() +
                "&lon=" + lon +
                "&lat=" + lat +
                "&uuid=" + uuid +
                "&EricaAndreaAaron=7";

        return rtn;
    }

    public static String getBaseUrlForRegistration(Context context, String apiName, int folderId, String lat, String lon, String uuid) {
        byte[] decodedBytes = Base64.decode(BuildConfig.DevID, Base64.DEFAULT);

        // Convert the bytes to an integer
        int intValue = bytesToInt(decodedBytes);
        final int currVer = intValue;  //50922;
        int R1 = currVer;

        byte[] folder;

        switch (folderId) {
            case 1:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6e, 0x2f}; // mainFolder
                break;
            case 2:
                folder = new byte[]{0x73, 0x65, 0x61, 0x72, 0x63, 0x68, 0x2f}; // searchFolder
                break;
            case 3:
                folder = new byte[]{0x73, 0x69, 0x67, 0x6e, 0x75, 0x70, 0x2f}; //signupFolder
                break;
            case 4:
                folder = new byte[]{0x6f, 0x72, 0x64, 0x65, 0x72, 0x2f}; // orderFolder
                break;
            case 5:
                folder = new byte[]{0x61, 0x70, 0x70, 0x2f}; // appFolder
                break;
            case 6:
                folder = new byte[]{0x6d, 0x61, 0x69, 0x6c, 0x2f}; // mailFolder
                break;
            case 7:
                folder = new byte[]{0x77, 0x70, 0x2f}; // wpFolder
                break;
            case 8:
                folder = new byte[]{0x73, 0x74, 0x6f, 0x72, 0x65, 0x2f}; // storeFolder
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + folderId);
        }

        byte[] baseUrl = {0x68, 0x74, 0x74, 0x70, 0x73, 0x3a, 0x2f, 0x2f, 0x67, 0x65, 0x74, 0x66, 0x6f, 0x6f, 0x64, 0x2e, 0x61, 0x7a, 0x75, 0x72, 0x65, 0x77, 0x65, 0x62, 0x73, 0x69, 0x74, 0x65, 0x73, 0x2e, 0x6e, 0x65, 0x74, 0x2f};

        // D1
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.hack_date));
        String dateString = dateFormat.format(date);
        String[] hacks = dateString.split("-");
        String D1 = String.valueOf(Integer.parseInt(hacks[2]));

        String H1 = "182";

        // P1
        int year = Integer.parseInt(hacks[0]);
        int month = Integer.parseInt(hacks[1]);
        int day = Integer.parseInt(hacks[2]);
        int hour = Integer.parseInt(hacks[3]);
        int hackBase = 0, p1 = 0;

        hackBase = day * day * hour;
        p1 = hackBase + year + month + day + hour;
        String P1 = String.valueOf(p1);

        Date date1 = new Date();
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("Mddyy");
        String R2 = dateFormat2.format(date1);
        Log.e("TAG", "getBaseUrlForRegistration: " + R2);

        AppSettings appSettings = new AppSettings(context);

        String baseUrlString = "";
        String folderString = "";
        baseUrlString = new String(baseUrl, StandardCharsets.UTF_8);
        folderString = new String(folder, StandardCharsets.UTF_8);

        String rtn = baseUrlString + folderString + apiName + "?" +
                "P1=" + P1 +
                "&R1=" + R1 +
                "&R2=" + R2 +
                "&D1=" + D1 +
                "&H1=" + H1 +
//                "&devid=1435754" +
                "&devid=1436411" +
                "&appname=" + ourAppname() +
                "&utc=" + appSettings.getUTC() +
                "&cid=0" +
                "&workid=" + appSettings.getWorkid() +
                "&empid=" + appSettings.getEmpId() +
                "&lon=" + lon +
                "&lat=" + lat +
                "&uuid=" + uuid +
                "&EricaAndreaAaron=7";


        return rtn;
    }

    public static String getPreAuthUrl(Context context) {
        byte[] folder = new byte[]{0x6d, 0x61, 0x69, 0x6e, 0x2f}; // mainFolder
        byte[] baseUrl = {0x68, 0x74, 0x74, 0x70, 0x73, 0x3a, 0x2f, 0x2f, 0x67, 0x65, 0x74, 0x66, 0x6f, 0x6f, 0x64, 0x2e, 0x61, 0x7a, 0x75, 0x72, 0x65, 0x77, 0x65, 0x62, 0x73, 0x69, 0x74, 0x65, 0x73, 0x2e, 0x6e, 0x65, 0x74, 0x2f};

        // D1
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.hack_date));
        String dateString = dateFormat.format(date);
        String[] hacks = dateString.split("-");
        String D1 = String.valueOf(Integer.parseInt(hacks[2]));

        AppSettings appSettings = new AppSettings(context);

        String baseUrlString = "";
        String folderString = "";
        baseUrlString = new String(baseUrl, StandardCharsets.UTF_8);
        folderString = new String(folder, StandardCharsets.UTF_8);

        BaseActivity activity = (BaseActivity) context;
        KTXApplication app = (KTXApplication) activity.getApplication();

        return baseUrlString + folderString + "PreAuth" + "?" +
                "D1=" + D1 +
                "&appname=" + ourAppname() +
                "&utc=" + appSettings.getUTC() +
                "&cid=" + appSettings.getUserId() +
                "&workid=" + appSettings.getWorkid() +
                "&empid=" + appSettings.getEmpId() +
                "&lon=" + activity.getUserLon() +
                "&lat=" + activity.getUserLat() +
                "&uuid=" + app.getAndroidId();
    }

    private static String ourAppname() {
        return "OmniAndroid";
    }

    public static String decodeBaseURL(String url){
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
