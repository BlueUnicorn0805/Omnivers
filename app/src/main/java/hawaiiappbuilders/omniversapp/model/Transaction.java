package hawaiiappbuilders.omniversapp.model;

public class Transaction {

    private String TxID,ItemDate,Amt,Name,toID, status, Note;

    public Transaction() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxID() {
        return TxID;
    }

    public void setTxID(String txID) {
        TxID = txID;
    }

    public String getItemDate() {
        return ItemDate;
    }

    public void setItemDate(String itemDate) {
        ItemDate = itemDate;
    }

    public String getAmt() {
        return Amt;
    }

    public void setAmt(String amt) {
        Amt = amt;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
