package com.example.travellink.Trip.TripModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TripViewModel extends AndroidViewModel {
    FirebaseAuth myAuth;
    public static TripRepo repo;
    String userID;
    public final LiveData<List<Trip>> Trips;
    protected final MutableLiveData<String> _DataStatus = new MutableLiveData<>();
    public TripViewModel(@NonNull Application application) {
        super(application);
        repo = new TripRepo(application);
       Trips = repo.getAllData();
       myAuth = FirebaseAuth.getInstance();
       if(myAuth.getCurrentUser() != null) {
           userID = myAuth.getCurrentUser().getUid();
       }
    }
    public LiveData<Trip> getTrip(int id) { return repo.getTrip(id);}
    public LiveData<TripDAO.Trip_withTotalPrice> getTripPrice(int id) { return repo.getTripandPrice(id);}
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTripandPrice() {return repo.getTripAndSum();}
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getSubmittedTripandPrice() {return repo.getSubmittedTripAndSum();}
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTop3() {return repo.getTop3TripAndSum();}
    public LiveData<List<TripDAO.Trip_withTotalPrice>> getRecent() {return repo.getRecentTripAndSum();}
    public MutableLiveData<String> getData() {
        return _DataStatus;
    }
    public void setData(String value) {
        _DataStatus.setValue(value);
    }
    public void updateTrip(Trip trip) {
       repo.updateTrip(trip,userID );
    }
}
