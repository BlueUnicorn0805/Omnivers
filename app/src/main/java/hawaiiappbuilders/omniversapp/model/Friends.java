package hawaiiappbuilders.omniversapp.model;

import java.util.ArrayList;

public class Friends {

    /*FriendID": "10",
            "Nick": "nicky",
            "Email": "nick@aaa.com",
            "CP": "() -"*/
    private String FriendID,Nick,Email,CP;

    public Friends() {
    }

    public String getFriendID() {
        return FriendID;
    }

    public void setFriendID(String friendID) {
        FriendID = friendID;
    }

    public String getNick() {
        return Nick;
    }

    public void setNick(String nick) {
        Nick = nick;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCP() {
        return CP;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }

    public static final ArrayList<Friends> friendList = new ArrayList<>();

    public static void setFriendList(ArrayList<Friends> _friendList) {
        friendList.clear();
        friendList.addAll(_friendList);
    }

    public static ArrayList<Friends> getFriendList() {
        return friendList;
    }
}
