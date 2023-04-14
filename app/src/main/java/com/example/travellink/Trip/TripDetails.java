package com.example.travellink.Trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.travellink.Expense.CreateNewExpense;
import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Expense.ExpenseModel.ExpenseAdapter;
import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TripDetails extends AppCompatActivity {
    int myValue;
    Trip trip;
    RecyclerView viewExpenseList;
    ExpenseAdapter expenseAdapter;
    ExpenseRepo expenseRepo;
    TextView departName, arrivalName;
    private LiveData<TripDAO.Trip_withTotalPrice> listOfTrips;
    private List<Expense> listOfExpense;
   public FloatingActionButton addExpense;
    PieChart pieChart;
List<ExpenseDAO.Expense_amountByType> amountByTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        expenseRepo = new ExpenseRepo(getApplication());
        amountByTypes = expenseRepo.getExpenseAmountByType(myValue);
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
        trip = TravelDatabase.getInstance(this).tripDAO().TripByID(myValue);
        departName = findViewById(R.id.departName);
        arrivalName = findViewById(R.id.arriveName);
        departName.setText(trip.getTrip_departure());
        arrivalName.setText(trip.getTrip_arrival());

        //Chart
        pieChart = findViewById(R.id.type_chart);
        setupPieChart();
        loadPieChartData();


    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleColor(Color.rgb(29, 35, 42));
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterText("Spending by Expense Type");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(5);
        pieChart.getDescription().setEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setTextColor(Color.WHITE);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);;
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }
    private void loadPieChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        float total;
        String type;

        for ( ExpenseDAO.Expense_amountByType expense_amountByType : amountByTypes){
            total = expense_amountByType.getTotalOfExpense();
            type = expense_amountByType.getType();
            entries.add(new PieEntry(total,type));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        int [] color={ Color.rgb(140, 118, 82), Color.rgb(49, 75, 107), Color.rgb(121, 90, 164 ),
                Color.rgb(200, 54, 104 ), Color.rgb(204, 164, 6 ), Color.rgb(47,95,255)
        };
        pieDataSet.setColors(color);
        PieData data = new PieData(pieDataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);

    }
}
