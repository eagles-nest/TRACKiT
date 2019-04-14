package com.mwenda.trackit.Authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mwenda.trackit.App.Constants;
import com.mwenda.trackit.Homepage;
import com.mwenda.trackit.Locate;
import com.mwenda.trackit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private EditText user_email;
    private EditText user_password;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 200;
    SharedPreferences sp;
    Dialog myDialog;
    EditText etimei;
    Button btnenter;
    String imei,user_id,phone,gsm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=getSharedPreferences("login",MODE_PRIVATE);
        //if previously logged in..dont ask for email/password
        if(sp.contains("email")){
            startActivity(new Intent(MainActivity.this,Homepage.class));
            finish();   //finish current activity
        }
        //email/username & password
        user_email = (EditText)findViewById(R.id.editUsername);
        user_password = (EditText)findViewById(R.id.editPassword);
        myDialog = new Dialog(this);

    }
    //log the userin
    public void login(View view){
        //get user data
        String email=user_email.getText().toString().trim();
        String password=user_password.getText().toString().trim();
        //check if fields are empty
        if(email.isEmpty() || password.isEmpty()){
            //one of the fields is empty
            String errorMsg="Please fill all fields";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //proceed to authenticate user
            if(checkInternet(this)){
                //device has internet
                usrLogin(email,password);
            }else{
                //device has no internet connection
                String errorMsg="Error, please check your internet connection and try again";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void usrLogin(final String email, final String password) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String URL = Constants.LOGIN_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);

                    if(status){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("user_data");
                        phone = jsonObject1.optString("phone","");
                        gsm = jsonObject1.optString("gsm","");
                        imei = jsonObject1.optString("imei","");
                        user_id = jsonObject1.optString("user_id","");
                        Toast.makeText(MainActivity.this, ""+imei, Toast.LENGTH_SHORT).show();
                        //(true)login successful
                        //store details in SHARED PREFERENCES

                        /**DEMAND  location permissions**/
                        if(checkPerm()){
                           //good to go->check IMEI
                            switch (imei){
                                case("0"):
                                    Toast.makeText(MainActivity.this,"Update your gsm IMEI", Toast.LENGTH_LONG).show();
                                    //TODO lat long is empty->ask user to enter location physically
                                    updateIMEI(user_id,email);
                                    break;
                                default:
                                    SharedPreferences.Editor e = sp.edit();
                                    e.putString("email",email);
                                    e.putString("phone",phone);
                                    e.putString("gsm",gsm);
                                    e.putString("imei",imei);
                                    e.apply();
                                    Intent intent = new Intent(MainActivity.this, Homepage.class);
                                    startActivity(intent);
                                    finish();
                                    break;

                            }
                        }else{
                            //request permissions
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                        }

                    } else{
                        String reply = jsonObject.optString("message","");
                        Toast.makeText(MainActivity.this, reply, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //String errorM = jsonObject
                NetworkResponse response1 = error.networkResponse;
                if(response1 != null && response1.data != null){
                    String errorString = new String(response1.data);
                    try {
                        JSONObject jsonObject = new JSONObject(errorString);
                        String reply = jsonObject.optString("message","");
                        //reply = reply.replaceAll("[^\\w\\.\\@\\s]", "");

                        Toast.makeText(MainActivity.this,reply,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Log.e("login : ", e.toString());
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(strReq);
    }

    private void updateIMEI(final String user_id1, final String user_email) {
        myDialog.setContentView(R.layout.custompopup3);
        myDialog.setCanceledOnTouchOutside(false);
        etimei = (EditText) myDialog.findViewById(R.id.etimei);

        btnenter =(Button) myDialog.findViewById(R.id.btentercode);
        btnenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //run function to submit imei to db
                imei=etimei.getText().toString().trim();
                Toast.makeText(MainActivity.this,imei+ "\t"+user_id, Toast.LENGTH_SHORT).show();
                if(!imei.isEmpty()){
                    if(checkInternet(MainActivity.this)){
                        //has internet
                        //submitLoc(location,id);
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Saving IMEI...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        //todo add code to make api request to update user account with imei
                        //todo also save shared preferences data to sp

                        String URL = Constants.UPDATEIMEI_URL;//update  gsm IMEI
                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.optBoolean("success", false);
                                    Log.d("evanso", "onResponse: "+jsonObject.toString());

                                    if(status){
                                        //update successful
                                        String reply=jsonObject.optString("message");
                                        Toast.makeText(MainActivity.this, reply, Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor e = sp.edit();
                                        e.putString("email",user_email);
                                        e.putString("phone",phone);
                                        e.putString("gsm",gsm);
                                        e.putString("imei",imei);
                                        e.apply();
                                        Intent intent = new Intent(MainActivity.this, Homepage.class);
                                        startActivity(intent);
                                        finish();
                                    } else{
                                    //no results found
                                    String reply=jsonObject.optString("message");
                                    Toast.makeText(MainActivity.this, reply, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    // JSON error
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                //String errorM = jsonObject
                                NetworkResponse response1 = error.networkResponse;
                                if(response1 != null && response1.data != null){
                                    String errorString = new String(response1.data);
                                    try {
                                        JSONObject jsonObject = new JSONObject(errorString);
                                        Log.d("evanso", "onErrorResponse: "+errorString);;
                                        String reply = jsonObject.optString("message","");
                                        //reply = reply.replaceAll("[^\\w\\.\\@\\s]", "");

                                        Toast.makeText(MainActivity.this,reply,Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        // JSON error
                                        e.printStackTrace();
                                        Log.e("login : ", e.toString());
                                    }
                                }
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                // Posting parameters to login url
                                Map<String, String> params = new HashMap<>();
                                params.put("user_id", user_id1);
                                params.put("gsmIMEI", imei);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                        requestQueue.add(strReq);


                    }else{
                        //no internet
                        Toast.makeText(MainActivity.this, "Error,check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    //user forgot pass...reset it
    public void forgotPass(View view){
        //register activity called by an intent
        Intent intent = new Intent(MainActivity.this,ForgotPass.class);
        startActivity(intent);
    }

    //checks if internet connection available
    public boolean checkInternet(Context context){
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public void redirectRegister(View view) {
        //register activity called by an intent
        Intent intent = new Intent(MainActivity.this,Register.class);
        startActivity(intent);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        Intent intent = new Intent(MainActivity.this, Homepage.class);
                        startActivity(intent);
                        finish();
                    }else {
                        //permission denied
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("Location permissions are required for the app to work",
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
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
