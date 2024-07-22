package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class IndustryInfo implements Parcelable {
    String IndustryID;
    String TypeDesc;

    boolean isHead;
    ArrayList<IndustryInfo> childIndustryInfo;

    public IndustryInfo() {}

    public IndustryInfo(String id, String name, boolean head) {
        IndustryID = id;
        TypeDesc = name;
        isHead = head;
    }

    protected IndustryInfo(Parcel in) {
        IndustryID = in.readString();
        TypeDesc = in.readString();
        isHead = in.readInt() > 0;

        childIndustryInfo = in.createTypedArrayList(IndustryInfo.CREATOR);
    }

    public static final Creator<IndustryInfo> CREATOR = new Creator<IndustryInfo>() {
        @Override
        public IndustryInfo createFromParcel(Parcel in) {
            return new IndustryInfo(in);
        }

        @Override
        public IndustryInfo[] newArray(int size) {
            return new IndustryInfo[size];
        }
    };

    // Industry
    public String getIndustryID() { return IndustryID; }
    public void setIndustryID(String industryID) { IndustryID = industryID; }

    // TypeDesck
    public String getTypeDesc() { return TypeDesc; }
    public void setTypeDesc(String typeDesc) { TypeDesc = typeDesc; }

    public boolean isHead() {
        return isHead;
    }

    public ArrayList<IndustryInfo> getChildIndustryInfo() { return childIndustryInfo; }
    public void setChildIndustryInfo(ArrayList<IndustryInfo> childIndustryInfo) { this.childIndustryInfo = childIndustryInfo; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IndustryID);
        dest.writeString(TypeDesc);
        dest.writeInt(isHead ? 1 : 0);

        dest.writeTypedList(childIndustryInfo);
    }

    @Override
    public String toString() {
        return TypeDesc;
    }
}
