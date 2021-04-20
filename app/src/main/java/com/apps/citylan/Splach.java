package com.apps.citylan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class Splach extends AppCompatActivity {
    ProgressBar pb;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();

            }
        },3000);
        prog();


    }

    private void prog() {

        pb = (ProgressBar)findViewById(R.id.pb);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count+=3;
                pb.setProgress(count);

                if(count==100){
                    timer.cancel();
                }
            }
        };
        timer.schedule(timerTask,0,100);
    }
}