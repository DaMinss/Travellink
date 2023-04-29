package com.example.travellink.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.travellink.Auth.SplashScreen2;
import com.example.travellink.R;
import com.example.travellink.database.CloudRepo;
import com.google.firebase.auth.FirebaseAuth;

public class AdminWelcome extends AppCompatActivity {
    FirebaseAuth myAuth;
    Button Continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_welcome);
        Continue = findViewById(R.id.Continue);
        myAuth = FirebaseAuth.getInstance();
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloudRepo cloudRepo = new CloudRepo(AdminWelcome.this);
                cloudRepo.ImportData(myAuth.getCurrentUser().getUid());
                startActivity(new Intent(AdminWelcome.this, SplashScreen2.class));
                finish();
            }
        });
    }

}
