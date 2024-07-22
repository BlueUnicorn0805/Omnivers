package hawaiiappbuilders.omniversapp.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import hawaiiappbuilders.omniversapp.model.ContactInfo;

import java.io.IOException;
import java.util.ArrayList;

/* reflects the local database operation such as creation of tables, adding new log record removing and updating records */
public class ContactsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private ContactsSQLiteHelper dbHelper;

    // User table columns
    private String[] allColumnsOnUserTable = {ContactsSQLiteHelper.COLUMN_ID,
            ContactsSQLiteHelper.COLUMN_FNAME,
            ContactsSQLiteHelper.COLUMN_LNAME,
            ContactsSQLiteHelper.COLUMN_PHONE,
            ContactsSQLiteHelper.COLUMN_PHONEMETA,
            ContactsSQLiteHelper.COLUMN_EMAIL,
            ContactsSQLiteHelper.COLUMN_MLID
    };

    public ContactsDataSource(Context context) {
        dbHelper = new ContactsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // ----------------------------- User Info table operations ---------------------------------------
    /* Create New user Information on user table */
    public ContactInfo createUserInfo(ContactInfo userInfo) {

        ContentValues values = new ContentValues();
        values.put(ContactsSQLiteHelper.COLUMN_FNAME, userInfo.getFname());
        values.put(ContactsSQLiteHelper.COLUMN_LNAME, userInfo.getLname());
        values.put(ContactsSQLiteHelper.COLUMN_PHONE, userInfo.getPhoneData());
        values.put(ContactsSQLiteHelper.COLUMN_PHONEMETA, userInfo.getPhoneMetaData());
        values.put(ContactsSQLiteHelper.COLUMN_EMAIL, userInfo.getEmailData());
        values.put(ContactsSQLiteHelper.COLUMN_MLID, userInfo.getMlid());

        long insertId = database.insert(ContactsSQLiteHelper.TABLE_USER, null,
                values);
        Cursor cursor = database.query(ContactsSQLiteHelper.TABLE_USER,
                allColumnsOnUserTable, ContactsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ContactInfo newUser = cursorToUser(cursor);
        cursor.close();

        // Pop DataBase
        try {
            ContactsSQLiteHelper.popUpDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newUser;
    }

    /* Create New user Information on user table */
    public void changeUserInfo(int userId, String fName, String lName) {

        ContentValues cv = new ContentValues();
        cv.put(ContactsSQLiteHelper.COLUMN_FNAME, "fName");
        cv.put(ContactsSQLiteHelper.COLUMN_LNAME, "lName");

        database.update(ContactsSQLiteHelper.TABLE_USER, cv, "_id=" + userId, null);
    }

    /* Create New user Information on user table */
    public void updateUserInfo(ContactInfo contactInfo) {

        ContentValues values = new ContentValues();
        values.put(ContactsSQLiteHelper.COLUMN_FNAME, contactInfo.getFname());
        values.put(ContactsSQLiteHelper.COLUMN_LNAME, contactInfo.getLname());
        values.put(ContactsSQLiteHelper.COLUMN_PHONE, contactInfo.getPhoneData());
        values.put(ContactsSQLiteHelper.COLUMN_PHONEMETA, contactInfo.getPhoneMetaData());
        values.put(ContactsSQLiteHelper.COLUMN_EMAIL, contactInfo.getEmailData());
        values.put(ContactsSQLiteHelper.COLUMN_MLID, contactInfo.getMlid());

        database.update(ContactsSQLiteHelper.TABLE_USER, values, "_id=" + contactInfo.getId(), null);
    }

    /* Delete User Information from user table*/
    public void deleteUserInfo(ContactInfo userInfo) {
        long id = userInfo.getId();
        System.out.println("User deleted with id: " + id);
        database.delete(ContactsSQLiteHelper.TABLE_USER, ContactsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    /* Remove all user info */
    public void deleteAllUserInfo() {
        System.out.println("UserInfo All deleted");
        database.delete(ContactsSQLiteHelper.TABLE_USER, null, null);
    }

    public long getContactRecordsCount() {
        long count = DatabaseUtils.queryNumEntries(database, ContactsSQLiteHelper.TABLE_USER);
        return count;
    }

    /* Get All user Information */
    public ArrayList<ContactInfo> getAllUserInfo() {
        ArrayList<ContactInfo> userInfoList = new ArrayList<ContactInfo>();

        Cursor cursor = database.query(ContactsSQLiteHelper.TABLE_USER,
                allColumnsOnUserTable, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactInfo userInfo = cursorToUser(cursor);
            userInfoList.add(userInfo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return userInfoList;
    }

    /* Convert table record to ContactInfo object */
    private ContactInfo cursorToUser(Cursor cursor) {
        ContactInfo userInfo = new ContactInfo();
        userInfo.setId(cursor.getInt(0));
        userInfo.setFname(cursor.getString(1));
        userInfo.setLname(cursor.getString(2));
        userInfo.setPhoneData(cursor.getString(3));
        userInfo.setEmailData(cursor.getString(5));
        userInfo.setMlid(cursor.getInt(6));

        return userInfo;
    }
    //-------------------------------------------------------------------------------------------------
} 
