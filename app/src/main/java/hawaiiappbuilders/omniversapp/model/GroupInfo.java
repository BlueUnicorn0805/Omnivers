package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupInfo implements Parcelable {
    int id = 0;
    String grpname = "";
    int pri = 0;
    int sortby = 0;
    long createdAt = 0;
    String more = "";

    boolean isSelected = false;

    public GroupInfo() {}

    public GroupInfo(int id, String grpname, int pri, int sortby) {
        this.id = id;
        this.grpname = grpname;
        this.pri = pri;
        this.sortby = sortby;
    }

    protected GroupInfo(Parcel in) {
        id = in.readInt();
        grpname = in.readString();
        pri = in.readInt();
        sortby = in.readInt();
        createdAt = in.readLong();
        more = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGrpname() { return grpname; }
    public void setGrpname(String grpname) { this.grpname = grpname; }

    public int getPri() { return pri; }
    public void setPri(int pri) { this.pri = pri; }

    public int getSortby() { return sortby; }
    public void setSortby(int sortby) { this.sortby = sortby; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getMore() { return more; }
    public void setMore(String more) { this.more = more; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    @Override
    public String toString() {
        return grpname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(grpname);
        dest.writeInt(pri);
        dest.writeInt(sortby);
        dest.writeLong(createdAt);
        dest.writeString(more);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
