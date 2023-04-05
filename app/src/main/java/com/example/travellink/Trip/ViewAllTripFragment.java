package com.example.travellink.Trip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.travellink.R;
import com.example.travellink.Trip.TripAdapter;
import com.example.travellink.database.TripDAO;

import java.util.List;


public class ViewAllTripFragment extends Fragment {
    RecyclerView viewTripList;
    TripAdapter tripAdapter;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;
    private SearchView searchTrip;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_all_trip, container, false);
        viewTripList = root.findViewById(R.id.tripView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        viewTripList.setLayoutManager(linearLayoutManager);
        TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(TripViewModel.class);
        viewModel.getTripandPrice().observe(getActivity(), trips -> {
            Log.d("trip==>", "==>" + trips);
            listOfTrips = trips;
            viewTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
            tripAdapter = new TripAdapter(trips, getActivity());
            viewTripList.setAdapter(tripAdapter);
        });
       return root;
    }
}