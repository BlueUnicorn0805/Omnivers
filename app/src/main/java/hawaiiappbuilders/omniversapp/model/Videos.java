package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Videos implements Parcelable {

    long prodID;

    String Title;

    String Link;

    String Descript;

    String HeadLine;

    String StartDate;

    String StopDate;

    String timeDesc;

    String POC;

    String email;

    String Creator;

    Double GenAdmission;

    Double Lat;

    Double Lon;

    int catID;

    int tkReq;

    int sellerID;

    String Location;
    String addressFULL;
    String City;
    String State;
    Integer zip;

    public Videos() {
    }

    protected Videos(Parcel in) {
        prodID = in.readLong();
        Title = in.readString();
        Link = in.readString();
        Descript = in.readString();
        HeadLine = in.readString();
        StartDate = in.readString();
        StopDate = in.readString();
        POC = in.readString();
        timeDesc = in.readString();
        email = in.readString();
        Creator = in.readString();
        GenAdmission = in.readDouble();
        Lon = in.readDouble();
        Lat = in.readDouble();
        tkReq = in.readInt();
        catID = in.readInt();
        sellerID = in.readInt();
        Location = in.readString();
        addressFULL = in.readString();
        City = in.readString();
        State = in.readString();
        zip = in.readInt();
    }

    public static final Creator<Videos> CREATOR = new Creator<Videos>() {
        @Override
        public Videos createFromParcel(Parcel in) {
            return new Videos(in);
        }

        @Override
        public Videos[] newArray(int size) {
            return new Videos[size];
        }
    };

    public long getProdID() {
        return prodID;
    }

    public void setProdID(long prodID) {
        this.prodID = prodID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getDescript() {
        return Descript;
    }

    public void setDescript(String descript) {
        Descript = descript;
    }

    public String getHeadLine() {
        return HeadLine;
    }

    public void setHeadLine(String headLine) {
        HeadLine = headLine;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getStopDate() {
        return StopDate;
    }

    public void setStopDate(String stopDate) {
        StopDate = stopDate;
    }

    public String getTimeDesc() {
        return timeDesc;
    }

    public void setTimeDesc(String timeDesc) {
        this.timeDesc = timeDesc;
    }

    public String getPOC() {
        return POC;
    }

    public void setPOC(String POC) {
        this.POC = POC;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public Double getGenAdmission() {
        return GenAdmission;
    }

    public void setGenAdmission(Double genAdmission) {
        GenAdmission = genAdmission;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }

    public int getCatID() {
        return catID;
    }

    public void setCatID(int catID) {
        this.catID = catID;
    }

    public int getTkReq() {
        return tkReq;
    }

    public void setTkReq(int tkReq) {
        this.tkReq = tkReq;
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getAddressFULL() {
        return addressFULL;
    }

    public void setAddressFULL(String addressFULL) {
        this.addressFULL = addressFULL;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(prodID);
        dest.writeString(Title);
        dest.writeString(Link);
        dest.writeString(Descript);
        dest.writeString(HeadLine);
        dest.writeString(StartDate);
        dest.writeString(StopDate);
        dest.writeString(POC);
        dest.writeString(timeDesc);
        dest.writeString(email);
        dest.writeString(Creator);
        dest.writeDouble(GenAdmission);
        dest.writeDouble(Lon);
        dest.writeDouble(Lat);
        dest.writeInt(tkReq);
        dest.writeInt(catID);
        dest.writeInt(sellerID);
        dest.writeString(Location);
        dest.writeString(addressFULL);
        dest.writeString(City);
        dest.writeString(State);
        dest.writeInt(zip);
    }
}
