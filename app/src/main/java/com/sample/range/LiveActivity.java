package com.sample.range;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

public class LiveActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_live);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

    }
}
