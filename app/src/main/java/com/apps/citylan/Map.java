package com.apps.citylan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class Map extends AppCompatActivity implements OnMapReadyCallback, AutoPermissionsListener {

    private GoogleMap mMap;
    Button btnGeneralMap, btnSatelliteMap;

    Double myLatlng[] = new Double[2]; // 내위치 위도 경도
    ArrayAdapter<String> adapter;

    //boolean isSecurity = false;
    LocationManager manager; //내 위치 찾아 관리
    LocationListener listener; //내위치 경로를 받아와 처리햐줌
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnGeneralMap=findViewById(R.id.generalMap);
        btnSatelliteMap=findViewById(R.id.satelliteMap);

        AutoPermissions.Companion.loadAllPermissions(this,100);

        //일반지도 버튼 클릭 시
        btnGeneralMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        //위성지도 버튼 클릭시
        btnSatelliteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        mapFragment.getMapAsync(this);
       /* if(isSecurity==true){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setMylocation();
                    Log.i("테스트중","setMylocation() 부른 후 : "+myLatlng[0]);
                    Log.i("테스트중","setMylocation() location : "+location);
                    if(location != null){
                        tourMove(myLatlng);
                    }else{
                        showToast("내 위치 찾는 중..");
                    }
                    tourMove(myLatlng);
                }
            },500);
        }else {
            showToast("내 위치 접근을 거부해 찾을 수 없습니다.");
        }*/


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMylocation();

        if(location != null){
            Log.i("테스트중","onMapReady location : "+myLatlng[0]);
            tourMove(myLatlng);
        }else{
            showToast("내 위치 찾는 중..");
        }
        tourMove(myLatlng);
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    //토스트 메서드
    void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    // 내 위치찾는 메서드
    void setMylocation(){
        manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE); // 위치서비스 이용
        try {
            //gps나 네트워크 중 빠른 걸로 내 위치를 찾음
            location =manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Log.i("테스트중","setMylocation() before : "+location);
            if (location != null){

                myLatlng[0]=location.getLatitude();
                myLatlng[1]=location.getLongitude();

                Log.i("테스트중"," myLatlng[0] : "+location.getLatitude());
                Log.i("테스트중"," myLatlng[1] : "+location.getLongitude());

            }else {
                showToast("내위치 찾는 중");
            }
            //10초마다 1m마다 내위치 변경하는 것을 찾음
            MyListner myListner = new MyListner();
            manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,
                    1,myListner);
        }catch (SecurityException e){
            showToast("내 위치를 찾을 수 없습니다.");
        }
    }

    //퍼미션 결과 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this,100,permissions,this);
    }

    //내위치 이동 메서드
    void tourMove(Double latlngLocation[]){

        LatLng seoule = new LatLng(latlngLocation[0], latlngLocation[1]);
        //mMap.addMarker(new MarkerOptions().position(seoule).title("관광지 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoule,15));
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(seoule);

        markerOpt.title("내 가방 위치");
        markerOpt.snippet("현재 가방 위치입니다.");


        mMap.addMarker(markerOpt).showInfoWindow();

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
    class MyListner implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            //위치변동시
            myLatlng[0] = location.getLatitude();
            myLatlng[1] = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status){
                case LocationProvider.OUT_OF_SERVICE:
                    showToast("서비스 지역을 벋어났습니다.");
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
            //위치정보 껐을떄
            showToast("현재 서비스 사용 불가능 상태");
        }
    }
}