package com.example.utsav.schooldemo.DBClasses;

/**
 * Created by utsav on 1/2/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.utsav.schooldemo.DataClasses.ClientsData;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SchoolDemo";

    // Login table name
    private static final String TABLE_CLIENT = "clientList";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_PASS = "pass";
    private static final String KEY_CID = "cid";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_CLIENT +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PASS + " TEXT,"
                + KEY_CID + " TEXT,"
                + KEY_URL + " TEXT"+
                ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        //Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addClient(String name, String url, String pass, String cid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_URL, url); // Name
        values.put(KEY_PASS, pass); // Name
        values.put(KEY_CID, cid); // Name

        // Inserting Row
        long id = db.insert(TABLE_CLIENT, null, values);
        db.close(); // Closing database connection

        //Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public List<ClientsData> getClientList() {
        List<ClientsData> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_CLIENT, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new ClientsData(res.getString(res.getColumnIndex(KEY_NAME)),
                    res.getString(res.getColumnIndex(KEY_URL)),
                    res.getString(res.getColumnIndex(KEY_CID)),
                    res.getString(res.getColumnIndex(KEY_PASS))));
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
        db.delete(TABLE_CLIENT, null, null);
        db.close();

       // Log.d(TAG, "Deleted all user info from sqlite");
    }

}