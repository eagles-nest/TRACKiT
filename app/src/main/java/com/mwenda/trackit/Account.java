package com.mwenda.trackit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Account extends AppCompatActivity {
    private String password,ownPhone,gsmPhone="";
    public String userName;
    private EditText txtpassword,txtownPhone,txtgsmPhone;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //get the fields
        txtpassword=(EditText)findViewById(R.id.editPassword);
        txtownPhone=(EditText)findViewById(R.id.editownPhone);
        txtgsmPhone=(EditText)findViewById(R.id.editgsmPhone);
        Intent i = getIntent();
        userName =  i.getStringExtra("username");

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
        String method="updateUser";
        password=txtpassword.getText().toString().trim();
        ownPhone=txtownPhone.getText().toString().trim();
        gsmPhone=txtgsmPhone.getText().toString().trim();

        //check the fields if empty
        if(ownPhone.isEmpty() || gsmPhone.isEmpty()){
            //one of the above fields is empty..when it should not
            String errorMsg="Error, Please fill all required fields(Phone& GSM)";
            Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //check if password is filled
            if(password.isEmpty()){
                //user doesnt want to change password
                if(checkInternet(this)){
                    updateUsrDetails(ownPhone,gsmPhone,"");
                }else{
                    //device offline
                    String errorMsg="Error, Please check your internet connection and try again";
                    Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
                }
            }else{
                //usr wants to change password
                if(checkInternet(this)){
                    updateUsrDetails(ownPhone,gsmPhone,password);
                }else{
                    //device offline
                    String errorMsg="Error, Please check your internet connection and try again";
                    Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void updateUsrDetails(final String ownPhone,final String gsmPhone,final String password) {
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
                    JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                    if(status){
                        progressDialog.dismiss();
                        //TODO ADD CODE TO DISPLAY EMAIL LINK SENT
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
                        Toast.makeText(Account.this,reply,Toast.LENGTH_LONG).show();
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
                params.put("ownPhone", ownPhone);
                params.put("gsmPhone", gsmPhone);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Account.this);
        requestQueue.add(strReq);
    }
}
