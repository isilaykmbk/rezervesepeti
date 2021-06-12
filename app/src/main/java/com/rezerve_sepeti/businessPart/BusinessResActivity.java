package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.R;

import java.util.Map;

public class BusinessResActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
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
            startActivity(new Intent(BusinessResActivity.this,SignInActivity.class));
            finish();
        }else if(item.getItemId() == R.id.debug){
            Toast.makeText(getApplicationContext(),firebaseUser.getUid(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }//this method is used for what will be done when something has selected on menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_res);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //getDataForUsers();
        getDataForRes();

    }


    public void getDataForRes(){
        CollectionReference develop_res = firebaseFirestore.collection("develop_res");
        //dizme işlemi tarihe göre artarak:
        develop_res.orderBy("user_res_date", Query.Direction.ASCENDING); //TODO: Siralamayi degistir.
        develop_res.whereEqualTo("business_uuid",firebaseUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot querySnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(querySnapshot!=null){
                    for(DocumentSnapshot docsnap: querySnapshot.getDocuments()){
                        Map<String,Object> devdata = docsnap.getData();
                        String user_res_time =(String) devdata.get("user_res_time");
                        String user_table_no=(String) devdata.get("user_table_no");
                        System.out.println(user_table_no);

                    }
                }

            }
        });


    }

    public void getDataForUsers(){
        firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                System.out.println(value);
                if(value!=null){
                    for(DocumentSnapshot snapshot: value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();

                        //casting=(String)
                        String user_name =(String) data.get("user_name");
                        String user_mail =(String) data.get("user_mail");
                        String user_phone=(String) data.get("user_phone");
                        String user_surname=(String) data.get("user_surname");
                        //System.out.println(user_name);


            }}}
        });

    }

}

