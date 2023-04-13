package com.example.travellink.Trip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.travellink.CreateNewExpense;
import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TripDetails extends AppCompatActivity {
   public FloatingActionButton addExpense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TripDetails.this, CreateNewExpense.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });

    }
}