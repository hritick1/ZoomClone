package com.example.zoomclone.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zoomclone.LoginActivity;
import com.example.zoomclone.R;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView signUpView,signInView;
    private AppCompatButton btnJoinMeeting;
private ViewPager viewPager;
private SpringDotsIndicator springDotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        clickListener();
        setPagerAdapter();
    }

    private void setPagerAdapter() {
        viewPager.setAdapter(new WelcomePagerAdapter(this,getSupportFragmentManager()));
        springDotsIndicator.setViewPager(viewPager);
    }


    private void initViews() {
        signUpView=findViewById(R.id.signUpView);
        signInView=findViewById(R.id.signInView);
        btnJoinMeeting=findViewById(R.id.btnJoinMeeting);
        viewPager=findViewById(R.id.viewPager);
        springDotsIndicator=findViewById(R.id.dots_indicator);
    }



    private void clickListener() {
        signInView.setOnClickListener(this);
        signUpView.setOnClickListener(this);
        btnJoinMeeting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signInView:
                navigateToSignIn(true);
                    break;
            case R.id.signUpView:
                navigateToSignIn(false);

                    break;
            case R.id.btnJoinMeeting:

                    break;
        }
    }

    private void navigateToSignIn(boolean isSignIn) {
        Intent intent=new Intent(this, LoginActivity.class);
        intent.putExtra("isSignIn",isSignIn);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}