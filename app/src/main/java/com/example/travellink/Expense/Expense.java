package com.example.travellink.Expense;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.travellink.Trip.Trip;
import com.google.api.Billing;

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
    private String Expense_Location;
    private String Expense_Price;
    private String Expense_Date;
    private String Expense_Time;

    private int Trip_ID;

    public Expense() {

    }


    public Expense(int expense_Id, String expense_Name, String expense_Type, String expense_Comment, String expense_Image, String expense_location, String expense_Price, String expense_Date, String expense_Time, int trip_Id) {
        this.Expense_Id = expense_Id;
        Expense_Name = expense_Name;
        Expense_Type = expense_Type;
        Expense_Comment = expense_Comment;
        Image_Bill = expense_Image;
        Expense_Location = expense_location;
        Expense_Price = expense_Price;
        Expense_Date = expense_Date;
        Expense_Time = expense_Time;
        Trip_ID = trip_Id;
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

    public void setTrip_ID(int trip_Id) {
        Trip_ID = trip_Id;
    }

    public String getExpense_Location() {
        return Expense_Location;
    }

    public void setExpense_Location(String expense_Location) {
        Expense_Location = expense_Location;
    }

    public String getImage_Bill() {
        return Image_Bill;
    }

    public void setImage_Bill(String expense_Image) {
        Image_Bill = expense_Image;
    }
}
