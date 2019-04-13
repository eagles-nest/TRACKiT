package com.mwenda.trackit.Authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.mwenda.trackit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ForgotPass extends AppCompatActivity {
    private EditText txtUsername,editcode;
    private String email;
    ProgressDialog progressDialog;
    Dialog myDialog;
    Button btnenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        txtUsername=(EditText) findViewById(R.id.editUsername);
        myDialog = new Dialog(this);
    }
    protected void resetPass(View view){
        //reset the user password->prepare email checks
        email=txtUsername.getText().toString().trim();
        if(email.isEmpty()||!emailValidation(email)){
            //email empty or invalid
            String errorMsg="Error, Please provide a valid Email Address";
            Toast.makeText(ForgotPass.this,errorMsg, Toast.LENGTH_LONG).show();
        }else{
            if(checkInternet(this)){
                //internet available
                getResetCode(email);
            }else{
                String errorMsg="Error, Please check your internet connection and try again";
                Toast.makeText(ForgotPass.this,errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getResetCode(final String email) {
        progressDialog = new ProgressDialog(ForgotPass.this);
        progressDialog.setMessage("Sending reset password link ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        String URL = Constants.RESETPASS_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    String reply=jsonObject.optString("message","");
                    final String id=jsonObject.optString("id","");

                    if(status){
                        Toast.makeText(ForgotPass.this, reply, Toast.LENGTH_LONG).show();

                        myDialog.setContentView(R.layout.custompopup);
                        myDialog.setCanceledOnTouchOutside(false);
                        editcode = (EditText) myDialog.findViewById(R.id.etcode);
                        btnenter = (Button) myDialog.findViewById(R.id.btentercode);
                        btnenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //run function to submit code to db
                                String code=editcode.getText().toString().trim();
                                if(!code.isEmpty()){
                                    if(checkInternet(ForgotPass.this)){
                                        //has internet
                                        submitCode(code,id);
                                    }else{
                                        //no internet
                                        Toast.makeText(ForgotPass.this, "Error,check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ForgotPass.this, "Enter Reset Code first", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                    }else{
                        Toast.makeText(ForgotPass.this, reply, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ForgotPass.this, errorString, Toast.LENGTH_SHORT).show();

                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("forgot", "0");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPass.this);
        requestQueue.add(strReq);
    }

    private void submitCode(final String code, final String id) {
        progressDialog = new ProgressDialog(ForgotPass.this);
        progressDialog.setMessage("Sending code...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String URL = Constants.SENDRESETCODE_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    if(status){
                        //code valid...sent to ResetPassword class
                        Intent intent = new Intent(ForgotPass.this, ResetPass.class);
                        intent.putExtra("user_id",id);
                        startActivity(intent);
                    }else{
                        String reply=jsonObject.optString("message","");
                        //code does NOT EXIST..display toast and exit
                        Toast.makeText(ForgotPass.this, reply, Toast.LENGTH_SHORT).show();
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
                    String errorString = new String(response1.data);//An error occurred,please try again?
                    Toast.makeText(ForgotPass.this, errorString, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", code);
                params.put("user_id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPass.this);
        requestQueue.add(strReq);
    }


    //check if internet is available
    public boolean checkInternet(Context context){
        ConnectivityManager cm
                =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    private boolean emailValidation(final String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+"[a-zA-Z0-9_+&*-]+)*@" +"(?:[a-zA-Z0-9-]+\\.)+[a-z" +"A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
