package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InvoiceItem implements Parcelable {

    private long orderID;
    private String buyerName;
    private String amt;
    private String nameDesc;

    private String dt;
    private String toID;
    private String fromID;
    private String status;

    protected InvoiceItem(Parcel in) {
        orderID = in.readInt();
        buyerName = in.readString();
        amt = in.readString();
        nameDesc = in.readString();
        dt = in.readString();
        toID = in.readString();
        fromID = in.readString();
        status = in.readString();
    }

    public static final Creator<InvoiceItem> CREATOR = new Creator<InvoiceItem>() {
        @Override
        public InvoiceItem createFromParcel(Parcel in) {
            return new InvoiceItem(in);
        }

        @Override
        public InvoiceItem[] newArray(int size) {
            return new InvoiceItem[size];
        }
    };

    public long getOrderID() {
        return orderID;
    }
    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public String getBuyerName() {
        return buyerName;
    }
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getAmt() {
        return amt;
    }
    public void setAmt(String _hours) {
        this.amt = _hours;
    }

    public String getNameDesc() {
        return nameDesc;
    }
    public void setNameDesc(String nameDesc) {
        this.nameDesc = nameDesc;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getToID() {
        return toID;
    }
    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getFromID() { return fromID; }
    public void setFromID(String fromID) { this.fromID = fromID; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public InvoiceItem() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(orderID);
        dest.writeString(buyerName);
        dest.writeString(amt);
        dest.writeString(nameDesc);
        dest.writeString(dt);
        dest.writeString(toID);
        dest.writeString(fromID);
        dest.writeString(status);
    }
}
