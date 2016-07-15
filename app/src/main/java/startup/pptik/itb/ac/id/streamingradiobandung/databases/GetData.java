package startup.pptik.itb.ac.id.streamingradiobandung.databases;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by hynra on 5/10/2016.
 */
public class GetData {

    // get radioname
    public String[] getRadioNameList(Context context){
        String operators[] = {"No Data"};
        TBListRadio tbo = new TBListRadio(context);
        tbo.open();
        Cursor tCursor = tbo.getList();
        if (tCursor != null) {
            operators = new String[tCursor.getCount()];
            for(int idx=0; idx<tCursor.getCount(); idx++){
                String op = tCursor.getString(tCursor.getColumnIndex(TBListRadio.COL_RADIO_NAME)).toUpperCase();
                operators[idx] = op;
                tCursor.moveToNext();
            }
            tCursor.close();
        }
        tbo.close();
        return operators;
    }

    // get radiO freq
    public String[] getRadioFreqList(Context context){
        String operators[] = {"No Data"};
        TBListRadio tbo = new TBListRadio(context);
        tbo.open();
        Cursor tCursor = tbo.getList();
        if (tCursor != null) {
            operators = new String[tCursor.getCount()];
            for(int idx=0; idx<tCursor.getCount(); idx++){
                String op = tCursor.getString(tCursor.getColumnIndex(TBListRadio.COL_FREQ)) + " FM";
                operators[idx] = op;
                tCursor.moveToNext();
            }
            tCursor.close();
        }
        tbo.close();
        return operators;
    }

    // get radioname
    public String[] getRadioUrlList(Context context){
        String operators[] = {"No Data"};
        TBListRadio tbo = new TBListRadio(context);
        tbo.open();
        Cursor tCursor = tbo.getList();
        if (tCursor != null) {
            operators = new String[tCursor.getCount()];
            for(int idx=0; idx<tCursor.getCount(); idx++){
                String op = tCursor.getString(tCursor.getColumnIndex(TBListRadio.COL_URL_STREAMING));
                operators[idx] = op;
                tCursor.moveToNext();
            }
            tCursor.close();
        }
        tbo.close();
        return operators;
    }


    // get thumb
    public String[] getRadioThumbList(Context context){
        String operators[] = {"No Data"};
        TBListRadio tbo = new TBListRadio(context);
        tbo.open();
        Cursor tCursor = tbo.getList();
        if (tCursor != null) {
            operators = new String[tCursor.getCount()];
            for(int idx=0; idx<tCursor.getCount(); idx++){
                String op = tCursor.getString(tCursor.getColumnIndex(TBListRadio.COL_THUMB));
                operators[idx] = op;
                tCursor.moveToNext();
            }
            tCursor.close();
        }
        tbo.close();
        return operators;
    }
}
