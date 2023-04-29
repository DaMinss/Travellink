package com.example.travellink.Expense;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.Map_WithSearchFragment2;
import com.example.travellink.R;
import com.example.travellink.database.TravelDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class UpdateExpenseFragment extends DialogFragment implements MapWithSearchFragment.OnLocationSelectedListener, Map_WithSearchFragment2.OnLocationSelectedListener1 {

    int id, trip_id;
    ConstraintLayout detail;
    AutoCompleteTextView selection;
    TextView title, title_bill;
    TextInputLayout name, departure, arrive, start_date, end_date, descript, destination, category, amount;
    LinearLayout billing, arrive_layout;
    TextInputEditText expenseName, expenseDeparture, expenseArrive, expenseStartDate, expenseEndDate, expenseDescription, expenseDestination, expenseAmount;
    final Calendar calendar = Calendar.getInstance();
    public ImageView back, image_bill;
    CardView category_layout;
    Button update, remove;
    LottieAnimationView map, map1;
    Uri image_uri = null;
    ImageView billingImage;
    ProgressBar progressBar;
    //access camera
    protected static final int REQUEST_CODE_CAMERA = 1000;
    protected static final int REQUEST_CODE_PERMISSIONS_CAMERA = 1250;
    protected static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //access location
    protected final int LOCATION_REFRESH_TIME = 1000; // 1 seconds to update.
    protected final int LOCATION_REFRESH_DISTANCE = 5; // 5 meters to update.
    protected final int REQUEST_CODE_PERMISSIONS_GPS = 105;
    protected final String[] REQUIRED_PERMISSIONS_GPS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    protected LocationManager locationManager;
    String selectedText = "";
    public ArrayAdapter<String> arrayAdapter;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    FirebaseStorage fire_storage;


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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_update_expense2, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        fire_storage = FirebaseStorage.getInstance();
        category_layout = root.findViewById(R.id.category_layout);
        progressBar = root.findViewById(R.id.progress);
        detail = root.findViewById(R.id.detail_expense);
        arrive_layout = root.findViewById(R.id.arrive);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        category_layout.setLayoutTransition(layoutTransition);
        billingImage = root.findViewById(R.id.image_profile);
        title = root.findViewById(R.id.expense_Name);
        name = root.findViewById(R.id.name);
        descript = root.findViewById(R.id.description);
        destination = root.findViewById(R.id.destination);
        category = root.findViewById(R.id.category);
        amount = root.findViewById(R.id.amount);
        billing = root.findViewById(R.id.billing);
        image_bill = root.findViewById(R.id.image_profile);
        title_bill = root.findViewById(R.id.textView14);
        departure = root.findViewById(R.id.depart1);
        arrive = root.findViewById(R.id.arrive1);
        start_date = root.findViewById(R.id.sDate);
        end_date = root.findViewById(R.id.eDate);
        arrayAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.spinner,
                getResources().getStringArray(R.array.menu)) {
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = getResources().getStringArray(R.array.menu);
                        filterResults.count = getResources().getStringArray(R.array.menu).length;
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
        };

        selection = (AutoCompleteTextView) root.findViewById(R.id.selection);
        selection.setAdapter(arrayAdapter);
        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                selection.showDropDown();
            }
        });
        selection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedText = parent.getItemAtPosition(position).toString();
                expenseDeparture.setText("");
                expenseDestination.setText("");
                expenseArrive.setText("");
                if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
                    detail.setVisibility(View.VISIBLE);
                    destination.setVisibility(View.VISIBLE);
                    departure.setVisibility(View.GONE);
                    arrive_layout.setVisibility(View.GONE);
                } else if (selectedText.equals("Flight") || selectedText.equals("Taxi")) {
                    detail.setVisibility(View.VISIBLE);
                    destination.setVisibility(View.GONE);
                    departure.setVisibility(View.VISIBLE);
                    arrive_layout.setVisibility(View.VISIBLE);

                } else if (selectedText.equals("")) {
                    detail.setVisibility(View.GONE);
                }
            }
        });


        expenseName = root.findViewById(R.id.expenseName);
        expenseStartDate = root.findViewById(R.id.start_date);
        expenseEndDate = root.findViewById(R.id.end_date);
        expenseDescription = root.findViewById(R.id.expenseDescription);
        expenseDestination = root.findViewById(R.id.expense_destination);
        expenseDeparture = root.findViewById(R.id.expense_depart);
        expenseArrive = root.findViewById(R.id.expense_arrival);
        expenseAmount = root.findViewById(R.id.expenseAmount);

        map = root.findViewById(R.id.open_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeGPS();
                showMapWithSearchFragmentDialog();
            }
        });
        map1 = root.findViewById(R.id.open_map1);
        map1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeGPS();
                showMapWithSearchFragmentDialog1();
            }
        });
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date on the Calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create the TimePickerDialog and set the onTimeSet listener
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time on the Calendar object
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format the date/time and set it on the EditText field
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String dateTime = dateFormat.format(calendar.getTime());
                        expenseStartDate.setText(dateTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                // Show the TimePickerDialog
                timePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        expenseStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date on the Calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create the TimePickerDialog and set the onTimeSet listener
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time on the Calendar object
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Format the date/time and set it on the EditText field
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String dateTime = dateFormat.format(calendar.getTime());
                        expenseEndDate.setText(dateTime);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                // Show the TimePickerDialog
                timePickerDialog1.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        expenseEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog1.show();
            }
        });

        billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        remove = root.findViewById(R.id.buttonRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_uri = null;
                billingImage.setImageURI(null);
                remove.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.transparent_bg).into(image_bill);
            }
        });
        Bundle bundle = getArguments();
        id = bundle.getInt("ex_id");
        trip_id = bundle.getInt("t_id");
        Expense expense = TravelDatabase.getInstance(getActivity()).expenseDAO().getExpenseByID(id);
        setDetails(expense);
        update = root.findViewById(R.id.buttonUpdateExpense);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
                if (validation() == false) {
                    return;
                } else {
                    if (myAuth.getCurrentUser() != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        String user_id = myAuth.getCurrentUser().getUid();
                        CollectionReference expensesRef = fire_store.collection("my_expenses").document(user_id).collection("expenses");
                        Query expenseRef = expensesRef.whereEqualTo("expense_Id", id);
                        Expense expenseUpdate = get_data(id);
                        String imageUri = expenseUpdate.getImage_Bill();
                        Uri image = Uri.parse(imageUri);
                        if (!imageUri.equals("") && !imageUri.equals(image_bill_url)) {
                            File imageFile = new File(image.getPath());
                            String imageName = imageFile.getName();
                            StorageReference imageRef = fire_storage.getReference().child("UserImage/").child(user_id).child(imageName);

                            UploadTask uploadTask = imageRef.putFile(image);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image upload successful, get the download URL
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUrl) {
                                            // Image download URL retrieved, save the expense to Firestore
                                            expenseUpdate.setImage_Bill(downloadUrl.toString());
                                            expenseRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        // Update the expense with the new data
                                                        documentSnapshot.getReference().set(expenseUpdate)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        // Expense updated successfully
                                                                        int status = TravelDatabase.getInstance(getActivity()).expenseDAO().updateExpense(get_data(id));
                                                                        if (status > 0) {
                                                                            progressBar.setVisibility(View.GONE);
                                                                            Toast.makeText(getActivity(), "The selected trip has been update successfully", Toast.LENGTH_SHORT).show();
                                                                            dismiss();
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Error updating expense
                                                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error retrieving matching expenses
                                            Toast.makeText(getContext(), "Failed to retrieve", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error uploading image
                                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            expenseRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        // Update the expense with the new data
                                        documentSnapshot.getReference().set(expenseUpdate)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Expense updated successfully
                                                        int status = TravelDatabase.getInstance(getActivity()).expenseDAO().updateExpense(get_data(id));
                                                        if (status > 0) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(getActivity(), "The selected trip has been update successfully", Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        } else {
                                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error updating expense
                                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    } else {
                        int status = TravelDatabase.getInstance(getActivity()).expenseDAO().updateExpense(get_data(id));
                        if (status > 0) {
                            Toast.makeText(getActivity(), "The selected trip has been update successfully", Toast.LENGTH_SHORT).show();
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

    String image_bill_url;

    protected void setDetails(Expense expense) {
        expenseName.setText(expense.getExpense_Name());
        expenseDescription.setText(expense.getExpense_Comment());
        expenseAmount.setText(expense.getExpense_Price());
        expenseDeparture.setText(expense.getExpense_Location_Departure());
        expenseDestination.setText(expense.getExpense_Location_Departure());
        expenseArrive.setText(expense.getExpense_Location_Arrival());
        expenseStartDate.setText(expense.getExpense_StartDate());
        expenseEndDate.setText(expense.getExpense_EndDate());
        selection.setText(expense.getExpense_Type());
        selectedText = expense.getExpense_Type();
        selection.setSelection(arrayAdapter.getPosition(expense.getExpense_Type()));
        if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
            detail.setVisibility(View.VISIBLE);
            destination.setVisibility(View.VISIBLE);
            departure.setVisibility(View.GONE);
            arrive_layout.setVisibility(View.GONE);
        } else if (selectedText.equals("Flight") || selectedText.equals("Taxi")) {
            detail.setVisibility(View.VISIBLE);
            destination.setVisibility(View.GONE);
            departure.setVisibility(View.VISIBLE);
            arrive_layout.setVisibility(View.VISIBLE);
        }
        image_bill_url = expense.getImage_Bill();
        if (expense.getImage_Bill().isEmpty()) {
            Toast.makeText(getActivity(), "No billing attachment", Toast.LENGTH_SHORT).show();
        } else {
            Picasso.get().load(image_bill_url).into(image_bill);
            remove.setVisibility(View.VISIBLE);
        }
    }

    private Expense get_data(int _id) {
        int ex_id = _id;
        int t_id = trip_id;
        String expense_name = expenseName.getText().toString();
        String expense_category = selectedText;
        String expense_departure = "";
        if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
            expense_departure = expenseDestination.getText().toString();
        } else {
            expense_departure = expenseDeparture.getText().toString();
        }
        String expense_arrive = expenseArrive.getText().toString();
        String Start_dateandtime = expenseStartDate.getText().toString();
        String End_dateandtime = expenseEndDate.getText().toString();
        String expense_Price = expenseAmount.getText().toString();
        String description = expenseDescription.getText().toString();
        String inputImage;
        if (image_uri != null) {
            inputImage = String.valueOf(image_uri);
        } else if (image_uri == null) {
            inputImage = "";
        } else {
            inputImage = image_bill_url;
        }
        String expense_image = inputImage;
        return new Expense(ex_id, expense_name, expense_category, description, expense_image, expense_departure, expense_arrive, expense_Price, Start_dateandtime, End_dateandtime, t_id);

    }


    //camera
    protected boolean allPermissionsGranted_CAMERA() {
        for (String permission : REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
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
            Toast.makeText(getActivity(), "Saving Image FAILED.", Toast.LENGTH_SHORT).show();
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
                billingImage.setImageURI(uri);
                remove.setVisibility(View.VISIBLE);
                return;
            }
            Toast.makeText(getActivity(), "Select Image Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted_GPS() {
        for (String permission : REQUIRED_PERMISSIONS_GPS)
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    protected void takeGPS() {
        // Ask for permissions.
        if (!allPermissionsGranted_GPS()) {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS_GPS, REQUEST_CODE_PERMISSIONS_GPS);
            return;
        }
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) LOCATION_REFRESH_TIME, (float) LOCATION_REFRESH_DISTANCE, (LocationListener) getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean validation() {
        if (expenseName.getText().toString().isEmpty()) {
            name.setError("You need to enter your expense name !");
            return false;
        } else if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
            if (expenseDestination.getText().toString().isEmpty()) {
                destination.setError("You need to enter your destination !");
                return false;
            }
        } else if (selectedText.equals("Flight") || selectedText.equals("Taxi")) {
            if (expenseDeparture.getText().toString().isEmpty()) {
                departure.setError("You need to enter your departure destination !");
                return false;
            } else if (expenseArrive.getText().toString().isEmpty()) {
                arrive.setError("You need to enter your arrival destination !");
                return false;
            }
        } else if (expenseStartDate.getText().toString().isEmpty()) {
            start_date.setError("You need to enter your start date !");
            return false;
        } else if (expenseEndDate.getText().toString().isEmpty()) {
            end_date.setError("You need to enter your end date !");
            return false;
        } else {
            name.setError(null);
            departure.setError(null);
            arrive.setError(null);
            start_date.setError(null);
            return true;
        }
        return true;
    }

    private void showMapWithSearchFragmentDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt("status", 1);
        MapWithSearchFragment mapDialog = new MapWithSearchFragment();
        mapDialog.setArguments(bundle);
        mapDialog.setOnLocationSelectedListener(this);
        mapDialog.show(getChildFragmentManager(), "map_dialog");
    }

    private void showMapWithSearchFragmentDialog1() {
        Bundle bundle = new Bundle();
        bundle.putInt("status", 1);
        Map_WithSearchFragment2 mapDialog = new Map_WithSearchFragment2();
        mapDialog.setArguments(bundle);
        mapDialog.setOnLocationSelectedListener1(this);
        mapDialog.show(getChildFragmentManager(), "map_dialog");
    }


    @Override
    public void onLocationSelected(String data) {
        if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
            expenseDestination.setText(data);
        } else {
            expenseDeparture.setText(data);
        }

    }

    @Override
    public void onLocationSelected1(String data) {
        expenseArrive.setText(data);
    }
}