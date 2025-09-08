package com.example.pureharvest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference productDatabase;
    private ValueEventListener productListener;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "RecyclerView initialized");

        // Initialize product list and adapter
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Open the product details activity for editing or updating
                Intent intent = new Intent(getContext(), EditProductActivity.class);
                intent.putExtra("selectedProduct", product);  // Send the selected product to the activity
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set to RecyclerView");

        // Initialize FirebaseAuth and database reference
        auth = FirebaseAuth.getInstance();
        productDatabase = FirebaseDatabase.getInstance().getReference("Products");

        loadFarmerProducts(); // Load only the products uploaded by the logged-in farmer

        return view;
    }

    private void loadFarmerProducts() {
        Log.d(TAG, "Loading products from Firebase for the logged-in farmer");

        // Fetch products uploaded by the logged-in farmer
        String farmerId = auth.getCurrentUser().getUid();
        Log.d(TAG, "FarmerId used in query: " + farmerId);
        // Get the current logged-in farmer's ID
        // Now use a Query to filter products based on the farmerId
        Query farmerProductDatabase = productDatabase.orderByChild("farmerId").equalTo(farmerId);

        productListener = farmerProductDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Data fetched from Firebase");
                productList.clear(); // Clear previous data
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    } else {
                        Log.w(TAG, "Null product encountered");
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter about the data changes
                Log.d(TAG, "Adapter notified of data changes. Items: " + productList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the Firebase listener to avoid memory leaks and unnecessary data loading
        if (productListener != null) {
            productDatabase.removeEventListener(productListener);
        }
    }
}
