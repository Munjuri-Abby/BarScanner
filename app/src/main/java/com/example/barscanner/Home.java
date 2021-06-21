package com.example.barscanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
    {
        private DatabaseReference databaseReference;
        private RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        String testitem;
        private String type = "";
        ImageView offers;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        offers = findViewById(R.id.offers);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");


        Paper.init(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(Home.this, ShoppingCart.class);
                    startActivity(intent);
                }
            }
        });
        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(Home.this, ShoppingCart.class);
                    startActivity(intent);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin"))
        {
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        }


        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


        @Override
        protected void onStart()
        {
            super.onStart();

            FirebaseRecyclerOptions<Products> options =
                    new FirebaseRecyclerOptions.Builder<Products>()
                            .setQuery(databaseReference, Products.class)
                            .build();


            FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                        {
                            holder.txtProductName.setText(model.getProductName());
                            holder.txtProductDescription.setText(model.getBarCode());
                            holder.txtProductPrice.setText("Product Price = " + model.getProductPrice() + "$");
                            Picasso.get().load(model.getImage()).into(holder.imageView);


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    if(type.equals("Admin"))
                                    {
                                        Intent intent = new Intent(Home.this, Maintainproducts.class);
                                        intent.putExtra("Bar Code", model.getPID());
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(Home.this, ProductDetails.class);
                                        intent.putExtra("Bar Code", model.getPID());
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @NonNull
                        @Override
                        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                        {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                            ProductViewHolder holder = new ProductViewHolder(view);
                            return holder;
                        }
                    };
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }

        @Override
        public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == R.id.scan)
            {
                Toast.makeText(this, "Scan Item", Toast.LENGTH_SHORT).show();
                scanCode();
            }
            return super.onOptionsItemSelected(item);
        }

        private void scanCode() {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(Capture.class);
            integrator.setOrientationLocked(false);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scanning..");
            integrator.initiateScan();

        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage(result.getContents());
                    builder.setTitle("Scan Result");
                    builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scanCode();
                        }
                    }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        closeContextMenu();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                            else {
                                Toast.makeText(Home.this, "No Result", Toast.LENGTH_SHORT).show();
                            }
            } else {
                super.onActivityResult(requestCode, resultCode, data);

            }
        }
        @Override
        public boolean onNavigationItemSelected(MenuItem item)
        {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_cart)
            {
                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(Home.this, ShoppingCart.class);
                    startActivity(intent);
                }
            }
            else if (id == R.id.nav_search)
            {
                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(Home.this, ProductSearch.class);
                    startActivity(intent);
                }
            }
            else if (id == R.id.nav_categories)
            {

            }
            else if (id == R.id.nav_settings)
            {
                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(Home.this, Menumanage.class);
                    startActivity(intent);
                }
            }
            else if (id == R.id.nav_logout)
            {
                Paper.book().destroy();

                if (!type.equals("Admin"))
                {
                    Paper.book().destroy();

                    Intent intent = new Intent(Home.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


    }