package com.example.utsav.schooldemo.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.utsav.schooldemo.DataClasses.AboutsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 2/16/2016.
 */
public class AboutsDB extends SQLiteOpenHelper {

    private static final String TAG = NoticeDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Abouts";

    // Login table name
    private static final String TABLE_NOTICES = "about";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "title";
    private static final String KEY_EMAIL = "month";
    private static final String KEY_CONTACTS = "contacts";
    private static final String KEY_ABOUTS = "abouts";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    public AboutsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_NAME + " TEXT,"
                        + KEY_EMAIL + " TEXT,"                //in case of weird error change this data type
                        + KEY_CONTACTS + " TEXT,"
                        + KEY_ABOUTS + " TEXT,"
                        + KEY_WEBSITE + " TEXT,"
                        + KEY_LONGITUDE + " TEXT,"
                        + KEY_LATITUDE + " TEXT" + ")";
        db.execSQL(CREATE_NOTICE_TABLE);

       // Log.d(TAG, "Database tables created");
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
    public void addData(String name, String email, String contacts,
                            String abouts, String website, String longitude,
                            String latitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CONTACTS, contacts);
        values.put(KEY_ABOUTS, abouts);
        values.put(KEY_WEBSITE, website);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_LATITUDE, latitude);

        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

       // Log.d(TAG, "New contacts inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public List<AboutsData> getClientList() {
        List<AboutsData> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NOTICES, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new AboutsData(res.getString(res.getColumnIndex(KEY_NAME)),
                    res.getString(res.getColumnIndex(KEY_EMAIL)),
                    res.getString(res.getColumnIndex(KEY_CONTACTS)),
                    res.getString(res.getColumnIndex(KEY_ABOUTS)),
                    res.getString(res.getColumnIndex(KEY_WEBSITE)),
                    res.getString(res.getColumnIndex(KEY_LONGITUDE)),
                    res.getString(res.getColumnIndex(KEY_LATITUDE))));
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
        db.delete(TABLE_NOTICES, null, null);
        db.close();

       // Log.d(TAG, "Deleted all contacts info from sqlite");
    }

}