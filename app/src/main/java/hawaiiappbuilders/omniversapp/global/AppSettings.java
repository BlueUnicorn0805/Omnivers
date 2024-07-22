package hawaiiappbuilders.omniversapp.global;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.security.keystore.KeyGenParameterSpec;
import android.text.TextUtils;

import androidx.security.crypto.MasterKeys;

import java.util.ArrayList;

public class AppSettings {
    private static final String APP_SHARED_PREFS = "eappbuilder_Omni_prefs";
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private static final String LOGGED_IN = "logged_in";
    private static final String DEVICE_ID = "device_id";
    private static final String DEVICE_ID_SET = "device_id_set";
    private static final String DEVICE_TOKEN = "device_token";
    private String USER_ID = "userid";

    private static final String ADMIN_ALEV = "admin_alev";

    private String LOGIN_CID = "login_cid";
    private String ADMIN_PIN = "admin_pin";

    private String FN = "FN";
    private String LAT = "LAT";
    private String LNG = "LNG";


    private static final String VIDEO_URL = "videoURL";

    private static final String KEY1 = "key1";

    private static final String PIN = "pin";
    private static final String EMPID = "empid";
    private static final String DEPARTID = "departid";
    private static final String DEPARTNAME = "departname";
    private static final String LEVID = "levid";
    private static final String WORKID = "workID";
    private static final String INDUSTRYID = "industryid";

    private static final String VALET_ORDER = "valetorder";
    private static final String VALET_STORE = "valetstore";

    private static final String UTC_SETTING = "UTC";

    private static final String LOGIN_INPUT = "login_input";
    private static final String LEVNAME = "levname";
    private static final String GET_TOKEN_RETRY = "getTokenRetry";

    private static final String STORELON = "StoreLon";
    private static final String STORELAT = "StoreLat";

    KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;

    public AppSettings(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        /*try {
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            this.appSharedPrefs = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    mainKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            this.prefsEditor = appSharedPrefs.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public String getString(String key) {
        return appSharedPrefs.getString(key, "");
    }

    public int getInt(String key) {
        return appSharedPrefs.getInt(key, 0);
    }

    public long getLong(String key) {
        return appSharedPrefs.getLong(key, 0);
    }

    public boolean getBoolean(String key) {
        return appSharedPrefs.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public void putInt(String key, int value) {
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public void putLong(String key, long value) {
        prefsEditor.putLong(key, value);
        prefsEditor.commit();
    }

    public void putBoolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public void remove(String key) {
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public void clear() {
        /*prefsEditor.clear();
        prefsEditor.commit();*/

        setUserId(0);
        setFN("");
        setLN("");
        setEmail("");
        setEmpId(0);
        setWorkid(0);

        setShowedIntroZintaPay(false);

        logOut();
    }

    public void setLoginInput(String loginInput) {
        prefsEditor.putString(LOGIN_INPUT, loginInput);
        prefsEditor.commit();
    }

    public String getLoginInput() {
        return appSharedPrefs.getString(LOGIN_INPUT, "");
    }

    public int getUserId() {
        int userID = appSharedPrefs.getInt(USER_ID, 0);
        return userID;
    }

    public void setUserId(int userId) {
        prefsEditor.putInt(USER_ID, userId);
        prefsEditor.commit();
    }


    public int getTokenRetry() {
        return appSharedPrefs.getInt(GET_TOKEN_RETRY, 0);
    }

    public void setTokenRetry(int retry) {
        prefsEditor.putInt(GET_TOKEN_RETRY, retry);
        prefsEditor.commit();
    }


    public String getKey1() {
        return appSharedPrefs.getString(KEY1, "");
    }

    public void setKey1(String key) {
        prefsEditor.putString(KEY1, key);
        prefsEditor.commit();
    }


    public String getFN() {
        return appSharedPrefs.getString(FN, "");
    }

    public void setFN(String userId) {
        prefsEditor.putString(FN, userId);
        prefsEditor.commit();
    }

    public void setDeviceIdSet(boolean isAdded) {
        prefsEditor.putBoolean(DEVICE_ID_SET, isAdded);
        prefsEditor.apply();
    }

    public void setStoreLon(String value) {
        prefsEditor.putString(STORELON, value);
        prefsEditor.apply();
    }

    public String getStoreLon() {
        return appSharedPrefs.getString(STORELON, "0");
    }


    public boolean isDeviceIdSet() {
        return appSharedPrefs.getBoolean(DEVICE_ID_SET, false);
    }

    public void setDeviceId(String deviceId) {
        /*if (!isDeviceIdSet()) {
            setDeviceIdSet(true);
            prefsEditor.putString(DEVICE_ID,deviceId);
            prefsEditor.apply();
        }*/
        prefsEditor.putString(DEVICE_ID, deviceId);
        prefsEditor.apply();
    }

