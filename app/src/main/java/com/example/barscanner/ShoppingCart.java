package com.example.barscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShoppingCart extends AppCompatActivity {
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView totalPrice_Txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("CartItems");
        recyclerView = findViewById(R.id.cart_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        totalPrice_Txt = findViewById(R.id.total_price);








    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(databaseReference, CartItem.class)
                        .build();



        FirebaseRecyclerAdapter<CartItem, CartItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartItem, CartItemViewHolder>(options) {
                    int totalPrice = 0;


                    @Override
                    protected void onBindViewHolder(@NonNull CartItemViewHolder holder, int position, @NonNull final CartItem model) {
                        holder.productName.setText(model.getProductName());
                        holder.productQuantity.setText(model.getQuantity());
                        holder.productPrice.setText(model.getProductPrice()+".00");
                        int prod_price = Integer.parseInt(model.getProductPrice());
                        int qantity = Integer.parseInt(model.getQuantity());
                        int total_product = prod_price * qantity;
                        totalPrice += total_product;
                        Log.d("totals",String.valueOf(totalPrice));
                        totalPrice_Txt.setText(String.valueOf(totalPrice)+".00");



                    }



                    @NonNull
                    @Override
                    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_view, parent, false);
                        CartItemViewHolder holder = new CartItemViewHolder(view);
                        return holder;
                    }

                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}