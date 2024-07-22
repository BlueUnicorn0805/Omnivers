package hawaiiappbuilders.omniversapp.utils;

import android.content.Context;
import android.text.TextUtils;

import hawaiiappbuilders.omniversapp.global.AppSettings;

public class PhonenumberUtils {

    AppSettings appSettings;
    Context mContext;
    public PhonenumberUtils(Context context) {
        this.mContext = context;
        appSettings = new AppSettings(context);
    }

    public boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }

        int countryLangID = appSettings.getCountryLangId();

        phone = phone.replace("(", "");
        phone = phone.replace(")", "");
        phone = phone.replace("-", "");
        phone = phone.replace("+", "");
        phone = phone.replace(".", "");
        phone = phone.replace("*", "");
        phone = phone.replace("#", "");
        phone = phone.replace(" ", "");

        int countryPhoneLength = 10;
        switch (countryLangID) {
            case 0: { // default = USA
            }
            case 1: {
            }
            case 4: { // japanese
                break;
            }
            case 2: { // chinese
            }
            case 3: { // spanish
                countryPhoneLength = 11;
                break;
            }
        }
        // 5-4-2023: Don't validate now
        return true;
    }

    public boolean validateZipCode(String zip) {
        int countryLangID = appSettings.getCountryLangId();

        boolean isValid = false;
        if (!TextUtils.isEmpty(zip)) {
            if (countryLangID == 0 || countryLangID == 1) {// default = USA
                int zipInt = Integer.parseInt(zip);
                if (zipInt > 500 && zipInt <= 99999) {
                    isValid = true;
                }
            } else {
                isValid = true;
            }
        }
        return isValid;
    }

    public static String getFilteredPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        // Japan - 81 80 9096 2366
        // PH - +639479918689 (12) or 09479918689 mobile number , landline - (032) 415 1457
        phone = phone.replace("(", "");
        phone = phone.replace(")", "");
        phone = phone.replace("-", "");
        phone = phone.replace("+", "");
        phone = phone.replace(".", "");
        phone = phone.replace("*", "");
        phone = phone.replace("#", "");
        phone = phone.replace(" ", "");

        //phoneNo = phoneNo.replaceAll("[^0-9]+", "");

        return phone;
    }

    public static String getFormattedPhoneNumber(String phone) {
        // (032) 318-5780
        // (%s) %s-%s
        String filteredPhoneNumber = getFilteredPhoneNumber(phone);
        if (filteredPhoneNumber.length() == 10) {
            return String.format("(%s) %s-%s", filteredPhoneNumber.substring(0, 3), filteredPhoneNumber.substring(3, 6), filteredPhoneNumber.substring(6));
        } else {
            return phone;
        }
    }
}