    public String getDeviceId() {
        return appSharedPrefs.getString(DEVICE_ID, "");
    }

    public void setLoggedIn() {
        prefsEditor.putBoolean(LOGGED_IN, true);
        prefsEditor.apply();
    }

    public boolean isLoggedIn() {
        return appSharedPrefs.getBoolean(LOGGED_IN, false);
    }

    public boolean logOut() {
        prefsEditor.putBoolean(LOGGED_IN, false);
        return prefsEditor.commit();
    }

    public void setPIN(String pin) {
        prefsEditor.putString(PIN, pin);
        prefsEditor.apply();
    }

    public String getPIN() {
        return appSharedPrefs.getString(PIN, "");
    }

    public void setVideoUrl(String videoUrl) {
        prefsEditor.putString(VIDEO_URL, videoUrl);
        prefsEditor.apply();
    }

    public String getVideoUrl() {
        return appSharedPrefs.getString(VIDEO_URL, "");
    }


    private static final String HOME_ADDRESS = "homeAddress";

    public void setHomeAddress(String homeAddress) {
        prefsEditor.putString(HOME_ADDRESS, homeAddress);
        prefsEditor.apply();
    }

    public String getHomeAddress() {
        return appSharedPrefs.getString(HOME_ADDRESS, "");
    }


    private static final String WORK_ADDRESS = "workAddress";

    public void setWorkAddress(String workAddress) {
        prefsEditor.putString(WORK_ADDRESS, workAddress);
        prefsEditor.apply();
    }

    public String getWorkAddress() {
        return appSharedPrefs.getString(WORK_ADDRESS, "");
    }


    // EmpId
    public void setEmpId(long empId) {
        prefsEditor.putLong(EMPID, empId);
        prefsEditor.apply();
    }

    public long getEmpId() {
        return appSharedPrefs.getLong(EMPID, 0);
    }

    // DepartId
    public void setDepartId(String departId) {
        prefsEditor.putString(DEPARTID, departId);
        prefsEditor.apply();
    }

    public String getDepartId() {
        return appSharedPrefs.getString(DEPARTID, "");
    }

    // DepartName
    public void setDepartName(String departname) {
        prefsEditor.putString(DEPARTNAME, departname);
        prefsEditor.apply();
    }

    public String getDepartName() {
        return appSharedPrefs.getString(DEPARTNAME, "");
    }

    // Country Code
    private static final String COUNTRY_CODE = "countryCode";

    public void setCountryCode(String countryCode) {
        prefsEditor.putString(COUNTRY_CODE, countryCode);
        prefsEditor.apply();
    }

    public String getCountryCode() {
        return appSharedPrefs.getString(COUNTRY_CODE, "");
    }


    // LevId
    public void setALev(String levId) {
        prefsEditor.putString(LEVID, levId);
        prefsEditor.apply();
    }

    public void setAdminPIN(String levId) {
        prefsEditor.putString(ADMIN_PIN, levId);
        prefsEditor.apply();
    }

    public String getAdminPIN() {
        return appSharedPrefs.getString(ADMIN_PIN, "");
    }

    public void setALevName(String levName) {
        prefsEditor.putString(LEVNAME, levName);
        prefsEditor.apply();
    }

    public String getALevName() {
        return appSharedPrefs.getString(LEVNAME, "");
    }


    public String getALev() {
        return appSharedPrefs.getString(LEVID, "");
    }

    public void setAdminAlev(int adminAlev) {
        prefsEditor.putInt(ADMIN_ALEV, adminAlev);
        prefsEditor.apply();
    }

    public int getAdminAlev() {
        return appSharedPrefs.getInt(ADMIN_ALEV, 0);
    }

    // WORKID
    public void setWorkid(int workId) {
        prefsEditor.putInt(WORKID, workId);
        prefsEditor.apply();
    }

    public int getWorkid() {
        return appSharedPrefs.getInt(WORKID, 0);
    }

    private static final String STE = "ste";

    public void setSTE(String ste) {
        prefsEditor.putString(STE, ste);
        prefsEditor.apply();
    }

    public String getSTE() {
        return appSharedPrefs.getString(STE, "");
    }


    public void setStoreLat(String value) {
        prefsEditor.putString(STORELAT, value);
        prefsEditor.apply();
    }

    public String getStoreLat() {
        return appSharedPrefs.getString(STORELAT, "0");
    }


    public void setLoginCID(int loginCid) {
        prefsEditor.putInt(LOGIN_CID, loginCid);
        prefsEditor.commit();
    }

