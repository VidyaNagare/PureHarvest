package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        View backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });

        Button btnConsumer = findViewById(R.id.btnConsumer);
        Button btnFarmer = findViewById(R.id.btnFarmer);

        btnConsumer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegisterConsumerActivity.class);
            startActivity(intent);
        });

        btnFarmer.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegisterFarmerActivity.class);
            startActivity(intent);
        });
    }
}
