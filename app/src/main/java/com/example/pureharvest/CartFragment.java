package com.example.pureharvest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartList;
    private ProgressBar progressBar;
    private TextView emptyText, totalText;
    private Button checkoutButton;
    private FirebaseAuth auth;
    private DatabaseReference cartDatabase, ordersDatabase;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        totalText = view.findViewById(R.id.totalText);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, getContext());
        recyclerView.setAdapter(cartAdapter);

        auth = FirebaseAuth.getInstance();
        cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        loadCartItems();

        checkoutButton.setOnClickListener(v -> showConfirmationDialog());

        return view;
    }

    private void loadCartItems() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = auth.getCurrentUser().getUid();
        cartDatabase.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                cartList.clear();
                int totalValue = 0;

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        cartList.add(product);
                        totalValue += product.getPrice() * product.getQuantity(); // Assuming price is an integer
                    }
                }

                progressBar.setVisibility(View.GONE);
                totalText.setText("Total: ₹" + totalValue);

                if (cartList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }

                cartAdapter.notifyDataSetChanged();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Checkout")
                .setMessage("Are you sure you want to place this order?")
                .setPositiveButton("Yes", (dialog, which) -> placeOrder())
                .setNegativeButton("No", null)
                .show();
    }

    private void placeOrder() {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Fetch user details
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String customerName = snapshot.child("name").getValue(String.class);
                String customerAddress = snapshot.child("address").getValue(String.class);

                if (customerName == null || customerAddress == null) {
                    Toast.makeText(getContext(), "Customer details not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Product product : cartList) {
                    String orderId = ordersDatabase.push().getKey(); // Generate unique ID for the order

                    if (orderId != null) {
                        Map<String, Object> orderDetails = new HashMap<>();
                        orderDetails.put("orderId", orderId);
                        orderDetails.put("buyerId", userId);
                        orderDetails.put("farmerId", product.getFarmerId());
                        orderDetails.put("price", product.getPrice() * product.getQuantity());
                        orderDetails.put("productName", product.getName());
                        orderDetails.put("quantity", product.getQuantity());
                        orderDetails.put("status", "Pending");

                        // Include customer's name and address in the order
                        orderDetails.put("customerName", customerName);
                        orderDetails.put("customerAddress", customerAddress);

                        // Save the order details
                        ordersDatabase.child(orderId).setValue(orderDetails);

                        // Update the quantity of the product in the database
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products").child(product.getId());

                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Product existingProduct = snapshot.getValue(Product.class);
                                    if (existingProduct != null) {
                                        int currentQuantity = existingProduct.getQuantity();
                                        int orderedQuantity = product.getQuantity();
                                        int updatedQuantity = currentQuantity - orderedQuantity;

                                        // Update the product's quantity only if there's enough stock
                                        if (updatedQuantity > 0) {
                                            productRef.child("quantity").setValue(updatedQuantity)
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (!updateTask.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Failed to update product quantity.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else if (updatedQuantity == 0) {
                                            // Remove the product from the database if quantity is zero
                                            productRef.removeValue()
                                                    .addOnCompleteListener(removeTask -> {
                                                        if (!removeTask.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Failed to remove product.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getContext(), "Insufficient stock for product: " + product.getName(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to fetch product details for update.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                // Clear the cart after placing the order
                cartDatabase.child(userId).removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                redirectToConfirmationPage();
                            } else {
                                Toast.makeText(getContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch customer details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToConfirmationPage() {
        Intent intent = new Intent(getContext(), OrderConfirmationActivity.class);
        startActivity(intent);
    }
}
