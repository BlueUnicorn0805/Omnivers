package hawaiiappbuilders.omniversapp.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.AlarmNotifyReceiver;
import hawaiiappbuilders.omniversapp.global.AppSettings;
import hawaiiappbuilders.omniversapp.utils.DateUtil;

public class AlarmMeetDataManager {
    private static AlarmMeetDataManager INSTANCE;

    AppSettings appSettings;
    public static AlarmMeetDataManager getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new AlarmMeetDataManager(context);
        return INSTANCE;
    }

    public class AlarmMeetData {
        public long apptID;
        public long notifyTime;
        public String title;
        public long priorTime;
        public int repeatOption;

        public AlarmMeetData(long apptID, long notifyTime, String title, long priorTime, int repeatOption) {
            this.apptID = apptID;
            this.notifyTime = notifyTime;
            this.title = title;

            this.priorTime = priorTime;
            this.repeatOption = repeatOption;
        }
    }

    private ArrayList<AlarmMeetData> alarmDataList = new ArrayList<>();

    public ArrayList<AlarmMeetData> getAlarmDataList() {
        return alarmDataList;
    }

    public AlarmMeetDataManager(Context context) {
        appSettings = new AppSettings(context);
        String apptAlaramData = appSettings.getApptAlarmData();
        Log.e("qix", apptAlaramData);
        try {
            if(!apptAlaramData.isEmpty()) {
                JSONArray jsonArray = new JSONArray(apptAlaramData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    alarmDataList.add(new AlarmMeetData(jsonObject.getInt("id"), jsonObject.getLong("time"), jsonObject.optString("title"),
                            jsonObject.optLong("prior", 1000 * 60 * 30), jsonObject.optInt("repeat")));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addNewAlaramData(long id, long time, String title, long timeNotification, int repeatOption) {
        alarmDataList.add(new AlarmMeetData(id, time, title, timeNotification, repeatOption));
        saveAlarmMeetData();
    }

    public void removeAlarm(Context context, long apptId) {
        for (int i = alarmDataList.size() - 1; i >= 0; i--) {
            AlarmMeetData alarmData = alarmDataList.get(i);

            long alarmID = alarmData.apptID;
            if (alarmID == apptId) {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmNotifyReceiver.class);
                //intent.putExtra("title", alarmData.title);
                //intent.putExtra("msg", "It's almost time for your meeting");
                intent.putExtra("title", alarmData.title);
                intent.putExtra("msg", DateUtil.toStringFormat_38(new Date(alarmData.notifyTime)));

                PendingIntent alarmIntent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    alarmIntent = PendingIntent.getBroadcast(context, (int) alarmData.apptID, intent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    alarmIntent = PendingIntent.getBroadcast(context, (int) alarmData.apptID, intent, PendingIntent.FLAG_IMMUTABLE);
                }

                alarmMgr.cancel(alarmIntent);

                alarmDataList.remove(i);

                saveAlarmMeetData();
            }
        }
    }

    public AlarmMeetData getAlarm(long apptId) {
        for (int i = alarmDataList.size() - 1; i >= 0; i--) {
            AlarmMeetData alarmData = alarmDataList.get(i);

            long alarmID = alarmData.apptID;
            if (alarmID == apptId) {
                return alarmData;
            }
        }

        return null;
    }

    private void saveAlarmMeetData() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < alarmDataList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", alarmDataList.get(i).apptID);
                jsonObject.put("time", alarmDataList.get(i).notifyTime);
                jsonObject.put("title", alarmDataList.get(i).title);
                jsonObject.put("prior", alarmDataList.get(i).priorTime);
                jsonObject.put("repeat", alarmDataList.get(i).repeatOption);

                jsonArray.put(jsonObject);
            }
            appSettings.setApptAlaramData(jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void restoreAlarms(Context context) {
        for (int i = alarmDataList.size() - 1; i >= 0; i--) {
            AlarmMeetData alarmData = alarmDataList.get(i);
            if (alarmData.notifyTime < System.currentTimeMillis()) {
                // If already timeout, then throw it
                alarmDataList.remove(i);
            } else {
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, AlarmNotifyReceiver.class);
                //intent.putExtra("title", alarmData.title);
                //intent.putExtra("msg", "It's almost time for your meeting");
                intent.putExtra("title", alarmData.title);
                intent.putExtra("msg", DateUtil.toStringFormat_38(new Date(alarmData.notifyTime + alarmData.priorTime)));

                PendingIntent alarmIntent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    alarmIntent = PendingIntent.getBroadcast(context, (int) alarmData.apptID, intent, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    alarmIntent = PendingIntent.getBroadcast(context, (int) alarmData.apptID, intent, PendingIntent.FLAG_IMMUTABLE);
                }

                if (alarmData.repeatOption == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmData.notifyTime, alarmIntent);
                    } else {
                        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, alarmData.notifyTime, alarmIntent);
                    }
                } else {
                    // "Does not repeat", "Every day", "Every week", "Every month", "Every year"
                    long repeatInterval = 0;
                    if (alarmData.repeatOption == 1) {
                        repeatInterval = AlarmManager.INTERVAL_DAY;
                    } else if (alarmData.repeatOption == 2) {
                        repeatInterval = AlarmManager.INTERVAL_DAY * 7;
                    } else if (alarmData.repeatOption == 3) {
                        repeatInterval = AlarmManager.INTERVAL_DAY * 30;
                    } else if (alarmData.repeatOption == 4) {
                        repeatInterval = AlarmManager.INTERVAL_DAY * 365;
                    }
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmData.notifyTime, repeatInterval, alarmIntent);
                }
            }
        }

        saveAlarmMeetData();
    }
}
