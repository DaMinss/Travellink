package com.example.travellink.Auth;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class LoginAdapter extends FragmentStatePagerAdapter {
    private Context context;

    public LoginAdapter(FragmentManager fm, Context context, int behavior) {
        super(fm, behavior);
        this.context = context;
    }


    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new SignUpFragment();
            default:
                return new LoginFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Login";
                break;
            case 1:
                title = "Signup";
        }
        return title;
    }
}
