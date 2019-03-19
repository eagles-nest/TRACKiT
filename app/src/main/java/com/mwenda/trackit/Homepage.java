package com.mwenda.trackit;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.mwenda.trackit.App.Constants;
import com.mwenda.trackit.Authentication.MainActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Homepage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView txtUsername;
    public String gsmIMEI;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE2 = 201;
    SharedPreferences sp;
    boolean permEnabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sMapFragment=SupportMapFragment.newInstance();
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp=getSharedPreferences("login",MODE_PRIVATE);

        if(checkPerm()){
            permEnabled=true;
        }else{
            permEnabled=false;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        txtUsername=(TextView)findViewById(R.id.textViewWelcome);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fM = getFragmentManager();
        android.support.v4.app.FragmentManager sFM = getSupportFragmentManager();
        int id = item.getItemId();
//        if(sMapFragment.isAdded()){
//            sFM.beginTransaction().hide(sMapFragment).commit();
//        }
        switch(id){
            case (R.id.nav_locate):
                getLocation();
                break;
            case (R.id.nav_history):
                history();
                break;
            case (R.id.nav_account):
                account();
                break;
            case (R.id.nav_logout):
                logout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLocation(){
        if(permEnabled){
            //location permission on
            getJSON("718145956");
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
                        getJSON("718145956");
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
            case PERMISSION_REQUEST_CODE2:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        permEnabled=true;
                        getJSON1("718145956");
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
        new AlertDialog.Builder(Homepage.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private void getJSON(final String gsmIMEI){
        String url = Constants.LOCATE_URL+gsmIMEI;//get latlon endpoint
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String lat="";
                            String lon="";
                            String timestamp="";
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
                                    //send the data to the locate class to display marker on map
                                    //on the provided latitude & longitude
                                }

                                Intent a = new Intent(Homepage.this,Locate.class);
                                a.putExtra("lat",lat);
                                a.putExtra("lon",lon);
                                a.putExtra("time",timestamp);
                                startActivity(a);
                            }else{
                                //no results found
                                String reply=jsonObject.optString("message","Location data not found");
                                Toast.makeText(Homepage.this, reply, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Spareparts.this, "error getting all car types "+error, Toast.LENGTH_SHORT).show();
                        Toast.makeText(Homepage.this, "Location data not found ", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(Homepage.this);
        requestQueue.add(jsonObjectRequest);
    }

    //history module
    private void history(){
        if(permEnabled){
            //location permission on
            getJSON1("718145956");
        }else{
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE2);
        }
    }
    private void getJSON1(final String gsmIMEI){
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
                                //results found->create array_lists to store data
                                ArrayList<String> latitudes = new ArrayList<String>();
                                ArrayList<String> longitudes = new ArrayList<String>();
                                ArrayList<String> timestamps = new ArrayList<String>();


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
                                    timestamps.add(timestamp);//stores all the longitude values
                                }
                                Intent a = new Intent(Homepage.this,History.class);
                                a.putExtra("latitudes",latitudes);
                                a.putExtra("longitudes",longitudes);
                                a.putExtra("timestamps",timestamps);
                                startActivity(a);
                            }else{
                                //no results found
                                String reply=jsonObject.optString("message","Location data not found");
                                Toast.makeText(Homepage.this, reply, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Spareparts.this, "error getting all car types "+error, Toast.LENGTH_SHORT).show();
                        Toast.makeText(Homepage.this, "Location data not found ", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(Homepage.this);
        requestQueue.add(jsonObjectRequest);
    }


    private void account() {
        //load my account activity
        Intent intent = new Intent(Homepage.this,Account.class);
        startActivity(intent);
    }

    private void logout(){
        //logs user out->destroy saved preferences
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();
        new AlertDialog.Builder(Homepage.this)
                .setMessage("Logout of TrackIt?")
                .setIcon(R.drawable.account)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent= new Intent(Homepage.this,MainActivity.class);
                        SharedPreferences.Editor e = sp.edit();
                        e.clear();
                        e.apply();
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("NO",null).show();
    }

}
