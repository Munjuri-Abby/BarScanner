package com.example.barscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class Register extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword, editTextEmail, editTextcPassword;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



//        progressBar.setVisibility(View.GONE);

        CreateAccountButton = (Button) findViewById(R.id.button_register);
        InputName = (EditText) findViewById(R.id.userName);
        editTextEmail = findViewById(R.id.emailRegister);
        editTextcPassword= findViewById(R.id.confirmPassword);
        InputPassword = (EditText) findViewById(R.id.passwordRegister);
        InputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        progressBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerUser();
            }
        });
    }

    private void registerUser()
    {

        String name = InputName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String cpassword = editTextcPassword.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.setError("Empty field");
            editTextEmail.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            InputName.setError("Empty Field");
            InputName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid Email");
            editTextEmail.requestFocus();
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
            progressBar.setTitle("Sign Up");
            progressBar.setMessage("Verifying credentials, please wait.");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            validateEmail(name, phone, email,cpassword, password);
        }
    }    private void validateEmail(final String name, final String phone,final String cpassword, final String email, final String password)
    {
        final DatabaseReference mAuth;
        mAuth = FirebaseDatabase.getInstance().getReference();
        mAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("phone", phone);
                    userdataMap.put("name", name);
                    userdataMap.put("password", password);
                    userdataMap.put("cpassword", cpassword);

                    mAuth.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "Your account has been created successfully.", Toast.LENGTH_LONG).show();
                                        progressBar.dismiss();

                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(Register.this, "Not Successful, please try again later.", Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(Register.this, "This " +phone+ " already exists.", Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                    Toast.makeText(Register.this, "Account exists, please sign in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.adminicon){
            Intent intent = new Intent(Register.this, RegisterAdmin.class);
            startActivity(intent);
            Toast.makeText(this, "Admin only!", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }
}