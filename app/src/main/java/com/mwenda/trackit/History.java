package com.mwenda.trackit;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class History extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> latitudes;
    ArrayList<String> longitudes;
    ArrayList<String> timestamps;
    public String t1,t2,t3;
    public double eo1,eo2,eo3,lo1,lo2,lo3;
    public double lat1,lon1;
    String time;
    LatLng loc13;
    double[] latArray;
    double[] lonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        latitudes = (ArrayList<String>)extras.getStringArrayList("latitudes");
        longitudes = (ArrayList<String>)extras.getStringArrayList("longitudes");
        timestamps = (ArrayList<String>)extras.getStringArrayList("timestamps");

        //lat&lon arrays
        latArray = new double[latitudes.size()];
        lonArray = new double[longitudes.size()];
        //getAddrFrmLatlong(lat,lon)


//        //lat[0]=Double.parseDouble(latitude.get(0));
//        String e1 = latitude.get(0);String l1 = longitude.get(0);t1 = timers.get(0);
//        String e2 = latitude.get(1);String l2 = longitude.get(1);t2 = timers.get(1);
//        String e3 = latitude.get(2);String l3 = longitude.get(2);t3 = timers.get(2);
//        eo1=Double.parseDouble(e1);lo1=Double.parseDouble(l1);
//        eo2=Double.parseDouble(e2);lo2=Double.parseDouble(l2);
//        eo3=Double.parseDouble(e3);lo3=Double.parseDouble(l3);
//
        Toast.makeText(this,String.valueOf(latitudes),Toast.LENGTH_SHORT).show();//to confirm the cordinates reach the locate.java
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

        //Toast.makeText(this, "lat1->"+latitudes.get(0), Toast.LENGTH_SHORT).show();
        for(int i=0;i<longitudes.size();i++){
            //convert lat long to double
            latArray[i]=Double.parseDouble(latitudes.get(i));//lat[0],lat[1],lat[2]
            lonArray[i]=Double.parseDouble(longitudes.get(i));
            loc13=new LatLng(latArray[i],lonArray[i]);
            time=timestamps.get(i);

            MarkerOptions options = new MarkerOptions()
                    .title(time)
                    .position(loc13)
                    .snippet("address");
            //setMarker(lat1,lon1,time);
            mMap.addMarker(options);
        }
        for(int j=0;j<longitudes.size();j++){
            int k=j+1;
            if(k<longitudes.size()){
                PolylineOptions line = new PolylineOptions()
                        .add(new LatLng(latArray[j],lonArray[j]), new LatLng(latArray[k],lonArray[k]))
                        .width(5)
                        .color(Color.RED);
                mMap.addPolyline(line);
            }else{
                //dont create polylineOptions
            }

        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc13));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc13, 10.0f));
    }
    //public void onBackPressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}

