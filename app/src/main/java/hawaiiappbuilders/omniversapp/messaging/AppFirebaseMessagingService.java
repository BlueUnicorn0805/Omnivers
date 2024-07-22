package hawaiiappbuilders.omniversapp.messaging;

import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.buildCustomNotificationView;
import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.buildNotificationView;
import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.getPayloadData;
import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.getPushData;
import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.getTitleMessage;
import static hawaiiappbuilders.omniversapp.messaging.NotificationHelper.showNotificationView;
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
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_INCOMING_Response_VIDEO_CALL;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_INCOMING_VIDEO_CALL;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Invoice_Paid;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Invoice_Sent;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Lock_Out;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Log_Attempt_Open_Door;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Log_Vehicle_Door_Opened;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_New_Appointment;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Order_Status;
import static hawaiiappbuilders.omniversapp.messaging.PayloadType.PT_Phone_Verify_Receive_OTP;
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
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hawaiiappbuilders.omniversapp.ActivityTransaction;
import hawaiiappbuilders.omniversapp.ConnectionActivity;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.meeting.utilities.Constants;
import hawaiiappbuilders.omniversapp.messaging.model.NotificationTitleMessage;
import hawaiiappbuilders.omniversapp.messaging.model.PushData;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.server.ApiUtil;
import hawaiiappbuilders.omniversapp.services.GpsTracker;
import hawaiiappbuilders.omniversapp.utils.BaseFunctions;
import hawaiiappbuilders.omniversapp.utils.DataUtil;
import hawaiiappbuilders.omniversapp.utils.DateUtil;
import hawaiiappbuilders.omniversapp.videocall.IncomingVideoCallActivity;
import hawaiiappbuilders.omniversapp.videocall.IncomingVideoCallActivityAgora;


public class AppFirebaseMessagingService extends FirebaseMessagingService {

    // Intent Actions
    public static final String ACTION_TEXT_MESSAGE = "ConnectionActivity.TEXT_MSG";
    public static final String ACTION_VIDEO_CALL = "ConnectionActivity.VIDEO_CALL";
    public static final String ACTION_NEW_TASK = "ConnectionActivity.NEW_TASK";
    public static final String ACTION_VIEW_LOCATION = "ViewLocationActivity.SHARE_LOCATION";

    public static final String ACTION_DIAL_PHONE = "ActivityHomeEvents.DIAL_PHONE";

    public static final String ACTION_INVOICE_SENT = "ActivityInvoiceDetails.NEW_INVOICE";

    public static final String ACTION_NEW_APPT = "ConnectionActivity.NEW_APPOINTMENT";
    public static final String ACTION_UPDATE_APPT = "ConnectionActivity.APPOINTMENT_UPDATE";

    // LocalBroadcastManager Intent Actions
    public static final String ACTION_TEXT_MESSAGE_CHAT_ACTIVE = "hawaiiappbuilders.Omni.newmessage";
    public static final String ACTION_VIDEO_RESPONSE_ACTIVE = "hawaiiappbuilders.Omni.reponsevcall";
    public static final String ACTION_RECEIVE_OTP_PHONE_VERIFY = "hawaiiappbuilders.Omni.receiveotp";
    public static final String UPDATE_STATUS_ID = "UPDATE_STATUS_ID";
    public static final String UPDATE_APPOINTMENT = "UPDATE_APPOINTMENT";

    public static final int DATA_EXTRAS_FOREGROUND_DEFAULT = 0;
    public static final int DATA_EXTRAS_BACKGROUND = 1;
    public static final int DATA_EXTRAS_BACKGROUND_VIDEO_CALL = 2;
    private static boolean isAppInBackground = true;

    public static void setAppInBackground(boolean isBackground) {
        isAppInBackground = isBackground;
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        new AppSettings(this).setDeviceToken(s);
    }

