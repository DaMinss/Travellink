package com.example.travellink.database;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellink.Trip.Trip;

import org.checkerframework.framework.qual.EnsuresQualifier;

import java.util.List;

@Dao
public interface TripDAO {
    @Query("SELECT * FROM trip")
    List<Trip> getAll();

    @Query("SELECT * FROM trip WHERE id = :tripIds")
    LiveData<Trip> loadTripByID(int tripIds);

    @Query("SELECT trip.*, SUM(expense.Expense_Price) as SUMOfExpense  FROM trip LEFT OUTER JOIN expense ON trip.id = expense.Trip_ID  GROUP BY trip.id")
    LiveData<List<Trip_withTotalPrice>> getAllTripWithTotalExpense();

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
    void update( LiveData<Trip> trip);

    @Query("DELETE FROM Trip")
    void deleteAll();


}
