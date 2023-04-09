package com.example.travellink.Trip;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.MapWithSearchFragment;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.database.TravelDatabase;
import com.example.travellink.database.TripDAO;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;


public class UpdateTripFragment extends DialogFragment {

    TextInputLayout name,departure, arrive, date, note;
    TextInputEditText tripName, tripDeparture, tripArrive, tripStartDate, tripNote;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateListener;
    Button Edit;
    ImageView map;
    int id;

    public UpdateTripFragment() {
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_update_trip, container, false);
        name = root.findViewById(R.id.name);
        departure = root.findViewById(R.id.depart1);
        arrive = root.findViewById(R.id.arrival);
        date = root.findViewById(R.id.Date);
        note = root.findViewById(R.id.note);
        tripName = root.findViewById(R.id.editTripName);
        tripStartDate = root.findViewById(R.id.edit_start_date);
        tripNote = root.findViewById(R.id.editNote);
        tripDeparture = root.findViewById(R.id.editTripDepart);
        tripArrive =root.findViewById(R.id.editTripArrival);
        map = root.findViewById(R.id.edit_open_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapWithSearchFragment mapsFragment = new MapWithSearchFragment();
                mapsFragment.show(getActivity().getSupportFragmentManager(), "Select location");
            }
        });
        tripStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(getContext(), dateListener, year, month, day);

                datePickerDialog.show();

            }
        });
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(ContentValues.TAG, "onDateSet: dd/mmmm/yyyy" + day + "/" + month + "/" + year);
                tripStartDate.setText(day + "-" + month + "-" + year);
            }
        };

        Bundle bundle = getArguments();
        id = bundle.getInt("trip_id");
        Edit = root.findViewById(R.id.buttonEditTrip);
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
                if (validation() == false) {
                    return;
                } else {
                    int status = TravelDatabase.getInstance(getActivity()).tripDAO().updateTrip(get_data(id));
                    if(status > 0){
                        Toast.makeText(getActivity(), "The selected trip has been update successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Trip trip = TravelDatabase.getInstance(getActivity()).tripDAO().TripByID(id);
        setView(trip);
        return root;
    }
    private Trip get_data(int id) {
        String trip_name = tripName.getText().toString();
        String trip_depart = tripDeparture.getText().toString();
        String trip_arrive = tripArrive.getText().toString();
        String Start_date = tripStartDate.getText().toString();
        String Note = tripNote.getText().toString();
        String status = "Draft";
        String End_date = null;
        return new Trip(id, trip_name, trip_depart, trip_arrive, status, Start_date, End_date, Note);

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


    public void setView(Trip trip){
        tripName.setText(trip.getTrip_name());
        tripDeparture.setText(trip.getTrip_departure());
        tripArrive.setText(trip.getTrip_arrival());
        tripStartDate.setText(trip.getTrip_start_date());
        tripNote.setText(trip.getNote());

    }
}