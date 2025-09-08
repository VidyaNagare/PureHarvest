package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterConsumerActivity extends AppCompatActivity {

    private EditText etConsumerName, etConsumerEmail, etConsumerPassword, etConsumerAddress, etConsumerPhone;
    private Button btnRegisterConsumer;

    private FirebaseAuth auth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_consumer);

        etConsumerName = findViewById(R.id.etConsumerName);
        etConsumerEmail = findViewById(R.id.etConsumerEmail);
        etConsumerPassword = findViewById(R.id.etConsumerPassword);
        etConsumerAddress = findViewById(R.id.etConsumerAddress);
        etConsumerPhone = findViewById(R.id.etConsumerPhone);
        btnRegisterConsumer = findViewById(R.id.btnRegisterConsumer);


        // Set up the back button
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        View backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btnRegisterConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerConsumer();
            }
        });
    }

    private void registerConsumer() {
        String name = etConsumerName.getText().toString().trim();
        String email = etConsumerEmail.getText().toString().trim();
        String password = etConsumerPassword.getText().toString().trim();
        String address = etConsumerAddress.getText().toString().trim();
        String phone = etConsumerPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)) {
            Toast.makeText(RegisterConsumerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    Map<String, String> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("email", email);
                    userData.put("address", address);
                    userData.put("phone", phone);
                    userData.put("userType", "buyer");

                    userDatabase.child(userId).setValue(userData).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(RegisterConsumerActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterConsumerActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterConsumerActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Log the error message for debugging
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(RegisterConsumerActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }
}
