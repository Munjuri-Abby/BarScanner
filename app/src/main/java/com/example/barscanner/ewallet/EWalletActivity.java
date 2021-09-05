package com.example.barscanner.ewallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.barscanner.R;

public class EWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewallet);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new EWalletFragment()).commit();

    }
}