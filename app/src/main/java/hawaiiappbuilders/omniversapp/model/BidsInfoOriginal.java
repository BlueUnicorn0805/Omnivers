package hawaiiappbuilders.omniversapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BidsInfoOriginal implements Serializable {

    @SerializedName("BidID")
    private String bidID;

    @SerializedName("DelID")
    private String delID;

    @SerializedName("MLID")
    private String mlID;

    @SerializedName("driverID")
    private String driverID;

    @SerializedName("Rating")
    private float ratings;

    @SerializedName("DFN")
    private String DFName;

    @SerializedName("SFN")
    private String SFName;

    @SerializedName("Lat")
    private Double lat;

    @SerializedName("Lon")
    private Double lon;

    @SerializedName("BidAmt")
    private float bidAmount;

    @SerializedName("ETAPU")
    private String pickTime;

    public BidsInfoOriginal(String name, int amount, int rating, String time) {
        DFName = name;
        bidAmount = amount;
        ratings = rating;
        pickTime = time;
    }

    public String getBidID() {
        return bidID;
    }
    public void setBidID(String delid) {
        this.bidID = delid;
    }

    public String getDelID() { return delID; }
    public void setDelID(String delID) { this.delID = delID; }

    public String getMlID() { return mlID; }
    public void setMlID(String mlID) { this.mlID = mlID; }

    public String getDriverID() { return driverID; }
    public void setDriverID(String driverID) { this.driverID = driverID; }

    public float getRatings() {
        return ratings;
    }
    public void setRatings(float rating) {
        this.ratings = rating;
    }

    public String getDFName() {
        return DFName;
    }
    public void setDFName(String DFName) {
        this.DFName = DFName;
    }

    public String getSFName() { return SFName; }
    public void setSFName(String SFName) { this.SFName = SFName; }

    public float getBidAmount() {
        return bidAmount;
    }
    public void setBidAmount(float bidAmount) {
        this.bidAmount = bidAmount;
    }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getPickTime() {
        return pickTime;
    }
    public void setPickTime(String pickTime) {
        this.pickTime = pickTime;
    }
}
