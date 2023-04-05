package com.example.travellink.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.travellink.MainActivity;
import com.example.travellink.R;

public class Personal extends AppCompatActivity {
    Button Continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        Continue = findViewById(R.id.Continue);
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPersonal();
                startActivity(new Intent(Personal.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
    }
    private void onPersonal() {
        SharedPreferences sharedPreferences = getSharedPreferences("PersonalScreen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Finished", true);
        editor.apply();
    }
}