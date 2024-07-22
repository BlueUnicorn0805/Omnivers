package hawaiiappbuilders.omniversapp.utils;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.throwable;

import android.content.Context;
import android.text.Spanned;
import android.util.Log;

import androidx.core.text.HtmlCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import hawaiiappbuilders.omniversapp.KTXApplication;
import hawaiiappbuilders.omniversapp.adapters.CustomContactModel;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.ldb.Message;
import hawaiiappbuilders.omniversapp.ldb.MessageDataManager;
import hawaiiappbuilders.omniversapp.meeting.models.User;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.server.ApiUtil;

public class DataUtil {
    public static String currentTime(){
        SimpleDateFormat df = new SimpleDateFormat("H:mm a");
        return df.format(new Date());
    }
    public static String convertSECtoHMS(long secondsCount) {
        //Calculate the seconds to display:
        long seconds = secondsCount %60;
        secondsCount -= seconds;
        //Calculate the minutes:
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        //Calculate the hours:
        long hoursCount = minutesCount / 60;
        //Build the String
        String result = "";
        if(hoursCount > 0){
            result += hoursCount + "h ";
        }

        if(minutes > 0){
            result += minutes + "m ";
        }

        result += seconds + "s";
        return result;
    }

    public static String toAmountFormat(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        return formatter.format(amount);
    }

    public static String toAmountFormat(BigDecimal amount) {
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        return formatter.format(amount);
    }

    public static String formatAgeDisplay(int age) {
        String ageFormat = "";
        if (age <= 1) {
            ageFormat = age + " year old";
        } else {
            ageFormat = age + " years old";
        }
        return "(" + ageFormat + ")";
    }

    public static int getNextBirthdayInDays(String birthdate) {
        Date nowDate = new Date(Calendar.getInstance().getTimeInMillis());
        Date nextBirthdate = new Date(DataUtil.getCalendarBirthday(birthdate).getTimeInMillis());
        if (nowDate.after(nextBirthdate)) {
            Calendar birthdayNew = DataUtil.getCalendarBirthday(birthdate);
            birthdayNew.add(Calendar.YEAR, 1);
            nextBirthdate = new Date(birthdayNew.getTimeInMillis());
        }
        long differenceInDays = nextBirthdate.getTime() - nowDate.getTime();
        return (int) TimeUnit.DAYS.convert(differenceInDays, TimeUnit.MILLISECONDS);
    }

    public static boolean isYourBirthday(String birthdate) {
        Date nowDate = new Date(Calendar.getInstance().getTimeInMillis());
        Date nextBirthdate = new Date(DataUtil.getCalendarBirthday(birthdate).getTimeInMillis());
        return nowDate.equals(nextBirthdate);
    }

    public static Calendar getCalendarBirthday(String birthdate) {
        Date birthDate = DateUtil.parseDataFromFormat13(birthdate);
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.setTime(birthDate);
        // Get birthday for current year
        Calendar birthdayThisYear = Calendar.getInstance();
        birthdayThisYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        birthdayThisYear.set(Calendar.MONTH, birthdayCalendar.get(Calendar.MONTH));
        birthdayThisYear.set(Calendar.DAY_OF_MONTH, birthdayCalendar.get(Calendar.DAY_OF_MONTH));
        return birthdayThisYear;
    }

    public static String getBirthdaySummary(String birthdate) {
        Date birthDate = DateUtil.parseDataFromFormat13(birthdate);
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.setTime(birthDate);

        // Extract birthdate values
        int currYears = calculateAge(birthdate);
        int currMonths = Calendar.getInstance().get(Calendar.MONTH) - birthdayCalendar.get(Calendar.MONTH);
        int currDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - birthdayCalendar.get(Calendar.DAY_OF_MONTH);

        // Get birthday for current year
        Calendar birthdayThisYear = Calendar.getInstance();
        birthdayThisYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        birthdayThisYear.set(Calendar.MONTH, birthdayCalendar.get(Calendar.MONTH));
        birthdayThisYear.set(Calendar.DAY_OF_MONTH, birthdayCalendar.get(Calendar.DAY_OF_MONTH));

        int result = (currYears * 365) + (currMonths * 31) + currDays;
        int ageInYears;
        if (currYears != 0) {
            ageInYears = result / currYears / 12;
        } else {
            ageInYears = 0;
        }

        int ageInMonths;
        if (ageInYears != 0) {
            ageInMonths = ageInYears % 12;
        } else {
            ageInMonths = 0;
        }

        int ageInDays = ageInMonths * 31 / 30;
        return currYears + " years " + ageInMonths + " months " + ageInDays + " days";
    }

