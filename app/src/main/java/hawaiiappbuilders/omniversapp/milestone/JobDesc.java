package hawaiiappbuilders.omniversapp.milestone;

import android.os.Parcel;
import android.os.Parcelable;

public class JobDesc implements Parcelable {

    String jobTitle;

    String jobDescription;

    String postedBy;

    String timestamp;

    Double jobCost;

    public JobDesc() {

    }

    public JobDesc(Parcel in) {
        this.jobTitle = in.readString();
        this.jobDescription = in.readString();
        this.postedBy = in.readString();
        this.timestamp = in.readString();
        this.jobCost = in.readDouble();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getJobCost() {
        return jobCost;
    }

    public void setJobCost(Double jobCost) {
        this.jobCost = jobCost;
    }

    public static final Creator<JobDesc> CREATOR = new Creator<JobDesc>() {
        @Override
        public JobDesc createFromParcel(Parcel in) {
            return new JobDesc(in);
        }

        @Override
        public JobDesc[] newArray(int size) {
            return new JobDesc[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(jobTitle);
        parcel.writeString(jobDescription);
        parcel.writeString(postedBy);
        parcel.writeString(timestamp);
        parcel.writeDouble(jobCost);
    }

}
