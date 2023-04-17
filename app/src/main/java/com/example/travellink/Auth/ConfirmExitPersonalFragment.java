package com.example.travellink.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travellink.Auth.Login;
import com.example.travellink.Auth.Personal;
import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TravelDatabase;

public class ConfirmExitPersonalFragment extends DialogFragment {
    Button proceed;
    TextView name;

    public ConfirmExitPersonalFragment() {

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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_confirm_exit_personal, container, false);
        Bundle bundle = getArguments();
//        int status = bundle.getInt("status");
        proceed= root.findViewById(R.id.confirmExit);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPersonal();
                startActivity(new Intent(getContext(), Login.class));
               getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                getActivity().finish();
            }
        });


        return root;
    }
    private void onPersonal() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PersonalScreen", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Finished", false);
        editor.apply();
    }
}