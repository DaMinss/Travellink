package com.example.travellink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.travellink.Trip.ConfirmDialogFragment;
import com.example.travellink.Trip.CreateNewTrip;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity{
    DrawerLayout drawerLayout;
    RelativeLayout content;
    ImageView hamburger_bar;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    NavController navController;
    NavHostFragment navHostFragment;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";
    FloatingActionButton add;
    static final float END_SCALE = 0.7f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
//        initUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        content = findViewById(R.id.content);
        hamburger_bar = findViewById(R.id.menu_bar);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navigationDrawer();
        View headerView = navigationView.getHeaderView(0);
        ImageView app_logo = headerView.findViewById(R.id.app_logo);
        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateNewTrip.class));
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exitFragment:
                        new ConfirmExitPersonalFragment().show(getSupportFragmentManager(), null);
                        break;
                }
                return false;
            }});



    }
    private void navigationDrawer(){
           navigationView.bringToFront();
           hamburger_bar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }else {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
               }
           });
           animateNavigationDrawer();
    }
    private void animateNavigationDrawer(){
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //scale view
                final float diffScaleOffset = slideOffset * (1-END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                content.setScaleX(offsetScale);
                content.setScaleY(offsetScale);

                final float xOffSet = drawerView.getWidth() * slideOffset;
                final float xOffSetDiff = content.getWidth() * diffScaleOffset /2;
                final float xTranslation = xOffSet - xOffSetDiff;
                content.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}