package com.example.travellink;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class WeekValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        switch ((int) value) {
            case 0:
                return "";
            case 1:
                return "Week 1";
            case 2:
                return "Week 2";
            case 3:
                return "Week 3";
            case 4:
                return "Week 4";
            default:
                return "Week 1";
        }
    }
}
