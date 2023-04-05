package com.example.travellink.Trip;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.travellink.R;
import com.example.travellink.database.TripDAO;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private String status = "";
    private int trip_id;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;
    Context context;
    private ViewBinderHelper viewBinder = new ViewBinderHelper();


    public TripAdapter(List<TripDAO.Trip_withTotalPrice> _list, Context context) {
        listOfTrips = _list;
        this.context = context;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card, parent, false);
        return new TripViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripDAO.Trip_withTotalPrice trip_withTotalPrice = listOfTrips.get(position);
        if (trip_withTotalPrice == null) {
            return;
        }

//        if (status.equals(trip.getTrip_status())) {
//            holder.types.setVisibility(View.GONE);
//        } else {
//            type = Trip.getTrip_Type();
//            holder.types.setText(Trip.getTrip_Type());
//            holder.types.setVisibility(View.VISIBLE);
//        }
        holder.Trip_Name.setText(trip_withTotalPrice.getTrip().getTrip_name());
        holder.Trip_StartDate.setText(trip_withTotalPrice.getTrip().getTrip_start_date());
        holder.Trip_endDate.setText(trip_withTotalPrice.getTrip().getTrip_end_date());
        if(trip_withTotalPrice.getTotalOfExpense() != null){
        holder.Trip_Price.setText(String.format("%.2f", trip_withTotalPrice.getTotalOfExpense()));}
        else{
            holder.Trip_Price.setText("0.0");
        }
        if(trip_withTotalPrice.getTrip().getTrip_end_date() == null){
            holder.to.setVisibility(View.GONE);
        }
        holder.Trip_Depart.setText(trip_withTotalPrice.getTrip().getTrip_departure());
        holder.Trip_Arrival.setText(trip_withTotalPrice.getTrip().getTrip_arrival());
        viewBinder.setOpenOnlyOne(true);
        viewBinder.bind(holder.swipe_Layout, String.valueOf(trip_withTotalPrice.getTrip().getId()));
        viewBinder.closeLayout(String.valueOf(listOfTrips.get(position).getTrip().getId()));

    }

    @Override
    public int getItemCount() {
        if (listOfTrips != null) {
            return listOfTrips.size();
        }
        return 0;
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        public TextView Trip_Name,Trip_Price, Trip_StartDate,Trip_endDate, Trip_Depart,Trip_Arrival, to;
        public SwipeRevealLayout swipe_Layout;
        CardView cardView;
        private ImageView update_trip, delete_trip, details;
        //        private TextView types;
        private LinearLayout card_layout, details_layout;

        public TripViewHolder(@NonNull View item_view) {
            super(item_view);
            Trip_Name = item_view.findViewById(R.id.trip_name);
            Trip_Price = item_view.findViewById(R.id.trip_price);
            Trip_endDate = item_view.findViewById(R.id.end_date);
            Trip_Depart = item_view.findViewById(R.id.depart);
            Trip_Arrival = item_view.findViewById(R.id.des);
            Trip_StartDate = item_view.findViewById(R.id.start_date);
            swipe_Layout = item_view.findViewById(R.id.swipe_layout);
            update_trip = item_view.findViewById(R.id.updateTrips);
            delete_trip = item_view.findViewById(R.id.deleteTrips);
//            types = item_view.findViewById(R.id.viewType);
            cardView = item_view.findViewById(R.id.trip_Id);
            details_layout = item_view.findViewById(R.id.toggle);
            card_layout = item_view.findViewById(R.id.Card_layout);
            details = item_view.findViewById(R.id.next);
            to = item_view.findViewById(R.id.to);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            card_layout.setLayoutTransition(layoutTransition);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int visible = (details_layout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    TransitionManager.beginDelayedTransition(card_layout, new AutoTransition());
                    details_layout.setVisibility(visible);
                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            update_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            delete_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
    public void saveStates(Bundle outState) {
        viewBinder.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinder.restoreStates(inState);
    }

}
