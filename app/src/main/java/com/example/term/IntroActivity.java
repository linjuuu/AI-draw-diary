package com.example.term;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class IntroActivity extends AppCompatActivity {
    ImageView introLogo;
    Animation introAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);





        //-------------------------------------------------------------------
        //인트로 띄우기
        introLogo = findViewById(R.id.introLogo);
        introAnim = AnimationUtils.loadAnimation(this, R.anim.intro_anim);
        introLogo.startAnimation(introAnim);

        introAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션이 끝난 후에 수행할 작업
                Intent intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);

                // 현재 액티비티를 종료
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });



    }
}