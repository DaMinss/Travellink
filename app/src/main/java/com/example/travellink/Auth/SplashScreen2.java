package com.example.travellink.Auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travellink.Admin.AdminActivity;
import com.example.travellink.Admin.AdminWelcome;
import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.database.CloudRepo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
//                    if (account != null) {
//                        startActivity(new Intent(SplashScreen2.this, MainActivity.class));
//                        finish();
//                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth myAuth = FirebaseAuth.getInstance();
                        db.collection("user").document(myAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String isAdmin = documentSnapshot.getString("admin");
                                            Log.d(TAG, "DocumentSnapshot exists.");
                                            if (documentSnapshot.contains("isAdmin")) {
                                                isAdmin = documentSnapshot.getString("admin");
                                                Log.d(TAG, "isAdmin value: " + isAdmin);
                                            }

                                            if (isAdmin != null && isAdmin.equals("Yes")) {
                                                startActivity(new Intent(SplashScreen2.this, AdminActivity.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(SplashScreen2.this, MainActivity.class));
                                                finish();

                                            }
                                        }
                                    }
                                });
//                    }
                } else {
                    startActivity(new Intent(SplashScreen2.this, Login.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    finish();
                }

            }
        }, 3000);
    }
}