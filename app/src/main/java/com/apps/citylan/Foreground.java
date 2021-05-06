package com.apps.citylan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static com.apps.citylan.MainActivity.context_main;
import static com.apps.citylan.Setup.context_setup;

public class Foreground extends Service {
    ImageButton notiBtn_vib, notiBtn_sound;

    String channelID1="notiServiceChannel1";
    String getChannelName1="notiServiceChannel1";
    String channelID2="notiServiceChannel2";
    String getChannelName2="notiServiceChannel2";
    String channelID3="notiServiceChannel3";
    String getChannelName3="notiServiceChannel3";

    boolean bagOpen=((MainActivity)MainActivity.context_main).bag;  //MainActivity의 아두이노에서 받은 신호 값을 가진 변수
    boolean btOn=((MainActivity)MainActivity.context_main).i;  //MainActivity의 블루투스 연결 상태 값을 가진 변수


    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Ringtone rt;
    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService1(btOn);
        Log.i("테스트중","result : "+btOn);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override //서비스 시작 명령
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("테스트중2","result2 : "+btOn);

        boolean noti = PreferenceManager.getBoolean(context_main, "noti");
        boolean vib = PreferenceManager.getBoolean(context_main, "vib");
        boolean siren = PreferenceManager.getBoolean(context_main, "siren");
        boolean find = PreferenceManager.getBoolean(context_main, "find");
        boolean bagCheck = PreferenceManager.getBoolean(context_main, "bagCheck");
        Log.i("테스트중","preference result : "+noti+","+vib+","+siren+","+find);
        //연결 노티피케이션 실행 조건
        if (btOn==false) {  //false가 연결 된 상태.
            startForegroundService1(noti);
        }
        //가방 열림 노티피케이션 실행문
        if (bagOpen==true) {
            startForegroundService2(vib);
        }
        //가방 거리 알림 노티피케이션 실행문
        if (bagCheck == true) {
            startForegroundService3(siren);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override //서비스 중지 명령
    public void onDestroy() {
        super.onDestroy();
    }

    void startForegroundService1(boolean a) {
        Intent notiIntent=new Intent(this,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this, 1, notiIntent,0);
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
        //설정이 true 일 때 노티 생성
        if(a==true) {
            builder.setSmallIcon(R.drawable.notirogo)
                    .setContent(remoteViews)  //노티에 셋팅
                    .setContentIntent(pIntent)
                    //.setVibrate(new long[] {100,0,0,0})
                    //.addAction(R.id.ntBtn_sound,"소리알림",pIntent)
                    //.addAction(R.id.ntBtn_vib,"진동알람",pIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            startForeground(1,builder.build());
            Log.i("테스트중3","result3 : "+a);
        }else {
                stopForeground(true);
            Log.i("테스트중4","result4 : "+a);
        }

    }

    void startForegroundService2(boolean a) {
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
        if (a==true) {
            builder.setSmallIcon(R.drawable.ic_noti_warning)
                    .setContentTitle("경고")
                    .setContentIntent(pIntent)
                    .setVibrate(new long[] {100,0,100,0})
                    .setDefaults(Notification.DEFAULT_VIBRATE) //진동 알람
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            startForeground(2,builder.build());
        }else {
            stopForeground(true);
        }
    }

    void startForegroundService3(boolean a) {
        Intent notiIntent=new Intent(this,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(this, 0, notiIntent,0);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= 26) { // 26버전 이상이면 채널값 필요
            NotificationChannel channel3 = new NotificationChannel(channelID3,
                    getChannelName3, NotificationManager.IMPORTANCE_DEFAULT);
            channel3.setDescription("채널정의3");
            channel3.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).
                    createNotificationChannel(channel3);
            builder=new NotificationCompat.Builder(this,channelID3);
        }else {
            builder=new NotificationCompat.Builder(this);
        }
        if (a==true) {
            builder.setSmallIcon(R.drawable.ic_noti_warning)
                    .setContentTitle("경고")
                    .setContentText("가방과 멀어졌습니다.")
                    .setContentIntent(pIntent)
                    .setVibrate(new long[] {100,100,100,100})
                    .setDefaults(Notification.DEFAULT_VIBRATE) //진동 알람
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            rt = RingtoneManager.getRingtone(getApplicationContext(),notification);
            rt.play();
            startForeground(3,builder.build());
        }else {
            stopForeground(true);
        }


    }
}