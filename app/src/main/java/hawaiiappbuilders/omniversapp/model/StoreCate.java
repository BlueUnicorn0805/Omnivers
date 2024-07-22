package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class StoreCate implements Parcelable {
    private String ID;
    private String description;
    private ArrayList<StoreItem> items = new ArrayList<>();
    private boolean removed;
    private boolean selected;

    public StoreCate() {
    }

    protected StoreCate(Parcel in) {
        ID = in.readString();
        description = in.readString();
        items = in.createTypedArrayList(StoreItem.CREATOR);
        removed = in.readInt() > 0;
    }

    public static final Creator<StoreCate> CREATOR = new Creator<StoreCate>() {
        @Override
        public StoreCate createFromParcel(Parcel in) {
            return new StoreCate(in);
        }

        @Override
        public StoreCate[] newArray(int size) {
            return new StoreCate[size];
        }
    };

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ArrayList<StoreItem> getItems() {
        return items;
    }
    public void addNewItem(StoreItem newItem) {
        items.add(newItem);
    }

    public boolean isRemoved() { return removed; }
    public void setRemoved(boolean removed) { this.removed = removed; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeString(description);
        parcel.writeTypedList(items);
        parcel.writeInt(removed ? 1 : 0);
    }
}
