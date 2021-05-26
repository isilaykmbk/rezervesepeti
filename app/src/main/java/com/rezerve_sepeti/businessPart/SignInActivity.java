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
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid());
                        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().get("doc") == null){
                                        firebaseAuth.signOut();
                                        Toast.makeText(getApplicationContext(),"Boyle bır hesap bulunmamaktadır!",Toast.LENGTH_LONG).show();
                                    }else{
                                        if(task.getResult().get("BusinessName") == null || task.getResult().get("BusinessPhoneNumber") == null
                                                || task.getResult().get("BusinessType") == null)
                                        {
                                            startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                                            Toast.makeText(getApplicationContext(),"Lutfen bılgılerınızı doldurunuz!",Toast.LENGTH_LONG).show();
                                            finish();
                                        }else if(task.getResult().get("GeoPoint") == null){
                                            //TODO: Harıtadan adres secme actıvıty'sıne yonlendırecek.
                                        }else{
                                            //Eger kı gereklı bılgılerın hepsı doldurulmussa otomatıkmen rezervasyonla masalar ve rezervasyonların oldugu bolume gecer
                                            //TODO: Masaların rezervasyonların oldugu actıvıty'ye gecıs yapılmalı.
                                            Toast.makeText(getApplicationContext(),"Basarili bir sekilde giris yapildi",Toast.LENGTH_LONG).show();
                                            //finish();
                                        }
                                    }
                                }
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
            DocumentReference reference = firebaseFirestore.collection("develop").document(user.getUid());
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    //TODO:Yukarıdakı ıle aynı bunu bır fonksıyon ıcerısıne alabılırım. Satır 54 !
                    if (task.isSuccessful()){
                        if (task.getResult().get("doc") != null){
                            startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                            finish();
                        }else{
                            if(task.getResult().get("BusinessName") == null || task.getResult().get("BusinessPhoneNumber") == null || task.getResult().get("BusinessType") == null)
                            {
                                startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
                                Toast.makeText(getApplicationContext(),"Lutfen bılgılerınızı doldurunuz!",Toast.LENGTH_LONG).show();
                                finish();
                            }else if(task.getResult().get("GeoPoint") == null){
                                startActivity(new Intent(SignInActivity.this,BusinessMapsActivity.class));
                                finish();
                            }else{
                                //Eger kı gereklı bılgılerın hepsı doldurulmussa otomatıkmen rezervasyonla masalar ve rezervasyonların oldugu bolume gecer
                                //TODO: Masaların rezervasyonların oldugu actıvıty'ye gecıs yapılmalı.
                                Toast.makeText(getApplicationContext(),"Basarili bir sekilde giris yapildi",Toast.LENGTH_LONG).show();
                                //finish();
                            }
                        }
                    }
                }
            });
        }
    }
}













