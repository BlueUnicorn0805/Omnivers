package hawaiiappbuilders.omniversapp.messaging;


import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Accept_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Advertisement_Industry_Offer;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Cancel_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Consumer_Parcel_Req;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Decline_New_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Accepted;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Delivery_Almost_There;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Delivery_On_Site;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Delivery_Placed_Valid_Spot;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Failed;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Get_Order_By_Item_Count;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_Headed_Next_Stop;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Delivery_In_Route_To_Customer;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Dial_Phone;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Driver_Food_Grab;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Driver_Food_Req;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Driver_Parcel_Bid;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Emergency_Unlock;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Follow_My_Location;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Funds_Sent;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_INCOMING_VIDEO_CALL;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Invoice_Paid;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Invoice_Sent;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Just_Ordered;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Lock_Out;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Log_Attempt_Open_Door;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Log_Vehicle_Door_Opened;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_New_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Order_Status;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Propose_Reschedule_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Reschedule_Accepted;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Reschedule_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Reschedule_Declined;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Accepted;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Complete;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Preparing;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Refused;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Service_Requested;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Rest_Table_Ready;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Set_Date_Range;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Share_Location;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Share_My_Info;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Share_Task;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Store_Total_Sale_Be_Prepared;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Text_Message;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Text_Message_W_Subject;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Text_W_Pictures;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Accept;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Confirm;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Decline;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Get_Car;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Park_Location;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Valet_Req;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.UpdateCashBroadcast;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.messaging.model.NotificationTitleMessage;
import hawaiiappbuilders.omniversapp.messaging.model.PushData;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.utils.GoogleCertProvider;


