package com.example.travellink.database;


import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Trip.TripModel.Trip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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

    public LiveData<List<Expense>> getAllExpensesByTripId_andType(int trip_id, String type) {
        return expenseDAO.getAllByType(trip_id, type);
    }

    public List<ExpenseDAO.Expense_amountByType> getExpenseAmountByType(int trip_id) {
        return expenseDAO.getExpenseAmountByType(trip_id);
    }

    public List<ExpenseDAO.Expense_amountByDate> getExpenseAmountByDate() {
        return expenseDAO.getExpenseAmountByDate();
    }

    public LiveData<Expense> getExpenseByID(int id) {
        return expenseDAO.loadExpenseByID(id);
    }

    public LiveData<List<Expense>> getCloudExpensesByUserId(String userId, int tripId) {
        MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
        FirebaseFirestore fire_store = FirebaseFirestore.getInstance();
        CollectionReference expensesRef = fire_store.collection("my_expenses").document(userId).collection("expenses");
        Query query = expensesRef.whereEqualTo("trip_ID", tripId);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e(TAG, "Error getting expenses by trip ID: ", error);
                expensesLiveData.setValue(null);
                return;
            }
            List<Expense> expensesList = new ArrayList<>();
            for (DocumentSnapshot document : value) {
                Expense expenses = document.toObject(Expense.class);
                expensesList.add(expenses);
            }
            expensesLiveData.setValue(expensesList);
        });

        return expensesLiveData;
    }
    public void insertExpense(Expense expense) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            expenseDAO.insertExpense(expense);
        });
    }
}
