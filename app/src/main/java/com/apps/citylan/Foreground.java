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
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class Foreground extends Service {
    ImageButton notiBtn_vib, notiBtn_sound;

    String channelID1="notiServiceChannel1";
    String getChannelName1="notiServiceChannel1";
    String channelID2="notiServiceChannel2";
    String getChannelName2="notiServiceChannel2";
    //Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);

    //boolean setupnoti=((Setup)Setup.context_setup).setNoti; //Setup 액티비티에서 setNoti 변수 값 가져오기

    @Override
    public void onCreate() {
        super.onCreate();

        startForegroundService1();
        startForegroundService2();
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

    void startForegroundService1() {
        Intent notiIntent=new Intent(this,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this, 0, notiIntent,0);
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= 26) { // 26버전 이상이면 채널값 필요
            NotificationChannel channel1 = new NotificationChannel(channelID1,
                    getChannelName1, NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("채널정의1");
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).
                    createNotificationChannel(channel1);
            builder=new NotificationCompat.Builder(this,channelID1);
        }else {
            builder=new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.notirogo)
                .setContent(remoteViews)  //노티에 셋팅
                .setContentIntent(pIntent)
                //.setVibrate(new long[] {100,0,0,0})
                //.addAction(R.id.ntBtn_sound,"소리알림",pIntent)
                //.addAction(R.id.ntBtn_vib,"진동알람",pIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false);
        startForeground(1,builder.build());
    }

    void startForegroundService2() {
        Intent notiIntent=new Intent(this,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this, 0, notiIntent,0);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= 26) { // 26버전 이상이면 채널값 필요
            NotificationChannel channel2 = new NotificationChannel(channelID2,
                    getChannelName2, NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("채널정의2");
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).
                    createNotificationChannel(channel2);
            builder=new NotificationCompat.Builder(this,channelID2);
        }else {
            builder=new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.ic_noti_warning)
                .setContentTitle("경고") //노티에 셋팅
                .setContentIntent(pIntent)
                .setVibrate(new long[] {100,0,100,0})
                .setDefaults(Notification.DEFAULT_SOUND) //소리 알람
                .setDefaults(Notification.DEFAULT_VIBRATE) //진동 알람
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false);

    }
}