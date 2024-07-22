package hawaiiappbuilders.omniversapp.meeting.listener;

import hawaiiappbuilders.omniversapp.meeting.models.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
