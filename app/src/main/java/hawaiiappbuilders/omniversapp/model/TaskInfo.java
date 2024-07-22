package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskInfo implements Parcelable {
    private String ID;
    private float priority;
    private String description;
    private String assigned;
    private String completed;

    private boolean selected;

    public TaskInfo() {
    }

    protected TaskInfo(Parcel in) {
        ID = in.readString();
        priority = in.readFloat();
        description = in.readString();
        assigned = in.readString();
        completed = in.readString();
        selected = in.readByte() != 0;
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        @Override
        public TaskInfo createFromParcel(Parcel in) {
            return new TaskInfo(in);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public float getPriority() { return priority; }
    public void setPriority(float priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssigned() { return assigned; }
    public void setAssigned(String assigned) { this.assigned = assigned; }

    public String getCompleted() { return completed; }
    public void setCompleted(String completed) { this.completed = completed; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ID);
        parcel.writeFloat(priority);
        parcel.writeString(description);
        parcel.writeString(assigned);
        parcel.writeString(completed);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
