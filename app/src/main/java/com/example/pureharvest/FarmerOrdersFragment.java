package com.example.pureharvest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class FarmerOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;
    private TextView emptyText;

    private FirebaseAuth auth;
    private DatabaseReference ordersDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_orders, container, false);

        recyclerView = view.findViewById(R.id.ordersRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(orderList, true); // Pass `true` for farmers

        recyclerView.setAdapter(ordersAdapter);

        auth = FirebaseAuth.getInstance();
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");

        loadFarmerOrders();

        return view;
    }

    private void loadFarmerOrders() {
        progressBar.setVisibility(View.VISIBLE);

        String farmerId = auth.getCurrentUser().getUid();
        Query farmerOrdersQuery = ordersDatabase.orderByChild("farmerId").equalTo(farmerId);

        farmerOrdersQuery.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                double totalAmount = 0;  // Initialize totalAmount variable

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);

                    // Ensure order is not null and has necessary details
                    if (order != null) {
                        orderList.add(order);
                        totalAmount += order.getPrice();  // Add the price of the order to totalAmount
                    }
                }

                // Update the total sum TextView
                TextView totalSumText = requireView().findViewById(R.id.totalSumText);
                if (orderList.isEmpty()) {
                    totalSumText.setText("Total Amount: ₹0.00"); // Reset total amount
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    totalSumText.setText("Total Amount: ₹" + totalAmount); // Set calculated total
                    emptyText.setVisibility(View.GONE);
                }


                progressBar.setVisibility(View.GONE);

                if (orderList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }
                Log.d("FarmerOrdersFragment", "Total Amount Calculated: ₹" + totalAmount);
                Log.d("FarmerOrdersFragment", "Order List Size: " + orderList.size());

                ordersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                emptyText.setText("Failed to load orders.");
                emptyText.setVisibility(View.VISIBLE);
            }
        });
    }

}
