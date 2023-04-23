package com.example.travellink.Expense;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.Trip.TripDetails;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ConfirmExpenseFragment extends DialogFragment {

    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    FirebaseStorage fire_storage;
    String user_id = "";
    Button Return, Confirm;
    protected Expense expense;
    protected int trip_id;

    public ConfirmExpenseFragment(Expense expense_, int myTripId) {
        expense = expense_;
        trip_id = myTripId;
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_confirm_expense, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        Return = root.findViewById(R.id.Return);
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Confirm = root.findViewById(R.id.confirm);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cloud();
            }
        });
        return root;
    }

    private void Cloud() {

            long status = TravelDatabase.getInstance(getActivity()).expenseDAO().insertExpense(expense);
            if (status > 0) {
                Toast.makeText(getActivity(), "Your expense has been added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), TripDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("trip_id", trip_id);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                getActivity().finish();
            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }


}