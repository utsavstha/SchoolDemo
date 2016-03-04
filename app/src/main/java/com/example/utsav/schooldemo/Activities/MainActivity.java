package com.example.utsav.schooldemo.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.utsav.schooldemo.DBClasses.SQLiteHandler;
import com.example.utsav.schooldemo.DataClasses.ClientsData;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.Utils.CustomVolleyRequestQueue;
import com.example.utsav.schooldemo.app.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getSimpleName();
    SessionManager session;
    int count = 0;
    List<ClientsData> listData = new ArrayList<>();
    SQLiteHandler db;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listView = (ListView)findViewById(R.id.list_clients);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        listData = db.getClientList();
        listView.setAdapter(new BaseAdapter() {


            @Override
            public int getCount() {
                return listData.size();
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
                            .inflate(R.layout.client_rv_list_adapter, parent, false);
                }
                NetworkImageView icon = (NetworkImageView) convertView.findViewById(R.id.client_image);
                //CircularNetworkImageView icon = (CircularNetworkImageView) convertView.findViewById(R.id.client_image);
                TextView tv = (TextView) convertView.findViewById(R.id.client_tv);
                tv.setTextSize(16);

                mImageLoader = CustomVolleyRequestQueue.getInstance(getApplicationContext())
                        .getImageLoader();
                tv.setText(listData.get(position).getName());

                tv.setTypeface(null, Typeface.BOLD);

                mImageLoader.get("", ImageLoader.getImageListener(icon,
                        android.R.drawable.arrow_down_float, android.R.drawable
                                .ic_dialog_alert));

                icon.setImageUrl(listData.get(position).getUrl(), mImageLoader);
               // Log.d(TAG,listData.get(position).getName()+ " and "+ position + " and "+ listData.get(0).getUrl());
                return convertView;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listData.get(position).getPass().equalsIgnoreCase("true")) {
                    Intent intent = new Intent(MainActivity.this, Password.class);
                    intent.putExtra("cid",listData.get(position).getName());
                    startActivity(intent);
                }else{
                    //String cid = listData.get(position).getName().replaceAll(" ", "+");
                    session.setLogin(true, listData.get(position).getCid());
                    ActivityCompat.finishAffinity(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, NoticeAndStuff.class));
                }


            }
        });

    }



    @Override
    public void onBackPressed() {
        // moveTaskToBack(true);
        if (count == 1) {
            count = 0;
            System.exit(1);
        } else {
            Toast.makeText(getApplicationContext(), "Press Back again to quit.", Toast.LENGTH_SHORT).show();
            count++;
        }

        return;
    }
}
