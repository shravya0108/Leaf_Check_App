package com.example.bananaleafdisease.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bananaleafdisease.MainActivity;
import com.example.bananaleafdisease.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://banana-leaf-disease-default-rtdb.firebaseio.com/").getReference();

        // Get verification ID and resending token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String verificationId = prefs.getString("verificationId", null);

        Button verifyButton = findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = ((EditText) findViewById(R.id.otp_input)).getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            saveUserInformation(user);
                            Toast.makeText(OtpActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                            Intent LoginSuccessIntent = new Intent(OtpActivity.this, MainActivity.class);
                            startActivity(LoginSuccessIntent);
                        } else {
                            Toast.makeText(OtpActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInformation(FirebaseUser user) {
        String userId = user.getUid();
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        UserInformation userInfo = new UserInformation(firstName, lastName, phoneNumber);

        mDatabase.child("users").child(userId).setValue(userInfo);
    }
}
