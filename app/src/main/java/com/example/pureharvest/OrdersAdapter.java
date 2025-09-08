package com.example.pureharvest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private boolean isFarmer; // To differentiate views
    private DatabaseReference ordersDatabase;

    public OrdersAdapter(List<Order> orderList, boolean isFarmer) {
        this.orderList = orderList;
        this.isFarmer = isFarmer; // Initialize the flag
        ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.productName.setText(order.getProductName() != null ? order.getProductName() : "Unknown");
        holder.quantity.setText("Quantity: " + order.getQuantity());
        holder.price.setText("Price: ₹" + order.getPrice());
        holder.status.setText("Status: " + order.getStatus());

        if (isFarmer) {
            // Make name and address visible for farmers
            holder.customerName.setVisibility(View.VISIBLE);
            holder.customerAddress.setVisibility(View.VISIBLE);

            holder.customerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "No Name");
            holder.customerAddress.setText(order.getCustomerAddress() != null ? order.getCustomerAddress() : "No Address");

            // Enable spinner for farmers
            holder.statusSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_spinner_item, new String[]{"Pending", "Shipped", "Delivered"});
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.statusSpinner.setAdapter(statusAdapter);

            int statusPosition = statusAdapter.getPosition(order.getStatus());
            holder.statusSpinner.setSelection(statusPosition);

            holder.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String newStatus = parent.getItemAtPosition(position).toString();
                    if (!newStatus.equals(order.getStatus())) {
                        ordersDatabase.child(order.getOrderId()).child("status").setValue(newStatus);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        } else {
            // Hide name, address, and spinner for consumers
            holder.customerName.setVisibility(View.GONE);
            holder.customerAddress.setVisibility(View.GONE);
            holder.statusSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, quantity, price, status, customerName, customerAddress;
        Spinner statusSpinner;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.orderProductName);
            quantity = itemView.findViewById(R.id.orderQuantity);
            price = itemView.findViewById(R.id.orderPrice);
            status = itemView.findViewById(R.id.orderStatus);
            customerName = itemView.findViewById(R.id.orderCustomerName);
            customerAddress = itemView.findViewById(R.id.orderCustomerAddress);
            statusSpinner = itemView.findViewById(R.id.statusSpinner);
        }
    }

}
