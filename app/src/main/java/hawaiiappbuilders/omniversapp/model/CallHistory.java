package hawaiiappbuilders.omniversapp.model;

import java.util.Date;

public class CallHistory {
    int id;
    int ldbid;
    String phNumber;
    int statusID;
    int callType; // In out
    int callDuration;
    Date callDate;

    String name; // Not in db

    public CallHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLdbid() { return ldbid; }
    public void setLdbid(int ldbid) { this.ldbid = ldbid; }

    public String getPhNumber() { return phNumber; }
    public void setPhNumber(String phNumber) { this.phNumber = phNumber; }

    public int getStatusID() { return statusID; }
    public void setStatusID(int statusID) { this.statusID = statusID; }

    public int getCallType() { return callType; }
    public void setCallType(int callType) { this.callType = callType; }

    public Date getCallDate() { return callDate; }
    public void setCallDate(Date callDate) { this.callDate = callDate; }

    public int getCallDuration() { return callDuration; }
    public void setCallDuration(int callDuration) { this.callDuration = callDuration; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
