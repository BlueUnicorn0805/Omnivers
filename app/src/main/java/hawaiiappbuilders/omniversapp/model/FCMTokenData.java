package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FCMTokenData implements Parcelable {
    public static final int OS_UNKNOWN = 0;
    public static final int OS_ANDROID = 85;
    public static final int OS_IOS = 86;

    String token;
    int type;

    public FCMTokenData(String token, int type) {
        this.token = token;
        this.type = type;
    }

    protected FCMTokenData(Parcel in) {
        token = in.readString();
        type = in.readInt();
    }

    public static final Creator<FCMTokenData> CREATOR = new Creator<FCMTokenData>() {
        @Override
        public FCMTokenData createFromParcel(Parcel in) {
            return new FCMTokenData(in);
        }

        @Override
        public FCMTokenData[] newArray(int size) {
            return new FCMTokenData[size];
        }
    };

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeInt(type);
    }
}
