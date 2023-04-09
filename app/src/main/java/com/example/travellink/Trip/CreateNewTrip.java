package com.example.travellink.Trip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.MainActivity;
import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class CreateNewTrip extends AppCompatActivity {
    TextView title;
    TextInputLayout name,departure, arrive, date, note;
    LinearLayout depart, arrival;
    TextInputEditText tripName, tripDeparture, tripArrive, tripStartDate, tripNote;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateListener;
    private ImageView back;
    float v = 0;
    Button Create;
    ImageView map;
    LottieAnimationView loading;

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
                MapWithSearchFragment mapsFragment = new MapWithSearchFragment();
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
}