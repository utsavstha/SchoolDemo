package com.example.utsav.schooldemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.Utils.DownloadsDB;
import com.example.utsav.schooldemo.Utils.SubsDB;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadFiles extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener{
    public static String TAG = DownloadFiles.class.getSimpleName();
    private RecyclerView recyclerView;  //recycler view variable
    CircularProgressView progressView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager sessionManager;
    SubsDB subsDB;
    private List<DownloadData> listData = new ArrayList<>() ; //creating list of the Download data class
    DownloadsDB downloadsDB;
    List<String> subsData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_download);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_downloads);
        progressView = (CircularProgressView) findViewById(R.id.progress_view_downloads);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_download);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_download);
        toolbar.showOverflowMenu();
        swipeRefreshLayout.setOnRefreshListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        progressView.setColor(Color.parseColor("#D32F2F"));
        sessionManager = new SessionManager(getApplicationContext());
        subsDB = new SubsDB(getApplicationContext());
        subsData = subsDB.getSubsList();
        downloadsDB = new DownloadsDB(getApplicationContext());
        if(sessionManager.getKeyDownloads()){
            fetchDataAndAddToDownloads(sessionManager.getCid());
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
            recyclerView.setVisibility(View.GONE);
            sessionManager.setKeyDownloads(false);
        }else{
            populateRecyclerView();
        }
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        recyclerView.setLayoutManager(llm);

    }

    private void populateRecyclerView() {

    }

    private void fetchDataAndAddToDownloads(String cid) {
        final String tag_string_req = "fetch download data";
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
                                JSONArray files = jObj.getJSONArray("file");
                                int i;
                                int length = files.length();
                                downloadsDB.deleteRecords();
                                for (i = 0; i < length; i++) {

                                    JSONObject noticeValue = files.getJSONObject(i);
                                    String title = noticeValue.getString("title");
                                    String link = noticeValue.getString("link");
                                    String size = noticeValue.getString("size");
                                    String day = noticeValue.getString("day");
                                    String month = noticeValue.getString("month");
                                    String year = noticeValue.getString("year");
                                    //add data to db
                                    downloadsDB.addDownloadList(title, link,size, month, day, year);
                                    populateRecyclerView();
                                }


                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                //if(!session.getFetchData()){
                                populateRecyclerView();
                                //}

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
                        String subname = "";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "file");
                        params.put("cid", sessionManager.getCid());
                        if(subsData.size() >= 1){
                            for(int i = 0; i < subsData.size(); i++){
                                subname +=  subsData.get(i) + "+";
                            }
                            String finalData = subname.replaceAll(" ", "_");
                            params.put("subname",finalData);
                        }



                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }, 1);

        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onRefresh() {

    }
}