    /**
     * Handle notification here when app is active or in foreground
     */
//    @SuppressLint("LogNotTimber")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("@@@@" + this.getClass().getSimpleName(), "onMessageReceived: " + "Notification -> " + remoteMessage.getNotification() + "||||||||" + "data -> " + remoteMessage.getData());

        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
        Bundle b = new Bundle();
        b.putString(Constants.CAll_ID, remoteMessage.getData().get(Constants.CAll_ID));
        b.putString(Constants.FIRST_NAME, remoteMessage.getData().get(Constants.FIRST_NAME));
        b.putString(Constants.LAST_NAME, remoteMessage.getData().get(Constants.LAST_NAME));
        b.putString(Constants.REMOTE_MSG_TYPE, type);
        try {
            showNotification(remoteMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* if (type != null) {
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                b.putString(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
                b.putString(Constants.KEY_FIRST_NAME, remoteMessage.getData().get(Constants.KEY_FIRST_NAME));
                b.putString(Constants.KEY_LAST_NAME, remoteMessage.getData().get(Constants.KEY_LAST_NAME));
                b.putString(Constants.KEY_MLID, remoteMessage.getData().get(Constants.KEY_MLID));
                b.putString(Constants.REMOTE_MSG_INVITER_TOKEN, remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN));
                b.putString(Constants.REMOTE_MSG_MEETING_ROOM, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_ROOM));
            } else {
                b.putString(Constants.REMOTE_MSG_INVITATION_RESPONSE, remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE));
                b.putString(Constants.KEY_FIRST_NAME, remoteMessage.getData().get(Constants.KEY_FIRST_NAME));
                b.putString(Constants.KEY_LAST_NAME, remoteMessage.getData().get(Constants.KEY_LAST_NAME));
                b.putString(Constants.KEY_MLID, remoteMessage.getData().get(Constants.KEY_MLID));
            }
            KTXApplication application = (KTXApplication) this.getApplication();
//            handleVideoCallExtras(application,this, b);
        } else {// Handle Payload Types here
            try {
                showNotification(remoteMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    /*private static void handleVideoCallExtras(KTXApplication application, Context context, Bundle extras) {
        if (extras != null) {
            String type = extras.getString(Constants.REMOTE_MSG_TYPE);
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                application.playVideoCallRingtone();
                Intent intent = new Intent(application.getApplicationContext(), IncomingInvitationActivity.class);
                intent.putExtras(extras);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtras(extras);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }
    }*/

    private void showNotification(RemoteMessage remoteMessage) throws JSONException {
        Map<String, String> dataMap = remoteMessage.getData();
        /*if (BuildConfig.DEBUG) {
            result.clear();
            Map<String, Object> result = extractDataMap(new JSONObject(dataMap));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonStr = gson.toJson(result);
            Log.i("AppFirebaseMessagingService", jsonStr);
        }*/
        Log.d(this.getClass().getSimpleName(), "onMessageReceived: " + remoteMessage.getData());
        Bundle bundle = new Bundle();
        try {
            JSONObject jsonObject = new JSONObject(dataMap);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    if (key.contentEquals("payloadtype") || key.contentEquals("SenderID") || key.contentEquals("psenderid")) {
                        bundle.putInt(key, Integer.parseInt(String.valueOf(value)));
                    } else {
                        bundle.putString(key, (String) value);
                    }
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
        Log.d(this.getClass().getSimpleName(), "@payloadtype: " + dataMap.get("payloadtype"));
        if (dataMap.get("payloadtype") != null) {
            int payloadType = bundle.getInt("payloadtype");
            int SenderID = bundle.getInt("SenderID");
            receivePushData(this, SenderID, bundle, payloadType, DATA_EXTRAS_FOREGROUND_DEFAULT, null);
        }
    }

    /**
     * This is where we handle push data for different payload types
     *
     * @param context        The context of the push data
     * @param bundle         The extras that holds the push notification data
     * @param dataExtrasType 1 if push data is received from the Main activity extras or
     *                       from when the app is killed
     */
    public static void receivePushData(Context context, int SenderID, Bundle bundle, int payloadType, int dataExtrasType, Bundle videoCallBundle) throws JSONException {
        /*if (dataExtrasType == DATA_EXTRAS_BACKGROUND_VIDEO_CALL) {
            KTXApplication application = (KTXApplication) ((BaseActivity)context).getApplication();
            handleVideoCallExtras(application, context, videoCallBundle);
        } else {



        }*/
        PushData pushData = getPushData(bundle);
        int LL = 1;
        Log.d("tesing", "title/subject-> " + bundle.get("subject") + ", body/message->" + bundle.get("message"));

        NotificationTitleMessage titleMessage = getTitleMessage(payloadType, bundle);
        boolean showPushNotification = true;
        Intent intent = new Intent();
        try {
            JSONObject payloadData = getPayloadData(bundle);
            titleMessage = getTitleMessage(payloadType, bundle);
            switch (payloadType) {
                case PT_INCOMING_Response_VIDEO_CALL:
                    LL = 2;
                    if(!(payloadData == null)) {
                        Intent localMsg = new Intent(ACTION_VIDEO_RESPONSE_ACTIVE);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);
                    }
                    break;
                case PT_INCOMING_VIDEO_CALL:

                    LL = 2;
                    intent = new Intent(context, IncomingVideoCallActivityAgora.class);
                    intent.putExtra(Constants.FIRST_NAME, bundle.getString(Constants.FIRST_NAME));
                    intent.putExtra(Constants.LAST_NAME, bundle.getString(Constants.LAST_NAME));
                    LL = 3;
                    intent.putExtra("agoraToken",bundle.getString("agoraToken"));
//                    intent.putExtra("fromFcmToken",payloadData.getString("fromFcmToken"));
//                    intent.putExtra("agoraId", payloadData.getString("agoraId"));
                    LL = 4;
                    intent.setAction("STOP_RINGTONE_ACTION");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if (bundle.getString("callid") != null) {
                        Log.d("@COming!!!!!!!!!!!!!!!!!!!!","");
                        intent.putExtra(Constants.CAll_ID, bundle.getString("callid"));
                    }
                    if (!isAppInBackground) {

                        LL = 5;
                        context.startActivity(intent);
                    } else {
                        LL = 6;
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    }
                    LL = 7;
                    if(!(payloadData ==null)){
                        String statusID = bundle.getString("callid");
                        Intent localMsg = new Intent(ACTION_VIDEO_CALL);
                        localMsg.putExtra("callId", statusID);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);
                    }
//                    // Navigate to ConnectionActivity
//                    intent = new Intent(ACTION_VIDEO_CALL);
//                    intent.putExtra("mlid", SenderID);
//                    intent.putExtra("payloadtype", payloadType);
                    Log.d("@Intent", ""+ intent.getStringExtra("callid"));
                    Log.d("@Bundle", ""+ bundle.getString("callid"));
                    Log.d("@PT_Video_Message","HAHAHAHAH!!!!!!!!!!!!!!");
                    Log.d("@APPFirebase::: LL", ""+LL);
                    break;
                case PT_Text_Message_W_Subject:
                case PT_Text_W_Pictures:
                case PT_Text_Message:
                    // Add contact if does not exists in local database
                    MessageDataManager dm = new MessageDataManager(context);
                    //  ((BaseActivity)context).showToastMessage("SENDER ID HERE!!!" + SenderID);
                    Log.d("@PT_Text_Message","");
                    Log.d("@APPFirebase::: dm", ""+dm);

                    if (!dm.findContact(SenderID)) {
                        ContactInfo newContactInfo = new ContactInfo();
                        newContactInfo.setMlid(SenderID);
                        newContactInfo.setName(pushData.getName());
                        newContactInfo.setFname(pushData.getFn());
                        newContactInfo.setLname(pushData.getLn());
                        newContactInfo.setCo(pushData.getCo());
                        newContactInfo.setEmail(pushData.getEmail());
                        dm.addContact(newContactInfo);
                    }
                    // Save message to local database

                    AppSettings appSettings = new AppSettings(context);
                    Message newMsg = new Message();
                    newMsg.setFromID(SenderID);
                    newMsg.setToID(appSettings.getUserId());
                    newMsg.setMsg(pushData.getSubject()/*pushData.getMessage()*/);
                    newMsg.setCreateDate(DateUtil.toStringFormat_20(new Date()));
                    newMsg.setName(pushData.getName());
                    dm.addMessage(newMsg);
                    dm.close();
                    boolean activeChatIsOpened = ConnectionActivity.isMsgForCurrentContact(SenderID);
                    if (activeChatIsOpened) {
                        showPushNotification = false;
                        // send local message
                        Intent localMsg = new Intent(ACTION_TEXT_MESSAGE_CHAT_ACTIVE);
                        localMsg.putExtra("from", pushData.getName());
                        localMsg.putExtra("message", pushData.getSubject()/*pushData.getMessage()*/);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
                        r.play();
                    }
                    // Navigate to ConnectionActivity
                    intent = new Intent(ACTION_TEXT_MESSAGE);
                    intent.putExtra("mlid", SenderID);
                    intent.putExtra("payloadtype", payloadType);
                    break;
                case PT_Funds_Sent:
                    intent = new Intent(context, ActivityTransaction.class);
                    break;
                case PT_Phone_Verify_Receive_OTP:
                    if (payloadData != null) {
                        showPushNotification = false;
                        Intent localMsg = new Intent(ACTION_RECEIVE_OTP_PHONE_VERIFY);
                        localMsg.putExtra("OTP", payloadData.getString("OTP"));
                        localMsg.putExtra("msg", pushData.getMessage() + " " + payloadData.getString("OTP"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);
                    }
                case PT_Share_Task:
                    intent = new Intent(ACTION_NEW_TASK);
                    break;
                case PT_Share_My_Info:
                    break;
                case PT_Store_Total_Sale_Be_Prepared:
                    showPushNotification = false;
                    break;
                case PT_Valet_Req:
                    String scanresult = "https://z99.io?appID=19&indust=125&x=P&m=99&qrid=3&d=2023-02-14&pNote=Your%20Custom%20QRCode<BR>Message%20Here";
                    intent = new Intent("scanvalet");
                    intent.putExtra("scanresult", scanresult);
                    break;
                case PT_Valet_Confirm:
                    break;
                case PT_Valet_Decline:
                    break;
                case PT_Valet_Accept:
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
                    if (payloadData != null) {
                        Double latData = payloadData.getDouble("lat");
                        Double lonData = payloadData.getDouble("lon");
                        intent = new Intent(ACTION_VIEW_LOCATION);
                        intent.putExtra("SenderName", pushData.getName());
                        intent.putExtra("lat", latData);
                        intent.putExtra("lon", lonData);
                        intent.putExtra("zoom", bundle.getInt("zoom"));
                    }
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
                        String phoneNumber = payloadData.getString("phone");
                        intent = new Intent(ACTION_DIAL_PHONE);
                        intent.putExtra("phone", phoneNumber);
                    }
                    break;
                case PT_Advertisement_Industry_Offer:
                    break;
                case PT_New_Appointment:
                    // TODO:  Open Community > Meet
                    if (payloadData != null) {
                        long apptId = payloadData.getLong("NewApptID");
                        intent = new Intent(ACTION_NEW_APPT);
                        intent.putExtra("NewApptID", apptId);
                    }
                    break;
                case PT_Cancel_Appointment:
                case PT_Accept_Appointment:
                case PT_Decline_New_Appointment:
                case PT_Propose_Reschedule_Appointment:
                case PT_Reschedule_Appointment:
                case PT_Reschedule_Accepted:
                case PT_Reschedule_Declined:
                    // TODO:  Open Community > Meet
                    if (payloadData != null) {
                        int apptStatusID = payloadData.getInt("ApptStatusID");
                        long ApptID = payloadData.getLong("ApptID");
                        intent = new Intent(ACTION_UPDATE_APPT);
                        intent.putExtra("ApptStatusID", apptStatusID);
                        intent.putExtra("ApptID", ApptID);
                        /*Intent localMsg = new Intent(UPDATE_APPOINTMENT);
                        intent.putExtra("ApptStatusID", apptStatusID);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);
                        showPushNotification = false;*/
                    }
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
                    if (payloadData != null) {
                        long invoiceOrderId = payloadData.getLong("orderId");
                        intent = new Intent(ACTION_INVOICE_SENT);
                        intent.putExtra("orderId", invoiceOrderId);
                    }
                    break;
                case PT_Invoice_Paid:
                    break;
                case PT_Emergency_Unlock:
                    break;
                case PT_Order_Status:
                    if (payloadData != null) {
                        int statusID = payloadData.getInt("statusID");
                        Intent localMsg = new Intent(UPDATE_STATUS_ID);
                        localMsg.putExtra("statusID", statusID);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(localMsg);
                        showPushNotification = false;
                    }
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
                default:
                    Log.i("UnknownPM", bundle.toString());
                    Log.i("UnknownPM", "Payload ignored.");
                    /*if (fromMainActivityExtras) {
                        sendPayloadToServer(context, bundle.toString());
                    }*/
                    // todo: do not call
                    return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DataUtil dataUtil = new DataUtil(context, context.getClass().getSimpleName());
            dataUtil.zzzLogIt(e, "PM", LL);
        }
        Log.d("TAG", "receivePushData: " + dataExtrasType);
        switch (dataExtrasType) {
            case DATA_EXTRAS_FOREGROUND_DEFAULT:
                if (showPushNotification) {
                    NotificationCompat.Builder notificationBuilder = buildNotificationView(context, intent, titleMessage, pushData);

                    switch (payloadType) {
                        case PT_Text_Message_W_Subject:
                            notificationBuilder = buildCustomNotificationView(context, intent, titleMessage, pushData);
                            break;
                        default:
                            break;
                    }

                    if (notificationBuilder != null) {
                        if (payloadType == PT_INCOMING_VIDEO_CALL && !isAppInBackground) {
                        } else {
                            showNotificationView(context, notificationBuilder);
                        }
                    }
                }
                break;
            case DATA_EXTRAS_BACKGROUND:
                // Push already shown via notification tray
                // launch intent action directly
                // todo: add intent flag to prevent app from launching the main activity again
                context.startActivity(intent);
                break;
        }
    }

    private static void sendPayloadToServer(Context context, String payloads) {
        double userLat = 0;
        double userLon = 0;
        AppSettings appSettings = new AppSettings(context);
        if (appSettings.getLocationPermission() == 1) {
            GpsTracker gpsTracker = new GpsTracker(context);
            if (gpsTracker.canGetLocation()) {
                userLat = gpsTracker.getLatitude();
                userLon = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
        HashMap<String, String> params = new HashMap<>();
        String baseUrl = BaseFunctions.getBaseUrl(context,
                "PMsIgnore",
                BaseFunctions.MAIN_FOLDER,
                String.valueOf(userLat),
                String.valueOf(userLon),
                ((BaseActivity) context).getAndroidId(context));
        String extraParams = "&msg=" + payloads;
        baseUrl += extraParams;
        Log.e("Request", baseUrl);
        new ApiUtil(context).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
            @Override
            public void onSuccess(String response) {
                Log.e("PMsIgnored", response);
            }

            @Override
            public void onResponseError(String msg) {

            }

            @Override
            public void onServerError() {

            }
        });
    }
}
