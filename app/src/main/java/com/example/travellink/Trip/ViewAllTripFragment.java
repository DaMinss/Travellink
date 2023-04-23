package com.example.travellink.Trip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.Trip;
import com.example.travellink.Trip.TripModel.TripAdapter;
import com.example.travellink.Trip.TripCloudModel.TripCloudAdapter;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.Trip.TripCloudModel.TripWithTotalExpense;
import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class ViewAllTripFragment extends Fragment {
    RecyclerView viewTripList;
    TripAdapter tripAdapter;
    TripCloudAdapter tripCloudAdapter;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;
    private List<TripWithTotalExpense> listOfTrips1;
    private SearchView searchTrip;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";
    androidx.appcompat.widget.SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_all_trip, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        viewTripList = root.findViewById(R.id.tripView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        viewTripList.setLayoutManager(linearLayoutManager);
        searchView = root.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tripAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tripAdapter != null) {
                    tripAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        Cloud();

        return root;
    }

    public void Cloud() {
            TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(TripViewModel.class);
            viewModel.getTripandPrice().observe(getActivity(), trips -> {
                Log.d("trip==>", "==>" + trips);
                listOfTrips = trips;
                viewTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
                tripAdapter = new TripAdapter(trips, getActivity());
                viewTripList.setAdapter(tripAdapter);
            });
    }
}