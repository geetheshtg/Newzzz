package com.example.newzzz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    Button reset;
    private FirebaseAuth mFirebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText)findViewById(R.id.forgotpasswordedit);
        reset = (Button)findViewById(R.id.resetpass);
        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Enter Email ID", Toast.LENGTH_SHORT).show();
                }else if(!(email.getText().toString().contains("@") && email.getText().toString().contains("."))){
                    Toast.makeText(ForgotPasswordActivity.this, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Sending Reset E-Mail to\n"+email.getText().toString());
                    progressDialog.show();
                    ResetPassword(email.getText().toString());
                }
            }
        });
    }

    private void ResetPassword(String userEmail) {

        mFirebaseAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this, "An email has been sent to you.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}