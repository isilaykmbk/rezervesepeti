package com.rezerve_sepeti.userPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.userPart.UserSignInActivity;
import com.rezerve_sepeti.userPart.UserSignUpActivity;

public class UserSignUpActivity extends AppCompatActivity {
    private FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        fireBaseAuth = FirebaseAuth.getInstance();
        EditText fullname = findViewById(R.id.inputFullname);
        EditText username = findViewById(R.id.inputUsername);
        EditText email = findViewById(R.id.inputEmail);
        EditText password = findViewById(R.id.forgotPassword);
        findViewById(R.id.textViewSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Task<AuthResult> authResultTask = fireBaseAuth.createUserWithEmailAndPassword(fullname.getText().toString(), username.getText().toString(), email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
                        Toast.makeText(getApplicationContext(), "Hesap Basarili Bir Sekilde olusturuldu.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
            }
        });



    }
}