package com.rezerve_sepeti.userPart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rezerve_sepeti.Place;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.databinding.ActivityUserMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback{
    private FirebaseFirestore firebaseFirestore;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityUserMapsBinding binding;
    private Location currentLocation;
    private Marker userMarker;
    private ArrayList<Marker> businessMarkerList;
    private boolean dataSwitch = false;
    private float radius = 5;
    //private Location userLocation;//Sonra kullanılabılır

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        findViewById(R.id.UserMapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMapsActivity.this, UserDashboardAct.class));
            }
        });
        findViewById(R.id.UserReserveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMapsActivity.this, UserReserveButton.class));
            }
        });
        findViewById(R.id.user_map_currentpos_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });
    }
    //HARITA SUAN DA KULLANICININ MARKER'INI HARKET ETTIKCE GUNCELLIYOR ANCAK SUANKI KONUMU GOSTER TUSUNA BASMADAN
    //KAMERAYI ORAYA GOTURMUYOR...
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Suanki konum"));
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //1-1 alt yerler dahil
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = new Location(location);
                userMarker.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                if (!dataSwitch){
                getBusinessData();
                }
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(getApplicationContext(), "Lutfen GPS Servısınızı acınız!", Toast.LENGTH_SHORT).show();
            }
        };
            //izin verilmemis ise //1-2
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            //izin verildiyse 1-3
            //minTime'ı arttır
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                //son bilinen konumu al, kamerayi oraya cevir
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //userMarker.position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                if (lastLocation != null) {
                    currentLocation = new Location(lastLocation);
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    userMarker.setPosition(currentLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
            getBusinessData();
        }
    @Override //izin ilk defa verildiginde ne yapilacak 1-3
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) { //eger icinde cevap varsa
            if (requestCode == 1) { //eger yukaridaki izin verildiyse
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //izin verildiyse 1-4
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    // 2-1
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        currentLocation = new Location(lastLocation);
                        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        userMarker.setPosition(currentLatLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                }
            }
        }
    }
    private void getBusinessData(){
        /*.whereEqualTo("isOpen",true).
                    whereGreaterThanOrEqualTo("latitude",currentLocation.getLatitude()-radius).
                    whereLessThanOrEqualTo("latitude",currentLocation.getLatitude()+radius).
                    whereGreaterThanOrEqualTo("longitude",currentLocation.getLongitude()-radius).
        whereLessThanOrEqualTo("longitude",currentLocation.getLongitude()+radius)
        */
        if (currentLocation == null){
            firebaseFirestore.collection("develop").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    System.out.println("Basarili");
                    mMap.clear();
                    userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).title("Suanki konum"));
                    businessMarkerList = new ArrayList<>();
                    int i = 0;
                    for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                        System.out.println(snapshot);

                        LatLng latLng = new LatLng((double)snapshot.get("latitude"),(double)snapshot.get("longtitude"));
                        businessMarkerList.add(mMap.addMarker(new MarkerOptions().position(latLng).title((String) snapshot.get("business_name"))));
                        businessMarkerList.get(i).setTag((String)snapshot.get("business_uuid"));
                        i++;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    System.out.println("Basarisiz");
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            dataSwitch = true;
        }
    }
    private void changeRadius(float radius){
        this.radius = radius;
    }
}