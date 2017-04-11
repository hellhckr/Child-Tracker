package com.codemazk.codboy.missing.aadarentry;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codemazk.codboy.missing.MainHome;
import com.codemazk.codboy.missing.R;
import com.codemazk.codboy.missing.readfromserver;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;



    EditText name,place,address,uid,mobile,bg;
    Button save,fingerbtn;
    ImageView imageView;

    String Name,Place,Adress,Uid,Mobile,finger,imageString,BG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);


    /*    try {
            if (mGloble.DATA != null) {
                Toast.makeText(MainActivity.this, String.valueOf(mGloble.DATA), Toast.LENGTH_SHORT).show();
            }
        } catch(NullPointerException e){

            }*/


        imageView=(ImageView)findViewById(R.id.profile);
        name=(EditText) findViewById(R.id.name);
        place=(EditText) findViewById(R.id.place);
        address=(EditText) findViewById(R.id.address);
        uid=(EditText) findViewById(R.id.uid);
        bg=(EditText) findViewById(R.id.bg);
        mobile=(EditText) findViewById(R.id.mobile);

        uid.setEnabled(false);
        Bundle extras = getIntent().getExtras();
       // String uid;

        if (extras != null) {
            uid.setText(extras.getString("UID"));
            // and get whatever type user account id is
        }
        save=(Button)findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = name.getText().toString().trim();
                Place = place.getText().toString().trim();
                Adress = address.getText().toString().trim();
                Uid = uid.getText().toString().trim();
                BG = bg.getText().toString().trim();
                Mobile = mobile.getText().toString().trim();
                //finger=mGloble.DATA;
                //  finger=getStringByte(mGloble.minu_code1);
                imageString=getStringImage(bitmap);

               new adddata().execute();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }
    public String getStringByte(byte[] byt){
        String encodedByt = Base64.encodeToString(byt, Base64.DEFAULT);
        return encodedByt;
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
       /* intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
        Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(in, PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            bitmap = (Bitmap) data.getExtras().get("data");
            filePath = data.getData();
            // profile.setImageURI(selectedImageUri);


            imageView.setImageBitmap(bitmap);
        }

    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class adddata extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.e("ns",Name);

            nameValuePairs.add(new BasicNameValuePair("name",Name));
            nameValuePairs.add(new BasicNameValuePair("place", Place));
            nameValuePairs.add(new BasicNameValuePair("mobile",Mobile));
            nameValuePairs.add(new BasicNameValuePair("address", Adress));
            nameValuePairs.add(new BasicNameValuePair("image", imageString));
            nameValuePairs.add(new BasicNameValuePair("uid", Uid));
            nameValuePairs.add(new BasicNameValuePair("BG", BG));
          //  nameValuePairs.add(new BasicNameValuePair("finger", finger));
            nameValuePairs.add(new BasicNameValuePair("finffger", "fddf"));



                    Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/aadar.php", readfromserver.POST, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(MainActivity.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Adding data...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
                   pg.dismiss();
            if (Json != null) {
                // try {
                Log.e("json",Json);
                if(Json.contains("please fill all values")){
                   // message("Please fill all Fields",0);
                    Toast.makeText(MainActivity.this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
                }else if(Json.contains("mobile or email already exist")){
                    Toast.makeText(MainActivity.this, "mobile or email already exist", Toast.LENGTH_SHORT).show();
                   // message("Mobile or Email already exist",0);
                }else if(Json.contains("successfully registered")){
                    Toast.makeText(MainActivity.this, "successfully added", Toast.LENGTH_SHORT).show();
                   // message("successfully added",1);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent n=new Intent(MainActivity.this, MainHome.class);
        startActivity(n);
        finish();
    }
}