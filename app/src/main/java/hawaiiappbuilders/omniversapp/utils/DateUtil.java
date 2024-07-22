package hawaiiappbuilders.omniversapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static final int HOUR_IN_SECONDS = 3600;
    private static final int HOUR_IN_MINUTES = 60;
    public static final String DATE_FORMAT_1 = "MM/dd/yyyy";
    public static final String DATE_FORMAT_2 = "MM/dd/yyyy HH:mm";
    public static final String DATE_FORMAT_3 = "MMM./dd/yyyy";
    public static final String DATE_FORMAT_4 = "dd/MM/yyyy";
    public static final String DATE_FORMAT_5 = "dd-MMM-yyyy";
    public static final String DATE_FORMAT_6 = "MMM-yyyy";
    public static final String DATE_FORMAT_7 = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_8 = "EEE, MMM d, h:mm a";    // Tue, Nov 24, 10:58 AM
    public static final String DATE_FORMAT_9 = "EEE, d MMM yyyy";        // Thu, 15 Apr 2015
    public static final String DATE_FORMAT_10 = "hh:mm a";                // 08:00 AM
    public static final String DATE_FORMAT_11 = "dd.MM.yyyy";
    public static final String DATE_FORMAT_12 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_13 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_14 = "yyyy/MM/dd";
    public static final String DATE_FORMAT_15 = "MM/dd/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_16 = "yyyyMMddhhmmss";
    public static final String DATE_FORMAT_17 = "MM-dd-yyyy HH:mm";
    public static final String DATE_FORMAT_18 = "MM-dd h:mm a";
    public static final String DATE_FORMAT_19 = "MMM dd h:mm a";        // Aug 21 11:15 AM,
    public static final String DATE_FORMAT_20 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_21 = "EEE MM-dd h:mm a";        // Aug 21 11:15 AM,
    public static final String DATE_FORMAT_22 = "MM-dd-yyyy";
    public static final String DATE_FORMAT_23 = "HH:mm";        // Aug 21 11:15 AM,
    public static final String DATE_FORMAT_24 = "yyyy-MM-dd'T'HH:mm:ss.SSS";        // Aug 21 11:15 AM,
    public static final String DATE_FORMAT_25 = "MM-dd";        // 08-21
    public static final String DATE_FORMAT_26 = "MMddYY";        // 08-21
    public static final String DATE_FORMAT_27 = "EEEE, MMMM dd, yyyy";        // Thursday, August 5, 2021
    public static final String DATE_FORMAT_28 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_29 = "EEE, MMM d, yyyy";        // Thu, 15 Apr 2015
    public static final String DATE_FORMAT_30 = "h a";        // Thu, 15 Apr 2015
    public static final String DATE_FORMAT_31 = "MMMM dd, yyyy";        // August 5, 2021
    public static final String DATE_FORMAT_32 = "MMM dd";        // Aug 5
    public static final String DATE_FORMAT_33 = "yyyy,mm,dd,HH,mm,ss";
    public static final String DATE_FORMAT_34 = "MM-dd-yyyy";
    public static final String DATE_FORMAT_35 = "MMM";

    public static final String DATE_FORMAT_36 = "MMM dd yyyy hh:mm a";

    public static final String DATE_FORMAT_37 = "EEE, MMM d, yyyy, h:mm a";
    public static final String DATE_FORMAT_38 = "EEE d, h:mm a";

    /**
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * toString for format 1.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_1(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_1);
    }

    public static String toStringFormat_37(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_37);
    }

    public static String toStringFormat_38(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_38);
    }

    public static String toStringFormatMMMddyyyy(Date date) {
        if (date == null)
            return "";
        return dateToString(date, "MMM dd, yyyy");
    }

    /**
     * toString for format 2.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_2(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_2);
    }


    /**
     * toString for format 3.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_3(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_3);
    }

    /**
     * toString for format 4.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_4(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_4);
    }

    /**
     * toString for format 5.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_5(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_5);
    }

    /**
     * toString for format 6.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_6(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_6);
    }

    /**
     * toString for format 7.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_7(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_7);
    }

    /**
     * toString for format 8.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_8(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_8);
    }

    /**
     * toString for format 9.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_9(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_9);
    }

    /**
     * toString for format 10.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_10(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_10);
    }

    /**
     * toString for format 11.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_11(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_11);
    }

    /**
     * toString for format 12.
     *
     * @param date
     * @return
     */
    public static String toStringFormat_12(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_12);
    }

    public static String toStringFormat_13(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_13);
    }

    public static String toStringFormat_34(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_34);
    }

    public static String toStringFormat_35(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_35);
    }

    public static String toStringFormat_14(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_14);
    }

    public static String toStringFormat_15(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_15);
    }

    public static String toStringFormat_16(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_16);
    }

    public static String toStringFormat_17(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_17);
    }

    public static String toStringFormat_18(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_18);
    }

    public static String toStringFormat_19(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_19);
    }

    public static String toStringFormat_20(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_20);
    }

    public static String toStringFormat_21(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_21);
    }

    public static String toStringFormat_22(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_22);
    }

    public static String toStringFormat_23(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_23);
    }

    public static String toStringFormat_24(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_24);
    }

    public static String toStringFormat_25(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_25);
    }

    public static String toStringFormat_26(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_26);
    }

    public static String toStringFormat_27(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_27);
    }

    public static String toStringFormat_28(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_28);
    }

    public static String toStringFormat_29(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_29);
    }

    public static String toStringFormat_30(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_30);
    }

    public static String toStringFormat_31(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_31);
    }

    public static String toStringFormat_32(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_32);
    }

    public static String toStringFormat_33(Date date) {
        if (date == null)
            return "";
        return dateToString(date, DATE_FORMAT_33);
    }

    public static Object getSOAPDateString(Date itemValue) {
        String lFormatTemplate = "yyyy-MM-dd'T'hh:mm:ss'Z'";
        DateFormat lDateFormat = new SimpleDateFormat(lFormatTemplate);
        String lDate = lDateFormat.format(itemValue);
        return lDate;
    }

    public static Calendar getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        int day = getDayFromTimestamp(currentTimeMillis);
        int month = getMonthFromTimestamp(currentTimeMillis);
        int year = getYearFromTimestamp(currentTimeMillis);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar;
    }

    public static long getTimestamp(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static int getYearFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.YEAR);
    }

    public static int getMonthFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.MONTH);
    }

    public static int getHourFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.MINUTE);
    }

    public static int getSecondFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.SECOND);
    }

    public static String getFormattedDateFromTimestamp(long timestamp) {
        int year = getYearFromTimestamp(timestamp);
        int month = getMonthFromTimestamp(timestamp);
        int day = getDayFromTimestamp(timestamp);

        return year + " - " + (month + 1) + " - " + day;
    }

    public static String getFormattedDate2FromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return toStringFormat_1(cal.getTime());
    }

    public static int getDayFromTimestamp(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);

        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getCalendarFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        int day = getDayFromTimestamp(timestamp);
        int month = getMonthFromTimestamp(timestamp);
        int year = getYearFromTimestamp(timestamp);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar;
    }

    public static String getCompleteFormattedDateFromTimestamp(long timestamp) {
        int year = getYearFromTimestamp(timestamp);
        int month = getMonthFromTimestamp(timestamp);
        int day = getDayFromTimestamp(timestamp);
        int hour = getHourFromTimestamp(timestamp);
        int minute = getMinuteFromTimestamp(timestamp);
        int second = getSecondFromTimestamp(timestamp);

        return year + " - " + String.format("%02d", (month + 1)) + " - " + String.format("%02d", day) + " "
                + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
    }

    public static Date parseDataFromFormat1(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_1);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat36(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_36);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat7(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_7);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat12(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_12);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat13(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_13);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat19(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_19);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat20(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_20);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat22(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_22);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }


    public static Date parseMonthName(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MMMM");
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDateToMonthFullName(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }



    public static Date parseDataFromFormat24(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_24);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

    public static Date parseDataFromFormat28(String dateString) {
        Date retDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_28);
        try {
            retDate = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return retDate;
    }

}
