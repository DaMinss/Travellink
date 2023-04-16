package com.example.travellink;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;


public class SettingFragment extends Fragment {

 Switch night;
 TextView mode;
    private static final String MODE_PREF = "mode_pref";
    private static final String MODE_KEY = "mode_key";
    private int currentMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

       night = root.findViewById(R.id.nightmode);
       mode = root.findViewById(R.id.textView3);
       night.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               if(isChecked){
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                   mode.setText("Night Mode");
               }else {
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                   mode.setText("Light Mode");
               }
           }
       });
       boolean isNightModeOn =  AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
       night.setChecked(isNightModeOn);
       if(isNightModeOn){
           mode.setText("Night Mode");
       }else{
           mode.setText("Light Mode");
       }
       return root;
    }


}