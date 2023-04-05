package com.example.travellink.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travellink.MainActivity;
import com.example.travellink.OnboardingScreen;
import com.example.travellink.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
//    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                } else if(onPersonalMode()) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    finish();
                } else if(onBoardingFinished()){
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                }else {
                    startActivity(new Intent(SplashScreen.this, OnboardingScreen.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                }

            }
        },4000);
    }
    private boolean onBoardingFinished() {
        SharedPreferences preferences = getSharedPreferences("OnBoardingScreen", Context.MODE_PRIVATE);
        return preferences.getBoolean("Finished", false);
    }
    private boolean onPersonalMode() {
        SharedPreferences preferences = getSharedPreferences("PersonalScreen", Context.MODE_PRIVATE);
        return preferences.getBoolean("Finished", false);
    }
}