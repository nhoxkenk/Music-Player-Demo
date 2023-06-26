package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {

    public static String CHANNEL_ID = "channel1";
    public static String PLAY = "play";
    public static String NEXT = "next";
    public static String PREVIOUS = "previous";
    public static String EXIT = "exit";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This os a important channel for showing song!!");

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}