package hawaiiappbuilders.omniversapp.utils;

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
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.model.FCMTokenData;

public class SendPushTaskToAVA extends AsyncTask<String, Void, String> {

    ArrayList<FCMTokenData> deviceList;
    int pushType;
    String title;
    String message;
    JSONObject additionalData;

    public SendPushTaskToAVA(ArrayList<FCMTokenData> deviceIdList, int pushType, String title, String message, JSONObject additionalData) {

        this.deviceList = deviceIdList;

        this.pushType = pushType;
        this.title = title;
        this.message = message;
        this.additionalData = additionalData;
    }

    @Override
    protected void onPreExecute() {
        //showProgressDialog();
    }

    @Override
    protected void onPostExecute(String result) {
        //hideProgressDialog();
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

        // Custom Data Message
        JSONObject jData = new JSONObject();
        // Set Title and Message

        String timeStamp = String.valueOf(System.currentTimeMillis());
        try {
            jData.put("payloadtype", pushType);
            jData.put("title", title/*"You have been requested as a favorite driver."*/);
            jData.put("message", message);
            jData.put("timestamp", timeStamp);
            jData.put("name", additionalData.get("name"));
            jData.put("SenderID", additionalData.get("SenderID"));
            jData.put("email", additionalData.get("email"));

            // Set additional data
//            if (additionalData != null) {
//                jData.put("payloads", additionalData);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom Data Message
        JSONObject jNotification = new JSONObject();
        // Set Title and Message
        try {
            jNotification.put("title", title);
            jNotification.put("body", message);
            jNotification.put("sound", "default");

            /*if (pushType == 1) {
                jNotification.put("click_action", "com.ver1.ava.message_ACTIVITY");
            } else if (pushType == 2) {
                jNotification.put("click_action", "com.ver1.ava.orderstatus_ACTIVITY");
            } else if (pushType == 3) {
                jNotification.put("click_action", "com.ver1.ava.money_ACTIVITY");
            }*/
            /*if (pushType == 1) {
                jNotification.put("click_action", "message_ACTIVITY");
            } else if (pushType == 2) {
                jNotification.put("click_action", "orderstatus_ACTIVITY");
            } else if (pushType == 3) {
                jNotification.put("click_action", "money_ACTIVITY");
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject apns = new JSONObject();
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

        for (FCMTokenData device : deviceList) {
            try {
                try {
                    // Prepare JSON containing the FCM message content. What to send and where to send.
                    JSONObject jGcmData = new JSONObject();

                    // Where to send GCM message.
                    jGcmData.put("to", device.getToken());

                    // What to send in GCM message.
                    jGcmData.put("data", jData);

                    if (device.getType() == FCMTokenData.OS_IOS) {
                        jGcmData.put("notification", jNotification);
                        jGcmData.put("apns", apns);
                    } else if (device.getType() == FCMTokenData.OS_ANDROID) {
                        jGcmData.put("android", android);
                    }

                    // Create connection to send GCM Message request.
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key=" + "");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send FCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jGcmData.toString().getBytes());

                    // Read FCM response.
                    InputStream inputStream = conn.getInputStream();
                    String resp = IOUtils.toString(inputStream, "UTF-8");
                    Log.e("pushmsg", resp); //{"multicast_id":4004573332092354359,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1659136453468389%28c7fb7ff9fd7ecd"}]}
                    /*Log.e("pushmsg", "Check your device/emulator for notification or logcat for " +
                            "confirmation of the receipt of the GCM message.");*/
                } catch (IOException e) {
                    Log.e("pushmsg","Unable to send GCM message.");
                    Log.e("pushmsg","Please ensure that API_KEY has been replaced by the server " +
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