public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();

    BaseFunctions baseFunctions;
    OnGetTokenListener callBack;
    Context context;
    BaseActivity parentActivity;
    String lon;
    String lat;

    int tokenMlid;
    static String channelId = "HawaiiAppsAloha";

    public NotificationHelper(int tokenMlid, Context context, BaseActivity parentActivity, String lat, String lon) {
        // initializing the callback object from the constructor
        this.callBack = null;
        this.context = context;
        this.parentActivity = parentActivity;
        this.lon = lon;
        this.lat = lat;
        this.tokenMlid = tokenMlid;
        this.baseFunctions = new BaseFunctions(this.context, TAG);
    }

    public NotificationHelper(int tokenMlid, Context context, BaseActivity parentActivity) {
        // initializing the callback object from the constructor
        this.callBack = null;
        this.context = context;
        this.parentActivity = parentActivity;
        if (parentActivity.getLocation()) {
            this.lon = parentActivity.getUserLon();
            this.lat = parentActivity.getUserLat();
        }
        this.tokenMlid = tokenMlid;
    }

    public void getToken(int payloadType, JSONObject payloadsData, OnGetTokenListener callBack) {
        this.callBack = callBack;
        if (parentActivity.getLocation()) {
            KTXApplication application = (KTXApplication) parentActivity.getApplication();
            cjlGetToken(payloadType, payloadsData, context, parentActivity, tokenMlid, lat, lon, application.getAndroidId());
        }
    }

    public void cjlGetToken(int payloadType, JSONObject payloadsData, Context context, BaseActivity parentActivity, int tokenMlid, String lat, String lon, String androidId) {
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(context,
                "CJLGetToken",
                BaseFunctions.MAIN_FOLDER,
                lat,
                lon,
                androidId);


        String mode = "0";
        // storeowner, pos
        if (payloadType == PT_Text_Message) {
            mode = "7";
        }

        String extraParams =
                "&mode=" + mode +
                        "&TokenMLID=" + tokenMlid;
        baseUrl += extraParams;
        Log.i("Request", baseUrl);

        parentActivity.showProgressDialog();
        RequestQueue queue = Volley.newRequestQueue(context);
        // HttpsTrustManager.allowAllSSL();
        GoogleCertProvider.install(context);
        String finalBaseUrl = baseUrl;
        StringRequest sr = new StringRequest(Request.Method.POST, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parentActivity.hideProgressDialog();
                Log.i("CJLGetToken", response);
                prepareDataForPush(response, payloadsData, payloadType);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                baseFunctions.handleVolleyError(context, error, TAG, BaseFunctions.getApiName(finalBaseUrl));
                callBack.onVolleyError(error);
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

    private void prepareDataForPush(String response, JSONObject payloadsData, int payloadType) {
        if (response != null && response.length() > 15) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() == 0) {
                    callBack.onJsonArrayEmpty();
                } else {
                    ArrayList<FCMTokenData> tokenList = new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    if (jsonObject.has("status") && !jsonObject.getBoolean("status")) {
                        parentActivity.showToastMessage(jsonObject.optString("msg"));
                    } else {
                        callBack.onSuccess(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            String token = jsonData.getString("Token");
                            int osType = jsonData.optInt("OS");
                            tokenList.add(new FCMTokenData(token, osType));
                        }
                        callBack.onFinishPopulateTokenList(tokenList);
                    }

                    if (!tokenList.isEmpty()) {
                        /*"data": {
                            "payloadtype": payloadType,
                            "SenderID": "",
                            "name": "",
                            "fn": "",
                            "ln": "",
                            "co": "",
                            "email": "",
                            "siteName":  "",
                            "imgURL"
                            "payloads": {
                                // payloadType specific data
                            },
                             "message": "" // will be used as the notification contentText
                        }*/
                        sendPushNotification(context, tokenList, payloadType, payloadsData);
                    } else {
                        callBack.onTokenListEmpty();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                callBack.onJsonException();
            }
        } else {
            callBack.onEmptyResponse();
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<FCMTokenData> getTokenList(JSONArray jsonArray) {
        ArrayList<FCMTokenData> tokenList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String token = jsonData.getString("Token");
                int osType = jsonData.optInt("OS");
                tokenList.add(new FCMTokenData(token, osType));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tokenList;
    }

    /**
     * Sends a push notification
     *
     * @param context      The context
     * @param tokenList    The list of device tokens to push
     * @param payloadType  The type of push notification to send
     * @param payloadsData The payloadType specific data to be sent inside "payloads"
     */
    public void sendPushNotification(Context context, ArrayList<FCMTokenData> tokenList, int payloadType, JSONObject payloadsData) {
        AppSettings appSettings = new AppSettings(context);
        try {
            JSONObject fcmData = new JSONObject();

            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.HOUR, appSettings.getUTC());
            String timesent = DateUtil.dateToString(new Date(calendar.getTimeInMillis()), DateUtil.DATE_FORMAT_38);
            String name = appSettings.getFN() + " " + appSettings.getLN();
            fcmData.put("payloadtype", payloadType);
            fcmData.put("SenderID", appSettings.getUserId());
            fcmData.put("email", appSettings.getEmail());
            fcmData.put("fn", appSettings.getFN());
            fcmData.put("ln", appSettings.getLN());
            fcmData.put("name", name);
            fcmData.put("co", appSettings.getCompany());
            fcmData.put("timesent", timesent);
//            String agoraToken = payloadsData.getString("agoraToken");
//            String agoraId = "";
//            fcmData.put("agoraToken", agoraToken);
//            String fromFcmToken = payloadsData.getString("fromFcmToken");
//            fcmData.put("fromFcmToken", fromFcmToken);
//            fcmData.put("fcmToken", tokenList.get(0));
//            fcmData.put("agoraId", payloadsData.getString("agoraId"));

            String body = "";
            String title = "";
            if (payloadsData != null) {
                fcmData.put("payloads", payloadsData);
                if (payloadsData.has("message")) {
                    fcmData.put("message", payloadsData.get("message"));
                    body = payloadsData.getString("message");
                }

                if (payloadsData.has("subject")) {
                    fcmData.put("subject", payloadsData.get("subject"));
                    title = payloadsData.getString("subject");
                }
            }
            Bundle bundle = new Bundle();
            try {
                JSONObject jsonObject = new JSONObject(fcmData.toString());
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, (Integer) value);
                    } else if (value instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, (Double) value);
                    } else if (value instanceof Float) {
                        bundle.putFloat(key, (Float) value);
                    } else if (value instanceof Long) {
                        bundle.putLong(key, (Long) value);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NotificationTitleMessage notificationTitleMessage = getTitleMessage(payloadType, bundle);
            Log.d("@NotificationHelper::: bundle", "" + bundle);
            Log.d("@NotificationHelper::: payloadType", "" + payloadType);
            new SendPushTask(payloadType, tokenList, fcmData, notificationTitleMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (JSONException e) {
            e.printStackTrace();
            callBack.onJsonException();
        }
    }

    static Map<String, Object> result = new HashMap<>();

    /**
     * Returns true if @param obj is a valid JSONObject
     */
    static boolean isJson(Object obj) {
        try {
            if (obj instanceof String) {
                JSONObject jsonObject = new JSONObject(String.valueOf(obj));
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Extracts @param jsonObject into key-value format format
     */
    public static Map<String, Object> extractDataMap(JSONObject jsonObject) throws JSONException {
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            Object ob = jsonObject.get(key);
            if (ob instanceof String) {
                if (isJson(ob)) {
                    JSONObject j = new JSONObject(jsonObject.get(key).toString());
                    result.put(key, j);
                } else {
                    result.put(key, ob);
                }
            } else {
                result.put(key, ob);
            }
        }
        return result;
    }

    /**
     * @return NotificationCompat.Builder for showing *custom* notification view
     */
    public static NotificationCompat.Builder buildCustomNotificationView(Context context, Intent intent, NotificationTitleMessage titleMessage, PushData pushData) {
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            // Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            @SuppressLint("RemoteViewLayout") RemoteViews notificationLayout = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.layout_notification_small);
            notificationLayout.setTextViewText(R.id.text_app_name, pushData.getSiteName());
            notificationLayout.setTextViewText(R.id.text_title, titleMessage.getTitle());
            notificationLayout.setTextViewText(R.id.text_subject, pushData.getSubject());
            notificationLayout.setTextViewText(R.id.text_full_message, titleMessage.getBody());
            notificationLayout.setTextViewText(R.id.text_user_details, pushData.getName());
            notificationLayout.setTextViewText(R.id.text_time_sent, pushData.getTimesent());
            notificationLayout.setTextViewText(R.id.text_email, pushData.getEmail());
            Bitmap bitmap = getBitmapFromURL(pushData.getEmail());
            if (bitmap != null) {
                notificationLayout.setImageViewBitmap(R.id.img, bitmap);
            }
            Log.e(TAG, "buildCustomNotificationView: check here");
            return new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(titleMessage.getTitle())
                    .setContentText(titleMessage.getBody())
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setSound(defaultSoundUri)
                    .setPriority(100)
                    .setContentIntent(pendingIntent);
        }
        return null;
    }

    /**
     * @return NotificationCompat.Builder for showing notification view
     */
    public static NotificationCompat.Builder buildNotificationView(Context context, Intent intent, NotificationTitleMessage titleMessage, PushData pushData) {
        Log.e(TAG, "buildNotificationView: check here");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            // Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            return new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(titleMessage.getTitle())
                    .setContentText(titleMessage.getBody())
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setSound(defaultSoundUri)
                    .setPriority(100)
//                    .addAction(0, "Stop Ringtone", pendingIntent)
                    .setContentIntent(pendingIntent);

        }
        return null;
    }

    /**
     * Shows the notification view
     */
    public static void showNotificationView(Context context, NotificationCompat.Builder notificationBuilder) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "OmniVers App notification";
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // notification id should be unique
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        UpdateCashBroadcast.sendBroadcast(context);
    }

    /**
     * @return an icon resource for the notification view
     */
    private static int getNotificationIcon() {
        return R.drawable.ic_fcm_pm;
    }

    /**
     * @return FCM Push Notification data model
     */
    public static PushData getPushData(Bundle bundle) {
        PushData data = new PushData();

        data.setPayloadType(bundle.getInt("payloadtype")); // Payload Type
        data.setSiteName(bundle.getString("siteName"));
        data.setSubject(bundle.getString("subject"));
        data.setImgURL(bundle.getString("imgURL"));
        data.setSenderID(bundle.getInt("SenderID")); // Sender ID
        data.setName(bundle.getString("name")); // Sender Name
        data.setEmail(bundle.getString("email")); // Email
        data.setFn(bundle.getString("fn")); // First Name
        data.setLn(bundle.getString("ln")); // Last Name
        data.setCo(bundle.getString("co")); // Company
        data.setMessage(bundle.getString("message")); // Message
        data.setPayloads(bundle.getString("payloads")); // Payloads
        data.setTimesent(bundle.getString("timesent")); // Time Sent
        data.setTitle(bundle.getString("title")); // Time Sent
        data.setBody(bundle.getString("body")); // Time Sent
        return data;
    }


    /**
     * Defines the contentTitle and contentText of each notification payloadType
     *
     * @param payloadType The payload type of the push notification
     * @return NotificationTitleMessage object
     */
    public static NotificationTitleMessage getTitleMessage(int payloadType, Bundle bundle) throws JSONException {
        PushData pushData = getPushData(bundle);
        JSONObject payloadData = getPayloadData(bundle);
        NotificationTitleMessage notificationTitleMessage = new NotificationTitleMessage();
        notificationTitleMessage.setTitle("New Notification");
        notificationTitleMessage.setBody("Please click to see notification details");
        switch (payloadType) {
            case PT_INCOMING_VIDEO_CALL:
                notificationTitleMessage = new NotificationTitleMessage(bundle.getString("title") + " From " + bundle.getString("fn") + " " + bundle.getString("ln"), "");
                break;
            case PT_Text_Message_W_Subject:
            case PT_Text_W_Pictures:
            case PT_Text_Message:
                //notificationTitleMessage = new NotificationTitleMessage(String.format("Message from %s", pushData.getName()), String.format("%s\n\n%s", pushData.getMessage(), pushData.getTimesent()));
                //notificationTitleMessage = new NotificationTitleMessage(pushData.getSubject()/*pushData.getMessage()*/, String.format("%s\n%s", pushData.getName(), pushData.getTimesent()));
//                notificationTitleMessage = new NotificationTitleMessage(pushData.getSubject(), pushData.getMessage());
                notificationTitleMessage = new NotificationTitleMessage(pushData.getTitle(), pushData.getBody());
                break;
            case PT_Funds_Sent:
//                notificationTitleMessage = new NotificationTitleMessage("Payment Received!", String.format("Tap to see your Instant Funds that %s has sent you. \nThe funds are available NOW!", pushData.getName()));
                notificationTitleMessage = new NotificationTitleMessage(pushData.getTitle(), pushData.getBody());
                break;
            case PT_Share_Task:
                notificationTitleMessage = new NotificationTitleMessage("You have a new task", String.format("%s has shared a new task with you", pushData.getName()));
                break;
            case PT_Share_My_Info:
                break;
            case PT_Store_Total_Sale_Be_Prepared:
                break;
            case PT_Valet_Req:
                break;
            case PT_Valet_Confirm:
                break;
            case PT_Valet_Accept:
                break;
            case PT_Valet_Decline:
                break;
            case PT_Valet_Park_Location:
                break;
            case PT_Valet_Get_Car:
                break;
            case PT_Consumer_Parcel_Req:
                break;
            case PT_Driver_Parcel_Bid:
                break;
            case PT_Driver_Food_Req:
                break;
            case PT_Driver_Food_Grab:
                break;
            case PT_Share_Location:
                notificationTitleMessage = new NotificationTitleMessage("Share Location", String.format("%s has shared a location with you", pushData.getName()));
                break;
            case PT_Follow_My_Location:
                break;
            case PT_Delivery_Get_Order_By_Item_Count:
                break;
            case PT_Delivery_In_Route_To_Customer:
                break;
            case PT_Delivery_Delivery_Almost_There:
                break;
            case PT_Delivery_Delivery_On_Site:
                break;
            case PT_Delivery_Delivery_Placed_Valid_Spot:
                break;
            case PT_Delivery_Accepted:
                break;
            case PT_Delivery_Failed:
                break;
            case PT_Delivery_Headed_Next_Stop:
                break;
            case PT_Delivery_:
                break;
            case PT_Dial_Phone:
                if (payloadData != null) {
                    notificationTitleMessage = new NotificationTitleMessage("Dial Phone Number", String.format("\nNumber is %s\nThe app being used is: %s\n Tap to Dial", payloadData.getString("phone"), pushData.getSiteName()));
                }
                break;
            case PT_Advertisement_Industry_Offer:
                break;
            case PT_New_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("New Appointment", String.format("by %s", pushData.getName()));
                break;
            case PT_Cancel_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("Appointment Cancelled", String.format("by %s", pushData.getName()));
                break;
            case PT_Accept_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("Appointment Accepted", String.format("by %s", pushData.getName()));
                break;
            case PT_Decline_New_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("Appointment Declined", String.format("by %s", pushData.getName()));
            case PT_Propose_Reschedule_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("Appointment Rescheduled", String.format("%s proposed a new schedule", pushData.getName()));
                break;
            case PT_Reschedule_Appointment:
                notificationTitleMessage = new NotificationTitleMessage("Appointment Reschedule Request", String.format("by %s", pushData.getName()));
                break;
            case PT_Reschedule_Accepted:
                notificationTitleMessage = new NotificationTitleMessage("Reschedule Appointment Accepted", String.format("by %s", pushData.getName()));
                break;
            case PT_Reschedule_Declined:
                notificationTitleMessage = new NotificationTitleMessage("Reschedule Appointment Declined", String.format("by %s", pushData.getName()));
                break;
            case PT_Set_Date_Range:
                break;
            case PT_Log_Vehicle_Door_Opened:
                break;
            case PT_Lock_Out:
                break;
            case PT_Log_Attempt_Open_Door:
                break;
            case PT_Invoice_Sent:
                notificationTitleMessage = new NotificationTitleMessage("New Invoice", String.format("by %s", pushData.getName()));
                break;
            case PT_Invoice_Paid:
                break;
            case PT_Emergency_Unlock:
                break;
            case PT_Order_Status:
                break;
            case PT_Just_Ordered:
                break;
            case PT_Rest_Accepted:
                break;
            case PT_Rest_Refused:
                break;
            case PT_Rest_Preparing:
                break;
            case PT_Rest_Complete:
                break;
            case PT_Rest_Table_Ready:
                break;
            case PT_Rest_Service_Requested:
                break;
        }
        return notificationTitleMessage;
    }

    public static JSONObject getPayloadData(Bundle bundle) {
        String payloadsString = bundle.getString("payloads");
        try {
            if (payloadsString != null && !payloadsString.isEmpty()) {
                return new JSONObject(payloadsString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
