package com.example.newzzz;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.securepreferences.SecurePreferences;

import com.example.newzzz.helpers.Utils;


public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button registerHere,Login;
    TextView forgotPassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    SecurePreferences pref;
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Login = findViewById(R.id.login);
        registerHere = findViewById(R.id.registerHere);
        forgotPassword = findViewById(R.id.forgotpassword);
        pref = new SecurePreferences(this);
        email.setText(pref.getString("email",""));
        password.setText(pref.getString("password",""));

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() !=null){
            openHome();
        }
        progressDialog = new ProgressDialog(LoginActivity.this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginActivity.this);
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter All fields!", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                    LoginToNext(email.getText().toString(), password.getText().toString());
                }
            }
        });

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    private void LoginToNext(final String userEmail, final String Password) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, Password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                        pref.edit()
                                .putString("email",userEmail)
                                .putString("password",Password)
                                .apply();
                        //mFirebaseAnalytics.setUserProperty("UserData", userEmail+" : "+Password);
                        openHome();
                    }else{
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginActivity.this, "Verify your email before you proceed", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openHome(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}