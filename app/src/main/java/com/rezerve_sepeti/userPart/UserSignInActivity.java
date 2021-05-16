package com.rezerve_sepeti.userPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.rezerve_sepeti.R;
import com.rezerve_sepeti.businessPart.DashboardActivity;
import com.rezerve_sepeti.businessPart.SignInActivity;
import com.rezerve_sepeti.businessPart.SignUpActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rezerve_sepeti.R;

public class UserSignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.inputUsername);
        EditText password = findViewById(R.id.inputEmail);
        findViewById(R.id.textViewSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this, UserSignUpActivity.class));
            }
        });findViewById(R.id.btnlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(UserSignInActivity.this, DashboardActivity.class));
                        Toast.makeText(getApplicationContext(),"Basarili bir sekilde giris yapildi",Toast.LENGTH_LONG).show();
                        finish();
                }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        /*if(user != null){
            startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
            finish();
        }*/
    }
}