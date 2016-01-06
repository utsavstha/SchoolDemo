package com.example.utsav.schooldemo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends AppCompatActivity {

    TextView title;
    TextView date;
    TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Details.this, NoticeAndStuff.class));
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title_details);
        date = (TextView)findViewById(R.id.date_details);
        message = (TextView) findViewById(R.id.message_details);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        message.setText(getIntent().getStringExtra("message"));

    }
    @Override
    public void onBackPressed()
    {
       startActivity(new Intent(Details.this, NoticeAndStuff.class));
        finish();
    }

}
