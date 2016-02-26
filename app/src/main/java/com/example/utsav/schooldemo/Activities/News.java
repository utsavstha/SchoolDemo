package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.NewsDB;
import com.example.utsav.schooldemo.DBClasses.SubsDB;
import com.example.utsav.schooldemo.DataClasses.NoticeData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.Utils.RVAdapter;
import com.example.utsav.schooldemo.Utils.RecyclerTouchListener;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.PopulateViews;
import com.example.utsav.schooldemo.app.SessionManager;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class News extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, PopulateViews{
    public static String TAG = News.class.getSimpleName();
    private RecyclerView recyclerView;  //recycler view variable
    private List<NoticeData> listData = new ArrayList<>() ; //creating list of the Notice data class
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    SessionManager session;
    private String cid;
    SubsDB subsDB;
    NewsDB db;
    CircularProgressView progressView;
    List<String> subsData = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_news);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_news);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_news);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_news);
        toolbar.showOverflowMenu();
        subsDB = new SubsDB(getApplicationContext());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_news);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_news);
        progressView = (CircularProgressView) findViewById(R.id.progress_view_news);
        db = new NewsDB(getApplicationContext());
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        session = new SessionManager(getApplicationContext());
        cid = session.getCid();
        subsData = subsDB.getSubsList();
        progressView.setColor(Color.parseColor("#D32F2F"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchDataNews(cid);
            }
        });
        Log.e(TAG,session.getKeyNews()+"");
        if(session.getKeyNews()){
            fetchDataNews(cid);
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
            recyclerView.setVisibility(View.GONE);
        }else{
            populateRecyclerView();
        }

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        recyclerView.setLayoutManager(llm);

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(News.this, DetailsNews.class);
                        intent.putExtra("title", listData.get(position).getTitle());

                        intent.putExtra("date", listData.get(position).getWeekday() + ", "+
                                        getMonth(listData.get(position).getMonth()) +" " +
                                        listData.get(position).getDay()+ ", "+
                                        listData.get(position).getYear());
                        intent.putExtra("message", listData.get(position).getMessage());

                        startActivity(intent);
                    }
                })
        );

    }
    public String getMonth(String month) {
        int monthValue = Integer.parseInt(month);
        return new DateFormatSymbols().getMonths()[monthValue-1];
    }
    @Override
    public void populateRecyclerView() {
        listData = db.getClientList();
        progressView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        //progressView.
        RVAdapter adapter = new RVAdapter(listData);

        recyclerView.setAdapter(adapter);
        //swipeRefreshLayout.setRefreshing(false);
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void populateImageViews() {

    }

    @Override
    public void populateOtherViews() {

    }

    private void fetchDataNews(final String cid) {

        final String tag_string_req = "fetch data";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LIST, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                       // Log.d(TAG, "Login Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            int count = jObj.getInt("count");
                            if(count == 0){
                                Snackbar.make(coordinatorLayout, " No data to be displayed...", Snackbar.LENGTH_LONG).show();
                            }
                            // Check for error node in json
                            if (!error) {
                                // data successfully fetched
                                JSONArray notice = jObj.getJSONArray("news");
                                int i;
                                int length = notice.length();
                                db.deleteClients();
                                for (i = 0; i < length; i++) {

                                    JSONObject noticeValue = notice.getJSONObject(i);
                                    String title = noticeValue.getString("title");
                                    String message = noticeValue.getString("message");
                                    String day = noticeValue.getString("day");
                                    String month = noticeValue.getString("month");
                                    String year = noticeValue.getString("year");
                                    String weekday = noticeValue.getString("weekday");
                                    //add data to db
                                    db.addNotice(title, message, weekday ,month, day, year);

                                }
                                session.setKeyNews(false);
                                populateRecyclerView();

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                //if(!session.getFetchData()){
                                populateRecyclerView();

                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }

                            if(count == 0){
                                Snackbar.make(coordinatorLayout, " No data to be displayed...", Snackbar.LENGTH_LONG).show();
                                session.setKeyNews(true);
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Snackbar.make(coordinatorLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                            session.setKeyNews(true);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Log.e(TAG, "Login Error: " + error.getMessage());
                        HandleVolleyError volleyError = new HandleVolleyError(error, coordinatorLayout);
                        // hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        String subname = "";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "news");
                        params.put("cid", cid);

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
        
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.notice_board){
            startActivity(new Intent(News.this, NoticeAndStuff.class));
            finishAffinity();
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(News.this, Abouts.class));
            finishAffinity();
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(News.this, FeedBack.class));
            finishAffinity();
        }else if (item.getItemId() == R.id.downloads){
            startActivity(new Intent(News.this, DownloadFiles.class));
            finishAffinity();
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(News.this, Contacts.class));
            finishAffinity();
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(News.this, Resources.class));
            finishAffinity();
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(News.this, NoticeAndStuff.class));
        finishAffinity();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notice_and_stuff, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());

                startActivity(new Intent(News.this, SplashScreen.class));
                finishAffinity();
                return true;
            case R.id.action_subs:
                startActivity(new Intent(News.this, SubscriptionNews.class));
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
