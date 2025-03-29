package com.example.possystem.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.possystem.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<TransactionModel> transactions = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel current = transactions.get(position);
        holder.tvTransactionId.setText(current.getReceiptNumber());
        holder.tvTransactionDate.setText(dateFormat.format(current.getDate()));
        holder.tvCashier.setText("Cashier: " + current.getCashierName());
        holder.tvTransactionAmount.setText(String.format("$%.2f", current.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<TransactionModel> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTransactionId;
        private final TextView tvTransactionDate;
        private final TextView tvCashier;
        private final TextView tvTransactionAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvCashier = itemView.findViewById(R.id.tvCashier);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
} 