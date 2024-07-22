package hawaiiappbuilders.omniversapp.meeting.utilities;

import java.util.HashMap;

import hawaiiappbuilders.omniversapp.BuildConfig;
import hawaiiappbuilders.omniversapp.utils.K;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_USER_ID = "user_id";

    public static final String KEY_USER_Agora_Token = "agora_token";
    public static final String KEY_USER_Agora_Channel = "agora_channel";
    public static final String KEY_USER_Agora_Id = "agora_id";
    public static final String KEY_USER_Camera_Statue = "user_camera_statue";

    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_MLID = "mlid";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";
    public static final String CAll_ID = "pcallid";
    public static final String I_STARTED_CALL = "iStartedTheCall";
    public static final String NAME = "name";
    public static final String FIRST_NAME = "pfirst_name";
    public static final String LAST_NAME = "plast_name";
    public static final String COMING_FROM = "coming_from";
    public static final String INCOMING_SCREEN = "incoming_screen";
    public static final String OUTGOING_SCREEN = "outgoing_screen";
    public static final String CALLING_IP = "calling_ip";
    public static final int CALL_ACCEPTED = 1400;
    public static final int CALL_DECLINED = 1375;
    public static final int CALL_WAITING = 1355;
    public static final int CALL_CANCELED_HUNG_UP = 1380;
    public static final int CALL_DELIVERED = 1360;
    public static final int NEW_CALL = 1350;
    public static final int CALL_ERROR = 1361;
    public static final int NEED_TO_SETUP = 1365;
    public static final int CALL_BLOCKED = 1370;
    public static final int CALL_ON_HOLD = 1499;
    public static final int CALL_MUTE = 1450;
    public static final int CALL_NO_ANSWER = 1385;

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=" + K.gKy(BuildConfig.PM)
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");

        return headers;
    }
}
