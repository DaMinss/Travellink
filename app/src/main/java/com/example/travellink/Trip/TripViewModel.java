package com.example.travellink.Trip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;

import java.util.List;

public class TripViewModel extends AndroidViewModel {
    public static TripRepo repo;
    public final LiveData<List<Trip>> Trips;
    public TripViewModel(@NonNull Application application) {
        super(application);
        repo = new TripRepo(application);
       Trips = repo.getAllData();
    }
    public LiveData<Trip> getTrip(int id) { return repo.getTrip(id);}
//    public static void updateTrip( LiveData<Trip> trip) {repo.updateTrip(trip);}
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTripandPrice() {return repo.getTripAndSum();}
    public void deleteAll() {repo.resetLocalDatabase();}
}
