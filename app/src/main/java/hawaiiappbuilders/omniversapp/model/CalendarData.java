package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CalendarData {
    @SerializedName("data")
    List<Data> calendarData;

    public List<Data> getCalendarData() {
        return calendarData;
    }

    public static class Data implements Parcelable {

        @SerializedName("TZ")
        float TZ;

        @SerializedName("videoMeetingURL")
        String videoMeetingURL;

        @SerializedName("LDBID")
        int ldbID;

        @SerializedName("ApptWithMLID")
        int apptWithMLID;

        @SerializedName("CALID")
        long calId;

        @SerializedName("CALOrderID")
        int orderId;

        @SerializedName("CALAmt")
        String calAmt;

        @SerializedName("CALSetByID")
        int calSetById;

        @SerializedName("apptLat")
        double apptLat;

        @SerializedName("apptLon")
        double apptLon;

        @SerializedName("sellerLAT")
        double sellerLat;

        @SerializedName("sellerLON")
        double sellerLon;

        @SerializedName("sellerID")
        int sellerId;

        @SerializedName("address")
        String address;

        @SerializedName("MeetingID")
        String meetingId;

        @SerializedName("NoteID")
        String noteId;

        @SerializedName("BuyerID")
        String buyerId;

        @SerializedName("DetailID")
        String detailId;

        @SerializedName("DetailStatusID")
        String detailStatusId;

        @SerializedName("ApptStartTime")
        String startDate;

        @SerializedName("ApptEndTime")
        String endDate;

        @SerializedName("Title")
        String title;

        @SerializedName("Qty")
        int qty;

        @SerializedName("Name")
        String name;

        @SerializedName("CP")
        String cp;

        @SerializedName("Email")
        String email;

        @SerializedName("Price")
        float price;

        @SerializedName("TotPrice")
        float totPrice;

        @SerializedName("attendeeMLID")
        int attendeeMLID;

        @SerializedName("senderName")
        String attendeeName;

        @SerializedName("share")
        String share;

        @SerializedName("ApptStatusID")
        int ApptStatusID;

        protected Data(Parcel in) {
            this.TZ = in.readInt();
            videoMeetingURL = in.readString();
            ldbID = in.readInt();
            apptWithMLID = in.readInt();
            calId = in.readLong();
            orderId = in.readInt();
            calAmt = in.readString();
            calSetById = in.readInt();
            apptLat = in.readDouble();
            apptLon = in.readDouble();
            sellerLat = in.readDouble();
            sellerLon = in.readDouble();
            sellerId = in.readInt();
            this.address = in.readString();
            meetingId = in.readString();
            noteId = in.readString();
            buyerId = in.readString();
            detailId = in.readString();
            detailStatusId = in.readString();
            startDate = in.readString();
            endDate = in.readString();
            title = in.readString();
            qty = in.readInt();
            name = in.readString();
            cp = in.readString();
            email = in.readString();
            price = in.readFloat();
            totPrice = in.readFloat();
            attendeeMLID = in.readInt();
            attendeeName = in.readString();
            share = in.readString();
            ApptStatusID = in.readInt();
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public float getTZ() {
            return TZ;
        }

        public void setTZ(float TZ) {
            this.TZ = TZ;
        }

        public String getVideoMeetingURL() {
            return videoMeetingURL;
        }

        public void setVideoMeetingURL(String videoMeetingURL) {
            this.videoMeetingURL = videoMeetingURL;
        }

        public int getLdbID() {
            return ldbID;
        }

        public void setLdbID(int ldbID) {
            this.ldbID = ldbID;
        }

        public int getApptWithMLID() {
            return apptWithMLID;
        }

        public void setApptWithMLID(int apptWithMLID) {
            this.apptWithMLID = apptWithMLID;
        }

        public long getCalId() {
            return calId;
        }

        public void setCalId(long calId) {
            this.calId = calId;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getCalSetById() {
            return calSetById;
        }

        public void setCalSetById(int calSetById) {
            this.calSetById = calSetById;
        }

        public double getApptLat() {
            return apptLat;
        }

        public void setApptLat(double apptLat) {
            this.apptLat = apptLat;
        }

        public double getApptLon() {
            return apptLon;
        }

        public void setApptLon(double apptLon) {
            this.apptLon = apptLon;
        }

        public double getSellerLat() {
            return sellerLat;
        }

        public void setSellerLat(double sellerLat) {
            this.sellerLat = sellerLat;
        }

        public double getSellerLon() {
            return sellerLon;
        }

        public void setSellerLon(double sellerLon) {
            this.sellerLon = sellerLon;
        }

        public int getSellerId() {
            return sellerId;
        }

        public void setSellerId(int sellerId) {
            this.sellerId = sellerId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getNoteId() {
            return noteId;
        }

        public void setNoteId(String noteId) {
            this.noteId = noteId;
        }

        public String getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(String buyerId) {
            this.buyerId = buyerId;
        }

        public String getDetailStatusId() {
            return detailStatusId;
        }

        public void setDetailStatusId(String detailStatusId) {
            this.detailStatusId = detailStatusId;
        }

        public int getStatusId() {
            int statusId = 0;
            try {
                statusId = Integer.parseInt(detailStatusId);
            } catch (Exception e) {
            }
            return statusId;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCp() {
            return cp;
        }

        public void setCp(String cp) {
            this.cp = cp;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        //---------------------------- Not used now ----------------------------
        public String getCalAmt() {
            return calAmt;
        }

        public void setCalAmt(String calAmt) {
            this.calAmt = calAmt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetailId() {
            return detailId;
        }

        public void setDetailId(String detailId) {
            this.detailId = detailId;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getTotPrice() {
            return totPrice;
        }

        public void setTotPrice(float totPrice) {
            this.totPrice = totPrice;
        }

        public int getAttendeeMLID() {
            return attendeeMLID;
        }

        public void setAttendeeMLID(int attendeeMLID) {
            this.attendeeMLID = attendeeMLID;
        }

        public String getAttendeeName() {
            return attendeeName;
        }

        public void setAttendeeName(String attendeeName) {
            this.attendeeName = attendeeName;
        }

        public String getShare() {
            return share;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public int getApptStatusID() {
            return ApptStatusID;
        }

        public void setApptStatusID(int apptStatusID) {
            ApptStatusID = apptStatusID;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeDouble(TZ);
            parcel.writeString(videoMeetingURL);
            parcel.writeInt(ldbID);
            parcel.writeInt(apptWithMLID);
            parcel.writeLong(calId);
            parcel.writeInt(orderId);
            parcel.writeString(calAmt);
            parcel.writeInt(calSetById);
            parcel.writeDouble(apptLat);
            parcel.writeDouble(apptLon);
            parcel.writeDouble(sellerLat);
            parcel.writeDouble(sellerLon);
            parcel.writeInt(sellerId);
            parcel.writeString(address);
            parcel.writeString(meetingId);
            parcel.writeString(noteId);
            parcel.writeString(buyerId);
            parcel.writeString(detailId);
            parcel.writeString(detailStatusId);
            parcel.writeString(startDate);
            parcel.writeString(endDate);
            parcel.writeString(title);
            parcel.writeInt(qty);
            parcel.writeString(name);
            parcel.writeString(cp);
            parcel.writeString(email);
            parcel.writeFloat(price);
            parcel.writeFloat(totPrice);
            parcel.writeInt(attendeeMLID);
            parcel.writeString(attendeeName);
            parcel.writeString(share);
            parcel.writeInt(ApptStatusID);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            return calId != 0 ? calId == data.calId : data.calId == 0;
        }

        @Override
        public int hashCode() {
            return calId != 0 ? (int) calId : 0;
        }

        //-----------------------------------------------------------------------

    }

    private static List<Data> calendarDataList = new ArrayList<>();

    public static List<Data> getCalendarDataList() {
        return calendarDataList;
    }

    public static void setCalendarDataList(List<Data> calendarDataList) {
        CalendarData.calendarDataList.clear();
        CalendarData.calendarDataList.addAll(calendarDataList);
    }

    public static void resetCalendarDataList() {
        CalendarData.calendarDataList.clear();
    }
}
