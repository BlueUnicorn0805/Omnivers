package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PickupAndApptInfo implements Parcelable {
    public String puDate = "";
    public String puTime = "";
    public String puCompany = "";
    public boolean isOneWayTrip = false;
    public String puVehicleTypeID = "";
    public String puStreet = "";
    public String puStreetNum = "";
    public String puSuite = "";
    public String puCity = "";
    public String puState = "";
    public String puZip = "";
    public String puPhone = "";
    public String puNote = "";

    public String date = "";
    public String time = "";
    public String company = "";
    public String street = "";
    public String streetNum = "";
    public String suite = "";
    public String city = "";
    public String state = "";
    public String zip = "";
    public String phone = "";
    public String note = "";
    public String repeat = "";

    public PickupAndApptInfo() {}
    public PickupAndApptInfo(Parcel in) {
        puDate = in.readString();
        puTime = in.readString();
        puCompany = in.readString();
        isOneWayTrip = in.readByte() != 0;
        puVehicleTypeID = in.readString();
        puStreet = in.readString();
        puStreetNum = in.readString();
        puSuite = in.readString();
        puCity = in.readString();
        puState = in.readString();
        puZip = in.readString();
        puPhone = in.readString();
        puNote = in.readString();
        date = in.readString();
        time = in.readString();
        company = in.readString();
        street = in.readString();
        streetNum = in.readString();
        suite = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readString();
        phone = in.readString();
        note = in.readString();
        repeat = in.readString();
    }

    public static final Creator<PickupAndApptInfo> CREATOR = new Creator<PickupAndApptInfo>() {
        @Override
        public PickupAndApptInfo createFromParcel(Parcel in) {
            return new PickupAndApptInfo(in);
        }

        @Override
        public PickupAndApptInfo[] newArray(int size) {
            return new PickupAndApptInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(puDate);
        parcel.writeString(puTime);
        parcel.writeString(puCompany);
        parcel.writeByte((byte) (isOneWayTrip ? 1 : 0));
        parcel.writeString(puVehicleTypeID);
        parcel.writeString(puStreet);
        parcel.writeString(puStreetNum);
        parcel.writeString(puSuite);
        parcel.writeString(puCity);
        parcel.writeString(puState);
        parcel.writeString(puZip);
        parcel.writeString(puPhone);
        parcel.writeString(puNote);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(company);
        parcel.writeString(street);
        parcel.writeString(streetNum);
        parcel.writeString(suite);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(zip);
        parcel.writeString(phone);
        parcel.writeString(note);
        parcel.writeString(repeat);
    }
}
