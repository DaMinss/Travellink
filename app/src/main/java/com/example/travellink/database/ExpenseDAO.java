package com.example.travellink.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Trip.TripModel.Trip;

import java.util.List;

@Dao
public interface ExpenseDAO {
    @Query("SELECT * FROM expense")
    LiveData<List<Expense>>  getAll();

    @Query("SELECT * FROM expense WHERE Expense_Id = :expenseIds")
    LiveData<Expense> loadExpenseByID(int expenseIds);

    @Insert
    long insertExpense(Expense expense);

    @Update
    int updateTrip(Trip trip);

    @Query("Delete FROM expense WHERE Expense_Id = :expenseIds")
    int DeleteExpense(int expenseIds);

}
