package com.example.travellink.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Auth.ConfirmExitPersonalFragment;
import com.example.travellink.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Menu optionMenu;
    RelativeLayout content;
    ImageView hamburger_bar;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    BottomAppBar userBar, adminBar;
    TextView headerName, headerMail, mode;
    NavController navController;
    NavHostFragment navHostFragment;
    FirebaseAuth myAuth;
    FirebaseFirestore fire_store;
    String user_id = "";
    FloatingActionButton add;
    LottieAnimationView  admin;
    static final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        mode =findViewById(R.id.mode_name);
        userBar = findViewById(R.id.bottom_bar);
        adminBar = findViewById(R.id.bottom_barAdmin);
        initUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        content = findViewById(R.id.content);
        hamburger_bar = findViewById(R.id.menu_bar);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewAdmin);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_admin);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navigationDrawer();
        View headerView = navigationView.getHeaderView(0);
        ImageView app_logo = headerView.findViewById(R.id.app_logo);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exitFragment:
                        Bundle bundle = new Bundle();
                        bundle.putInt("status", 0);
                        ConfirmExitPersonalFragment confirmExitPersonalFragment = new ConfirmExitPersonalFragment();
                        confirmExitPersonalFragment.setArguments(bundle);
                        confirmExitPersonalFragment.show(getSupportFragmentManager(), null);
                        break;
                    case R.id.exitUser:
                        bundle = new Bundle();
                        bundle.putInt("status", 1);
                        confirmExitPersonalFragment = new ConfirmExitPersonalFragment();
                        confirmExitPersonalFragment.setArguments(bundle);
                        confirmExitPersonalFragment.show(getSupportFragmentManager(), null);
                        break;
                }
                return false;
            }
        });


    }


    private void navigationDrawer() {
        navigationView.bringToFront();
        hamburger_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //scale view
                final float diffScaleOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                content.setScaleX(offsetScale);
                content.setScaleY(offsetScale);

                final float xOffSet = drawerView.getWidth() * slideOffset;
                final float xOffSetDiff = content.getWidth() * diffScaleOffset / 2;
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

    private void initUser() {
        if (myAuth.getCurrentUser() != null) {
            navigationView = findViewById(R.id.navigation_view);
            navigationView.getMenu().findItem(R.id.exitFragment).setVisible(false);
            navigationView.getMenu().findItem(R.id.exitUser).setVisible(true);
            navigationView.getMenu().findItem(R.id.viewAccountFragment).setVisible(true);
            headerName = navigationView.getHeaderView(0).findViewById(R.id.header_username);
            admin = navigationView.getHeaderView(0).findViewById(R.id.lottieAnimationView3);
            admin.setVisibility(View.VISIBLE);
            user_id = myAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fire_store.collection("user").document(user_id);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        headerName.setText((documentSnapshot.getString("last_name")) + " " + (documentSnapshot.getString("first_name")));

                    } else {
                        Toast.makeText(AdminActivity.this, "Fail ", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminActivity.this, "Fail ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            return;
        }

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