package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OpenDeliveryInfo implements Parcelable {

    private String userName;
    private int bidAmount;
    private String address;
    private String pickTime;

    @SerializedName("DelID")
    private String DelID;

    @SerializedName("Lat")
    private double Lat;

    @SerializedName("Lon")
    private double Lon;

    @SerializedName("toLat")
    private double toLat;

    @SerializedName("toLon")
    private double toLon;

    @SerializedName("CapabilitiesID")
    private int CapabilitiesID;

    @SerializedName("StatusID")
    private int StatusID;

    @SerializedName("DriverID")
    private int DriverID;

    @SerializedName("AgreedAmt")
    private double AgreedAmt;

    //@SerializedName("ETAmins")
    //private int ETAmins;

    // -------------------------- Package Info Relations
    @SerializedName("PakSize")
    private int PakSize;

    @SerializedName("PakWgt")
    private int PakWgt;

    @SerializedName("Instructions")
    private String Instructions;

    @SerializedName("QTY")
    private String QTY;

    // -------------------------- Time Relations
    @SerializedName("PULatestTime")
    private String PULatestTime;

    @SerializedName("AcceptedLocalTime")
    private String AcceptedLocalTime;

    @SerializedName("PickedUpLocalTime")
    private String PickedUpLocalTime;

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

    public OpenDeliveryInfo(String name, int amount, String addr, String time) {
        userName = name;
        bidAmount = amount;
        address = addr;
        pickTime = time;
    }

    protected OpenDeliveryInfo(Parcel in) {
        userName = in.readString();
        bidAmount = in.readInt();
        address = in.readString();
        pickTime = in.readString();

        DelID = in.readString();

        Lat = in.readDouble();
        Lon = in.readDouble();
        toLat = in.readDouble();
        toLon = in.readDouble();

        CapabilitiesID = in.readInt();
        StatusID = in.readInt();
        DriverID = in.readInt();
        AgreedAmt = in.readDouble();

        //ETAmins = in.readInt();

        PakSize = in.readInt();
        PakWgt = in.readInt();

        Instructions = in.readString();
        QTY = in.readString();

        PULatestTime = in.readString();
        AcceptedLocalTime = in.readString();
        PickedUpLocalTime = in.readString();

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
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBidAmount() {
        return bidAmount;
    }
    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String addr) {
        this.address = addr;
    }

    public String getPickTime() {
        return pickTime;
    }
    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }

    public String getDelID() { return DelID; }
    public void setDelID(String delID) { DelID = delID; }

    public double getLat() { return Lat; }
    public void setLat(double lat) { Lat = lat; }

    public double getLon() { return Lon; }
    public void setLon(double lon) { Lon = lon; }

    public double getToLat() { return toLat; }
    public void setToLat(double toLat) { this.toLat = toLat; }

    public double getToLon() { return toLon; }
    public void setToLon(double toLon) { this.toLon = toLon; }

    public int getCapabilitiesID() { return CapabilitiesID; }
    public void setCapabilitiesID(int capabilitiesID) { CapabilitiesID = capabilitiesID; }

    public int getStatusID() { return StatusID; }
    public void setStatusID(int statusID) { StatusID = statusID; }

    public int getDriverID() { return DriverID; }
    public void setDriverID(int driverID) { DriverID = driverID; }

    public double getAgreedAmt() { return AgreedAmt; }
    public void setAgreedAmt(double agreedAmt) { AgreedAmt = agreedAmt; }

    //public int getETAmins() { return ETAmins; }
    //public void setETAmins(int ETAmins) { this.ETAmins = ETAmins; }

    // --------------- Package Information
    public int getPakSize() { return PakSize; }
    public void setPakSize(int pakSize) { PakSize = pakSize; }

    public int getPakWgt() { return PakWgt; }
    public void setPakWgt(int pakWgt) { PakWgt = pakWgt; }

    public String getInstructions() { return Instructions; }
    public void setInstructions(String instructions) { Instructions = instructions; }

    public String getQTY() { return QTY; }
    public void setQTY(String QTY) { this.QTY = QTY; }

    // -------------- Time Information
    public String getPULatestTime() { return PULatestTime; }
    public void setPULatestTime(String PULatestTime) { this.PULatestTime = PULatestTime; }

    public String getAcceptedLocalTime() { return AcceptedLocalTime; }
    public void setAcceptedLocalTime(String acceptedLocalTime) { AcceptedLocalTime = acceptedLocalTime; }

    public String getPickedUpLocalTime() { return PickedUpLocalTime; }
    public void setPickedUpLocalTime(String pickedUpLocalTime) { PickedUpLocalTime = pickedUpLocalTime; }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeInt(bidAmount);
        parcel.writeString(address);
        parcel.writeString(pickTime);

        parcel.writeString(DelID);

        parcel.writeDouble(Lat);
        parcel.writeDouble(Lon);
        parcel.writeDouble(toLat);
        parcel.writeDouble(toLon);

        parcel.writeInt(CapabilitiesID);
        parcel.writeInt(StatusID);
        parcel.writeInt(DriverID);
        parcel.writeDouble(AgreedAmt);

        //parcel.writeInt(ETAmins);

        parcel.writeInt(PakSize);
        parcel.writeInt(PakWgt);

        parcel.writeString(Instructions);
        parcel.writeString(QTY);

        parcel.writeString(PULatestTime);
        parcel.writeString(AcceptedLocalTime);
        parcel.writeString(PickedUpLocalTime);

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
    }

    public static final Creator<OpenDeliveryInfo> CREATOR = new Creator<OpenDeliveryInfo>() {
        @Override
        public OpenDeliveryInfo createFromParcel(Parcel in) {
            return new OpenDeliveryInfo(in);
        }

        @Override
        public OpenDeliveryInfo[] newArray(int size) {
            return new OpenDeliveryInfo[size];
        }
    };
}
