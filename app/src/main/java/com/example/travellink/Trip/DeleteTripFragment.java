package com.example.travellink.Trip;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.MainActivity;
import com.example.travellink.R;
import com.example.travellink.database.TravelDatabase;
import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;

import java.io.Console;
import java.util.Objects;

public class DeleteTripFragment extends DialogFragment {
Button proceed;
TextView name;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_delete_trip, container, false);
        Bundle bundle = getArguments();
        int id = bundle.getInt("trip_id");
        TripViewModel tripViewModel = new ViewModelProvider(getActivity()).get(TripViewModel.class);
        name = root.findViewById(R.id.nameTrip);
        tripViewModel.getTrip(id).observe(getViewLifecycleOwner(), trip -> {
            name.setText(trip.getTrip_name());
        });
        proceed = root.findViewById(R.id.confirmDelete);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = TravelDatabase.getInstance(getActivity()).tripDAO().Delete(id);
                if(status > 0){
                    Toast.makeText(getActivity(), "The selected trip has been deleted successfully", Toast.LENGTH_SHORT);

                }else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                }
            }
        });
        return root;
    }
}