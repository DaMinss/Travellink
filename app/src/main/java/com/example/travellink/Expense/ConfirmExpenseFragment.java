package com.example.travellink.Expense;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.travellink.Expense.ExpenseModel.Expense;
import com.example.travellink.R;
import com.example.travellink.Trip.TripDetails;
import com.example.travellink.database.TravelDatabase;


public class ConfirmExpenseFragment extends DialogFragment {


    Button Return, Confirm;
    protected Expense expense;
    protected int trip_id;

    public ConfirmExpenseFragment(Expense expense_, int myTripId) {
        expense = expense_;
        trip_id = myTripId;
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_confirm_expense, container, false);
        Return = root.findViewById(R.id.Return);
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Confirm = root.findViewById(R.id.confirm);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long status = TravelDatabase.getInstance(getActivity()).expenseDAO().insertExpense(expense);
                if (status > 0) {
                    Toast.makeText(getActivity(), "Your expense has been added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), TripDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("trip_id", trip_id);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}