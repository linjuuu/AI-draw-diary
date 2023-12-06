package com.example.term;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.term.Fragment.FragDiary;
import com.example.term.Fragment.FragHome;
import com.example.term.Fragment.FragCalendar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // 바텀 네비게이션
    BottomNavigationView bottomNavigationView;

    private String TAG = "메인";

    // 프래그먼트 변수
    Fragment fragment_diary, fragment_home, fragment_calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 생성
        fragment_diary = new FragDiary();
        fragment_home = new FragHome();
        fragment_calendar = new FragCalendar();


        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_diary).commitAllowingStateLoss();


        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i(TAG, "바텀 네비게이션 클릭");

                if(item.getItemId() == R.id.diary)
                {
                        Log.i(TAG, "diary");
                        getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout,fragment_diary).commitAllowingStateLoss();
                        return true;
                }
                else if(item.getItemId() == R.id.home)
                {
                    Log.i(TAG, "home");
                    getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
                    return true;
                }

                else if(item.getItemId() == R.id.calendar)
                {
                    Log.i(TAG, "calendar");
                    getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout, fragment_calendar).commitAllowingStateLoss();
                    return true;
                }

                return true;
            }
        });


    }
}