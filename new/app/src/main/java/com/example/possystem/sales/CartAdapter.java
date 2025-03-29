package com.example.possystem.sales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private final LayoutInflater inflater;
    private final CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, boolean isIncreased);
    }

    public CartAdapter(Context context, CartItemListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        if (cartItems != null) {
            CartItem current = cartItems.get(position);
            holder.tvProductName.setText(current.getProduct().getName());
            holder.tvPrice.setText(String.format("$%.2f each", current.getProduct().getPrice()));
            holder.tvQuantity.setText(String.valueOf(current.getQuantity()));
            holder.tvSubtotal.setText(String.format("$%.2f", current.getSubtotal()));

            holder.btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(position, true);
                }
            });

            holder.btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(position, false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public CartItem getCartItemAt(int position) {
        return cartItems.get(position);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;
        private final ImageButton btnIncrease;
        private final ImageButton btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
} 