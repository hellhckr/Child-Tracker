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

public class RehabChildDetails extends AppCompatActivity {

    ImageView profile;
    TextView rehab,spot,desc,t1,t2,t3,t4,t5;
    Button Rfone,Vfone;
    String vfon,rfon,v;

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rehab_child_details);

        profile=(ImageView)findViewById(R.id.imageView4);
        rehab=(TextView) findViewById(R.id.nbr);
        t1=(TextView) findViewById(R.id.textView15);
        t2=(TextView) findViewById(R.id.textView16);
        t3=(TextView) findViewById(R.id.textView13);
        t4=(TextView) findViewById(R.id.textView8);
        t5=(TextView) findViewById(R.id.textView10);


        spot=(TextView) findViewById(R.id.addrs);
        desc=(TextView) findViewById(R.id.bg);

        Rfone=(Button)findViewById(R.id.r);
        Vfone=(Button)findViewById(R.id.v);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
          v =bundle.getString("v");

        }
        if(v.equals("v")){
            profile.setVisibility(View.INVISIBLE);
            Rfone.setVisibility(View.GONE);
            t1.setText("Volunteer Name");
            t2.setText("Volunteer Location");
            t3.setText("Volunteer Number");


            new Getvol().execute();
        }else{
            new Getchild().execute();
        }


        Rfone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rfon!=null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + rfon.toString().trim()));
                    startActivity(callIntent);
                }
            }
        });

        Vfone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vfon!=null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + vfon.toString().trim()));
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

                Log.e("id",id);
            nameValuePairs.add(new BasicNameValuePair("id", id));




            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/rehabchild.php", readfromserver.GET,nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(RehabChildDetails.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Downloading...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(Json.contains("[]")){
                Log.e("json",Json);
                Toast.makeText(getApplicationContext(),"There is no data",Toast.LENGTH_LONG).show();
            }else {
                if (Json != null) {
                    // try {
                    Log.e("json", Json);


                    try {

                        JSONArray jsonObj = new JSONArray(Json);
                        JSONObject jo;
                        for (int i = 0; i < jsonObj.length(); i++) {
                            jo = jsonObj.getJSONObject(i);

                            Log.e("id", jo.getString("_id").toString());

                            String id=jo.getString("_id").toString();
                            String rehabnam=jo.getString("_rehab").toString();
                            String place=jo.getString("_place").toString();
                            rfon=jo.getString("_rfone").toString();
                            vfon=jo.getString("_vfone").toString();
                            String image=jo.getString("_image").toString();
                            String de=jo.getString("_desc").toString();

                                rehab.setText(rehabnam);
                            spot.setText(place);

                            profile.setImageBitmap(StringToBitMap(image));
                            desc.setText(de);
                            //  public items(String id,String carname, String carnbr,String lati,String time,String date,String longi){
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("JSON Data", "Didn't receive any data from server!");
                }
            }
            pg.dismiss();
            super.onPostExecute(result);
        }

    }
    public class Getvol extends AsyncTask<String, String, String> {

        String Json;
        String Results;
        ProgressDialog pg;

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            readfromserver jsonParser = new readfromserver();
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            Log.e("id",id);
            nameValuePairs.add(new BasicNameValuePair("id", id));




                Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/getvol.php", readfromserver.GET,nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(RehabChildDetails.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Downloading...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(Json.contains("[]")){
                Log.e("json",Json);
                Toast.makeText(getApplicationContext(),"There is no data",Toast.LENGTH_LONG).show();
            }else {
                if (Json != null) {
                    // try {
                    Log.e("json", Json);


                    try {

                        JSONArray jsonObj = new JSONArray(Json);
                        JSONObject jo;
                        for (int i = 0; i < jsonObj.length(); i++) {
                            jo = jsonObj.getJSONObject(i);



                            Log.e("id", jo.getString("_id").toString());

                            String id=jo.getString("_id").toString();
                            String pla=jo.getString("_name").toString();
                            String name=jo.getString("_place").toString();
                            String mob=jo.getString("_mobile").toString();


                            rehab.setText(mob);
                            spot.setText(pla);
                            vfon=mob;
                            desc.setText(name);

                            //  public items(String id,String carname, String carnbr,String lati,String time,String date,String longi){
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("JSON Data", "Didn't receive any data from server!");
                }
            }
            pg.dismiss();
            super.onPostExecute(result);
        }

    }

}
