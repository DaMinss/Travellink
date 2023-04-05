package com.example.travellink.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.travellink.Expense.Expense;
import com.example.travellink.Trip.Trip;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class, Expense.class}, version = 1)
public abstract class TravelDatabase extends RoomDatabase {
    private static  final String DATBASE_NAME = "travellink.db";
    private static TravelDatabase instance;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized TravelDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), TravelDatabase.class, DATBASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract TripDAO tripDAO();
    public abstract ExpenseDAO expenseDAO();
}
