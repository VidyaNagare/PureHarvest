package com.example.pureharvest;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;


public class AddProductFragment extends Fragment {

    private EditText etName, etDescription, etPrice, etImageUrl, etQuantity, etCategory;
    private Button btnSave;

    private DatabaseReference productDatabase;
    private FirebaseAuth auth;  // Declare FirebaseAuth instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etPrice = view.findViewById(R.id.etPrice);
        etImageUrl = view.findViewById(R.id.etImageUrl);
        etQuantity = view.findViewById(R.id.etQuantity);
        etCategory = view.findViewById(R.id.etCategory);
        btnSave = view.findViewById(R.id.btnSave);

        // Initialize Firebase references
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");
        auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth instance

        btnSave.setOnClickListener(v -> addItem());

        return view;
    }

    private void addItem() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr) ||
                TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Get the farmer's ID from the authenticated user
        String farmerId = auth.getCurrentUser().getUid();  // Now auth is properly initialized

        // Generate a unique product ID for the new product
        String productId = productDatabase.push().getKey();

        // Create a new product with the additional details
        Product product = new Product(productId, name, description, price, imageUrl, quantity, category, farmerId);

        // Save the product to Firebase
        if (productId != null) {
            productDatabase.child(productId).setValue(product)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Failed to generate product ID", Toast.LENGTH_SHORT).show();
        }
    }
}
