package com.example.barscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddItem extends AppCompatActivity {
    private String ProductCategory, Barcode, ProductPrice, ProductName, saveCurrentDate, saveCurrentTime;
    private EditText itemname, itemprice;
    private TextView itembarcode;
    private FirebaseAuth firebaseAuth;
    public static TextView resulttextview;
    ImageView productImage;
    Button scanbutton, additemtodatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private static final int GalleryPic = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_item);
//        databaseReferencecat = FirebaseDatabase.getInstance().getReference("Users");
        ProductCategory = getIntent().getExtras().get("category").toString();
        storageReference = FirebaseStorage.getInstance().getReference().child("Products Images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        progressDialog = new ProgressDialog(this);

        resulttextview = findViewById(R.id.barcodeview);
        additemtodatabase = findViewById(R.id.additembuttontodatabase);
        scanbutton = findViewById(R.id.buttonscan);
        itemname = findViewById(R.id.edititemname);
        itemprice = findViewById(R.id.editprice);
        productImage = findViewById(R.id.product_image);
        itembarcode = findViewById(R.id.barcodeview);


        // String result = finaluser.substring(0, finaluser.indexOf("@"));
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGallery();
            }
        });


        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Scan.class));
            }
        });

        additemtodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }
    private void gotoGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPic  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            productImage.setImageURI(ImageUri);
        }
    }

    private void addItem() {
        Barcode = itembarcode.getText().toString();
        ProductPrice = itemprice.getText().toString();
        ProductName = itemname.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this, "Select product Image", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Barcode))
        {
            Toast.makeText(this, "Scan Product barcode", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ProductPrice))
        {
            Toast.makeText(this, "Please enter the product price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ProductName))
        {
            Toast.makeText(this, "Please enter the product name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        progressDialog.setTitle("Add New Products");
        progressDialog.setMessage("Please wait while we are adding the new product.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = storageReference.child(ImageUri.getLastPathSegment() + Barcode + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddItem.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddItem.this, "Product Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddItem.this, "Captured Products image Url Successfully", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("PID", productRandomKey);
        productMap.put("Date", saveCurrentDate);
        productMap.put("Time", saveCurrentTime);
        productMap.put("BarCode", Barcode);
        productMap.put("Image", downloadImageUrl);
        productMap.put("Category", ProductCategory);
        productMap.put("ProductPrice", ProductPrice);
        productMap.put("ProductName", ProductName );

        databaseReference.child(Barcode).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddItem.this, AdminDashboard.class);
                            startActivity(intent);

                            progressDialog.dismiss();
                            Toast.makeText(AdminAddItem.this, "Product added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddItem.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }

}