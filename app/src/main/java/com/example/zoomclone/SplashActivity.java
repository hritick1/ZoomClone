package com.example.zoomclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.zoomclone.welcome.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        runNextScreen();
    }

    private void runNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();//destroying the splash screen
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        },1500);
    }
}