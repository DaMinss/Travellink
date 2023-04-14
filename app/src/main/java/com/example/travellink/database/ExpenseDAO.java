package com.example.travellink.database;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Trip.TripModel.Trip;

import java.util.List;

@Dao
public interface ExpenseDAO {
    @Query("SELECT * FROM expense WHERE Trip_ID =:tripIds ")
    LiveData<List<Expense>>  getAll(int tripIds);

    @Query("SELECT * FROM expense WHERE Expense_Id = :expenseIds")
    LiveData<Expense> loadExpenseByID(int expenseIds);

    @Query("SELECT Expense_Type as Type, SUM(Expense_Price)  as TotalOfExpenses  FROM expense WHERE Trip_ID =:tripIds")
    List<ExpenseDAO.Expense_amountByType> getExpenseAmountByType(int tripIds);

    class Expense_amountByType {
        @ColumnInfo(name = "Type")
        public String Type;
        @ColumnInfo(name = "TotalOfExpenses")
        public Float TotalOfExpense;

        public String getType() {
            return Type ;
        }

        public Float getTotalOfExpense() {
            return TotalOfExpense;
        }
    }

    @Insert
    long insertExpense(Expense expense);

    @Update
    int updateTrip(Trip trip);

    @Query("Delete FROM expense WHERE Expense_Id = :expenseIds")
    int DeleteExpense(int expenseIds);

}
