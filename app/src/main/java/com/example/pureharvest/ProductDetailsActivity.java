package com.example.pureharvest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productName, productPrice, productDescription;
    private ImageView productImage;
    private Button addToCartButton;
    private FirebaseAuth auth;
    private DatabaseReference cartDatabase;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productImage = findViewById(R.id.productImage);
        addToCartButton = findViewById(R.id.addToCartButton);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });


        auth = FirebaseAuth.getInstance();
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");

        // Retrieve selected product from intent
        selectedProduct = (Product) getIntent().getSerializableExtra("selectedProduct");

        if (selectedProduct != null) {
            productName.setText(selectedProduct.getName());
            productPrice.setText("₹" + selectedProduct.getPrice());
            productDescription.setText(selectedProduct.getDescription());
            // Load product image using Glide
            if (selectedProduct.getImageUrl() != null && !selectedProduct.getImageUrl().isEmpty()) {
                Glide.with(this).load(selectedProduct.getImageUrl()).into(productImage);
            }
        }

        addToCartButton.setOnClickListener(v -> showQuantityDialog());
    }

    private void showQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Quantity(in kg)");

        // Input field for quantity
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String quantityText = input.getText().toString();
            if (!quantityText.isEmpty()) {
                int quantity = Integer.parseInt(quantityText);
                if (quantity > 0 && quantity <= selectedProduct.getQuantity()) {
                    selectedProduct.setQuantity(quantity);
                    addToCart(selectedProduct);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Invalid quantity. Please enter a value within available stock.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProductDetailsActivity.this, "Please enter a quantity.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addToCart(Product product) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userCart = cartDatabase.child(userId).child(product.getId());

        userCart.setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProductDetailsActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductDetailsActivity.this, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
