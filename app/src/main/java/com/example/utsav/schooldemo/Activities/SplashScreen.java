package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.SQLiteHandler;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    int i;
    static int count = 0;
    public static String TAG = SplashScreen.class.getSimpleName();
    private SQLiteHandler db;
    CircleProgressBar cv;
    private Handler handler = new Handler();
    private int mProgress = 0;
    SessionManager session;
    private CoordinatorLayout coordinatorLayout;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        cv = (CircleProgressBar) findViewById(R.id.progress);


        cv.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        cv.setCircleBackgroundEnabled(false);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent intent = new Intent(SplashScreen.this, NoticeAndStuff.class);
            ActivityCompat.finishAffinity(this);
            startActivity(intent);
            //ActivityCompat.finishAffinity(this);
        }
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_splash);
        db = new SQLiteHandler(getApplicationContext());
        try {
            db.deleteClients();
        } catch (Exception e) {
            //Log.d(TAG, e.toString());
        }
        cv.setVisibility(View.VISIBLE);
        //  cv.startAnimation();
       // OptionView.noBackground = true;
        if (!session.isLoggedIn())
            startProgress();
        //downloadData();




    }

    private void startProgress() {
        final String tag_string_req = "req_Client";
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_LIST, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, "Login Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                // user successfully logged in

                                // Now store the data in SQLite
                                //int count = jObj.getInt("count");
                                final JSONArray client = jObj.getJSONArray("client");
                                final int length = client.length();

                                for (i = 0; i < length; i++) {
                                    JSONObject id = client.getJSONObject(i);
                                    String name = id.getString("org_name");
                                    String url = id.getString("icon");

                                    Boolean pass = id.getBoolean("passkey");
                                    String cid = id.getString("cid");
                                    //session.setLogin(false, cid);
                                    String passString = pass.toString();
                                    db.addClient(name, url, passString, cid);
                                    //session.setKeyPassword(pass);

                                }
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                ActivityCompat.finishAffinity(SplashScreen.this);
                                startActivity(intent);

                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                               /* Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();*/
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
                      //  Log.e(TAG, "Login Error: " + error.getMessage());
                        HandleVolleyError volleyError = new HandleVolleyError(error, coordinatorLayout);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("tag", "client");

                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


                // while (mProgress < 100) {
                // progressStatus = downloadFile();

                // mProgress++;
                //     }

            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (count == 1) {
            count = 0;
            ActivityCompat.finishAffinity(this);
        } else {
            Toast.makeText(getApplicationContext(), "Press Back again to quit.", Toast.LENGTH_SHORT).show();
            count++;
        }

        return;
    }
}
