package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StoreItem implements Parcelable {
    private String ID;
    private String description;
    private boolean removed;

    public StoreItem() {
    }

    protected StoreItem(Parcel in) {
        ID = in.readString();
        description = in.readString();
        removed = in.readInt() > 0;
    }

    public static final Creator<StoreItem> CREATOR = new Creator<StoreItem>() {
        @Override
        public StoreItem createFromParcel(Parcel in) {
            return new StoreItem(in);
        }

        @Override
        public StoreItem[] newArray(int size) {
            return new StoreItem[size];
        }
    };

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isRemoved() { return removed; }
    public void setRemoved(boolean removed) { this.removed = removed; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(description);
        parcel.writeInt(removed ? 1 : 0);
    }
}
