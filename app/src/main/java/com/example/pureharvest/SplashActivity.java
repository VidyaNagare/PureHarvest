package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Array of dynamic quotes
        String[] quotes = {
                "Fresh produce at your doorstep!",
                "Farm-to-table made easy.",
                "Healthy living starts here.",
                "Eat fresh, stay healthy!",
                "From our farm to your home!"
        };

        // Select a random quote
        int randomIndex = new Random().nextInt(quotes.length);

        // Set the random quote to the TextView
        TextView splashQuote = findViewById(R.id.splashQuote);
        splashQuote.setText(quotes[randomIndex]);

        // Delay for 2 seconds and then navigate to the LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class); // Navigate to Login
            startActivity(intent);
            finish();
        }, 3000); // 2-second delay
    }
}

