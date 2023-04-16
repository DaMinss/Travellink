package com.example.travellink.Expense.ExpenseModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travellink.database.ExpenseRepo;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    public static ExpenseRepo repo;
    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repo = new ExpenseRepo(application);
    }
    public LiveData<Expense> getExpense(int id) { return repo.getExpenseByID(id);}
    public LiveData<List<Expense>> getAllExpensesByTripId(int trip_id) { return repo.getAllExpensesByTripId(trip_id);}
    public LiveData<List<Expense>> getAllExpensesByTripId_Type(int trip_id, String type) { return repo.getAllExpensesByTripId_andType(trip_id, type);}
}
