package com.codemazk.codboy.missing;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView signup;
    int MY_PERMISSIONS_REQUEST=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  MY_PERMISSIONS_REQUEST                    );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},  MY_PERMISSIONS_REQUEST                    );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.passowrd);
        login = (Button) findViewById(R.id.Login);
        signup = (TextView) findViewById(R.id.signup1);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ClsLogin().execute();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(LoginActivity.this, RegistraionActivity.class);
                startActivity(n);
                finish();
            }
        });
    }


    public class ClsLogin extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));

            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));



            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/userlogin.php", readfromserver.GET, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(LoginActivity.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Authenticating...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            pg.dismiss();
            try {
                if (Json.contains("[]")){
                    Toast.makeText(getApplication(),"You are not registered User",Toast.LENGTH_LONG).show();
                }
                else  if (Json != null) {
                    // try {
                    Log.e("json", Json);


                    try {

                        JSONArray jsonObj = new JSONArray(Json);
                        JSONObject jo;
                        for (int i = 0; i < jsonObj.length(); i++) {
                            jo = jsonObj.getJSONObject(i);

                            Log.e("id", jo.getString("_id").toString());
                            SharedPreferences UserDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
                            SharedPreferences.Editor edit = UserDetails.edit();
                            edit.putString("id", jo.getString("_id"));
                            edit.putString("name", jo.getString("_name"));
                            edit.putString("email", jo.getString("_email"));
                            edit.putString("mobile", jo.getString("_mobile"));
                            edit.putString("UrV", "U");

                            edit.apply();
                        }


                        Intent n = new Intent(LoginActivity.this, UserHome.class);
                        startActivity(n);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                } else {
                    Log.e("JSON Data", "Didn't receive any data from server!");
                }


                super.onPostExecute(result);
            }
            catch (Exception e){

            }
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vol, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent n = new Intent(LoginActivity.this, VolLogin.class);
            startActivity(n);
            finish();
            return true;
        }
        return true;
    }

}