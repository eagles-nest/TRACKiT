package com.mwenda.trackit.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.mwenda.trackit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private EditText user_email;
    private EditText user_ownPhone;
    private EditText user_username;
    private EditText user_gsmPhone;
    private EditText user_password,user_cpassword;
    private TextView user_register;
    ProgressDialog progressDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //get the values
        user_email=(EditText)findViewById(R.id.editEmail);
        user_ownPhone=(EditText)findViewById(R.id.editownPhone);
        user_gsmPhone=(EditText)findViewById(R.id.editgsmPhone);
        user_password=(EditText)findViewById(R.id.editPassword);
        user_cpassword=(EditText)findViewById(R.id.editCPassword);
        //s
        sp=getSharedPreferences("login",MODE_PRIVATE);
    }
    //register the user
    public void register(View view){
        String method="register";
        String email=user_email.getText().toString().trim();
        String ownPhone=user_ownPhone.getText().toString().trim();
        String gsmPhone=user_gsmPhone.getText().toString().trim();
        String password=user_password.getText().toString().trim();
        String cpassword=user_cpassword.getText().toString().trim();

        //process the fields
        if(email.isEmpty() || password.isEmpty() ||cpassword.isEmpty() || ownPhone.isEmpty() || gsmPhone.isEmpty()){
            //one or more of the strings is empty
            String errorMsg="Please fill all fields";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //validate phones
            if(phoneValidation(ownPhone)&&phoneValidation(gsmPhone)){
                //phones valid->validate email
                if(emailValidation(email)){
                    //email valid->check password& confirm password
                    if (password.equals(cpassword)) {
                        //passwords match->validate password
                        if(passwordValidation(password)){
                            //password valid->check Internet connection
                            if(checkInternet(this)){
                                //internet available
                                regUser(email,ownPhone,gsmPhone,password);
                            }else{
                                //internet error
                                String errorMsg="Error, please check your internet connection and try again";
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        }else{
                            //invalid password
                            Toast.makeText(this, "Passwords should at least be 5 characters long", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //passwords dont match
                        Toast.makeText(this, "Password and Confirm Password don't match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //email invalid
                    Toast.makeText(this, "Please use a valid Email Address", Toast.LENGTH_SHORT).show();
                }
            }else{
                //phone numbers invalid
                Toast.makeText(this, "Please use valid phone number(s)", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void regUser(final String email,final String ownPhone,final String gsmPhone,final String password) {
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Registering ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        String URL = Constants.REGISTER_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                //Toast.makeText(Signupuser.this, response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                    if(status){
                        //(true)registration successful
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        //clear all shared preferences in the device
                        SharedPreferences.Editor e=sp.edit();
                        e.clear();
                        e.apply();
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
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
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                        //get the error messages
                        String emailErr=jsonObject1.optString("email","");
                        String phoneErr=jsonObject1.optString("phone","");
                        StringBuilder reply = new StringBuilder();


                        String[] errors = {emailErr, phoneErr};
                        for (int i = 0; i < errors.length; i++) {
                            if (!errors[i].isEmpty()) {
                                errors[i] = errors[i].replaceAll("[^\\w\\.\\@\\s]", "");
                                reply.append(errors[i] + "\t");
                            }
                        }
                        Toast.makeText(Register.this,reply,Toast.LENGTH_LONG).show();
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
                params.put("ownPhone", ownPhone);
                params.put("gsmPhone", gsmPhone);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
        requestQueue.add(strReq);
    }

    //checks if internet connection available
    public boolean checkInternet(Context context){
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    //redirects user to register screen
    public void redirectLogin(View view){
        Intent intent = new Intent(Register.this,MainActivity.class);
        startActivity(intent);
    }
    private boolean emailValidation(final String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@" +"(?:[a-zA-Z0-9-]+\\.)+[a-z" +"A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
    private boolean phoneValidation(final String phone){
        String passRegex = "^[0-9]{10,}+$";
        Pattern pat = Pattern.compile(passRegex);
        if (phone == null)
            return false;
        return pat.matcher(phone).matches();
    }
    private boolean passwordValidation(final String password) {
        String passRegex = "^[a-zA-Z0-9#?!@$%^&*+]{5,}+$";
        Pattern pat = Pattern.compile(passRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();

    }
}
