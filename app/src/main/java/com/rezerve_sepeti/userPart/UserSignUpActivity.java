package com.rezerve_sepeti.userPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
        userSignupButton(email, password, name , surname, phone);

        findViewById(R.id.user_text_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
            }
        });
    }

    private boolean CheckInputData(TextView name, TextView surname, TextView email, TextView phone) {
        return (name.getText().toString().length() > 0 &&
                surname.getText().toString().length() > 0 &&
                email.getText().toString().length() > 0 &&
                phone.getText().toString().length() > 0);
    }

    HashMap<String, Object> GetUserModel(String name, String surname, String email, String phone,String password, String uuId) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("user_name", name);
        model.put("user_surname", surname);
        model.put("user_mail", email);
        model.put("user_phone", phone);
        model.put("user_password", password);
        model.put("user_uuid", uuId);
        return model;
    }

    private void userSignupButton(EditText email, EditText password, EditText name, EditText surname, EditText phone) {
        findViewById(R.id.user_signup_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CheckInputData(email, name, surname, phone)) {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(GetUserModel(name.getText().toString(), surname.getText().toString(), email.getText().toString(), phone.getText().toString(),password.getText().toString(),firebaseAuth.getCurrentUser().getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "User Created.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
