package com.codemazk.codboy.missing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    EditText name,mob,email,addre;
    Button add,call;
        String Name,Mobile,Email,Address,date,time,userid,U,UID,id;


    private static final int SELECT_PICTURE = 100;
    private static final int CAMERA_REQUEST = 1888;

    Uri selectedImageUri;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences child = getSharedPreferences("child", MODE_PRIVATE);
        userid = userDetails.getString("id", "");
        U = userDetails.getString("UrV", "");

        call=(Button) findViewById(R.id.button4);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            if(id!=null){
                new getchild().execute();
                child.edit().remove("child").commit();
            }
            // and get whatever type user account id is
        }

        Name = child.getString("name","");
        Mobile = child.getString("mobile","");

        Address = child.getString("address","");









        name=(EditText) findViewById(R.id.name);
        mob=(EditText) findViewById(R.id.number);
        email=(EditText) findViewById(R.id.email);
        addre=(EditText) findViewById(R.id.adress);
        add=(Button) findViewById(R.id.add);

        profile=(ImageView) findViewById(R.id.imageView2);


        if (Address != null) {


            name.setText(Name);
            mob.setText(Mobile);

            addre.setText(Address);


        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name=name.getText().toString();
                Mobile=mob.getText().toString();
                Email=email.getText().toString();
                Address=addre.getText().toString();


                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                date = df.format(c.getTime());
                Log.e("day", date);
                SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                Log.e("time", tf.format(c.getTime()).toString());
                time = tf.format(c.getTime()).toString();
                new Clsaddchild().execute();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+mob.getText().toString().trim()));
                startActivity(callIntent);
            }
        });


    }

    void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //   selectedImageUri = data.getData();
            //  profile.setImageURI(selectedImageUri);
            profile.setImageBitmap(photo);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                selectedImageUri = data.getData();

                if (null != selectedImageUri) {




                    profile.setImageURI(selectedImageUri);


                    // Reading from Database after 3 seconds just to show the message
                /*   new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loadImageFromDB()) {
                                showMessage("Image Loaded from Database...");
                            }
                        }
                    }, 3000);*/
                }

            }
        }
    }
    public class Clsaddchild extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.e("ns",name.getText().toString());
            nameValuePairs.add(new BasicNameValuePair("fname", Name.toString()));


            nameValuePairs.add(new BasicNameValuePair("email", Email.toString()));
            nameValuePairs.add(new BasicNameValuePair("mobile", Mobile.toString()));

            nameValuePairs.add(new BasicNameValuePair("idd", userid.toString()));
            nameValuePairs.add(new BasicNameValuePair("UrV", U.toString()));
            nameValuePairs.add(new BasicNameValuePair("date", date.toString()));
            nameValuePairs.add(new BasicNameValuePair("time", time.toString()));





            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/child.php", readfromserver.POST, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(ProfileActivity.this);
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
                    message("successfully added",1);
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        alertDialogBuilder.setMessage(Message);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do what you want to do if user clicks ok
                        if(status==1){

                        }else{

                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class getchild extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.e("ns",id.toString());
            nameValuePairs.add(new BasicNameValuePair("id", id.toString()));





            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/child.php", readfromserver.POST, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(ProfileActivity.this);
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


                try {
                    JSONObject jsonObj = new JSONObject(Json);
                    Name = jsonObj.getString("_name");
                    Mobile = jsonObj.getString("_Mobile");
                    name.setText(Name);
                    mob.setText(Mobile);
                    call.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

}
