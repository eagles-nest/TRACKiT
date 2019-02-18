package com.mwenda.trackit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.mwenda.trackit.R.id.textLogin;
import static com.mwenda.trackit.R.id.textRegister;

public class Register extends AppCompatActivity {
    private EditText user_email;
    private EditText user_ownPhone;
    private EditText user_username;
    private EditText user_gsmPhone;
    private EditText user_password,user_cpassword;
    private TextView user_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //get the values
        user_email=(EditText)findViewById(R.id.editEmail);
        user_ownPhone=(EditText)findViewById(R.id.editownPhone);
        user_username=(EditText)findViewById(R.id.editUsername);
        user_gsmPhone=(EditText)findViewById(R.id.editgsmPhone);
        user_password=(EditText)findViewById(R.id.editPassword);
        user_cpassword=(EditText)findViewById(R.id.editCPassword);
    }
    //register the user
    public void register(View view){
        String method="register";
        String email=user_email.getText().toString().trim();
        String username=user_username.getText().toString().trim();
        String ownPhone=user_ownPhone.getText().toString().trim();
        String gsmPhone=user_gsmPhone.getText().toString().trim();
        String password=user_password.getText().toString().trim();
        String cpassword=user_cpassword.getText().toString().trim();

        //process the fields
        if(email.isEmpty() ||username.isEmpty() || password.isEmpty() ||cpassword.isEmpty() || ownPhone.isEmpty() || gsmPhone.isEmpty()){
            //one or more of the strings is empty
            String errorMsg="Please fill all fields";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //all fields filled->check internen connection\
            boolean isConnected = checkInternet(this);
            if(isConnected){
                //has internet ->check if password && cpassword match
                if(password.equals(cpassword)){
                    //register user
                    BackgroundTask backgroundTask = new BackgroundTask(this);
                    MyWrapper myWrapper = new MyWrapper(method,email,username,password,ownPhone,gsmPhone,"");
                    backgroundTask.execute(myWrapper);
                }else{
                    String errorMsg="Password and Confirm Password don't match";
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }else{
                //no internet
                String errorMsg="Error, please check your internet connection and try again";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
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
}
