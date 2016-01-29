package com.example.utsav.schooldemo.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/3/2016.
 */
public class SubsDB extends SQLiteOpenHelper {

    private static final String TAG = NoticeDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Subs";

    // Login table name
    private static final String TABLE_SUBSCRIPTIONS = "subs";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TTTLE = "title";

    public SubsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_SUBSCRIPTIONS +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_TTTLE + " TEXT"+ ")";
        db.execSQL(CREATE_NOTICE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addSubs(String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TTTLE, title); // Title
        // Inserting Row
        long id = db.insert(TABLE_SUBSCRIPTIONS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New subs inserted into sqlite: " + id);
    }
    public void deleteSubs(String title){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TTTLE, title); // Title
        // Inserting Row
        String whereClause = KEY_TTTLE + "=?";
        String[] whereArgs = new String[] { String.valueOf(title) };
        long id = db.delete(TABLE_SUBSCRIPTIONS, whereClause, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "subs deleted into sqlite: " + id);
    }
    /**
     * Getting user data from database
     * */
    public List<String> getSubsList() {
        List<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_SUBSCRIPTIONS, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_TTTLE)));

                    res.moveToNext();
        }
        return array_list;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteClients() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_SUBSCRIPTIONS, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}