package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class HistoryData implements Comparator<HistoryData>, Parcelable {

   int id;
   String date;
   String time;
   Double lat;
   Double lon;
   String zip;
   String streetAddress;
   String city;
   String state;
   String fullAddress;

   public HistoryData() {
   }

   public HistoryData(Parcel in) {
      this.id = in.readInt();
      this.date = in.readString();
      this.time = in.readString();
      this.lat = in.readDouble();
      this.lon = in.readDouble();
      this.zip = in.readString();
      this.streetAddress = in.readString();
      this.city = in.readString();
      this.state = in.readString();
      this.fullAddress = in.readString();
   }

   public static final Creator<HistoryData> CREATOR = new Creator<HistoryData>() {
      @Override
      public HistoryData createFromParcel(Parcel in) {
         return new HistoryData(in);
      }

      @Override
      public HistoryData[] newArray(int size) {
         return new HistoryData[size];
      }
   };


   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public Double getLat() {
      return lat;
   }

   public void setLat(Double lat) {
      this.lat = lat;
   }

   public Double getLon() {
      return lon;
   }

   public void setLon(Double lon) {
      this.lon = lon;
   }

   public String getZip() {
      return zip;
   }

   public void setZip(String zip) {
      this.zip = zip;
   }

   public String getStreetAddress() {
      return streetAddress;
   }

   public void setStreetAddress(String streetAddress) {
      this.streetAddress = streetAddress;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public String getFullAddress() {
      return fullAddress;
   }

   public void setFullAddress(String fullAddress) {
      this.fullAddress = fullAddress;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int flags) {
      parcel.writeInt(id);
      parcel.writeString(date);
      parcel.writeString(time);
      parcel.writeDouble(lat);
      parcel.writeDouble(lon);
      parcel.writeString(zip);
      parcel.writeString(streetAddress);
      parcel.writeString(city);
      parcel.writeString(state);
      parcel.writeString(fullAddress);
   }

   @Override
   public int compare(HistoryData left, HistoryData right) {
      String str1 = left.toString();
      String str2 = right.toString();

      return str1.compareTo(str2);
   }
}