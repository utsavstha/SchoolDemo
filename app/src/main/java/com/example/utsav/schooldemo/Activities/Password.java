package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.HandleVolleyError;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Password extends AppCompatActivity {
    public static String TAG = Password.class.getSimpleName();
    EditText editText;
    Button button;
    SessionManager session;
    String password, name, cid;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_password_one);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Password.this, MainActivity.class));
                finishAffinity();
            }
        });
        editText = (EditText) findViewById(R.id.password_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_password);
        button = (Button) findViewById(R.id.login);
        password = editText.getText().toString().trim();
        name = getIntent().getStringExtra("cid");
        cid = name;

        Log.d(TAG,cid);

        session = new SessionManager(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToClientNotice(cid, editText.getText().toString().trim());
            }
        });

    }
    private void loginToClientNotice(final String spinnerData, final String passwordData) {


        final String tag_string_req = "req_Login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                Log.d(TAG, passwordData);
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    JSONArray cid = jObj.getJSONArray("login");
                    JSONObject cidValue = cid.getJSONObject(0);
                    String cidFinal = cidValue.getString("cid");
                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        session.setLogin(!error, cidFinal);

                        //Launch main activity
                        Intent i=new Intent(getApplicationContext(), NoticeAndStuff.class);
                        i.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finishAffinity();
                        startActivity(i);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Snackbar.make(coordinatorLayout, "Login Failed...", android.support.design.widget.Snackbar.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("cid", spinnerData);
                params.put("pass", passwordData);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
