package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        ImageButton backButton = findViewById(R.id.backButton);
        Button backToConsumerButton = findViewById(R.id.backToConsumerButton);

// Back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Navigates back to the previous activity
            }
        });

// Back to Home button functionality
        backToConsumerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the consumer activity
                Intent intent = new Intent(OrderConfirmationActivity.this, ConsumerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });


        TextView confirmationMessage = findViewById(R.id.confirmationMessage);
        confirmationMessage.setText("Your order has been placed! You will receive it within 1-2 days. Payment will be Cash on Delivery.");
    }
}