    public int getLoginCID() {
        int loginCID = appSharedPrefs.getInt(LOGIN_CID, 0);
        setCID(loginCID);
        return loginCID;
    }


    public void setCID(int userId) {
        prefsEditor.putInt(USER_ID, userId);
        prefsEditor.commit();
    }

    // IndustryID
    public void setIndustryid(String industryid) {
        prefsEditor.putString(INDUSTRYID, industryid);
        prefsEditor.apply();
    }

    public String getIndustryid() {
        return appSharedPrefs.getString(INDUSTRYID, "");
    }


    // UTC
    public void setUTC(Float utc) {
        prefsEditor.putFloat(UTC_SETTING, utc);
        prefsEditor.commit();
    }

    public Float getUTC() {
        return appSharedPrefs.getFloat(UTC_SETTING, 0.0F);
    }

    public String getDeviceLat() {
        return appSharedPrefs.getString(LAT, "0.0");
    }

    public String getDeviceLng() {
        return appSharedPrefs.getString(LNG, "0.0");
    }

    public void setDeviceLat(String deviceLat) {
        prefsEditor.putString(LAT, deviceLat);
        prefsEditor.apply();
    }

    public void setDeviceLng(String deviceLng) {
        prefsEditor.putString(LNG, deviceLng);
        prefsEditor.apply();
    }

    public void setDeviceToken(String deviceToken) {
        prefsEditor.putString(DEVICE_TOKEN, deviceToken);
        prefsEditor.apply();
    }

    public String getDeviceToken() {
        return appSharedPrefs.getString(DEVICE_TOKEN, "");
    }

    public String getValetOrder() {
        return appSharedPrefs.getString(VALET_ORDER, "");
    }

    public void setValetOrder(String valetOrder) {
        prefsEditor.putString(VALET_ORDER, valetOrder);
        prefsEditor.apply();
    }

    public String getValetStore() {
        return appSharedPrefs.getString(VALET_STORE, "");
    }

    public void setValetStore(String valetOrder) {
        prefsEditor.putString(VALET_STORE, valetOrder);
        prefsEditor.apply();
    }

    private String APP_ORIENTATION = "APP_ORIENTATION";

    public int getAppOrientation() {
        return appSharedPrefs.getInt(APP_ORIENTATION, 0);
    }

    public void setAppOrientation(int value) {
        prefsEditor.putInt(APP_ORIENTATION, value);
        prefsEditor.commit();
    }

    private static final String IS_CLOCKED_IN = "is_p_in";
    private static final String IN_TIME = "in_time";
    private static final String LUNCH_IN_TIME = "lunch_in_time";
    private static final String LUNCH_END_TIME = "lunch_end_time";
    private static final String REMAINING_OUT_TIME = "r_o_time";
    private static final String IS_HAVING_LUNCH = "is_h_lunch";
    private static final String IS_LUNCH_TIME_OVER = "is_lunch_t_ovver";

    public void setInTime(String inTime) {
        prefsEditor.putString(IN_TIME, inTime);
        prefsEditor.commit();
    }

    public String getInTime() {
        return appSharedPrefs.getString(IN_TIME, "");
    }

    public void setLunchInTime(String inTime) {
        prefsEditor.putString(LUNCH_IN_TIME, inTime);
        prefsEditor.commit();
    }

    public String getLunchInTime() {
        return appSharedPrefs.getString(LUNCH_IN_TIME, "");
    }

    public void setLunchEndTime(String inTime) {
        prefsEditor.putString(LUNCH_END_TIME, inTime);
        prefsEditor.commit();
    }

    public String getLunchEndTime() {
        return appSharedPrefs.getString(LUNCH_END_TIME, "");
    }

    public void setClockedIn() {
        prefsEditor.putBoolean(IS_CLOCKED_IN, true);
        prefsEditor.apply();
    }

    public void setClockedOut() {
        prefsEditor.putBoolean(IS_CLOCKED_IN, false);
        prefsEditor.apply();
    }

    public void setIsHavingLunch() {
        prefsEditor.putBoolean(IS_HAVING_LUNCH, true);
        prefsEditor.apply();
    }

    public void setCompletedHavingLunch() {
        prefsEditor.putBoolean(IS_HAVING_LUNCH, false);
        prefsEditor.apply();
    }

    public boolean isHavingLunch() {
        return appSharedPrefs.getBoolean(IS_HAVING_LUNCH, false);
    }

    public boolean isClockedIn() {
        return appSharedPrefs.getBoolean(IS_CLOCKED_IN, false);
    }

    public void setRemainingTime(long remainingTime) {
        prefsEditor.putLong(REMAINING_OUT_TIME, remainingTime);
        prefsEditor.commit();
    }

