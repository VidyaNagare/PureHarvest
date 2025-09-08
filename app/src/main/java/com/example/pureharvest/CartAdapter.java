package com.example.pureharvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartList;
    private Context context;
    private DatabaseReference cartDatabase;
    private FirebaseAuth auth;

    public CartAdapter(List<Product> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.cartDatabase = FirebaseDatabase.getInstance().getReference("Cart");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("₹" + product.getPrice());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());
        holder.totalPrice.setText("Total: ₹" + (product.getPrice() * product.getQuantity()));

        // Remove from Cart Button Functionality
        holder.removeFromCartButton.setOnClickListener(v -> removeFromCart(product));
    }

    private void removeFromCart(Product product) {
        String userId = auth.getCurrentUser().getUid();
        cartDatabase.child(userId).child(product.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Product removed from cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to remove product from cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice, productQuantity, totalPrice;
        Button removeFromCartButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            removeFromCartButton = itemView.findViewById(R.id.removeFromCartButton);
        }
    }
}
