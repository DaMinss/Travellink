package com.example.travellink.Trip.TripModel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.travellink.R;
import com.example.travellink.Trip.DeleteTripFragment;
import com.example.travellink.Trip.UpdateTripFragment;
import com.example.travellink.Trip.TripDetails;
import com.example.travellink.database.TripDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> implements Filterable {
    private String status = "";
    private List<TripDAO.Trip_withTotalPrice> listOfTrips;
    private List<TripDAO.Trip_withTotalPrice> listOfTrips1;

    Context context;
    private ViewBinderHelper viewBinder = new ViewBinderHelper();


    public TripAdapter(List<TripDAO.Trip_withTotalPrice> _list, Context context) {
        listOfTrips = _list;
        listOfTrips1 = _list;
        this.context = context;
        notifyDataSetChanged();
    }

    private Context getContext() {
        return context;
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

        if (status.equals(trip_withTotalPrice.getTrip().getTrip_status())) {
            holder.Status.setVisibility(View.GONE);
        } else {
            status = trip_withTotalPrice.getTrip().getTrip_status();
            holder.Status.setText(trip_withTotalPrice.getTrip().getTrip_status());
            holder.Status.setVisibility(View.VISIBLE);
        }
        holder.Trip_Name.setText(trip_withTotalPrice.getTrip().getTrip_name());
        holder.Trip_StartDate.setText(trip_withTotalPrice.getTrip().getTrip_start_date());
        holder.Trip_endDate.setText(trip_withTotalPrice.getTrip().getTrip_end_date());
        if(trip_withTotalPrice.getTotalOfExpense() != null){
        holder.Trip_Price.setText(String.format("%.2f", trip_withTotalPrice.getTotalOfExpense()));}
        else{
            holder.Trip_Price.setText("0.0");
        }
        if(trip_withTotalPrice.getTrip().getTrip_end_date() == null || trip_withTotalPrice.getTrip().getTrip_end_date().equals("")) {
            holder.to.setVisibility(View.GONE);
        }
        holder.Trip_Depart.setText(trip_withTotalPrice.getTrip().getTrip_departure());
        holder.Trip_Arrival.setText(trip_withTotalPrice.getTrip().getTrip_arrival());
        holder.Trip_Status.setText(trip_withTotalPrice.getTrip().getTrip_status());
        if(trip_withTotalPrice.getTrip().getTrip_status().equals("Submitted")){
            holder.Trip_Status.setTextColor(Color.rgb(216, 165, 45 ));
            holder.Trip_Status.setText("Pending");
        }else if (trip_withTotalPrice.getTrip().getTrip_status().equals("Approved")){
            holder.Trip_Status.setTextColor(Color.rgb(57, 148, 4 ));
            holder.Trip_Status.setText("Approve");
        }else if(trip_withTotalPrice.getTrip().getTrip_status().equals("Declined")){
            holder.Trip_Status.setTextColor(Color.RED);
            holder.Trip_Status.setText(trip_withTotalPrice.getTrip().getTrip_status());
        }
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
        public TextView Trip_Name,Trip_Price, Trip_StartDate,Trip_endDate, Trip_Depart,Trip_Arrival, to, Trip_Status, Status;
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
            Trip_Depart = item_view.findViewById(R.id.startDateandTime);
            Trip_Arrival = item_view.findViewById(R.id.endDateandTime);
            Trip_StartDate = item_view.findViewById(R.id.start_date);
            Trip_Status = item_view.findViewById(R.id.status);
            Status = item_view.findViewById(R.id.viewStatus);
            swipe_Layout = item_view.findViewById(R.id.swipe_layout);
            update_trip = item_view.findViewById(R.id.updateExpense);
            delete_trip = item_view.findViewById(R.id.deleteTrips);
//            types = item_view.findViewById(R.id.viewType);
            cardView = item_view.findViewById(R.id.expense_Id);
            details_layout = item_view.findViewById(R.id.toggle);
            card_layout = item_view.findViewById(R.id.Card_layout);
            details = item_view.findViewById(R.id.detail);
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
                    TripDAO.Trip_withTotalPrice trip =  listOfTrips.get(getAdapterPosition());
                    Intent intent = new Intent(view.getContext(), TripDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("trip_id", trip.getTrip().getId());
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                   ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                }
            });

            update_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TripDAO.Trip_withTotalPrice trip =  listOfTrips.get(getAdapterPosition());
                    FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putInt("trip_id",trip.getTrip().getId());
                    UpdateTripFragment updateTripFragment = new UpdateTripFragment();
                    updateTripFragment.setArguments(bundle);
                    updateTripFragment.show(fragmentActivity.getSupportFragmentManager(),null);
                }
            });
            delete_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TripDAO.Trip_withTotalPrice trip =  listOfTrips.get(getAdapterPosition());
                    FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putInt("trip_id",trip.getTrip().getId());
                    bundle.putInt("status",1);
                    DeleteTripFragment deleteTripFragment = new DeleteTripFragment();
                    deleteTripFragment.setArguments(bundle);
                    deleteTripFragment.show(fragmentActivity.getSupportFragmentManager(),null);
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
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String Search = charSequence.toString();
                if (Search.isEmpty()) {
                    listOfTrips = listOfTrips1;

                } else {
                    List<TripDAO.Trip_withTotalPrice> listsss = new ArrayList<>();
                    for (TripDAO.Trip_withTotalPrice tripsss : listOfTrips1) {
                        if (tripsss.getTrip().getTrip_name().toLowerCase(Locale.ROOT).contains(Search.toLowerCase()) ||
                                tripsss.getTrip().getTrip_start_date().toLowerCase(Locale.ROOT).contains((Search.toLowerCase())) ||
                                tripsss.getTrip().getTrip_arrival().toLowerCase(Locale.ROOT).contains((Search.toLowerCase()))) {
                            listsss.add(tripsss);
                        }
                    }
                    listOfTrips = listsss;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listOfTrips;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listOfTrips = ( List<TripDAO.Trip_withTotalPrice>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }


}
