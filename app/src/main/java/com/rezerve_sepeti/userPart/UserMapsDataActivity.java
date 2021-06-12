package com.rezerve_sepeti.userPart;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rezerve_sepeti.R;

public class UserMapsDataActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private float radius = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        TextView businessName = findViewById(R.id.restaurant_name);
        TextView businessType = findViewById(R.id.restaurant_type);
        TextView allDeskNumber = findViewById(R.id.all_desk_number);
        TextView emptyDeskNumber = findViewById(R.id.empty_desk_number);

    }
    private boolean CheckInputData(TextView business_name, TextView business_type, TextView table_count, TextView empty_table) {
        return (business_name.getText().toString().length() > 0 &&
                business_type.getText().toString().length() > 0 &&
                table_count.getText().toString().length() > 0 &&
                empty_table.getText().toString().length() > 0);

    }
}