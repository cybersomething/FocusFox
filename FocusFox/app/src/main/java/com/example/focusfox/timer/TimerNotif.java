package com.example.focusfox.timer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.focusfox.timer.Timer.channelId;

public class TimerNotif extends BroadcastReceiver {

    //Declaring the variables to be used for the notification
    public static String NOTIFICATION_ID = "notificationId";
    public static String NOTIFICATION = "notification";

    //This function is used when the Broadcast Reciever is receiving a broadcast intent
    public void onReceive(Context context, Intent intent){
        //When an intent is received, the notification channel is created for the Timer
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE );
        Notification notification = intent.getParcelableExtra( NOTIFICATION );
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ){
            int importance = NotificationManager. IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( channelId , "Timer" , importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}