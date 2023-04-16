package com.example.travellink.Trip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.MainActivity;
import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.Map_WithSearchFragment2;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateNewTrip extends AppCompatActivity implements MapWithSearchFragment.MapWithSearchFragmentInterface, Map_WithSearchFragment2.MapWithSearchFragmentInterface1 {
    TextView title;
    TextInputLayout name, departure, arrive, date, note;
    LinearLayout depart, arrival;
    TextInputEditText tripName, tripDeparture, tripArrive, tripStartDate, tripNote;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateListener;
    private ImageView back;
    float v = 0;
    Button Create;
    ImageView map, map1;
    LottieAnimationView loading;
    protected final int LOCATION_REFRESH_TIME = 1000; // 1 seconds to update.
    protected final int LOCATION_REFRESH_DISTANCE = 5; // 5 meters to update.
    protected final int REQUEST_CODE_PERMISSIONS_GPS = 105;
    protected final String[] REQUIRED_PERMISSIONS_GPS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    protected LocationManager locationManager;

    public CreateNewTrip() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_trip);
        title = findViewById(R.id.textView9);
        name = findViewById(R.id.name);
        depart = findViewById(R.id.startDateandTime);
        departure = findViewById(R.id.depart1);
        arrive = findViewById(R.id.arrival);
        arrival = findViewById(R.id.arrive);
        date = findViewById(R.id.Date);
        note = findViewById(R.id.note);

        tripName = findViewById(R.id.tripName);
        tripStartDate = findViewById(R.id.start_date);
        tripNote = findViewById(R.id.Note);
        tripDeparture = findViewById(R.id.tripDepart);
        tripArrive = findViewById(R.id.tripArrival);

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
                startActivity(new Intent(CreateNewTrip.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        name.setTranslationX(800);
        depart.setTranslationX(800);
        arrival.setTranslationX(800);
        date.setTranslationX(800);
        note.setTranslationX(800);

        name.setAlpha(v);
        depart.setAlpha(v);
        arrival.setAlpha(v);
        date.setAlpha(v);
        note.setAlpha(v);

        name.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        depart.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        arrival.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        date.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        note.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        title.setTranslationY(300);
        title.setAlpha(v);
        title.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();

        tripStartDate = findViewById(R.id.start_date);
        tripStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(CreateNewTrip.this, dateListener, year, month, day);

                datePickerDialog.show();

            }
        });
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                CharSequence strDate = null;
                Time chosenDate = new Time();
                chosenDate.set(day, month, year);
                long dtDob = chosenDate.toMillis(true);

                strDate = DateFormat.format("MM/dd/yyyy", dtDob);

                tripStartDate.setText(strDate);
            }
        };
        loading = findViewById(R.id.lottieAnimationView);
        Create = findViewById(R.id.buttonCreateTrip);
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation() == false) {
                    loading.setVisibility(View.GONE);
                } else {
                    loading.setVisibility(View.VISIBLE);
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validation();
                        popUpConfirm();
                        loading.setVisibility(View.GONE);
                    }
                }, 2500);
            }
        });
    }

    private Trip get_data() {
        int id = new Trip().getId();
        String trip_name = tripName.getText().toString();
        String trip_depart = tripDeparture.getText().toString();
        String trip_arrive = tripArrive.getText().toString();
        String Start_date = tripStartDate.getText().toString();
        String Note = tripNote.getText().toString();
        String status = "Draft";
        String End_date = null;
        return new Trip(id, trip_name, trip_depart, trip_arrive, status, Start_date, End_date, Note);

    }

    private void popUpConfirm() {
        if (validation() == true) {
            Trip trip = get_data();
            new ConfirmDialogFragment(trip).show(getSupportFragmentManager(), null);
            return;
        }
    }

    protected boolean validation() {
        if (tripName.getText().toString().isEmpty()) {
            name.setError("You need to enter your trip name !");
            return false;
        } else if (tripDeparture.getText().toString().isEmpty()) {
            departure.setError("You need to enter your departure destination !");
            return false;
        } else if (tripArrive.getText().toString().isEmpty()) {
            arrive.setError("You need to enter your arrival destination !");
            return false;
        } else if (tripStartDate.getText().toString().isEmpty()) {
            date.setError("You need to enter your start date !");
            return false;
        } else {
            name.setError(null);
            departure.setError(null);
            arrive.setError(null);
            date.setError(null);
            return true;
        }
    }

    @Override
    public void getLocationFromMap(String address) {
        tripDeparture.setText(address);
    }

    @Override
    public void getLocationFromMap1(String address) {
        tripArrive.setText(address);
    }

    private boolean allPermissionsGranted_GPS() {
        for (String permission : REQUIRED_PERMISSIONS_GPS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }

    protected void takeGPS() {
        // Ask for camera permissions.
        if (!allPermissionsGranted_GPS()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS_GPS, REQUEST_CODE_PERMISSIONS_GPS);
            return;
        }
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) LOCATION_REFRESH_TIME, (float) LOCATION_REFRESH_DISTANCE, (LocationListener) this);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS_GPS) {
            if (allPermissionsGranted_GPS()) {
                return;
            }

            Toast.makeText(this, "GPS Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
        }
    }

}