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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShoppingCart extends AppCompatActivity {
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView totalPrice_Txt;
    private Button buttonPay;
    private int totalToPay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("CartItems");
        recyclerView = findViewById(R.id.cart_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        totalPrice_Txt = findViewById(R.id.total_price);

        buttonPay = findViewById(R.id.buttonPay);
        
        buttonPay.setOnClickListener(v -> makePayment());






    }

    private void makePayment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EWallet").child(Prevalent.currentOnlineUser.getPhone());

        if (totalToPay == 0){
            FancyToast.makeText(getBaseContext(),"There are no items in Cart", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return;
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild("balance")){
                    int balance = Integer.parseInt(snapshot.child("balance").getValue().toString());
                    if (totalToPay > balance){
                        FancyToast.makeText(getBaseContext(),"Insufficient Funds\nBalance is "+balance, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }else {
                        databaseReference.child("balance").setValue(balance-totalToPay).addOnCompleteListener(task -> {
                            FancyToast.makeText(getBaseContext(),"Payment Made Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            HashMap<String ,Object > hashMap = new HashMap<>();
                            hashMap.put("balance", String.valueOf(balance-totalToPay));
                            hashMap.put("date", new SimpleDateFormat("dd-MM-yyyy E 'at' HH:mm:ss", Locale.getDefault()).format(new Date()));
                            hashMap.put("mobile", "Balance: "+balance);
                            hashMap.put("transactionType", "Payment");
                            hashMap.put("amount", totalToPay);
                            databaseReference.child("history").push().setValue(hashMap).addOnCompleteListener(task1 -> {
                                FirebaseDatabase.getInstance().getReference("CartItems").removeValue().addOnCompleteListener(task2 -> finish());
                            });
                        });
                    }
                }else {
                    FancyToast.makeText(getBaseContext(),"Make A E-Wallet Deposit First ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
                        totalToPay = totalPrice;
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