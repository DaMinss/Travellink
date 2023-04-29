package com.example.travellink.Trip;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                    if (myAuth.getCurrentUser() != null) {
                        String user_id = myAuth.getCurrentUser().getUid();
                        CollectionReference tripRef = fire_store.collection("my_trips").document(user_id).collection("trips");
                        Query query = tripRef.whereEqualTo("id", id);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Get a reference to the expense document and delete it
                                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Trip deleted successfully
                                            int status1 = TravelDatabase.getInstance(getActivity()).tripDAO().Delete(id);
                                            if (status1 > 0) {
                                                Toast.makeText(getActivity(), "The selected trip has been deleted successfully", Toast.LENGTH_SHORT).show();
                                                dismiss();

                                            } else {
                                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                            CollectionReference expensesRef = fire_store.collection("my_expenses").document(user_id).collection("expenses");
                                            Query query = expensesRef.whereEqualTo("trip_ID", id);
                                            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        // Get a reference to the expense document and delete it
                                                        documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Expense deleted successfully
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Error deleting expense
                                                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error executing query
                                                    Toast.makeText(getContext(), "No expense in this trip", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error deleting trip
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error executing query
                            }
                        });
                    } else {

                        int status1 = TravelDatabase.getInstance(getActivity()).tripDAO().Delete(id);
                        if (status1 > 0) {
                            Toast.makeText(getActivity(), "The selected trip has been deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (myAuth.getCurrentUser() != null) {
                        String user_id = myAuth.getCurrentUser().getUid();
                        CollectionReference expensesRef = fire_store.collection("my_expenses").document(user_id).collection("expenses");
                        Query query = expensesRef.whereEqualTo("expense_Id", ex_id);
                        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Get a reference to the expense document and delete it
                                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Expense deleted successfully
                                            int status1 = TravelDatabase.getInstance(getActivity()).expenseDAO().DeleteExpense(ex_id);
                                            if (status1 > 0) {
                                                Toast.makeText(getActivity(), "The selected expense has been deleted successfully", Toast.LENGTH_SHORT).show();
                                                dismiss();

                                            } else {
                                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error deleting expense
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error executing query
                            }
                        });
                    } else {
                        int status1 = TravelDatabase.getInstance(getActivity()).expenseDAO().DeleteExpense(ex_id);
                        if (status1 > 0) {
                            Toast.makeText(getActivity(), "The selected expense has been deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        return root;
    }
}