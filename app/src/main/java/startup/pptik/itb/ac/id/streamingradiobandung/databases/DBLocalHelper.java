package startup.pptik.itb.ac.id.streamingradiobandung.databases;

/**
 * Created by hynra on 5/10/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBLocalHelper extends SQLiteOpenHelper {

    private String TAG = "Local DB";

    private static final String DATABASE_NAME = "pptik_radio.sql";
    private static final int DATABASE_VERSION = 1;

    private static DBLocalHelper mInstance;

    public DBLocalHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBLocalHelper getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new DBLocalHelper(context);
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating database " +DATABASE_NAME);
        Log.i(TAG, "Creating table " + TBListRadio.TABLE_LIST_RADIO);
        db.execSQL(TBListRadio.INITIAL_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        onCreate(db);
    }

}