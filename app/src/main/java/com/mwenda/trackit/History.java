package com.mwenda.trackit;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class History extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public String t1,t2,t3;
    public double eo1,eo2,eo3,lo1,lo2,lo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        double [] lat={};
        double[] lon={};
        Bundle extras = getIntent().getExtras();
        ArrayList<String> latitude = (ArrayList<String>)extras.getStringArrayList("latitude");
        ArrayList<String> longitude = (ArrayList<String>)extras.getStringArrayList("longitude");
        ArrayList<String> timers = (ArrayList<String>)extras.getStringArrayList("time");
        //lat[0]=Double.parseDouble(latitude.get(0));
        String e1 = latitude.get(0);String l1 = longitude.get(0);t1 = timers.get(0);
        String e2 = latitude.get(1);String l2 = longitude.get(1);t2 = timers.get(1);
        String e3 = latitude.get(2);String l3 = longitude.get(2);t3 = timers.get(2);
        eo1=Double.parseDouble(e1);lo1=Double.parseDouble(l1);
        eo2=Double.parseDouble(e2);lo2=Double.parseDouble(l2);
        eo3=Double.parseDouble(e3);lo3=Double.parseDouble(l3);
//
        Toast.makeText(this,String.valueOf(lo3),Toast.LENGTH_SHORT).show();//to confirm the cordinates reach the locate.java
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
        LatLng loc1 = new LatLng(eo1,lo1);
        LatLng loc2 = new LatLng(eo2,lo2);
        LatLng loc3 = new LatLng(eo3,lo3);
        mMap.addMarker(new MarkerOptions().position(loc1).title(t1));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc1));
        mMap.addMarker(new MarkerOptions().position(loc2).title(t2));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc2));
        mMap.addMarker(new MarkerOptions().position(loc3).title(t3));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc3));
    }
}
