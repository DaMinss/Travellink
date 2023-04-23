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

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Auth.Login;
import com.example.travellink.Auth.Personal;
import com.example.travellink.Expense.ExpenseModel.ExpenseViewModel;
import com.example.travellink.R;
import com.example.travellink.Trip.TripModel.TripViewModel;
import com.example.travellink.database.TravelDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class ConfirmExitPersonalFragment extends DialogFragment {
    Button proceed;
    TextView name;
    FirebaseAuth firebaseAuth;
    LottieAnimationView personal, online;
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
        firebaseAuth = FirebaseAuth.getInstance();
        Bundle bundle = getArguments();
        personal = root.findViewById(R.id.lottieAnimationView2);
        online = root.findViewById(R.id.lottieAnimationView4);
       int status = bundle.getInt("status");
       if(status == 1){
            online.setVisibility(View.VISIBLE);
            personal.setVisibility(View.GONE);
       }
        proceed= root.findViewById(R.id.confirmExit);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() != null){
                    firebaseAuth.signOut();
                    TravelDatabase.getInstance(getActivity()).tripDAO().deleteAll();
                    startActivity(new Intent(getActivity(), SplashScreen.class));
                    getActivity().finish();
                }else {
                    onPersonal();
                    TravelDatabase.getInstance(getActivity()).tripDAO().deleteAll();
                    startActivity(new Intent(getContext(), Login.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                    getActivity().finish();
                }
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