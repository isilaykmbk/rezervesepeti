package com.rezerve_sepeti.userPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.rezerve_sepeti.R;
import com.rezerve_sepeti.UserMapsActivity;

//TODO:: user'ın kurum mu yoksa kullanıcı mı olduunu test edip ona göre içeri al

public class UserSignInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        EditText emailText = findViewById(R.id.user_signin_username);
        EditText passwordText = findViewById(R.id.user_signin_password);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        findViewById(R.id.user_text_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this, UserSignUpActivity.class));
            }
        });

        findViewById(R.id.user_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(UserSignInActivity.this, UserMapsActivity.class));
                        Toast.makeText(getApplicationContext(),"Giris Basarili",Toast.LENGTH_LONG).show();
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
/*
        if (firebaseUser != null){
            startActivity(new Intent(UserSignInActivity.this,UserDashboardAct.class));
            finish();
        }

 */
    }
}