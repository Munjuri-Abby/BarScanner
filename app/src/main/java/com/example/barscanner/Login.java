package com.example.barscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    private EditText InputEmail, InputPassword, passwordresetemail, InputPhoneNumber;
    private Button LoginButton;
    private ProgressDialog progressBar;
    private TextView AdminLink, NotAdminLink, ForgetPasswordLink;
    private FirebaseAuth auth;
    private String parentDbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        LoginButton = findViewById(R.id.Login);
        InputPassword = findViewById(R.id.password);
        InputPhoneNumber = (EditText) findViewById(R.id.emailSignIn);
//        InputEmail = findViewById(R.id.emailSignIn);

        AdminLink = (TextView) findViewById(R.id.admin_link);
        NotAdminLink = (TextView) findViewById(R.id.user_link);
        passwordresetemail = findViewById(R.id.emailSignIn);
//        AdminLink = findViewById(R.id.adminLink);
//        UserLink = findViewById(R.id.userLink);
        ForgetPasswordLink = findViewById(R.id.forgotpassword);
        progressBar = new ProgressDialog(this);

//        TODO Remove me am for testing
//        InputPhoneNumber.setText("0746287172");
//        InputPassword.setText("foobarbaz");

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpasword();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

    }
    public void resetpasword() {
        final String resetemail = passwordresetemail.getText().toString();

        if (resetemail.isEmpty()) {
            passwordresetemail.setError("Empty field!");
            passwordresetemail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(resetemail)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password reset instructions sent to your email!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Failed to send reset instructions!", Toast.LENGTH_SHORT).show();
                        }}
                });

    }
    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
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
            progressBar.setTitle("Login to Account");
            progressBar.setMessage("Please wait while we validate your credentials.");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();


            LoginToAccount(phone, password);
        }
    }

    private void LoginToAccount(final String phone, final String password)
    {

        final DatabaseReference mAuth;
        mAuth = FirebaseDatabase.getInstance().getReference();

        mAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(Login.this, "Successfully logged in as an Admin", Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();

                                Intent intent = new Intent(Login.this, AdminDashboard.class);
                                startActivity(intent);
                            }

                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(Login.this, "User logged in Successfully...", Toast.LENGTH_SHORT).show();
                                progressBar.dismiss();

                                Intent intent = new Intent(Login.this, Home.class);
                                    Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            progressBar.dismiss();
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    progressBar.dismiss();
                    Toast.makeText(Login.this, " Account containing phone number"  +  phone  + " does not exist. Please create a new account.", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}