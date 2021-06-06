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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class UserSignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    EditText emailText, passwordText,nameText,surnameText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailText = findViewById(R.id.user_signup_email);
        passwordText = findViewById(R.id.user_signup_password);
        nameText = findViewById(R.id.user_signup_fullname);
        surnameText = findViewById(R.id.user_signup_username);
        findViewById(R.id.user_text_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
            }
        });
        findViewById(R.id.user_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignupButton(emailText.getText().toString(),passwordText.getText().toString(),nameText.getText().toString(),surnameText.getText().toString());
            }
        });
    }
    //Firestore HashMap yapısına gore kayıt yaptıgı ıcın kayıt yapacagımız kurumun modelı.
    HashMap<String,Object> getUserModel(String mail, String uuId, String name, String surname){
        HashMap<String,Object> model = new HashMap<>();
        //model.put("user_username",username); //e-mail yerine de geçebilir.
        model.put("user_mail",mail); //String
        model.put("user_uuid",uuId); //String
        model.put("user_name",name); //String
        model.put("user_surname",surname); //String
        return model;
    }
    boolean checkInputData(String email, String password, String name, String surname){
        return (email.length() > 0 &&
                password.length() > 0 &&
                name.length() > 0 &&
                surname.length() > 0);
    }
    public void userSignupButton (String email, String password, String name, String surname) {
        if (checkInputData(email, password, name, surname)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                //Uye olunma basarılı bır sekılde gerceklestiginde veri tabanına kullanıcı ıle ılgılı verıler eklenıyor.
                @Override
                public void onSuccess(AuthResult authResult) {
                    firebaseFirestore.collection("develop_user").document(firebaseAuth.getCurrentUser().getUid()).set(getUserModel(email, firebaseAuth.getCurrentUser().getUid(),name,surname)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        //Veri tabanına ekleme ıslemı basarılı bır sekılde gerceklestıgınde bır mesaj gosterılıyor ve anasayfaya gerı donuluyor.
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Hesap Basarili Bir Sekilde olusturuldu.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UserSignUpActivity.this, UserSignInActivity.class));
                        }
                        //Verı tabanına verı eklenemesse auth sıstemınden olusturulan kullanıcı sılınıyor ve hata mesajı gosterılıyor.
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            firebaseAuth.getCurrentUser().delete();
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //Auth sıstemı ıle uye olunamassa hata mesajı gosterılıyor.
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserSignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}