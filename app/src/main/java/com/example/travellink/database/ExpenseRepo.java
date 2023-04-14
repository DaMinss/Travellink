package com.example.travellink.database;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.travellink.Expense.ExpenseModel.Expense;

import java.util.List;

public class ExpenseRepo {
    private ExpenseDAO expenseDAO;
    private LiveData<List<Expense>> allExpense;

    public ExpenseRepo(Application application) {
        TravelDatabase db = TravelDatabase.getInstance(application);
        expenseDAO = db.expenseDAO();
    }
    public LiveData<List<Expense>> getAllExpensesByTripId(int trip_id) {
        return expenseDAO.getAll(trip_id);
    }
//    public void insert(Trip trip) {
//        TravelDatabase.databaseWriteExecutor.execute(() -> {
//            tripDao.insertTrip(trip);
//        });
//    }
//    public void deleteTrip( int id) {
//        TravelDatabase.databaseWriteExecutor.execute(() -> {
//            tripDao.Delete(id);
//        });
//    }
    public LiveData<Expense> getExpenseByID(int id) {
        return expenseDAO.loadExpenseByID(id);
    }

}
