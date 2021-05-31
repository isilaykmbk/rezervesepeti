package com.rezerve_sepeti.userPart;

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
import android.view.View;

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
import com.rezerve_sepeti.Place;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.databinding.ActivityUserMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;            //1-0
    LocationListener locationListener;          //1-0
    private ActivityUserMapsBinding binding;
    private Location currentLocation;
    private MarkerOptions userMarker;
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
        userMarker = new MarkerOptions();
        findViewById(R.id.UserMapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserMapsActivity.this, UserDashboardAct.class));
            }
        });
        //TODO:Burasını haritaya bir buton ekleyip idsini assagıdakı gıbı yaptıktan sonra ac.
        /*
        findViewById(R.id.user_map_currentpos_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });*/
    }
    //HARITA SUAN DA KULLANICININ MARKER'INI HARKET ETTIKCE GUNCELLIYOR ANCAK SUANKI KONUMU GOSTER TUSUNA BASMADAN
    //KAMERAYI ORAYA GOTURMUYOR...
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //1-1 alt yerler dahil
        locationListener = location -> {
                //kullanici istedigi yerde gezinebiliyor,çıkınca eski yerde kalıyor //3-0
            //SharedPreferences sharedPreferences = UserMapsActivity.this.getSharedPreferences("com.rezerve_sepeti.rezervesepeti",MODE_PRIVATE);
            //boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean",false);
            currentLocation = new Location(location);
            userMarker.position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
            //LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude()); //5-1
            mMap.clear();
            mMap.addMarker(userMarker);
            //kaydedilmemisse true yap //3-1
             /*if(!trackBoolean) {
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));   //5-1
                 sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                }*/
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
                userMarker.position(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                mMap.addMarker(userMarker);
                if (lastLocation != null) {
                    currentLocation = new Location(lastLocation);
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    userMarker.position(currentLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
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
                    mMap.addMarker(userMarker);
                    if (lastLocation != null) {
                        currentLocation = new Location(lastLocation);
                        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        userMarker.position(currentLatLng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                }
            }
        }
    }
    //TODO: Uzun basılan noktanın adresı gerekıyor mu ?
    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList != null && addressList.size() > 0) {
                if(addressList.get(0).getThoroughfare() != null) {
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



        mMap.addMarker(new MarkerOptions().position(latLng).title("Restoran"));

        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;

        Place place = new Place(address, latitude, longitude);
        System.out.println(address); // !!! Logcat bolumunu acip haritaya uzun basarsaniz adres gozukuyor

    }
}