package com.example.travellink.Expense.ExpenseModel;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.travellink.Trip.TripModel.Trip;

@Entity(tableName = "expense", foreignKeys = {@ForeignKey(entity = Trip.class,
        parentColumns = "id",
        childColumns = "Trip_ID",
        onDelete = ForeignKey.CASCADE)
})
public class Expense {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int Expense_Id;
    private String Expense_Name;
    private String Expense_Type;
    private String Expense_Comment;
    private String Image_Bill;
    private String Expense_Location_Departure;
    private String Expense_Location_Arrival;
    private String Expense_Price;
    private String Expense_Date;
    private String Expense_Time;

    private int Trip_ID;

    public Expense(int expense_Id, String expense_Name, String expense_Type, String expense_Comment, String image_Bill, String expense_Location_Departure, String expense_Location_Arrival, String expense_Price, String expense_Date, String expense_Time, int trip_ID) {
        Expense_Id = expense_Id;
        Expense_Name = expense_Name;
        Expense_Type = expense_Type;
        Expense_Comment = expense_Comment;
        Image_Bill = image_Bill;
        Expense_Location_Departure = expense_Location_Departure;
        Expense_Location_Arrival = expense_Location_Arrival;
        Expense_Price = expense_Price;
        Expense_Date = expense_Date;
        Expense_Time = expense_Time;
        Trip_ID = trip_ID;
    }
    public Expense(){

    }

    public int getExpense_Id() {
        return Expense_Id;
    }

    public void setExpense_Id(int expense_Id) {
        Expense_Id = expense_Id;
    }

    public String getExpense_Name() {
        return Expense_Name;
    }

    public void setExpense_Name(String expense_Name) {
        Expense_Name = expense_Name;
    }

    public String getExpense_Type() {
        return Expense_Type;
    }

    public void setExpense_Type(String expense_Type) {
        Expense_Type = expense_Type;
    }

    public String getExpense_Comment() {
        return Expense_Comment;
    }

    public void setExpense_Comment(String expense_Comment) {
        Expense_Comment = expense_Comment;
    }

    public String getImage_Bill() {
        return Image_Bill;
    }

    public void setImage_Bill(String image_Bill) {
        Image_Bill = image_Bill;
    }

    public String getExpense_Location_Departure() {
        return Expense_Location_Departure;
    }

    public void setExpense_Location_Departure(String expense_Location_Departure) {
        Expense_Location_Departure = expense_Location_Departure;
    }

    public String getExpense_Location_Arrival() {
        return Expense_Location_Arrival;
    }

    public void setExpense_Location_Arrival(String expense_Location_Arrival) {
        Expense_Location_Arrival = expense_Location_Arrival;
    }

    public String getExpense_Price() {
        return Expense_Price;
    }

    public void setExpense_Price(String expense_Price) {
        Expense_Price = expense_Price;
    }

    public String getExpense_Date() {
        return Expense_Date;
    }

    public void setExpense_Date(String expense_Date) {
        Expense_Date = expense_Date;
    }

    public String getExpense_Time() {
        return Expense_Time;
    }

    public void setExpense_Time(String expense_Time) {
        Expense_Time = expense_Time;
    }

    public int getTrip_ID() {
        return Trip_ID;
    }

    public void setTrip_ID(int trip_ID) {
        Trip_ID = trip_ID;
    }
}


