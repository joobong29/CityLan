package com.apps.citylan;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.apps.citylan.MainActivity.context_main;

public class Setup extends AppCompatActivity{
    Switch sw_noti, sw_siren, sw_find, sw_vib;

    public static Context context_setup;
    public static boolean setNoti1=true;
    public static boolean setNoti2=true;
    public static boolean setbag=true;

    boolean btOn=((MainActivity) context_main).i;
   // String bag=((MainActivity)MainActivity.context_main).bag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        context_setup = this;

        sw_noti=findViewById(R.id.sw_noti);
        sw_find=findViewById(R.id.sw_find);
        sw_vib=findViewById(R.id.sw_vib);
        sw_siren=findViewById(R.id.sw_siren);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Intent intent=new Intent(Setup.this,Foreground.class);
        //블루투스 미연결 시 스위치 비활성화

        Intent intent=new Intent(Setup.this,Foreground.class);

        //옵션 탭 닫고 다시 열 때 스위치 상태값 유지
        sw_noti.setChecked(PreferenceManager.getBoolean(context_main, "noti"));
        sw_siren.setChecked(PreferenceManager.getBoolean(context_main, "siren"));
        sw_find.setChecked(PreferenceManager.getBoolean(context_main, "find"));
        sw_vib.setChecked(PreferenceManager.getBoolean(context_main, "vib"));

        sw_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true) {
                    PreferenceManager.setBoolean(context_main, "noti", true);
                }else {
                    PreferenceManager.setBoolean(context_main, "noti", false);
                }
                if(Build.VERSION.SDK_INT>=26) {
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }

            }
        });

        //거리 경보 설정
        sw_siren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true) {
                    PreferenceManager.setBoolean(context_main, "siren", true);
                }else {
                    PreferenceManager.setBoolean(context_main, "siren", false);
                }
                if(Build.VERSION.SDK_INT>=26) {
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }
            }
        });

        //자동추적설정
        sw_find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true) {
                    PreferenceManager.setBoolean(context_main, "find", true);
                }else {
                    PreferenceManager.setBoolean(context_main, "find", false);
                }

            }
        });

        //열림 경보 설정
        sw_vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //가방 열림 신호를 받았을 때 = 노티가 동작하는 중일 때
                if(isChecked == true) {
                    PreferenceManager.setBoolean(context_main, "vib", true);
                }else {
                    PreferenceManager.setBoolean(context_main, "vib", false);
                }
                if(Build.VERSION.SDK_INT>=26) {
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }
            }
        });
    }
/*    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }*/

    //액션바 뒤로가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
