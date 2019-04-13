package com.mwenda.trackit.Authentication;

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

public class ResetPass extends AppCompatActivity {
    EditText passTxt,cpassTxt;
    String user_id;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        passTxt=(EditText)findViewById(R.id.editpassword);
        cpassTxt=(EditText)findViewById(R.id.editcpassword);
        Intent intent = getIntent();
        user_id=intent.getStringExtra("user_id");
    }

    public void setPass(View view) {
        //get the field data
        String pass=passTxt.getText().toString().trim();
        String cpass=cpassTxt.getText().toString().trim();
        if(pass.isEmpty()|| cpass.isEmpty()){
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
        }else{
            //validate password
            if(pass.equals(cpass)){
                if(passwordValidation(pass)){
                    //check internet
                    if(checkInternet(this)){
                        //submit pass
                        submitPass(user_id,pass);
                    }else{
                        Toast.makeText(this, "Error,check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(this, "Password and Confirm Password don't match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitPass(final String user_id,final String pass) {
        progressDialog = new ProgressDialog(ResetPass.this);
        progressDialog.setMessage("Updating password...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String URL = Constants.UPDATEPASS_URL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.optBoolean("success", false);
                    if(status){
                        String msg=jsonObject.optString("message","Password change successful");
                        Toast.makeText(ResetPass.this, msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPass.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(ResetPass.this, "An error occurred,please try again", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ResetPass.this,errorString,Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pass", pass);
                params.put("user_id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ResetPass.this);
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
    private boolean passwordValidation(final String password) {
        String passRegex = "^[a-zA-Z0-9#?!@$%^&*+]{5,}+$";
        Pattern pat = Pattern.compile(passRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();

    }
}
