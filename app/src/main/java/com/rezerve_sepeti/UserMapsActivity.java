package com.rezerve_sepeti;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rezerve_sepeti.databinding.ActivityUserMapsBinding;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;            //1-0
    LocationListener locationListener;          //1-0
    private ActivityUserMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //1-1 alt yerler dahil
        locationListener = location -> {
                //kullanici istedigi yerde gezinebiliyor,çıkınca eski yerde kalıyor //3-0
             SharedPreferences sharedPreferences = UserMapsActivity.this.getSharedPreferences("com.rezerve_sepeti.rezervesepeti",MODE_PRIVATE);
             boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean",false);
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude()); //5-1
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));   //5-1
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Konumum"));
                //kaydedilmemisse true yap //3-1
             if(!trackBoolean) {

                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
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
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                    mMap.addMarker(new MarkerOptions().position(lastUserLocation).title("Konumum"));
                }
            }
        }
        /*else{           //2-0

        }*/


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
                       LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                    }

                    /*else {

                    }*/
                }
            }
        }
    }
}