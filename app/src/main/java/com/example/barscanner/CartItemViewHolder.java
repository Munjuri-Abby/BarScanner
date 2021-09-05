package com.example.barscanner;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemViewHolder extends RecyclerView.ViewHolder {
     TextView productName,productQuantity,productPrice;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.prod_name);
        productQuantity = itemView.findViewById(R.id.prod_quantity);
        productPrice = itemView.findViewById(R.id.prod_price);

    }
}
