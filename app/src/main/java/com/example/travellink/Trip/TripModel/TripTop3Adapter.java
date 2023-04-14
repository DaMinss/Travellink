package com.example.travellink.Trip.TripModel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.travellink.R;
import com.example.travellink.database.TripDAO;

import java.util.List;

public class TripTop3Adapter extends RecyclerView.Adapter<TripTop3Adapter.TripViewHolder> {
    private String status = "";
    float highest ;
    float secondHighest;
    float thirdHighest;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;

    Context context;
    private ViewBinderHelper viewBinder = new ViewBinderHelper();


    public TripTop3Adapter(List<TripDAO.Trip_withTotalPrice> _list, Context context) {
        listOfTrips = _list;
        this.context = context;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card_home, parent, false);
        return new TripViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripDAO.Trip_withTotalPrice trip_withTotalPrice = listOfTrips.get(position);
        if (trip_withTotalPrice == null) {
            return;
        }


        if (!listOfTrips.isEmpty() && trip_withTotalPrice.getTotalOfExpense() != null) {
            highest = listOfTrips.get(0).getTotalOfExpense();
            if (listOfTrips.size() > 1) {
                secondHighest = listOfTrips.get(1).getTotalOfExpense();
                if (listOfTrips.size() > 2) {
                    thirdHighest = listOfTrips.get(2).getTotalOfExpense();
                }
            }
            if(trip_withTotalPrice.getTotalOfExpense() == highest){
                holder.top1.setVisibility(View.VISIBLE);
            }else {
                holder.top1.setVisibility(View.GONE);
            }
            if(trip_withTotalPrice.getTotalOfExpense() == secondHighest){
                holder.top2.setVisibility(View.VISIBLE);
            }else {
                holder.top2.setVisibility(View.GONE);
            }
            if(trip_withTotalPrice.getTotalOfExpense() == thirdHighest){
                holder.top3.setVisibility(View.VISIBLE);
            }else {
                holder.top3.setVisibility(View.GONE);
            }
        }


        holder.Trip_Name.setText(trip_withTotalPrice.getTrip().getTrip_name());
        holder.Trip_StartDate.setText(trip_withTotalPrice.getTrip().getTrip_start_date());
        holder.Trip_endDate.setText(trip_withTotalPrice.getTrip().getTrip_end_date());
        if (trip_withTotalPrice.getTotalOfExpense() != null) {
            holder.Trip_Price.setText(String.format("%.2f", trip_withTotalPrice.getTotalOfExpense()));
        } else {
            holder.Trip_Price.setText("0.0");
        }
        if (trip_withTotalPrice.getTrip().getTrip_end_date() == null) {
            holder.to.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        if (listOfTrips != null) {
            return listOfTrips.size();
        }
        return 0;
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        public TextView Trip_Name, Trip_Price, Trip_StartDate, Trip_endDate, Trip_Depart, Trip_Arrival, to;
        public SwipeRevealLayout swipe_Layout;
        CardView cardView;
        ImageView top1, top2, top3;
        private LinearLayout card_layout, details_layout;

        public TripViewHolder(@NonNull View item_view) {
            super(item_view);
            Trip_Name = item_view.findViewById(R.id.trip_name_home);
            Trip_Price = item_view.findViewById(R.id.trip_price_home);
            Trip_endDate = item_view.findViewById(R.id.end_date_home);
            Trip_StartDate = item_view.findViewById(R.id.start_date_home);
            cardView = item_view.findViewById(R.id.expense_Id);
            card_layout = item_view.findViewById(R.id.Card_layout);
            to = item_view.findViewById(R.id.to);
            top1 = item_view.findViewById(R.id.top1);
            top2 = item_view.findViewById(R.id.top2);
            top3 = item_view.findViewById(R.id.top3);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }

}

