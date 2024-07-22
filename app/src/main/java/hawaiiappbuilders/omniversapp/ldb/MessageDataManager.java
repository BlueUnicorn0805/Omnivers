package hawaiiappbuilders.omniversapp.ldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import hawaiiappbuilders.omniversapp.ActivityFamilyHierarchy;
import hawaiiappbuilders.omniversapp.depositcheck.checks.Check;
import hawaiiappbuilders.omniversapp.model.CallHistory;
import hawaiiappbuilders.omniversapp.model.ChatUserInfoProvider;
import hawaiiappbuilders.omniversapp.model.ContactInfo;
import hawaiiappbuilders.omniversapp.model.GroupInfo;

public class MessageDataManager extends SQLiteOpenHelper {

    //DATABASE NAME
    private static final String DB_NAME = "infinite_conn_db";
    //DATABASE VERSION
    private static final int DB_VERSION = 19;

    private SQLiteDatabase db;

    public MessageDataManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        // User Table
        // Query to create table for User Table, name of table and columns are from QuizTable.java
        final String QUERY_CREATE_USERS_TABLE = "CREATE TABLE " +
                LocalTables.ContactTable.TABLE_NAME + " ( " +
                LocalTables.ContactTable._ID + " `INTEGER` PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.ContactTable.TABLE_COL_EMAIL + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_MLID + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_FN + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_LN + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_HANDLE + " TEXT, " +

                LocalTables.ContactTable.TABLE_COL_ADDR + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_SUITE + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_ZIP + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_STATE + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_CITY + " TEXT, " +

                LocalTables.ContactTable.TABLE_COL_CP + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_DOB + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_SHARELOC + " TEXT, " +

                LocalTables.ContactTable.TABLE_COL_YOUTUBE + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_FB + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_TWITTER + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_LINKEDIN + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_PINTEREST + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_SNAPCHAT + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_INSTAGRAM + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_WHATSAPP + " TEXT, " +

                LocalTables.ContactTable.TABLE_COL_CO + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_TITLE + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_WORKADD + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_WEBSITE + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_WP + " TEXT, " +
                LocalTables.ContactTable.TABLE_COL_CREATEDATE + " TEXT," +

                LocalTables.ContactTable.TABLE_COL_PRI + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER + " TEXT," +

                LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_GENDER + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_INITIAL + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_STREETNUM + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_STREET + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_STE + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_UTC + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_MARITAL + " TEXT," +

                LocalTables.ContactTable.TABLE_COL_VERIFIED + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_RATING + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_COA + " INTEGER," +

                LocalTables.ContactTable.TABLE_COL_PERSONAL + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_BUSINESS + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_FAMILY + " INTEGER," +

                LocalTables.ContactTable.TABLE_COL_BLOCKED + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_ARCHIVED + " INTEGER," +
                LocalTables.ContactTable.TABLE_COL_LON + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_LAT + " TEXT," +

                LocalTables.ContactTable.TABLE_COL_EDITDATE + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_INDUSTRYID + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL + " TEXT," +
                LocalTables.ContactTable.TABLE_COL_GROUPIDS + " TEXT" +
                ")";

        db.execSQL(QUERY_CREATE_USERS_TABLE);

        //fillUserTable();

        // Database table for Questions, query string
        final String QUERY_CREATE_MSGHISTORIES_TABLE = "CREATE TABLE " +
                LocalTables.MessageHistoryTable.TABLE_NAME + " ( " +
                LocalTables.MessageHistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.MessageHistoryTable.TABLE_COL_SVRID + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_STATUSID + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_FROMID + " TEXT," +
                LocalTables.MessageHistoryTable.TABLE_COL_TOID + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_EMPLOYERID + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_MSG + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_NAME + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE + " TEXT, " +
                LocalTables.MessageHistoryTable.TABLE_COL_CHANNEL + " TEXT" + ")";

        db.execSQL(QUERY_CREATE_MSGHISTORIES_TABLE);
        //RUN THIS TO POPULATE QUESTION TABLE

        // Database table for Group Information, query string
        final String QUERY_CREATE_GROUP_TABLE = "CREATE TABLE " +
                LocalTables.GroupTable.TABLE_NAME + " ( " +
                LocalTables.GroupTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.GroupTable.TABLE_COL_GRPNAME + " TEXT, " +
                LocalTables.GroupTable.TABLE_COL_PRI + " INTEGER, " +
                LocalTables.GroupTable.TABLE_COL_SORTBY + " INTEGER," +
                LocalTables.GroupTable.TABLE_COL_CREATEDAT + " INTEGER, " +
                LocalTables.GroupTable.TABLE_COL_MORE + " TEXT" + ")";
        db.execSQL(QUERY_CREATE_GROUP_TABLE);
        fillGroupTable();

        final String QUERY_CREATE_FAMILY_MEMBERS = "CREATE TABLE " +
                LocalTables.FamilyMemberTable.TABLE_NAME + " ( " +
                LocalTables.FamilyMemberTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.FamilyMemberTable.TABLE_COL_FNAME + " TEXT, " +
                LocalTables.FamilyMemberTable.TABLE_COL_LNAME + " TEXT, " +
                LocalTables.FamilyMemberTable.TABLE_COL_BIRTHDATE + " INTEGER, " +
                LocalTables.FamilyMemberTable.TABLE_COL_TITLE + " TEXT," +
                LocalTables.FamilyMemberTable.TABLE_COL_AVATAR + " TEXT, " +
                LocalTables.FamilyMemberTable.TABLE_COL_MOM_ID + " INTEGER, " +
                LocalTables.FamilyMemberTable.TABLE_COL_DAD_ID + " INTEGER, " +
                LocalTables.FamilyMemberTable.TABLE_COL_SPOUSE_ID + " INTEGER, " +
                LocalTables.FamilyMemberTable.TABLE_COL_SETTINGS + " TEXT, " +
                LocalTables.FamilyMemberTable.TABLE_COL_TITLE_ID + " INTEGER," +
                LocalTables.FamilyMemberTable.TABLE_COL_CHILDREN + " TEXT" + ")";

        db.execSQL(QUERY_CREATE_FAMILY_MEMBERS);

        final String QUERY_CREATE_CHILDREN = "CREATE TABLE " +
                LocalTables.ChildrenTable.TABLE_NAME + " ( " +
                LocalTables.ChildrenTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.ChildrenTable.TABLE_COL_MOM_ID + " INTEGER, " +
                LocalTables.ChildrenTable.TABLE_COL_DAD_ID + " INTEGER, " +
                LocalTables.ChildrenTable.TABLE_COL_CHILD_ID + " INTEGER" + ")";

        db.execSQL(QUERY_CREATE_CHILDREN);

        final String QUERY_CREATE_CALLLOG_TABLE = "CREATE TABLE " +
                LocalTables.CallLogTable.TABLE_NAME + " ( " +
                LocalTables.CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.CallLogTable.TABLE_COL_LDBID + " INTEGER DEFAULT 0, " +
                LocalTables.CallLogTable.TABLE_COL_PHONE + " TEXT, " +
                LocalTables.CallLogTable.TABLE_COL_STATUSID + " INTEGER DEFAULT 0," +
                LocalTables.CallLogTable.TABLE_COL_INOUT + " INTEGER DEFAULT 0, " +
                LocalTables.CallLogTable.TABLE_COL_CALLSECS + " INTEGER DEFAULT 0, " +
                LocalTables.CallLogTable.TABLE_COL_CREATEDATE + " INTEGER DEFAULT 0" + ")";

        db.execSQL(QUERY_CREATE_CALLLOG_TABLE);

        final String QUERY_CREATE_CHECK = "CREATE TABLE " +
                LocalTables.CheckTable.TABLE_NAME + " ( " +
                LocalTables.CheckTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID + " INTEGER, " +
                LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_BANK_NAME + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_NAME + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_MEMO + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_ADDRESS + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_BACK_IMAGE + " TEXT, " +
                LocalTables.CheckTable.TABLE_COL_AMOUNT + " TEXT" + ")";

