package hawaiiappbuilders.omniversapp.model;

public class TransferRequest {
    String ID;
    String amt;
    String TxRegID;
    String OrderID;
    String CreateDate;
    String Status;
    String acct;

    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }

    public String getAmt() { return amt; }
    public void setAmt(String amt) { this.amt = amt; }

    public String getTxRegID() { return TxRegID; }
    public void setTxRegID(String txRegID) { TxRegID = txRegID; }

    public String getOrderID() { return OrderID; }
    public void setOrderID(String orderID) { OrderID = orderID; }

    public String getCreateDate() { return CreateDate; }
    public void setCreateDate(String createDate) { CreateDate = createDate; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { Status = status; }

    public String getAcct() { return acct; }
    public void setAcct(String acct) { this.acct = acct; }
}
