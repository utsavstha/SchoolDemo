package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
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
import com.example.utsav.schooldemo.DBClasses.ContactsDB;
import com.example.utsav.schooldemo.DataClasses.ResourcesData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.Utils.RVAdapterResources;
import com.example.utsav.schooldemo.Utils.RecyclerTouchListener;
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

public class Resources extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    public static String TAG = Resources.class.getSimpleName();
    SessionManager sessionManager;
    CircularProgressView progressView;
    private RecyclerView recyclerView;  //recycler view variable
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    private List<ResourcesData> listData = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_resources);
        setSupportActionBar(toolbar);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_resources);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_resources);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_resources);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_resources);
        progressView = (CircularProgressView) findViewById(R.id.progress_view_resources);
        toolbar.showOverflowMenu();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_resources);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        sessionManager = new SessionManager(getApplicationContext());
        progressView.setColor(Color.parseColor("#D32F2F"));
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchResourcesData(sessionManager.getCid());
            }
        });
        recyclerView.setLayoutManager(llm);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        fetchResourcesData(sessionManager.getCid());

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String url = listData.get(position).getUrl();
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                })
        );
    }

    private void fetchResourcesData(final String cid) {
        final String tag_string_req = "fetch data";

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
                                JSONArray contacts = jObj.getJSONArray("resource");
                                int i;
                                int length = contacts.length();
                                for (i = 0; i < length; i++) {

                                    JSONObject noticeValue = contacts.getJSONObject(i);
                                    String title = noticeValue.getString("title");
                                    String url = noticeValue.getString("url");
                                    String day = noticeValue.getString("day");
                                    String month = noticeValue.getString("month");
                                    String year = noticeValue.getString("year");
                                    //add data to db
                                    // db.addNotice(title, message, month, day, year);
                                    listData.add(new ResourcesData(title, year,url,day, month));

                                }
                                populateRecyclerView(listData);

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");

                                Toast.makeText(getApplicationContext(),
                                        errorMsg+"jhgjhg", Toast.LENGTH_LONG).show();
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
                        HandleVolleyError volleyError = new HandleVolleyError(error, coordinatorLayout);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "resource");
                        params.put("cid", cid);
                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }, 1);
    }

    private void populateRecyclerView(List<ResourcesData> listData) {
        progressView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        RVAdapterResources adapter = new RVAdapterResources(listData);

        recyclerView.setAdapter(adapter);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.news){
            //intent = new Intent(NoticeAndStuff.this, )
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(Resources.this, Abouts.class));
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(Resources.this, FeedBack.class));
        }else if (item.getItemId() == R.id.downloads){
            startActivity(new Intent(Resources.this, DownloadFiles.class));
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(Resources.this, Contacts.class));
        }else if(item.getItemId() == R.id.notice_board) {
            startActivity(new Intent(Resources.this, NoticeAndStuff.class));
        }
        return true;
    }
    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(Resources.this, NoticeAndStuff.class));
    }
}
