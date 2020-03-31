package com.example.newzzz;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.newzzz.helpers.Utils;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserName, emailid, password,cpassword;
    private Button registerBtn;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference profileDataBaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserName = findViewById(R.id.userNAME);
        emailid = findViewById(R.id.userEMAIL);
        password = findViewById(R.id.userpassword);
        cpassword = findViewById(R.id.cpassword);
        registerBtn = findViewById(R.id.registerbtn);
        firebaseAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(RegisterActivity.this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(RegisterActivity.this);
                if(UserName.getText().toString().isEmpty() ||emailid.getText().toString().isEmpty() || password.getText().toString().isEmpty() || cpassword.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter All Fields!", Toast.LENGTH_SHORT).show();
                }else if(UserName.getText().toString().length() < 5){
                    Toast.makeText(RegisterActivity.this, "Username Length should be greater than 5", Toast.LENGTH_SHORT).show();
                }else if(!(emailid.getText().toString().contains("@") || emailid.getText().toString().contains("."))){
                    Toast.makeText(RegisterActivity.this, "Email ID Invalid", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length() < 5){
                    Toast.makeText(RegisterActivity.this, "Password Length should be greater than 5", Toast.LENGTH_SHORT).show();
                }
                else if(!(password.getText().toString().equals(cpassword.getText().toString()))){
                    Toast.makeText(RegisterActivity.this, "Both passwords must be same!", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();
                    RegisterUser(emailid.getText().toString(), password.getText().toString());
                }

            }
        });
    }

    private void RegisterUser(String umail, String pass) {

        firebaseAuth.createUserWithEmailAndPassword(umail,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.setMessage("Sending Verification Mail...");
                            updateProfile();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.showToast(RegisterActivity.this,e.getMessage());

                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Utils.showToast(RegisterActivity.this,task.getException().getMessage());
                }
            }
        });
    }

    private void updateProfile() {
        UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();
        profileBuilder.setDisplayName(UserName.getText().toString());
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileBuilder.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.setMessage("Finishing Up...");
                profileDataBaseRef = FirebaseDatabase.getInstance().getReference().child("userProfile").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());

                final Map<String, String> notemap = new HashMap<>();
                notemap.put("isProfileUpdatedFully", "0");
                notemap.put("Name", UserName.getText().toString());
                notemap.put("Email", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
                notemap.put("ProfileID", firebaseAuth.getCurrentUser().getUid());
                notemap.put("ResumeDownloadLink","NU");
                notemap.put("EducationalDetails","NU");
                notemap.put("Projects", "NU");
                notemap.put("Bio","NU");
                notemap.put("CurrentDesignation", "NU");
                notemap.put("IconURL", "NU");
                notemap.put("TimeTable", "https://firebasestorage.googleapis.com/v0/b/faculty-dashboard-fd.appspot.com/o/Faculty%20TimeTable%2Ftimetable.jpg?alt=media&token=22d47f5a-e785-4ef2-af45-cbcdc9a7d4ff");
                notemap.put("ContactNumber", "75399xxx45");

                profileDataBaseRef.setValue(notemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            Utils.showToast(RegisterActivity.this,"User Registration Successful!\nVerify your Email!");
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //   Toast.makeText(add_note_activity.this, "Error : "+fAuth.getCurrentUser(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Utils.showToast(RegisterActivity.this,e.getMessage());
            }
        });
    }


}