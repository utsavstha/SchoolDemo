package com.example.utsav.schooldemo.app;

/**
 * Created by utsav on 1/2/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "SchoolLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_CID = "cid";
    private static final String KEY_CONTACTS = "contacts";
    private static final String KEY_FETCH = "fetchData";
    private static final String KEY_DOWNLOADS = "downloadFile";
    private static final String KEY_IMAGES = "imagedownload";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String cid) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_CID,cid);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setKeyFetch(boolean fetchData){
        editor.putBoolean(KEY_FETCH, fetchData);
        editor.commit();
        Log.d(TAG, "Fetech data set to" + fetchData+"");
    }
    public void setKeyDownloads(boolean downloads){ //false if file list exists
        editor.putBoolean(KEY_DOWNLOADS, downloads);
        editor.commit();
        Log.d(TAG, "Key download set to "+ downloads);
    }
    public void setKeyContacts(boolean contacts){   //false if data already exists
        editor.putBoolean(KEY_CONTACTS, contacts);
        editor.commit();
        Log.d(TAG, "Key contacts set to " + contacts);
    }
    public void setKeyImages(boolean image){        //false if data already exits
        editor.putBoolean(KEY_IMAGES, image);
        editor.commit();
        Log.d(TAG, "Key contacts set to " + image);
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public String getCid(){ return pref.getString(KEY_CID, "NOT FOUND"); }
    public boolean getFetchData(){ return pref.getBoolean(KEY_FETCH, true); }

    public boolean getKeyDownloads() {
        return pref.getBoolean(KEY_DOWNLOADS, true);    //false if file list exists and no download required
    }

    public boolean getKeyContacts(){
        return pref.getBoolean(KEY_CONTACTS, true);     //false if file list exists and no download required
    }
    public boolean getKeyImages(){
        return pref.getBoolean(KEY_IMAGES, true);     //false if file list exists and no download required
    }
}