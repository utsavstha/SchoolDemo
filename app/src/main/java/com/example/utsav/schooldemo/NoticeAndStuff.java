package com.example.utsav.schooldemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.Utils.NoticeDB;
import com.example.utsav.schooldemo.Utils.SQLiteHandler;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NoticeAndStuff extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static String TAG = NoticeAndStuff.class.getSimpleName();
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    NoticeDB db;
    private String cid;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_and_stuff);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        progressBar = new ProgressDialog(getApplicationContext());
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon

        session = new SessionManager(getApplicationContext());
        cid = session.getCid();
        db = new NoticeDB(getApplicationContext());
        fetchDataAndAddToDb(cid);
    }

    private void fetchDataAndAddToDb(final String cid) {
        final String tag_string_req = "fetch data";

       /* progressBar.setCancelable(true);
        progressBar.setMessage("Fetching data ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LIST, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                // data successfully fetched
                                JSONArray notice = jObj.getJSONArray("notice");
                                int i;
                                int length = notice.length();

                                for(i = 0; i < length; i++){

                                    JSONObject noticeValue = notice.getJSONObject(i);
                                    String title = noticeValue.getString("title");
                                    String message = noticeValue.getString("message");
                                    String day = noticeValue.getString("day");
                                    String month = noticeValue.getString("month");
                                    String year = noticeValue.getString("year");
                                    //add data to db
                                    db.addNotice(title,message,month,day,year);
                                    progressBarStatus = (i*100) / length;
                                   /* progressBarbHandler.post(new Runnable() {
                                        public void run() {
                                            progressBar.setProgress(progressBarStatus);
                                        }
                                    });*/
                                    //progressBar.setProgress(progressBarStatus);
                                }

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        // hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "notice");
                        params.put("cid", cid);

                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }, 1000);


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.news){
            //intent = new Intent(NoticeAndStuff.this, )
        }else if(item.getItemId() == R.id.routines){

        }else{
            //for contacts
        }

        return true;
    }
}
