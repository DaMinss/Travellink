package com.example.travellink.Trip;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.TripCloudAdapter;
import com.example.travellink.Trip.TripModel.TripAdapter;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TripDAO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class ViewAllTripFragment extends Fragment {
    RecyclerView viewTripList;
    TripAdapter tripAdapter;
    TripCloudAdapter tripCloudAdapter;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;
    private SearchView searchTrip;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";
    androidx.appcompat.widget.SearchView searchView;
    LottieAnimationView  nodata;
    LinearLayout noTrip;
    ConstraintLayout content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_view_all_trip, container, false);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        viewTripList = root.findViewById(R.id.tripView);
        nodata = root.findViewById(R.id.lottieAnimationViewNodata);
        noTrip = root.findViewById(R.id.notrip);
        content = root.findViewById(R.id.viewTrips);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        viewTripList.setLayoutManager(linearLayoutManager);
        Cloud();
        searchView = root.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tripAdapter.getFilter().filter(query);
                tripCloudAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tripAdapter != null) {
                    tripAdapter.getFilter().filter(newText);
                }else if (tripCloudAdapter != null){
                    tripCloudAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });


        return root;
    }

    public void Cloud() {
        if(myAuth.getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(myAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String isAdmin = documentSnapshot.getString("admin");
                                Log.d(TAG, "DocumentSnapshot exists.");
                                if (documentSnapshot.contains("isAdmin")) {
                                    isAdmin = documentSnapshot.getString("admin");
                                    Log.d(TAG, "isAdmin value: " + isAdmin);
                                }

                                if (isAdmin != null && isAdmin.equals("Yes")) {
                                    TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(TripViewModel.class);
                                    viewModel.getSubmittedTripandPrice().observe(getActivity(), trips -> {
                                        Log.d("trip==>", "==>" + trips);
                                        listOfTrips = trips;
                                        viewTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        tripCloudAdapter = new TripCloudAdapter(trips, getActivity());
                                        viewTripList.setAdapter(tripCloudAdapter);
                                        readData(listOfTrips.size());
                                    });
                                } else {
                                    TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(TripViewModel.class);
                                    viewModel.getTripandPrice().observe(getActivity(), trips -> {
                                        Log.d("trip==>", "==>" + trips);
                                        listOfTrips = trips;
                                        viewTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        tripAdapter = new TripAdapter(trips, getActivity());
                                        viewTripList.setAdapter(tripAdapter);
                                        readData(listOfTrips.size());
                                    });
                                }
                            }
                        }
                    });
        }else {
            TripViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(TripViewModel.class);
            viewModel.getTripandPrice().observe(getActivity(), trips -> {
                Log.d("trip==>", "==>" + trips);
                listOfTrips = trips;
                viewTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
                tripAdapter = new TripAdapter(trips, getActivity());
                viewTripList.setAdapter(tripAdapter);
                readData(listOfTrips.size());
            });
        }
    }
    void readData(int count) {
        if (count == 0) {
            noTrip.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);

        } else {
            noTrip.setVisibility(View.GONE);
            nodata.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

}