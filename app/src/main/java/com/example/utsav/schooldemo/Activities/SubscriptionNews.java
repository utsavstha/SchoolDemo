package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.DBClasses.SubsDB;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.Demo;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubscriptionNews extends AppCompatActivity {
    public static String TAG = SubscriptionNews.class.getSimpleName();

    List<Demo> subsData = new ArrayList<>();
    private Set<Demo> checkedSet = new HashSet<>();
    SessionManager session;
    SubsDB subsDB;
    CheckBox checkBox;
    // Demo item;
    List<String> data = new ArrayList();
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout_subscriptions);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubscriptionNews.this, News.class));
                session.setKeyFetch(true);
                ActivityCompat.finishAffinity(SubscriptionNews.this);
            }
        });
        //Toast.makeText(getApplicationContext(),"All previous Subscriptions have been cleared, choose again to subscribe",Toast.LENGTH_LONG).show();
        subsDB = new SubsDB(getApplicationContext());
        //subsDB.deleteClients();
        session = new SessionManager(getApplicationContext());
        bindData();


    }

    private void bindData() {
        final String tag_string_req = "fetch subscription data";

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
                                JSONArray subscribe = jObj.getJSONArray("subscribe");
                                int i;
                                int length = subscribe.length();
                                for (i = 0; i < length; i++) {
                                    JSONObject subsValue = subscribe.getJSONObject(i);
                                    String name = subsValue.getString("name");
                                    Demo demo = new Demo();
                                    demo.setContent(name);
                                    subsData.add(demo);
                                }
                                bindViews();

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
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("tag", "subscribe");
                        params.put("cid", session.getCid());
                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        }, 1);


    }

    private void bindViews() {
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return subsData.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.subscription_check_list, parent, false);
                }

                TextView text = (TextView) convertView.findViewById(R.id.text);
                checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

                final Demo item = subsData.get(position);
                text.setText(item.getContent());

                if (checkedSet.contains(item)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false); //has animation
                    //checkBox.setUncheckStatus();
                }
                data = subsDB.getSubsList();
                //  for(int i = 0; i < data.size(); i++){
                if (data.contains(subsData.get(position).getContent())) {
                    checkBox.setChecked(true);
                    // Log.e(TAG, data.get(position));
                } else {
                    //checkBox.setChecked(false); //has animation
                    checkBox.setChecked(false);
                    //checkedSet.add(subsData.get(position));

                }
                // }
                // subsDB.deleteClients();

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //checkBox.setChecked(isChecked);
                            checkedSet.add(item);
                            subsDB.addSubs(item.getContent());
                        } else {
                            //checkBox.setChecked(isCheck);
                            checkedSet.remove(item);
                            subsDB.deleteSubs(item.getContent());
                            //Toast.makeText(getApplicationContext(), isCheck + "not checked" , Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return convertView;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SubscriptionNews.this, News.class));
        session.setKeyNews(true);
        ActivityCompat.finishAffinity(this);
    }
}