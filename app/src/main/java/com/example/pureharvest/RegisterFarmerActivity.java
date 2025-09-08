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

public class RegisterFarmerActivity extends AppCompatActivity {

    private EditText etFarmerName, etFarmerEmail, etFarmerPassword, etFarmDetails;
    private Button btnRegisterFarmer;


    private FirebaseAuth auth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_farmer);

        etFarmerName = findViewById(R.id.etFarmerName);
        etFarmerEmail = findViewById(R.id.etFarmerEmail);
        etFarmerPassword = findViewById(R.id.etFarmerPassword);
        etFarmDetails = findViewById(R.id.etFarmDetails);
        btnRegisterFarmer = findViewById(R.id.btnRegisterFarmer);


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

        btnRegisterFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFarmer();
            }
        });
    }

    private void registerFarmer() {
        String name = etFarmerName.getText().toString().trim();
        String email = etFarmerEmail.getText().toString().trim();
        String password = etFarmerPassword.getText().toString().trim();
        String farmDetails = etFarmDetails.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(farmDetails)) {
            Toast.makeText(RegisterFarmerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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
                    userData.put("farmDetails", farmDetails);
                    userData.put("userType", "farmer");
                    userData.put("farmerId", userId);

                    userDatabase.child(userId).setValue(userData).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(RegisterFarmerActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterFarmerActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterFarmerActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(RegisterFarmerActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
