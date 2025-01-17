package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDB {
    private final DBHelper helper;
    private SQLiteDatabase db;

    public MyDB(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public long Insert(Note note) {
        ContentValues cv = new ContentValues();
        cv.put("TITLE", note.getTitle());
        cv.put("CONTENT", note.getContent());
        return db.insert("NOTES", null, cv);
    }

    public int Update(int id, String title, String content) {
        ContentValues cv = new ContentValues();
//        cv.put("ID", id); do not add the attribute which identifies the row to update
        cv.put("TITLE", title);
        cv.put("CONTENT", content);
        String args[] = {String.valueOf(id)};
        return db.update("NOTES", cv, "ID = ?", args);
    }

    public int Delete(int id) {
        String args[] = {String.valueOf(id)};
        return db.delete("NOTES", "ID = ?", args);
    }

    public Cursor SelectAll() {
        Cursor c = null;
        if (db != null) {
            String query = "SELECT * FROM NOTES";
            c = db.rawQuery(query, null);
        }
        return c;
    }

}

