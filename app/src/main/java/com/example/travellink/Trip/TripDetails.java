package com.example.travellink.Trip;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.Expense.CreateNewExpense;
import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Expense.ExpenseModel.ExpenseAdapter;
import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.Trip.TripCloudModel.TripCloudAdapter;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.Trip.TripModel.TripAdapter;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.ExpenseDAO;
import com.example.travellink.database.ExpenseRepo;
import com.example.travellink.database.TravelDatabase;
import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TripDetails extends AppCompatActivity {
    int myValue;
    int type;
    Trip trip;
    ImageView back;
    AppCompatButton submit, unSubmit;
    RecyclerView viewExpenseList;
    ExpenseAdapter expenseAdapter;
    ExpenseRepo expenseRepo;
    TextView departName, arrivalName, start_date, end_date, total, note, to;
    private LiveData<TripDAO.Trip_withTotalPrice> listOfTrips;
    private List<Expense> listOfExpense;
    public ImageButton addExpense, toggleChart;
    PieChart pieChart;
    LinearLayout layoutChart;
    ConstraintLayout detailBar;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";
    List<ExpenseDAO.Expense_amountByType> amountByTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        myAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myValue = bundle.getInt("trip_id");
        }
        back = findViewById(R.id.backBTN);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TripDetails.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        expenseRepo = new ExpenseRepo(getApplication());
        amountByTypes = expenseRepo.getExpenseAmountByType(myValue);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutChart = findViewById(R.id.layout_chart);
        toggleChart = findViewById(R.id.chart);
        detailBar = findViewById(R.id.detailBar);
        toggleChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int visible = (layoutChart.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                TransitionManager.beginDelayedTransition(detailBar, new AutoTransition());
                layoutChart.setVisibility(visible);
            }
        });
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
        trip = TravelDatabase.getInstance(this).tripDAO().TripByID(myValue);
        departName = findViewById(R.id.departName);
        arrivalName = findViewById(R.id.arriveName);
        start_date = findViewById(R.id.startDate);
        end_date = findViewById(R.id.endDate);
        note = findViewById(R.id.tripNote);
        total = findViewById(R.id.total);
        to = findViewById(R.id.text);
        viewExpenseList = findViewById(R.id.list_ofExpense);
        int position = 5; // set the position you want to scroll to
        viewExpenseList.scrollToPosition(position);
        submit = findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    int status = TravelDatabase.getInstance(TripDetails.this).tripDAO().updateTrip(get_data(myValue));
                    if (status > 0) {
                        Toast.makeText(TripDetails.this, "The selected trip has been submitted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TripDetails.this, MainActivity.class));
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    } else {
                        Toast.makeText(TripDetails.this, "Failed to submit", Toast.LENGTH_SHORT).show();
                    }
                }
        });
        unSubmit = findViewById(R.id.buttonUnSubmit);
        unSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = TravelDatabase.getInstance(TripDetails.this).tripDAO().updateTrip(get_data1(myValue));
                if (status > 0) {
                    Toast.makeText(TripDetails.this, "The selected trip has been not-submitted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TripDetails.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                } else {
                    Toast.makeText(TripDetails.this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Chart
        pieChart = findViewById(R.id.type_chart);
        setupPieChart();
        loadPieChartData();
        Cloud();
    }

    private Trip get_data(int id) {
        String trip_name = trip.getTrip_name();
        String trip_depart = trip.getTrip_departure();
        String trip_arrive = trip.getTrip_arrival();
        String Start_date = trip.getTrip_start_date();
        String Note = trip.getNote();
        String status = "Submitted";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        String End_date = currentDate;
        return new Trip(id, trip_name, trip_depart, trip_arrive, status, Start_date, End_date, Note);

    }

    private Trip get_data1(int id) {
        String trip_name = trip.getTrip_name();
        String trip_depart = trip.getTrip_departure();
        String trip_arrive = trip.getTrip_arrival();
        String Start_date = trip.getTrip_start_date();
        String Note = trip.getNote();
        String status = "Not-submitted";
        String End_date = trip.getTrip_end_date();
        return new Trip(id, trip_name, trip_depart, trip_arrive, status, Start_date, End_date, Note);

    }

    public void Cloud() {
            departName.setText(trip.getTrip_departure());
            arrivalName.setText(trip.getTrip_arrival());
            start_date.setText(trip.getTrip_start_date());
            end_date.setText(trip.getTrip_end_date());
            note.setText(trip.getNote());
            TripViewModel viewModel1 = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(TripViewModel.class);
            viewModel1.getTripPrice(myValue).observe(this, trips -> {
                String price = String.valueOf(trips.getTotalOfExpense());
                total.setText(price);
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            viewExpenseList.setLayoutManager(linearLayoutManager);
            ExpenseViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(ExpenseViewModel.class);
            viewModel.getAllExpensesByTripId(myValue).observe(this, expenses -> {
                listOfExpense = expenses;
                viewExpenseList.setLayoutManager(new LinearLayoutManager(this));
                expenseAdapter = new ExpenseAdapter(expenses, this);
                viewExpenseList.setAdapter(expenseAdapter);
                readData(listOfExpense.size());
            });
        }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleColor(Color.rgb(29, 35, 42));
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterText("Spending by Type");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(14);
        pieChart.getLayoutParams().width = 800; // in pixels
        pieChart.getLayoutParams().height = 800; // in pixels
        pieChart.setHoleRadius(70f); // in dp
        pieChart.setTransparentCircleRadius(80f);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setTextColor(Color.WHITE);
        l.setTextSize(15);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        ;
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (ExpenseDAO.Expense_amountByType amount : amountByTypes) {
            float total = amount.getTotalOfExpense();
            String type = amount.getType();
            entries.add(new PieEntry(total, type));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        int[] color = {Color.rgb(140, 118, 82), Color.rgb(49, 75, 107), Color.rgb(121, 90, 164),
                Color.rgb(200, 54, 104), Color.rgb(204, 164, 6), Color.rgb(47, 95, 255)
        };
        pieDataSet.setColors(color);
        PieData data = new PieData(pieDataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(18f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();


        pieChart.animateY(1400, Easing.EaseInOutQuad);

    }

    void readData(int count) {
        if (count == 0) {
            submit.setVisibility(View.GONE);

        } else {
            if (trip.getTrip_status().equals("Submitted")) {
                submit.setVisibility(View.GONE);
                unSubmit.setVisibility(View.VISIBLE);
            } else {
                submit.setVisibility(View.VISIBLE);
            }
        }
    }
}
