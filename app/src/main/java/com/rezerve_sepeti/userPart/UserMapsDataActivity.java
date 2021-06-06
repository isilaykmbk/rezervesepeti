package com.rezerve_sepeti.userPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UserMapsDataActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

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

        UserMapButton(businessName,businessType,allDeskNumber,emptyDeskNumber);

        DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("restaurant_name") != null)
                    businessName.setText((String) documentSnapshot.get("restaurant_name"));
                if (documentSnapshot.get("restaurant_type") != null)
                    businessType.setText((String) documentSnapshot.get("restaurant_type"));
                if (documentSnapshot.get("all_desk_number") != null)
                    allDeskNumber.setText((String) documentSnapshot.get("all_desk_number"));
                if (documentSnapshot.get("empty_desk_number") != null)
                    emptyDeskNumber.setText((String) documentSnapshot.get("empty_desk_number"));


            }
        });
    }



    HashMap<String, Object> getBusinessData(String business_name, String business_type, String table_count, String empty_table) {
        HashMap<String, Object> businessData = new HashMap<>();
        businessData.put("businessName", business_name);
        businessData.put("businessType", business_type);
        businessData.put("allDeskNumber", table_count);
        businessData.put("emptyDeskNumber", empty_table);
        return businessData;
    }

    private boolean CheckInputData(TextView business_name, TextView business_type, TextView table_count, TextView empty_table) {
        return (business_name.getText().toString().length() > 0 &&
                business_type.getText().toString().length() > 0 &&
                table_count.getText().toString().length() > 0 &&
                empty_table.getText().toString().length() > 0);

    }

    private void UserMapButton(TextView businessName, TextView businessType, TextView allDeskNumber, TextView emptyDeskNumber) {
        findViewById(R.id.UserMapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInputData(businessName,businessType,allDeskNumber,emptyDeskNumber)){
                    firebaseFirestore.collection("develop").document(firebaseAuth.getCurrentUser().getUid()).set(getBusinessData(businessName.getText().toString(),businessType.getText().toString(),allDeskNumber.getText().toString(),emptyDeskNumber.getText().toString()), SetOptions.merge()).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(),"Restorant bilgileri gosterilmistir.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UserMapsDataActivity.this, UserMapsActivity.class));
                                    finish();
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

}