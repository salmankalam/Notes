package com.example.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "mynotes.db";
    public static final int VERSION = 1;

    public DBHelper (Context context) {
        super(context, DBNAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd = "CREATE TABLE IF NOT EXISTS NOTES (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, CONTENT TEXT)";
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
