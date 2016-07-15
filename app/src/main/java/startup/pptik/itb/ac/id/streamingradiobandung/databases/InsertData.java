package startup.pptik.itb.ac.id.streamingradiobandung.databases;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by hynra on 5/10/2016.
 */
public class InsertData {
    public static void insertTBList(Context context, String radioName, String radioFreq, String radioUrl, String radioThumb, String city){
        TBListRadio tbListRadio = new TBListRadio(context);
        tbListRadio.open();
        ContentValues values = new ContentValues();
        values.put(TBListRadio.COL_RADIO_NAME, radioName);
        values.put(TBListRadio.COL_FREQ, radioFreq);
        values.put(TBListRadio.COL_URL_STREAMING, radioUrl);
        values.put(TBListRadio.COL_THUMB, radioThumb);
        values.put(TBListRadio.COL_CITY, city);
        tbListRadio.insert(values);
        tbListRadio.close();
    }
}
