package hawaiiappbuilders.omniversapp.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.model.HistoryData;

public class HistoryDataSource {
    // Database fields
    private SQLiteDatabase database;
    private HistorySQLiteHelper dbHelper;

    // User table columns
    private String[] allColumnsOnUserTable = {HistorySQLiteHelper.COLUMN_ID,
            HistorySQLiteHelper.COLUMN_DATE,
            HistorySQLiteHelper.COLUMN_TIME,
            HistorySQLiteHelper.COLUMN_LAT,
            HistorySQLiteHelper.COLUMN_LON,
            HistorySQLiteHelper.COLUMN_ZIP,
            HistorySQLiteHelper.COLUMN_STREET_ADDRESS,
            HistorySQLiteHelper.COLUMN_CITY,
            HistorySQLiteHelper.COLUMN_STATE,
            HistorySQLiteHelper.COLUMN_FULL_ADDRESS
    };

    public HistoryDataSource(Context context) {
        dbHelper = new HistorySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // ----------------------------- History table operations ---------------------------------------
    /* Create New user Information on history table */
    public HistoryData createLocationHistory(HistoryData historyLocationData) {

        ContentValues values = new ContentValues();

        values.put(HistorySQLiteHelper.COLUMN_DATE, historyLocationData.getDate());
        values.put(HistorySQLiteHelper.COLUMN_TIME, historyLocationData.getTime());
        values.put(HistorySQLiteHelper.COLUMN_LAT, historyLocationData.getLat());
        values.put(HistorySQLiteHelper.COLUMN_LON, historyLocationData.getLon());
        values.put(HistorySQLiteHelper.COLUMN_ZIP, historyLocationData.getZip());
        values.put(HistorySQLiteHelper.COLUMN_STREET_ADDRESS, historyLocationData.getStreetAddress());
        values.put(HistorySQLiteHelper.COLUMN_CITY, historyLocationData.getCity());
        values.put(HistorySQLiteHelper.COLUMN_STATE, historyLocationData.getState());
        values.put(HistorySQLiteHelper.COLUMN_FULL_ADDRESS, historyLocationData.getFullAddress());

        long insertId = database.insert(HistorySQLiteHelper.TABLE_HISTORY, null,
                values);
        Cursor cursor = database.query(HistorySQLiteHelper.TABLE_HISTORY,
                allColumnsOnUserTable, HistorySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        HistoryData newHistory = cursorToHistory(cursor);
        cursor.close();

        // Pop DataBase
        try {
            HistorySQLiteHelper.popUpDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newHistory;
    }

    /* Delete User Information from user table*/
    public void deleteLocationHistory(HistoryData historyData) {
        long id = historyData.getId();
        System.out.println("History deleted with id: " + id);
        database.delete(HistorySQLiteHelper.TABLE_HISTORY, HistorySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /* Get All history Information */
    public ArrayList<HistoryData> getAllLocationHistory() {
        ArrayList<HistoryData> historyList = new ArrayList<HistoryData>();

        Cursor cursor = database.query(HistorySQLiteHelper.TABLE_HISTORY,
                allColumnsOnUserTable, null, null, null, null, HistorySQLiteHelper.COLUMN_ID + " DESC", "16");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HistoryData historyData = cursorToHistory(cursor);
            historyList.add(historyData);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return historyList;
    }

    /* Convert table record to HistoryData object */
    private HistoryData cursorToHistory(Cursor cursor) {
        HistoryData historyData = new HistoryData();

        historyData.setId(cursor.getInt(0));
        historyData.setDate(cursor.getString(1));
        historyData.setTime(cursor.getString(2));
        historyData.setLat(cursor.getDouble(3));
        historyData.setLon(cursor.getDouble(4));
        historyData.setZip(cursor.getString(5));
        historyData.setStreetAddress(cursor.getString(6));
        historyData.setCity(cursor.getString(7));
        historyData.setState(cursor.getString(8));
        historyData.setFullAddress(cursor.getString(9));

        return historyData;
    }
    //-------------------------------------------------------------------------------------------------
}
