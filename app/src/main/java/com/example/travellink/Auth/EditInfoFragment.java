package com.example.travellink.Auth;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.travellink.R;
import com.example.travellink.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditInfoFragment extends DialogFragment {
    TextInputEditText firstName, lastName, phoneNumb;
    Button edit;
    FirebaseAuth myAuth;

    public EditInfoFragment() {
        // Required empty public constructor
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_edit_info, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        firstName = root.findViewById(R.id.registerFirstName);
        lastName = root.findViewById(R.id.registerLastName);
        phoneNumb = root.findViewById(R.id.registerPhonenumb);
        db.collection("user").document(myAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            firstName.setText(userData.getFirst_name());
                            lastName.setText(userData.getLast_name());
                            phoneNumb.setText(userData.getPhone());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                });
        edit = root.findViewById(R.id.buttonEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fName = firstName.getText().toString();
                String lName = lastName.getText().toString();
                String num = phoneNumb.getText().toString();
                DocumentReference userRef = db.collection("user").document(myAuth.getCurrentUser().getUid());
                userRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    UserData userData = documentSnapshot.toObject(UserData.class);
                                    userData.setFirst_name(fName);
                                    userData.setLast_name(lName);
                                    userData.setPhone(num);
                                    Map<String, Object> userUpdates = new HashMap<>();
                                    userUpdates.put("first_name", userData.getFirst_name());
                                    userUpdates.put("last_name", userData.getLast_name());
                                    userUpdates.put("phone", userData.getPhone());
                                    userRef.update(userUpdates);
                                    startActivity(new Intent(getActivity(), SplashScreen.class));
                                    getActivity().finish();
                                    dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Fail to update", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return root;
    }
}