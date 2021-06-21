package com.example.barscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterAdmin extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword, editTextEmail, editTextcPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);



//        progressBar.setVisibility(View.GONE);

        CreateAccountButton = (Button) findViewById(R.id.button_register);
        InputName = (EditText) findViewById(R.id.userName);
        editTextcPassword= findViewById(R.id.confirmPassword);
        InputPassword = (EditText) findViewById(R.id.passwordRegister);
        InputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {

        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String cpassword = editTextcPassword.getText().toString();


        if (name.isEmpty()) {
            InputName.setError("Empty Field");
            InputName.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            InputPassword.setError("Empty field");
            InputPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            InputPassword.setError("short password, > 6 characters");
            InputPassword.requestFocus();
            return;
        }
        if(!password.equals(cpassword)){
            editTextcPassword.setError("Password Mismatch");
            editTextcPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            InputPhoneNumber.setError("Empty field");
            InputPhoneNumber.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            InputPhoneNumber.setError("Must be 10 characters!");
            InputPhoneNumber.requestFocus();
            return;
        }


        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validateEmail(name, phone, password);
        }
    }    private void validateEmail(final String name, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Admins").child(phone).exists()))
                {
                    HashMap<String, Object> admindataMap = new HashMap<>();
                    admindataMap.put("phone", phone);
                    admindataMap.put("name", name);
                    admindataMap.put("password", password);
                    RootRef.child("Admins").child(phone).updateChildren(admindataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterAdmin.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterAdmin.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterAdmin.this, "Network Error: Please try again after some time.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(RegisterAdmin.this, "This " +phone+ " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterAdmin.this, "Please try again using another email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterAdmin.this, Login.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}