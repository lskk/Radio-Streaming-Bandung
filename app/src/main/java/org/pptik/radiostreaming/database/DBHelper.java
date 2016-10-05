package org.pptik.radiostreaming.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "radiolist.db";
    
    private static final int DATABASE_VERSION = 1;
    
    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public DBHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table if not exists radiolist(_id integer primary key autoincrement,"
            + "name varchar(30),path varchar(80),info boolean)");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("alter table classinfo add column other string");
    }
    
}
