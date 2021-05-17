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
import com.google.firebase.firestore.auth.User;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.businessPart.SignInActivity;
import com.rezerve_sepeti.businessPart.SignUpActivity;

import org.jetbrains.annotations.NotNull;

public class UserSignInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.inputEmail);
        passwordText = findViewById(R.id.signupPassword);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(UserSignInActivity.this, UserDashboardAct.class);
            startActivity(intent);
            finish();
        }


        findViewById(R.id.textViewSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this, UserSignUpActivity.class));
            }
        });
    }

        public void userSigninButton(View view) {
            String email = emailText.getText().toString() ;
            String password = passwordText.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent = new Intent(UserSignInActivity.this,UserDashboardAct.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(UserSignInActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                }
            });

        }


}