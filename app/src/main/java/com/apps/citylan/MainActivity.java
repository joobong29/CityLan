package com.apps.citylan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    ImageView ivPower;
    TextView tvPowerText;
    Button btn_conn, btn_map, btn_setup;
    boolean i=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPower=findViewById(R.id.ivPower);
        tvPowerText=findViewById(R.id.tvPowerText);
        btn_conn=findViewById(R.id.btn_conn);
        btn_map=findViewById(R.id.btn_map);
        btn_setup=findViewById(R.id.btn_setup);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        AutoPermissions.Companion.loadAllPermissions(this,100);

        btn_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Foreground.class);
                if(Build.VERSION.SDK_INT>=26) {
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }

                //연결 이미지 전환
                if(i == true){
                    ivPower.setImageResource(R.drawable.poweron);
                    tvPowerText.setText("연결됨");
                    showToast("연결완료");
                    i=false;
                }else {
                    ivPower.setImageResource(R.drawable.poweroff);
                    tvPowerText.setText("연결되지 않음");
                    showToast("연결해제");
                    i =true;
                    stopService(intent);
                }
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Map.class);
                startActivity(intent);
            }
        });

        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Setup.class);
                startActivity(intent);
            }
        });
    }
    //퍼미션 결과 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this,100,permissions,this);
    }

    @Override
    public void onDenied(int i, @NotNull String[] strings) {

    }

    @Override
    public void onGranted(int i, @NotNull String[] strings) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}