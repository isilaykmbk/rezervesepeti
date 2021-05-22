package com.rezerve_sepeti.userPart;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.databinding.ActivityUserrMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityUserrMapsBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserrMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        //yeni bir yer eklemek istiyorsa 6-1

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //5-1 /6-1
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                //kullanici istedigi yerde gezinebiliyor,çıkınca eski yerde kalıyor 6-2
                SharedPreferences sharedPreferences = UserMapsActivity.this.getSharedPreferences("com.rezerve_sepeti",MODE_PRIVATE);
                boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean", false);
                //kaydedilmemisse true yap 6-2
                if (!trackBoolean) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude()); //5-1
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));   //5-1
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply();
                }
            }
        };

        //izinleri kontrol et
        //5-2

        //izin verilmemis ise
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //izin verildiyse 5-2
        //minTime'ı arttır
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


            //son bilinen konumu al, kamerayi oraya cevir 5-2
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
            }

        }
    }


    @Override //izin ilk defa verildiginde ne yapilacak 5-2
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) { //eger icinde cevap varsa
            if (requestCode == 1) { //eger yukaridaki izin verildiyse
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //izin verildiyse 5-2
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    //------------------------------------------------

                    //6-1
                    Intent intent = getIntent();
                    String info = intent.getStringExtra("info");

                    //yeni bir yer eklenecekse 6-1
                    if (info.matches("new")) {
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }
                    }

                }
            }
        }

    }

    //implements ile ortaya cikti 7-1
    //onMapReady altında tanımlama yap 7-1
    @Override
    public void onMapLongClick(LatLng latLng) { //144.video

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address += " ";
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }
            } else {
                address = "New Place";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.clear();

        mMap.addMarker(new MarkerOptions().title(address).position(latLng));
    }
}