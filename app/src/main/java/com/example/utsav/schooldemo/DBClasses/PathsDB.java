package com.example.utsav.schooldemo.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by utsav on 1/19/2016.
 */
public class PathsDB extends SQLiteOpenHelper {

    private static final String TAG = PathsDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "paths";

    // Login table name
    private static final String TABLE_NOTICES = "downloads_path";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_ID = "fileid";
    private static final String KEY_PATH = "path";

    public PathsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_FILE_ID + " TEXT,"
                        + KEY_PATH + " TEXT"
                        + ")";
        db.execSQL(CREATE_NOTICE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICES);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addPath(int _id, String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_ID, _id); // Title
        values.put(KEY_PATH, path);
        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New record inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NOTICES, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void updatePath(String path, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PATH, path); //These Fields should be your String values of actual column names
        db.update(TABLE_NOTICES, cv, "fileid=" + id, null);

        Log.d(TAG, "path updated for:" + id);
    }

    public String getPath(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTICES + " where fileid=" + id, null);
        Log.d(TAG, "returned updated path");
        res.moveToFirst();
        String path = res.getString(res.getColumnIndex(KEY_PATH));
        return path;
    }
}
