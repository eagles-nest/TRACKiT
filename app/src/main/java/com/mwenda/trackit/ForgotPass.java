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
import com.mwenda.trackit.Authentication.MainActivity;
import com.mwenda.trackit.Authentication.Register;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ForgotPass extends AppCompatActivity {
    private EditText txtUsername;
    private String email;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        txtUsername=(EditText) findViewById(R.id.editUsername);
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
                resetUsrPass(email);
            }else{
                String errorMsg="Error, Please check your internet connection and try again";
                Toast.makeText(ForgotPass.this,errorMsg, Toast.LENGTH_LONG).show();
            }
        }

    }

    private void resetUsrPass(final String email) {
        progressDialog = new ProgressDialog(ForgotPass.this);
        progressDialog.setMessage("Sending reset password link ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        String URL = Constants.RESETPASS_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(Signupuser.this, response, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ForgotPass.this,reply,Toast.LENGTH_LONG).show();
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
