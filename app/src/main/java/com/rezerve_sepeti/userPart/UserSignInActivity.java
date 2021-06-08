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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

//TODO:: user'ın kurum mu yoksa kullanıcı mı olduunu test edip ona göre içeri al

public class UserSignInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signin);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText email = findViewById(R.id.user_signin_username);
        EditText password = findViewById(R.id.user_signin_password);

        findViewById(R.id.user_text_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this, UserSignUpActivity.class));
            }
        });
        findViewById(R.id.user_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),password.getText().toString());
            }
        });
        if (firebaseUser != null){
            DocumentReference reference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.get("user_uuid") != null) {
                        startActivity(new Intent(UserSignInActivity.this, UserMapsActivity.class));
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "Boyle bır hesap yok.", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }
                }
            });
        }
    }
    void signIn(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                DocumentReference reference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.get("user_uuid") != null){
                            startActivity(new Intent(UserSignInActivity.this, UserMapsActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Boyle bır hesap yok.",Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}