package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater(); // inflater is usable for linking two file- xml,layout etc.
        menuInflater.inflate(R.menu.optionsmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }// This method is used for linking menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.signout){
            firebaseAuth.signOut();
            startActivity(new Intent(DashboardActivity.this,SignInActivity.class));
            finish();
        }else if(item.getItemId() == R.id.debug){
            Toast.makeText(getApplicationContext(),firebaseUser.getUid(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }//this method is used for what will be done when something has selected on menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        EditText business_name = findViewById(R.id.business_name);
        EditText business_type = findViewById(R.id.business_phone);
        EditText business_phone = findViewById(R.id.business_type);
        button4(business_name,business_type, business_phone);
        button();

        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                business_name.setText((String)documentSnapshot.get("business_name"));
                business_phone.setText((String)documentSnapshot.get("business_phone"));
                business_type.setText((String)documentSnapshot.get("business_type"));
                System.out.println("Verileriniz alınmıstır");
            }
        });
    }
    //firestore holds data according to the hashmap structure. Codes between 68-74th lines describe this structure.
    HashMap<String,Object> GetBusinessModel(String business_name,String business_type,String business_phone){
        HashMap<String,Object> DashboardData= new HashMap<>();
        DashboardData.put("business_name", business_name);
        DashboardData.put("business_type", business_type);
        DashboardData.put("business_phone",business_phone);
        return DashboardData;
    }
    private boolean CheckInputData(EditText business_name, EditText business_type, EditText business_phone) {
        return (business_name.getText().toString().length() > 0 &&
                business_type.getText().toString().length() > 0 &&
                business_phone.getText().toString().length() > 0);
    }
    private void button4(EditText business_name, EditText business_type, EditText business_phone) {
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInputData(business_name,business_type,business_phone)){
                    firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(GetBusinessModel(business_name.getText().toString(),business_type.getText().toString(),business_phone.getText().toString()), SetOptions.merge()).// yoksa ekliyor varsa üzerine yazıyor.
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Verileriniz güncellenmiştir.",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(DashboardActivity.this,BusinessMapsActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    private void button(){
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("documents").document(firebaseAuth.getCurrentUser().getUid())
                        .delete()//update yap
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Duzenlemeye baslayabilirsiniz.",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Duzenlemede hata oldu. Tekrar deneyiniz.",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    };

}
