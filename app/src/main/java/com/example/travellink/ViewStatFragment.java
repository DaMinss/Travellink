package com.example.travellink;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.database.ExpenseDAO;
import com.example.travellink.database.ExpenseRepo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
    TextView currentMonth;

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
        currentMonth = root.findViewById(R.id.currentMonth);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        String current = dateFormat.format(calendar.getTime());
        currentMonth.setText(current);
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
        dataSet.setDrawFilled(true);
        dataSet.setValueTextColor(R.color.purple_600);
        dataSet.setValueTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        dataSet.setCircleColor(Color.rgb(225, 195, 255));
        dataSet.setValueTextSize(11f);
        dataSet.setColor(Color.rgb(225, 195, 255));
        dataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.chart_gradient));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Week " + ((int) value);
            }
        });
        xAxis.setTextSize(10f);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(12f);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        yAxis.setValueFormatter(new LargeValueFormatter());
        yAxis.setGranularity(1f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setExtraBottomOffset(-50);
        lineChart.setMinimumHeight(650);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.invalidate();
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float x = e.getX();
                float y = e.getY();
                Toast.makeText(getContext(), "Week " + ((int) x) + ": " + y + " $", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                // do nothing
            }
        });

        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);


    }
}