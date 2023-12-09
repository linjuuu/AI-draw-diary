// IntroActivity.java

package com.example.term;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2000; // 2 seconds
    ImageView imagelogo1, imagelogo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        imagelogo1 = findViewById(R.id.imagelogo1);
        imagelogo2 = findViewById(R.id.imagelogo2);

        // 0.2초 간격으로 번갈아가며 이미지를 보여주는 Runnable
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (count % 2 == 0) {
                    imagelogo1.setAlpha(1f);
                    imagelogo2.setAlpha(0f);
                } else {
                    imagelogo1.setAlpha(0f);
                    imagelogo2.setAlpha(1f);
                }

                count++;

                if (count <= 10) { // 예시로 2초 간격으로 10번 반복
                    handler.postDelayed(this, 200);
                }
            }
        };

        // 0.2초 후 runnable 실행
        handler.postDelayed(runnable, 200);

        // 2초 후 MainActivity로 이동
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료
        }, SPLASH_TIME);
    }
}
