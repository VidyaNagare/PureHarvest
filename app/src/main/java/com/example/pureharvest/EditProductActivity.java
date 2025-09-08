package com.example.pureharvest;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProductActivity extends AppCompatActivity {

    private EditText etName, etDescription, etPrice, etQuantity, etCategory, etImageUrl;
    private Button btnUpdate;
    private Product selectedProduct;
    private DatabaseReference productDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize views
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etCategory = findViewById(R.id.etCategory);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnUpdate = findViewById(R.id.btnUpdate);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });


        // Initialize Firebase database reference
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        // Retrieve the selected product from intent
        selectedProduct = (Product) getIntent().getSerializableExtra("selectedProduct");

        if (selectedProduct != null) {
            // Populate fields with existing product details
            etName.setText(selectedProduct.getName());
            etDescription.setText(selectedProduct.getDescription());
            etPrice.setText(String.valueOf(selectedProduct.getPrice()));
            etQuantity.setText(String.valueOf(selectedProduct.getQuantity()));
            etCategory.setText(selectedProduct.getCategory());
            etImageUrl.setText(selectedProduct.getImageUrl());
        }

        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void updateProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr) ||
                TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(category) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        // Update the product details
        selectedProduct.setName(name);
        selectedProduct.setDescription(description);
        selectedProduct.setPrice(price);
        selectedProduct.setQuantity(quantity);
        selectedProduct.setCategory(category);
        selectedProduct.setImageUrl(imageUrl);

        // Save the updated product to Firebase
        productDatabase.child(selectedProduct.getId()).setValue(selectedProduct)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(EditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
