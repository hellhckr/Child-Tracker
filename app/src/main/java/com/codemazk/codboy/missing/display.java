package com.codemazk.codboy.missing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class display extends AppCompatActivity {

    Button call;
    String Name,phone;
    TextView name,add,place,bg,fone;
    ImageView profilr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Name= extras.getString("name");
            Name=Name.replace(".txt","");
        Log.e("f",Name);
        }
       // Name="abhinay.txt";

        call=(Button)findViewById(R.id.v);
        name=(TextView)findViewById(R.id.nam);
        add=(TextView)findViewById(R.id.addrs);
        place=(TextView)findViewById(R.id.pla);
        bg=(TextView)findViewById(R.id.bg);
        fone=(TextView)findViewById(R.id.nbr);
        profilr=(ImageView) findViewById(R.id.imageView4);

        new Getchild().execute();
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone!=null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone.toString().trim()));
                    startActivity(callIntent);
                }
            }
        });


    }


    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    public class Getchild extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("uid", Name));




            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/getfinger.php", readfromserver.GET, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(display.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Validating");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            pg.dismiss();
            if(Json.contains("[]")){
                Log.e("json",Json);



            }else {
                if (Json != null) {
                    // try {
                    Log.e("json", Json);


                    try {

                        JSONArray jsonObj = new JSONArray(Json);
                        JSONObject jo;
                        byte[] b= new byte[512];
                        for (int i = 0; i < jsonObj.length(); i++) {
                            jo = jsonObj.getJSONObject(i);

                            Log.e("id", jo.getString("_id").toString());
                           name.setText( jo.getString("_name").toString());
                            place.setText( jo.getString("_place").toString());
                          add.setText(jo.getString("_address").toString());
                           bg.setText(jo.getString("_blood").toString());
                           fone.setText(jo.getString("_mobile").toString());
                           phone=jo.getString("_mobile").toString();


                        profilr.setImageBitmap(StringToBitMap(jo.getString("_image").toString()));



                            //  CheckMatch(decoded,SearchFinger.this);
                          /*  if(r==-1) {
                                Intent n = new Intent(SearchFinger.this, ProfileActivity.class);
                                n.putExtra("id",id);
                                startActivity(n);
                                finish();
                            }else{
                                Toast.makeText(SearchFinger.this, "no match", Toast.LENGTH_SHORT).show();
                            }*/
                            //  public items(String id,String carname, String carnbr,String lati,String time,String date,String longi){
                        }




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("JSON Data", "Didn't receive any data from server!");
                }
            }

            super.onPostExecute(result);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent m=new Intent(getApplicationContext(),SearchFinger.class);
        startActivity(m);
        finish();
    }
}
