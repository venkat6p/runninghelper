package com.blogspot.stintech.runninghelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

//This class handles the database entries
public class DbAdapter {

    public static final String TABLE_NAME = "runningdetails";

    public static final String COL_PK = "_id";
    public static final String COL_DATE = "Date";
   // public static final String COL_LOC = "location";
    public static final String COL_TIME = "time";
    public static final String DB_NAME = "birds.db";
    public static final int VERSION = 2;

    //for creating table
    public static final String CREATE_TABLE =
            "create table " + TABLE_NAME +
                    "(" + COL_PK + " integer primary key autoincrement, " +
                    COL_DATE + " text, " +

                    COL_TIME + " text)";

    final Context CONTEXT;

    SQLiteDatabase db;
    MySQLiteHelper dbHelper;

    //constructor
    public DbAdapter(Context context) {
        this.CONTEXT = context;
        dbHelper = new MySQLiteHelper(CONTEXT, DB_NAME, null, VERSION);
    }

    //opening adapter
    public DbAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }


    //close adapter
    public void close() {
        dbHelper.close();
    }

    //get all entries
    public Cursor getAll() {
        return db.query(TABLE_NAME, new String[] { COL_PK, COL_DATE, COL_TIME }, null, null, null, null, COL_DATE);
    }

    //get one entry
    public Cursor getOne(long rowID) {
        Cursor c = db.query(true, TABLE_NAME, new String[] {COL_PK,COL_DATE, COL_TIME}, COL_PK + " = " + rowID, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    //to insert one entry
    public void insertOne(String n, String t)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, n);
       // cv.put(COL_LOC, l);
        cv.put(COL_TIME, t);
        db.insert(TABLE_NAME, null, cv);
    }

    //to update one entry
    public void updateOne(int id, String n, String t)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, n);

        cv.put(COL_TIME, t);
        db.update(TABLE_NAME, cv,COL_PK+"=" + id, null);

    }

    //delete one entry
    public boolean deleteOne(long id) {
        return db.delete(TABLE_NAME, COL_PK + " = " + id,null) > 0;
    }


    //MySqlite helper
    private static class MySQLiteHelper extends SQLiteOpenHelper {

        public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
