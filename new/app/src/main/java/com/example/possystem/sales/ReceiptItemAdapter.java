package com.example.possystem.sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;

import java.util.ArrayList;
import java.util.List;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemAdapter.ReceiptItemViewHolder> {

    private List<ReceiptItemModel> receiptItems = new ArrayList<>();

    @NonNull
    @Override
    public ReceiptItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt, parent, false);
        return new ReceiptItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptItemViewHolder holder, int position) {
        ReceiptItemModel current = receiptItems.get(position);
        holder.tvItemName.setText(current.getName());
        holder.tvItemPrice.setText(String.format("$%.2f", current.getPrice()));
        holder.tvItemQuantity.setText(String.valueOf(current.getQuantity()));
        holder.tvItemTotal.setText(String.format("$%.2f", current.getTotal()));
    }

    @Override
    public int getItemCount() {
        return receiptItems.size();
    }

    public void setReceiptItems(List<ReceiptItemModel> receiptItems) {
        this.receiptItems = receiptItems;
        notifyDataSetChanged();
    }

    static class ReceiptItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemPrice;
        private final TextView tvItemQuantity;
        private final TextView tvItemTotal;

        public ReceiptItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }
    }
} 