package com.example.utsav.schooldemo.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.AboutsDB;
import com.example.utsav.schooldemo.DBClasses.DownloadsDB;
import com.example.utsav.schooldemo.DBClasses.NoticeDB;
import com.example.utsav.schooldemo.DBClasses.PathsDB;
import com.example.utsav.schooldemo.DBClasses.SubsDB;
import com.example.utsav.schooldemo.DataClasses.AboutsData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.PopulateViews;
import com.example.utsav.schooldemo.app.SessionManager;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Abouts extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        PopulateViews{
    public static String TAG = Abouts.class.getSimpleName();
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    TextView name,email, contact, website;
    HtmlTextView about;
    NoticeDB db;
    PathsDB pathsDB;
    SubsDB subsDB;
    CardView cardView;
    String contacts = "";
    String webUrl = "";
    AboutsDB aboutsDB;
    List<AboutsData> listData;
    //CircularProgressView circularProgressView;
    CircleProgressBar progressBar;
    FloatingActionButton floatingActionButton;
    private CoordinatorLayout coordinatorLayout;
    DownloadsDB downloadsDB;
    int count = 0;
    String longitude = "";
    String latitude = "";
    ScrollView scrollView;
    String pinName = "";
    SessionManager sessionManager;
    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        progressBar = (CircleProgressBar) findViewById(R.id.progress_view_abouts_1);
        //circularProgressView = (CircularProgressView) findViewById(R.id.progress_view_abouts);
        setSupportActionBar(toolbar);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_aboouts);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        toolbar.showOverflowMenu();
        scrollView = (ScrollView) findViewById(R.id.scrollview_aboouts);
        scrollView.requestDisallowInterceptTouchEvent(true);
       // swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh_abouts);
        db = new NoticeDB(getApplicationContext());
        pathsDB = new PathsDB(getApplicationContext());
        cardView = (CardView) findViewById(R.id.cardView_abouts);
        subsDB = new SubsDB(getApplicationContext());
        linearLayout = (LinearLayout) findViewById(R.id.layout_abouts);
        downloadsDB = new DownloadsDB(getApplicationContext());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_abouts);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_abouts);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_abouts);
        listData= new ArrayList<>();
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        aboutsDB = new AboutsDB(getApplicationContext());
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        contact = (TextView) findViewById(R.id.contact);
        about = (HtmlTextView) findViewById(R.id.about);
        website = (TextView) findViewById(R.id.website);
        name.setTypeface(null, Typeface.BOLD);
        sessionManager = new SessionManager(getApplicationContext());
        progressBar.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        progressBar.setCircleBackgroundEnabled(false);
      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAndFeedData();
            }
        });*/

        if(sessionManager.getKeyAbouts()){
            fetchAndFeedData();
            cardView.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            contact.setVisibility(View.GONE);
            about.setVisibility(View.GONE);
            website.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }else{
            populateOtherViews();
        }


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Abouts.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.setContentView(R.layout.custom_alert);
                //dialog.setTitle("Call: " + listData.get(position).getNameContacts());

                // set the custom dialog components - text, image and button
                Button call = (Button) dialog.findViewById(R.id.call);
                Button email = (Button) dialog.findViewById(R.id.email);
                email.setText("Visit Website");
                //tv.setText("Call: "+listData.get(position).getNameContacts());
                // if button is clicked, close the custom dialog
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL,
                                Uri.parse("tel:" + listData.get(0).getContacts()));
                        startActivity(intent);
                       // Toast.makeText(getApplicationContext(), contacts.trim(), Toast.LENGTH_LONG).show();

                    }
                });

                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = webUrl;
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q="+latitude+","+longitude+" (" + pinName + ")"));
                startActivity(intent);
            }
        });
    }

    private void fetchAndFeedData() {

        final String tag_string_req = "fetch data for abouts";

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
                                JSONArray client = jObj.getJSONArray("client");
                                //Hard coded by the array index
                                JSONObject value = client.getJSONObject(0);
                                aboutsDB.deleteClients();
                                aboutsDB.addData(value.getString("name"),
                                        value.getString("email"),
                                        value.getString("contact"),
                                        value.getString("about"),
                                        value.getString("website"),
                                        value.getString("longitude"),
                                        value.getString("latitude"));

                                populateOtherViews();
                                sessionManager.setKeyAbouts(false);


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
                        params.put("tag", "client");
                        params.put("cid", sessionManager.getCid());


                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }, 1);


    }


    @Override
    public void onBackPressed()
    {
        ActivityCompat.finishAffinity(this);
        startActivity(new Intent(Abouts.this, NoticeAndStuff.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notice_and_stuff, menu);
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem refresh = menu.findItem(R.id.refresh);

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fetchAndFeedData();
                cardView.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                contact.setVisibility(View.GONE);
                about.setVisibility(View.GONE);
                website.setVisibility(View.GONE);

                //progressBar.setVisibility(View.VISIBLE);
                //progressBar.setIndeterminate(true);
               /* circularProgressView.setVisibility(View.VISIBLE);
                circularProgressView.setIndeterminate(true);
                circularProgressView.startAnimation();*/
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());
                startActivity(new Intent(Abouts.this, SplashScreen.class));
                ActivityCompat.finishAffinity(this);
                return true;
            case R.id.action_subs:
                startActivity(new Intent(Abouts.this, Subscriptions.class));
                //ActivityCompat.finishAffinity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.news){
            startActivity(new Intent(Abouts.this, News.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.notice_board){
            startActivity(new Intent(Abouts.this, NoticeAndStuff.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(Abouts.this, FeedBack.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.downloads){
            startActivity(new Intent(Abouts.this, DownloadFiles.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(Abouts.this, Contacts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(Abouts.this, Resources.class));
            ActivityCompat.finishAffinity(this);
        }
        return true;
    }

    @Override
    public void populateRecyclerView() {

    }

    @Override
    public void populateImageViews() {

    }

    @Override
    public void populateOtherViews() {
        listData = aboutsDB.getClientList();
        progressBar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        //email.setVisibility(View.VISIBLE);
        //contact.setVisibility(View.VISIBLE);
        about.setVisibility(View.VISIBLE);
        //website.setVisibility(View.VISIBLE);

        name.setText(listData.get(0).getName());
        email.setText(listData.get(0).getEmail());
        contact.setText(listData.get(0).getContacts());
        //about.setText(listData.get(0).getAbouts());
        about.setHtmlFromString(listData.get(0).getAbouts(), new HtmlTextView.LocalImageGetter());
        website.setText(listData.get(0).getWebsite());
        webUrl = listData.get(0).getWebsite();

        longitude = listData.get(0).getLongitude();
        latitude = listData.get(0).getLatitude();
        pinName = listData.get(0).getName();
        /*if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }
}
