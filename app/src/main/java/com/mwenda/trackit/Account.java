package com.mwenda.trackit;

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
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mwenda.trackit.App.Constants;
import com.mwenda.trackit.Authentication.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Account extends AppCompatActivity {
    private String password,ownPhone,gsmPhone,gsmIMEI="";
    public String userName,phoneNum,gsmNum;
    private EditText txtpassword,txtownPhone,txtgsmPhone,txtgsmIMEI;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        sp=getSharedPreferences("login",MODE_PRIVATE);
        userName=sp.getString("email","");
        phoneNum=sp.getString("phone","");
        gsmNum=sp.getString("gsm","");
        gsmIMEI=sp.getString("imei","");

        //get the fields
        txtpassword=(EditText)findViewById(R.id.editPassword);
        txtownPhone=(EditText)findViewById(R.id.editownPhone);
        txtownPhone.setText(phoneNum);
        txtgsmPhone=(EditText)findViewById(R.id.editgsmPhone);
        txtgsmPhone.setText(gsmNum);
        txtgsmIMEI=(EditText)findViewById(R.id.editgsmIMEI);
        txtgsmIMEI.setText(gsmIMEI);

    }
    //check if internet is available
     public boolean checkInternet(Context context){
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    //update the user details
    public void updateDetails(View view){
        //get the field data
        password=txtpassword.getText().toString().trim();
        ownPhone=txtownPhone.getText().toString().trim();
        gsmPhone=txtgsmPhone.getText().toString().trim();
        gsmIMEI=txtgsmIMEI.getText().toString().trim();

        //check the fields if empty
        if(ownPhone.isEmpty() || gsmPhone.isEmpty() || gsmIMEI.isEmpty()){
            //one of the above fields is empty..when it should not
            String errorMsg="Error, Please fill all required fields(Phone& GSM)";
            Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //check if password is filled
            if(password.isEmpty()){
                //user doesnt want to change password
                if(checkInternet(this)){
                    updateUsr(ownPhone,gsmPhone,gsmIMEI);
                }else{
                    //device offline
                    String errorMsg="Error, Please check your internet connection and try again";
                    Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
                }
            }else{
                //usr wants to change password also
                if(checkInternet(this)){
                    updateUsr1(ownPhone,gsmPhone,gsmIMEI,password);
                }else{
                    //device offline
                    String errorMsg="Error, Please check your internet connection and try again";
                    Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void updateUsr(final String ownPhone,final String gsmPhone,final String gsmIMEI) {
        progressDialog = new ProgressDialog(Account.this);
        progressDialog.setMessage("Updating Account details ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String URL = Constants.UPDATEACCOUNT_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    String reply = jsonObject.optString("message","");
                    if(status){
                        //clearSharedPref();
                        progressDialog.dismiss();
                        //TODO ACCOUNT UPDATED SUCCESSFULLY
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("phone",ownPhone);
                        e.putString("gsm",gsmPhone);
                        e.putString("imei",gsmIMEI);
                        e.commit();
                        Toast.makeText(Account.this, reply, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Account.this,Homepage.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Account.this, reply, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Account.this,errorString,Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownPhone", ownPhone);
                params.put("gsmPhone", gsmPhone);
                params.put("gsmIMEI", gsmIMEI);
                params.put("email", userName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Account.this);
        requestQueue.add(strReq);
    }
    private void updateUsr1(final String ownPhone,final String gsmPhone,final String gsmIMEI,final String password) {
        progressDialog = new ProgressDialog(Account.this);
        progressDialog.setMessage("Updating Account details ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        String URL = Constants.UPDATEACCOUNT_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    String reply = jsonObject.optString("message","");
                    if(status){
                        progressDialog.dismiss();
                        //TODO ACCOUNT UPDATED SUCCESSFULLY
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("phone",ownPhone);
                        e.putString("gsm",gsmPhone);
                        e.putString("imei",gsmIMEI);
                        e.commit();
                        Toast.makeText(Account.this, reply, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Account.this,Homepage.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Account.this, reply, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Account.this,errorString,Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("ownPhone", ownPhone);
                params.put("gsmPhone", gsmPhone);
                params.put("gsmIMEI", gsmIMEI);
                params.put("password", password);
                params.put("email", userName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Account.this);
        requestQueue.add(strReq);
    }

}
