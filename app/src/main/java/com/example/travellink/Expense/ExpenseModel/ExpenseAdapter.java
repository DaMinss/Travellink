package com.example.travellink.Expense.ExpenseModel;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.travellink.R;
import com.example.travellink.Trip.DeleteTripFragment;
import com.example.travellink.Trip.TripDetails;
import com.example.travellink.database.ExpenseDAO;
import com.example.travellink.database.TripDAO;
import com.example.travellink.fragment_firstscreen;
import com.example.travellink.fragment_secondscreen;
import com.example.travellink.fragment_thirdscreen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private String date = "";
    private List<Expense> listOfExpense;
    private int lastClickedPosition = -1;

    Context context;
    private ViewBinderHelper viewBinder = new ViewBinderHelper();


    public ExpenseAdapter(List<Expense> _list, Context context) {
        super();
        listOfExpense = _list;
        this.context = context;
        notifyDataSetChanged();
    }

    private Context getContext() {
        return context;
    }


    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_card, parent, false);
        return new ExpenseViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = listOfExpense.get(position);
        if (expense == null) {
            return;
        }

        String datetimeString = expense.getExpense_StartDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date currentDate = Calendar.getInstance().getTime();

        try {
            date1 = dateFormat.parse(datetimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

// Format the date as a string
        String dateString = dateFormat.format(date1);
        String current_dateString = dateFormat.format(currentDate);

        if (date.equals(dateString)) {
            holder.DateView.setVisibility(View.GONE);
        } else {
            date = dateString;
            holder.DateView.setText(dateString);
            holder.DateView.setVisibility(View.VISIBLE);
        }
        if(dateString.equals(current_dateString)){
            holder.DateView.setText("Today");
        }
        if(expense.getExpense_Type().equals("Food")) {
                holder.food.setVisibility(View.VISIBLE);
                holder.food1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Taxi")) {
            holder.taxi.setVisibility(View.VISIBLE);
            holder.taxi1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Flight")) {
            holder.plane.setVisibility(View.VISIBLE);
            holder.plane1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Shopping")) {
            holder.shopping.setVisibility(View.VISIBLE);
            holder.shopping1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Hotel")) {
            holder.hotel.setVisibility(View.VISIBLE);
            holder.hotel1.setVisibility(View.VISIBLE);
        }
        if(expense.getExpense_Type().equals("Others")) {
            holder.other.setVisibility(View.VISIBLE);
            holder.other1.setVisibility(View.VISIBLE);
        }

        holder.Expense_Name.setText(expense.getExpense_Name());
        holder.Expense_StartDate.setText(expense.getExpense_StartDate());
        holder.Expense_endDate.setText(expense.getExpense_EndDate());
        holder.Expense_Price.setText(expense.getExpense_Price());
        holder.Expense_Depart.setText(expense.getExpense_Location_Departure());
        holder.Expense_Arrival.setText(expense.getExpense_Location_Arrival());
        viewBinder.setOpenOnlyOne(true);
        viewBinder.bind(holder.swipe_Layout, String.valueOf(expense.getExpense_Id()));
        viewBinder.closeLayout(String.valueOf(listOfExpense.get(position).getExpense_Id()));



    }

    @Override
    public int getItemCount() {
        if (listOfExpense != null) {
            return listOfExpense.size();
        }
        return 0;
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public TextView Expense_Name, Expense_Price, Expense_StartDate, Expense_endDate, Expense_Depart, Expense_Arrival, DateView;
        public SwipeRevealLayout swipe_Layout;
        CardView cardView;
        public ImageView delete_expense, details, hotel, hotel1, shopping, shopping1, taxi, taxi1, plane, plane1, other, other1, food, food1;
        private ConstraintLayout category;
        //        private TextView types;
        private LinearLayout card_layout, details_layout;

        public ExpenseViewHolder(@NonNull View item_view) {
            super(item_view);
            DateView = item_view.findViewById(R.id.viewDate);
            hotel = item_view.findViewById(R.id.bedroom);
            hotel1 = item_view.findViewById(R.id.bedroom1);
            food = item_view.findViewById(R.id.food);
            food1 = item_view.findViewById(R.id.food1);
            plane = item_view.findViewById(R.id.plane);
            plane1 = item_view.findViewById(R.id.plane1);
            shopping = item_view.findViewById(R.id.shopping);
            shopping1 = item_view.findViewById(R.id.shopping1);
            taxi = item_view.findViewById(R.id.taxi);
            taxi1 = item_view.findViewById(R.id.taxi1);
            other = item_view.findViewById(R.id.other);
            other1 = item_view.findViewById(R.id.other1);
            Expense_Name = item_view.findViewById(R.id.expense_name);
            Expense_Price = item_view.findViewById(R.id.expense_price);
            Expense_endDate = item_view.findViewById(R.id.endDateandTime);
            Expense_StartDate = item_view.findViewById(R.id.startDateandTime);
            Expense_Depart = item_view.findViewById(R.id.expense_des);
            Expense_Arrival = item_view.findViewById(R.id.expense_des1);
            swipe_Layout = item_view.findViewById(R.id.swipe_layout);
            delete_expense = item_view.findViewById(R.id.deleteExpense);
            cardView = item_view.findViewById(R.id.expense_Id);
            details_layout = item_view.findViewById(R.id.toggle);
            card_layout = item_view.findViewById(R.id.Card_layout);
            details = item_view.findViewById(R.id.detail);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            card_layout.setLayoutTransition(layoutTransition);
            category = item_view.findViewById(R.id.category);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int visible = (details_layout.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                    int gone = (category.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                    TransitionManager.beginDelayedTransition(card_layout, new AutoTransition());
                    details_layout.setVisibility(visible);
                    category.setVisibility(gone);
                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    TripDAO.Trip_withTotalPrice trip =  listOfTrips.get(getAdapterPosition());
//                    Intent intent = new Intent(view.getContext(), TripDetails.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("trip_id", trip.getTrip().getId());
//                    intent.putExtras(bundle);
//                    view.getContext().startActivity(intent);
//                    ((Activity) getContext()).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                }
            });


            delete_expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Expense expense=  listOfExpense.get(getAdapterPosition());
                    FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
                    Bundle bundle = new Bundle();
                    bundle.putInt("expense_id", expense.getExpense_Id());
                    bundle.putInt("status",2);
                    DeleteTripFragment deleteTripFragment = new DeleteTripFragment();
                    deleteTripFragment.setArguments(bundle);
                    deleteTripFragment.show(fragmentActivity.getSupportFragmentManager(),null);
                }
            });
        }
    }
}
