package com.mwenda.trackit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mwenda.trackit.App.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class History extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> latitudes;
    ArrayList<String> longitudes;
    ArrayList<String> timestamps;
    String time;
    LatLng loc13;
    double[] latArray;
    double[] lonArray;
    boolean permEnabled=false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SupportMapFragment mapFragment;
    SwipeRefreshLayout swipeLayout;
    String gsmIMEI;
    ProgressDialog pDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        sp=getSharedPreferences("login",MODE_PRIVATE);
        gsmIMEI=sp.getString("imei","");//865674036511646


        if(checkPerm()){
            permEnabled=true;
        }else{
            permEnabled=false;
        }
        getHistory(gsmIMEI);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setDistanceToTriggerSync(200);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Insert your code here
                refreshActivity();
            }
        });


        latitudes=new ArrayList<>();
        longitudes=new ArrayList<>();
        timestamps=new ArrayList<>();
    }

    private void getHistory(String gsmIMEI) {
        if(permEnabled){
            /**location permission on->pass the gsmIMEI for which to fetch location details of*/
            getJSON1(gsmIMEI);
        }else{
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    private boolean checkPerm() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(permissionGranted){
            //permission granted
            return true;
        }else{
            //permission NOT granted
            return false;
        }
    }
    private void getJSON1(final String gsmIMEI){
        pDialog = new ProgressDialog(History.this);
        pDialog.setMessage("Retrieving location data...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        String url = Constants.HISTORY_URL+gsmIMEI;//get latlon endpoint

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String lat,lon,timestamp="";
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            boolean status=jsonObject.optBoolean("success",false);
                            if(status){
                                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                                JSONArray jArray = jsonObject1.getJSONArray("result");
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject jsonObject2 = jArray.getJSONObject(i);//latlong object
                                    lat=jsonObject2.optString("latitude","");
                                    lon=jsonObject2.optString("longitude","");
                                    timestamp=jsonObject2.optString("timestamp","");

                                    //latitudes={-1.24,-1.2713,-1.2714}
                                    latitudes.add(lat);//stores all the latitude values
                                    longitudes.add(lon);//stores all the longitude values
                                    timestamps.add(timestamp);//stores all the timestamp values
                                }

                            }else{
                                //no results found
                                pDialog.dismiss();
                                String reply=jsonObject.optString("message","Location data not found");
                                Toast.makeText(History.this, reply, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        //Toast.makeText(Spareparts.this, "error getting all car types "+error, Toast.LENGTH_SHORT).show();
                        Toast.makeText(History.this, "Location data not found ", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(History.this);
        requestQueue.add(jsonObjectRequest);
    }
    private void refreshActivity() {
        final Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
        /**
         *Most times the map is ready before lat long details are
         * fetched from the server...add delay of 6 seconds to ensure
         * by then it will have fetched data to avoid null pointer exception
         */
        mMap = googleMap;
        //get the ui settings of the google maps object
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);//indicate zoom in / zoom out
        uiSettings.setZoomGesturesEnabled(false);//disable zoom gestures to allow swipe to refresh layout to work well
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!latitudes.isEmpty()){
                            //lat&lon arrays
                            latArray = new double[latitudes.size()];
                            lonArray = new double[longitudes.size()];
                            pDialog.dismiss();
                            /**loop through array to retrieve all the lats,lons,timestamp*/
                            for(int i=0;i<latitudes.size();i++){
                                //convert lat long to double
                                latArray[i]=Double.parseDouble(latitudes.get(i));//lat[0],lat[1],lat[2]
                                lonArray[i]=Double.parseDouble(longitudes.get(i));
                                loc13=new LatLng(latArray[i],lonArray[i]);
                                time=timestamps.get(i);

                                MarkerOptions options = new MarkerOptions()
                                        .title(time)
                                        .position(loc13);
                                //setMarker(lat1,lon1,time);
                                mMap.addMarker(options);
                            }
                            /**draw the polylines here to show movement*/
                            for(int j=0;j<longitudes.size();j++){
                                int k=j+1;
                                if(k<longitudes.size()){
                                    PolylineOptions line = new PolylineOptions()
                                            .add(new LatLng(latArray[j],lonArray[j]), new LatLng(latArray[k],lonArray[k]))
                                            .width(5)
                                            .color(Color.RED);
                                    mMap.addPolyline(line);
                                }else{
                                    /**dont create polylineOptions->array out of bounds**/
                                }

                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc13));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc13, 10.0f));
                            mapFragment.onResume();
                        }else{
                            Toast.makeText(History.this, "Co-ordinates not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 7000);
    }

    //public void onBackPressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.onResume();
    }
}

