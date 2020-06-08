package com.sample.range;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.location.Location;

import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import android.renderscript.Sampler;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 100;

    TextView sizeBarView; //사이즈바

    int timecount = 1; //타이머 카운트 초기화

    NotificationCompat.Builder mBuilder1, mBuilder2, mBuilder3; //안드로이드에서 notification을 만드는 가장 쉬운 방법이다.


    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    //위도와 경도 초기화
    double latitude = 0;
    double longitude = 0;
    Marker marker2;

    boolean inside = true;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 네이버 지도 객체
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);//지도 객체 생성
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        // 네이버 지도 객체 끝


        PendingIntent plntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent plntent2 = PendingIntent.getActivity(MainActivity.this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent plntent3 = PendingIntent.getActivity(MainActivity.this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder1 = new NotificationCompat.Builder(MainActivity.this, "kbu1")

                .setOngoing(false) //노티피케이션유지
                .setSmallIcon(R.drawable.mylocation)
                .setContentTitle("위치 알림")
                .setContentText("응 성서대")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(plntent2);

        mBuilder2 = new NotificationCompat.Builder(MainActivity.this, "kbu2")

                .setOngoing(true) //노티피케이션유지
                .setSmallIcon(R.drawable.mylocation)
                .setContentTitle("위치 알림")
                .setContentText("성서대 아냐")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(plntent3);

        //파이어베이스 값 가져오기

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Log.e("Main","single"+ snapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE); //위치코드
        naverMap.setLocationSource(locationSource); //지도상의 로케이션 활성화
        UiSettings uiSettings = naverMap.getUiSettings(); //ui 세팅에 대한 객체
        uiSettings.setCompassEnabled(true);// 나침반 활성화
        uiSettings.setLogoClickEnabled(false); // 네이버 클릭 비활성화

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);//키자마자 트래킹 모드


        // Notification으로 작업을 수행할 때 인텐트가 실행되도록 합니다.


        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() { // 실시간 위치를 토스트를 통해서 위도,경도를 표시해주는 곳.
            @Override
            public void onLocationChange(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
        });



        //여기서 타이머 설정 해서 넘겨주면 될 듯
      /*  TimerTask tt = new TimerTask() {
            @Override
            public void run() { //타이머
                databaseReference.child("id").child("myposition").child("lat").setValue(latitude, counter);
                databaseReference.child("id").child("myposition").child("lon").setValue(longitude, counter);
                //  Log.e("카운터:", String.valueOf(counter)); //2초마다 출력하기, 로그 캣에
                counter++;

                if (inside = true)
                    if (37.64775518225889 < latitude && latitude < 37.64955181282713)
                        if (127.06321675379839 < longitude && longitude < 127.0654858821362) {
                            Log.e("범위", "범위내");

                            inside = false;
                        } else if (inside = false)
                            if (latitude > 37.64955181282713 || latitude < 37.64775518225889)
                                if (longitude > 127.0654858821362 || longitude < 127.06321675379839) {
                                    Log.e("범위", "범위밖");
                                    inside = true;
                                }

            }
        };
        Timer timer = new Timer(); //타이머
        timer.schedule(tt, 0, 2000);//2초 설정

       */


        //타이머 부분(예제에 나와 있는 부분)
        TimerTask Taskact = new TimerTask() {
            public void run() { //주기적으로 실행할 작업 추가
                databaseReference.child("id").child("myposition").child("lat").setValue(latitude, timecount); //파이어 베이스에 위도 값 주기적으로 넘기기
                databaseReference.child("id").child("myposition").child("lon").setValue(longitude, timecount); //파이어 베이스에 경도 값 주기적으로 넘기기
                // Log.e("카운터:", String.valueOf(timecount));

                timecount++; //timecount 계속 누적 시키기

                /*
                NotificationManager notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26)
                {
                    notiMan.createNotificationChannel(new NotificationChannel("kbu", "한", NotificationManager.IMPORTANCE_DEFAULT));
                }
                notiMan.notify(1004, mBuilder.build());
*/

                if (inside == true)
                    if (37.64775518225889 < latitude && latitude < 37.64955181282713)
                        if (127.06321675379839 < longitude && longitude < 127.0654858821362) {
                            NotificationManager notiMan1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= 26) {
                                notiMan1.createNotificationChannel(new NotificationChannel("kbu1", "규", NotificationManager.IMPORTANCE_DEFAULT));
                            }
                            notiMan1.notify(1005, mBuilder1.build());
                            inside = false;
                            Log.e("에러","false로 변경됨");

                        } else if (inside == false)
                            if (latitude > 37.64955181282713 || latitude < 37.64775518225889)
                                if (longitude > 127.0654858821362 || longitude < 127.06321675379839) {
                                    NotificationManager notiMan2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        notiMan2.createNotificationChannel(new NotificationChannel("kbu2", "정", NotificationManager.IMPORTANCE_DEFAULT));
                                    }
                                    notiMan2.notify(1006, mBuilder2.build());
                                    inside = true;
                                    Log.e("에러","true로 변경됨");

                                }

            }
        };
        Timer timer = new Timer();
        timer.schedule(Taskact, 30, timecount * (30 * 100)); //// 60초후 첫실행, timecount분마다 계속실행


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //위치를 허가 받는 곳.
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION_REQUEST_CODE:
                locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }


}