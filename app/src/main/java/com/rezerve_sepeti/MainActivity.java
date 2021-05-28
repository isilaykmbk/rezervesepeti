package com.rezerve_sepeti;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rezerve_sepeti.businessPart.BusinessMapsActivity;
import com.rezerve_sepeti.businessPart.SignInActivity;
import com.rezerve_sepeti.userPart.UserSignInActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(MainActivity.this, BusinessMapsActivity.class));
        findViewById(R.id.businessbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }
        });

        findViewById(R.id.userbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserSignInActivity.class));
            }
        });
    }
}