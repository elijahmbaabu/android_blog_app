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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText mail;
    Button recoverPass;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mail = (EditText) findViewById(R.id.forgotEmail);
        recoverPass = (Button) findViewById(R.id.forgotPasswordBtn);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Forgot Password");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resetMail = mail.getText().toString();
                if (resetMail.isEmpty()){
                    mail.setError("Registered Email Required");
                    mail.requestFocus();
                }
                else {
                    recoverPassword(resetMail);
                }
            }
        });
    }

    private void recoverPassword(String email) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "Please Check Mail, link has been sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}