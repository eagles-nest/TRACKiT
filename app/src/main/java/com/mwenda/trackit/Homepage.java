package com.mwenda.trackit;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

public class Homepage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView txtUsername;
    public String userName,gsmIMEI;
    //SupportMapFragment sMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sMapFragment=SupportMapFragment.newInstance();
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        //get the value of the username
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String username=sharedPreferences.getString("usr_username",null);
        gsmIMEI=sharedPreferences.getString("usr_gsmIMEI",null);
        userName=username;
        Toast.makeText(this,gsmIMEI, Toast.LENGTH_SHORT).show();

        txtUsername=(TextView)findViewById(R.id.textViewWelcome);
        txtUsername.setText("Welcome "+username+",\n");

//        sMapFragment.getMapAsync(this);
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
//                if(!sMapFragment.isAdded()){
//                    sFM.beginTransaction().add(R.id.map,sMapFragment).commit();
//                }else{
//                    sFM.beginTransaction().show(sMapFragment).commit();
//                }

                //locate();
                break;
            case (R.id.nav_history):
                history();
                break;
            case (R.id.nav_account):
                account(userName);
                break;
            case (R.id.nav_about):
                about();
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
        getJSON("https://2db65f43.ngrok.io/locate.php?limit=1&gsmIMEI="+gsmIMEI);
    }
    private void getJSON(final String urlWebService){
        //urlWebService->url containing php script outputting the database data in json format
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                //Called before execution..may set a progress bar
                super.onPreExecute();
            }
            //display toast with json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s==null){
                    Toast.makeText(Homepage.this, "You have no co-ordinates to display", Toast.LENGTH_LONG).show();
                }else{
                    //you have cordinates
                    try {
                        showloc(s);
                        //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                //creating a URL
                try {
                    URL url = new URL(urlWebService);
                    //open the url
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    //string builder object to read the data
                    InputStream inputStream = httpURLConnection.getInputStream();
                    //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        stringBuilder.append(json +"\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    //return the read string
                    String sb = stringBuilder.toString().trim();
                    return sb;
                }catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    private void showloc(String json) throws JSONException{
        //extract the lat,lon from json string
        JSONObject object = new JSONObject(json);
        JSONArray jArray = object.getJSONArray("result");
        for(int i=0;i<jArray.length();i++){
            JSONObject jasonobject = jArray.getJSONObject(i);
            String lat = jasonobject.getString("latitude");
            String lon = jasonobject.getString("longitude");
            String timestamp=jasonobject.getString("timestamp");
            //send these values to the locate function
            Intent a = new Intent(Homepage.this,Locate.class);
            a.putExtra("lat",lat);
            a.putExtra("lon",lon);
            a.putExtra("time",timestamp);
            startActivity(a);
        }
    }

    //history module
    private void history(){
        getJSON1("https://2db65f43.ngrok.io/locate.php?limit=3");
    }
    private void getJSON1(final String urlWebService){
        //urlWebService->url containing php script outputting the database data in json format
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                //Called before execution..may set a progress bar
                super.onPreExecute();
            }
            //display toast with json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                if(s==null){
                    Toast.makeText(Homepage.this, "You have no history to display", Toast.LENGTH_LONG).show();
                }else{
                    //you have cordinates
                    try {
                        showloc1(s);
                        //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                //creating a URL
                try {
                    URL url = new URL(urlWebService);
                    //open the url
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    //string builder object to read the data
                    InputStream inputStream = httpURLConnection.getInputStream();
                    //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        stringBuilder.append(json +"\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    //return the read string
                    return stringBuilder.toString().trim();
                }catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    private void showloc1(String json) throws JSONException{
        //extract the lat,lon from json string
        JSONObject object = new JSONObject(json);
        JSONArray jArray = object.getJSONArray("result");
        //String [] location ={};
        ArrayList<String> location = new ArrayList<String>();
        ArrayList<String> location2 = new ArrayList<String>();
        ArrayList<String> timers = new ArrayList<String>();
        for(int i=0;i<jArray.length();i++){
            JSONObject jasonobject = jArray.getJSONObject(i);
            String lat  = jasonobject.getString("latitude");
            String lon = jasonobject.getString("longitude");
            String time  = jasonobject.getString("timestamp");
            //String [] location =  {lat,lon};
            //location[i] = lat,lon;//location[0]={1.24,3.45}
            location.add(lat);//stores all the latitude values
            location2.add(lon);//stores all the longitude values
            timers.add(time);//stores all the longitude values
            Intent a = new Intent(Homepage.this,History.class);
            a.putExtra("latitude",location);
            a.putExtra("longitude",location2);
            a.putExtra("time",timers);
            startActivity(a);
        }
    }

    private void account(String username) {
        //load my account activity
        Intent intent = new Intent(Homepage.this,Account.class);
        intent.putExtra("username",username);
        startActivity(intent);
        //Toast.makeText(Homepage.this,"account function",Toast.LENGTH_LONG).show();
    }

    private void about() {
        //about the app
        Toast.makeText(Homepage.this,"About function",Toast.LENGTH_LONG).show();
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
//                        editor.clear();
//                        editor.commit();
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("NO",null).show();
    }

}
