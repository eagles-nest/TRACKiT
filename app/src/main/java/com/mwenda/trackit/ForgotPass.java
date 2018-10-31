package com.mwenda.trackit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPass extends AppCompatActivity {
    private EditText txtUsername;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        txtUsername=(EditText) findViewById(R.id.editUsername);
    }
    protected void resetPass(View view){
        //reset the user password->prepare email checks
        email=txtUsername.getText().toString().trim();
        if(email.isEmpty()){
            //email empty
            String errorMsg="Error, Please provide a valid Email Address";
            Toast.makeText(ForgotPass.this,errorMsg, Toast.LENGTH_LONG).show();
        }else{
            //email NOT empty->check if valid
            String method="reset";
            //check internet connection
            boolean isConnected = checkInternet(ForgotPass.this);
            if(isConnected){
                //device has internet connection->reset password
                BackgroundTask backgroundTask = new BackgroundTask(ForgotPass.this);
                MyWrapper myWrapper = new MyWrapper(method,email,"","","","","");
                backgroundTask.execute(myWrapper);
            }else{
                //device has no internet connection
                String errorMsg="Error, Please check your internet connection and try again";
                Toast.makeText(ForgotPass.this,errorMsg, Toast.LENGTH_LONG).show();
            }
        }

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
}
