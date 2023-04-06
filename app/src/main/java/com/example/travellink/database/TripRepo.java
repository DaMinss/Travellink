package com.example.travellink.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.travellink.Trip.Trip;

import java.util.List;

public class TripRepo {
    private TripDAO tripDao;
    private ExpenseDAO expenseDAO;
    private LiveData<List<TripDAO.Trip_withTotalPrice>> allTripSum;
    private LiveData<List<Trip>> allTrips;

    public TripRepo(Application application) {
        TravelDatabase db = TravelDatabase.getInstance(application);
        tripDao = db.tripDAO();
//        expenseDAO = db.expenseDao();
        allTripSum = tripDao.getAllTripWithTotalExpense();
    }
    public LiveData<List<Trip>> getAllData() { return allTrips; }
    public void insert(Trip trip) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            tripDao.insertTrip(trip);
        });
    }
    public void deleteTrip( int id) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            tripDao.Delete(id);
        });
    }
    public LiveData<Trip> getTrip(int id) {
        return tripDao.loadTripByID(id);
    }
//    public void updateTrip( LiveData<Trip> trip){
//     TravelDatabase.databaseWriteExecutor.execute(() -> tripDao.update(trip));
//    }
//    public void deleteTrip(Trip trip) {
//       TravelDatabase.databaseWriteExecutor.execute(() -> tripDao.delete(trip));
//    }
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTripAndSum(){
        return tripDao.getAllTripWithTotalExpense();
    }
    public void resetLocalDatabase() {
        tripDao.deleteAll();
    }
}
