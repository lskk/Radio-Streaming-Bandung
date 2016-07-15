package startup.pptik.itb.ac.id.streamingradiobandung.databases;

/**
 * Created by hynra on 5/10/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBListRadio {
    public static final String TAG = "Table List Radio";

    public static final String TABLE_LIST_RADIO = "list_radio";

    public static final String COL_ID = "id_";
    public static final String COL_RADIO_NAME = "radio_name";
    public static final String COL_URL_STREAMING = "url_stream";
    public static final String COL_FREQ = "freq";
    public static final String COL_THUMB = "thumb";
    public static final String COL_CITY = "city";
    private SQLiteDatabase db;
    private DBLocalHelper dbHelper;

    public static final String INITIAL_CREATE = "create table " + TABLE_LIST_RADIO
            + "(" + COL_ID + " integer primary key autoincrement,"
            + COL_URL_STREAMING + " varchar(260) not null,"
            + COL_FREQ + " varchar(30),"
            + COL_THUMB + " varchar(30),"
            + COL_CITY + " varchar(30),"
            + COL_RADIO_NAME + " varchar(30));";

    public TBListRadio(Context context) {
        this.dbHelper = new DBLocalHelper(context);
    }

    public void open() {
        Log.w(TAG, "Open database connection...");
        this.db = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.w(TAG, "Close database connection...");
        this.dbHelper.close();
    }

    public long insert(ContentValues values) {
        Log.w(TAG, "inserting to table account");
        Long result = db.insert(TABLE_LIST_RADIO, null, values);
        if (result != -1) {
            Log.i(TAG, "inserting to table account succeed");
        } else {
            Log.e(TAG, "inserting to table account failed");
        }

        return result;
    }

    public int delete(String uname) {
        return db.delete(TABLE_LIST_RADIO, COL_RADIO_NAME + "=?",
                new String[] { uname });
    }

    public int update(String tID, ContentValues values) {
        Log.w(TAG, "updating to table account");
        if (tID == null) {
            Log.e(TAG, "updating to table account failed, no ID");
            return 0;
        }
        int result = db.update(TABLE_LIST_RADIO, values, COL_ID + "=?",
                new String[]{tID});
        if (result > 0) {
            Log.i(TAG, "updating to "+TAG+" succeed");
        } else {
            Log.e(TAG, "updating to "+TAG+" failed");
        }
        return result;
    }

    public int delete() {
        Log.w(TAG, "Deleting " + TAG);

        int result = db.delete(TABLE_LIST_RADIO, null, null);

        if (result > 0) {
            Log.i(TAG, "deleting " + TAG + " succeed");
        } else {
            Log.e(TAG, "deleting " + TAG + " failed");
        }

        return result;
    }


    public Cursor getList(){
        Log.i(TAG, "Get list Radio Baandung");
        String[] projection = new String[] {COL_ID, COL_RADIO_NAME, COL_URL_STREAMING, COL_FREQ, COL_THUMB, COL_CITY};

        Cursor c = db.query(TABLE_LIST_RADIO, projection, COL_CITY + "=?", new String[] { "Bandung" }, null, null, null);

        if (c.moveToFirst()) {
            Log.i(TAG, "Get all "+TAG);
            return c;
        }

        Log.i(TAG, "No data found");
        return null;
    }
}