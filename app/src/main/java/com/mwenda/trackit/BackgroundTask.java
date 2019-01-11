package com.mwenda.trackit;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
        String login_url =  "https://evansmwendaem.000webhostapp.com/login.php";
        String reg_url =    "https://evansmwendaem.000webhostapp.com/register.php";
        String update_url = "https://evansmwendaem.000webhostapp.com/update.php";
        String reset_url  = "https://evansmwendaem.000webhostapp.com/reset.php";
        //ngrok urls
//        String login_url  = "http://2db65f43.ngrok.io/projects/trackit/login.php";
//        String reg_url    = "http://2db65f43.ngrok.io/projects/trackit/register.php";
//        String update_url = "http://2db65f43.ngrok.io/projects/trackit/update.php";


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
                MyWrapper w = new MyWrapper(method,"","","","",gsmPhone,result);
                w.result=result;
                w.gsmPhone=gsmPhone;
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
        else if(method=="reset"){
            //update details of signed in user
            try {
                URL url = new URL(reset_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data =URLEncoder.encode("email", "UTF-8")+"="+ URLEncoder.encode(email, "UTF-8");
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
                w.email=email;
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
protected void onPostExecute(MyWrapper wrapper){
    String result=wrapper.result;
    String username=wrapper.username;
    String gsmIMEI=wrapper.gsmPhone;
    String usrEmail=wrapper.email;
    //Toast.makeText(context,result, Toast.LENGTH_SHORT).show();

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
            // code to save gsmIMEI into sharedPreferences
            SharedPreferences sharedPref = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editorReg = sharedPref.edit();
            editorReg.putString("usr_gsmIMEI",gsmIMEI);
            editorReg.apply();
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
        case("mail_sent"):
            //UpdateUser failed
            errorMsg="Password Reset Mail sent to "+usrEmail+" . Check your inbox or spam folder";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
        case("mail_notSent"):
            //UpdateUser failed
            errorMsg="An error occured, please try again";
            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
            break;
//        case("not_found1"):
//            //UpdateUser failed
//            errorMsg="User Account Not Found.Contact Site Admin for further assistance";
//            Toast.makeText(context,errorMsg, Toast.LENGTH_LONG).show();
//            break;
    }


    progressDialog.dismiss();
    }
protected void onProgressUpdate(Void... values){
    super.onProgressUpdate(values);
}
}
