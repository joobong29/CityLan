package com.apps.citylan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import static com.apps.citylan.MainActivity.context_main;

public class Map extends AppCompatActivity implements OnMapReadyCallback, AutoPermissionsListener {

    private GoogleMap mMap;
    Button btnMode;
    ImageView imgLocation;
    LinearLayout lySign;
    TextView tvDistance;

    Double myLatlng[] = new Double[2]; // 내위치 위도 경도
    Double bagLatlng[] = new Double[2]; // 가방위치 위도 경도
    double bagLat, bagLng;
    double myLat, myLng;
    int meter; //거리 차이 계산 변수
    ArrayAdapter<String> adapter;

    boolean click = true;
    LocationManager manager; //내 위치 찾아 관리
    LocationManager bagManager; //가방 위치 찾아 관리
    Location myLocation,bagLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnMode = findViewById(R.id.btnMode);
        imgLocation = findViewById(R.id.imgLocation);
        lySign = findViewById(R.id.lySign);
        tvDistance = findViewById(R.id.tvDistance);

        AutoPermissions.Companion.loadAllPermissions(this, 100);

        //Intent gintent = getIntent();
        bagLat=37.350305;
        bagLng=127.110089;

        //bagLat=37.394108;
        //bagLng=127.240719;

        //버튼 클릭(위성, 일반)
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    btnMode.setText("위성");
                    click = false;
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    btnMode.setText("일반");
                    click = true;
                }
            }
        });

        ActionBar bar = getSupportActionBar();
        bar.setTitle("위치추적");
        bar.setDisplayHomeAsUpEnabled(true);


        //내위치 버튼(이미지) 클릭시
        imgLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgLocation.setImageResource(R.drawable.target_touch);
                        tourMove(myLatlng);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgLocation.setImageResource(R.drawable.target);
                        break;
                }
                return true;
            }
        });
        setMylocation();
        setBaglocation();
        //거리 계산
        calculatedDistance(myLat, myLng, bagLat, bagLng);
        mapFragment.getMapAsync(this);

    }//END_onCreate()


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean find = PreferenceManager.getBoolean(context_main, "find");



        if (myLocation != null) {
            Log.i("테스트중", "onMapReady location : " + myLatlng[0]);
            tourMove(myLatlng);
        } else {
            showToast("내 위치 찾는 중..");
        }
        if (find == true){
            if (bagLocation != null) {
                bagMove(bagLatlng);
                Log.i("테스트중", "find : " + find);
                Log.i("테스트중", "bagLocation : " + bagLocation);
            } else {
                showToast("가방 위치 찾는 중");
            }
        }else {

        }

        tourMove(myLatlng);
    }

    //토스트 메서드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // 내 위치찾는 메서드
    void setMylocation() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 위치서비스 이용
        try {
            //gps나 네트워크 중 빠른 걸로 내 위치를 찾음
            myLocation = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Log.i("테스트중", "setMylocation() before : " + myLocation);
            if (myLocation != null) {

                myLatlng[0] = myLocation.getLatitude();
                myLatlng[1] = myLocation.getLongitude();

                myLat = myLatlng[0];
                myLng = myLatlng[1];

                Log.i("테스트중", " myLatlng[0] : " + myLocation.getLatitude());
                Log.i("테스트중", " myLatlng[1] : " + myLocation.getLongitude());
                Log.i("테스트중" , "myLat : " + myLat);
                Log.i("테스트중" , "myLng : " + myLng);

            } else {
                showToast("내위치 찾는 중");
            }
            //10초마다 1m마다 내위치 변경하는 것을 찾음
            MyListner myListner = new MyListner();
            manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000,
                    1, myListner);
        } catch (SecurityException e) {
            showToast("내 위치를 찾을 수 없습니다.");
        }
    }

    // 가방위치 찾는 메서드
    void setBaglocation() {
        bagManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 위치서비스 이용
        try {
            //gps나 네트워크 중 빠른 걸로 내 위치를 찾음
            bagLocation = bagManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            Log.i("테스트중", "setBaglocation() before : " + bagLocation);
            if (bagLocation != null) {
                bagLatlng[0] = bagLat;  bagLatlng[1] = bagLng;
                Log.i("테스트중", "bagLat : " + bagLat);
                Log.i("테스트중", "bagLan : " + bagLng);
            } else {
                showToast("내위치 찾는 중");
            }
            //5초마다 1m마다 내위치 변경하는 것을 찾음
            MyListner myListner = new MyListner();
            bagManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000,
                    1, myListner);
        } catch (SecurityException e) {
            showToast("내 위치를 찾을 수 없습니다.");
        }
    }

    //퍼미션 결과 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, 100, permissions, this);
    }

    //내위치 이동 메서드
    void tourMove(Double latlngLocation[]) {

        LatLng seoule = new LatLng(latlngLocation[0], latlngLocation[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoule, 15));
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(seoule);

        markerOpt.title("내위치");
        markerOpt.snippet("현재 당신의 위치입니다");

        mMap.addMarker(markerOpt).showInfoWindow();

    }
    void bagMove(Double latlngLocation[]){
        LatLng bag = new LatLng(latlngLocation[0], latlngLocation[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bag, 15));
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(bag);
        if(((Setup)Setup.context_setup).setbag == true) {
            markerOpt.title("가방위치");
            markerOpt.snippet("현재 가방의 위치입니다");
            mMap.addMarker(markerOpt).showInfoWindow();
        }

    }

    //거부했을 떄
    @Override
    public void onDenied(int i, String[] strings) {
    }

    // 허용했을 떄
    @Override
    public void onGranted(int i, String[] strings) {
        setMylocation();
    }

    // 내 위치값이 변했을 때 값을 가져오는 클래스
    class MyListner implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            //위치변동 시
            myLatlng[0] = location.getLatitude();
            myLatlng[1] = location.getLongitude();
            myLat = myLatlng[0];
            myLng = myLatlng[1];
            calculatedDistance(myLat, myLng, bagLat, bagLng);
            Log.i("테스트중" , "위치계산 값 :" + myLat+","+myLng+","+bagLat+","+bagLng);
            if(meter >15 ) {
                PreferenceManager.setBoolean(context_main, "bagCheck", true);
                Intent intent = new Intent(Map.this, Foreground.class);
                if(Build.VERSION.SDK_INT>=26) {
                    startForegroundService(intent);
                }else {
                    startService(intent);
                }
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    showToast("서비스 지역을 벗어났습니다.");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    showToast("일시적으로 사용할 수 없습니다.");
                    break;
                case LocationProvider.AVAILABLE:
                    showToast("서비스 이용이 가능합니다.");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            //위치정보 켰을 때
            showToast("현재 서비스 사용 가능 상태");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //위치정보 껐을때
            showToast("현재 서비스 사용 불가능 상태");
        }
    }

    // 거리측정 메서드
    public double calculatedDistance(double myLat, double myLng, double bagLat, double bagLng) {
        Log.i("테스트중" ,"거리계산"+myLat+","+myLng+","+bagLat+","+bagLng);
        double distance=0;
        String sDistance;
        Location locationA = new Location("point A");
        locationA.setLatitude(myLat);
        locationA.setLongitude(myLng);

        Location locationB = new Location("point B");
        locationB.setLatitude(bagLat);
        locationB.setLongitude(bagLng);

        distance = locationA.distanceTo(locationB);
        Log.i("테스트중", "distance : " + distance);
        sDistance = String.valueOf(distance);
        Log.i("테스트중", "distance : " + sDistance);
        meter = Integer.parseInt(String.valueOf(Math.round(distance)));
        Log.i("테스트중", "meter :" + meter);

        lySign.setVisibility(View.VISIBLE);
        tvDistance.setText("가방과의 거리 :" + sDistance +"M");
        Toast.makeText(getApplicationContext(), sDistance, Toast.LENGTH_SHORT).show();

        return distance;
    }

    //(액션바)뒤로가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}