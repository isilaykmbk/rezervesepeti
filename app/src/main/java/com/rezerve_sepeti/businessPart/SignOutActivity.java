package com.rezerve_sepeti.businessPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.rezerve_sepeti.R;

public class SignOutActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

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
        Intent intenttosignup = new Intent(SignOutActivity.this,SignUpActivity.class);
        startActivity(intenttosignup);
        }
        else{

        }
        return super.onOptionsItemSelected(item);
    }//this method is used for what will be done when something has selected on menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);

        firebaseAuth=FirebaseAuth.getInstance();
    }
}