package com.example.farmersblog;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText edtMail, edtPassword;
    TextView txtRegister, txtFPassword;
    Button userSignIn;

    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtMail = (EditText) findViewById(R.id.userMail);
        edtPassword = (EditText) findViewById(R.id.userPassword);
        txtRegister = (TextView) findViewById(R.id.registerLink);
        txtFPassword = (TextView) findViewById(R.id.forgotPassword);
        userSignIn = (Button) findViewById(R.id.login_btn);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        txtFPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        userSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtMail.getText().toString().trim();
                String userPass = edtPassword.getText().toString().trim();

                if (email.isEmpty()){
                    edtMail.setError("Email Required for Sign in!");
                    edtMail.requestFocus();
                }
                else if (userPass.isEmpty()){
                    edtPassword.setError("Password Required for sign in!");
                    edtPassword.requestFocus();
                }
                else{
                    loginUser(email, userPass);
                }
            }
        });
    }

    private void loginUser(String email, String userPass) {
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Login Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
    }

}