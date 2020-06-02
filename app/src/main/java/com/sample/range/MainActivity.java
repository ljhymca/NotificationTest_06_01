package com.sample.range;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 1000;

    TextView sizeBarView;
    static int counter = 0;

    NotificationManager manager;
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    //위도와 경도 초기화
    double latitude = 0;
    double longitude = 0;
    Marker marker2;
    AlertReceiver receiver;
    PendingIntent proximityIntent;
    boolean inside = true;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);//지도 객체 생성
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //   LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //   manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,(LocationListener)this);

    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        locationSource = new FusedLocationSource(this, ACCESS_LOCATION_PERMISSION_REQUEST_CODE); //위치코드
        naverMap.setLocationSource(locationSource); //지도상의 로케이션 활성화

        UiSettings uiSettings = naverMap.getUiSettings(); //ui 세팅에 대한 객체
        uiSettings.setLocationButtonEnabled(true);


        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() { // 실시간 위치를 토스트를 통해서 위도,경도를 표시해주는 곳.
            @Override
            public void onLocationChange(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
        });

        //여기서 타이머 설정 해서 넘겨주면 될 듯
        TimerTask tt = new TimerTask() {
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
                            showNoti();
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

    private class AlertReceiver {
    }


    // 경도, 위도를 분석하여 근접 알람을 알려주는 구간
   /* (marker2 != null) {
        try {
            if() {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);
                isLocRequested = true;
            }
            else
                Toast.makeText(this, "Permission이 없습니다..", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    } else if (view.getId() == R.id.alert) {
        // 근접 경보를 받을 브로드캐스트 리시버 객체 생성 및 등록
        // 액션이 kr.ac.koreatech.msp.locationAlert인 브로드캐스트 메시지를 받도록 설정
        receiver = new AlertReceiver();
        IntentFilter filter = new IntentFilter("com.sample.range.MainActivity");
        registerReceiver(receiver, filter);

        // ProximityAlert 등록을 위한 PendingIntent 객체 얻기
        Intent intent = new Intent("com.sample.range.MainActivity");
        proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        try {
            // 근접 경보 등록 메소드
            // void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)
            // 아래 위도, 경도 값의 위치는 2공학관 420호 창가 부근

            locationSource.addProximityAlert(latitude, longtitude, 20, -1, proximityIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        isAlertRegistered = true;
    }else if(view.getId() == R.id.alert_release){
        // 자원 사용 해제
        try {
            if(isAlertRegistered) {
                locManager.removeProximityAlert(proximityIntent);
                unregisterReceiver(receiver);
            }
            Toast.makeText(getApplicationContext(),"근접 경보 해제 되었습니다.",Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        // 자원 사용 해제
        try {
            if(isLocRequested) {
                locManager.removeUpdates(this);
                isLocRequested = false;
            }
            if(isAlertRegistered) {
                locManager.removeProximityAlert(proximityIntent);
                unregisterReceiver(receiver);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
*/
    public void showNoti() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                ));

                builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            }
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContentTitle("안되는이유좀");
        builder.setContentText("왜안되냐");
        builder.setSmallIcon(android.R.drawable.ic_menu_view);
        Notification noti = builder.build();

        manager.notify(1, noti);
    }
}
