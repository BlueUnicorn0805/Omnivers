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

public class ContactsSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 1;

    // User Table : user(_id, name)
    public static final String TABLE_USER = "user";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FNAME = "fname";
    public static final String COLUMN_LNAME = "lname";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PHONEMETA = "meta";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_MLID = "mlid";

    // Database creation sql statement for user table
    private static final String DATABASE_CREATE_USER_TABLE = "create table "
            + TABLE_USER +
            "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FNAME + " text , "
            + COLUMN_LNAME + " text , "
            + COLUMN_PHONE + " text , "
            + COLUMN_PHONEMETA + " text , "
            + COLUMN_EMAIL + " text , "
            + COLUMN_MLID + " integer"
            + " );";


    public ContactsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_USER_TABLE);
    }

    // This is automatically called when user change the table structure and versioncode
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ContactsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
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
