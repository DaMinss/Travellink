package com.example.travellink.Expense.ExpenseModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travellink.database.ExpenseRepo;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    public static ExpenseRepo repo;
    public final LiveData<List<Expense>> Expenses;
    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repo = new ExpenseRepo(application);
        Expenses = repo.getAllData();
    }
    public LiveData<Expense> getExpense(int id) { return repo.getExpenseByID(id);}
    public LiveData<List<Expense>> getAllExpense() {return repo.getAllData();}
}
