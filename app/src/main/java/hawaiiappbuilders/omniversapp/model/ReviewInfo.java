package hawaiiappbuilders.omniversapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReviewInfo implements Serializable {

    @SerializedName("ReviewID")
    private String reviewID;

    @SerializedName("Rating")
    private float ratings;

    @SerializedName("Name")
    private String userName;

    @SerializedName("Image")
    private String userAvatar;

    @SerializedName("Description")
    private String description;

    @SerializedName("DateTime")
    private String dateTime;

    public ReviewInfo(String id, String name, float rating, String desc, String avatar, String time) {
        reviewID = id;
        userName = name;
        ratings = rating;
        description = desc;
        userAvatar = avatar;
        dateTime = time;
    }

    public String getReviewID() {
        return reviewID;
    }
    public void setReviewID(String delid) {
        this.reviewID = delid;
    }

    public float getRatings() {
        return ratings;
    }
    public void setRatings(float rating) {
        this.ratings = rating;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
