package com.example.travellink.database;

import android.app.Application;
import android.content.Context;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.Trip.TripModel.Trip;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CloudRepo {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TripRepo tripRepo;
    ExpenseRepo expenseRepo;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Context context;

    public CloudRepo(Context context) {
        this.context = context;
        tripRepo = new TripRepo((Application) context.getApplicationContext());
        expenseRepo = new ExpenseRepo((Application) context.getApplicationContext());
    }

    public void ImportData(String userID) {
        db.collection("my_trips").document(userID).collection("trips")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> trips = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot trip : trips) {
                            Trip getTrip = new Trip();
                            getTrip.setId(Integer.parseInt(trip.get("id").toString()));
                            getTrip.setTrip_name(trip.get("trip_name").toString());
                            getTrip.setTrip_start_date(trip.get("trip_start_date").toString());
//                            getTrip.setTrip_end_date(trip.get("trip_end_date").toString());
                            getTrip.setTrip_arrival(trip.get("trip_arrival").toString());
                            getTrip.setTrip_departure(trip.get("trip_departure").toString());
                            getTrip.setTrip_status(trip.get("trip_status").toString());
                            getTrip.setNote(trip.get("note").toString());
                            tripRepo.insert(getTrip);
                        }
                    }
                });

        db.collection("my_expenses").document(userID).collection("expenses")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> expenses = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot expense : expenses) {
                            Expense getExpense = new Expense();
                            getExpense.setExpense_Id(Integer.parseInt(expense.get("expense_Id").toString()));
                            getExpense.setExpense_Name(expense.get("expense_Name").toString());
                            getExpense.setExpense_Price(expense.get("expense_Price").toString());
                            getExpense.setExpense_Type(expense.get("expense_Type").toString());
                            getExpense.setExpense_Comment(expense.get("expense_Comment").toString());
                            getExpense.setImage_Bill(expense.get("image_Bill").toString());
                            getExpense.setExpense_EndDate(expense.get("expense_EndDate").toString());
                            getExpense.setExpense_StartDate(expense.get("expense_StartDate").toString());
                            getExpense.setExpense_Location_Arrival(expense.get("expense_Location_Arrival").toString());
                            getExpense.setExpense_Location_Departure(expense.get("expense_Location_Departure").toString());
                            getExpense.setTrip_ID(Integer.parseInt(expense.get("trip_ID").toString()));
                            expenseRepo.insertExpense(getExpense);
                        }
                    }
                });
    }

    public void ResetData() {
        tripRepo.resetDatabase();
    }
}


