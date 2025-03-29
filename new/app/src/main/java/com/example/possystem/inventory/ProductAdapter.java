package com.example.possystem.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;
import com.example.possystem.database.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final LayoutInflater inflater;
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (products != null) {
            Product current = products.get(position);
            holder.tvProductName.setText(current.getName());
            holder.tvPrice.setText(String.format("%.2f", current.getPrice()));
            holder.tvStock.setText(String.valueOf(current.getStockQuantity()));
            holder.tvBarcode.setText("Barcode: " + current.getBarcode());
            holder.tvCategory.setText("Category: " + current.getCategory());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(current);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName;
        private final TextView tvPrice;
        private final TextView tvStock;
        private final TextView tvBarcode;
        private final TextView tvCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
} 