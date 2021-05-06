package com.apps.citylan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    public static Context context_main;
    ImageView ivPower;
    TextView tvPowerText;
    Button btn_conn, btn_map, btn_setup;
    BluetoothAdapter bluetoothAdapter; //블루투스 관련 기능 지원
    int pairedDeviceCount=0; //페어링된 장치 수를 나타낼 변수
    Set<BluetoothDevice> devices; //디바이스 목록을 담을 클래스 객체
    BluetoothDevice remotedevice; //컨트롤할 블루투스 장치
    BluetoothSocket bluetoothSocket; //통신이기때문에 소켓 필요
    OutputStream outputStream=null; //신호를 보냄
    InputStream inputStream=null; //신호를 받음
    Thread workerThread=null; //보낼 때 받을 수 있는 동시 수행 작업 용
    String strDelimiter="\n"; //마지막에 줄바꿈 신호를 보내서 데이터가 끝나는 것을 알림
    public static boolean i=true;  //연결 버튼 상태 변수
    public static boolean bag=false; //가방 열림 신호 받을 변수
    char charDelimiter='\n';
    byte readBuffer[];
    int readBufferPosition;
    double lat, lan;
    boolean dataZero=false;  //데이터를 수신했다는 것을 판별하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPower=findViewById(R.id.ivPower);
        tvPowerText=findViewById(R.id.tvPowerText);
        btn_conn=findViewById(R.id.btn_conn);
        btn_map=findViewById(R.id.btn_map);
        btn_setup=findViewById(R.id.btn_setup);
        context_main = this;

        PreferenceManager.setBoolean(context_main, "noti", true);
        PreferenceManager.setBoolean(context_main, "siran", true);
        PreferenceManager.setBoolean(context_main, "find", true);
        PreferenceManager.setBoolean(context_main, "vib", true);
        PreferenceManager.setBoolean(context_main, "dataZero", false);
        PreferenceManager.setBoolean(context_main, "bagCheck", false);
        ActionBar bar = getSupportActionBar();//액션바 숨기기
        bar.hide();

        AutoPermissions.Companion.loadAllPermissions(this,100);

        btn_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == true) {
                    checkBluetooth();
                }else {
                    ivPower.setImageResource(R.drawable.poweroff);
                    btn_conn.setText("기기연결");
                    tvPowerText.setText("연결되지 않음");
                    showToast("연결해제");
                    btn_setup.setEnabled(false);
                    btn_map.setEnabled(false);
                    try {
                        workerThread.interrupt(); //스레드 중단
                        inputStream.close();
                        outputStream.close();
                        bluetoothSocket.close();
                        Intent intent=new Intent(MainActivity.this,Foreground.class);
                        stopService(intent);
                    }catch (Exception e) {
                        showToast("앱 종료 중 에러 발생");
                    }
                    i =true;
                }
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginListenForDate();
                Intent intent = new Intent(getApplicationContext(),Map.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lan",lan);

                Log.i("test","lat : "+lat);
                Log.i("test","lan : "+lan);

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

    //스마트폰의 블루투스 지원 여부 검사
    void checkBluetooth() {
        //블루투스 어뎁터 반환, 연결 안되는 것에 하면 null을 반환
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter==null) {
            showToast("블루투스를 지원하지 않는 장치입니다.");
            i=true;
        }else {
            //장치가 블루투스를 지원하는 경우
            if(!bluetoothAdapter.isEnabled()) { //BT활성화 = true, 비활성화 false
                //블루투스를 꺼놨을 때 작동하여 연결할건지 물음
                Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 10);//양방향 액티비티 //requestCode
            }else {// 블루투스를 켜놨을 때
                selectDevice();//페어링 목록 메서드 호출
            }
        }
    }

    //페어링된 장치 목록 출력 및 선택 메서드
    void selectDevice(){
        devices=bluetoothAdapter.getBondedDevices(); //블루투스 장치 목록
        pairedDeviceCount=devices.size();
        if(pairedDeviceCount==0) { //페어링된 장치가 없는 경우
            showToast("페어링된 장치가 하나도 없습니다.");
        }else {
            //다이얼로그 대화상자로 목록 나열
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("블루투스 장치 선택");
            List<String> listItems=new ArrayList<String>();
            for(BluetoothDevice device:devices) { //향상된 for문
                listItems.add(device.getName());
            }
            listItems.add("취소");
            //객체라 동적배열을 썻지만 dialog대화상자는 동적배열 불가. toArray 동적배열을 일반 배열화 시킴
            final CharSequence[] items=listItems.toArray(new CharSequence[listItems.size()]);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if(which==pairedDeviceCount){ //페어링 목록 선택 안하고 취소 선택 시
                        showToast("취소를 선택했습니다.");
                    }else { //페어링 목록에서 선택 시
                        connectToSelectedDevice(items[which].toString());
                    }
                }
            });
            builder.setCancelable(false); //뒤로가기 버튼 사용 금지(코드 꼬임)
            AlertDialog dlg=builder.create(); //다이얼로그 대화상자 표시
            dlg.show();
        }
    }
    //선택한 블루투스 장치와 연결 메서드
    void connectToSelectedDevice(String selectedDeviceName) {
        remotedevice=getDeviceFromBoundList(selectedDeviceName);//
        UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try { //소켓이 연결된 경우
            i=false;
            bluetoothSocket=remotedevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();//기기와 연결 완료
            ivPower.setImageResource(R.drawable.poweron); //연결 완료 시 이미지 변경
            tvPowerText.setText("연결됨");  //연결 완료 시 텍스트뷰 변경
            showToast("연결완료"); //연결 완료 시 토스트로 알림
            Intent intent=new Intent(MainActivity.this,Foreground.class);
            if(Build.VERSION.SDK_INT>=26) {
                startForegroundService(intent);
            }else {
                startService(intent);
            }
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();
            btn_conn.setText("연결 해제");
            btn_setup.setEnabled(true);
            btn_map.setEnabled(true);
            beginListenForDate();


        }catch (Exception e){ //소켓이 연결 안될 경우
            showToast("소켓 연결이 되지 않습니다.");
        }
    }

    //데이터 수신 준비 및 처리 메서드
    void beginListenForDate() {
        final Handler handler=new Handler();
        readBuffer=new byte[1024]; //수신 버퍼
        readBufferPosition=0; //버퍼 내 수신 문자 저장 위치의 기본값 설정
        //문자열 수신 쓰레드
        workerThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable=inputStream.available(); //수신 데이터 확인
                        if (bytesAvailable > 0) { //아두이노에서 신호를 보냈음
                            Log.i("테스트중","데이터 들어왔는지 : " + bytesAvailable);
                            byte[] packetBytes=new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i=0; i<bytesAvailable; i++) { //받은 데이터만큼 처리
                                byte b=packetBytes[i];
                                if (b==charDelimiter){ //데이터 끝 신호를 받았을 때
                                    byte[] encodeBytes=new byte[readBufferPosition];
                                    System.arraycopy(readBuffer,0,encodeBytes,0,encodeBytes.length);
                                    final String latData=new String(encodeBytes,"US-ASCII"); //data는 아두이노에서 받은 데이터를 담은 변수
                                    final String lanData=new String(encodeBytes,"US-ASCII"); //data는 아두이노에서 받은 데이터를 담은 변수
                                    //final String openbag=new String(encodeBytes,"US-ASCII"); //data는 아두이노에서 받은 데이터를 담은 변수
                                    //Log.i("테스트중","GPS값1 : " + latData +""+ lanData);
                                    readBufferPosition=0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //data변수에 수신된 문자열에 대한 처리작업
                                            lat = Double.parseDouble(latData);
                                            lan = Double.parseDouble(lanData);
                                            //Log.i("테스트중","GPS값2 : " + latData +""+ lanData);
                                            dataZero=true; //가방에서 신호가 왔을 때 true
                                            //bag = openbag;
                                            /*if (bag!=null) {
                                                Intent intent=new Intent(MainActivity.this,Foreground.class);
                                                if(Build.VERSION.SDK_INT>=26) {
                                                    startForegroundService(intent);
                                                }else {
                                                    startService(intent);
                                                }
                                            }*/
                                        }
                                    });
                                }else { //데이터 끝 신호가 안왔을 때
                                    readBuffer[readBufferPosition++]=b; //버퍼 값을 쌓음
                                    Log.i("테스트중","신호 끊김 찾는 중1");
                                }
                            }

                        }

                    }catch (IOException e) {
                        Log.i("테스트중","신호 끊김 찾는 중2");
                        showToast("데이터 수신 중 오류가 발생했습니다.");
                    }

                }
                if(bag==false){
                    if(dataZero==true) {
                        bag=true; //가방에서 신호가 왔을 때
                        Intent intent = new Intent(MainActivity.this,Foreground.class);
                        if(Build.VERSION.SDK_INT>=26) {
                            startForegroundService(intent);
                        }else {
                            startService(intent);
                        }
                        dataZero=false;
                    }
                }

            }

        });
        workerThread.start();
    }

    //페어링된 블루투스 장치를 이름으로 찾는 객체 변수
    BluetoothDevice getDeviceFromBoundList(String name) {
        BluetoothDevice selectedDevice=null;
        for(BluetoothDevice device:devices) {
            if(name.equals(device.getName())){
                selectedDevice=device;
                break;
            }
        }
        return selectedDevice;//객체형이라 리턴 필요
    }


    //데이터 송신 메서드(안드로이드에서 아두이노로 데이터 전송)
    void sendData(String msg) {
        msg+=strDelimiter;
        try {
            outputStream.write(msg.getBytes()); //문자열 전송
        }catch (Exception e) {
            showToast("문자열 전송 도중에 오류가 발생했습니다.");

        }
    }

    //앱이 종료 될 때 수행할 메서드
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            workerThread.interrupt(); //스레드 중단
            inputStream.close();
            outputStream.close();
            bluetoothSocket.close();
        }catch (Exception e){
            showToast("앱 종료 중 에러 발생");
        }
    }

    //앱에 결과화면 출력 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if(resultCode==RESULT_OK) {//허용할까요? 했을 때 yes 누른 경우
                    selectDevice();//(10) 허용해서 블루투스가 켜졌을 때 페어링 선택 목록 호출
                }else if(resultCode==RESULT_CANCELED){
                    showToast("블루투스 활성화를 취소했습니다.");
                    ivPower.setImageResource(R.drawable.poweroff);
                    tvPowerText.setText("연결되지 않음");
                    showToast("연결해제");
                    i =true;
                    Intent intent = new Intent(MainActivity.this,Foreground.class);
                    stopService(intent);
                }
                break;
        }
    }
}