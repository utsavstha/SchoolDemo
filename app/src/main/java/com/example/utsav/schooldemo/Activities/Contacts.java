package com.example.utsav.schooldemo.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.ContactsDB;
import com.example.utsav.schooldemo.DataClasses.ContactsData;
import com.example.utsav.schooldemo.DataClasses.NoticeData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.Utils.RVAdapterContacts;
import com.example.utsav.schooldemo.Utils.RecyclerTouchListener;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.SessionManager;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contacts extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    public static String TAG = Contacts.class.getSimpleName();
    SessionManager sessionManager;
    CircularProgressView progressView;
    private RecyclerView recyclerView;  //recycler view variable
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    ContactsDB contactsDB;
    private CoordinatorLayout coordinatorLayout;
    private List<ContactsData> listData = new ArrayList<>() ; //creating list of the ContactsData class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contacts);
        setSupportActionBar(toolbar);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_contacts);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_contacts);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_contacts);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_contacts);
        progressView = (CircularProgressView) findViewById(R.id.progress_view_contacts);
        toolbar.showOverflowMenu();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_contacts);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
         /*linking drawer layout and drawer toggle
        drawer toggle keeps the track of who is active on the screen drawer or main content*/
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        contactsDB = new ContactsDB(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        progressView.setColor(Color.parseColor("#D32F2F"));
        if(sessionManager.getKeyContacts()){
            fetchContactsData(sessionManager.getCid());
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
            recyclerView.setVisibility(View.GONE);
            sessionManager.setKeyContacts(false);
        }else{
            populateRecyclerView();
        }
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchContactsData(sessionManager.getCid());
            }
        });
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        // custom dialog
                        final Dialog dialog = new Dialog(Contacts.this);
                        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        dialog.setContentView(R.layout.custom_alert);
                        dialog.setTitle("Call: " + listData.get(position).getNameContacts());

                        // set the custom dialog components - text, image and button
                        Button call = (Button) dialog.findViewById(R.id.call);
                        Button email = (Button) dialog.findViewById(R.id.email);
                        //tv.setText("Call: "+listData.get(position).getNameContacts());
                        // if button is clicked, close the custom dialog
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL,
                                        Uri.parse("tel:" + listData.get(position).getPhone().trim()));
                                startActivity(intent);

                            }
                        });

                        email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                                        Uri.parse("mailto:" + Uri.encode(listData.get(position).getEmail())));

                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "enter your text..");
                                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
                            }
                        });
                        dialog.show();

                    }

                })
        );
    }

    private void populateRecyclerView() {
        listData = contactsDB.getClientList();
        progressView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        RVAdapterContacts adapter = new RVAdapterContacts(listData);

        recyclerView.setAdapter(adapter);

        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notice_and_stuff, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void fetchContactsData(final String cid) {
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
                                JSONArray contacts = jObj.getJSONArray("contact");
                                int i;
                                int length = contacts.length();
                                contactsDB.deleteClients();
                                for (i = 0; i < length; i++) {

                                    JSONObject noticeValue = contacts.getJSONObject(i);
                                    String name = noticeValue.getString("name");
                                    String designation = noticeValue.getString("designation");
                                    String email = noticeValue.getString("email");
                                    String phone = noticeValue.getString("phone");
                                    //add data to db
                                    // db.addNotice(title, message, month, day, year);
                                    contactsDB.addContacts(name, designation, email, phone);

                                }
                                populateRecyclerView();

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
                        HandleVolleyError volleyError = new HandleVolleyError(error, coordinatorLayout);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "contact");
                        params.put("cid", cid);
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
        if(item.getItemId() == R.id.news){
            //intent = new Intent(NoticeAndStuff.this, )
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(Contacts.this, Abouts.class));
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(Contacts.this, FeedBack.class));
        }else if (item.getItemId() == R.id.downloads){
            startActivity(new Intent(Contacts.this, DownloadFiles.class));
        }else if(item.getItemId() == R.id.notice_board){
            startActivity(new Intent(Contacts.this, DownloadFiles.class));
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(Contacts.this, Resources.class));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());

                startActivity(new Intent(Contacts.this, SplashScreen.class));
                finish();
                return true;
            case R.id.action_subs:
                startActivity(new Intent(Contacts.this, Subscriptions.class));
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(Contacts.this, NoticeAndStuff.class));
    }
}
