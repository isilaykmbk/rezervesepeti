package com.rezerve_sepeti.userPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class UserSignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        EditText email = findViewById(R.id.user_signup_email);
        EditText password = findViewById(R.id.user_signup_password);
        EditText name = findViewById(R.id.user_signup_name);
        EditText surname = findViewById(R.id.user_signup_surname);
        EditText phone = findViewById(R.id.user_phone);
        userSignupButton(email.getText().toString(), password.getText().toString(), name.getText().toString(), surname.getText().toString(), phone.getText().toString());

        findViewById(R.id.user_text_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
            }
        });
    }
    private boolean CheckInputData(String name, String surname, String email, String phone) {
        return (name.length() > 0 &&
                surname.length() > 0 &&
                email.length() > 0 &&
                phone.length() > 0);

    }

    HashMap<String, Object> GetUserModel(String name, String surname, String email, String phone) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("user_name", name);
        model.put("user_surname", surname);
        model.put("user_mail", email);
        model.put("user_phone", phone);
        return model;
    }

    private void userSignupButton(String email, String password, String name, String surname, String phone) {
        findViewById(R.id.user_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInputData(email,name,surname,phone)){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(GetUserModel(name,surname,email,phone)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(),"User Created.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
                                    finish();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });

                }
            }
        });
    }

}