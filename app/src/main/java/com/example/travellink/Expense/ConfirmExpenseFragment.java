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
import android.widget.ProgressBar;
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
import java.util.Random;
import java.util.UUID;


public class ConfirmExpenseFragment extends DialogFragment {

    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    FirebaseStorage fire_storage;
    String user_id = "";
    Button Return, Confirm;
    protected Expense expense;
    protected int trip_id;
    ProgressBar progressBar;

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
        fire_storage = FirebaseStorage.getInstance();
        progressBar = root.findViewById(R.id.progress);
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
            progressBar.setVisibility(View.VISIBLE);
            user_id = myAuth.getCurrentUser().getUid();
            CollectionReference expensesRef = fire_store.collection("my_expenses").document(user_id).collection("expenses");
            Query query = expensesRef.orderBy("id", Query.Direction.DESCENDING).limit(1);
            String imageUri = expense.getImage_Bill();
            if (!imageUri.equals("")) {

                StorageReference imageRef = fire_storage.getReference().child("UserImage/").child(user_id);

                UploadTask uploadTask = imageRef.putFile(Uri.parse(imageUri));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image upload successful, get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                // Image download URL retrieved, save the expense to Firestore
                                expense.setImage_Bill(downloadUrl.toString());
                                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    int nextId = generateRandomId();
                                    expense.setExpense_Id(nextId);
                                expensesRef.add(expense)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // Expense added successfully
                                                long status = TravelDatabase.getInstance(getActivity()).expenseDAO().insertExpense(expense);
                                                if (status > 0) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(), "Your expense has been uploaded successfully", Toast.LENGTH_SHORT).show();
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
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Failed to upload expense", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });
                        }
                        }).addOnFailureListener(e -> Log.e(TAG, "Error", e));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error uploading image
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    int nextId = generateRandomId();
                    expense.setExpense_Id(nextId);
                    expensesRef.add(expense)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Expense added successfully
                                    long status = TravelDatabase.getInstance(getActivity()).expenseDAO().insertExpense(expense);
                                    if (status > 0) {
                                        Toast.makeText(getActivity(), "Your expense has been uploaded successfully", Toast.LENGTH_SHORT).show();
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
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to upload expense", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        } else {
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
    private int generateRandomId() {
        Random random = new Random();
        return random.nextInt(1000000);
    }

}