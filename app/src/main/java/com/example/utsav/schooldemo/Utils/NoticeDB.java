package com.example.utsav.schooldemo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/2/2016.
 */
public class NoticeDB extends SQLiteOpenHelper {

    private static final String TAG = NoticeDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Notice";

    // Login table name
    private static final String TABLE_NOTICES = "notices";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TTTLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_YEAR = "year";

    public NoticeDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                            + KEY_TTTLE + " TEXT,"
                            + KEY_MESSAGE + " TEXT,"                //in case of weird error change this data type
                            + KEY_MONTH + " TEXT,"
                            + KEY_DAY + " TEXT,"
                            + KEY_YEAR + " TEXT" + ")";
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
     * */
    public void addNotice(String title, String message, String month, String day, String year) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TTTLE, title); // Title
        values.put(KEY_MESSAGE, message); // message
        values.put(KEY_MONTH, month); // month
        values.put(KEY_DAY, day); // day
        values.put(KEY_YEAR, year); // year
        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public List<String> getClientList() {
        List<String> client = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                client.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + client.toString());

        return client;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteClients() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NOTICES, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}