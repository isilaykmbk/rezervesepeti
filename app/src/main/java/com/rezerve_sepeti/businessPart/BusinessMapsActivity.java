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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Location selectedLocation; // Haritadan elle yada GPS sisteminin anl??k olarak g??nderdi??i mevcut konum aras??nda se??im yaparken kullan??lan de??i??ken.
    private Location currentLocation; //GPS sistemi a????ksa kullan??c??n??n ??uanki konumunu gerekti??inde kullanmak i??in tutan de??i??ken.
    // currentLocation kullan??c?? her hareket etti??inde g??ncellenirken selectedLocation sadece kullan??c?? haritadan yer se??ti??inde yada currentLocationButton tu??una bas??nca g??ncellenir.
    //---------------------------------------------------
    private String addressString;

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
            startActivity(new Intent(BusinessMapsActivity.this,SignInActivity.class));
            finish();
        }else if(item.getItemId() == R.id.debug){
            Toast.makeText(getApplicationContext(),firebaseUser.getUid(),Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }//this method is used for what will be done when something has selected on menu


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
                // Kay??t edice??imiz verileri hashmap ??eklinde tutup,firestore database'ine merge se??ene??iyle kay??t ediyoruz.
                // Merge se??ene??i kay??tl?? verilerde varsa ??zerine yazmaya yoksa s??f??rdan key ad??yla bir alan olu??turup yazmay?? sa??lar.
                // B??ylece sadece istedi??imiz verileri de??i??tirmi?? oluruz ve di??er verilerimiz silinmez yada de??i??mez.
                HashMap<String,Object> model = new HashMap<>();
                model.put("geo_point",new GeoPoint(selectedLocation.getLatitude(),selectedLocation.getLongitude()));
                model.put("latitude",selectedLocation.getLatitude());
                model.put("longitude",selectedLocation.getLongitude());
                model.put("business_address",addressString);
                DocumentReference reference = firebaseFirestore.collection("develop").document(firebaseUser.getUid());
                reference.set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(),"Veriler guncellend??",Toast.LENGTH_SHORT).show();
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
        //Codes between 144th-151st lines allow to change screen to dashboard
        findViewById(R.id.business_map_dash_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessMapsActivity.this,DashboardActivity.class));
                finish();
            }
        });
        //Codes between 152nd-158th lines allow to change screen to tables
        findViewById(R.id.business_map_tables_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessMapsActivity.this,TablesActivity.class));
                finish();
            }
        });
        //Codes between 144th-160th lines allow to change screen to res
        findViewById(R.id.business_map_reserves_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessMapsActivity.this,BusinessResActivity.class));
                finish();
            }
        });
    }
    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        mMap = googleMap;
        //-------------OnMapClick-----------------------
        //Haritan??n ??zerine t??kland??????nda se??ilmi?? konum de??i??keni onMapClick fonksiyonunun d??nd??rd??????
        //kordinat d??zlemiyle ayarlan??r haritadaki i??aret??iler silinir ve yerine d??d??r??len kordinat d??zlemine sahip bir i??aret??i gelir.
        //SetLocation fonksiyonu haritan??n alt??nda bulunan adres a????klamas??n?? eklemek i??in ??a??r??l??r.
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
                Toast.makeText(getApplicationContext(), "Lutfen GPS Serv??s??n??z?? ac??n??z!", Toast.LENGTH_SHORT).show();
            }
        };
        //-------------------------------------------------------------
        //Iz??n almad??ysa ??z??n ??ste varsa bulundugun konumu ??ste--------
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
    //Fonks??yon Str??ng deger dondurur sek??lde yap.
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
            Toast.makeText(getApplicationContext(),"Konum adresi suan da al??nam??yor.",Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}
