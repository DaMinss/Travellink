package com.example.travellink.database;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TripRepo {
    public interface OnTripDataChangedListener {
        void onTripDataChanged();
    }

    private OnTripDataChangedListener onTripDataChangedListener;

    public void setOnTripDataChangedListener(OnTripDataChangedListener listener) {
        this.onTripDataChangedListener = listener;
    }
    private TripDAO tripDao;
    private ExpenseDAO expenseDAO;
    private LiveData<List<TripDAO.Trip_withTotalPrice>> allTripSum;
    private LiveData<List<Trip>> allTrips;
    FirebaseFirestore fire_store;

    public TripRepo(Application application) {
        TravelDatabase db = TravelDatabase.getInstance(application);
        tripDao = db.tripDAO();
//        expenseDAO = db.expenseDao();
        allTripSum = tripDao.getAllTripWithTotalExpense();
    }

    public LiveData<List<Trip>> getAllData() {
        return allTrips;
    }

    public void insert(Trip trip) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            tripDao.insertTrip(trip);
        });
    }
    public void insertCloud(Trip trip) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            long id = tripDao.insertTrip(trip);
            trip.setId((int) id); // set the generated id back to the Trip object
        });
    }

    public void deleteTrip(int id) {
        TravelDatabase.databaseWriteExecutor.execute(() -> {
            tripDao.Delete(id);
        });
    }

    public LiveData<Trip> getTrip(int id) {
        return tripDao.loadTripByID(id);
    }

    public LiveData<TripDAO.Trip_withTotalPrice> getTripandPrice(int id) {
        return tripDao.getTripWithTotalExpense(id);
    }

    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTripAndSum() {
        return tripDao.getAllTripWithTotalExpense();
    }

    public LiveData<List<TripDAO.Trip_withTotalPrice>> getSubmittedTripAndSum() {
        return tripDao.getAllSubmittedTripWithTotalExpense();
    }

    public LiveData<List<TripDAO.Trip_withTotalPrice>> getRecentTripAndSum() {
        return tripDao.getRecent5TripWithTotalExpense();
    }

    public LiveData<List<TripDAO.Trip_withTotalPrice>> getTop3TripAndSum() {
        return tripDao.getTop3TripWithTotalExpense();
    }

    public void resetDatabase() {
        tripDao.deleteAll();
    }


    public void updateTrip(Trip trip, String userID) {
        CollectionReference tripsRef = FirebaseFirestore.getInstance().collection("my_trips").document(userID).collection("trips");
        Query query = tripsRef.whereEqualTo("id", trip.getId());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot.isEmpty()) {
                    // Trip not found, do nothing
                    return;
                }
                DocumentSnapshot tripDocSnapshot = querySnapshot.getDocuments().get(0);
                tripDocSnapshot.getReference().set(trip)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update the LiveData list

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error updating trip", e);
                            }
                        });
            } else {
                Log.e(TAG, "Error getting trip document", task.getException());
            }
        });
    }



}
