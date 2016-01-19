package com.example.utsav.schooldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.utsav.schooldemo.Utils.SQLiteHandler;
import com.example.utsav.schooldemo.app.AppConfig;
import com.example.utsav.schooldemo.app.AppController;
import com.example.utsav.schooldemo.app.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    public static String TAG = MainActivity.class.getSimpleName();
    Spinner spinner;
    Button loginButton;
    EditText editText;
    SessionManager session;
    static String spinnerValue;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner)findViewById(R.id.client_list);
        editText = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login_button);
        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());
        spinner.setOnItemSelectedListener(this);
        loadSpinner();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editText.getText().toString().trim();
/*
                Toast.makeText(getApplicationContext(), "You selected: " + spinnerValue+" password: "+password,
                Toast.LENGTH_LONG).show();
*/
                loginToClientNotice(spinnerValue, password);
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
                        finish();
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
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void loadSpinner() {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        List<String> clientList = db.getClientList();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, clientList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerValue = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        /*Toast.makeText(parent.getContext(), "You selected: " + spinnerValue,
                Toast.LENGTH_LONG).show();*/


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*@Override
    public void onBackPressed()
    {
       // moveTaskToBack(true);
        if(count == 1)
        {
            count=0;
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Press Back again to quit.", Toast.LENGTH_SHORT).show();
            count++;
        }

        return;
    }*/
}
