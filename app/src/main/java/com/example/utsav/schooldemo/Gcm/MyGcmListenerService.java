package com.example.utsav.schooldemo.Gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.utsav.schooldemo.Activities.DownloadFiles;
import com.example.utsav.schooldemo.Activities.News;
import com.example.utsav.schooldemo.Activities.NoticeAndStuff;
import com.example.utsav.schooldemo.Activities.Resources;
import com.example.utsav.schooldemo.R;
import com.example.utsav.schooldemo.app.SessionManager;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    SessionManager sessionManager;

    private static final String TAG = "RegIntentService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + data);
        Log.d(TAG, "Message: " + message);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.setKeyFetch(true);

        String type = data.getString("type");
       /* if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
*/
        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, type);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String type) {
        Intent intent = null;
        if (type.equals("news")) {
            intent = new Intent(this, News.class);
        } else if (type.equals("file")) {
            intent = new Intent(this, DownloadFiles.class);
        } else if (type.equals("resource")) {
            intent = new Intent(this, Resources.class);
        } else {
            intent = new Intent(this, NoticeAndStuff.class);
        }

        //intent.putExtra("key",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("nBulletin")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}