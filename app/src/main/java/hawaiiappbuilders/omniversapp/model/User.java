package hawaiiappbuilders.omniversapp.model;

import java.io.Serializable;

public class User implements Serializable {

    private String Verified, WeStoppedAcct, OwnerStoppedAcct, FN, LN, Initial, zip, DOB, Street, StreetNum, Apt, City, st, WP, CP, Email;
    private int userID;
    private Float UTC;

    private String PIN, DepartID, DepartName, AccessLevel, IndustryID;
    private long EmpId;
    private int WorkID;

    private String gender;
    private String marital;

    public User() {
    }

    public String getVerified() {
        return Verified;
    }

    public void setVerified(String verified) {
        Verified = verified;
    }

    public String getWeStoppedAcct() {
        return WeStoppedAcct;
    }

    public void setWeStoppedAcct(String weStoppedAcct) {
        WeStoppedAcct = weStoppedAcct;
    }

    public String getOwnerStoppedAcct() {
        return OwnerStoppedAcct;
    }

    public void setOwnerStoppedAcct(String ownerStoppedAcct) {
        OwnerStoppedAcct = ownerStoppedAcct;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFN() {
        return FN;
    }

    public void setFN(String FN) {
        this.FN = FN;
    }

    public String getLN() {
        return LN;
    }

    public void setLN(String LN) {
        this.LN = LN;
    }

    public String getInitial() {
        return Initial;
    }

    public void setInitial(String initial) {
        Initial = initial;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getStreetNum() {
        return StreetNum;
    }

    public void setStreetNum(String streetNum) {
        StreetNum = streetNum;
    }

    public String getApt() {
        return Apt;
    }

    public void setApt(String apt) {
        Apt = apt;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getWP() {
        return WP;
    }

    public void setWP(String WP) {
        this.WP = WP;
    }

    public String getCP() {
        return CP;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String pin) {
        PIN = pin;
    }

    public long getEmpId() {
        return EmpId;
    }

    public void setEmpId(long empId) {
        EmpId = empId;
    }

    public String getDepartID() {
        return DepartID;
    }

    public void setDepartID(String departID) {
        DepartID = departID;
    }

    public String getDepartName() {
        return DepartName;
    }

    public void setDepartName(String departName) {
        DepartName = departName;
    }


    public String getAccessLevel() {
        return AccessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        AccessLevel = accessLevel;
    }

    public int getWorkID() {
        return WorkID;
    }

    public void setWorkID(int workID) {
        WorkID = workID;
    }

    public String getIndustryID() {
        return IndustryID;
    }

    public void setIndustryID(String industryID) {
        IndustryID = industryID;
    }

    private String DriverMLID;
    private String DriverID;
    private String Token;

    public String getDriverMLID() {
        return DriverMLID;
    }

    public void setDriverMLID(String driverMLId) {
        DriverMLID = driverMLId;
    }

    public String getDriverID() {
        return DriverID;
    }

    public void setDriverID(String driverId) {
        DriverID = driverId;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String toString() {
        String fullName = FN + " " + LN;
        return fullName.trim();
    }

    public Float getUTC() {
        return UTC;
    }

    public void setUTC(String utc) {
        UTC = Float.parseFloat(utc);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMarital() {
        return marital;
    }

    public void setMarital(String marital) {
        this.marital = marital;
    }
}
