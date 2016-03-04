package com.example.utsav.schooldemo.app;

import android.content.Context;
import android.os.Environment;

import com.example.utsav.schooldemo.DBClasses.AboutsDB;
import com.example.utsav.schooldemo.DBClasses.ContactsDB;
import com.example.utsav.schooldemo.DBClasses.ImageDB;
import com.example.utsav.schooldemo.DBClasses.NewsDB;
import com.example.utsav.schooldemo.DBClasses.NoticeDB;
import com.example.utsav.schooldemo.DBClasses.PathsDB;
import com.example.utsav.schooldemo.DBClasses.SubsDB;

import java.io.File;
import java.io.IOException;

/**
 * Created by utsav on 1/28/2016.
 */
public class Logout {
    PathsDB pathsDB;
    NewsDB newsDB;
    AboutsDB aboutsDB;
    NoticeDB db;
    ImageDB imageDB;
    SubsDB subsDB;
    SessionManager sessionManager;
    ContactsDB contactsDB;
    //Context context;
    public Logout(Context context){
        File SDCardRoot = Environment.getExternalStorageDirectory();
        //create a new file, to save the downloaded file
        final File directory = new File(SDCardRoot, "/schoolDemo/");
        //this.context = context;
        newsDB = new NewsDB(context);
        pathsDB = new PathsDB(context);
        db = new NoticeDB(context);
        imageDB = new ImageDB(context);
        subsDB = new SubsDB(context);
        sessionManager = new SessionManager(context);
        contactsDB = new ContactsDB(context);
        aboutsDB = new AboutsDB(context);


        aboutsDB.deleteClients();
        newsDB.deleteClients();
        pathsDB.deleteRecords();
        db.deleteClients();
        contactsDB.deleteClients();
        imageDB.deleteRecords();
        subsDB.deleteClients();
        sessionManager.setLogin(false, "0");
        sessionManager.setKeyFetch(true);
        sessionManager.setKeyDownloads(true);
        sessionManager.setKeyContacts(true);
        sessionManager.setKeyNews(true);
        sessionManager.setKeyAbouts(true);
        // sessionManager.setKey(false);
        deleteFiles(directory.toString());

    }
    private static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }
}
