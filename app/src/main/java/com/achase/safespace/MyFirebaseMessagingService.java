package com.achase.safespace;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Adam Chase on 4/22/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String MESSAGE = "message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        //Log.d(TAG, "Message Received");

        if(remoteMessage.getData().size() > 0){

            Log.d(TAG, "MESSAGE RECEIVED");
            Map<String, String> data = remoteMessage.getData();

            String message = data.get(MESSAGE);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentTitle("Emergency In Your Area!")
                            .setContentText(message);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, EditProfileActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(EditProfileActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }else
            Log.d(TAG,"MESSAGE CANNOT BE RECEIVED: " + remoteMessage.getData().size());
    }
}
