package com.codemazk.codboy.missing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Add_Child extends AppCompatActivity {

    ImageView profile;
    EditText name,mobile,desc,rehab,rehabfone,place;
    Button sav;

    private static final int SELECT_PICTURE = 100;
    private static final int CAMERA_REQUEST = 1888;

    Uri selectedImageUri;
    Globle mGloble;

    private Bitmap bitmap;

    String MobilePattern = "[0-9]{10}";
    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String userid,U,date,time,nameinput,mobileinput,placeinput,rehabinput,image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__child);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(mGloble.minu_code1!=null){
            Toast.makeText(Add_Child.this,  String.valueOf(mGloble.minu_code1), Toast.LENGTH_SHORT).show();}

        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        userid = userDetails.getString("id", "");
        U = userDetails.getString("UrV", "");



        profile=(ImageView)findViewById(R.id.profile);
        name=(EditText) findViewById(R.id.fName);


        place=(EditText)findViewById(R.id.location);
        mobile=(EditText)findViewById(R.id.mobile);
        desc=(EditText)findViewById(R.id.bg);
        rehab=(EditText)findViewById(R.id.nbr);
        rehabfone=(EditText)findViewById(R.id.rehabFone);



        sav=(Button)findViewById(R.id.save);
        sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("ns",name.getText().toString());


                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                date = df.format(c.getTime());
                Log.e("day", date);
                SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                Log.e("time", tf.format(c.getTime()).toString());
                time = tf.format(c.getTime()).toString();

                nameinput=name.getText().toString().trim();
                mobileinput=mobile.getText().toString().trim();
                placeinput=place.getText().toString().trim();
                rehabinput=rehabfone.getText().toString().trim();
                image=getStringImage(bitmap);
                if(profile.getDrawable()==null){
                    Toast.makeText(getApplication(),"Insert photo of child",Toast.LENGTH_LONG).show();
                }else
                if(placeinput.isEmpty()||mobileinput.isEmpty()){
                    Toast.makeText(getApplication(),"Enter Empty fields",Toast.LENGTH_LONG).show();
                }
                else if(!mobileinput.matches(MobilePattern)||!rehabinput.matches(MobilePattern)){
                    Toast.makeText(getApplication(),"invalid Mobile number",Toast.LENGTH_LONG).show();
                }
                else {
                    Log.e("ns",name.getText().toString());
                    new Clsaddchild().execute();
                }

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openImageChooser();

                openCamera();
            }
        });

    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    void InsertData() throws IOException {

            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);

            byte[] inputData = Utils.getBytes(iStream);

        Toast.makeText(getApplicationContext(),"data inserted",Toast.LENGTH_LONG).show();
    }
    public String getStringByte(byte[] byt){
        String encodedByt = Base64.encodeToString(byt, Base64.DEFAULT);
        return encodedByt;
    }
    // Choose an image from Gallery
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            bitmap = (Bitmap) data.getExtras().get("data");
           selectedImageUri = data.getData();
           // profile.setImageURI(selectedImageUri);


             profile.setImageBitmap(bitmap);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                 selectedImageUri = data.getData();

                if (null != selectedImageUri) {

                    // Saving to Database...

                        Toast.makeText(getApplicationContext(),"Image Saved in Database...",Toast.LENGTH_LONG).show();


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
            nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("rehab", rehab.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("rfone", rehabfone.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("mobile", mobile.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("desc", desc.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("place", place.getText().toString()));

            nameValuePairs.add(new BasicNameValuePair("date", date.toString()));
            nameValuePairs.add(new BasicNameValuePair("time", time.toString()));
            nameValuePairs.add(new BasicNameValuePair("image",image));



            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/child.php", readfromserver.POST, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(Add_Child.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Adding Child...");
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Add_Child.this);
        alertDialogBuilder.setMessage(Message);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //do what you want to do if user clicks ok
                        if(status==1){
                            Intent n=new Intent(Add_Child.this,Home.class);
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
        Intent n=new Intent(Add_Child.this,Home.class);
        startActivity(n);
        finish();
    }

}