    public void resetRemainingTime() {
        prefsEditor.putLong(REMAINING_OUT_TIME, 30 * 60 * 1000);
        prefsEditor.commit();
    }

    public long getRemainingTime() {
        return appSharedPrefs.getLong(REMAINING_OUT_TIME, 30 * 60 * 1000);
    }

    public void setLunchTimeOver(boolean isLunchTimeOver) {
        prefsEditor.putBoolean(IS_LUNCH_TIME_OVER, isLunchTimeOver);
        prefsEditor.apply();
    }

    public boolean isLunchTimeOver() {
        return appSharedPrefs.getBoolean(IS_LUNCH_TIME_OVER, false);
    }

    private String LN = "LN";
    private String zip = "zip";
    private String Street = "Street";
    private String Apt = "Apt";
    private String StreetNum = "StreetNum";
    private String City = "City";
    private String St = "St";
    private String Email = "Email";
    private String Deliveries = "Deliveries";
    private String DRIVER_ID = "driverid";
    private String CP = "CP";
    private String WP = "WP";

    private String HANDLE = "HANDLE";

    private String PHONE = "Phone";
    private String ADDRESS = "Address";
    private String DOB = "DOB";

    private String APPOINTMENT = "Appointment";

    private String GENDAR = "Gendar";
    private String MARITAL = "Marital";

    // Last Update Date
    public String LAST_UPDATE = "LastUpdate_Date";

    // Company
    public String COMPANY = "COMPANY";
    public String TITLE = "TITLE";

    public String WEIGHT = "WEIGHT";
    public String HEIGHT = "HEIGHT";
    public String MEDICAID = "MEDICAID";
    public String MEDICARE = "MEDICARE";
    public String LANGUAGE = "LANGUAGE";
    public String RACE = "RACE";

    // Social Channels
    public String YOUTUBE = "YOUTUBE";
    public String FACEBOOK = "FACEBOOK";
    public String TWITTER = "TWITTER";
    public String LINKEDIN = "LINKEDIN";
    public String PINTREST = "PINTREST";
    public String SNAPCHAT = "SNAPCHAT";
    public String INSTAGRAM = "INSTAGRAM";
    public String WHATSAPP = "WHATSAPP";

    public String getLN() {
        return appSharedPrefs.getString(LN, "");
    }

    public void setLN(String userId) {
        prefsEditor.putString(LN, userId);
        prefsEditor.commit();
    }

    public String getZip() {
        return appSharedPrefs.getString(zip, "");
    }

    public void setZip(String _zip) {
        prefsEditor.putString(zip, _zip);
        prefsEditor.commit();
    }

    public String getStreet() {
        return appSharedPrefs.getString(Street, "");
    }

    public void setStreet(String street) {
        prefsEditor.putString(Street, street);
        prefsEditor.commit();
    }

    public String getApt() {
        return appSharedPrefs.getString(Apt, "");
    }

    public void setApt(String apt) {
        prefsEditor.putString(Apt, apt);
        prefsEditor.commit();
    }

    public String getStreetNum() {
        return appSharedPrefs.getString(StreetNum, "");
    }

    public void setStreetNum(String streetNum) {
        prefsEditor.putString(StreetNum, streetNum);
        prefsEditor.commit();
    }

    public String getCity() {
        return appSharedPrefs.getString(City, "");
    }

    public void setCity(String city) {
        prefsEditor.putString(City, city);
        prefsEditor.commit();
    }

    public String getSt() {
        return appSharedPrefs.getString(St, "");
    }

    public void setSt(String _st) {
        prefsEditor.putString(St, _st);
        prefsEditor.commit();
    }

    public String getEmail() {
        return appSharedPrefs.getString(Email, "");
    }

    public void setEmail(String _Email) {
        prefsEditor.putString(Email, _Email);
        prefsEditor.commit();
    }

    public String getCP() {
        return appSharedPrefs.getString(CP, "");
    }

    public void setCP(String _CP) {
        prefsEditor.putString(CP, _CP);
        prefsEditor.commit();
    }

    public String getWP() {
        return appSharedPrefs.getString(WP, "");
    }

    public void setWP(String _WP) {
        prefsEditor.putString(WP, _WP);
        prefsEditor.commit();
    }

    public String getHandle() {
        return appSharedPrefs.getString(HANDLE, "");
    }

    public void setHandle(String _handle) {
        prefsEditor.putString(HANDLE, _handle);
        prefsEditor.commit();
    }

    public String getPhone() {
        return appSharedPrefs.getString(PHONE, "");
    }

    public void setPhone(String phone) {
        prefsEditor.putString(PHONE, phone);
        prefsEditor.commit();
    }

