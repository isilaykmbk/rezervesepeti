package com.rezerve_sepeti.businessPart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BusinessResActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private int tableNo;

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
        Bundle extra = getIntent().getExtras();
        if (extra != null){
            tableNo = extra.getInt("tableNo");
        }
        //getDataForUsers();
        scrollView = new ScrollView(this);
        scrollView.setId(R.id.scrollRes);
        contentLayout = new LinearLayout(this);
        LinearLayout scrollHolder = findViewById(R.id.business_scroll_holder);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        getDataForRes();
        scrollView.addView(contentLayout);
        scrollHolder.addView(scrollView);
        //allow to change screen to tables
        findViewById(R.id.business_res_tables_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessResActivity.this,TablesActivity.class));
                finish();
            }
        });
        //allow to change screen to map
        findViewById(R.id.business_res_maps_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessResActivity.this,BusinessMapsActivity.class));
                finish();
            }
        });
        //allow to change screen to dashboard
        findViewById(R.id.business_res_dashboard_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessResActivity.this,DashboardActivity.class));
                finish();
            }
        });

    }
    public void getDataForRes(){
        System.out.println("snapshot");
        firebaseFirestore.collection("develop_res").whereEqualTo("business_uuid",firebaseAuth.getUid()).orderBy("user_res_date",Query.Direction.DESCENDING).orderBy("user_table_no",Query.Direction.DESCENDING).orderBy("user_res_time",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {

                    String name = (String) snapshot.get("user_fullname");
                    int time = ((Long)(snapshot.get("user_res_time"))).intValue();
                    int no = ((Long)(snapshot.get("user_table_no"))).intValue();
                    Long reservedDate = (Long) snapshot.get("user_resTo_date");
                    Long reservationDate = (Long) snapshot.get("user_res_date");
                    String UUID = snapshot.getId();
                    //System.out.println("UUID: " + UUID);
                    initReservationUi(name,time,no,reservationDate,reservedDate,UUID);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                System.out.println(e.getLocalizedMessage());
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
        View template = getLayoutInflater().inflate(R.layout.business_reservations_frame,scrollView,false);
        template.setTag(UUID);
        ((TextView)template.findViewById(R.id.business_res_name)).setText("Musteri Adı: "+ businessName);
        ((TextView)template.findViewById(R.id.business_res_tableNo)).setText("Masa No: "+ tableNo);
        ((TextView)template.findViewById(R.id.business_res_date)).setText("Rezerve Edilen Tarih: " + reservedDateStr + "-" +String.format("%2d:00",reservedTime));
        ((TextView)template.findViewById(R.id.business_res_hour)).setText("Rezerve Yapılan Tarih: "+ reservationDateStr);
        contentLayout.addView(template);
    }
}
