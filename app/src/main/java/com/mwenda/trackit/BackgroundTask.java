package com.mwenda.trackit;

/**
* Created by NDANU on 10/11/2018.
*/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Wrapper;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
* Created by NDANU on 8/15/2018.
*/

public class BackgroundTask extends AsyncTask <MyWrapper,Void ,MyWrapper> {
Context context ;
ProgressDialog progressDialog;

    BackgroundTask(Context ctx){
    context=ctx;
}

    //

    @Override
    protected MyWrapper doInBackground(MyWrapper... myWrappers) {
        String method   = myWrappers[0].method;
        String email    = myWrappers[0].email;
        String username = myWrappers[0].username;
        String password = myWrappers[0].password;
        String ownPhone = myWrappers[0].ownPhone;
        String gsmPhone = myWrappers[0].gsmPhone;

        //the URLs
        String login_url = "https://evansmwendaem.000webhostapp.com/login.php";
        String reg_url = "https://evansmwendaem.000webhostapp.com/register.php";
        String update_url = "https://evansmwendaem.000webhostapp.com/update.php";

        if (method.equals("register")) {
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("email", "UTF-8")+"="+ URLEncoder.encode(email, "UTF-8")+"&"+
                        URLEncoder.encode("username", "UTF-8")+"="+ URLEncoder.encode(username, "UTF-8")+"&"+
                        URLEncoder.encode("password", "UTF-8")+"="+ URLEncoder.encode(password, "UTF-8")+"&"+
                        URLEncoder.encode("gsmPhone", "UTF-8")+"="+ URLEncoder.encode(gsmPhone, "UTF-8")+"&"+
                        URLEncoder.encode("ownPhone", "UTF-8")+"="+ URLEncoder.encode(ownPhone, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                MyWrapper w = new MyWrapper("","","","","","","");
                w.result=result;
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return w;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("login")){
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                MyWrapper w = new MyWrapper("login","","","","","","");
                w.result=result;
                w.username=username;
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return w;
                //return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method=="updateUser"){
            //update details of signed in user
            try {
                URL url = new URL(update_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data =URLEncoder.encode("password", "UTF-8")+"="+ URLEncoder.encode(password, "UTF-8")+"&"+
                        URLEncoder.encode("username", "UTF-8")+"="+ URLEncoder.encode(email, "UTF-8")+"&"+
                        URLEncoder.encode("gsmPhone", "UTF-8")+"="+ URLEncoder.encode(gsmPhone, "UTF-8")+"&"+
                        URLEncoder.encode("ownPhone", "UTF-8")+"="+ URLEncoder.encode(ownPhone, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                MyWrapper w = new MyWrapper("","","","","","","");
                w.result=result;
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return w;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onPreExecute(){
    progressDialog = new ProgressDialog(context);
    progressDialog.setMessage("Loading...");
    progressDialog.setTitle("TRACKiT");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.show();

}
protected void onPostExecute(MyWrapper w){
    String result=w.result;
    String username=w.username;

    switch (result){
        case("login_success"):
            //login successful
            Intent i = new Intent(context,Homepage.class);
            //TODO: code to save username into sharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("usr_username",username);
            editor.apply();
            context.startActivity(i);
            break;
        case("login_failed"):
            //login failed
            String errorMsg="Error, Incorrect username/password combination";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("user_null"):
            //login->user doesnt exist
            errorMsg="Error, Incorrect username/password combination";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("user_many"):
            //login->duplicate user accounts
            errorMsg="An error occurred, please consult with the system administrator for further assistance";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("user_exists"):
            //register->user already exists
            errorMsg="Username or Email Address already exists,please select another one ";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("register_success"):
            //register successful
            i = new Intent(context,MainActivity.class);
            context.startActivity(i);
            break;
        case("register_failed"):
            //register failed
            errorMsg="An error occured during registration, please try again";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("no_usernaeme"):
            //update->username not submitted
            errorMsg="An error occured, please try again later";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("update_success"):
            //UpdateUser successful
            errorMsg="Account details have been successfully updated";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("update_failed"):
            //UpdateUser failed
            errorMsg="Error,Failed to update account details, please try again";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("not_found"):
            //UpdateUser failed
            errorMsg="Error, A user with such an Email Address does not exist in the system";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("change_failed"):
            //UpdateUser failed
            errorMsg="Error,Failed to update account passsword, please try again later";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("change_success"):
            //UpdateUser failed
            errorMsg="Password change successful, log in to the app with the new password";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("user_found"):
            //UpdateUser failed
            errorMsg="Password change successful, log in to the app with the new password";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
    }

//            //create the user session variables
//            //SessionManagement session = new SessionManagement(context);
//            //session.createLoginSession(email);
//            //context.startActivity(new Intent("com.mwenda.trackit.home"));

//            //start the main activity through intent
////            Intent i = new Intent(context,MainActivity.class);
////            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            context.startActivity(i);
//            //context.startActivity(new Intent(".MainActivity"));
//        }
    progressDialog.dismiss();
    }
protected void onProgressUpdate(Void... values){
    super.onProgressUpdate(values);
}
}
