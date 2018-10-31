package com.mwenda.trackit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Wrapper;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private EditText user_email;
    private EditText user_password;
    private Button button_login;
    private TextView textLogin;
    private String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //user variables
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
                BackgroundTask backgroundTask = new BackgroundTask(MainActivity.this);
                MyWrapper myWrapper= new MyWrapper(method,email,email,password,"","","");
                backgroundTask.execute(myWrapper);
            }else{
                //device has no internet connection
                String errorMsg="Error, please check your internet connection and try again";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
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
