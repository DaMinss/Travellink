package com.example.travellink.Trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.travellink.Expense.CreateNewExpense;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.Trip.TripModel.TripAdapter;
import com.example.travellink.database.TravelDatabase;
import com.example.travellink.database.TripDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class TripDetails extends AppCompatActivity {
    int myValue;
    RecyclerView viewExpenseList;
    TripAdapter tripAdapter;
    TextView departName, arrivalName;
    private LiveData<TripDAO.Trip_withTotalPrice> listOfTrips;
   public FloatingActionButton addExpense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myValue = bundle.getInt("trip_id");
        }

        addExpense = findViewById(R.id.addExpense);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripDetails.this, CreateNewExpense.class);
                Bundle bundle = new Bundle();
                bundle.putInt("trip_ids", myValue);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        Trip trip = TravelDatabase.getInstance(this).tripDAO().TripByID(myValue);
        departName = findViewById(R.id.departName);
        departName.setText(trip.getTrip_departure());

    }
}