package com.rezerve_sepeti.userPart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class UserDashboardAct extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private static  final String TAG = "UserDashboardAct";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseFirestore firebaseFirestore;

    private String businessUUID;
    private int tablePcs;
    private int openTime;
    private int closeTime;
    private ArrayList<Long> tableChairPcs;
    private HashMap<String,Integer> reservations = new HashMap<>(); //Burasini fonksiyona parametre olarak yollayabilirim.

    private long selectedTimestamp;
    private int selectedTime = -1;
    private int selectedTableNo = -1;

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
            Intent intentUserToSignUp = new Intent(UserDashboardAct.this, UserSignInActivity.class);
            startActivity(intentUserToSignUp);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            businessUUID = extras.getString("selectedBusiness_UUID");
        }

        findViewById(R.id.rezerve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(selectedTableNo + " " + selectedTime);
                if (selectedTableNo != -1 && selectedTime != -1){
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    reserveTable(selectedTableNo,selectedTime,getTimestamp(year,month,day),selectedTimestamp);
                }
            }
        });

        // Tarih icin gerekli kod
        mDisplayDate = (TextView) findViewById(R.id.date);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UserDashboardAct.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                System.out.println("once" + month);
                month = month + 1;
                System.out.println("sonra" + month);
                Log.d(TAG,"onDateSet : mm/dd/yyy:" + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
                selectedTimestamp = getTimestamp(year,month,day);
                onDateChange();
            }
        };
        //------------Guncel Tarih Alma---------------------------
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.MONTH)+1 + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
        mDisplayDate.setText(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        selectedTimestamp = getTimestamp(year,month,day);
        //---------------------------------------------------
        firebaseFirestore.collection("develop").document(businessUUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println(documentSnapshot.get("table_pcs"));
                tablePcs = Math.toIntExact((Long) documentSnapshot.get("table_pcs"));
                tableChairPcs = (ArrayList<Long>)documentSnapshot.get("table_chair_pcs");
                openTime = Math.toIntExact((Long)documentSnapshot.get("opening_time"));
                closeTime = Math.toIntExact((Long)documentSnapshot.get("closing_time"));
                getReservationData();
            }
        });
        //.orderBy("user_res_time") Belki kullanilabilir.
    }
    private void initLayout(){
        LinearLayout tableLayout = findViewById(R.id.addTable);
        tableLayout.removeAllViewsInLayout();
        LinearLayout chairLayout = findViewById(R.id.chairLayout);
        chairLayout.removeAllViewsInLayout();
        RadioGroup tableGroup = new RadioGroup(this);
        tableGroup.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < tablePcs; i++) {
            //---------Table Init------------------------
            RadioButton table = new RadioButton(this);
            table.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            table.setText("Masa " + (i+1));
            table.setTextSize(15);
            table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTableNo = Integer.parseInt((((RadioButton)v).getText().toString().split(" "))[1]);
                    initTimeLine(selectedTableNo);
                }
            });
            tableGroup.addView(table);
            //ToggleButton table = new ToggleButton(this);
            //tableToggleButtons.add(table);
            //table.setText("Masa " + i + "-");
            //table.setWidth(125);
            //table.setHeight(20);
            //tableLayout.addView(table);
            //-------------------------------------------
            //---------Chair Init------------------------
            TextView chair = new TextView(this);
            chair.setWidth(120);
            chair.setHeight(140);
            chair.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            chair.setTextSize(14);
            chair.setText(tableChairPcs.get(i).toString());
            chairLayout.addView(chair);
            //---------------------------------------------
        }
        tableLayout.addView(tableGroup);
    }
    private void initTimeLine(int tableNo){
        //----------------- Time Line---------------------
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        String date = simpleDateFormat.format(calendar.getTime());
        int currentTime = Integer.parseInt(date);
        int startTime = Math.max(openTime, currentTime);
        RadioGroup timeRadioGroup = new RadioGroup(this);
        LinearLayout timeline = findViewById(R.id.user_dashboard_timeline);
        timeline.removeAllViewsInLayout();
        for (int j = 0; j < 24; j++) {
            RadioButton time = new RadioButton(this);
            time.setId(j);
            timeRadioGroup.addView(time);
            boolean isReserved = reservations.getOrDefault(j+"-"+tableNo,99) == j;
            if(isReserved || j < startTime+1 || j > closeTime-1)
            {
                System.out.println("Saat :" +j+" Reservasyon durumu: " + isReserved + " Acilis saati: " + (startTime + 1) + " Kapanis saati: "+ (closeTime - 1));
                System.out.println(reservations);
                if (isReserved)
                    time.setText("Reserved");
                else
                    time.setText("Time Out");
                time.setClickable(false);
                time.setTextColor(Color.RED);

            }else{
                time.setText(String.format("%2d:00",j));
                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTime = v.getId();
                    }
                });
            }
        }
        timeline.addView(timeRadioGroup);
        //---------------------------------------------
    }
    private void reserveTable(int tableNo, int time, Long resDate,Long date){
        System.out.println("reserve deneme" + " " + tableNo + " " + time + " " + resDate + " " + date);
        firebaseFirestore.collection("users").document(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String,Object> resModel = new HashMap<>();
                resModel.put("business_uuid",businessUUID);
                resModel.put("user_uuid",documentSnapshot.get("user_uuid"));
                resModel.put("user_fullname", documentSnapshot.get("user_name") + ((String) documentSnapshot.get("user_surname")));
                resModel.put("user_resTo_date",date);
                resModel.put("user_table_no",tableNo);
                resModel.put("user_res_time",time);
                resModel.put("user_res_date",resDate);
                firebaseFirestore.collection("develop_res").document(UUID.randomUUID().toString()).set(resModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //startActivity(new Intent(UserDashboardAct.this, UserReserveActivity.class));
                        Intent toUserDashboard = new Intent(UserDashboardAct.this, UserReserveActivity.class);
                        toUserDashboard.putExtra("selectedBusiness_UUID",businessUUID);
                        startActivity(toUserDashboard);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }
    private Long getTimestamp(int year,int month,int day){
        String strDay;
        String strMonth;
        String strYear = ""+year;
        if (day < 10){
            strDay = "0" + day;
        }else{
            strDay = "" + day;
        }
        if (month < 10){
            strMonth = "0" + month;
        }else{
            strMonth = "" + month;
        }
        return Long.parseLong(strYear+strMonth+strDay);
    }
    private void onDateChange(){
        reservations = new HashMap<>();
        LinearLayout timeline = findViewById(R.id.user_dashboard_timeline);
        timeline.removeAllViewsInLayout();
        getReservationData();
    }
    private void getReservationData(){
        System.out.println("Zaman: "+selectedTimestamp);
        firebaseFirestore.collection("develop_res").whereEqualTo("business_uuid",businessUUID).whereEqualTo("user_resTo_date",selectedTimestamp).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots) {
                    System.out.println(snapshot);
                    int resTime = Math.toIntExact((Long)snapshot.get("user_res_time"));
                    int tableNo = Math.toIntExact((Long)snapshot.get("user_table_no"));
                    reservations.put(resTime+"-"+tableNo,resTime);
                }
                initLayout();
            }
        });
    }
}



