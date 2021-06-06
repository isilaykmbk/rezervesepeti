package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
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

public class TablesActivity extends AppCompatActivity{
    private Switch onOffSwitch;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private Button newButton;
    private TextView tablePcsTextView;
    private int tablePcs;
    private int[] tableChairPcs;
    private int pageIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        tablePcsTextView = (TextView) findViewById(R.id.table_pcss);
        updateTablePcs(Integer.parseInt(tablePcsTextView.getText().toString()));
        //If table_pcs is not null, then show the number that has been taken before.
        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("table_pcs") != null){
                    tablePcs = (int)documentSnapshot.get("table_pcs");
                }else{
                    tablePcs = 0;
                }
                if(documentSnapshot.get("table_chair_count")!=null){
                    tableChairPcs = (int[])documentSnapshot.get("table_chair_count");
                }else{
                    tableChairPcs = new int[0];
                }
                onOffSwitch.setChecked(documentSnapshot.getBoolean("isOpen"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                tablePcs = 0;
                tableChairPcs = new int[0];
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        tablePcsTextView.setText(tablePcs);
        onOffSwitch = (Switch)findViewById(R.id.business_table_onOff_switch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<String,Object> model = new HashMap<>();
                model.put("isOpen",isChecked);
                firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(model, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        onOffSwitch.setChecked(!isChecked);
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //back button for changing screen from tables to maps
        findViewById(R.id.business_table_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TablesActivity.this,BusinessMapsActivity.class));
            }
        });
        initTables(tablePcs);
    }
    void initTables(int pcs){
        for (int i = 0; i < (Math.min(pcs, 9)); i++) {
            System.out.println(i);
            newButton = new Button(this);
            newButton.setText(i+".Button");
            /*newButton.setMinWidth();
            newButton.setMaxWidth();
            newButton.setMinHeight();
            newButton.setMaxHeight();*/
            int buttonIndex = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tableButtonClicked(buttonIndex);
                }
            });
            ((GridLayout)findViewById(R.id.business_table_tables)).addView(newButton);
        }
    }
    private void updateTablePcs(int table_pcs) {
        findViewById(R.id.business_table_tableSave).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (table_pcs > 0){
                    HashMap<String,Object> tableData= new HashMap<>();
                    tableData.put("table_pcs", table_pcs);
                    firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(tableData, SetOptions.merge()).// yoksa ekliyor varsa üzerine yazıyor.
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
    private void tableButtonClicked(int i){
        System.out.println("Basilan Tus:"+i*pageIndex);
    }
}