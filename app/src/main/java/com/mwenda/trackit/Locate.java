package com.mwenda.trackit;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Locate extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public String time;
    private double lat1,lon1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        String lat =  i.getStringExtra("lat");
        String lon = i.getStringExtra("lon");
        time = i.getStringExtra("time");

        //convert the string types to double for latlong
        lat1=Double.parseDouble(lat);
        lon1=Double.parseDouble(lon);

        StringBuilder sb = new StringBuilder();
        sb.append(lat+"\n"+lon);
        Toast.makeText(this,sb,Toast.LENGTH_SHORT).show();//to confirm the cordinates reach the locate.java
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng laptop_location = new LatLng(lat1, lon1);
        //LatLng laptop_location = new LatLng(lat1, lon1);
        mMap.addMarker(new MarkerOptions().position(laptop_location).title(time));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(laptop_location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(laptop_location, 15.0f));

    }
}
