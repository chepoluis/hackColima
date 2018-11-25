package app.hack.com.hackaton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Console;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        LocationListener {
    Button btnAceptar;

    private GoogleMap mMap;
    LocationManager locationManager;
    String proveedor;
    private boolean networkOn;

    Double lat, lng;
    LatLng location;
    private Location mLastLocation;
    Marker marker;

    String title, type, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        ubicacion();

        title = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("description");

        btnAceptar = findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, AddReportActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("lat", String.valueOf(lat));
                intent.putExtra("lng", String.valueOf(lng));
                Toast.makeText(MapsActivity.this, "Ubicación tomada!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    public void ubicacion() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        proveedor = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(proveedor, 1000, 1, this);
        getLocation();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location ic = locationManager.getLastKnownLocation(proveedor);
        if (ic != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("Altitus:").append(ic.getLatitude())
                    .append("Longitud:").append(ic.getLongitude());
            lat = ic.getLatitude();
            lng = ic.getLongitude();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Ubicación"));

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        marker.remove();

        //Add marker on Click position
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title("Ubicación"));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
