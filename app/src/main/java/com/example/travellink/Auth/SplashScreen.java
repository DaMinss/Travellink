package com.example.travellink.Auth;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travellink.Admin.AdminActivity;
import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.MainActivity;
import com.example.travellink.OnboardingScreen;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SplashScreen extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Trip> roomTrips;
    List<Expense> roomExpenses;
    private FirebaseAuth mAuth;
    private TravelDatabase travelDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        travelDb = TravelDatabase.getInstance(this);
        roomTrips = TravelDatabase.getInstance(SplashScreen.this).tripDAO().getAll();
        roomExpenses = TravelDatabase.getInstance(SplashScreen.this).expenseDAO().getAllExpense();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null ) {
                    backup();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("user").document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String isAdmin = documentSnapshot.getString("admin");
                                            Log.d(TAG, "DocumentSnapshot exists.");
                                            if (documentSnapshot.contains("admin")) {
                                                isAdmin = documentSnapshot.getString("admin");
                                                Log.d(TAG, "isAdmin value: " + isAdmin);
                                            }

                                            if (isAdmin != null && isAdmin.equals("Yes")) {
                                                startActivity(new Intent(SplashScreen.this, AdminActivity.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                                finish();

                                            }
                                        }
                                    }
                                });


                } else if (onPersonalMode()) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    finish();
                } else if (onBoardingFinished()) {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    finish();
                } else {
                    startActivity(new Intent(SplashScreen.this, OnboardingScreen.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    finish();
                }

            }
        }, 4500);
    }

    private boolean onBoardingFinished() {
        SharedPreferences preferences = getSharedPreferences("OnBoardingScreen", Context.MODE_PRIVATE);
        return preferences.getBoolean("Finished", false);
    }

    private boolean onPersonalMode() {
        SharedPreferences preferences = getSharedPreferences("PersonalScreen", Context.MODE_PRIVATE);
        return preferences.getBoolean("Finished", false);
    }

    private void backup() {
        if(mAuth.getCurrentUser() != null) {
            db.collection("my_trips").document(mAuth.getCurrentUser().getUid()).collection("trips")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> trips = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot trip : trips) {
                                // trip data is different
                                Trip getTrip = new Trip();
                                getTrip.setId(Integer.parseInt(trip.get("id").toString()));
                                getTrip.setTrip_name(trip.get("trip_name").toString());
                                getTrip.setTrip_start_date(trip.get("trip_start_date").toString());
                                if (trip.get("trip_end_date").toString() != null) {
                                    getTrip.setTrip_end_date(trip.get("trip_end_date").toString());
                                }
                                getTrip.setTrip_arrival(trip.get("trip_arrival").toString());
                                getTrip.setTrip_departure(trip.get("trip_departure").toString());
                                getTrip.setTrip_status(trip.get("trip_status").toString());
                                getTrip.setNote(trip.get("note").toString());
                                getTrip.setUid(trip.get("uid").toString());
                                travelDb.tripDAO().updateTrip(getTrip);
                            }

                        }
                    });
        }


    }


}