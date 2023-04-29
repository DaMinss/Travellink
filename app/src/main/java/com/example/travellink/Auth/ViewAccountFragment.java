package com.example.travellink.Auth;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.R;
import com.example.travellink.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewAccountFragment extends Fragment {
    //access camera

    Uri image_uri;
    TextView firstName, lastName, email, phone, username;
    CircleImageView profile_image;
    FirebaseAuth myAuth;
    ImageView addImage, edit;
    LottieAnimationView profileDefault;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_account, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        firstName = root.findViewById(R.id.firstName);
        lastName = root.findViewById(R.id.lastName);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.phonenum);
        profileDefault = root.findViewById(R.id.userMain);
        profile_image = root.findViewById(R.id.img_profile);
        username = root.findViewById(R.id.header_username);
        addImage = root.findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileImageFragment editProfileImageFragment = new EditProfileImageFragment();
                editProfileImageFragment.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        edit = root.findViewById(R.id.editProfile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditInfoFragment editInfoFragment = new EditInfoFragment();
                editInfoFragment.show(getActivity().getSupportFragmentManager(), null);
            }
        });
        db.collection("user").document(myAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            username.setText(userData.getFirst_name() + " " + userData.getLast_name());
                            firstName.setText(userData.getFirst_name());
                            lastName.setText(userData.getLast_name());
                            email.setText(userData.getEmail());
                            phone.setText(userData.getPhone());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to retrieve user data", Toast.LENGTH_SHORT).show();
                    }
                });
   if(myAuth.getCurrentUser().getPhotoUrl() != null){
       Picasso.get().load(myAuth.getCurrentUser().getPhotoUrl()).into(profile_image);
       profile_image.setVisibility(View.VISIBLE);
       profileDefault.setVisibility(View.GONE);
   }else {
       profileDefault.setVisibility(View.VISIBLE);
   }
        return root;
    }


}