    public String getAddress() {
        return appSharedPrefs.getString(ADDRESS, "");
    }

    public void setAddress(String phone) {
        prefsEditor.putString(ADDRESS, phone);
        prefsEditor.commit();
    }

    public String getDOB() {
        return appSharedPrefs.getString(DOB, "");
    }

    public void setDOB(String phone) {
        prefsEditor.putString(DOB, phone);
        prefsEditor.commit();
    }

    public String getGendar() {
        return appSharedPrefs.getString(GENDAR, "");
    }

    public void setGendar(String gendar) {
        prefsEditor.putString(GENDAR, gendar);
        prefsEditor.commit();
    }

    public String getMarital() {
        return appSharedPrefs.getString(MARITAL, "");
    }

    public void setMarital(String marital) {
        prefsEditor.putString(MARITAL, marital);
        prefsEditor.commit();
    }

    // ------------------------------------------ Company ---------------------------------------------------
    public String getCompany() {
        return appSharedPrefs.getString(COMPANY, "");
    }

    public void setCompany(String value) {
        prefsEditor.putString(COMPANY, value);
        prefsEditor.commit();
    }

    public String getTitle() {
        return appSharedPrefs.getString(TITLE, "");
    }

    public void setTitle(String value) {
        prefsEditor.putString(TITLE, value);
        prefsEditor.commit();
    }

    public String getMedicaid() {
        return appSharedPrefs.getString(MEDICAID, "");
    }

    public void setMedicaid(String value) {
        prefsEditor.putString(MEDICAID, value);
        prefsEditor.commit();
    }

    public String getMedicare() {
        return appSharedPrefs.getString(MEDICARE, "");
    }

    public void setMedicare(String value) {
        prefsEditor.putString(MEDICARE, value);
        prefsEditor.commit();
    }

    public float getWeight() {
        return appSharedPrefs.getFloat(WEIGHT, 0);
    }

    public void setWeight(float weight) {
        prefsEditor.putFloat(WEIGHT, weight);
        prefsEditor.commit();
    }

    public float getHeight() {
        return appSharedPrefs.getFloat(HEIGHT, 0);
    }

    public void setHeight(float height) {
        prefsEditor.putFloat(HEIGHT, height);
        prefsEditor.commit();
    }
    public String getLanguage() {
        return appSharedPrefs.getString(LANGUAGE, "");
    }

    public void setLanguage(String value) {
        prefsEditor.putString(LANGUAGE, value);
        prefsEditor.commit();
    }

    public String getRace() {
        return appSharedPrefs.getString(RACE, "");
    }

    public void setRace(String value) {
        prefsEditor.putString(RACE, value);
        prefsEditor.commit();
    }


    // ---------------------------------------- Social Media -------------------------------------------------
    public String getYoutube() {
        return appSharedPrefs.getString(YOUTUBE, "");
    }

    public void setYoutube(String value) {
        prefsEditor.putString(YOUTUBE, value);
        prefsEditor.commit();
    }

    public String getFacebook() {
        return appSharedPrefs.getString(FACEBOOK, "");
    }

    public void setFacebook(String value) {
        prefsEditor.putString(FACEBOOK, value);
        prefsEditor.commit();
    }

    public String getTwitter() {
        return appSharedPrefs.getString(TWITTER, "");
    }

    public void setTwitter(String value) {
        prefsEditor.putString(TWITTER, value);
        prefsEditor.commit();
    }

    public static final String FAMILY_MEMBER_SETTINGS = "memberSettings";

    public String getMemberSettings() {
        return appSharedPrefs.getString(FAMILY_MEMBER_SETTINGS, "");
    }

    public void setMemberSettings(String allSettings) {
        prefsEditor.putString(FAMILY_MEMBER_SETTINGS, allSettings);
        prefsEditor.commit();
    }

    public static final String USER_FAMILY_ID = "familyUserId";

    public long getFamilyUserId() {
        return appSharedPrefs.getLong(USER_FAMILY_ID, 0);
    }

    public void setFamilyUserId(long id) {
        prefsEditor.putLong(USER_FAMILY_ID, id);
        prefsEditor.commit();
    }

    public String getLinkedIn() {
        return appSharedPrefs.getString(LINKEDIN, "");
    }

    public void setLinkedIn(String value) {
        prefsEditor.putString(LINKEDIN, value);
        prefsEditor.commit();
    }

    public String getPintrest() {
        return appSharedPrefs.getString(PINTREST, "");
    }

    public void setPintrest(String value) {
        prefsEditor.putString(PINTREST, value);
        prefsEditor.commit();
    }

    public String getSnapchat() {
        return appSharedPrefs.getString(SNAPCHAT, "");
    }