        db.execSQL(QUERY_CREATE_CHECK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;

        //db.execSQL("DROP TABLE IF EXISTS " + LocalTables.MessageHistoryTable.TABLE_NAME);
        //onCreate(db);
        if (oldVersion < 2) {
            // If you need to add a column
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_ZIP + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_STATE + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_CITY + " TEXT;");
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_PRI + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER + " TEXT;");

            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_GENDER + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_INITIAL + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_STREETNUM + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_STREET + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_STE + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_UTC + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_MARITAL + " TEXT;");

            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_VERIFIED + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_RATING + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_COA + " INTEGER;");

            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_PERSONAL + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_BUSINESS + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_FAMILY + " INTEGER;");

            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_BLOCKED + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_ARCHIVED + " INTEGER;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_LON + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_LAT + " TEXT;");

            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_EDITDATE + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_INDUSTRYID + " TEXT;");
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL + " TEXT;");

        }

        if (oldVersion < 4) {
            // In version 4, we added following
            // Database table for Group Information, query string
            final String QUERY_CREATE_GROUP_TABLE = "CREATE TABLE " +
                    LocalTables.GroupTable.TABLE_NAME + " ( " +
                    LocalTables.GroupTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    LocalTables.GroupTable.TABLE_COL_GRPNAME + " TEXT, " +
                    LocalTables.GroupTable.TABLE_COL_PRI + " INTEGER, " +
                    LocalTables.GroupTable.TABLE_COL_SORTBY + " INTEGER," +
                    LocalTables.GroupTable.TABLE_COL_CREATEDAT + " INTEGER, " +
                    LocalTables.GroupTable.TABLE_COL_MORE + " TEXT" + ")";

            db.execSQL(QUERY_CREATE_GROUP_TABLE);
            fillGroupTable();
        }

        if (oldVersion < 5) {

            String query = "INSERT INTO " + LocalTables.GroupTable.TABLE_NAME + " (" +
                    LocalTables.GroupTable.TABLE_COL_GRPNAME + ", " +
                    LocalTables.GroupTable.TABLE_COL_PRI + ", " +
                    LocalTables.GroupTable.TABLE_COL_SORTBY + ", " +
                    LocalTables.GroupTable.TABLE_COL_CREATEDAT + ", " +
                    LocalTables.GroupTable.TABLE_COL_MORE + " ) " +
                    "VALUES ('All', 0, 0, 0, '');";

            db.execSQL(query);
        }

        if (oldVersion < 6) {
            String QUERY_CREATE_CALLLOG_TABLE = "CREATE TABLE " +
                    LocalTables.CallLogTable.TABLE_NAME + " ( " +
                    LocalTables.CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    LocalTables.CallLogTable.TABLE_COL_LDBID + " TEXT, " +
                    LocalTables.CallLogTable.TABLE_COL_PHONE + " INTEGER, " +
                    LocalTables.CallLogTable.TABLE_COL_STATUSID + " INTEGER," +
                    LocalTables.CallLogTable.TABLE_COL_INOUT + " INTEGER, " +
                    LocalTables.CallLogTable.TABLE_COL_CALLSECS + " INTEGER, " +
                    LocalTables.CallLogTable.TABLE_COL_CREATEDATE + " TEXT" + ")";

            db.execSQL(QUERY_CREATE_CALLLOG_TABLE);
        }

        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_GROUPIDS + " TEXT;");
        }

        if (oldVersion < 13) {
            // contacts
            // family hierarchy
            // todo: if new database version detected, do something so it won't break the app
        }

        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE " + LocalTables.ContactTable.TABLE_NAME + " ADD COLUMN " + LocalTables.ContactTable.TABLE_COL_HANDLE + " TEXT;");
        }

        Log.i("SQLITEDATABASE", "onUpgrade");
    }

    private void fillUserTable() {

        ArrayList<ChatUserInfoProvider.ChatUserInfo> initialUser = ChatUserInfoProvider.getInstance().getUserList();

        for (int i = 0; i < initialUser.size(); i++) {
            String[] names = initialUser.get(i).Name.split(" ");
            String fn = names[0];
            String ln = names[1];
            String mlid = initialUser.get(i).ID;

            String query = "INSERT INTO " + LocalTables.ContactTable.TABLE_NAME + " (" +
                    LocalTables.ContactTable.TABLE_COL_MLID + ", " +
                    LocalTables.ContactTable.TABLE_COL_FN + ", " +
                    LocalTables.ContactTable.TABLE_COL_LN + " ) " +
                    "VALUES (" + "'" + mlid + "', '" + fn + "', '" + ln + "'); ";

            db.execSQL(query);
        }
    }

    // Get checks
    public ArrayList<Check> getCheckHistory() {

        ArrayList<Check> qList = new ArrayList<>();
        db = getReadableDatabase();

        // String groupInfo = "|" + groupID + "|";

        String query = "SELECT * FROM " + LocalTables.CheckTable.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        //if there is an entry

        while (c.moveToNext()) {
            Check m = new Check();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.CheckTable._ID)));
            m.setTransactionId(c.getInt(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID)));
            m.setTransactionDate(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE)));
            m.setBankName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BANK_NAME)));
            m.setName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_NAME)));
            m.setMemo(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_MEMO)));
            m.setAddress(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ADDRESS)));
            m.setCheckNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER)));
            m.setRoutingNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER)));
            m.setAccountNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER)));
            m.setFrontImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE)));
            m.setBackImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BACK_IMAGE)));
            m.setAmount(Double.parseDouble(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_AMOUNT))));
            qList.add(m);
        }

        c.close();
        return qList;
    }

    public Check getCheckById(long id) {

        Check check = new Check();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.CheckTable.TABLE_NAME + " WHERE " + LocalTables.CheckTable._ID + " = " + id + ";";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry
        while (c.moveToNext()) {
            Check m = new Check();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.CheckTable._ID)));
            m.setTransactionId(c.getInt(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID)));
            m.setTransactionDate(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE)));
            m.setBankName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BANK_NAME)));
            m.setName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_NAME)));
            m.setMemo(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_MEMO)));
            m.setAddress(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ADDRESS)));
            m.setCheckNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER)));
            m.setRoutingNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER)));
            m.setAccountNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER)));
            m.setFrontImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE)));
            m.setBackImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BACK_IMAGE)));
            m.setAmount(Double.parseDouble(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_AMOUNT))));
            check = m;
        }

        c.close();
        return check;
    }

    public boolean isCheckAdded(String checkNo, String routingNo, String accountNo) {

        Check check = new Check();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.CheckTable.TABLE_NAME + " WHERE "
                + LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER + " = ? AND "
                + LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER + " = ? AND "
                + LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER + " = ?";
        Cursor c = db.rawQuery(query, new String[]{checkNo, routingNo, accountNo});
//db.isCheckAdded("9283749", "4801", "1234567890");
        c.moveToFirst();
        c.moveToFirst();
        int resultCount = c.getCount();
        //if there is an entry
        /*while (c.moveToNext()) {
            Check m = new Check();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.CheckTable._ID)));
            m.setTransactionId(c.getInt(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID)));
            m.setTransactionDate(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE)));
            m.setBankName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BANK_NAME)));
            m.setName(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_NAME)));
            m.setMemo(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_MEMO)));
            m.setAddress(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ADDRESS)));
            m.setCheckNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER)));
            m.setRoutingNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER)));
            m.setAccountNumber(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER)));
            m.setFrontImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE)));
            m.setBackImage(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_BACK_IMAGE)));
            m.setAmount(Double.parseDouble(c.getString(c.getColumnIndex(LocalTables.CheckTable.TABLE_COL_AMOUNT))));
            check = m;
        }*/

        c.close();
        return resultCount != 0;
    }

    public long addCheck(Check check) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID, check.getTransactionId());
        values.put(LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE, check.getTransactionDate());
        values.put(LocalTables.CheckTable.TABLE_COL_BANK_NAME, check.getBankName());
        values.put(LocalTables.CheckTable.TABLE_COL_NAME, check.getName());
        values.put(LocalTables.CheckTable.TABLE_COL_MEMO, check.getMemo());
        values.put(LocalTables.CheckTable.TABLE_COL_ADDRESS, check.getAddress());
        values.put(LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER, check.getCheckNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER, check.getRoutingNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER, check.getAccountNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE, check.getFrontImage());
        values.put(LocalTables.CheckTable.TABLE_COL_BACK_IMAGE, check.getBackImage());
        values.put(LocalTables.CheckTable.TABLE_COL_AMOUNT, check.getAmount());
        long insertId = db.insert(LocalTables.CheckTable.TABLE_NAME, null,
                values);

        check.setId((int) insertId);
        return insertId;
    }

    public void updateCheck(Check check) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.CheckTable.TABLE_COL_TRANSACTION_ID, check.getTransactionId());
        values.put(LocalTables.CheckTable.TABLE_COL_TRANSACTION_DATE, check.getTransactionDate());
        values.put(LocalTables.CheckTable.TABLE_COL_BANK_NAME, check.getBankName());
        values.put(LocalTables.CheckTable.TABLE_COL_NAME, check.getName());
        values.put(LocalTables.CheckTable.TABLE_COL_MEMO, check.getMemo());
        values.put(LocalTables.CheckTable.TABLE_COL_ADDRESS, check.getAddress());
        values.put(LocalTables.CheckTable.TABLE_COL_CHECK_NUMBER, check.getCheckNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_ROUTING_NUMBER, check.getRoutingNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_ACCOUNT_NUMBER, check.getAccountNumber());
        values.put(LocalTables.CheckTable.TABLE_COL_FRONT_IMAGE, check.getFrontImage());
        values.put(LocalTables.CheckTable.TABLE_COL_BACK_IMAGE, check.getBackImage());
        values.put(LocalTables.CheckTable.TABLE_COL_AMOUNT, check.getAmount());
        db.update(LocalTables.CheckTable.TABLE_NAME, values, LocalTables.CheckTable._ID + "=" + check.getId(), null);
    }

    public void removeCheck(Check check) {
        db = getWritableDatabase();
        db.delete(LocalTables.CheckTable.TABLE_NAME, LocalTables.CheckTable._ID
                + " = " + check.getId(), null);
    }

    /**
     * Get user's family members
     */
    public ArrayList<ActivityFamilyHierarchy.FamilyMember> getFamilyMembers() {

        ArrayList<ActivityFamilyHierarchy.FamilyMember> qList = new ArrayList<>();
        db = getReadableDatabase();

        // String groupInfo = "|" + groupID + "|";

        String query = "SELECT * FROM " + LocalTables.FamilyMemberTable.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        //if there is an entry

        while (c.moveToNext()) {
            ActivityFamilyHierarchy.FamilyMember m = new ActivityFamilyHierarchy.FamilyMember();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.FamilyMemberTable._ID)));
            m.setFirstName(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_FNAME)));
            m.setLastName(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_LNAME)));
            m.setBirthdate(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_BIRTHDATE)));
            m.setAvatarImg(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_AVATAR)));
            m.setTitle(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_TITLE)));
            m.setMomId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_MOM_ID)));
            m.setDadId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_DAD_ID)));
            m.setSpouseId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_SPOUSE_ID)));
            m.setSettings(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_SETTINGS)));
            m.setTitleId(c.getInt(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_TITLE_ID)));
            m.setChildren(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_CHILDREN)));
            qList.add(m);
        }

        c.close();
        return qList;
    }

    public ActivityFamilyHierarchy.FamilyMember getFamilyMemberById(long id) {

        ActivityFamilyHierarchy.FamilyMember member = new ActivityFamilyHierarchy.FamilyMember();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.FamilyMemberTable.TABLE_NAME + " WHERE " + LocalTables.FamilyMemberTable._ID + " = " + id + ";";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry
        while (c.moveToNext()) {
            ActivityFamilyHierarchy.FamilyMember m = new ActivityFamilyHierarchy.FamilyMember();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.FamilyMemberTable._ID)));
            m.setFirstName(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_FNAME)));
            m.setLastName(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_LNAME)));
            m.setBirthdate(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_BIRTHDATE)));
            m.setAvatarImg(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_AVATAR)));
            m.setTitle(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_TITLE)));
            m.setMomId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_MOM_ID)));
            m.setDadId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_DAD_ID)));
            m.setSpouseId(c.getLong(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_SPOUSE_ID)));
            m.setSettings(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_SETTINGS)));
            m.setTitleId(c.getInt(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_TITLE_ID)));
            m.setChildren(c.getString(c.getColumnIndex(LocalTables.FamilyMemberTable.TABLE_COL_CHILDREN)));
            member = m;
        }

        c.close();
        return member;
    }

    /**
     * Add a family member
     *
     * @return
     */
    public long addFamilyMember(ActivityFamilyHierarchy.FamilyMember familyMember) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_FNAME, familyMember.getFirstName());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_LNAME, familyMember.getLastName());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_BIRTHDATE, familyMember.getBirthdate());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_AVATAR, familyMember.getAvatarImg());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_TITLE, familyMember.getTitle());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_MOM_ID, familyMember.getMomId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_DAD_ID, familyMember.getDadId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_SPOUSE_ID, familyMember.getSpouseId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_SETTINGS, familyMember.getSettings());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_TITLE_ID, familyMember.getTitleId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_CHILDREN, familyMember.getChildren());
        long insertId = db.insert(LocalTables.FamilyMemberTable.TABLE_NAME, null,
                values);

        familyMember.setId((int) insertId);
        return insertId;
    }

    /**
     * Remove family member
     */
    public void removeFamilyMember(ActivityFamilyHierarchy.FamilyMember familyMember) {
        db = getWritableDatabase();
        db.delete(LocalTables.FamilyMemberTable.TABLE_NAME, LocalTables.FamilyMemberTable._ID
                + " = " + familyMember.getId(), null);
    }

    /**
     * Update family member
     */
    public void updateFamilyMember(ActivityFamilyHierarchy.FamilyMember familyMember) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_FNAME, familyMember.getFirstName());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_LNAME, familyMember.getLastName());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_BIRTHDATE, familyMember.getBirthdate());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_AVATAR, familyMember.getAvatarImg());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_TITLE, familyMember.getTitle());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_MOM_ID, familyMember.getMomId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_DAD_ID, familyMember.getDadId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_SPOUSE_ID, familyMember.getSpouseId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_SETTINGS, familyMember.getSettings());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_TITLE_ID, familyMember.getTitleId());
        values.put(LocalTables.FamilyMemberTable.TABLE_COL_CHILDREN, familyMember.getChildren());
        db.update(LocalTables.FamilyMemberTable.TABLE_NAME, values, LocalTables.FamilyMemberTable._ID + "=" + familyMember.getId(), null);
    }

    /**
     * Get FamilyMember.id's children
     */
    public ArrayList<ActivityFamilyHierarchy.Child> getChildren(int parentId) {
        ArrayList<ActivityFamilyHierarchy.Child> qList = new ArrayList<>();
        db = getReadableDatabase();

        // String groupInfo = "|" + groupID + "|";

        String query = "SELECT * FROM " + LocalTables.ChildrenTable.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        //if there is an entry

        while (c.moveToNext()) {
            ActivityFamilyHierarchy.Child m = new ActivityFamilyHierarchy.Child();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.ChildrenTable._ID)));
            m.setChildId(c.getLong(c.getColumnIndex(LocalTables.ChildrenTable.TABLE_COL_CHILD_ID)));
            m.setMomId(c.getLong(c.getColumnIndex(LocalTables.ChildrenTable.TABLE_COL_MOM_ID)));
            m.setDadId(c.getLong(c.getColumnIndex(LocalTables.ChildrenTable.TABLE_COL_DAD_ID)));
            qList.add(m);
        }

        c.close();
        return qList;
    }

    /**
     * Add a child
     *
     * @return
     */
    public long addChild(ActivityFamilyHierarchy.Child child) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.ChildrenTable.TABLE_COL_CHILD_ID, child.getChildId());
        values.put(LocalTables.ChildrenTable.TABLE_COL_MOM_ID, child.getMomId());
        values.put(LocalTables.ChildrenTable.TABLE_COL_DAD_ID, child.getDadId());

        long insertId = db.insert(LocalTables.ChildrenTable.TABLE_NAME, null,
                values);

        child.setId((int) insertId);
        return insertId;
    }

    /**
     * Remove a child
     */
    public void removeChild(ActivityFamilyHierarchy.Child child) {
        db = getWritableDatabase();
        db.delete(LocalTables.ChildrenTable.TABLE_NAME, LocalTables.ChildrenTable._ID
                + " = " + child.getId(), null);
    }

    private void fillGroupTable() {
        String[] grpNames = {"None", "Pinned", "Favs", "Family", "Friends", "Business", "Groups"};
        int[] grpPris = {57, 100, 99, 98, 97, 96, 95};
        int[] sortBys = {57, 1, 2, 3, 4, 5, 6};

        for (int i = 0; i < grpNames.length; i++) {
            String grpName = grpNames[i];
            int grpPri = grpPris[i];
            int sortBy = sortBys[i];

            String query = "INSERT INTO " + LocalTables.GroupTable.TABLE_NAME + " (" +
                    LocalTables.GroupTable.TABLE_COL_GRPNAME + ", " +
                    LocalTables.GroupTable.TABLE_COL_PRI + ", " +
                    LocalTables.GroupTable.TABLE_COL_SORTBY + ", " +
                    LocalTables.GroupTable.TABLE_COL_CREATEDAT + ", " +
                    LocalTables.GroupTable.TABLE_COL_MORE + " ) " +
                    "VALUES ('" + grpName + "', " + grpPri + ", " + sortBy + ", 0, '');";

            db.execSQL(query);
        }
    }

    // Find a user record
    public Cursor searchUser(String fn, String ln) {
        db = getReadableDatabase();
        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                LocalTables.ContactTable.TABLE_COL_FN + " = '" + fn + "' AND " + LocalTables.ContactTable.TABLE_COL_LN + " = '" + ln + "';";
        Cursor c = db.rawQuery(query, null);
        return c;
    }

    // Find a user record
    public boolean findContact(ContactInfo contactInfo) {
        if (contactInfo == null || TextUtils.isEmpty(contactInfo.getEmail())) {
            return false;
        }

        db = getReadableDatabase();
        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                LocalTables.ContactTable.TABLE_COL_EMAIL + " LIKE '%" + contactInfo.getEmail() + "%';";
        Cursor c = db.rawQuery(query, null);
        if (c == null || c.getCount() == 0) {
            return false;
        }

        c.moveToFirst();

        contactInfo.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
        contactInfo.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
        contactInfo.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
        contactInfo.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
        contactInfo.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
        contactInfo.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

        contactInfo.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
        contactInfo.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
        contactInfo.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
        contactInfo.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
        contactInfo.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

        contactInfo.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
        contactInfo.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
        contactInfo.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

        contactInfo.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
        contactInfo.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
        contactInfo.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
        contactInfo.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
        contactInfo.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
        contactInfo.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
        contactInfo.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
        contactInfo.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

        contactInfo.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
        contactInfo.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
        contactInfo.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
        contactInfo.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
        contactInfo.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
        contactInfo.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

        // New Fields in V3
        contactInfo.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
        contactInfo.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

        contactInfo.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
        contactInfo.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
        contactInfo.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
        contactInfo.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
        contactInfo.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
        contactInfo.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
        contactInfo.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
        contactInfo.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

        contactInfo.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
        contactInfo.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
        contactInfo.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

        contactInfo.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
        contactInfo.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
        contactInfo.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

        contactInfo.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
        contactInfo.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
        contactInfo.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
        contactInfo.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

        contactInfo.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
        contactInfo.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
        contactInfo.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));

        return true;
    }

    public boolean findContact(int mlid) {

        db = getReadableDatabase();
        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                LocalTables.ContactTable.TABLE_COL_MLID + "='" + mlid + "';";
        Cursor c = db.rawQuery(query, null);
        if (c == null || c.getCount() == 0) {
            return false;
        } else {
            c.close();
            return true;
        }
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void addContact(ContactInfo contactInfo) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.ContactTable.TABLE_COL_EMAIL, contactInfo.getEmail());
        values.put(LocalTables.ContactTable.TABLE_COL_MLID, contactInfo.getMlid());
        values.put(LocalTables.ContactTable.TABLE_COL_FN, contactInfo.getFname());
        values.put(LocalTables.ContactTable.TABLE_COL_LN, contactInfo.getLname());
        values.put(LocalTables.ContactTable.TABLE_COL_HANDLE, contactInfo.getHandle());

        values.put(LocalTables.ContactTable.TABLE_COL_ADDR, contactInfo.getAddress());
        values.put(LocalTables.ContactTable.TABLE_COL_SUITE, contactInfo.getSuite());
        values.put(LocalTables.ContactTable.TABLE_COL_ZIP, contactInfo.getZip());
        values.put(LocalTables.ContactTable.TABLE_COL_STATE, contactInfo.getState());
        values.put(LocalTables.ContactTable.TABLE_COL_CITY, contactInfo.getCity());

        values.put(LocalTables.ContactTable.TABLE_COL_CP, contactInfo.getCp());
        values.put(LocalTables.ContactTable.TABLE_COL_DOB, contactInfo.getDob());
        values.put(LocalTables.ContactTable.TABLE_COL_SHARELOC, contactInfo.getShareloc());

        values.put(LocalTables.ContactTable.TABLE_COL_YOUTUBE, contactInfo.getYoutube());
        values.put(LocalTables.ContactTable.TABLE_COL_FB, contactInfo.getFb());
        values.put(LocalTables.ContactTable.TABLE_COL_TWITTER, contactInfo.getTwitter());
        values.put(LocalTables.ContactTable.TABLE_COL_LINKEDIN, contactInfo.getLinkedin());
        values.put(LocalTables.ContactTable.TABLE_COL_PINTEREST, contactInfo.getPintrest());
        values.put(LocalTables.ContactTable.TABLE_COL_SNAPCHAT, contactInfo.getSnapchat());
        values.put(LocalTables.ContactTable.TABLE_COL_INSTAGRAM, contactInfo.getInstagram());
        values.put(LocalTables.ContactTable.TABLE_COL_WHATSAPP, contactInfo.getWhatsapp());

        values.put(LocalTables.ContactTable.TABLE_COL_CO, contactInfo.getCo());
        values.put(LocalTables.ContactTable.TABLE_COL_TITLE, contactInfo.getTitle());
        values.put(LocalTables.ContactTable.TABLE_COL_WORKADD, contactInfo.getWorkAddr());
        values.put(LocalTables.ContactTable.TABLE_COL_WEBSITE, contactInfo.getWebsite());
        values.put(LocalTables.ContactTable.TABLE_COL_WP, contactInfo.getWp());

        values.put(LocalTables.ContactTable.TABLE_COL_CREATEDATE, contactInfo.getCreateDate());

        // New fields in V3
        values.put(LocalTables.ContactTable.TABLE_COL_PRI, contactInfo.getPri());
        values.put(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER, contactInfo.getLocalDBOwnerMLID());

        values.put(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL, contactInfo.isFriendLevel());
        values.put(LocalTables.ContactTable.TABLE_COL_GENDER, contactInfo.getGender());
        values.put(LocalTables.ContactTable.TABLE_COL_INITIAL, contactInfo.getInitial());
        values.put(LocalTables.ContactTable.TABLE_COL_STREETNUM, contactInfo.getStreetNum());
        values.put(LocalTables.ContactTable.TABLE_COL_STREET, contactInfo.getStreet());
        values.put(LocalTables.ContactTable.TABLE_COL_STE, contactInfo.getSte());
        values.put(LocalTables.ContactTable.TABLE_COL_UTC, contactInfo.getUtc());
        values.put(LocalTables.ContactTable.TABLE_COL_MARITAL, contactInfo.getMarital());

        values.put(LocalTables.ContactTable.TABLE_COL_VERIFIED, contactInfo.getVerified());
        values.put(LocalTables.ContactTable.TABLE_COL_RATING, contactInfo.getRating());
        values.put(LocalTables.ContactTable.TABLE_COL_COA, contactInfo.getCoa());

        values.put(LocalTables.ContactTable.TABLE_COL_PERSONAL, contactInfo.getPersonal());
        values.put(LocalTables.ContactTable.TABLE_COL_BUSINESS, contactInfo.getBusiness());
        values.put(LocalTables.ContactTable.TABLE_COL_FAMILY, contactInfo.getFamily());

        values.put(LocalTables.ContactTable.TABLE_COL_BLOCKED, contactInfo.getBlocked());
        values.put(LocalTables.ContactTable.TABLE_COL_ARCHIVED, contactInfo.getArchived());
        values.put(LocalTables.ContactTable.TABLE_COL_LON, contactInfo.getLon());
        values.put(LocalTables.ContactTable.TABLE_COL_LAT, contactInfo.getLat());

        values.put(LocalTables.ContactTable.TABLE_COL_EDITDATE, contactInfo.getEditDate());
        values.put(LocalTables.ContactTable.TABLE_COL_INDUSTRYID, contactInfo.getIndustryID());

        values.put(LocalTables.ContactTable.TABLE_COL_GROUPIDS, contactInfo.getGroupInfo());
        values.put(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL, contactInfo.getVideoMeetingUrl());

        long insertId = db.insert(LocalTables.ContactTable.TABLE_NAME, null,
                values);

        contactInfo.setId((int) insertId);
    }

    public long addContactRtId(ContactInfo contactInfo) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.ContactTable.TABLE_COL_EMAIL, contactInfo.getEmail());
        values.put(LocalTables.ContactTable.TABLE_COL_MLID, contactInfo.getMlid());
        values.put(LocalTables.ContactTable.TABLE_COL_FN, contactInfo.getFname());
        values.put(LocalTables.ContactTable.TABLE_COL_LN, contactInfo.getLname());
        values.put(LocalTables.ContactTable.TABLE_COL_HANDLE, contactInfo.getHandle());

        values.put(LocalTables.ContactTable.TABLE_COL_ADDR, contactInfo.getAddress());
        values.put(LocalTables.ContactTable.TABLE_COL_SUITE, contactInfo.getSuite());
        values.put(LocalTables.ContactTable.TABLE_COL_ZIP, contactInfo.getZip());
        values.put(LocalTables.ContactTable.TABLE_COL_STATE, contactInfo.getState());
        values.put(LocalTables.ContactTable.TABLE_COL_CITY, contactInfo.getCity());

        values.put(LocalTables.ContactTable.TABLE_COL_CP, contactInfo.getCp());
        values.put(LocalTables.ContactTable.TABLE_COL_DOB, contactInfo.getDob());
        values.put(LocalTables.ContactTable.TABLE_COL_SHARELOC, contactInfo.getShareloc());

        values.put(LocalTables.ContactTable.TABLE_COL_YOUTUBE, contactInfo.getYoutube());
        values.put(LocalTables.ContactTable.TABLE_COL_FB, contactInfo.getFb());
        values.put(LocalTables.ContactTable.TABLE_COL_TWITTER, contactInfo.getTwitter());
        values.put(LocalTables.ContactTable.TABLE_COL_LINKEDIN, contactInfo.getLinkedin());
        values.put(LocalTables.ContactTable.TABLE_COL_PINTEREST, contactInfo.getPintrest());
        values.put(LocalTables.ContactTable.TABLE_COL_SNAPCHAT, contactInfo.getSnapchat());
        values.put(LocalTables.ContactTable.TABLE_COL_INSTAGRAM, contactInfo.getInstagram());
        values.put(LocalTables.ContactTable.TABLE_COL_WHATSAPP, contactInfo.getWhatsapp());

        values.put(LocalTables.ContactTable.TABLE_COL_CO, contactInfo.getCo());
        values.put(LocalTables.ContactTable.TABLE_COL_TITLE, contactInfo.getTitle());
        values.put(LocalTables.ContactTable.TABLE_COL_WORKADD, contactInfo.getWorkAddr());
        values.put(LocalTables.ContactTable.TABLE_COL_WEBSITE, contactInfo.getWebsite());
        values.put(LocalTables.ContactTable.TABLE_COL_WP, contactInfo.getWp());

        values.put(LocalTables.ContactTable.TABLE_COL_CREATEDATE, contactInfo.getCreateDate());

        // New fields in V3
        values.put(LocalTables.ContactTable.TABLE_COL_PRI, contactInfo.getPri());
        values.put(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER, contactInfo.getLocalDBOwnerMLID());

        values.put(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL, contactInfo.isFriendLevel());
        values.put(LocalTables.ContactTable.TABLE_COL_GENDER, contactInfo.getGender());
        values.put(LocalTables.ContactTable.TABLE_COL_INITIAL, contactInfo.getInitial());
        values.put(LocalTables.ContactTable.TABLE_COL_STREETNUM, contactInfo.getStreetNum());
        values.put(LocalTables.ContactTable.TABLE_COL_STREET, contactInfo.getStreet());
        values.put(LocalTables.ContactTable.TABLE_COL_STE, contactInfo.getSte());
        values.put(LocalTables.ContactTable.TABLE_COL_UTC, contactInfo.getUtc());
        values.put(LocalTables.ContactTable.TABLE_COL_MARITAL, contactInfo.getMarital());

        values.put(LocalTables.ContactTable.TABLE_COL_VERIFIED, contactInfo.getVerified());
        values.put(LocalTables.ContactTable.TABLE_COL_RATING, contactInfo.getRating());
        values.put(LocalTables.ContactTable.TABLE_COL_COA, contactInfo.getCoa());

        values.put(LocalTables.ContactTable.TABLE_COL_PERSONAL, contactInfo.getPersonal());
        values.put(LocalTables.ContactTable.TABLE_COL_BUSINESS, contactInfo.getBusiness());
        values.put(LocalTables.ContactTable.TABLE_COL_FAMILY, contactInfo.getFamily());

        values.put(LocalTables.ContactTable.TABLE_COL_BLOCKED, contactInfo.getBlocked());
        values.put(LocalTables.ContactTable.TABLE_COL_ARCHIVED, contactInfo.getArchived());
        values.put(LocalTables.ContactTable.TABLE_COL_LON, contactInfo.getLon());
        values.put(LocalTables.ContactTable.TABLE_COL_LAT, contactInfo.getLat());

        values.put(LocalTables.ContactTable.TABLE_COL_EDITDATE, contactInfo.getEditDate());
        values.put(LocalTables.ContactTable.TABLE_COL_INDUSTRYID, contactInfo.getIndustryID());

        values.put(LocalTables.ContactTable.TABLE_COL_GROUPIDS, contactInfo.getGroupInfo());
        values.put(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL, contactInfo.getVideoMeetingUrl());

        long insertId = db.insert(LocalTables.ContactTable.TABLE_NAME, null,
                values);

        contactInfo.setId((int) insertId);

        return insertId;
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void removeUser(ContactInfo contactInfo) {
        db = getWritableDatabase();
        db.delete(LocalTables.ContactTable.TABLE_NAME, LocalTables.ContactTable._ID
                + " = " + contactInfo.getId(), null);
    }

    public void removeAllContacts() {
        db = getWritableDatabase();
        db.delete(LocalTables.ContactTable.TABLE_NAME, null, null);
        db.close();
    }

    //    Clear records with MLID > 0
    public void removeAllContactsWithMLIDGT0() {
        db = getWritableDatabase();
        int result = db.delete(LocalTables.ContactTable.TABLE_NAME, LocalTables.ContactTable.TABLE_COL_MLID + " > ?", new String[]{"0"});
        db.close();
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void removeUserWithMLID(ContactInfo contactInfo) {
        db = getWritableDatabase();
        db.delete(LocalTables.ContactTable.TABLE_NAME, LocalTables.ContactTable.TABLE_COL_MLID
                + " = " + contactInfo.getMlid(), null);
    }

    public void updateContact(ContactInfo contactInfo) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.ContactTable.TABLE_COL_EMAIL, contactInfo.getEmail());
        values.put(LocalTables.ContactTable.TABLE_COL_MLID, contactInfo.getMlid());
        values.put(LocalTables.ContactTable.TABLE_COL_FN, contactInfo.getFname());
        values.put(LocalTables.ContactTable.TABLE_COL_LN, contactInfo.getLname());
        values.put(LocalTables.ContactTable.TABLE_COL_HANDLE, contactInfo.getHandle());

        values.put(LocalTables.ContactTable.TABLE_COL_ADDR, contactInfo.getAddress());
        values.put(LocalTables.ContactTable.TABLE_COL_SUITE, contactInfo.getSuite());
        values.put(LocalTables.ContactTable.TABLE_COL_ZIP, contactInfo.getZip());
        values.put(LocalTables.ContactTable.TABLE_COL_STATE, contactInfo.getState());
        values.put(LocalTables.ContactTable.TABLE_COL_CITY, contactInfo.getCity());

        values.put(LocalTables.ContactTable.TABLE_COL_CP, contactInfo.getCp());
        values.put(LocalTables.ContactTable.TABLE_COL_DOB, contactInfo.getDob());
        values.put(LocalTables.ContactTable.TABLE_COL_SHARELOC, contactInfo.getShareloc());

        values.put(LocalTables.ContactTable.TABLE_COL_YOUTUBE, contactInfo.getYoutube());
        values.put(LocalTables.ContactTable.TABLE_COL_FB, contactInfo.getFb());
        values.put(LocalTables.ContactTable.TABLE_COL_TWITTER, contactInfo.getTwitter());
        values.put(LocalTables.ContactTable.TABLE_COL_LINKEDIN, contactInfo.getLinkedin());
        values.put(LocalTables.ContactTable.TABLE_COL_PINTEREST, contactInfo.getPintrest());
        values.put(LocalTables.ContactTable.TABLE_COL_SNAPCHAT, contactInfo.getSnapchat());
        values.put(LocalTables.ContactTable.TABLE_COL_INSTAGRAM, contactInfo.getInstagram());
        values.put(LocalTables.ContactTable.TABLE_COL_WHATSAPP, contactInfo.getWhatsapp());

        values.put(LocalTables.ContactTable.TABLE_COL_CO, contactInfo.getCo());
        values.put(LocalTables.ContactTable.TABLE_COL_TITLE, contactInfo.getTitle());
        values.put(LocalTables.ContactTable.TABLE_COL_WORKADD, contactInfo.getWorkAddr());
        values.put(LocalTables.ContactTable.TABLE_COL_WEBSITE, contactInfo.getWebsite());
        values.put(LocalTables.ContactTable.TABLE_COL_WP, contactInfo.getWp());

        values.put(LocalTables.ContactTable.TABLE_COL_CREATEDATE, contactInfo.getCreateDate());

        // New fields in V3
        values.put(LocalTables.ContactTable.TABLE_COL_PRI, contactInfo.getPri());
        values.put(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER, contactInfo.getLocalDBOwnerMLID());

        values.put(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL, contactInfo.isFriendLevel());
        values.put(LocalTables.ContactTable.TABLE_COL_GENDER, contactInfo.getGender());
        values.put(LocalTables.ContactTable.TABLE_COL_INITIAL, contactInfo.getInitial());
        values.put(LocalTables.ContactTable.TABLE_COL_STREETNUM, contactInfo.getStreetNum());
        values.put(LocalTables.ContactTable.TABLE_COL_STREET, contactInfo.getStreet());
        values.put(LocalTables.ContactTable.TABLE_COL_STE, contactInfo.getSte());
        values.put(LocalTables.ContactTable.TABLE_COL_UTC, contactInfo.getUtc());
        values.put(LocalTables.ContactTable.TABLE_COL_MARITAL, contactInfo.getMarital());

        values.put(LocalTables.ContactTable.TABLE_COL_VERIFIED, contactInfo.getVerified());
        values.put(LocalTables.ContactTable.TABLE_COL_RATING, contactInfo.getRating());
        values.put(LocalTables.ContactTable.TABLE_COL_COA, contactInfo.getCoa());

        values.put(LocalTables.ContactTable.TABLE_COL_PERSONAL, contactInfo.getPersonal());
        values.put(LocalTables.ContactTable.TABLE_COL_BUSINESS, contactInfo.getBusiness());
        values.put(LocalTables.ContactTable.TABLE_COL_FAMILY, contactInfo.getFamily());

        values.put(LocalTables.ContactTable.TABLE_COL_BLOCKED, contactInfo.getBlocked());
        values.put(LocalTables.ContactTable.TABLE_COL_ARCHIVED, contactInfo.getArchived());
        values.put(LocalTables.ContactTable.TABLE_COL_LON, contactInfo.getLon());
        values.put(LocalTables.ContactTable.TABLE_COL_LAT, contactInfo.getLat());

        values.put(LocalTables.ContactTable.TABLE_COL_EDITDATE, contactInfo.getEditDate());
        values.put(LocalTables.ContactTable.TABLE_COL_INDUSTRYID, contactInfo.getIndustryID());

        values.put(LocalTables.ContactTable.TABLE_COL_GROUPIDS, contactInfo.getGroupInfo());
        values.put(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL, contactInfo.getVideoMeetingUrl());

        db.update(LocalTables.ContactTable.TABLE_NAME, values, LocalTables.ContactTable._ID + "=" + contactInfo.getId(), null);
    }

    public ArrayList<ContactInfo> getAlLContacts() {

        ArrayList<ContactInfo> cList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + ";";
        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.moveToNext()) {
            ContactInfo newContact = new ContactInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            newContact.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            newContact.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
            newContact.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            newContact.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            newContact.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            newContact.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
            newContact.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
            newContact.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
            newContact.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
            newContact.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

            newContact.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
            newContact.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
            newContact.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

            newContact.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
            newContact.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
            newContact.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
            newContact.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
            newContact.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
            newContact.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
            newContact.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
            newContact.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

            newContact.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            newContact.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
            newContact.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
            newContact.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
            newContact.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
            newContact.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            // New Fields in V3
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
            newContact.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

            newContact.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
            newContact.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
            newContact.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
            newContact.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
            newContact.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
            newContact.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
            newContact.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
            newContact.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

            newContact.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
            newContact.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
            newContact.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

            newContact.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
            newContact.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
            newContact.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

            newContact.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
            newContact.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
            newContact.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
            newContact.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

            newContact.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
            newContact.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
            newContact.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));

            cList.add(newContact);
        }

        c.close();
        return cList;
    }

    public ArrayList<ContactInfo> getAlLContacts(int priValue) {

        ArrayList<ContactInfo> cList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                LocalTables.ContactTable.TABLE_COL_PRI + "=" + priValue + " ORDER BY " +
                LocalTables.ContactTable.TABLE_COL_PRI + " DESC," +
                LocalTables.ContactTable.TABLE_COL_CO + " ASC," +
                LocalTables.ContactTable.TABLE_COL_LN + " ASC," +
                LocalTables.ContactTable.TABLE_COL_FN + " ASC" +
                ";";
        if (priValue == 0) {
            query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                    LocalTables.ContactTable.TABLE_COL_PRI + ">=" + priValue + " ORDER BY " +
                    LocalTables.ContactTable.TABLE_COL_PRI + " DESC," +
                    LocalTables.ContactTable.TABLE_COL_CO + " ASC," +
                    LocalTables.ContactTable.TABLE_COL_LN + " ASC," +
                    LocalTables.ContactTable.TABLE_COL_FN + " ASC" +
                    ";";
        }

        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.getCount() > 0 && c.moveToNext()) {
            ContactInfo newContact = new ContactInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            newContact.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            newContact.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
            newContact.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            newContact.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            newContact.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            newContact.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
            newContact.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
            newContact.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
            newContact.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
            newContact.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

            newContact.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
            newContact.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
            newContact.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

            newContact.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
            newContact.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
            newContact.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
            newContact.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
            newContact.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
            newContact.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
            newContact.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
            newContact.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

            newContact.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            newContact.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
            newContact.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
            newContact.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
            newContact.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
            newContact.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            // New Fields in V3
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
            newContact.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

            newContact.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
            newContact.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
            newContact.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
            newContact.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
            newContact.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
            newContact.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
            newContact.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
            newContact.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

            newContact.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
            newContact.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
            newContact.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

            newContact.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
            newContact.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
            newContact.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

            newContact.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
            newContact.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
            newContact.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
            newContact.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

            newContact.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
            newContact.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
            newContact.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));

            cList.add(newContact);
        }

        c.close();
        return cList;
    }

    public ArrayList<ContactInfo> getContactsWithPriValue(int priValue) {

        ArrayList<ContactInfo> cList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " +
                LocalTables.ContactTable.TABLE_COL_PRI + "=" + priValue + " ORDER BY " +
                LocalTables.ContactTable.TABLE_COL_PRI + " DESC," +
                LocalTables.ContactTable.TABLE_COL_CO + " ASC," +
                LocalTables.ContactTable.TABLE_COL_LN + " ASC," +
                LocalTables.ContactTable.TABLE_COL_FN + " ASC" +
                ";";

        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.moveToNext()) {
            ContactInfo newContact = new ContactInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            newContact.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            newContact.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
            newContact.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            newContact.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            newContact.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            newContact.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
            newContact.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
            newContact.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
            newContact.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
            newContact.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

            newContact.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
            newContact.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
            newContact.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

            newContact.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
            newContact.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
            newContact.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
            newContact.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
            newContact.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
            newContact.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
            newContact.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
            newContact.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

            newContact.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            newContact.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
            newContact.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
            newContact.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
            newContact.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
            newContact.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            // New Fields in V3
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
            newContact.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

            newContact.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
            newContact.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
            newContact.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
            newContact.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
            newContact.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
            newContact.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
            newContact.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
            newContact.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

            newContact.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
            newContact.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
            newContact.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

            newContact.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
            newContact.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
            newContact.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

            newContact.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
            newContact.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
            newContact.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
            newContact.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

            newContact.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
            newContact.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
            newContact.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));
            cList.add(newContact);
        }

        c.close();
        return cList;
    }

    //adds question to the database
    public void addMessage(Message message) {
        db = getWritableDatabase();

        //create contentvalues
        ContentValues cv = new ContentValues();

        //column name, get the question's data
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_SVRID, message.getType());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_STATUSID, message.getStatusID());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_FROMID, message.getFromID());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_TOID, message.getToID());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_EMPLOYERID, message.getEmployerID());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_MSG, message.getMsg());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_NAME, message.getName());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE, message.getCreateDate());
        cv.put(LocalTables.MessageHistoryTable.TABLE_COL_CHANNEL, message.getChannel());

        //Insert this values to our database
        db.insert(LocalTables.MessageHistoryTable.TABLE_NAME, null, cv);
    }

    public ArrayList<Message> getMessageList(int mlid, int myMlid) {

        boolean isMine = mlid == myMlid;

        ArrayList<Message> qList = new ArrayList<>();
        db = getReadableDatabase();

        if (0 == mlid) {
            // SELECT qix_users.mlid, qix_users.co, qix_users.fn, qix_users.ln, qix_messages.msg, qix_messages.createdate
            // FROM qix_users INNER JOIN qix_messages ON (qix_users.mlid = qix_messages.fromid OR qix_users.mlid = qix_messages.toid)
            // WHERE (qix_users.mlid > 0 AND qix_messages._id = (SELECT MAX(_id) AS Expr1 FROM qix_messages WHERE (qix_messages.fromid = qix_users.mlid OR qix_messages.toid = qix_users.mlid)))

            String query = "SELECT " + LocalTables.ContactTable.TABLE_COL_MLID + ", " + LocalTables.ContactTable.TABLE_COL_CO + ", " + LocalTables.ContactTable.TABLE_COL_FN + ", " +
                    LocalTables.ContactTable.TABLE_COL_LN + ", " + LocalTables.MessageHistoryTable.TABLE_COL_MSG + ", " +
                    LocalTables.MessageHistoryTable.TABLE_NAME + "." + LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE + ", " +
                    LocalTables.MessageHistoryTable.TABLE_COL_FROMID + ", " + LocalTables.MessageHistoryTable.TABLE_COL_TOID + " " +
                    "FROM " + LocalTables.ContactTable.TABLE_NAME + " INNER JOIN " + LocalTables.MessageHistoryTable.TABLE_NAME + " ON (" +
                    LocalTables.ContactTable.TABLE_COL_MLID + " = " + LocalTables.MessageHistoryTable.TABLE_COL_FROMID + " OR " +
                    LocalTables.ContactTable.TABLE_COL_MLID + " = " + LocalTables.MessageHistoryTable.TABLE_COL_TOID + ") WHERE (" +
                    LocalTables.ContactTable.TABLE_COL_MLID + " > 0 AND " + LocalTables.MessageHistoryTable.TABLE_NAME + "." + LocalTables.MessageHistoryTable._ID +
                    " = (SELECT MAX(" + LocalTables.MessageHistoryTable.TABLE_NAME + "." + LocalTables.MessageHistoryTable._ID + ") AS Expr1 FROM " +
                    LocalTables.MessageHistoryTable.TABLE_NAME + " WHERE (" +
                    LocalTables.ContactTable.TABLE_COL_MLID + " = " + LocalTables.MessageHistoryTable.TABLE_COL_FROMID + " OR " +
                    LocalTables.ContactTable.TABLE_COL_MLID + " = " + LocalTables.MessageHistoryTable.TABLE_COL_TOID + ")));";

            Cursor c = db.rawQuery(query, null);
            //if there is an entry
            while (c.moveToNext()) {
                Message m = new Message();

                int msgMLID = c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID));
                String co = c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO));
                String fn = c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN));
                String ln = c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN));
                String msg = c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_MSG));
                String createDate = c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE));
                int fromID = c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_FROMID));
                int toID = c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_TOID));

                String name = "";
                if (TextUtils.isEmpty(co)) {
                    name = String.format("%s %s", fn, ln).trim();
                } else {
                    name = String.format("%s, %s %s", co, fn, ln).trim();
                }

                m.setMLID(msgMLID);
                m.setFromID(fromID);
                m.setToID(toID);
                m.setMsg(msg);
                m.setName(name);
                m.setCreateDate(createDate);
                qList.add(m);
            }

            c.close();
        } else {
            String query = "";
            if (isMine) {
                query = "SELECT * FROM " + LocalTables.MessageHistoryTable.TABLE_NAME + " WHERE " +
                        LocalTables.MessageHistoryTable.TABLE_COL_FROMID + " = '" + mlid + "' AND " +
                        //LocalTables.MessageHistoryTable.TABLE_COL_TOID + " = '0' OR " +
                        LocalTables.MessageHistoryTable.TABLE_COL_TOID + " = '" + mlid + "';";
            } else {
                query = "SELECT * FROM " + LocalTables.MessageHistoryTable.TABLE_NAME + " WHERE " +
                        LocalTables.MessageHistoryTable.TABLE_COL_FROMID + " = '" + mlid + "' OR " +
                        //LocalTables.MessageHistoryTable.TABLE_COL_TOID + " = '0' OR " +
                        LocalTables.MessageHistoryTable.TABLE_COL_TOID + " = '" + mlid + "';";
            }

            Cursor c = db.rawQuery(query, null);
            //if there is an entry
            while (c.moveToNext()) {
                Message m = new Message();
                m.setType(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_SVRID)));
                m.setStatusID(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_STATUSID)));
                m.setFromID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_FROMID)));
                m.setToID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_TOID)));
                m.setEmployerID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_EMPLOYERID)));
                m.setMsg(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_MSG)));
                m.setName(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_NAME)));
                m.setCreateDate(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE)));
                qList.add(m);
            }

            c.close();
        }

        return qList;
    }

    public ContactInfo getContactInfoBySenderId(int id) {

        ContactInfo contactInfo = new ContactInfo();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " + LocalTables.ContactTable.TABLE_COL_MLID + " = " + id + ";";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry
        while (c.moveToNext()) {
            ContactInfo newContact = new ContactInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            newContact.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            newContact.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
            newContact.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            newContact.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            newContact.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            newContact.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
            newContact.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
            newContact.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
            newContact.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
            newContact.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

            newContact.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
            newContact.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
            newContact.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

            newContact.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
            newContact.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
            newContact.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
            newContact.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
            newContact.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
            newContact.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
            newContact.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
            newContact.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

            newContact.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            newContact.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
            newContact.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
            newContact.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
            newContact.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
            newContact.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            // New Fields in V3
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
            newContact.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

            newContact.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
            newContact.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
            newContact.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
            newContact.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
            newContact.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
            newContact.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
            newContact.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
            newContact.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

            newContact.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
            newContact.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
            newContact.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

            newContact.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
            newContact.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
            newContact.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

            newContact.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
            newContact.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
            newContact.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
            newContact.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

            newContact.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
            newContact.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
            newContact.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));
            contactInfo = newContact;
        }

        c.close();
        return contactInfo;
    }

    public ContactInfo getContactInfoById(int id) {

        ContactInfo contactInfo = new ContactInfo();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " + LocalTables.ContactTable._ID + " = " + id + ";";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry
        while (c.moveToNext()) {
            ContactInfo newContact = new ContactInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            newContact.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            newContact.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));
            newContact.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            newContact.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            newContact.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            newContact.setAddress(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ADDR)));
            newContact.setSuite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SUITE)));
            newContact.setZip(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ZIP)));
            newContact.setState(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STATE)));
            newContact.setCity(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CITY)));

            newContact.setCp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CP)));
            newContact.setDob(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_DOB)));
            newContact.setShareloc(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SHARELOC)));

            newContact.setYoutube(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_YOUTUBE)));
            newContact.setFb(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FB)));
            newContact.setTwitter(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TWITTER)));
            newContact.setLinkedin(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LINKEDIN)));
            newContact.setPintrest(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PINTEREST)));
            newContact.setSnapchat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_SNAPCHAT)));
            newContact.setInstagram(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INSTAGRAM)));
            newContact.setWhatsapp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WHATSAPP)));

            newContact.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            newContact.setTitle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_TITLE)));
            newContact.setWorkAddr(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WORKADD)));
            newContact.setWebsite(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WEBSITE)));
            newContact.setWp(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_WP)));
            newContact.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            // New Fields in V3
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PRI)));
            newContact.setLocalDBOwnerMLID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LOCALDBOWNER)));

            newContact.setFriendLevel(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FRIENDLEVEL)) > 0);
            newContact.setGender(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GENDER)));
            newContact.setInitial(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INITIAL)));
            newContact.setStreetNum(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREETNUM)));
            newContact.setStreet(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STREET)));
            newContact.setSte(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_STE)));
            newContact.setUtc(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_UTC)));
            newContact.setMarital(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MARITAL)));

            newContact.setVerified(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VERIFIED)));
            newContact.setRating(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_RATING)));
            newContact.setCoa(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_COA)));

            newContact.setPersonal(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_PERSONAL)));
            newContact.setBusiness(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BUSINESS)));
            newContact.setFamily(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FAMILY)));

            newContact.setBlocked(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_BLOCKED)));
            newContact.setArchived(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_ARCHIVED)));
            newContact.setLon(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LON)));
            newContact.setLat(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LAT)));

            newContact.setEditDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EDITDATE)));
            newContact.setIndustryID(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_INDUSTRYID)));
            newContact.setVideoMeetingUrl(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_VIDEO_MEETING_URL)));
            contactInfo = newContact;
        }

        c.close();
        return contactInfo;
    }

    public ArrayList<Message> getAllMessages() {

        ArrayList<Message> qList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.MessageHistoryTable.TABLE_NAME + ";";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry
        while (c.moveToNext()) {
            Message m = new Message();
            m.setType(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_SVRID)));
            m.setStatusID(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_STATUSID)));
            m.setFromID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_FROMID)));
            m.setToID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_TOID)));
            m.setEmployerID(c.getInt(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_EMPLOYERID)));
            m.setMsg(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_MSG)));
            m.setName(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_NAME)));
            m.setCreateDate(c.getString(c.getColumnIndex(LocalTables.MessageHistoryTable.TABLE_COL_CREATEDATE)));
            qList.add(m);
        }

        c.close();
        return qList;
    }

    public ArrayList<ContactInfo> getContacts(int groupID) {

        ArrayList<ContactInfo> qList = new ArrayList<>();
        db = getReadableDatabase();

        String groupInfo = "" + groupID + "";
        String query = "SELECT * FROM " + LocalTables.ContactTable.TABLE_NAME + " WHERE " + LocalTables.ContactTable.TABLE_COL_GROUPIDS + " LIKE '%" + groupInfo + "%';";
        Cursor c = db.rawQuery(query, null);
        //if there is an entry

        while (c.moveToNext()) {
            ContactInfo m = new ContactInfo();

            m.setId(c.getInt(c.getColumnIndex(LocalTables.ContactTable._ID)));
            m.setMlid(c.getInt(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_MLID)));

            m.setEmail(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_EMAIL)));
            m.setCo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CO)));
            m.setFname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_FN)));
            m.setLname(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_LN)));
            m.setHandle(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_HANDLE)));

            m.setGroupInfo(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_GROUPIDS)));

            m.setCreateDate(c.getString(c.getColumnIndex(LocalTables.ContactTable.TABLE_COL_CREATEDATE)));

            qList.add(m);
        }

        c.close();
        return qList;
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void addGroup(GroupInfo groupInfo) {

        // Get Max Value
        int newPri = 101;
        int newSort = 99;

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();

        String query = "SELECT MAX(" + LocalTables.GroupTable.TABLE_COL_PRI + ") FROM " + LocalTables.GroupTable.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            newPri = c.getInt(0) + 1;
        }

        // todo:  assign sort value
        String sortQuery = "SELECT MAX(" + LocalTables.GroupTable.TABLE_COL_SORTBY + ") FROM " + LocalTables.GroupTable.TABLE_NAME;
        Cursor c2 = db.rawQuery(sortQuery, null);
        if (c2 != null && c2.getCount() > 0) {
            c2.moveToFirst();
            newSort = c2.getInt(0) + 1;
        }


        ContentValues values = new ContentValues();
        values.put(LocalTables.GroupTable.TABLE_COL_GRPNAME, groupInfo.getGrpname());
        values.put(LocalTables.GroupTable.TABLE_COL_PRI, newPri);
        values.put(LocalTables.GroupTable.TABLE_COL_SORTBY, newSort);
        values.put(LocalTables.GroupTable.TABLE_COL_CREATEDAT, new Date().getTime());
        values.put(LocalTables.GroupTable.TABLE_COL_MORE, groupInfo.getMore());

        long insertId = db.insert(LocalTables.GroupTable.TABLE_NAME, null,
                values);

        groupInfo.setId((int) insertId);
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void removeGroup(GroupInfo groupInfo) {
        db = getWritableDatabase();
        db.delete(LocalTables.GroupTable.TABLE_NAME, LocalTables.GroupTable._ID
                + " = " + groupInfo.getId(), null);
    }

    public void updateGroup(GroupInfo groupInfo) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalTables.GroupTable.TABLE_COL_GRPNAME, groupInfo.getGrpname());
        values.put(LocalTables.GroupTable.TABLE_COL_PRI, groupInfo.getPri());
        values.put(LocalTables.GroupTable.TABLE_COL_SORTBY, groupInfo.getSortby());
        values.put(LocalTables.GroupTable.TABLE_COL_CREATEDAT, groupInfo.getCreatedAt());
        values.put(LocalTables.GroupTable.TABLE_COL_MORE, groupInfo.getMore());

        db.update(LocalTables.GroupTable.TABLE_NAME, values, LocalTables.GroupTable._ID + "=" + groupInfo.getId(), null);
    }

    public ArrayList<GroupInfo> getAlLGroups() {

        ArrayList<GroupInfo> gList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.GroupTable.TABLE_NAME + " ORDER BY " + LocalTables.GroupTable.TABLE_COL_PRI + " DESC;";
        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.moveToNext()) {
            GroupInfo newContact = new GroupInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.GroupTable._ID)));
            newContact.setGrpname(c.getString(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_GRPNAME)));
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_PRI)));
            newContact.setSortby(c.getInt(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_SORTBY)));
            newContact.setCreatedAt(c.getLong(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_CREATEDAT)));
            newContact.setMore(c.getString(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_MORE)));

            if (newContact.getGrpname().equals("None")) {
                newContact.setGrpname("All");
            }

            gList.add(newContact);
        }

        c.close();
        return gList;
    }

    public ArrayList<GroupInfo> getAlLUserGroups() {

        ArrayList<GroupInfo> gList = new ArrayList<>();
        db = getReadableDatabase();

        String query = "SELECT * FROM " + LocalTables.GroupTable.TABLE_NAME + " WHERE " + LocalTables.GroupTable._ID + " > 5" + " ORDER BY " + LocalTables.GroupTable.TABLE_COL_PRI + " ASC;";
        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.moveToNext()) {
            GroupInfo newContact = new GroupInfo();
            newContact.setId(c.getInt(c.getColumnIndex(LocalTables.GroupTable._ID)));
            newContact.setGrpname(c.getString(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_GRPNAME)));
            newContact.setPri(c.getInt(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_PRI)));
            newContact.setSortby(c.getInt(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_SORTBY)));
            newContact.setCreatedAt(c.getLong(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_CREATEDAT)));
            newContact.setMore(c.getString(c.getColumnIndex(LocalTables.GroupTable.TABLE_COL_MORE)));

            gList.add(newContact);
        }

        c.close();
        return gList;
    }

    public ArrayList<CallHistory> getAlLHistory(int id) {

        ArrayList<CallHistory> gList = new ArrayList<>();
        db = getReadableDatabase();

        // "SELECT * FROM " + LocalTables.CallLogTable.TABLE_NAME + " ORDER BY " + LocalTables.CallLogTable.TABLE_COL_CREATEDATE + " DESC;";
        String query = "SELECT a._id, a.ldbid, a.phone, a.statusid, a.inout, a.callsecs, a.createdate, b.fn, b.ln, b.co FROM qix_calllogs a LEFT OUTER JOIN qix_users b ON a.ldbid=b._id GROUP BY a.createdate, a.phone ORDER BY a.createdate DESC;";

        if (id > 0) {
            //query = "SELECT * FROM " + LocalTables.CallLogTable.TABLE_NAME + " WHERE " +
            //        LocalTables.CallLogTable.TABLE_COL_LDBID + " = " + id + " ORDER BY " + LocalTables.CallLogTable.TABLE_COL_CREATEDATE + " DESC;";

            query = "SELECT a._id, a.ldbid, a.phone, a.statusid, a.inout, a.callsecs, a.createdate, b.fn, b.ln, b.co FROM qix_calllogs a LEFT OUTER JOIN qix_users b ON a.ldbid=b._id WHERE a.ldbid=" + id + " GROUP BY a.createdate, a.phone ORDER BY a.createdate DESC;";
        }

        Cursor c = db.rawQuery(query, null);

        //if there is an entry, add this to the list
        while (c.moveToNext()) {
            CallHistory newContact = new CallHistory();
            newContact.setId(c.getInt(0));
            newContact.setLdbid(c.getInt(1));
            newContact.setPhNumber(c.getString(2));
            newContact.setStatusID(c.getInt(3));
            newContact.setCallType(c.getInt(4));
            newContact.setCallDuration(c.getInt(5));
            Date dateCreated = new Date(c.getLong(6));
            newContact.setCallDate(dateCreated);

            String fn = c.getString(7);
            if (fn == null) fn = "";

            String ln = c.getString(8);
            if (ln == null) ln = "";

            String co = c.getString(9);
            if (co == null) co = "";

            if (co.length() > 5) co = co.substring(0, 5);

            newContact.setName(String.format("%s %s %s", co, fn.trim(), ln.trim()).trim());

            gList.add(newContact);
        }

        c.close();
        return gList;
    }

    //INSERT A USERNAME TO THE USER TABLE
    public void addCallHistory(CallHistory callInfo) {

        //TO OPEN THE DATABASE AND WRITE TO IT
        db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocalTables.CallLogTable.TABLE_COL_LDBID, callInfo.getLdbid());
        values.put(LocalTables.CallLogTable.TABLE_COL_PHONE, callInfo.getPhNumber());
        values.put(LocalTables.CallLogTable.TABLE_COL_CALLSECS, callInfo.getCallDuration());
        values.put(LocalTables.CallLogTable.TABLE_COL_INOUT, callInfo.getCallType());
        values.put(LocalTables.CallLogTable.TABLE_COL_STATUSID, callInfo.getStatusID());
        values.put(LocalTables.CallLogTable.TABLE_COL_CREATEDATE, callInfo.getCallDate().getTime());

        long insertId = db.insert(LocalTables.CallLogTable.TABLE_NAME, null, values);

        callInfo.setId((int) insertId);
    }

    // INSERT A USERNAME TO THE USER TABLE
    public void removeGroup(CallHistory callInfo) {
        db = getWritableDatabase();
        db.delete(LocalTables.CallLogTable.TABLE_NAME, LocalTables.CallLogTable._ID
                + " = " + callInfo.getId(), null);
    }
}