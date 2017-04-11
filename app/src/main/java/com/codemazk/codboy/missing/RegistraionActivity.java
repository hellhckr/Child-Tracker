package com.codemazk.codboy.missing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class RegistraionActivity extends AppCompatActivity {

    EditText name,phone,password,email;
    Button Signup;

    String emailInput,emailPattern,MobilePattern,nameinput,phoneinput,passwordinput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        name=(EditText)findViewById(R.id.name);
        phone=(EditText)findViewById(R.id.Mobile);
        password=(EditText)findViewById(R.id.password);
        email=(EditText)findViewById(R.id.Email);
        Signup=(Button)findViewById(R.id.button3);
        MobilePattern = "[0-9]{10}";
        emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameinput=name.getText().toString().trim();
                phoneinput=phone.getText().toString().trim();
                passwordinput=password.getText().toString().trim();
                emailInput = email.getText().toString().trim();

                if(nameinput.isEmpty() ||emailInput.isEmpty()||phoneinput.isEmpty()||passwordinput.isEmpty()){
                    Toast.makeText(getApplication(),"Enter Empty fields",Toast.LENGTH_LONG).show();
                }
                else if(!phoneinput.matches(MobilePattern)){
                    Toast.makeText(getApplication(),"invalid Mobile number",Toast.LENGTH_LONG).show();
                }
                else if (!emailInput.matches(emailPattern)) {

                    Toast.makeText(getApplication(),"invalid email address",Toast.LENGTH_LONG).show();
                } else {
                    new ClsRegistraion().execute();
                }
            }
        });




    }

    public class ClsRegistraion extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("mobile", phone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));



            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/user.php", readfromserver.GET, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(RegistraionActivity.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Creating your Account...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (Json != null) {
                // try {
                Log.e("json",Json);
                if(Json.contains("please fill all values")){
                    message("Please fill all Fields",0);
                }else if(Json.contains("mobile or email already exist")){
                    message("Mobile or Email already exist",0);
                }else if(Json.contains("successfully registered")){
                    message("successfully registered",1);
                }
                //  JSONObject jsonObj = new JSONObject(Json);
                //  Results = jsonObj.getString("userid");


             /*   } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            pg.dismiss();
            super.onPostExecute(result);
        }

    }



    void message(String Message, final int status){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistraionActivity.this);
        alertDialogBuilder.setMessage(Message);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do what you want to do if user clicks ok
                        if(status==1){
                            Intent n=new Intent(RegistraionActivity.this,LoginActivity.class);
                            startActivity(n);
                            finish();
                        }else{

                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