    public void setSnapchat(String value) {
        prefsEditor.putString(SNAPCHAT, value);
        prefsEditor.commit();
    }

    public String getInstagram() {
        return appSharedPrefs.getString(INSTAGRAM, "");
    }

    public void setInstagram(String value) {
        prefsEditor.putString(INSTAGRAM, value);
        prefsEditor.commit();
    }

    public String getWhatsApp() {
        return appSharedPrefs.getString(WHATSAPP, "");
    }

    public void setWhatsApp(String value) {
        prefsEditor.putString(WHATSAPP, value);
        prefsEditor.commit();
    }
    // --------------------------------------------------------------------------------------------------------

    public String getDriverID() {
        return appSharedPrefs.getString(DRIVER_ID, "");
    }

    public void setDriverID(String _driverId) {
        prefsEditor.putString(DRIVER_ID, _driverId);
        prefsEditor.commit();
    }

    public ArrayList<String> getMyDeliveries() {

        String deliveriesArchives = appSharedPrefs.getString(Deliveries, "");
        if (TextUtils.isEmpty(deliveriesArchives)) {
            return null;
        } else {
            String[] deliveriIDs = deliveriesArchives.split("_");
            if (deliveriIDs != null && deliveriIDs.length > 0) {
                ArrayList<String> deliveriIDList = new ArrayList<>();
                for (String delID : deliveriIDs) {
                    deliveriIDList.add(delID);
                }
                return deliveriIDList;
            } else {
                return null;
            }
        }
    }

    public void addMyDelivery(String newDel) {
        String deliveries = appSharedPrefs.getString(Deliveries, "");
        deliveries += "_" + newDel;

        prefsEditor.putString(Deliveries, deliveries);
        prefsEditor.commit();
    }

    // Save Appointment Data
    public void setAppointmentData(String dumpData) {
        prefsEditor.putString(APPOINTMENT, dumpData);
        prefsEditor.commit();
    }

    // Restore Appointment Data
    public String getAppointmentData() {
        return appSharedPrefs.getString(APPOINTMENT, "");
    }

    // Save Last Update Date
    public void setLastUpdateDate(String dumpData) {
        prefsEditor.putString(LAST_UPDATE, dumpData);
        prefsEditor.commit();
    }

    // Restore Update Date
    public String getLastUpdateDate() {
        return appSharedPrefs.getString(LAST_UPDATE, "");
    }

    // Last Update Date
    public String LAST_LOCATION_LAT = "LASTLOCATIONLAT";
    public String LAST_LOCATION_LON = "LASTLOCATIONLON";

    public String IS_APP_KILLED = "IS_APP_KILLED";

    public void setIsAppKilled(boolean value) {
        prefsEditor.putBoolean(IS_APP_KILLED, value);
        prefsEditor.commit();
    }

    public boolean isAppKilled() {
        return appSharedPrefs.getBoolean(IS_APP_KILLED, true);
    }

    public static final String COUNTRY_LANG_ID = "countryLangID";

    public int getCountryLangId() {
        return appSharedPrefs.getInt(COUNTRY_LANG_ID, 0);
    }

    public void setCountryLangId(int countryLangId) {
        prefsEditor.putInt(COUNTRY_LANG_ID, countryLangId);
        prefsEditor.commit();
    }


    public static final String SCANCHECKID = "scanCheckID";

    public Integer getScanCheckId() {
        return appSharedPrefs.getInt(SCANCHECKID, 0);
    }

    public void setScanCheckId(int scanCheckId) {
        prefsEditor.putInt(SCANCHECKID, scanCheckId);
        prefsEditor.commit();
    }

    public static final String COUNTRY_LANG_CODE = "countryLangCode";

    public String getCountryLangCode() {
        return appSharedPrefs.getString(COUNTRY_LANG_CODE, "");
    }

    public void setCountryLangCode(String countryLangCode) {
        prefsEditor.putString(COUNTRY_LANG_CODE, countryLangCode);
        prefsEditor.commit();
    }

    // Last Location
    public void setLastLocationLat(String value) {
        prefsEditor.putString(LAST_LOCATION_LAT, value);
        prefsEditor.commit();
    }

    public String getLastLocationLat() {
        return appSharedPrefs.getString(LAST_LOCATION_LAT, "0");
    }

    public void setLastLocationLon(String value) {
        prefsEditor.putString(LAST_LOCATION_LON, value);
        prefsEditor.commit();
    }

    public String getLastLocationLon() {
        return appSharedPrefs.getString(LAST_LOCATION_LON, "0");
    }

    // Menu Options
    public String OPTION_MENU = "OPTION_MENU";

