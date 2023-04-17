package com.example.travellink;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travellink.database.ExpenseDAO;
import com.example.travellink.database.ExpenseRepo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ViewStatFragment extends Fragment {
    LineChart lineChart;
    ExpenseRepo expenseRepo;
    List<ExpenseDAO.Expense_amountByDate> amountByDate;

    public ViewStatFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_stat, container, false);
        lineChart = root.findViewById(R.id.line_chart);
        expenseRepo = new ExpenseRepo(getActivity().getApplication());
        amountByDate = expenseRepo.getExpenseAmountByDate();
        loadLineChartData();
        return root;
    }

    private void loadLineChartData(){
        ArrayList<Entry> entries = new ArrayList<>();

        for (ExpenseDAO.Expense_amountByDate amount: amountByDate) {
            float total = amount.getTotalOfExpense();
            int Week = amount.getWeek();
            Log.v(getTag(),total + " - " + Week);
            entries.add(new Entry(Week, total));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Expenses");
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new LargeValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setValueFormatter(new LargeValueFormatter());
        yAxis.setGranularity(1f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setExtraBottomOffset(-50);
        lineChart.setMinimumHeight(250);
        lineChart.invalidate();




    }
}