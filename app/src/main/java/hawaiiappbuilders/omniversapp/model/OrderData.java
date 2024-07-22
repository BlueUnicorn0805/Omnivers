package hawaiiappbuilders.omniversapp.model;

public class OrderData {
    String ID;
    String Seller;
    String Status;
    String StatusID;

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getSeller() { return Seller; }
    public void setSeller(String seller) { Seller = seller; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { Status = status; }

    public String getStatusID() { return StatusID; }
    public void setStatusID(String statusID) { StatusID = statusID; }
}
