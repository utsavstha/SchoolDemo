package com.example.utsav.schooldemo.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utsav.schooldemo.DBClasses.NewsDB;
import com.example.utsav.schooldemo.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailsNews extends AppCompatActivity {

    TextView title;
    TextView date;
    HtmlTextView message;
    String filePath;
    float mRatio = 1.0f;
    int downloadedSize = 0, totalSize = 0;
    float fontsize = 18;
    // float titleSize = 30;
    SeekBar seekBar;
    FloatingActionButton fab;
    String id, path, url;
    ImageView imageView;
    NewsDB newsDB;
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
                ActivityCompat.finishAffinity(DetailsNews.this);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab_details);
        title = (TextView) findViewById(R.id.title_details);
        date = (TextView) findViewById(R.id.date_details);
        message = (HtmlTextView) findViewById(R.id.message_details);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        imageView = (ImageView) findViewById(R.id.image_details_news);

        title.setTypeface(null, Typeface.BOLD);
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        message.setHtmlFromString(getIntent().getStringExtra("message"), new HtmlTextView.LocalImageGetter());

        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        path = getIntent().getStringExtra("path");
        //title.setTextSize(mRatio + titleSize);
        //date.setTextSize(mRatio + fontsize);
        message.setTextSize(mRatio + fontsize);

        if(path.equalsIgnoreCase("xxx")&& !url.isEmpty()){
            new Thread(new Runnable() {
                public void run() {
                    downloadFile(url, Integer.parseInt(id));
                }
            }).start();

        }else if(!url.isEmpty()){
            imageView.setVisibility(View.VISIBLE);

            Bitmap myBitmap = BitmapFactory.decodeFile(path);

            imageView.setImageBitmap(myBitmap);
        }

        newsDB = new NewsDB(getApplicationContext());

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
                                        + "\n\nMessage: " + getIntent().getStringExtra("message");
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
        ActivityCompat.finishAffinity(this);
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

            final FileOutputStream fileOutput = new FileOutputStream(new File(directory,
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
                       // progress.setProgress((int) per);
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
                   // progress.hide();
                    newsDB.updatePath(filePath,id);
                    imageView.setVisibility(View.VISIBLE);

                    Bitmap myBitmap = BitmapFactory.decodeFile(filePath);

                    imageView.setImageBitmap(myBitmap);

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
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                //Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG).show();
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

}