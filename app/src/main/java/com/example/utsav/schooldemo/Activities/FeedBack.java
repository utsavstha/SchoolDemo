package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.NoticeDB;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedBack extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static String TAG = FeedBack.class.getSimpleName();
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    TextView subject, message,name;
    SessionManager sessionManager;
    NoticeDB db;
    Button send;
    int count = 0;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new NoticeDB(getApplicationContext());
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_feedback);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        toolbar.showOverflowMenu();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_feedback);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        sessionManager = new SessionManager(getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_feedback);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon

        subject = (TextView) findViewById(R.id.subject);
        message = (TextView) findViewById(R.id.message);
        name = (TextView)findViewById(R.id.name);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tag_string_req = "send feed back";

                if(subject.getText().toString().isEmpty() || message.getText().toString().isEmpty() || name.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please Fill all the Empty fields", Toast.LENGTH_LONG).show();

                }else {
                    send.setText("Please wait..");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StringRequest strReq = new StringRequest(Request.Method.POST,
                                    AppConfig.URL_LIST, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    //Log.d(TAG, "Response: " + response.toString());
                                    //hideDialog();

                                    try {
                                        JSONObject jObj = new JSONObject(response);
                                        boolean error = jObj.getBoolean("error");

                                        // Check for error node in json
                                        if (!error) {
                                            String msg = jObj.getString("msg");
                                            if (!msg.isEmpty()) {
                                                subject.setText("");
                                                message.setText("");
                                                name.setText("");
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                                send.setText("Send");
                                            }

                                        } else {
                                            // Error in login. Get the error message
                                            String errorMsg = jObj.getString("msg");
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
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("tag", "feedback");
                                    String finalSubject = subject.getText().toString().trim().replaceAll(" ", "_");
                                    params.put("title", finalSubject);
                                    String finalMessage = message.getText().toString().trim().replaceAll(" ", "_");
                                    params.put("msg", finalMessage);
                                    String finalName = name.getText().toString().trim().replaceAll(" ", "_");
                                    params.put("name", finalName);
                                    params.put("cid", sessionManager.getCid());

                                    return params;
                                }

                            };

                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                        }
                    }, 1);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.news){
            startActivity(new Intent(FeedBack.this, News.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.notice_board){
            startActivity(new Intent(FeedBack.this, NoticeAndStuff.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(FeedBack.this, Abouts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.downloads){
            startActivity(new Intent(FeedBack.this, DownloadFiles.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(FeedBack.this, Contacts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(FeedBack.this, Resources.class));
            ActivityCompat.finishAffinity(this);
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        ActivityCompat.finishAffinity(this);
        startActivity(new Intent(FeedBack.this, NoticeAndStuff.class));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notice_and_stuff, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());
                startActivity(new Intent(FeedBack.this, SplashScreen.class));
                ActivityCompat.finishAffinity(this);
                return true;
            case R.id.action_subs:
                startActivity(new Intent(FeedBack.this, Subscriptions.class));
                //ActivityCompat.finishAffinity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
