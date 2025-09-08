package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



public class FProfileFragment extends Fragment {

    private EditText etFarmerName, etFarmerEmail, etFarmDetails;
    private Button btnSaveChanges;

    private FirebaseAuth auth;
    private DatabaseReference userDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fprofile, container, false);

        etFarmerName = view.findViewById(R.id.etFarmerName);
        etFarmerEmail = view.findViewById(R.id.etFarmerEmail);
        etFarmDetails = view.findViewById(R.id.etFarmDetails);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);

        auth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabase.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> userData = (Map<String, Object>) task.getResult().getValue();
                    if (userData != null) {
                        // Populate the EditTexts with the farmer's current information
                        etFarmerName.setText((String) userData.get("name"));
                        etFarmerEmail.setText((String) userData.get("email"));
                        etFarmDetails.setText((String) userData.get("farmDetails"));
                    }
                }
            });
        }

        btnSaveChanges.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void updateProfile() {
        String name = etFarmerName.getText().toString().trim();
        String email = etFarmerEmail.getText().toString().trim();
        String farmDetails = etFarmDetails.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(farmDetails)) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", name);
            updatedData.put("email", email);
            updatedData.put("farmDetails", farmDetails);

            userDatabase.child(userId).updateChildren(updatedData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

