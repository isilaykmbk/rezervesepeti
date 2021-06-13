package com.rezerve_sepeti.userPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rezerve_sepeti.MainActivity;
import com.rezerve_sepeti.R;

public class UserReserveActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String tempBusinessUUID;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.rezerv_options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.usersignout){
            firebaseAuth.signOut();
            Intent intentUserToSignUp = new Intent(UserReserveActivity.this, MainActivity.class);
            startActivity(intentUserToSignUp);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reserve);
        firebaseAuth = FirebaseAuth.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tempBusinessUUID = extras.getString("selectedBusiness_UUID");
        }
        findViewById(R.id.user_reserved_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUserDashboard = new Intent(UserReserveActivity.this, UserDashboardAct.class);
                toUserDashboard.putExtra("selectedBusiness_UUID",tempBusinessUUID);
                startActivity(toUserDashboard);
            }
        });
    }
}
