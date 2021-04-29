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

public class Setup extends AppCompatActivity {
    RadioGroup rGroup;
    RadioButton oneMeter, threeMeter, fiveMeter;
    Switch sw_noti, sw_find, sw_vib;

    public static Context context_setup;
    public boolean setNoti=true;
    public String open;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        context_setup = this;

        sw_noti=findViewById(R.id.sw_noti);
        sw_find=findViewById(R.id.sw_find);
        sw_vib=findViewById(R.id.sw_vib);
        rGroup=findViewById(R.id.rGroup);
        oneMeter=findViewById(R.id.oneMeter);
        threeMeter=findViewById(R.id.threeMeter);
        fiveMeter=findViewById(R.id.fiveMeter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent=new Intent(Setup.this,Foreground.class);
        //연결알림설정
        sw_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true) {
                    setNoti=true;
                    if(Build.VERSION.SDK_INT>=26) {
                        startForegroundService(intent);
                    }else {
                        startService(intent);
                    }
                }else {
                    stopService(intent);
                    setNoti=false;
                }
            }
        });
        //경보유효거리설정
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (rGroup.getCheckedRadioButtonId()){
                    case R.id.oneMeter:

                        showToast("1m 초과시 경보가 울립니다.");
                        break;
                    case R.id.threeMeter:

                        showToast("3m 초과시 경보가 울립니다.");
                        break;
                    case R.id.fiveMeter:

                        showToast("5m 초과시 경보가 울립니다.");
                        break;
                }
            }
        });
        //자동추적설정
        sw_find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            }
        });
        //가방경보설정
        sw_vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true) {
                    if(Build.VERSION.SDK_INT>=26) {
                        startForegroundService(intent);
                    }else {
                        startService(intent);
                    }
                }else if(isChecked == false) {
                    stopService(intent);
                }
            }
        });
    }
    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

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
