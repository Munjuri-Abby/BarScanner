package com.example.barscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminDashboard extends AppCompatActivity {

    private ImageView tShirts, sportsShirts, shoes, sweaters;
    private ImageView glasses, hatsCaps, Purse_bags, stationery ;
    private ImageView headPhones, Laptops, watches, smart_phones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tShirts = (ImageView) findViewById(R.id.t_shirts);
        sportsShirts = (ImageView) findViewById(R.id.sports_t_shirts);
        shoes = (ImageView) findViewById(R.id.shoes);
        sweaters = (ImageView) findViewById(R.id.sweaters);

        glasses = (ImageView) findViewById(R.id.glasses);
        hatsCaps = (ImageView) findViewById(R.id.hats_caps);
        Purse_bags = (ImageView) findViewById(R.id.purses_bags);
        stationery = (ImageView) findViewById(R.id.stationery);

        headPhones = (ImageView) findViewById(R.id.headphones);
        Laptops = (ImageView) findViewById(R.id.laptops);
        watches = (ImageView) findViewById(R.id.watches);
        smart_phones = (ImageView) findViewById(R.id.smart_phones);


        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "tShirts");
                startActivity(intent);
            }
        });


        sportsShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Sports tShirts");
                startActivity(intent);
            }
        });


        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Shoes");
                startActivity(intent);
            }
        });

        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Sweaters");
                startActivity(intent);
            }
        });


        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Glasses");
                startActivity(intent);
            }
        });


        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Hats Caps");
                startActivity(intent);
            }
        });


        Purse_bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Purse Bags");
                startActivity(intent);
            }
        });


        stationery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Stationery");
                startActivity(intent);
            }
        });
        headPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "HeadPhones");
                startActivity(intent);
            }
        });


        Laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Laptops");
                startActivity(intent);
            }
        });


        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "Watches");
                startActivity(intent);
            }
        });


        smart_phones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, AdminAddItem.class);
                intent.putExtra("category", "SmartPhones");
                startActivity(intent);
            }
        });
    }}

