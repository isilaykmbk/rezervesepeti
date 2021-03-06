package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

public class SignInActivity  extends AppCompatActivity {
private FirebaseAuth firebaseAuth;
private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signin);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        EditText email = findViewById(R.id.business_signin_email);
        EditText password = findViewById(R.id.business_signin_password);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        findViewById(R.id.business_text_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        findViewById(R.id.business_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Email ve password icerigi bossa program cokuyor.
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseAuth.getUid());
                        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.get("business_uuid") != null){
                                    if (documentSnapshot.get("business_name") == null || documentSnapshot.get("business_phone") == null || documentSnapshot.get("business_type") == null){
                                        startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                                        finish();
                                    }else if (documentSnapshot.get("business_address") == null || documentSnapshot.get("geo_point") == null){
                                        startActivity(new Intent(SignInActivity.this,BusinessMapsActivity.class));
                                        finish();
                                    }else{
                                        startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                                        finish();
                                    }
                                }else{
                                    firebaseAuth.signOut();
                                    Toast.makeText(getApplicationContext(),"Boyle b??r hesap yok.",Toast.LENGTH_SHORT).show();
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
        });
        if(user != null){
            DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseAuth.getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.get("business_uuid") != null){
                        if (documentSnapshot.get("business_name") == null || documentSnapshot.get("business_phone") == null || documentSnapshot.get("business_type") == null){
                            startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                            finish();
                        }else if (documentSnapshot.get("business_address") == null || documentSnapshot.get("geo_point") == null){
                            startActivity(new Intent(SignInActivity.this,BusinessMapsActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Boyle b??r hesap yok.",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}