package com.example.travellink.database;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellink.Trip.TripModel.Trip;

import java.util.List;

@Dao
public interface TripDAO {
    @Query("SELECT * FROM trip")
    List<Trip> getAll();

    @Query("SELECT * FROM trip WHERE trip_id = :tripIds")
    LiveData<Trip> loadTripByID(int tripIds);


    @Query("SELECT * FROM trip WHERE trip_id = :tripIds")
    Trip TripByID(int tripIds);

    @Query("SELECT trip.*, SUM(expense.Expense_Price) as TotalOfExpenses  FROM trip LEFT OUTER JOIN expense ON trip.trip_id = expense.Trip_ID  GROUP BY trip.trip_id ORDER BY trip.Trip_start_date DESC")
    LiveData<List<Trip_withTotalPrice>> getAllTripWithTotalExpense();

    @Query("SELECT trip.*, SUM(expense.Expense_Price) as TotalOfExpenses  FROM trip LEFT OUTER JOIN expense ON trip.trip_id = expense.Trip_ID WHERE trip.trip_id = :tripIds ")
    LiveData<Trip_withTotalPrice> getTripWithTotalExpense(int tripIds);

    @Query("SELECT trip.*, SUM(expense.Expense_Price) as TotalOfExpenses  FROM trip LEFT OUTER JOIN expense ON trip.trip_id = expense.Trip_ID  GROUP BY trip.trip_id ORDER BY trip.Trip_start_date DESC LIMIT 5")
    List<Trip_withTotalPrice> getRecent5TripWithTotalExpense();

    @Query("SELECT trip.*, SUM(expense.Expense_Price) as TotalOfExpenses  FROM trip LEFT OUTER JOIN expense ON trip.trip_id = expense.Trip_ID  GROUP BY trip.trip_id ORDER BY TotalOfExpenses DESC LIMIT 3")
    List<Trip_withTotalPrice> getTop3TripWithTotalExpense();

    class Trip_withTotalPrice {
        @Embedded
        public Trip trip;
        @ColumnInfo(name = "TotalOfExpenses")
        public Float TotalOfExpense;

        public Trip getTrip() {
            return trip;
        }

        public Float getTotalOfExpense() {
            return TotalOfExpense;
        }
    }

    @Insert
    long insertTrip(Trip trip);

    @Update
    int updateTrip(Trip trip);

    @Query("Delete FROM trip WHERE  trip_id = :tripIds")
    int Delete(int tripIds);

    @Query("DELETE FROM Trip")
    void deleteAll();


}
