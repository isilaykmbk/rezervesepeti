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
import com.google.firebase.firestore.GeoPoint;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore =  FirebaseFirestore.getInstance();
        EditText email = findViewById(R.id.user_signup_username);
        EditText password = findViewById(R.id.user_signup_email);
        EditText username = findViewById(R.id.user_signup_fullname);
        EditText confirmPassword = findViewById(R.id.inputConformPassword);
        SignUpButton(email, password, username, confirmPassword);
    }
    //Firestore HashMap yapısına gore kayıt yaptıgı ıcın kayıt yapacagımız kurumun modelı.
    HashMap<String,Object> GetBusinessModel(String username,String mail,String uuId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("Username",username);
        map.put("Email",mail);
        map.put("UUID",uuId);
        map.put("TableCount",null);
        map.put("ChairCounts",null);//new int[0]
        map.put("GeoPoint",null); //new GeoPoint(0,0)
        return map;
    }
    //TODO:Bu yapı muhtemelen dashboardda masaları duzenlerken kullanılacak.
    HashMap<String,Object> GetTableModel()
    {
        return null;
    }
    boolean CheckInputDatas(EditText email, EditText password, EditText username, EditText confirmPassword) {
        return (email.getText().toString().length() > 0 &&
                username.getText().toString().length() > 0 &&
                password.getText().toString().length() > 0 &&
                confirmPassword.getText().toString().length() > 0 &&
                confirmPassword.getText().toString() == password.getText().toString());
    }
    private void SignUpButton(EditText email, EditText password, EditText username, EditText confirmPassword) {
        findViewById(R.id.user_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Girilen veriler dolumu ve şifreler aynımı diye kontroll ediliyor.
                if (CheckInputDatas(email,password,username,confirmPassword)){
                    // Firebase'in email ve password kullanarak auth sistemine kayıt olunmasını sağlaya API'ı kullanarak uye olunma ıslemı baslıyor.
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    //Uye olunma basarılı bır sekılde gerceklestiginde veri tabanına kullanıcı ıle ılgılı verıler eklenıyor.
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(GetBusinessModel(username.getText().toString(),email.getText().toString(),firebaseAuth.getCurrentUser().getUid())).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    if (password.getText().toString() != confirmPassword.getText().toString()){
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
