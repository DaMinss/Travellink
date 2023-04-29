package com.example.travellink.Auth;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.travellink.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class EditProfileImageFragment extends DialogFragment {
    //access camera
    protected static final int REQUEST_CODE_CAMERA = 1000;
    protected static final int REQUEST_CODE_PERMISSIONS_CAMERA = 1250;
    protected static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Uri image_uri;
    Button remove, add;
    LinearLayout add_img;
    ImageView profile_image;


    public EditProfileImageFragment() {
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_edit_profile_image, container, false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        add = root.findViewById(R.id.buttonAdd);
        add_img = root.findViewById(R.id.billing);
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        profile_image = root.findViewById(R.id.image_profile);
        remove = root.findViewById(R.id.buttonRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_uri = null;
                profile_image.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_uri == null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(null)
                            .build();
                    user.updateProfile(profileUpdates);
                    startActivity(new Intent(getActivity(), SplashScreen.class));
                    getActivity().finish();
                    dismiss();

                } else if (user.getPhotoUrl() != null) {
                    dismiss();
                } else {
                    String userID = user.getUid();
                    StorageReference profileImageRef = storageRef.child("profile_images/" + userID + ".jpg");
                    UploadTask uploadTask = profileImageRef.putFile(image_uri);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Get the download URL of the image
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profileImageURL = uri.toString();
                                    // Save the download URL to the user's profile in Firebase Authentication
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(profileUpdates);
                                    startActivity(new   Intent(getActivity(), SplashScreen.class));
                                    getActivity().finish();
                                    dismiss();
                                }
                            });
                        }
                    });
                }
            }

        });
        if (user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(profile_image);
            profile_image.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
        } else {
            add_img.setVisibility(View.VISIBLE);
        }
        return root;
    }

    //camera
    protected boolean allPermissionsGranted_CAMERA() {
        for (String permission : REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    protected Uri saveImage(Bitmap bitmap) {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
        String path = Environment.DIRECTORY_PICTURES + File.separator + "Travellink";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");

        ContentResolver resolver = getActivity().getContentResolver();
        image_uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try (OutputStream stream = resolver.openOutputStream(image_uri)) {
            // Perform operations on "stream".
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Saving Image FAILED.", Toast.LENGTH_SHORT).show();
            return null;

        }
        return image_uri;
    }

    protected void takePicture() {
        // Ask for camera permissions.
        if (!allPermissionsGranted_CAMERA()) {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS_CAMERA);
            return;
        }
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent file = new Intent(Intent.ACTION_GET_CONTENT);
        file.setType("image/*");
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        Intent chooseIntent = Intent.createChooser(camera, "Select Image");
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{file, gallery});

        startActivityForResult(chooseIntent, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri uri = data.getData();
                if (null == uri) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    uri = saveImage(bitmap);
                }
                image_uri = uri;
                profile_image.setImageURI(uri);
                remove.setVisibility(View.VISIBLE);
                return;
            }
            Toast.makeText(getContext(), "Select Image Failed.", Toast.LENGTH_SHORT).show();
        }
    }
}