package hawaiiappbuilders.omniversapp.localdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class HistorySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "historylocations.db";
    private static final int DATABASE_VERSION = 2;

    // User Table : user(_id, name)
    public static final String TABLE_HISTORY = "locations";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_ZIP = "zip";
    public static final String COLUMN_STREET_ADDRESS = "street_address";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_FULL_ADDRESS = "fullAddress";

    // Database creation sql statement for user table
    private static final String DATABASE_CREATE_HISTORY_TABLE = "create table "
            + TABLE_HISTORY +
            "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text , "
            + COLUMN_TIME + " text , "
            + COLUMN_LAT + " real , "
            + COLUMN_LON + " real , "
            + COLUMN_ZIP + " text , "
            + COLUMN_STREET_ADDRESS + " integer ,"
            + COLUMN_CITY + " integer ,"
            + COLUMN_STATE + " integer ,"
            + COLUMN_FULL_ADDRESS + " text "
            + " );";


    public HistorySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_HISTORY_TABLE);
    }

    // This is automatically called when user change the table structure and versioncode
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ContactsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    private static boolean popMode = false;
    /**
     * Copies your database from your local folder to the just external storage
     * area where user can handle handled. This is done by transfering
     * bytestream.
     */
    public static void popUpDataBase() throws IOException {

        if (!popMode)
            return;

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/com.ver1.qix/databases/" + DATABASE_NAME;
                String backupDBPath = DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.w("Settings Backup", e);
        }
    }
}