    public void setOptionMenu(int value) {
        prefsEditor.putInt(OPTION_MENU, value);
        prefsEditor.commit();
    }

    public int getOptionMenu() {
        return appSharedPrefs.getInt(OPTION_MENU, 0);
    }

    // Menu Options
    public String AVATAR_IMAGE = "AVATAR_IMAGE";

    public void setAvatarImage(String value) {
        prefsEditor.putString(AVATAR_IMAGE, value);
        prefsEditor.commit();
    }

    public String getAvatarImage() {
        return appSharedPrefs.getString(AVATAR_IMAGE, "");
    }


    public String CURR_STATUS_ID = "statusIdCurrent";

    public void setCurrStatusId(int statusId) {
        prefsEditor.putInt(CURR_STATUS_ID, statusId);
        prefsEditor.commit();
    }

    public int getCurrStatusId() {
        return appSharedPrefs.getInt(CURR_STATUS_ID, 0);
    }

    public String CURR_STATUS_ID_DATE = "statusIdCurrentDate";

    public void setCurrStatusIdDate(String date) {
        prefsEditor.putString(CURR_STATUS_ID_DATE, date);
        prefsEditor.commit();
    }

    public String getCurrStatusIdDate() {
        return appSharedPrefs.getString(CURR_STATUS_ID_DATE, "");
    }

    public String SHOW_AVATAR = "SHOW_AVATAR";

    public void setShowAvatar(boolean value) {
        prefsEditor.putBoolean(SHOW_AVATAR, value);
        prefsEditor.commit();
    }

    public boolean isShowAvatar() {
        return appSharedPrefs.getBoolean(SHOW_AVATAR, true);
    }

    public String IMAGE_OPTION = "IMAGE_OPTION";

    public void setImageOption(int value) {
        prefsEditor.putInt(IMAGE_OPTION, value);
        prefsEditor.commit();
    }

    public int getImageOption() {
        return appSharedPrefs.getInt(IMAGE_OPTION, 0);
    }

    // Related with Delivery
    private String DELIVERY_INFO = "delivery_info";

    public String getDeliveryInfo() {
        return appSharedPrefs.getString(DELIVERY_INFO, "");
    }

    public void setDeliveryInfo(String _deliveryInfo) {
        prefsEditor.putString(DELIVERY_INFO, _deliveryInfo);
        prefsEditor.commit();
    }

    // New Delivery ID
    private String NEW_DEL_ID = "New_Del_ID";

    public void setNewDelID(int val) {
        prefsEditor.putInt(NEW_DEL_ID, val);
        prefsEditor.apply();
    }

    public int getNewDelID() {
        return appSharedPrefs.getInt(NEW_DEL_ID, 0);
    }

    private String ORDERID = "ORDERID";

    public String getOrderID() {
        return appSharedPrefs.getString(ORDERID, "");
    }

    public void setOrderID(String value) {
        prefsEditor.putString(ORDERID, value);
        prefsEditor.commit();
    }

    private String ORDERDUEDATE = "ORDERDUEDATE";

    public String getOrderDueDate() {
        return appSharedPrefs.getString(ORDERDUEDATE, "");
    }

    public void setOrderDueDate(String value) {
        prefsEditor.putString(ORDERDUEDATE, value);
        prefsEditor.commit();
    }

    private String ORDERSTATUS = "ORDERSTATUS";

    public int getOrderStatus() {
        return appSharedPrefs.getInt(ORDERSTATUS, 0);
    }

    public void setOrderStatus(int value) {
        prefsEditor.putInt(ORDERSTATUS, value);
        prefsEditor.commit();
    }

    // -1   : Deny
    // 0    : Unknow
    // 1    : Accepted
    private String LOCATIONPERMISSION = "LOCATIONPERMISSION";

    public int getLocationPermission() {
        return appSharedPrefs.getInt(LOCATIONPERMISSION, 0);
    }

    public void setLocationPermission(int value) {
        prefsEditor.putInt(LOCATIONPERMISSION, value);
        prefsEditor.commit();
    }

    // 0= not seen anything and not setup
    // 1= not set account & routing
    // 2= needs to verify
    // 3= they are verified so stay on tab(1)
    private String TRANSMONEY_STATUS = "TRANSMONEY_STATUS";

    public int getTransMoneyStatus() {
        return appSharedPrefs.getInt(TRANSMONEY_STATUS, 0);
    }

    public void setTransMoneyStatus(int value) {
        prefsEditor.putInt(TRANSMONEY_STATUS, value);
        prefsEditor.commit();
    }

    private String APPT_ALRAMDATA = "APPT_ALRAMDATA";

    public String getApptAlarmData() {
        return appSharedPrefs.getString(APPT_ALRAMDATA, "");
    }

