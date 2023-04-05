package com.example.travellink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.travellink.Auth.Login;
import com.example.travellink.Auth.SplashScreen;

import me.relex.circleindicator.CircleIndicator;
import me.relex.circleindicator.CircleIndicator3;

public class OnboardingScreen extends AppCompatActivity {

    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator;
    private ViewPagerAdapter viewPagerAdapter;
    private Button Next,Finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        viewPager = findViewById(R.id.viewPager2);
        circleIndicator =findViewById(R.id.indicator);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        circleIndicator.setViewPager(viewPager);
        Finish = findViewById(R.id.Finish);
        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnboardingScreen.this, Login.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                onBoardingFinished();
            }
        });
        Next = findViewById(R.id.Next);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() < 2){
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2 ){
                    Next.setVisibility(View.GONE);
                    Finish.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
    private void onBoardingFinished() {
        SharedPreferences sharedPreferences = getSharedPreferences("OnBoardingScreen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Finished", true);
        editor.apply();
    }
}