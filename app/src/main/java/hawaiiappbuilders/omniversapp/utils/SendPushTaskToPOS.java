package hawaiiappbuilders.omniversapp.utils;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SendPushTaskToPOS extends AsyncTask<String, Void, String> {

    ArrayList<String> deviceList;
    int pushType;
    String title;
    String message;
    JSONObject additionalData;

    public SendPushTaskToPOS(ArrayList<String> deviceIdList, int pushType, String title, String message, JSONObject additionalData) {

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

            // Set additional data
            if (additionalData != null) {
                jData.put("payloads", additionalData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Custom Data Message
        JSONObject jNotification = new JSONObject();
        // Set Title and Message
        try {
            jNotification.put("title", title);
            jNotification.put("body", message);

            jNotification.put("click_action", "orderstatus_ACTIVITY");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (String device : deviceList) {
            try {
                try {
                    // Prepare JSON containing the FCM message content. What to send and where to send.
                    JSONObject jGcmData = new JSONObject();

                    // Where to send GCM message.
                    jGcmData.put("to", device);

                    // What to send in GCM message.
                    jGcmData.put("data", jData);
                    jGcmData.put("notification", jNotification); // Don't remove this or else notification won't work

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
