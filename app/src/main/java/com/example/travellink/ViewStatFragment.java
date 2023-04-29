package com.example.travellink;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.database.ExpenseDAO;
import com.example.travellink.database.ExpenseRepo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ViewStatFragment extends Fragment {
    LineChart lineChart;
    BarChart  barChart;
    ExpenseRepo expenseRepo;
    List<ExpenseDAO.Expense_amountByDate> amountByDate;
    TextView currentMonth;
    String selectedMonth;
    String formattedDate;
    PieChart pieChart;
    List<ExpenseDAO.Expense_amountByType> amountByTypes;
    FirebaseAuth myAuth;
    FirebaseFirestore db;
    LottieAnimationView nodata;
    LinearLayout noTrip, content;

    public ViewStatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_stat, container, false);
        myAuth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        barChart = root.findViewById(R.id.bar_chart);
        lineChart = root.findViewById(R.id.line_chart);
        expenseRepo = new ExpenseRepo(getActivity().getApplication());
        nodata = root.findViewById(R.id.lottieAnimationViewNodata);
        noTrip = root.findViewById(R.id.noExpense);
        content = root.findViewById(R.id.stats_layout);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(), R.layout.spinner,
                getResources().getStringArray(R.array.month)) {
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = getResources().getStringArray(R.array.month);
                        filterResults.count = getResources().getStringArray(R.array.month).length;
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
        };
        currentMonth = root.findViewById(R.id.currentMonth);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String current = dateFormat.format(calendar.getTime());
        currentMonth.setText(current);
        AutoCompleteTextView selection = (AutoCompleteTextView) root.findViewById(R.id.selection);
        selection.setAdapter(arrayAdapter);
        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                selection.showDropDown();
            }
        });
        selection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = parent.getItemAtPosition(position).toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                String formattedDate = sdf.format(calendar.getTime());
                String finalDate = formattedDate.substring(0, 5) + monthToNumber(selectedMonth);
                amountByDate = expenseRepo.getExpenseAmountByDate(finalDate);
                amountByTypes = expenseRepo.getExpenseAmountByTypeByDate(finalDate);
                Toast.makeText(getContext(), finalDate, Toast.LENGTH_SHORT).show();
                loadBarChartData();
                loadLineChartData();
                setupPieChart();
                loadPieChartData();
            }
        });
        formattedDate = new SimpleDateFormat("yyyy-MM").format(calendar.getTime());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM");
        try {
            Date date = dateFormat1.parse(formattedDate);
            String month = new SimpleDateFormat("MMMM").format(date);
            selection.setText(month);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        amountByDate = expenseRepo.getExpenseAmountByDate(formattedDate);
        amountByTypes = expenseRepo.getExpenseAmountByTypeByDate(formattedDate);
        loadLineChartData();
        pieChart = root.findViewById(R.id.pie_chart);
        setupPieChart();
        loadPieChartData();
        loadBarChartData();
        Cloud();
        readData(expenseRepo.getExpenseAmountByTypeByDate(formattedDate).size());
        return root;
    }
    private void Cloud(){
        if(myAuth.getCurrentUser() != null) {
            db.collection("user").document(myAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                            barChart.setVisibility(View.VISIBLE);
                            lineChart.setVisibility(View.GONE);

                        } else {
                            barChart.setVisibility(View.GONE);
                            lineChart.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }


    private void loadLineChartData() {
        ArrayList<Entry> entries = new ArrayList<>();

        for (ExpenseDAO.Expense_amountByDate amount : amountByDate) {
            float total = amount.getTotalOfExpense();
            int Week = amount.getWeek();
            Log.v(getTag(), total + " - " + Week);
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
        Entry e = new Entry();
        float x = e.getX();
        xAxis.setValueFormatter(new WeekValueFormatter());
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
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // set x-axis position
        lineChart.animateY(2500, Easing.EaseInOutQuad);
        lineChart.animateX(1500, Easing.EaseInOutQuad);
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
    private void loadBarChartData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (ExpenseDAO.Expense_amountByDate amount : amountByDate) {
            float total = amount.getTotalOfExpense();
            int Week = amount.getWeek();
            Log.v(getTag(), total + " - " + Week);
            entries.add(new BarEntry(Week, total));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Total Expenses");
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f);
        barChart.setData(barData);
        dataSet.setValueTextColor(R.color.purple_600);
        dataSet.setValueTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
        dataSet.setValueTextSize(11f);
        dataSet.setColor(Color.rgb(225, 195, 255));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        Entry e = new Entry();
        float x = e.getX();
        xAxis.setValueFormatter(new WeekValueFormatter());
        xAxis.setTextSize(10f);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextSize(12f);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setValueFormatter(new LargeValueFormatter());
        yAxis.setGranularity(1f);
        barChart.animateY(2500, Easing.EaseInOutQuad);
        barChart.animateX(1500, Easing.EaseInOutQuad);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setFitBars(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setExtraBottomOffset(-50);
        barChart.setMinimumHeight(650);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.invalidate();
        barChart.setHighlightPerTapEnabled(true);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);


    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.rgb(160, 160, 160 ));
        pieChart.setCenterText("Spending by Type");
        pieChart.setCenterTextColor(Color.rgb(57,165,213));
        pieChart.setCenterTextSize(12);
//        pieChart.getLayoutParams().width = 800; // in pixels
//        pieChart.getLayoutParams().height = 800; // in pixels
        pieChart.setHoleRadius(70f); // in dp
        pieChart.setTransparentCircleRadius(80f);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setTextColor(Color.rgb(160, 160, 160 ));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);;
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
        int[] color = {Color.rgb(202, 143, 241), Color.rgb(159, 142, 237), Color.rgb(142, 158, 237 ),
                Color.rgb(242, 186, 245 ), Color.rgb(245, 186, 205 ), Color.rgb(47, 95, 255)
        };
        pieDataSet.setColors(color);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1OffsetPercentage(100f);
        pieDataSet.setValueLinePart1Length(0.3f);
        pieDataSet.setValueLinePart2Length(0.3f);
        pieDataSet.setSliceSpace(0f);
        PieData data = new PieData(pieDataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.rgb(160, 160, 160 ));

        pieChart.setData(data);
        pieChart.invalidate();


        pieChart.animateY(1400, Easing.EaseInOutQuad);

    }

    public static String monthToNumber(String month) {
        switch (month.toLowerCase()) {
            case "january":
                return "01";
            case "february":
                return "02";
            case "march":
                return "03";
            case "april":
                return "04";
            case "may":
                return "05";
            case "june":
                return "06";
            case "july":
                return "07";
            case "august":
                return "08";
            case "september":
                return "09";
            case "october":
                return "10";
            case "november":
                return "11";
            case "december":
                return "12";
            default:
                return "";
        }
    }
    void readData(int count) {
        if (count == 0) {
            noTrip.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);

        } else {
            noTrip.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }
}