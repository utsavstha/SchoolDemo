package com.example.utsav.schooldemo.Activities;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.example.utsav.schooldemo.DBClasses.ImageDB;
import com.example.utsav.schooldemo.DBClasses.NoticeDB;
import com.example.utsav.schooldemo.DBClasses.PathsDB;
import com.example.utsav.schooldemo.DBClasses.SubsDB;
import com.example.utsav.schooldemo.DataClasses.NoticeData;
import com.example.utsav.schooldemo.Gcm.QuickstartPreferences;
import com.example.utsav.schooldemo.Gcm.RegistrationIntentService;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.CustomPagerAdapter;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.Utils.RVAdapter;
import com.example.utsav.schooldemo.Utils.RecyclerTouchListener;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.PopulateViews;
import com.example.utsav.schooldemo.app.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NoticeAndStuff extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, PopulateViews{


    public static String TAG = NoticeAndStuff.class.getSimpleName();
    private ViewPager viewPager;
    private RecyclerView recyclerView;  //recycler view variable
    private List<NoticeData> listData = new ArrayList<>() ; //creating list of the Notice data class
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    List<String> imageList;
    PathsDB pathsDB;
    NoticeDB db;
    ImageDB imageDB;
    SubsDB subsDB;
    private int count = 0;
    SessionManager session;
    private String cid;
    CircleProgressBar progressView;
    Timer timer;
    private int position = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    List<String> subsData = new ArrayList<>();
    CustomPagerAdapter customPagerAdapter;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_and_stuff);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh);
        toolbar.showOverflowMenu();
        subsDB = new SubsDB(getApplicationContext());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        progressView = (CircleProgressBar) findViewById(R.id.progress_view);
        viewPager = (ViewPager)findViewById(R.id.pager_introduction);

        imageList = new ArrayList<>();
        imageDB = new ImageDB(getApplicationContext());
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchDataAndAddToDb(cid);
            }
        });
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        /*************************************************************/
        pathsDB = new PathsDB(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        cid = session.getCid();
        db = new NoticeDB(getApplicationContext());
        subsData = subsDB.getSubsList();

        progressView.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        progressView.setCircleBackgroundEnabled(false);

        if(session.getFetchData()){
            imageDB.addImageList("http://www.xyz.com"); //to feed view pager and the circle indicator with some initializing data
            populateImageViews();
            fetchDataAndAddToDb(cid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (session.getKeyServer()) {
                        sendToServer(session.getKeyRegid());
                    }
                }

            }, 1);
            progressView.setVisibility(View.VISIBLE);
            // progressView.startAnimation();
            recyclerView.setVisibility(View.GONE);
        }else{
            //imageDB.deleteRecords();
           // imageDB.addImageList("http://www.xyz.com"); //to feed view pager and the circle indicator with some initializing data
            populateRecyclerView();
            populateImageViews();
        }

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        recyclerView.setLayoutManager(llm);

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), new RecyclerTouchListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent intent = new Intent(NoticeAndStuff.this, Details.class);
                        intent.putExtra("title",listData.get(position).getTitle());
                        intent.putExtra("date", listData.get(position).getWeekday() + ", "+
                                getMonth(listData.get(position).getMonth()) +" " +
                                listData.get(position).getDay()+ ", "+
                                listData.get(position).getYear());
                        intent.putExtra("message",listData.get(position).getMessage());

                        startActivity(intent);
                    }
                })
        );


    }

    private void sendToServer(final String keyRegid) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(TAG, response);
                        session.setKey(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());

                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "gcm");
                params.put("cid", session.getCid());
                params.put("regId", keyRegid);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "regid send");
    }

    public String getMonth(String month) {
        int monthValue = Integer.parseInt(month);
        return new DateFormatSymbols().getMonths()[monthValue-1];
    }
    private  void fetchDataAndAddToDb(final String cid) {
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
                                JSONArray notice = jObj.getJSONArray("notice");
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
                                    db.addNotice(title, message, weekday, month, day, year);

                                }
                                populateRecyclerView();
                               // if(session.getKeyImages()){
                                    JSONArray images = jObj.getJSONArray("image");
                                    imageDB.deleteRecords();
                                    for(int j = 0; j < images.length(); j++){
                                        String image = images.getString(j);
                                        //imageList.add(image);
                                        imageDB.addImageList(image);

                                    }
                               // }
                                session.setKeyFetch(false);
                                populateImageViews();

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                //if(!session.getFetchData()){
                                    populateRecyclerView();
                                    populateImageViews();
                                //}

                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
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
                        //Log.e(TAG, "Login Error: " + error.getMessage());
                        HandleVolleyError volleyError = new HandleVolleyError(error, coordinatorLayout);
                        // hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        String subname = "";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "notice");
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
    public void populateImageViews() {
        imageList.clear();
        imageList = imageDB.getDownloadList();
       // Log.d(TAG, imageList.size()+"");
        customPagerAdapter = new CustomPagerAdapter(this, imageList);
        viewPager.setAdapter(customPagerAdapter);
        viewPager.setCurrentItem(0);

        // Timer for auto sliding
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (position <= imageList.size()) {
                            viewPager.setCurrentItem(position);
                            position++;
                        } else {
                            position = 0;
                            viewPager.setCurrentItem(position);
                        }
                    }
                });
            }
        }, 500, 3000);
    }

    @Override
    public void populateOtherViews() {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.news){
            startActivity(new Intent(NoticeAndStuff.this, News.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(NoticeAndStuff.this, Abouts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(NoticeAndStuff.this, FeedBack.class));
            ActivityCompat.finishAffinity(this);
        }else if (item.getItemId() == R.id.downloads){
            startActivity(new Intent(NoticeAndStuff.this, DownloadFiles.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(NoticeAndStuff.this, Contacts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(NoticeAndStuff.this, Resources.class));
            ActivityCompat.finishAffinity(this);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notice_and_stuff, menu);
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) NoticeAndStuff.this.getSystemService(Context.SEARCH_SERVICE);

        MenuItem refresh = menu.findItem(R.id.refresh);

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                imageDB.addImageList("http://www.xyz.com"); //to feed view pager and the circle indicator with some initializing data
                populateImageViews();
                fetchDataAndAddToDb(cid);
                progressView.setVisibility(View.VISIBLE);
                // progressView.startAnimation();
                recyclerView.setVisibility(View.GONE);
                return true;
            }
        });
        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(NoticeAndStuff.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());

                startActivity(new Intent(NoticeAndStuff.this, SplashScreen.class));
                ActivityCompat.finishAffinity(this);
                return true;
            case R.id.action_subs:
                startActivity(new Intent(NoticeAndStuff.this, Subscriptions.class));
                //ActivityCompat.finishAffinity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        // moveTaskToBack(true);
        if(count == 1)
        {
            count=0;
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else
        {
            Toast.makeText(getApplicationContext(), "Press Back again to quit.", Toast.LENGTH_SHORT).show();
            count++;
        }

        return;
    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                ActivityCompat.finishAffinity(this);
            }
            return false;
        }
        return true;
    }

}
