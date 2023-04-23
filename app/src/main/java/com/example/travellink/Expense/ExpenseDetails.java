package com.example.travellink.Expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.Trip.TripDetails;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;


import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpenseDetails extends AppCompatActivity{
    int Ex_id, trip_id;
    Expense expense;
    public TextView Expense_Name, Expense_Price, Expense_StartDate, Expense_endDate, expense_Depart, expense_Arrival, DateView, description, Type;
    public ImageView edit_expense, hotel1, shopping1, taxi1, plane1, other1, food1, image_bill;
    CardView cardView;
    ConstraintLayout detail;
    ImageView back;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Ex_id = bundle.getInt("expense_id");
            trip_id = bundle.getInt("trip_id");
        }
        back = findViewById(R.id.backBTN);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpenseDetails.this, TripDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("trip_id", trip_id);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                finish();
            }
        });
        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.getExpense(Ex_id).observe(this, expenses -> {
            expense = expenses;
            setDetails(expense);
        });
        DateView = findViewById(R.id.start_Date);
        cardView = findViewById(R.id.card1);
        detail = findViewById(R.id.detail);
        hotel1 = findViewById(R.id.bedroom1);
        food1 = findViewById(R.id.food1);
        plane1 = findViewById(R.id.plane1);
        shopping1 = findViewById(R.id.shopping1);
        taxi1 = findViewById(R.id.taxi1);
        other1 = findViewById(R.id.other1);
        Expense_Name = findViewById(R.id.expense_Name);
        Expense_Price = findViewById(R.id.amount);
        Expense_endDate = findViewById(R.id.endDateandTime);
        Expense_StartDate = findViewById(R.id.startDateandTime);
        expense_Depart = findViewById(R.id.expense_des);
        expense_Arrival = findViewById(R.id.expense_des1);
        description = findViewById(R.id.description);
        Type = findViewById(R.id.category);
        image_bill = findViewById(R.id.image_billing);
        edit_expense = findViewById(R.id.updateExpense);
        edit_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
                Bundle bundle = new Bundle();
                bundle.putInt("ex_id", Ex_id);
                 bundle.putInt("t_id", trip_id);
                UpdateExpenseFragment updateExpenseFragment = new UpdateExpenseFragment();
                updateExpenseFragment.setArguments(bundle);
                updateExpenseFragment.show(getSupportFragmentManager(),null);

            }
        });



        detail.setTranslationX(800);
        detail.setAlpha(v);

        detail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        cardView.setTranslationY(300);
        cardView.setAlpha(v);
        cardView.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        Expense_Name.setTranslationY(300);
        Expense_Name.setAlpha(v);
        Expense_Name.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

    }
    protected void setDetails(Expense expense) {
        Expense_Name.setText(expense.getExpense_Name());
        description.setText(expense.getExpense_Comment());
        Expense_Price.setText(expense.getExpense_Price());
        expense_Depart.setText(expense.getExpense_Location_Departure());
        expense_Arrival.setText(expense.getExpense_Location_Arrival());
        Expense_StartDate.setText(expense.getExpense_StartDate());
        DateView.setText(expense.getExpense_StartDate());
        Expense_endDate.setText(expense.getExpense_EndDate());
        Type.setText(expense.getExpense_Type());
        if(expense.getImage_Bill().isEmpty()){
            Picasso.get().load(R.drawable.transparent_bg).into(image_bill);
        }else {
            Picasso.get().load(expense.getImage_Bill()).into(image_bill);
        }
        String datetimeString = expense.getExpense_StartDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date currentDate = Calendar.getInstance().getTime();

        try {
            date1 = dateFormat.parse(datetimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

// Format the date as a string
        String dateString = dateFormat.format(date1);
        String current_dateString = dateFormat.format(currentDate);

        if(dateString.equals(current_dateString)){
           DateView.setText("Today");
        }
        if(expense.getExpense_Type().equals("Food")) {
           food1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Taxi")) {
            taxi1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Flight")) {
            plane1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Shopping")) {
            shopping1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Hotel")) {
            hotel1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Others")) {
            other1.setVisibility(View.VISIBLE);
        }
    }

}