package com.example.utsav.schooldemo.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.utsav.schooldemo.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailsNews extends AppCompatActivity {

    TextView title;
    TextView date;
    HtmlTextView message;
    //final static float STEP = 200;
    float mRatio = 1.0f;
    // int mBaseDist;
    // float mBaseRatio;
    float fontsize = 18;
    // float titleSize = 30;
    SeekBar seekBar;
    FloatingActionButton fab;

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
                startActivity(new Intent(DetailsNews.this, News.class));
                finishAffinity();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab_details);
        title = (TextView) findViewById(R.id.title_details);
        date = (TextView) findViewById(R.id.date_details);
        message = (HtmlTextView) findViewById(R.id.message_details);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);

        title.setTypeface(null, Typeface.BOLD);
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        message.setHtmlFromString(getIntent().getStringExtra("message"), new HtmlTextView.LocalImageGetter());

        //title.setTextSize(mRatio + titleSize);
        //date.setTextSize(mRatio + fontsize);
        message.setTextSize(mRatio + fontsize);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(DetailsNews.this)
                        .setTitle("Share With")
                        .setMessage("All Shared contents will be sourced from School Demo")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String shareText = "Title: " + getIntent().getStringExtra("title") + "\n\n Date: " + getIntent().getStringExtra("date")
                                        + "\n\nMessage: " + getIntent().getStringExtra("message") +
                                        "\n\nsource: SchoolDemo";
                                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                                startActivity(Intent.createChooser(intent, "Share With"));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.alert_dark_frame)
                        .show();


            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                message.setTextSize(progress + fontsize);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                fab.hide();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fab.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailsNews.this, News.class));
        finishAffinity();
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            int action = event.getAction();
            int pureaction = action & MotionEvent.ACTION_MASK;
            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                mBaseDist = getDistance(event);
                mBaseRatio = mRatio;
            } else {
                float delta = (getDistance(event) - mBaseDist) / STEP;
                float multi = (float) Math.pow(2, delta);
                mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                //title.setTextSize(mRatio + titleSize);
               // date.setTextSize(mRatio + fontsize);

            }
        }
        return true;
    }

    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }*/

}