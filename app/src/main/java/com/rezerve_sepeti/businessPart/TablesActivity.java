package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

public class TablesActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private TextView table_pcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //EditText table_pcs = findViewById(R.id.textView2);
        table_pcs = (TextView) findViewById(R.id.table_pcss);
        buttontable(table_pcs);

        //If table_pcs is not null, then show the number that has been taken before.
        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("table_pcs") != null)
                    table_pcs.setText((String)documentSnapshot.get("table_pcs"));

            }
        });

        //back button for changing screen from tables to maps
        findViewById(R.id.business_dash_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TablesActivity.this,BusinessMapsActivity.class));
            }
        });

    }

    //firestore holds data according to the hashmap structure. Codes between 64-68th lines describe this structure.
    HashMap<String,Object> GetTableModel(String table_pcs){
        HashMap<String,Object> TablesData= new HashMap<>();
        TablesData.put("table_pcs", table_pcs);
        return TablesData;
    }


    private boolean CheckTableData(TextView table_pcs) {
        return (table_pcs.getText().toString().length() > 0);
    }


    //button method for taking pcs of table
    private void buttontable(TextView table_pcs) {
        findViewById(R.id.buttontable).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (CheckTableData(table_pcs)){
                    firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(GetTableModel(table_pcs.getText().toString()), SetOptions.merge()).// yoksa ekliyor varsa üzerine yazıyor.
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(),"Masa Sayısı Alınmıştır!",Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Masa Sayısı Alınamamıştır!",Toast.LENGTH_LONG).show();

                        }
                    });
                }

            }
        });
    }


}