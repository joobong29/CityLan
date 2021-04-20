package com.apps.citylan;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Setup extends AppCompatActivity {
    Switch sw_noti, sw_find, sw_vib;
    SeekBar sb_sound;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        sw_noti=findViewById(R.id.sw_noti);
        sw_find=findViewById(R.id.sw_find);
        sw_vib=findViewById(R.id.sw_vib);
        sb_sound=findViewById(R.id.sb_sound);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sw_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        sw_find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        sb_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override //int i가 진행률 1~100으로 나타내서 progress로 바꿈
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //textview.setText("현재진행률 : " +progress + "%");
            }

            @Override  //시크바 터치
            public void onStartTrackingTouch(SeekBar seekBar) {
                showToast("");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sw_vib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

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