    public static int calculateAge(String birthdate) {
        Date birthDate = DateUtil.parseDataFromFormat13(birthdate);
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.setTime(birthDate);

        Calendar birthdayThisYear = Calendar.getInstance();
        birthdayThisYear.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        birthdayThisYear.set(Calendar.MONTH, birthdayCalendar.get(Calendar.MONTH));
        birthdayThisYear.set(Calendar.DAY_OF_MONTH, birthdayCalendar.get(Calendar.DAY_OF_MONTH));

        Calendar now = Calendar.getInstance();

        if (now.equals(birthdayThisYear) || now.after(birthdayThisYear)) {
            return Calendar.getInstance().get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
        } else if (birthdayThisYear.before(now)) {
            return Calendar.getInstance().get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR) - 1;
        }
        return Calendar.getInstance().get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR) - 1;
    }

    private static String getExceptionMessageChain(Throwable throwable) {
        List<String> result = new ArrayList<String>();
        while (throwable != null) {
            result.add(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return String.join(", ", result);
    }

    public static Spanned getCompanyAndName(CustomContactModel model) {
        String companyValue;
        if (model.getCompany() == null) {
            companyValue = "";
        } else {
            if (model.getCompany().isEmpty()) {
                companyValue = "";
            } else {
                int limit = 15;
                if (model.getCompany().length() > limit) {
                    companyValue = model.getCompany().trim().substring(0, limit);
                } else {
                    companyValue = model.getCompany().trim();
                }
            }
        }

        if (companyValue.length() > 0) {
            companyValue += ", ";
        }

        return HtmlCompat.fromHtml("<b>" + companyValue + "</b>" + model.name, HtmlCompat.FROM_HTML_MODE_COMPACT);
    }

    Context mContext;
    String activityName;

    public DataUtil(Context context, String activityName) {
        this.mContext = context;
        this.activityName = activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }


    public void zzzLogIt(Throwable throwable, String apiName) {
        int lineNumber = throwable.getStackTrace()[0].getLineNumber();

        String stackTrace = getExceptionMessageChain(throwable);
        BaseActivity activity = (BaseActivity) mContext;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stacktrace", stackTrace);
            jsonObject.put("apiname", apiName);
            jsonObject.put("activityname", activityName + ":" + lineNumber);
            jsonObject.put("errval", 0);
            jsonObject.put("LL", 0);
            String baseUrl = BaseFunctions.getBaseData(jsonObject, mContext,
                    "zzzLogIt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

            new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.e("zzzLogIt", "Error logged to server: " + stackTrace);
                }

                @Override
                public void onResponseError(String msg) {
                    Log.e("zzzLogIt", msg);
                }

                @Override
                public void onServerError() {

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void zzzLogIt(Throwable throwable, String apiName, int LL) {
        int lineNumber = throwable.getStackTrace()[0].getLineNumber();

        String stackTrace = getExceptionMessageChain(throwable);
        BaseActivity activity = (BaseActivity) mContext;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stacktrace", stackTrace);
            jsonObject.put("apiname", apiName);
            jsonObject.put("activityname", activityName + ":" + lineNumber);
            jsonObject.put("errval", 0);
            jsonObject.put("LL", LL);
            String baseUrl = BaseFunctions.getBaseData(jsonObject, mContext,
                    "zzzLogIt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

            new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.e("zzzLogIt", "Error logged to server: " + stackTrace);
                }

                @Override
                public void onResponseError(String msg) {
                    Log.e("zzzLogIt", msg);
                }

                @Override
                public void onServerError() {

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void zzzLogItSplash(String msg, String apiName) {

        BaseActivity activity = (BaseActivity) mContext;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stacktrace", msg);
            jsonObject.put("apiname", apiName);
            jsonObject.put("activityname", activityName);
            jsonObject.put("errval", 0);
            String baseUrl = BaseFunctions.getBaseData(jsonObject, mContext,
                    "zzzLogIt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

            new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.e("zzzLogIt", "Error logged to server: " + msg);
                }

                @Override
                public void onResponseError(String msg) {
                    Log.e("zzzLogIt", msg);
                }

                @Override
                public void onServerError() {

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void zzzLogItSplash(int LL, String msg, String apiName) {

        BaseActivity activity = (BaseActivity) mContext;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LL", LL);
            jsonObject.put("stacktrace", msg);
            jsonObject.put("apiname", apiName);
            jsonObject.put("activityname", activityName);
            jsonObject.put("errval", 0);
            String baseUrl = BaseFunctions.getBaseData(jsonObject, mContext,
                    "zzzLogIt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

            new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.e("zzzLogIt", "Error logged to server: " + msg);
                }

                @Override
                public void onResponseError(String msg) {
                    Log.e("zzzLogIt", msg);
                }

                @Override
                public void onServerError() {

                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // todo: add errval to arguments
    public void zzzLogMessage(int errVal, String message, String apiName) { // throwable / exception
        BaseActivity activity = (BaseActivity) mContext;
        JSONObject jsonObject = new JSONObject();
        Log.e("zzzLogIt", "apiii");
        try {
            jsonObject.put("stacktrace", message);
            jsonObject.put("apiname", apiName);
            jsonObject.put("activityname", activityName);
            jsonObject.put("errval", errVal);
            String baseUrl = BaseFunctions.getBaseData(jsonObject, mContext,
                    "zzzLogIt", BaseFunctions.MAIN_FOLDER, activity.getUserLat(), activity.getUserLon(), ((KTXApplication) activity.getApplication()).getAndroidId());

            Log.e("zzzLogIt", BaseFunctions.decodeBaseURL(baseUrl));

            new ApiUtil(mContext).callApi(baseUrl, new ApiUtil.OnHandleApiResponseListener() {
                @Override
                public void onSuccess(String response) {
                    Log.e("zzzLogIt", "Error logged to server: " + message);
                }

                @Override
                public void onResponseError(String msg) {

                    Log.e("zzzLogIt", "msg -> " + msg);
                }

                @Override
                public void onServerError() {

                    Log.e("zzzLogIt", "server error");
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initiateVideoCallToAcceptedNewContact(Context mContext, User user) {
        MessageDataManager dm = new MessageDataManager(mContext);
        int mlid = user.getMlid();
        String fullName = user.getFirstName() + " " + user.getLastName();
        ContactInfo newContactInfo = new ContactInfo();
        newContactInfo.setMlid(mlid);
        newContactInfo.setName(fullName);
        newContactInfo.setFname(user.getFirstName());
        newContactInfo.setLname(user.getLastName());
        newContactInfo.setCo(""); // todo: add company?
        newContactInfo.setEmail(""); // todo: add email?
        dm.addContact(newContactInfo);

        //startedVideoCallToAcceptedNewContact(mContext, user);
    }

    public static void startedVideoCallToAcceptedNewContact(Context mContext, User user) {
        MessageDataManager dm = new MessageDataManager(mContext);
        AppSettings appSettings = new AppSettings(mContext);
        int mlid = user.getMlid();
        String fullName = user.getFirstName() + " " + user.getLastName();
        Message newMsg = new Message();
        newMsg.setFromID(mlid);
        newMsg.setToID(appSettings.getUserId());
        Calendar calendar = DateUtil.getCurrentDate();
        Date currentDate = new Date(calendar.getTimeInMillis());
        newMsg.setMsg("Video Call started\n" + DateUtil.toStringFormat_37(currentDate)); // should store duration of call
        newMsg.setCreateDate(DateUtil.toStringFormat_37(new Date()));
        newMsg.setName(fullName);
        dm.addMessage(newMsg);
        dm.close();
    }

    public static void endVideoCallToAcceptedNewContact(Context mContext, User user) {
        MessageDataManager dm = new MessageDataManager(mContext);
        AppSettings appSettings = new AppSettings(mContext);
        int mlid = user.getMlid();
        String fullName = user.getFirstName() + " " + user.getLastName();
        Message newMsg = new Message();
        newMsg.setFromID(mlid);
        newMsg.setToID(appSettings.getUserId());
        Calendar calendar = DateUtil.getCurrentDate();
        Date currentDate = new Date(calendar.getTimeInMillis());
        newMsg.setMsg("Video Call ended\n" + DateUtil.toStringFormat_37(currentDate)); // should store duration of call
        newMsg.setCreateDate(DateUtil.toStringFormat_37(new Date()));
        newMsg.setName(fullName);
        dm.addMessage(newMsg);
        dm.close();
    }
}
