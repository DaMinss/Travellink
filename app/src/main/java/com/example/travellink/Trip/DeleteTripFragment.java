package com.example.travellink.Trip;

import static android.content.ContentValues.TAG;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class DeleteTripFragment extends DialogFragment {
    Button proceed;
    TextView name;
    FirebaseFirestore fire_store;
    FirebaseAuth myAuth;

    public DeleteTripFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_delete_trip, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();
        int id = bundle.getInt("trip_id");
        int ex_id = bundle.getInt("expense_id");
        int status = bundle.getInt("status");
        TripViewModel tripViewModel = new ViewModelProvider(getActivity()).get(TripViewModel.class);
        ExpenseViewModel expenseViewModel = new ViewModelProvider(getActivity()).get(ExpenseViewModel.class);
        name = root.findViewById(R.id.nameTrip);
        if (status == 1) {
                tripViewModel.getTrip(id).observe(getViewLifecycleOwner(), trip -> {
                    name.setText(trip.getTrip_name());
                });
        } else {
            expenseViewModel.getExpense(ex_id).observe(getViewLifecycleOwner(), expenses -> {
                name.setText(expenses.getExpense_Name());
            });
        }
        proceed = root.findViewById(R.id.confirmExit);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (status == 1) {

                        int status1 = TravelDatabase.getInstance(getActivity()).tripDAO().Delete(id);
                        if (status1 > 0) {
                            Toast.makeText(getActivity(), "The selected trip has been deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                else {
                        int status1 = TravelDatabase.getInstance(getActivity()).expenseDAO().DeleteExpense(ex_id);
                        if (status1 > 0) {
                            Toast.makeText(getActivity(), "The selected expense has been deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });


        return root;
    }
}