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
    @Query("SELECT * FROM expense WHERE Trip_ID =:tripIds  ORDER BY Expense_StartDate DESC")
    LiveData<List<Expense>>  getAll(int tripIds);
    @Query("SELECT * FROM expense WHERE Trip_ID =:tripIds AND Expense_Type = :Type  ORDER BY Expense_StartDate DESC")
    LiveData<List<Expense>>  getAllByType(int tripIds, String Type);

    @Query("SELECT * FROM expense WHERE Expense_Id = :expenseIds")
    LiveData<Expense> loadExpenseByID(int expenseIds);

    @Query("SELECT * FROM expense WHERE Expense_Id = :expenseIds")
    Expense getExpenseByID(int expenseIds);

    @Query("SELECT Expense_Type as Type, SUM(Expense_Price)  as TotalOfExpenses  FROM expense WHERE Trip_ID =:tripIds GROUP BY Type")
    List<ExpenseDAO.Expense_amountByType> getExpenseAmountByType(int tripIds);
    @Query("SELECT ((strftime('%d', datetime(Expense_StartDate)) - 1) / 7) + 1 as week, SUM(Expense_Price) as TotalOfExpenses \n" +
            "FROM expense \n" +
            "WHERE strftime('%Y-%m', Expense_StartDate) = :yearAndMonth \n" +
            "GROUP BY week;")
    List<ExpenseDAO.Expense_amountByDate> getExpenseAmountByDate(String yearAndMonth);

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
    class Expense_amountByDate {
        @ColumnInfo(name = "week")
        public int week;
        @ColumnInfo(name = "TotalOfExpenses")
        public Float TotalOfExpense;

        public int getWeek() {
            return week ;
        }

        public Float getTotalOfExpense() {
            return TotalOfExpense;
        }
    }

    @Insert
    long insertExpense(Expense expense);

    @Update
    int updateExpense(Expense expense);

    @Query("Delete FROM expense WHERE Expense_Id = :expenseIds")
    int DeleteExpense(int expenseIds);

}
