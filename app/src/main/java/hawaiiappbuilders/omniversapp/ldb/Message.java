package hawaiiappbuilders.omniversapp.ldb;

public class Message {
    int ID;
    int MLID;
    String Type = "";
    String StatusID = "";
    int fromID;
    int toID;
    int employerID;
    String Msg = "";
    String Name = "";
    String CreateDate = "";

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public int getMLID() { return MLID; }
    public void setMLID(int MLID) { this.MLID = MLID; }

    public String getStatusID() { return StatusID; }
    public void setStatusID(String statusID) { StatusID = statusID; }

    public int getFromID() { return fromID; }
    public void setFromID(int fromID) { this.fromID = fromID; }

    public int getToID() { return toID; }
    public void setToID(int toID) { this.toID = toID; }

    public int getEmployerID() { return employerID; }
    public void setEmployerID(int employerID) { this.employerID = employerID; }

    public String getMsg() { return Msg; }
    public void setMsg(String msg) { Msg = msg; }

    public String getCreateDate() { return CreateDate; }
    public void setCreateDate(String createDate) {
        CreateDate = createDate.replace("'T'", " ").replace("T", " "); }

    public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public String getType() { return Type; }
    public void setType(String type) { Type = type; }

    public String getChannel() {
        if (fromID > toID) {
            return String.format("%d-%d", toID, fromID);
        } else {
            return String.format("%d-%d", fromID, toID);
        }
    }
}