    public void setApptAlaramData(String value) {
        prefsEditor.putString(APPT_ALRAMDATA, value);
        prefsEditor.commit();
    }

    private String DEL_INPUTS = "DEL_INPUTS";

    public String getDelInputs() {
        return appSharedPrefs.getString(DEL_INPUTS, "");
    }

    public void setDelInputs(String value) {
        prefsEditor.putString(DEL_INPUTS, value);
        prefsEditor.commit();
    }

    private String DELSPORTS_INPUTS = "DELSPORTS_INPUTS";

    public String getDelSportsInputs() {
        return appSharedPrefs.getString(DELSPORTS_INPUTS, "");
    }

    public void setDelSportsInputs(String value) {
        prefsEditor.putString(DELSPORTS_INPUTS, value);
        prefsEditor.commit();
    }

    private String CATER_INPUTS = "CATER_INPUTS";

    public String getCaterInputs() {
        return appSharedPrefs.getString(CATER_INPUTS, "");
    }

    public void setCaterInputs(String value) {
        prefsEditor.putString(CATER_INPUTS, value);
        prefsEditor.commit();
    }

    private String PARTY_INPUTS = "PARTY_INPUTS";

    public String getPartyInputs() {
        return appSharedPrefs.getString(PARTY_INPUTS, "");
    }

    public void setPartyInputs(String value) {
        prefsEditor.putString(PARTY_INPUTS, value);
        prefsEditor.commit();
    }

    private static final String SHOWED_INTRO_ZINTAPAY = "showed_intro_zintapay";

    public boolean isShowedIntroZintaPay() {
        return appSharedPrefs.getBoolean(SHOWED_INTRO_ZINTAPAY, false);
    }

    public void setShowedIntroZintaPay(boolean value) {
        prefsEditor.putBoolean(SHOWED_INTRO_ZINTAPAY, value);
        prefsEditor.commit();
    }

    private static final String BIOMETRICS_AUTH_STATUS = "biometrics_auth_status";

    // -1: Not determined, 0: USE Function, 1: Not USE birometric auth
    public int getBiometricAuthUseStatus() {
        return appSharedPrefs.getInt(BIOMETRICS_AUTH_STATUS, -1);
    }

    public void setBiometricAuthStatus(int value) {
        prefsEditor.putInt(BIOMETRICS_AUTH_STATUS, value);
        prefsEditor.commit();
    }

    private static final String TEMPERATURE_UNIT_STATUS = "temp_unit_status";

    // 0: USE Fahrenheit, 1: Celsius
    public int getTemperatureUnitStatus() {
        return appSharedPrefs.getInt(TEMPERATURE_UNIT_STATUS, 0);
    }

    public void settemperatureUnitStatus(int value) {
        prefsEditor.putInt(TEMPERATURE_UNIT_STATUS, value);
        prefsEditor.commit();
    }

    private static final String TEMPERATURE_LAST_VALUE = "temp_last_value";

    public int getTemperatureLastValue() {
        return appSharedPrefs.getInt(TEMPERATURE_LAST_VALUE, 0);
    }

    public void setTemperatureLastValue(int value) {
        prefsEditor.putInt(TEMPERATURE_LAST_VALUE, value);
        prefsEditor.commit();
    }

    private static final String TEMPERATURE_LAST_STRING = "temp_last_string";

    public String getTemperatureLastString() {
        return appSharedPrefs.getString(TEMPERATURE_LAST_STRING, "");
    }

    public void setTemperatureLastString(String value) {
        prefsEditor.putString(TEMPERATURE_LAST_STRING, value);
        prefsEditor.commit();
    }

    private static final String CATEGORY_LISTDATA = "category_list_Data";

    public String getCategoryListData() {
        return appSharedPrefs.getString(CATEGORY_LISTDATA, "");
    }

    public void setCategoryListData(String value) {
        prefsEditor.putString(CATEGORY_LISTDATA, value);
        prefsEditor.commit();
    }

    // Pickup Populate Info
    private static final String POPULATE_PICK_UP = "populate_pick_up";

    public String getPopulatePickUp() {
        return appSharedPrefs.getString(POPULATE_PICK_UP, "");
    }

    public void setPopulatePickUp(String value) {
        prefsEditor.putString(POPULATE_PICK_UP, value);
        prefsEditor.commit();
    }

    // Appointment Populate Info
    private static final String POPULATE_APPT = "populate_appt";

    public String getPopulateAppt() {
        return appSharedPrefs.getString(POPULATE_APPT, "");
    }

    public void setPopulateAppt(String value) {
        prefsEditor.putString(POPULATE_APPT, value);
        prefsEditor.commit();
    }
}