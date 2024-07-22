package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class ContactInfo implements Comparator<ContactInfo>, Parcelable {
    int id;
    String fname = "";
    String lname = "";
    String handle = "";
    ArrayList<String> emailList = new ArrayList<>();
    ArrayList<String> phoneList = new ArrayList<>();
    int mlid = 0;

    String email = "";
    String address = "";
    String city = "";
    String state = "";

    String suite = "";
    String zip = "";
    String cp = "";
    String dob = "";
    String shareloc = "";

    String youtube = "";
    String fb = "";
    String twitter = "";
    String linkedin = "";
    String pintrest = "";
    String snapchat = "";
    String instagram = "";
    String whatsapp = "";

    String co = "";
    String title = "";
    String workAddr = "";
    String website = "";
    String wEmail = "";
    String wp = "";
    String createDate = "";

    // Additional Items
    int pri = 0;
    String localDBOwnerMLID = "0";

    boolean friendLevel = false;
    String gender = "";
    String initial = "";
    String streetNum = "";
    String street = "";
    String ste = "";
    int utc = 0;
    String marital = "";

    int verified = 0;
    int rating = 0;
    int coa = 0;

    int personal = 0;
    int business = 0;
    int family = 0;

    int blocked = 0;
    int archived = 0;
    String lon = "0";
    String lat = "0";

    String editDate = "0";
    String industryID = "0";

    String groupInfo = "";

    String videoMeetingUrl = "";

    public ContactInfo() {
    }

    protected ContactInfo(Parcel in) {
        id = in.readInt();
        fname = in.readString();
        lname = in.readString();
        handle = in.readString();
        emailList = in.createStringArrayList();
        phoneList = in.createStringArrayList();
        mlid = in.readInt();
        email = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        suite = in.readString();
        zip = in.readString();
        cp = in.readString();
        dob = in.readString();
        shareloc = in.readString();
        youtube = in.readString();
        fb = in.readString();
        twitter = in.readString();
        linkedin = in.readString();
        pintrest = in.readString();
        snapchat = in.readString();
        instagram = in.readString();
        whatsapp = in.readString();
        co = in.readString();
        title = in.readString();
        workAddr = in.readString();
        website = in.readString();
        wEmail = in.readString();
        wp = in.readString();
        createDate = in.readString();
        pri = in.readInt();
        localDBOwnerMLID = in.readString();
        friendLevel = in.readByte() != 0;
        gender = in.readString();
        initial = in.readString();
        streetNum = in.readString();
        street = in.readString();
        ste = in.readString();
        utc = in.readInt();
        marital = in.readString();
        verified = in.readInt();
        rating = in.readInt();
        coa = in.readInt();
        personal = in.readInt();
        business = in.readInt();
        family = in.readInt();
        blocked = in.readInt();
        archived = in.readInt();
        lon = in.readString();
        lat = in.readString();
        editDate = in.readString();
        industryID = in.readString();

        groupInfo = in.readString();
        videoMeetingUrl = in.readString();
    }

    public static final Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        @Override
        public ContactInfo createFromParcel(Parcel in) {
            return new ContactInfo(in);
        }

        @Override
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };


    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    // ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // MLID
    public int getMlid() {
        return mlid;
    }

    public void setMlid(int mlid) {
        this.mlid = mlid;
    }

    // Name
    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFname() {
        if (fname != null) {
            return fname;
        }
        return "";
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLname() {
        if (lname != null) {
            return lname;
        }
        return "";
    }

    public void setName(String name) {
        lname = "";
        fname = "";

        if (name == null || name.equals("")) {
            name = "";
        } else {
            name = name.trim();
        }
        if (name.split(" ").length > 1) {
            lname = name.substring(name.lastIndexOf(" ") + 1);
            fname = name.substring(0, name.lastIndexOf(' '));
        } else {
            fname = name;
        }
    }

    public String getName() {
        String fullName = String.format("%s %s", fname, lname).trim();
        return fullName;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getEmail() {
        if (TextUtils.isEmpty(email) && emailList.size() > 0) {
            return emailList.get(0);
        } else {
            if (TextUtils.isEmpty(email))
                return "";
            return email;
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCSZ() {
        String cszInfo = String.format("%s, %s %s", city, state, zip).trim();
        if (cszInfo.startsWith(",")) {
            cszInfo = cszInfo.substring(1);
        }
        if (cszInfo.endsWith(",")) {
            cszInfo = cszInfo.substring(0, cszInfo.length() - 1);
        }
        return cszInfo;
    }

    public String getCp() {
        if (TextUtils.isEmpty(cp) && phoneList.size() > 0) {
            return phoneList.get(0);
        } else {
            if (TextUtils.isEmpty(cp))
                return "";
            return cp;
        }
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getShareloc() {
        return shareloc;
    }

    public void setShareloc(String shareloc) {
        this.shareloc = shareloc;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getPintrest() {
        return pintrest;
    }

    public void setPintrest(String pintrest) {
        this.pintrest = pintrest;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getCo() {
        if (co != null) {
            return co;
        }
        return "";
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkAddr() {
        return workAddr;
    }

    public void setWorkAddr(String workAddr) {
        this.workAddr = workAddr;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getwEmail() {
        return wEmail;
    }

    public void setwEmail(String wEmail) {
        this.wEmail = wEmail;
    }

    public String getWp() {
        return wp;
    }

    public void setWp(String wp) {
        this.wp = wp;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    // Email
    public ArrayList<String> getEmailList() {
        return emailList;
    }

    public void setEmailData(String emailData) {
        if (TextUtils.isEmpty(emailData))
            return;

        emailData = emailData.trim();

        String[] emails = emailData.split(",");
        for (String email : emails) {
            emailList.add(email);
        }
    }

    public String getEmailData() {
        String emailData = "";
        for (String email : emailList) {
            if (!TextUtils.isEmpty(emailData)) {
                emailData += ",";
            }
            emailData += email;
        }

        return emailData;
    }

    public void addNewEmail(String newEmail) {
        newEmail = newEmail.trim();
        if (emailList.contains(newEmail)) {
            return;
        }

        emailList.add(newEmail);
    }

    // Phone
    public ArrayList<String> getPhoneList() {
        return phoneList;
    }

    public void setPhoneData(String phoneData) {
        if (TextUtils.isEmpty(phoneData))
            return;

        phoneData = phoneData.trim();

        String[] phones = phoneData.split(",");
        for (String phone : phones) {
            phoneList.add(phone);
        }
    }

    public String getPhoneData() {
        String phoneData = "";
        for (String phone : phoneList) {
            if (!TextUtils.isEmpty(phoneData)) {
                phoneData += ",";
            }
            phoneData += phone;
        }
        return phoneData;
    }

    public String getPhoneMetaData() {
        String phoneData = "";
        for (String phone : phoneList) {
            if (!TextUtils.isEmpty(phoneData)) {
                phoneData += ",";
            }
            phoneData += formatPhone(phone);
        }
        return phoneData;
    }

    public void addNewPhone(String newPhone) {
        newPhone = newPhone.trim();
        for (String phone : phoneList) {
            String phoneDigits = formatPhone(phone);

            if (phoneDigits.equals(formatPhone(newPhone))) {
                return;
            }
        }

        phoneList.add(newPhone);
    }

    private String formatPhone(String num) {
        String edited = "";
        for (int i = 0; i < num.length(); i++) {

            char c = num.charAt(i);
            if (i == 0 && c == '+') {
                edited += c;
            } else if (Character.isDigit(c)) {
                edited += c;
            }
        }
        return edited;
    }

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    public String getLocalDBOwnerMLID() {
        return localDBOwnerMLID;
    }

    public void setLocalDBOwnerMLID(String localDBOwnerMLID) {
        this.localDBOwnerMLID = localDBOwnerMLID;
    }

    public boolean isFriendLevel() {
        return friendLevel;
    }

    public void setFriendLevel(boolean friendLevel) {
        this.friendLevel = friendLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSte() {
        return ste;
    }

    public void setSte(String ste) {
        this.ste = ste;
    }

    public int getUtc() {
        return utc;
    }

    public void setUtc(int utc) {
        this.utc = utc;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getCoa() {
        return coa;
    }

    public void setCoa(int coa) {
        this.coa = coa;
    }

    public int getPersonal() {
        return personal;
    }

    public void setPersonal(int personal) {
        this.personal = personal;
    }

    public int getBusiness() {
        return business;
    }

    public void setBusiness(int business) {
        this.business = business;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }

    public String getIndustryID() {
        return industryID;
    }

    public void setIndustryID(String industryID) {
        this.industryID = industryID;
    }

    public String getVideoMeetingUrl() {
        return videoMeetingUrl;
    }

    public void setVideoMeetingUrl(String videoMeetingUrl) {
        this.videoMeetingUrl = videoMeetingUrl;
    }

    @Override
    public int compare(ContactInfo left, ContactInfo right) {

        if (left.getPri() != right.getPri()) {
            return right.getPri() - left.getPri();
        } else {
            String str1 = left.toString();
            String str2 = right.toString();

            return str1.compareTo(str2);
        }
    }

    @Override
    public String toString() {
        if (TextUtils.isEmpty(co)) {
            String value = fname + " " + lname;
            return value.trim();
        } else {
            String value = (co + " " + fname).trim() + " " + lname;
            return value.trim();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(handle);
        parcel.writeStringList(emailList);
        parcel.writeStringList(phoneList);
        parcel.writeInt(mlid);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(city);
        parcel.writeString(state);
        parcel.writeString(suite);
        parcel.writeString(zip);
        parcel.writeString(cp);
        parcel.writeString(dob);
        parcel.writeString(shareloc);
        parcel.writeString(youtube);
        parcel.writeString(fb);
        parcel.writeString(twitter);
        parcel.writeString(linkedin);
        parcel.writeString(pintrest);
        parcel.writeString(snapchat);
        parcel.writeString(instagram);
        parcel.writeString(whatsapp);
        parcel.writeString(co);
        parcel.writeString(title);
        parcel.writeString(workAddr);
        parcel.writeString(website);
        parcel.writeString(wEmail);
        parcel.writeString(wp);
        parcel.writeString(createDate);
        parcel.writeInt(pri);
        parcel.writeString(localDBOwnerMLID);
        parcel.writeByte((byte) (friendLevel ? 1 : 0));
        parcel.writeString(gender);
        parcel.writeString(initial);
        parcel.writeString(streetNum);
        parcel.writeString(street);
        parcel.writeString(ste);
        parcel.writeInt(utc);
        parcel.writeString(marital);
        parcel.writeInt(verified);
        parcel.writeInt(rating);
        parcel.writeInt(coa);
        parcel.writeInt(personal);
        parcel.writeInt(business);
        parcel.writeInt(family);
        parcel.writeInt(blocked);
        parcel.writeInt(archived);
        parcel.writeString(lon);
        parcel.writeString(lat);
        parcel.writeString(editDate);
        parcel.writeString(industryID);

        parcel.writeString(groupInfo);
        parcel.writeString(videoMeetingUrl);
    }
}
