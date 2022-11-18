package com.example.farmersblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegisterEmail, edtRegisterPassword;
    Button btnRegister;
    TextView txtLogin;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtRegisterEmail = (EditText) findViewById(R.id.registerMail);
        edtRegisterPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.register_btn);
        txtLogin = (TextView) findViewById(R.id.loginLink);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Register User");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = edtRegisterEmail.getText().toString().trim();
                String password = edtRegisterPassword.getText().toString().trim();

                if (emailID.isEmpty()){
                    edtRegisterEmail.setError("User Email Address Required!");
                    edtRegisterEmail.requestFocus();
                }
                else if (password.isEmpty()){
                    edtRegisterPassword.setError("User Password Required");
                    edtRegisterPassword.requestFocus();
                }
                else if (password.length() < 6){
                    edtRegisterPassword.setError("Password should be at least 6 characters");
                    edtRegisterPassword.requestFocus();
                }
                else {
                    createUserWithEmailAndPassword(emailID, password);
                }
            }
        });
    }

    private void createUserWithEmailAndPassword(String emailID, String password) {
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(emailID, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            Toast.makeText(RegisterActivity.this, "User Registered Successfully, you can Login", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Register Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}