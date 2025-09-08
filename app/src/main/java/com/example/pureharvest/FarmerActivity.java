package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FarmerActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        fragmentManager = getSupportFragmentManager();

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, new DashboardFragment())
                    .commit();
        }

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new FProfileFragment();
            }else if (item.getItemId() == R.id.nav_add_product) {
                selectedFragment = new AddProductFragment(); // Use AddProductFragment instead of Intent
            }
            else if (item.getItemId() == R.id.nav_orders) {
                selectedFragment = new FarmerOrdersFragment(); // Use AddProductFragment instead of Intent
            }

            // Replace the fragment
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, selectedFragment);

                // Set custom animations
                transaction.setCustomAnimations(
                        R.anim.slide_in,  // Enter animation
                        R.anim.slide_out, // Exit animation
                        R.anim.slide_in,  // Pop enter animation
                        R.anim.slide_out  // Pop exit animation
                );

                // Optionally add the transaction to the back stack for back navigation
                transaction.addToBackStack(null);

                // Commit the transaction to apply the animation
                transaction.commit();
                loadFragment(selectedFragment);

            }
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}

