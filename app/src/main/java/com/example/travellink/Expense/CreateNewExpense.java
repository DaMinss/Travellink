package com.example.travellink.Expense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.Map_WithSearchFragment2;
import com.example.travellink.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CreateNewExpense extends AppCompatActivity implements MapWithSearchFragment.MapWithSearchFragmentInterface, Map_WithSearchFragment2.MapWithSearchFragmentInterface1 {
    int myTripId;
    ConstraintLayout detail;
    AutoCompleteTextView selection;
    TextView title, title_bill;
    TextInputLayout name, departure, arrive, start_date, end_date, descript, destination, category, amount;
    LinearLayout billing, arrive_layout;
    TextInputEditText expenseName, expenseDeparture, expenseArrive, expenseStartDate, expenseEndDate, expenseDescription, expenseDestination, expenseAmount;
    final Calendar calendar = Calendar.getInstance();
    public ImageView back, image_bill;
    float v = 0;
    CardView category_layout;
    Button Create, remove;
    LottieAnimationView map, map1;
    Uri image_uri = null;
    ImageView billingImage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_expense);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myTripId = bundle.getInt("trip_ids");
        }

        category_layout = findViewById(R.id.category_layout);
        detail = findViewById(R.id.detail_expense);
        arrive_layout = findViewById(R.id.arrive);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        category_layout.setLayoutTransition(layoutTransition);
        billingImage = findViewById(R.id.image_billing);
        title = findViewById(R.id.expense_Name);
        name = findViewById(R.id.name);
        descript = findViewById(R.id.description);
        destination = findViewById(R.id.destination);
        category = findViewById(R.id.category);
        amount = findViewById(R.id.amount);
        billing = findViewById(R.id.billing);
        image_bill = findViewById(R.id.image_billing);
        title_bill = findViewById(R.id.textView14);
        departure = findViewById(R.id.depart1);
        arrive = findViewById(R.id.arrive1);
        start_date = findViewById(R.id.sDate);
        end_date = findViewById(R.id.eDate);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner,
                getResources().getStringArray(R.array.menu));

        selection = (AutoCompleteTextView) findViewById(R.id.selection);
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


        expenseName = findViewById(R.id.expenseName);
        expenseStartDate = findViewById(R.id.start_date);
        expenseEndDate = findViewById(R.id.end_date);
        expenseDescription = findViewById(R.id.expenseDescription);
        expenseDestination = findViewById(R.id.expense_destination);
        expenseDeparture = findViewById(R.id.expense_depart);
        expenseArrive = findViewById(R.id.expense_arrival);
        expenseAmount = findViewById(R.id.expenseAmount);

        map = findViewById(R.id.open_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeGPS();
                MapWithSearchFragment mapsFragment = new MapWithSearchFragment();
                mapsFragment.show(getSupportFragmentManager(), "Select location");
            }
        });
        map1 = findViewById(R.id.open_map1);
        map1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeGPS();
                Map_WithSearchFragment2 mapsFragment = new Map_WithSearchFragment2();
                mapsFragment.show(getSupportFragmentManager(), "Select location");
            }
        });
        back = findViewById(R.id.backBTN);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        name.setTranslationX(800);
        descript.setTranslationX(800);
        billing.setTranslationX(800);
        category.setTranslationX(800);
        title_bill.setTranslationX(800);
        amount.setTranslationX(800);

        name.setAlpha(v);
        descript.setAlpha(v);
        billing.setAlpha(v);
        amount.setAlpha(v);
        name.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        category.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        descript.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        title_bill.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        billing.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        amount.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        title.setTranslationY(300);
        title.setAlpha(v);
        title.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date on the Calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create the TimePickerDialog and set the onTimeSet listener
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNewExpense.this, new TimePickerDialog.OnTimeSetListener() {
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
        DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date on the Calendar object
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Create the TimePickerDialog and set the onTimeSet listener
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(CreateNewExpense.this, new TimePickerDialog.OnTimeSetListener() {
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

        remove = findViewById(R.id.buttonRemove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_uri = null;
                billingImage.setImageURI(null);
                remove.setVisibility(View.GONE);
            }
        });
        Create = findViewById(R.id.buttonCreateExpense);
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpConfirm();
            }
        });
    }

    //camera
    protected boolean allPermissionsGranted_CAMERA() {
        for (String permission : REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
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

        ContentResolver resolver = this.getContentResolver();
        image_uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try (OutputStream stream = resolver.openOutputStream(image_uri)) {
            // Perform operations on "stream".
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Saving Image FAILED.", Toast.LENGTH_SHORT).show();
            return null;

        }
        return image_uri;
    }

    protected void takePicture() {
        // Ask for camera permissions.
        if (!allPermissionsGranted_CAMERA()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS_CAMERA);
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
            Toast.makeText(this, "Select Image Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted_GPS() {
        for (String permission : REQUIRED_PERMISSIONS_GPS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    protected void takeGPS() {
        // Ask for permissions.
        if (!allPermissionsGranted_GPS()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS_GPS, REQUEST_CODE_PERMISSIONS_GPS);
            return;
        }
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) LOCATION_REFRESH_TIME, (float) LOCATION_REFRESH_DISTANCE, (LocationListener) CreateNewExpense.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FirebaseAuth myAuth;
    FirebaseStorage fire_store;
    String image_storage_url;

    private Expense get_data() {
        int id = new Expense().getExpense_Id();
        int trip_id = myTripId;
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
        if (image_uri == null) {
            inputImage = "";
        } else {
            inputImage = String.valueOf(image_uri);
        }
        String expense_image = inputImage;
        return new Expense(id, expense_name, expense_category, description, expense_image, expense_departure, expense_arrive, expense_Price, Start_dateandtime, End_dateandtime, trip_id);
    }

    private void popUpConfirm() {
        if (validation() == true) {
            Expense expense = get_data();
            new ConfirmExpenseFragment(expense, myTripId).show(getSupportFragmentManager(), null);
            return;
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
        } else if (image_storage_url.toString().isEmpty()) {
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


    @Override
    public void getLocationFromMap(String address) {
        if (selectedText.equals("Food") || selectedText.equals("Shopping") || selectedText.equals("Hotel") || selectedText.equals("Others")) {
            expenseDestination.setText(address);
        } else {
            expenseDeparture.setText(address);
        }
    }

    @Override
    public void getLocationFromMap1(String address) {
        expenseArrive.setText(address);
    }
}