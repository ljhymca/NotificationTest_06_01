package com.sample.range;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.Utmk;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 1000;

    TextView sizeBarView;
    static int counter = 0;

    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* MapFragment mapFragment= (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);//지도 객체 생성

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();

        }
        mapFragment.getMapAsync(this);
        */
        MapFragment mapFragment= (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);//지도 객체 생성
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        locationSource = new FusedLocationSource(this,ACCESS_LOCATION_PERMISSION_REQUEST_CODE); //위치코드
        naverMap.setLocationSource(locationSource); //지도상의 로케이션 활성화

        UiSettings uiSettings = naverMap.getUiSettings(); //ui 세팅에 대한 객체
        uiSettings.setLocationButtonEnabled(true);

        Marker marker2 = new Marker();
        marker2.setPosition(new LatLng(1, 1));//일단 마커 위치지정
        marker2.setMap(naverMap);//마커 생성
        CircleOverlay markerCircle = new CircleOverlay();//원 생성

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();//오버레이 생성
        locationOverlay.setVisible(true);//오버레이생성
        locationOverlay.setPosition(new LatLng(37.5670135, 126.9783740));//좌표지정
        //SeekBar할당
        SeekBar sizeBar = (SeekBar) findViewById(R.id.sizeBar);
        final TextView sizeBarView =(TextView)findViewById(R.id.sizeBarView);

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeBarView.setText(""+progress);
                markerCircle.setRadius(progress);//원 반지름
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        naverMap.setOnMapClickListener((point, coord)-> { //지도 화면클릭시
            //Toast.makeText(this, getString(R.string.format_map_click, coord.latitude, coord.longitude), Toast.LENGTH_SHORT).show();
            String progress = sizeBarView.getText().toString();
            final long range = Long.parseLong(progress);
            marker2.setPosition(new LatLng(coord.latitude, coord.longitude));//클릭 좌표로 마커 위치 이동
            markerCircle.setCenter(coord);//원 중심을 선택한 위치로
            markerCircle.setRadius(range);//원 반지름
            markerCircle.setColor(0x4000FFFF);//불투명도 지정
            markerCircle.setMap(naverMap);
        //   sendbutton = (Button) findViewById(R.id.sendButton);
            long now = System.currentTimeMillis();//시간생성
            Date date = new Date(now);
            SimpleDateFormat Now = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = Now.format(date);



            //여기서 타이머 설정 해서 넘겨주면 될 듯
            TimerTask tt = new TimerTask() {
                @Override
                public void run() { //타이머
                    databaseReference.child("id").child(formatDate).child("latitude").setValue(coord.latitude,counter);//위도
                    databaseReference.child("id").child(formatDate).child("longitude").setValue(coord.longitude,counter);//경도
                    databaseReference.child("id").child(formatDate).child("range").setValue(range,counter);//원 범위
                    databaseReference.child("id").child("position").setValue(locationSource,counter);
                   Log.e("카운터:", String.valueOf(counter)); //2초마다 출력하기, 로그 캣에
                    counter++;

                }
            };

            Timer timer = new Timer(); //타이머
            timer.schedule(tt, 0, 2000);//2초 설정

               //     databaseReference.child("id").child(formatDate).child("latitude").setValue(coord.latitude);//위도
               //     databaseReference.child("id").child(formatDate).child("longitude").setValue(coord.longitude);//경도
            // databaseReference.child("id").child(formatDate).child("range").setValue(range);//원 범위
                    //id 값 넣을 자리


        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_REQUEST_CODE:
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }
}
