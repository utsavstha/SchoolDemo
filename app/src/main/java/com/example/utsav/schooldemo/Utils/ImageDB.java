package com.example.utsav.schooldemo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.utsav.schooldemo.DownloadData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/20/2016.
 */
public class ImageDB  extends SQLiteOpenHelper {

    private static final String TAG = DownloadsDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "imaage";

    // Login table name
    private static final String TABLE_NOTICES = "imagelist";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LINK = "link";

    public ImageDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"

                        + KEY_LINK + " TEXT"                //in case of weird error change this data type
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
    public void addImageList(String link) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LINK, link); // Link
        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New record inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public List<String> getDownloadList() {
        List<DownloadData> client = new ArrayList<DownloadData>();
        List<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NOTICES, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(
                    res.getString(res.getColumnIndex(KEY_LINK)));
            res.moveToNext();
        }
        Log.d(TAG, "Data successfully retrived from sqlite: " + array_list.size());
        return array_list;
    }

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
}