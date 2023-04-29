package com.example.travellink;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travellink.Auth.ConfirmExitPersonalFragment;
import com.example.travellink.Trip.CreateNewTrip;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
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
    LottieAnimationView personal, online, online1, personal1;
    static final float END_SCALE = 0.7f;
    CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myAuth = FirebaseAuth.getInstance();
        fire_store = FirebaseFirestore.getInstance();
        personal = findViewById(R.id.lottieAnimationView);
        online = findViewById(R.id.lottieAnimationView1);
        mode =findViewById(R.id.mode_name);
        userBar = findViewById(R.id.bottom_bar);
        adminBar = findViewById(R.id.bottom_barAdmin);
        drawerLayout = findViewById(R.id.drawer_layout);
        content = findViewById(R.id.content);
        hamburger_bar = findViewById(R.id.menu_bar);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navigationView.setNavigationItemSelectedListener(
                item -> NavigationUI.onNavDestinationSelected(item, navController)
        );
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
//        navigationView.getMenu().getItem()
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
                    case R.id.viewAccountFragment:
                        navController.navigate(R.id.viewAccountFragment);
                        navigationView.setCheckedItem(R.id.viewAccountFragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.homeFragment:
                        navController.navigate(R.id.homeFragment);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });
        initUser();

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
            online.setVisibility(View.VISIBLE);
            mode.setText("Online Mode");
            personal = navigationView.getHeaderView(0).findViewById(R.id.lottieAnimationView);
            headerName = navigationView.getHeaderView(0).findViewById(R.id.header_username);
            online1 = navigationView.getHeaderView(0).findViewById(R.id.lottieAnimationView1);
            online1.setVisibility(View.VISIBLE);
            user_id = myAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fire_store.collection("user").document(user_id);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        headerName.setText((documentSnapshot.getString("first_name")) + " " + (documentSnapshot.getString("last_name")));

                    } else {
                        if(myAuth.getCurrentUser().getDisplayName() != null){
                            headerName.setText(myAuth.getCurrentUser().getDisplayName());
                        }else {
                            Toast.makeText(MainActivity.this, "Fail to get user information ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(myAuth.getCurrentUser().getDisplayName() != null){
                        headerName.setText(myAuth.getCurrentUser().getDisplayName());
                    }else {
                        Toast.makeText(MainActivity.this, "Fail ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if(myAuth.getCurrentUser().getPhotoUrl() != null){
                profile= navigationView.getHeaderView(0).findViewById(R.id.img_profile);
                Picasso.get().load(myAuth.getCurrentUser().getPhotoUrl()).into(profile);
                online1.setVisibility(View.GONE);
                personal.setVisibility(View.GONE);
            }
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