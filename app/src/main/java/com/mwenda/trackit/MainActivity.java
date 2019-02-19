package com.mwenda.trackit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mwenda.trackit.App.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private EditText user_email;
    private EditText user_password;
    private ProgressDialog progressDialog;
    private String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //email/username & password
        user_email = (EditText)findViewById(R.id.editUsername);
        user_password = (EditText)findViewById(R.id.editPassword);
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
            String method="login";
            boolean isConnected = checkInternet(this);
            if(isConnected){
                //device has internet
                usrLogin(email,password);
//                BackgroundTask backgroundTask = new BackgroundTask(MainActivity.this);
//                MyWrapper myWrapper= new MyWrapper(method,email,email,password,"","","");
//                backgroundTask.execute(myWrapper);
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
                        //(true)login successful
                        Intent intent = new Intent(MainActivity.this, Homepage.class);
                        startActivity(intent);
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
}
