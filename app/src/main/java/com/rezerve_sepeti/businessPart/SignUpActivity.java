package com.rezerve_sepeti.businessPart;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        EditText email = findViewById(R.id.business_signup_email);
        EditText password = findViewById(R.id.business_signup_password);
        EditText username = findViewById(R.id.business_signup_username);
        EditText confirmPassword = findViewById(R.id.busi_inputConformPassword);
        signUpButton(email.getText().toString(), password.getText().toString(),
                username.getText().toString(), confirmPassword.getText().toString());

        findViewById(R.id.alreadyHaveAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });
    }
    //Firestore HashMap yapısına gore kayıt yaptıgı ıcın kayıt yapacagımız kurumun modelı.
    HashMap<String,Object> getBusinessModel(String username,String mail,String uuId){
        HashMap<String,Object> model = new HashMap<>();
        model.put("business_username",username); //e-mail yerine de geçebilir.
        model.put("business_mail",mail); //String
        model.put("business_uuid",uuId); //String
        model.put("table_pcs",null); //number/int
        model.put("geo_point",null); // new GeoPoint(0,0)
        model.put("business_address",null); //String
        model.put("business_name",null); //String
        model.put("business_phone",null); //String
        model.put("business_type",null); //String
        model.put("opening_time",null); // TimeStamp
        model.put("closing_time",null); // TimeStamp
        model.put("table_chair_pcs",null); // liste/dizi olabilir.
        model.put("isOpen",true); //Boolean
        return model;
    }
    boolean checkInputData(String email, String password, String username, String confirmPassword) {
        return (email.length() > 0 &&
                username.length() > 0 &&
                password.length() > 0 &&
                confirmPassword.length() > 0 &&
                confirmPassword.equals(password));
    }
    private void signUpButton(String email, String password, String username, String confirmPassword) {
        findViewById(R.id.business_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Girilen veriler dolumu ve şifreler aynımı diye kontroll ediliyor.
                if (checkInputData(email,password,username,confirmPassword)){
                    // Firebase'in email ve password kullanarak auth sistemine kayıt olunmasını sağlaya API'ı kullanarak uye olunma ıslemı baslıyor.
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    //Uye olunma basarılı bır sekılde gerceklestiginde veri tabanına kullanıcı ıle ılgılı verıler eklenıyor.
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(getBusinessModel(username,email,firebaseAuth.getCurrentUser().getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            //Veri tabanına ekleme ıslemı basarılı bır sekılde gerceklestıgınde bır mesaj gosterılıyor ve anasayfaya gerı donuluyor.
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Hesap Basarili Bir Sekilde olusturuldu.",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            }
                            //Verı tabanına verı eklenemesse auth sıstemınden olusturulan kullanıcı sılınıyor ve hata mesajı gosterılıyor.
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                firebaseAuth.getCurrentUser().delete();
                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    //Auth sıstemı ıle uye olunamassa hata mesajı gosterılıyor.
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
                //Eger kı gırılen verıler ıstenıldıgı gıbı degılse hata mesajları gosterılıyor.
                else{
                    //Sıfreler uyusmuyorsa.
                    if (!password.equals(confirmPassword)){
                        Toast.makeText(getApplicationContext(),"Sifreler uyuşmuyor!",Toast.LENGTH_LONG).show();
                    }//Istenılen butun verıler gırılmedıgınde.
                    else{
                        Toast.makeText(getApplicationContext(),"Lütfen boş bırakılan alanları doldurunuz!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
