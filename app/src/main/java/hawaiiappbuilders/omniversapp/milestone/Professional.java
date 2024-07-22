package hawaiiappbuilders.omniversapp.milestone;

import android.os.Parcel;
import android.os.Parcelable;

public class Professional implements Parcelable {

    String status;
    String name;
    String title;
    int reviews;

    String email;

    String servicesOffered;

    Double startingPayment;

    public Professional() {
    }

    public Professional(Parcel in) {
        status = in.readString();
        name = in.readString();
        title = in.readString();
        reviews = in.readInt();
        email = in.readString();
        servicesOffered = in.readString();
        startingPayment = in.readDouble();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getServicesOffered() {
        return servicesOffered;
    }

    public void setServicesOffered(String servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public Double getStartingPayment() {
        return startingPayment;
    }

    public void setStartingPayment(Double startingPayment) {
        this.startingPayment = startingPayment;
    }

    public static final Creator<Professional> CREATOR = new Creator<Professional>() {
        @Override
        public Professional createFromParcel(Parcel in) {
            return new Professional(in);
        }

        @Override
        public Professional[] newArray(int size) {
            return new Professional[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(status);
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeInt(reviews);
        parcel.writeString(email);
        parcel.writeString(servicesOffered);
        parcel.writeDouble(startingPayment);
    }
}
