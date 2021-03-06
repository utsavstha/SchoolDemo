package com.example.utsav.schooldemo.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.DownloadsDB;
import com.example.utsav.schooldemo.DBClasses.NoticeDB;
import com.example.utsav.schooldemo.DBClasses.PathsDB;
import com.example.utsav.schooldemo.DBClasses.SubsDB;
import com.example.utsav.schooldemo.DataClasses.DownloadData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.Utils.RVAdapterDownloads;
import com.example.utsav.schooldemo.Utils.RecyclerTouchListener;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.Logout;
import com.example.utsav.schooldemo.app.PopulateViews;
import com.example.utsav.schooldemo.app.SessionManager;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DownloadFiles extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, PopulateViews{
    public static String TAG = DownloadFiles.class.getSimpleName();
    private RecyclerView recyclerView;  //recycler view variable
    CircleProgressBar progressView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NavigationView mDrawer;   //object to initialise navigation view
    private DrawerLayout mDrawerLayout; //object that holds id to drawer layout
    private ActionBarDrawerToggle mDrawerToggle;
    String filePath;
    PathsDB pathsDB;
    SessionManager sessionManager;
    SubsDB subsDB;
    NoticeDB db;
    int count = 0;
    ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    private ProgressDialog progress;
    int totalSize = 0;
    private List<DownloadData> listData = new ArrayList<>() ; //creating list of the Download data class
    DownloadsDB downloadsDB;
    List<String> subsData = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_download);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_downloads);
        progressView = (CircleProgressBar) findViewById(R.id.progress_view_downloads);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer_download);//initialising navigation view
        mDrawer.setNavigationItemSelectedListener(this);           //tells this activity will handle click events
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_to_refresh_download);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_downloads);
        toolbar.showOverflowMenu();
        swipeRefreshLayout.setOnRefreshListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);//needed to show the hamburger icon
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_downloads);
         //linking drawer layout and drawer toggle
        //drawer toggle keeps the track of who is active on the screen drawer or main content
        mDrawerToggle.syncState(); //Synchronizes the state of hamburger icon
        sessionManager = new SessionManager(getApplicationContext());
        subsDB = new SubsDB(getApplicationContext());
        db  = new NoticeDB(getApplicationContext());
        subsData = subsDB.getSubsList();
        downloadsDB = new DownloadsDB(getApplicationContext());
        pathsDB = new PathsDB(getApplicationContext());

        progressView.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        progressView.setCircleBackgroundEnabled(false);

        if(sessionManager.getKeyDownloads()){
            fetchDataAndAddToDownloads(sessionManager.getCid());
            progressView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }else{
            populateRecyclerView();
        }
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext()); //this will make the recycler view work as list view

        recyclerView.setLayoutManager(llm);

        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        final String url = listData.get(position).getLink();
                        final int _id = listData.get(position).getId();
                        if(pathsDB.getPath(_id).equalsIgnoreCase("xxx")){
                            progress=new ProgressDialog(DownloadFiles.this);
                            progress.setMessage("Downloading: "+ listData.get(position).getTitle());
                            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progress.setIndeterminate(false);
                            progress.setProgress(0);
                            progress.show();
                            //Log.d(TAG, "file download started");
                            new Thread(new Runnable() {
                                public void run() {

                                    downloadFile(url,_id);
                                }
                            }).start();

                        }else{
                            File temp_file=new File(pathsDB.getPath(_id));
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(temp_file),getMimeType(temp_file.getAbsolutePath()));
                            startActivity(intent);
                        }



                       // Toast.makeText(getApplicationContext(), listData.get(position).getLink(), Toast.LENGTH_LONG).show();
                    }
                })
        );
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
                            //Log.d(TAG,"Response: "+response);
                            int count = jObj.getInt("count");
                            if(count == 0){
                                Snackbar.make(coordinatorLayout, " No data to be displayed...", Snackbar.LENGTH_LONG).show();
                            }
                            // Check for error node in json
                            if (!error) {
                                // data successfully fetched
                                JSONArray files = jObj.getJSONArray("file");
                                int i;
                                int length = files.length();
                                downloadsDB.deleteRecords();
                                for (i = 0; i < length; i++) {

                                    JSONObject noticeValue = files.getJSONObject(i);
                                    int id = noticeValue.getInt("file_id");
                                    String title = noticeValue.getString("title");
                                    String link = noticeValue.getString("link");
                                    String size = noticeValue.getString("size");
                                    String day = noticeValue.getString("day");
                                    String month = noticeValue.getString("month");
                                    String year = noticeValue.getString("year");
                                    //add data to db
                                    pathsDB.addPath(id, "xxx");
                                    downloadsDB.addDownloadList(id, title, link, size, "xxx", day, month, year);
                                }
                                sessionManager.setKeyDownloads(false);
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
                        if (subsData.size() >= 1) {
                            for (int i = 0; i < subsData.size(); i++) {
                                subname += subsData.get(i) + "+";
                            }
                            String finalData = subname.replaceAll(" ", "_");
                            params.put("subname", finalData);
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
        Intent intent = null;
        if(item.getItemId() == R.id.news){
            startActivity(new Intent(DownloadFiles.this, News.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.abouts){
            startActivity(new Intent(DownloadFiles.this, Abouts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.feed_back){
            startActivity(new Intent(DownloadFiles.this, FeedBack.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.notice_board){
            startActivity(new Intent(DownloadFiles.this, NoticeAndStuff.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.contacts){
            startActivity(new Intent(DownloadFiles.this, Contacts.class));
            ActivityCompat.finishAffinity(this);
        }else if(item.getItemId() == R.id.resources) {
            startActivity(new Intent(DownloadFiles.this, Resources.class));
            ActivityCompat.finishAffinity(this);
        }

        return true;
    }

    @Override
    public void onRefresh() {
        /*swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });*/
        fetchDataAndAddToDownloads(sessionManager.getCid());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // Red item was selected
                Logout logout = new Logout(getApplicationContext());
                startActivity(new Intent(DownloadFiles.this, SplashScreen.class));
                ActivityCompat.finishAffinity(this);
                return true;
            case R.id.action_subs:
                startActivity(new Intent(DownloadFiles.this, Subscriptions.class));
                //ActivityCompat.finishAffinity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notice_and_stuff, menu);
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem refresh = menu.findItem(R.id.refresh);

        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                fetchDataAndAddToDownloads(sessionManager.getCid());
                progressView.setVisibility(View.VISIBLE);
                //progressView.startAnimation();
                recyclerView.setVisibility(View.GONE);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void populateRecyclerView() {
        listData.clear();
        listData = downloadsDB.getDownloadList();
        progressView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        Log.d(TAG,"list Data size: "+listData.size());
        //progressView.
        RVAdapterDownloads adapter = new RVAdapterDownloads(listData);

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

    void downloadFile(final String dwnload_file_path,final int id){

        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            final File directory = new File(SDCardRoot, "/nBulletin/");
            if (!directory.exists())
            {
                directory.mkdir();
            }

            FileOutputStream fileOutput = new FileOutputStream(new File(directory,
                    dwnload_file_path.substring(dwnload_file_path.lastIndexOf("/") + 1)));

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                       // pb.setProgress(downloadedSize);
                        float per = ((float)downloadedSize/1024);
                        progress.setProgress((int) per);
                       // progress.incrementProgressBy((int)per);
                    }
                });
            }
            filePath = directory.toString() +"/"+
                    dwnload_file_path.substring(dwnload_file_path.lastIndexOf("/") + 1);
            //close the output stream when complete
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    progress.hide();
                    pathsDB.updatePath(filePath, id);
                    populateRecyclerView(); //delete this in case of weird error
                    // pb.dismiss(); // if you want close it..
                }
            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                Snackbar.make(coordinatorLayout,R.string.error_timeout,Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    @Override
    public void onBackPressed()
    {
        ActivityCompat.finishAffinity(this);
        startActivity(new Intent(DownloadFiles.this, NoticeAndStuff.class));
    }

}
