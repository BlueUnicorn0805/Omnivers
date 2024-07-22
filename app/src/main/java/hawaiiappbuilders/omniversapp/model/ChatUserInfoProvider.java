package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;

public class ChatUserInfoProvider {
    private static ChatUserInfoProvider INSTANCE;
    public static ChatUserInfoProvider getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ChatUserInfoProvider();
        return INSTANCE;
    }

    public class ChatUserInfo {
        public String Name, ID;
        public ChatUserInfo(String name, String id) {
            this.Name = name;
            this.ID = id;
        }

        @Override
        public String toString() {
            return Name;
        }
    }

    private ArrayList<ChatUserInfo> userList = new ArrayList<>();
    public ArrayList<ChatUserInfo> getUserList() {
        return userList;
    }

    public ChatUserInfoProvider() {

    }
}
