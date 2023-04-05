package com.example.travellink;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.travellink.Trip.Trip;
import com.example.travellink.database.TravelDatabase;


public class ConfirmDialogFragment extends DialogFragment {
    Button Return, Confirm;
    protected Trip trip;
    public ConfirmDialogFragment(Trip trip_) {
        trip = trip_;
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
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
                long status = TravelDatabase.getInstance(getActivity()).tripDAO().insertTrip(trip);
                if(status > 0){
                    Toast.makeText(getActivity(), "Your trip has been added successfully", Toast.LENGTH_SHORT);
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                }else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT);
                }
            }
        });
        return root;
    }
}