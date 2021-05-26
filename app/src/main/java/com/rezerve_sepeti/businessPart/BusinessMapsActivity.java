package com.rezerve_sepeti.businessPart;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.rezerve_sepeti.R;
import com.rezerve_sepeti.databinding.ActivityBusinessMapsBinding;
//import com.rezerve_sepeti.databinding.ActivityBusinessMapsBinding;

import org.jetbrains.annotations.NotNull;

public class BusinessMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityBusinessMapsBinding binding;
    private final int REQUESTCODE = 2;
    private final float ZOOM = 10;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MarkerOptions positionMarker;
    private Location lastKnownLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBusinessMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.business_map_fragment);
        mapFragment.getMapAsync(this);
        findViewById(R.id.business_dash_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetCurrentPosition();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        positionMarker = new MarkerOptions().title("Business Marker");
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                mMap.clear();
                positionMarker.position(latLng);
                mMap.addMarker(positionMarker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM));
            }
        });
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lastKnownLocation = location;
                System.out.println("Enlem:"+location.getAltitude()+"-"+lastKnownLocation.getAltitude()+"Boylam"+location.getLongitude()+"-"+lastKnownLocation.getLongitude());
            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            SetCurrentPosition();
        }
    }

    void SetCurrentPosition() {
        System.out.println(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        if (lastKnownLocation != null){
            Toast.makeText(getApplicationContext(),"harıtadakı suankı konumuna gıt",Toast.LENGTH_LONG).show();
            positionMarker.position(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()));
            mMap.clear();
            mMap.addMarker(positionMarker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),ZOOM));
        }else{
            Toast.makeText(getApplicationContext(),"Suan kı konumunuza erisemiyoruz.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == REQUESTCODE) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTCODE);
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}