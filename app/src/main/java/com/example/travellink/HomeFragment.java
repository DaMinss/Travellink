package com.example.travellink;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Trip.TripModel.TripHomeAdapter;
import com.example.travellink.Trip.TripModel.TripTop3Adapter;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TripDAO;
import com.example.travellink.database.TripRepo;

import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView viewTripList, top3List;
    TripHomeAdapter tripAdapter;
    TripTop3Adapter tripTop3Adapter;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips, listOfTop3Trips;
    TextView title1, title2;
    LottieAnimationView lottieAnimationView1, lottieAnimationView2, nodata;
    LinearLayout noTrip;
    ConstraintLayout content;
    TripRepo tripRepo;
    float v = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        nodata = root.findViewById(R.id.lottieAnimationViewNodata);
        noTrip = root.findViewById(R.id.notrip);
        content = root.findViewById(R.id.home_layout);
        viewTripList = (RecyclerView) root.findViewById(R.id.recent_trip);
        top3List = (RecyclerView) root.findViewById(R.id.top_trip);
        tripRepo = new TripRepo(getActivity().getApplication());
        listOfTrips = tripRepo.getRecentTripAndSum();
        viewTripList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        tripAdapter = new TripHomeAdapter(listOfTrips, getActivity());
        viewTripList.setAdapter(tripAdapter);

        listOfTop3Trips = tripRepo.getTop3TripAndSum();
        if(!listOfTop3Trips.isEmpty()) {
//            if (listOfTop3Trips.get(0).getTotalOfExpense() != null && listOfTop3Trips.get(1).getTotalOfExpense() != null && listOfTop3Trips.get(2).getTotalOfExpense() != null) {
                top3List.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                tripTop3Adapter = new TripTop3Adapter(listOfTop3Trips, getActivity());
                top3List.setAdapter(tripTop3Adapter);
//            }

        }
        readData(listOfTrips.size());

        title1 = root.findViewById(R.id.recent);
        title2 = root.findViewById(R.id.top_spent);
        lottieAnimationView1 = root.findViewById(R.id.lottieAnimationView4);
        lottieAnimationView2 = root.findViewById(R.id.lottieAnimationView5);

        title1.setTranslationY(300);
        title1.setAlpha(v);
        title1.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        title2.setTranslationY(300);
        title2.setAlpha(v);
        title2.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        lottieAnimationView1.setTranslationY(300);
        lottieAnimationView1.setAlpha(v);
        lottieAnimationView1.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        lottieAnimationView2.setTranslationY(300);
        lottieAnimationView2.setAlpha(v);
        lottieAnimationView2.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        viewTripList.setTranslationX(800);
        viewTripList.setAlpha(v);
        viewTripList.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        top3List.setTranslationX(800);
        top3List.setAlpha(v);
        top3List.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        return root;
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