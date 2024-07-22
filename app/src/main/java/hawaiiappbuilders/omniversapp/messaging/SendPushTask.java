package hawaiiappbuilders.omniversapp.messaging;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.messaging.model.NotificationTitleMessage;
import hawaiiappbuilders.omniversapp.model.FCMTokenData;
import hawaiiappbuilders.omniversapp.utils.K;

public class SendPushTask extends AsyncTask<String, Void, String> {

   int payloadtype;
   ArrayList<FCMTokenData> deviceList;
   String title;
   String message;
   JSONObject customData;

   NotificationTitleMessage notificationTitleMessage;

   public SendPushTask(int payloadtype, ArrayList<FCMTokenData> deviceIdList, JSONObject customData) {
      this.payloadtype = payloadtype;
      this.deviceList = deviceIdList;
      this.customData = customData;
   }

   public SendPushTask(int payloadtype, ArrayList<FCMTokenData> deviceIdList, JSONObject customData, NotificationTitleMessage notificationTitleMessage) {
      this.payloadtype = payloadtype;
      this.deviceList = deviceIdList;
      this.customData = customData;
      this.notificationTitleMessage = notificationTitleMessage;
   }

   @Override
   protected void onPreExecute() {

   }

   @Override
   protected void onPostExecute(String result) {

   }

   @Override
   protected void onProgressUpdate(Void... values) {
      super.onProgressUpdate(values);
   }

   @Override
   protected void onCancelled(String result) {
      super.onCancelled(result);
   }

   @Override
   protected String doInBackground(String... params) {
      String response = "";

      JSONObject notification = new JSONObject();
      // Set Title and Message for app in background
      /*"notification": {
         "title": "",
         "body": "",
         "sound": ""
      }*/
      try {
         notification.put("title", notificationTitleMessage.getTitle());
         notification.put("body", notificationTitleMessage.getBody());
         notification.put("sound", "default");
         notification.put("priority", 100);
      } catch (JSONException e) {
         e.printStackTrace();
      }

      for (FCMTokenData device : deviceList) {
         try {
            try {
               // Prepare JSON containing the FCM message content. What to send and where to send.
               JSONObject fcmData = new JSONObject();

               // Where to send FCM message.
               // "to": "" // device token
               fcmData.put("to", device.getToken());
               /*JSONObject apns = new JSONObject();
               try {
                  JSONObject apnsHeader = new JSONObject();
                  apnsHeader.put("apns-priority", "10");
                  apns.put("headers", apnsHeader);
                  JSONObject apnsPayloads = new JSONObject();
                  JSONObject apnsPayloadsAps = new JSONObject();
                  apnsPayloadsAps.put("sound", "default");
                  apnsPayloads.put("aps", apnsPayloadsAps);
                  apns.put("payload", apnsPayloads);
               } catch (JSONException e) {
                  e.printStackTrace();
               }

               JSONObject android = new JSONObject();
               try {
                  android.put("priority", "high");
                  JSONObject androidNotification = new JSONObject();
                  androidNotification.put("sound", "default");
                  android.put("notification", androidNotification);
               } catch (JSONException e) {
                  e.printStackTrace();
               }

               if (device.getType() == FCMTokenData.OS_IOS) {
                  fcmData.put("notification", notification);
                  fcmData.put("apns", apns);
               } else if (device.getType() == FCMTokenData.OS_ANDROID) {
                  fcmData.put("android", android);
               }*/

               // What to send in FCM message.
//               customData.put("fromFcmToken", device.getToken());

               fcmData.put("data", customData);
               fcmData.put("notification", notification);

               Log.d("@SendPushTask::: fcmData", ""+fcmData);

               // Create connection to send GCM Message request.
               URL url = new URL("https://fcm.googleapis.com/fcm/send");
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setRequestProperty("Authorization", "key=" + K.gKy(BuildConfig.PM));
               conn.setRequestProperty("Content-Type", "application/json");
               conn.setRequestMethod("POST");
               conn.setDoOutput(true);

               // Send FCM message content.
               OutputStream outputStream = conn.getOutputStream();
               outputStream.write(fcmData.toString().getBytes());

               // Read FCM response.
               InputStream inputStream = conn.getInputStream();
               String resp = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
               Log.d("@SendPushTask::: resp", resp);
               System.out.println(resp);
               System.out.println("Check your device/emulator for notification or logcat for " +
                       "confirmation of the receipt of the GCM message.");
            } catch (IOException e) {
               System.out.println("Unable to send GCM message.");
               System.out.println("Please ensure that API_KEY has been replaced by the server " +
                       "API key, and that the device's registration token is correct (if specified).");
               e.printStackTrace();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return response;
   }
}
