package com.rezerve_sepeti.businessPart;

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
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.databinding.ActivityBusinessMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//TODO:HALA Harita hareket guncellemelerı olmuyor NOKTA.

public class BusinessMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityBusinessMapsBinding binding;
    private Location selectedLocation;
    private Location currentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.business_map_fragment);
        mapFragment.getMapAsync(this);
        selectedLocation = new Location(LocationManager.GPS_PROVIDER);
        currentLocation = new Location(LocationManager.GPS_PROVIDER);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //--------------------Current Location Button//--------------------
        findViewById(R.id.business_map_set_currentposition_button).setOnClickListener(v -> {
            SetLocation(currentLocation);
            if(currentLocation != null){
                selectedLocation = new Location(currentLocation);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())));
            }});
        //---------------------------------------------------------------
        //--------------------------Next Button--------------------------
        findViewById(R.id.business_map_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> model = new HashMap<>();
                model.put("geo_point",new GeoPoint(selectedLocation.getLatitude(),selectedLocation.getLongitude()));
                model.put("business_address",((TextView)findViewById(R.id.business_map_address_text)).getText().toString());
                DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
                reference.set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Veriler guncellendı",Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(BusinessMapsActivity.this,bıryere));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //---------------------------------------------------
        //--------------------Back Button--------------------
        findViewById(R.id.business_map_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessMapsActivity.this,DashboardActivity.class));
            }
        });
        //----------------------------------------------------
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //-------------OnMapClick-----------------------
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                selectedLocation.setLatitude(latLng.latitude);
                selectedLocation.setLongitude(latLng.longitude);
                SetLocation(selectedLocation);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //----------------------Location Listener-----------------------
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = new Location(location);
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(getApplicationContext(), "Lutfen GPS Servısınızı acınız!", Toast.LENGTH_SHORT).show();
            }
        };
        //-------------------------------------------------------------
        //Izın almadıysa ızın ıste varsa bulundugun konumu ıste--------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            //currentLocation = new Location(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            if (currentLocation != null) {
                LatLng lastUserLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Current Location"));
            }
        }
        //--------------------------------------------------------------
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 2) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLocation != null) {
                        LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                    }
                }
            }
        }
    }
    //Fonksıyon Strıng deger dondurur sekılde yap.
    private void SetLocation(Location location){
        if (location != null){
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0){
                    //System.out.println(addresses.get(0).getAddressLine(0));
                    ((TextView)findViewById(R.id.business_map_address_text)).setText("Address:"+addresses.get(0).getAddressLine(0));
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Konum adresi suan da alınamıyor.",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Konum adresi suan da alınamıyor.",Toast.LENGTH_SHORT).show();
        }
    }
}