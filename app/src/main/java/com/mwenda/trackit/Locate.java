package com.mwenda.trackit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.mwenda.trackit.App.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Locate extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String lat="";
    String lon="";
    String timestamp="";
    public SwipeRefreshLayout swipeLayout;
    boolean permEnabled=false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SupportMapFragment mapFragment;
    String gsmIMEI;
    ProgressDialog pDialog;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        sp=getSharedPreferences("login",MODE_PRIVATE);
        gsmIMEI=sp.getString("imei","");//865674036511646

        if(checkPerm()){
            permEnabled=true;
        }else{
            permEnabled=false;
        }

        getLocation(gsmIMEI);
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
    private void getLocation(String gsmIMEI){
        if(permEnabled){
            //location permission on
            getJSON(gsmIMEI);
        }else{
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        permEnabled=true;
                        getJSON(gsmIMEI);
                    }else {
                        //permission denied
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to this permission for the app to work",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Locate.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void getJSON(final String gsmIMEI){
        lat="";lon="";
        pDialog = new ProgressDialog(Locate.this);
        pDialog.setMessage("Retrieving location data...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
        String url = Constants.LOCATE_URL+gsmIMEI;//get latlon endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            boolean status=jsonObject.optBoolean("success",false);
                            if(status){
                                //results found
                                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                                JSONArray jArray = jsonObject1.getJSONArray("result");
                                for(int i=0;i<jArray.length();i++){
                                    JSONObject jsonObject2 = jArray.getJSONObject(i);//latlong object
                                    lat=jsonObject2.optString("latitude","");
                                    lon=jsonObject2.optString("longitude","");
                                    timestamp=jsonObject2.optString("timestamp","");

                                }
                            }else{
                                //no results found
                                pDialog.dismiss();
                                String reply=jsonObject.optString("message","Location data not found");
                                Toast.makeText(Locate.this, reply, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Locate.this, "Location data not found ", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(Locate.this);
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
         * fetched from the server...add delay of 4 seconds to ensure
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
                        if(!lat.isEmpty()){
                            double lat2=Double.parseDouble(lat);
                            double lon2=Double.parseDouble(lon);
                            pDialog.dismiss();
                            LatLng laptop_location = new LatLng(lat2, lon2);
                            Toast.makeText(Locate.this,"lat->"+lat+"\nlon->"+lon,Toast.LENGTH_SHORT).show();
                            MarkerOptions options = new MarkerOptions()
                                    .title(timestamp)
                                    .position(laptop_location);
                            //setMarker(lat1,lon1,time);
                            mMap.addMarker(options);

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(laptop_location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(laptop_location, 15.0f));

                            mapFragment.onResume();
                        }else{
                            Toast.makeText(Locate.this, "Co-ordinates not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 4000);
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
