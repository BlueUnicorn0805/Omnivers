package hawaiiappbuilders.omniversapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Restaurant implements Parcelable {

    private int _id;
    private int _industryID;

    private String _name;
    private String _wp;
    private String _hours;
    private String _dist;
    private String _description;
    private double _lattiude;
    private double _longitude;
    private int _imageName;

    private float _rating;

    private String _address;
    private String _apt;

    private String _ste;
    private String _city;
    private String _st;
    private String _zip;
    private String _csz;

    private String link;

    private String MenuStyle;
    private String WelcomeMsg;
    private float TaxRate;

    private int orders;
    private int party;
    private int cater;
    private int res;
    private int del;
    private int appt;
    private int curb;

    private int onTable;
    private int pu;
    private int internalDel;
    private int currTakingOrders;

    private String MonB;
    private String MonE;
    private String TueB;
    private String TueE;
    private String WedB;
    private String WedE;
    private String ThuB;
    private String ThuE;
    private String FriB;
    private String FriE;
    private String SatB;
    private String SatE;
    private String SunB;
    private String SunE;
    
    private int _ownerID;

    private int closed;

    private double tableFee = 0;
    private double partyFee;
    private double DelFee;
    private double ResFee;
    private double CatFee;
    private double ApptFee;

    private int seekIT = 0;
    private int UTID = 0;

    // Menu :       0
    // Curbside :   1
    // Delivery :   2
    // Catering :   3
    // Party :      4
    // Reserve :    5
    private int option;

    public Restaurant() {}


    protected Restaurant(Parcel in) {
        _id = in.readInt();
        _industryID = in.readInt();
        _name = in.readString();
        _wp = in.readString();
        _hours = in.readString();
        _dist = in.readString();
        _description = in.readString();
        _lattiude = in.readDouble();
        _longitude = in.readDouble();
        _imageName = in.readInt();
        _rating = in.readFloat();
        _address = in.readString();
        _apt = in.readString();
        _ste = in.readString();
        _city = in.readString();
        _st = in.readString();
        _zip = in.readString();
        _csz = in.readString();

        link = in.readString();
        MenuStyle = in.readString();
        WelcomeMsg = in.readString();
        TaxRate = in.readFloat();

        orders = in.readInt();
        party = in.readInt();
        cater = in.readInt();
        res = in.readInt();
        del = in.readInt();
        appt = in.readInt();
        curb = in.readInt();

        onTable = in.readInt();
        pu = in.readInt();
        internalDel = in.readInt();
        currTakingOrders = in.readInt();

        MonB = in.readString();
        MonE = in.readString();
        TueB = in.readString();
        TueE = in.readString();
        WedB = in.readString();
        WedE = in.readString();
        ThuB = in.readString();
        ThuE = in.readString();
        FriB = in.readString();
        FriE = in.readString();
        SatB = in.readString();
        SatE = in.readString();
        SunB = in.readString();
        SunE = in.readString();
        _ownerID = in.readInt();
        closed = in.readInt();

        tableFee = in.readDouble();
        partyFee = in.readDouble();
        DelFee = in.readDouble();
        ResFee = in.readDouble();
        CatFee = in.readDouble();
        ApptFee = in.readDouble();

        seekIT = in.readInt();
        UTID = in.readInt();

        option = in.readInt();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_industryID() {
        return _industryID;
    }
    public void set_industryID(int _industryID) {
        this._industryID = _industryID;
    }

    public String get_name() {
        return _name;
    }
    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_wp() { return _wp; }
    public void set_wp(String _wp) { this._wp = _wp; }

    public String get_hours() {
        return _hours;
    }
    public void set_hours(String _hours) {
        this._hours = _hours;
    }

    public String get_dist() {
        return _dist;
    }
    public void set_dist(String _dist) {
        this._dist = _dist;
    }

    public String get_description() {
        return _description;
    }
    public void set_description(String _description) {
        this._description = _description;
    }

    public double get_lattiude() {
        return _lattiude;
    }
    public void set_lattiude(double _lattiude) {
        this._lattiude = _lattiude;
    }

    public double get_longitude() {
        return _longitude;
    }
    public void set_longitude(double _longitude) {
        this._longitude = _longitude;
    }

    public int get_imageName() {
        return _imageName;
    }
    public void set_imageName(int _imageName) {
        this._imageName = _imageName;
    }

    public float get_rating() { return _rating; }
    public void set_rating(float _rating) { this._rating = _rating; }

    public String get_address() { return _address; }
    public void set_address(String _address) { this._address = _address; }

    public String get_apt() { return _apt; }
    public void set_apt(String _apt) { this._apt = _apt; }

    public String getFullAddress() {
        String fullAddress = "";
        if (!TextUtils.isEmpty(_address)) {
            fullAddress += _address.trim();
        }
        if (!TextUtils.isEmpty(_city)) {
            fullAddress += ", " + _city.trim();
        }
        if (!TextUtils.isEmpty(_st)) {
            fullAddress += ", " + _st.trim();
        }
        if (!TextUtils.isEmpty(_zip)) {
            fullAddress += ", " + _zip.trim();
        }
        /*fullAddress += ", USA";*/

        if (fullAddress.startsWith(",")) {
            fullAddress = fullAddress.substring(1);
        }

        return fullAddress.trim();
    }

    public String getStZipCity() {
        String fullAddress = "";
        if (!TextUtils.isEmpty(_city)) {
            fullAddress += _city.trim();
        }
        if (!TextUtils.isEmpty(_st)) {
            fullAddress += ", " + _st.trim();
        }
        if (!TextUtils.isEmpty(_zip)) {
            fullAddress += ", " + _zip.trim();
        }
        /*fullAddress += ", USA";*/

        if (fullAddress.startsWith(",")) {
            fullAddress = fullAddress.substring(1);
        }

        return fullAddress.trim();
    }

    public String get_ste() { return _ste; }
    public void set_ste(String _ste) { this._ste = _ste; }

    public String get_city() { return _city; }
    public void set_city(String _city) { this._city = _city; }

    public String get_st() { return _st; }
    public void set_st(String _st) { this._st = _st; }

    public String get_zip() { return _zip; }
    public void set_zip(String _zip) { this._zip = _zip; }

    public String get_csz() {
        if (TextUtils.isEmpty(_csz)) {
            return String.format("%s, %s, %s", _city, _st, _zip);
        } else {
            return _csz;
        }
    }
    public void set_csz(String _csz) { this._csz = _csz; }

    public void setLink(String link) { this.link = link; }
    public String getLink() { return link; }

    public void setMenuStyle(String menuStyle) { MenuStyle = menuStyle; }
    public String getMenuStyle() { return MenuStyle; }

    public void setWelcomeMsg(String welcomeMsg) { WelcomeMsg = welcomeMsg; }
    public String getWelcomeMsg() { return WelcomeMsg; }

    public void setTaxRate(float taxRate) { TaxRate = taxRate; }
    public float getTaxRate() { return TaxRate; }

    public int getOrders() { return orders; }
    public void setOrders(int orders) { this.orders = orders; }

    public void setParty(int party) { this.party = party; }
    public int getParty() { return party; }

    public void setCater(int cater) { this.cater = cater; }
    public int getCater() { return cater; }

    public int getRes() { return res; }
    public void setRes(int res) { this.res = res; }

    public int getDel() { return del; }
    public void setDel(int del) { this.del = del; }

    public int getAppt() { return appt; }
    public void setAppt(int appt) { this.appt = appt; }

    public int getCurb() { return curb; }
    public void setCurb(int curb) { this.curb = curb; }

    public int getOnTable() { return onTable; }
    public void setOnTable(int onTable) { this.onTable = onTable; }

    public int getPu() { return pu; }
    public void setPu(int pu) { this.pu = pu; }

    public int getInternalDel() { return internalDel; }
    public void setInternalDel(int internalDel) { this.internalDel = internalDel; }

    public int getCurrTakingOrders() { return currTakingOrders; }
    public void setCurrTakingOrders(int currTakingOrders) { this.currTakingOrders = currTakingOrders; }

    public void setMonB(String monB) { MonB = monB; }
    public String getMonB() { return MonB; }

    public void setMonE(String monE) { MonE = monE; }
    public String getMonE() { return MonE; }

    public void setTueB(String tueB) { TueB = tueB; }
    public String getTueB() { return TueB; }

    public void setTueE(String tueE) { TueE = tueE; }
    public String getTueE() { return TueE; }

    public void setWedB(String wedB) { WedB = wedB; }
    public String getWedB() { return WedB; }

    public void setWedE(String wedE) { WedE = wedE; }
    public String getWedE() { return WedE; }

    public void setThuB(String thuB) { ThuB = thuB; }
    public String getThuB() { return ThuB; }

    public void setThuE(String thuE) { ThuE = thuE; }
    public String getThuE() { return ThuE; }

    public void setFriB(String friB) { FriB = friB; }
    public String getFriB() { return FriB; }

    public void setFriE(String friE) { FriE = friE; }
    public String getFriE() { return FriE; }

    public void setSatB(String satB) { SatB = satB; }
    public String getSatB() { return SatB; }

    public void setSatE(String satE) { SatE = satE; }
    public String getSatE() { return SatE; }

    public void setSunB(String sunB) { SunB = sunB; }
    public String getSunB() { return SunB; }

    public void setSunE(String sunE) { SunE = sunE; }
    public String getSunE() { return SunE; }

    public void set_ownerID(int _ownerID) { this._ownerID = _ownerID; }
    public int get_ownerID() { return _ownerID; }

    public void setClosed(int closed) { this.closed = closed; }
    public int getClosed() { return closed; }

    public double getTableFee() { return tableFee; }
    public void setTableFee(double tableFee) { this.tableFee = tableFee; }

    public double getApptFee() { return ApptFee; }
    public void setApptFee(double apptFee) { ApptFee = apptFee; }

    public double getCatFee() { return CatFee; }
    public void setCatFee(double catFee) { CatFee = catFee; }

    public double getPartyFee() { return partyFee; }
    public void setPartyFee(double partyFee) { this.partyFee = partyFee; }

    public double getDelFee() { return DelFee; }
    public void setDelFee(double delFee) { DelFee = delFee; }

    public double getResFee() { return ResFee; }
    public void setResFee(double resFee) { ResFee = resFee; }

    public int getOption() { return option; }
    public void setOption(int option) { this.option = option; }

    public int getSeekIT() { return seekIT; }
    public void setSeekIT(int seekIT) { this.seekIT = seekIT; }

    public int getUTID() { return UTID; }
    public void setUTID(int UTID) { this.UTID = UTID; }



    public Restaurant(int _id, String _name, String _hours, String _distance, String _description, double _lattiude, double _longitude, int _imageName, String _address) {
        this._id = _id;
        this._name = _name;
        this._hours = _hours;
        this._dist = _distance;
        this._description = _description;
        this._lattiude = _lattiude;
        this._longitude = _longitude;
        this._imageName = _imageName;
        this._address = _address;
    }

    public static boolean hasEmptyValue(String value) {
        if (TextUtils.isEmpty(value) || "null".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
            return true;
        }
        return false;
    }

    public static int getIntValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(_id);
        parcel.writeInt(_industryID);

        parcel.writeString(_name);
        parcel.writeString(_wp);

        parcel.writeString(_hours);
        parcel.writeString(_dist);
        parcel.writeString(_description);
        parcel.writeDouble(_lattiude);
        parcel.writeDouble(_longitude);
        parcel.writeInt(_imageName);

        parcel.writeFloat(_rating);

        parcel.writeString(_address);
        parcel.writeString(_apt);
        parcel.writeString(_ste);
        parcel.writeString(_city);
        parcel.writeString(_st);
        parcel.writeString(_zip);
        parcel.writeString(_csz);

        parcel.writeString(link);
        parcel.writeString(MenuStyle);
        parcel.writeString(WelcomeMsg);
        parcel.writeFloat(TaxRate);

        parcel.writeInt(orders);
        parcel.writeInt(party);
        parcel.writeInt(cater);
        parcel.writeInt(res);
        parcel.writeInt(del);
        parcel.writeInt(appt);
        parcel.writeInt(curb);

        parcel.writeInt(onTable);
        parcel.writeInt(pu);
        parcel.writeInt(internalDel);
        parcel.writeInt(currTakingOrders);

        parcel.writeString(MonB);
        parcel.writeString(MonE);
        parcel.writeString(TueB);
        parcel.writeString(TueE);
        parcel.writeString(WedB);
        parcel.writeString(WedE);
        parcel.writeString(ThuB);
        parcel.writeString(ThuE);
        parcel.writeString(FriB);
        parcel.writeString(FriE);
        parcel.writeString(SatB);
        parcel.writeString(SatE);
        parcel.writeString(SunB);
        parcel.writeString(SunE);

        parcel.writeInt(_ownerID);
        parcel.writeInt(closed);

        parcel.writeDouble(tableFee);
        parcel.writeDouble(partyFee);
        parcel.writeDouble(DelFee);
        parcel.writeDouble(ResFee);
        parcel.writeDouble(CatFee);
        parcel.writeDouble(ApptFee);

        parcel.writeInt(seekIT);
        parcel.writeInt(UTID);

        parcel.writeInt(option);
    }
}
