package com.mwenda.trackit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Account extends AppCompatActivity {
    private String password,ownPhone,gsmPhone="";
    public String userName;
    private EditText txtpassword,txtownPhone,txtgsmPhone;
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
            //check internet
            boolean isConnected=checkInternet(Account.this);
            if(isConnected){
                //internet available
                //all required fields filled->check if password filled
                if(!password.isEmpty()){
                    //password has been changed->proceed to update
                    BackgroundTask backgroundTask = new BackgroundTask(Account.this);
                    MyWrapper wrapper=new MyWrapper(method,userName,"",password,ownPhone,gsmPhone,"");
                    backgroundTask.execute(wrapper);
                }else{
                    //user has not changed password
                    BackgroundTask backgroundTask = new BackgroundTask(Account.this);
                    MyWrapper wrapper=new MyWrapper(method,userName,"","",ownPhone,gsmPhone,"");
                    backgroundTask.execute(wrapper);
                }
            }else{
                //device offline
                String errorMsg="Error, Please check your internet connection and try again";
                Toast.makeText(Account.this,errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
