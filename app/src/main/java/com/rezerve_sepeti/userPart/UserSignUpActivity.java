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
import com.rezerve_sepeti.MainActivity;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

public class UserSignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.inputEmail);
        passwordText = findViewById(R.id.signupPassword);

        findViewById(R.id.textViewSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
            }
        });
    }

        public void userSignupButton (View view){
        String email = emailText.getText().toString() ;
        String password = passwordText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(UserSignUpActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });


    }


}