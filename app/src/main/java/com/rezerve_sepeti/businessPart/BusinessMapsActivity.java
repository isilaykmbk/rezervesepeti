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

public class BusinessMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //---------------------Firabase----------------------
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    MarkerOptions marker = new MarkerOptions();
    //---------------------------------------------------
    //-------------------Harita/Lokasyon-----------------
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ActivityBusinessMapsBinding binding;
    private Location selectedLocation; // Haritadan elle yada GPS sisteminin anlık olarak gönderdiği mevcut konum arasında seçim yaparken kullanılan değişken.
    private Location currentLocation; //GPS sistemi açıksa kullanıcının şuanki konumunu gerektiğinde kullanmak için tutan değişken.
    // currentLocation kullanıcı her hareket ettiğinde güncellenirken selectedLocation sadece kullanıcı haritadan yer seçtiğinde yada currentLocationButton tuşuna basınca güncellenir.
    //---------------------------------------------------
    private String addressString;
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
            setLocation(currentLocation);
            if(currentLocation != null){
                selectedLocation = new Location(currentLocation);
                addressString = setLocation(selectedLocation);
                ((TextView)findViewById(R.id.business_map_address_text)).setText("Address:"+addressString);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(selectedLocation.getLatitude(),selectedLocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectedLocation.getLatitude(),selectedLocation.getLongitude()),15));
            }});
        //---------------------------------------------------------------
        //--------------------------Next Button--------------------------
        findViewById(R.id.business_map_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kayıt ediceğimiz verileri hashmap şeklinde tutup,firestore database'ine merge seçeneğiyle kayıt ediyoruz.
                // Merge seçeneği kayıtlı verilerde varsa üzerine yazmaya yoksa sıfırdan key adıyla bir alan oluşturup yazmayı sağlar.
                // Böylece sadece istediğimiz verileri değiştirmiş oluruz ve diğer verilerimiz silinmez yada değişmez.
                HashMap<String,Object> model = new HashMap<>();
                model.put("geo_point",new GeoPoint(selectedLocation.getLatitude(),selectedLocation.getLongitude()));
                model.put("latitude",selectedLocation.getLatitude());
                model.put("longitude",selectedLocation.getLongitude());
                model.put("business_address",addressString);
                DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
                reference.set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Veriler guncellendı",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BusinessMapsActivity.this,TablesActivity.class));
                        finish();
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
        // Haritadan dashboard ekranına geçmeyi sağlayan tuş.
        findViewById(R.id.business_map_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessMapsActivity.this,DashboardActivity.class));
                finish();
            }
        });
        //----------------------------------------------------
    }
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        //-------------OnMapClick-----------------------
        //Haritanın üzerine tıklandığında seçilmiş konum değişkeni onMapClick fonksiyonunun döndürdüğü
        //kordinat düzlemiyle ayarlanır haritadaki işaretçiler silinir ve yerine dödürülen kordinat düzlemine sahip bir işaretçi gelir.
        //SetLocation fonksiyonu haritanın altında bulunan adres açıklamasını eklemek için çağrılır.
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                selectedLocation.setLatitude(latLng.latitude);
                selectedLocation.setLongitude(latLng.longitude);
                setLocation(selectedLocation);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                return false;
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
            if (currentLocation != null) {
                LatLng lastUserLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
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
    private String setLocation(Location location){
        if (location != null){
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0){
                    return addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Konum adresi suan da alınamıyor.",Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}