package hawaiiappbuilders.omniversapp.model;

import com.google.gson.annotations.SerializedName;

public class Note {
    @SerializedName("note")
    String Note;
    @SerializedName("CreateDate")
    String CreateDate;

    public String getNote() { return Note; }
    public void setNote(String note) { Note = note; }

    public String getCreateDate() { return CreateDate; }
    public void setCreateDate(String createDate) { CreateDate = createDate; }
}
