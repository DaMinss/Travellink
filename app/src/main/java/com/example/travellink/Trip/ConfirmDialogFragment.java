package com.example.travellink.Trip;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


public class ConfirmDialogFragment extends DialogFragment {
    Button Return, Confirm;
    protected Trip trip;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";

    public ConfirmDialogFragment(Trip trip_) {
        trip = trip_;
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
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
        if (myAuth.getCurrentUser() != null) {
            user_id = myAuth.getCurrentUser().getUid();
            CollectionReference tripRef = fire_store.collection("my_trips").document(user_id).collection("trips");
            Query query = tripRef.orderBy("id", Query.Direction.DESCENDING).limit(1);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int nextId = 1;
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    nextId = documentSnapshot.getLong("id").intValue() + 1;
                }
                Trip newTrip = new Trip(nextId, trip.getTrip_name(), trip.getTrip_departure(), trip.getTrip_arrival(), trip.getTrip_status(), trip.getTrip_start_date(), trip.getTrip_end_date(), trip.getNote());
                tripRef.add(newTrip)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                long status = TravelDatabase.getInstance(getActivity()).tripDAO().insertTrip(trip);
                                if (status > 0) {
                                    Toast.makeText(getActivity(), "Your trip has been uploaded successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                                } else {
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to upload trip", Toast.LENGTH_SHORT).show();
                            }
                        });
            }).addOnFailureListener(e -> Log.e(TAG, "Error querying for current maximum trip ID", e));

        } else {
            long status = TravelDatabase.getInstance(getActivity()).tripDAO().insertTrip(trip);
            if (status > 0) {
                Toast.makeText(getActivity(), "Your trip has been added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            } else {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }


    }
}