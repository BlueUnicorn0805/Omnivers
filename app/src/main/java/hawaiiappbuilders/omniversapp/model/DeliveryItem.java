package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DeliveryItem implements Parcelable {

    @SerializedName("DelID")
    private String delID;

    @SerializedName("Name")
    private String userName;

    @SerializedName("fromAdd")
    private String fromAddress;

    @SerializedName("StreetNum")
    private String streetNum;

    @SerializedName("Street")
    private String street;

    @SerializedName("fLat")
    private Double lat;

    @SerializedName("fLon")
    private Double lon;

    @SerializedName("tLat")
    private Double toLat;

    @SerializedName("tLon")
    private Double toLon;

    @SerializedName("DelToAdd")
    private String delToAdd;

    @SerializedName("StatusID")
    private String statusID;

    @SerializedName("DriverID")
    private String driverID;

    @SerializedName("AgreedAmt")
    private float agreedAmt;

    @SerializedName("Rating")
    private float ratings;

    @SerializedName("BidAmt")
    private float bidAmount;

    @SerializedName("ETAPU")
    private String pickTime;

    @SerializedName("PakSize")
    private String PakSize;

    @SerializedName("PakWgt")
    private int PakWgt;

    @SerializedName("Instructions")
    private String Instructions;

    @SerializedName("QTY")
    private String QTY;

    @SerializedName("PULatestTime")
    private String puLatestTime;

    @SerializedName("AcceptedLocalTime")
    private String acceptedLocalTime;

    @SerializedName("PickedUpLocalTime")
    private String pickedUpLocalTime;

    // ------------------------ From Address Relations
    @SerializedName("fPH")
    private String fPH;

    @SerializedName("fName")
    private String fName;

    @SerializedName("fAdd")
    private String fAdd;

    @SerializedName("fApt")
    private String fApt;

    @SerializedName("fFloor")
    private String fFloor;

    @SerializedName("fCSZ")
    private String fCSZ;

    // -------------------------- To Address Relations
    @SerializedName("tPH")
    private String tPH;

    @SerializedName("tName")
    private String tName;

    @SerializedName("tAdd")
    private String tAdd;

    @SerializedName("tApt")
    private String tApt;

    @SerializedName("tFloor")
    private String tFloor;

    @SerializedName("tCSZ")
    private String tCSZ;

    @SerializedName("none")
    private int none;

    @SerializedName("hot")
    private int hot;

    @SerializedName("cold")
    private int cold;

    private boolean pickuped;

    protected DeliveryItem(Parcel in) {
        delID = in.readString();
        userName = in.readString();
        fromAddress = in.readString();

        streetNum = in.readString();
        street = in.readString();
        if (in.readByte() == 0) {
            lat = null;
        } else {
            lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            lon = null;
        } else {
            lon = in.readDouble();
        }
        if (in.readByte() == 0) {
            toLat = null;
        } else {
            toLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            toLon = null;
        } else {
            toLon = in.readDouble();
        }
        delToAdd = in.readString();

        statusID = in.readString();

        driverID = in.readString();

        agreedAmt = in.readFloat();

        ratings = in.readFloat();
        bidAmount = in.readFloat();
        pickTime = in.readString();

        PakSize = in.readString();
        PakWgt = in.readInt();
        Instructions = in.readString();
        QTY = in.readString();

        puLatestTime = in.readString();
        acceptedLocalTime = in.readString();
        pickedUpLocalTime = in.readString();

        fPH = in.readString();
        fName = in.readString();
        fAdd = in.readString();
        fApt = in.readString();
        fFloor = in.readString();
        fCSZ = in.readString();

        tPH = in.readString();
        tName = in.readString();
        tAdd = in.readString();
        tApt = in.readString();
        tFloor = in.readString();
        tCSZ = in.readString();

        none = in.readInt();
        hot = in.readInt();
        cold = in.readInt();

        pickuped = in.readInt() > 0;
    }

    public static final Creator<DeliveryItem> CREATOR = new Creator<DeliveryItem>() {
        @Override
        public DeliveryItem createFromParcel(Parcel in) {
            return new DeliveryItem(in);
        }

        @Override
        public DeliveryItem[] newArray(int size) {
            return new DeliveryItem[size];
        }
    };

    public String getDelID() {
        return delID;
    }
    public void setDelID(String delid) {
        this.delID = delid;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }

    public String getStreetNum() { return streetNum; }
    public void setStreetNum(String streetNum) { this.streetNum = streetNum; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public Double getToLat() { return toLat; }
    public void setToLat(Double lat) { this.toLat = lat; }

    public Double getToLon() { return toLon; }
    public void setToLon(Double lon) { this.toLon = lon; }

    public String getDelToAdd() { return delToAdd; }
    public void setDelToAdd(String delToAdd) { this.delToAdd = delToAdd; }

    public String getStatusID() { return statusID; }
    public void setStatusID(String statusID) { this.statusID = statusID; }

    public String getDriverID() { return driverID; }
    public void setDriverID(String driverID) { this.driverID = driverID; }

    public float getAgreedAmt() { return agreedAmt; }
    public void setAgreedAmt(float agreedAmt) { this.agreedAmt = agreedAmt; }

    public float getRatings() {
        return ratings;
    }
    public void setRatings(float rating) {
        this.ratings = rating;
    }

    public float getBidAmount() {
        return bidAmount;
    }
    public void setBidAmount(float bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getPickTime() {
        return pickTime;
    }
    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }

    public String getPakSize() { return PakSize; }
    public void setPakSize(String pakSize) { PakSize = pakSize; }

    public int getPakWgt() { return PakWgt; }
    public void setPakWgt(int pakWgt) { PakWgt = pakWgt; }

    public String getInstructions() { return Instructions; }
    public void setInstructions(String instructions) { Instructions = instructions; }

    public String getQTY() { return QTY; }
    public void setQTY(String QTY) { this.QTY = QTY; }

    public String getPuLatestTime() { return puLatestTime; }
    public void setPuLatestTime(String puLatestTime) { this.puLatestTime = puLatestTime; }

    public String getAcceptedLocalTime() { return acceptedLocalTime; }
    public void setAcceptedLocalTime(String acceptedLocalTime) { this.acceptedLocalTime = acceptedLocalTime; }

    public String getPickedUpLocalTime() { return pickedUpLocalTime; }
    public void setPickedUpLocalTime(String pickedUpLocalTime) { this.pickedUpLocalTime = pickedUpLocalTime; }

    // -------------- To Address Information
    public String getfPH() { return fPH; }
    public void setfPH(String ph) { this.fPH = ph; }

    public String getfName() { return fName; }
    public void setfName(String name) { this.fName = name; }

    public String getfAdd() { return fAdd; }
    public void setfAdd(String add) { this.fAdd = add; }

    public String getfApt() { return fApt; }
    public void setfApt(String apt) { this.fApt = apt; }

    public String getfFloor() { return fFloor; }
    public void setfFloor(String floor) { this.fFloor = floor; }

    public String getfCSZ() { return fCSZ; }
    public void setfCSZ(String csz) { this.fCSZ = csz; }

    // -------------- To Address Information
    public String gettPH() { return tPH; }
    public void settPH(String ph) { this.tPH = ph; }

    public String gettName() { return tName; }
    public void settName(String name) { this.tName = name; }

    public String gettAdd() { return tAdd; }
    public void settAdd(String add) { this.tAdd = add; }

    public String gettApt() { return tApt; }
    public void settApt(String apt) { this.tApt = apt; }

    public String gettFloor() { return tFloor; }
    public void settFloor(String floor) { this.tFloor = floor; }

    public String gettCSZ() { return tCSZ; }
    public void settCSZ(String csz) { this.tCSZ = csz; }

    public int getNone() { return none; }
    public void setNone(int value) { this.none = value; }

    public int getHot() { return hot; }
    public void setHot(int value) { this.hot = value; }

    public int getCold() { return cold; }
    public void setCold(int value) { this.cold = value; }

    // --------------- Pickup Status ------------------
    public boolean isPickuped() { return pickuped; }
    public void setPickuped(boolean pickuped) { this.pickuped = pickuped; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(delID);
        parcel.writeString(userName);
        parcel.writeString(fromAddress);
        parcel.writeString(streetNum);
        parcel.writeString(street);

        if (lat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(lat);
        }
        if (lon == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(lon);
        }
        if (toLat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(toLat);
        }
        if (toLon == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(toLon);
        }
        parcel.writeString(delToAdd);

        parcel.writeString(statusID);
        parcel.writeString(driverID);
        parcel.writeFloat(agreedAmt);

        parcel.writeFloat(ratings);
        parcel.writeFloat(bidAmount);
        parcel.writeString(pickTime);

        parcel.writeString(PakSize);
        parcel.writeInt(PakWgt);
        parcel.writeString(Instructions);
        parcel.writeString(QTY);

        parcel.writeString(puLatestTime);
        parcel.writeString(acceptedLocalTime);
        parcel.writeString(pickedUpLocalTime);

        parcel.writeString(fPH);
        parcel.writeString(fName);
        parcel.writeString(fAdd);
        parcel.writeString(fApt);
        parcel.writeString(fFloor);
        parcel.writeString(fCSZ);

        parcel.writeString(tPH);
        parcel.writeString(tName);
        parcel.writeString(tAdd);
        parcel.writeString(tApt);
        parcel.writeString(tFloor);
        parcel.writeString(tCSZ);

        parcel.writeInt(none);
        parcel.writeInt(hot);
        parcel.writeInt(cold);

        parcel.writeInt(pickuped ? 1 : 0);
    }
}
