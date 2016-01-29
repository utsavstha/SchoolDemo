package com.example.utsav.schooldemo.DBClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.utsav.schooldemo.DataClasses.DownloadData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utsav on 1/19/2016.
 */
public class DownloadsDB extends SQLiteOpenHelper {

    private static final String TAG = DownloadsDB.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Downloads";

    // Login table name
    private static final String TABLE_NOTICES = "downloads_list";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_ID = "fileid";
    private static final String KEY_TTTLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_SIZE = "size";
    private static final String KEY_MONTH = "month";
    private static final String KEY_DAY = "day";
    private static final String KEY_YEAR = "year";
    private static final String KEY_PATH = "path";

    public DownloadsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTICE_TABLE =
                "CREATE TABLE " + TABLE_NOTICES +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_FILE_ID + " TEXT,"
                        + KEY_TTTLE + " TEXT,"
                        + KEY_LINK + " TEXT,"                //in case of weird error change this data type
                        + KEY_SIZE + " TEXT,"
                        + KEY_PATH + " TEXT,"
                        + KEY_DAY + " INT,"
                        + KEY_MONTH + " TEXT,"
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
    public void addDownloadList(int _id,String title, String link, String size, String path ,String day, String month, String year) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_ID, _id); // Title
        values.put(KEY_TTTLE, title); // Title
        values.put(KEY_LINK, link); // Link
        values.put(KEY_SIZE,size);
        values.put(KEY_PATH,path);
        values.put(KEY_DAY, month); // month
        values.put(KEY_MONTH, day); // day
        values.put(KEY_YEAR, year); // year
        // Inserting Row
        long id = db.insert(TABLE_NOTICES, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New record inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public List<DownloadData> getDownloadList() {
        List<DownloadData> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NOTICES, null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new DownloadData(res.getInt(res.getColumnIndex(KEY_FILE_ID)),
                    res.getString(res.getColumnIndex(KEY_TTTLE)),
                    res.getString(res.getColumnIndex(KEY_LINK)),
                    res.getString(res.getColumnIndex(KEY_SIZE)),
                    res.getString(res.getColumnIndex(KEY_PATH)),
                    res.getString(res.getColumnIndex(KEY_DAY)),
                    res.getString(res.getColumnIndex(KEY_MONTH)),
                    res.getString(res.getColumnIndex(KEY_YEAR))));
            res.moveToNext();
        }
        Log.d(TAG, "Data successfully retrived from sqlite: " + array_list.size());
        return array_list;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NOTICES, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}