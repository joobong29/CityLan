package com.apps.citylan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class Foreground extends Service {
    ImageButton notiBtn_vib, notiBtn_sound;
    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override //서비스 시작 명령
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override //서비스 중지 명령
    public void onDestroy() {
        super.onDestroy();
    }

    void startForegroundService() {
        Intent notiIntent=new Intent(this,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this, 0, notiIntent,0);
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= 26) {
            String channelID="notiServiceChannel";
            NotificationChannel channel = new NotificationChannel(channelID,
                    "알림채널", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("채널정의");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {100,0,0,0});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).
                    createNotificationChannel(channel);
            builder=new NotificationCompat.Builder(this,channelID);
        }else {
            builder=new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.notirogo)
                .setContent(remoteViews)
                .setContentIntent(pIntent)
                .setVibrate(new long[] {100,0,0,0})
                //.addAction(R.id.ntBtn_sound,"소리알림",pIntent)
                .addAction(R.id.ntBtn_vib,"진동알람",pIntent)
                .setAutoCancel(true);
        startForeground(1,builder.build());

    }

    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}