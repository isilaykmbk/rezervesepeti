package com.rezerve_sepeti.userPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.MainActivity;
import com.rezerve_sepeti.R;

import java.util.ArrayList;
import java.util.Arrays;

public class UserReserveButton extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RadioGroup reservationRadioGroup = new RadioGroup(this);
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
            Intent intentUserToSignUp = new Intent(UserReserveButton.this, MainActivity.class);
            startActivity(intentUserToSignUp);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reserve_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();


    }
    private void getReservationData(){
        /*
        Ekstra bir filtreleme eklenebilir. Sadece belli bir tarih sonrasi yada oncesi olarak.
         */
        firebaseFirestore.collection("develop_res").whereEqualTo("user_uuid",firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    String name = (String) snapshot.get("business_name");
                    int time = (int) snapshot.get("user_res_time");
                    int no = (int) snapshot.get("user_table_no");
                    Long reservedDate = (Long) snapshot.get("user_resToDate");
                    Long reservationDate = (Long) snapshot.get("user_resDate");
                    System.out.println(snapshot);
                }
            }
        });
    }
    private void initReservationUi(String businessName, int reservedTime, int tableNo, Long reservationDate, Long reservedDate){
        String reservationDateStr;
        reservationDateStr = Long.toString(reservationDate);
        String reservedDateStr;
        reservedDateStr = Long.toString(reservedDate);
        char[] year = new char[4];
        char[] month = new char[2];
        char[] day = new char[2];
        reservationDateStr.getChars(0,4,year,0);
        reservationDateStr.getChars(4,6,month,0);
        reservationDateStr.getChars(6,8,day,0);
        reservationDateStr = Arrays.toString(day) + "/" + Arrays.toString(month) + "/" + Arrays.toString(year);
        reservedDateStr.getChars(0,4,year,0);
        reservedDateStr.getChars(4,6,month,0);
        reservedDateStr.getChars(6,8,day,0);
        reservedDateStr = Arrays.toString(day) + "/" + Arrays.toString(month) + "/" + Arrays.toString(year);

    }

}

