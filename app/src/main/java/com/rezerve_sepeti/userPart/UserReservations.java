package com.rezerve_sepeti.userPart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.MainActivity;
import com.rezerve_sepeti.R;
import java.util.ArrayList;
import java.util.Arrays;

public class UserReservations extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private ArrayList<RadioButton> reservationRadioGroup;
    private String selectedResUUID = "";

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
            Intent intentUserToSignUp = new Intent(UserReservations.this, MainActivity.class);
            startActivity(intentUserToSignUp);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);

        findViewById(R.id.cancelTheReservation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedResUUID.length() > 0)
                    deleteReservation(selectedResUUID);
            }
        });
        findViewById(R.id.delayTheReservation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedResUUID.length() > 0)
                    delayReservation(selectedResUUID);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        scrollView = new ScrollView(this);
        scrollView.setId(R.id.scrollRes);
        contentLayout = new LinearLayout(this);
        LinearLayout scrollHolder = findViewById(R.id.scrollHolder);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        getReservationData();
        scrollView.addView(contentLayout);
        scrollHolder.addView(scrollView);
    }
    private void getReservationData(){
        /*
        Ekstra bir filtreleme eklenebilir. Sadece belli bir tarih sonrasi yada oncesi olarak.
         */
        reservationRadioGroup = new ArrayList<>();
        firebaseFirestore.collection("develop_res").whereEqualTo("user_uuid",firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    String name = (String) snapshot.get("business_name");
                    int time = ((Long)(snapshot.get("user_res_time"))).intValue();
                    int no = ((Long)(snapshot.get("user_table_no"))).intValue();
                    Long reservedDate = (Long) snapshot.get("user_resTo_date");
                    Long reservationDate = (Long) snapshot.get("user_res_date");
                    String UUID = (String)snapshot.getId();
                    System.out.println("UUID: " + UUID);
                    initReservationUi(name,time,no,reservationDate,reservedDate,UUID);
                    System.out.println(snapshot);
                }
            }
        });
    }
    private void initReservationUi(String businessName, int reservedTime, int tableNo, Long reservationDate, Long reservedDate, String UUID){
        String reservationDateStr;
        reservationDateStr = Long.toString(reservationDate);
        System.out.println(reservationDateStr);
        String reservedDateStr;
        reservedDateStr = Long.toString(reservedDate);
        char[] year = new char[4];
        char[] month = new char[2];
        char[] day = new char[2];
        reservationDateStr.getChars(0,4,year,0);
        reservationDateStr.getChars(4,6,month,0);
        reservationDateStr.getChars(6,8,day,0);
        reservationDateStr = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        reservedDateStr.getChars(0,4,year,0);
        reservedDateStr.getChars(4,6,month,0);
        reservedDateStr.getChars(6,8,day,0);
        reservedDateStr = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
        View template = getLayoutInflater().inflate(R.layout.reservations_frame,scrollView,false);
        template.setTag(UUID);
        ((TextView)template.findViewById(R.id.restoranAdi)).setText("Restoran Adı: "+ businessName);
        ((TextView)template.findViewById(R.id.masaNo)).setText("Masa No: "+ tableNo);
        ((TextView)template.findViewById(R.id.toDate)).setText("Rezerve Edilen Tarih: " + reservedDateStr + "-" +String.format("%2d:00",reservedTime));
        ((TextView)template.findViewById(R.id.atDate)).setText("Rezerve Yapılan Tarih: "+ reservationDateStr);
        ((RadioButton)template.findViewById(R.id.isSelected)).setTag(reservationRadioGroup.size());
        reservationRadioGroup.add(((RadioButton)template.findViewById(R.id.isSelected)));
        ((RadioButton)template.findViewById(R.id.isSelected)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Basilan Tus: " + ((RadioButton)v).getTag());
                selectedResUUID = UUID;
                for (RadioButton button:reservationRadioGroup) {
                    System.out.println("Basilan Tus: " + ((RadioButton)v).getTag() + " - " + button.getTag());
                    if (((RadioButton)v).getTag() != button.getTag()){
                        button.setChecked(false);
                    }
                }
            }
        });
        contentLayout.addView(template);
    }
    private void deleteReservation(String UUID){
        //Rezervasyon silme rezervasyonu tamamen siliyor bu yuzden sonrada ulasmak istersek ulasamiyoruz.
        firebaseFirestore.collection("develop_res").document(UUID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Rezervasyon silinmistir.",Toast.LENGTH_SHORT).show();
                contentLayout.removeView(contentLayout.findViewWithTag(UUID));
                selectedResUUID = "";
            }
        });
    }

    private void delayReservation(String UUID){
        // Erteleme suanlik kendinden sonra reservasyon var mi diye bakmiyor sadece 1 saat ileri atiyor buda ondan sonra reservasyon varsa buga yol acar.
        firebaseFirestore.collection("develop_res").document(UUID).update("user_res_time", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Rezervasyon Ertelenmistir.",Toast.LENGTH_SHORT).show();
                TextView date = ((TextView)contentLayout.findViewWithTag(UUID).findViewById(R.id.toDate));
                date.setText(date.getText().toString().split("-")[0] + "-" + String.format("%2d:00",Integer.parseInt(date.getText().toString().split("-")[1].split(":")[0])+1));
            }
        });
    }

}

