package com.example.utsav.schooldemo.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.utsav.schooldemo.DataClasses.ContactsData;
import com.example.utsav.schooldemo.DataClasses.NoticeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/28/2016.
 */
public class ContactsDB extends SQLiteOpenHelper {

    private static final String TAG = NoticeDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Contacts";

    // Login table name
    private static final String TABLE_NOTICES = "Contact";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "title";
    private static final String KEY_DESIGNATION = "message";
    private static final String KEY_EMAIL = "month";
    private static final String KEY_PHONE = "day";

    public ContactsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_NAME + " TEXT,"
                        + KEY_DESIGNATION + " TEXT,"                //in case of weird error change this data type
                        + KEY_EMAIL + " TEXT,"
                        + KEY_PHONE + " TEXT" + ")";
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
    public void addContacts(String name, String designation, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DESIGNATION, designation);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);

        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New contacts inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public List<ContactsData> getClientList() {
        List<ContactsData> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NOTICES, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new ContactsData(res.getString(res.getColumnIndex(KEY_NAME)),
                    res.getString(res.getColumnIndex(KEY_DESIGNATION)),
                    res.getString(res.getColumnIndex(KEY_EMAIL)),
                    res.getString(res.getColumnIndex(KEY_PHONE))));
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

        Log.d(TAG, "Deleted all contacts info from sqlite");
    }

}