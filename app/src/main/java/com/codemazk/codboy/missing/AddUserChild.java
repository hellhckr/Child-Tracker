package com.codemazk.codboy.missing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddUserChild extends AppCompatActivity {

    EditText uid;
    Button search;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_child);
        uid=(EditText)findViewById(R.id.uid);
        search=(Button)findViewById(R.id.serh);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UID=uid.getText().toString();
              new  Getchild().execute();
            }
        });


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


            nameValuePairs.add(new BasicNameValuePair("uid",UID));






            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/searchuId.php", readfromserver.GET, nameValuePairs);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(AddUserChild.this);
            pg.setTitle("Please Wait");
            pg.setMessage("Authenticating...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if(Json.contains("[]")){
                Log.e("json",Json);
                Toast.makeText(getApplicationContext(),"Not Found",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

                startActivity(intent);
            }else {
                if (Json != null) {
                    // try {
                    Log.e("json", Json);
                    String id="",name="",mob="",place="";

                    try {

                        JSONArray jsonObj = new JSONArray(Json);
                        JSONObject jo;
                        for (int i = 0; i < jsonObj.length(); i++) {
                            jo = jsonObj.getJSONObject(i);

                            Log.e("id", jo.getString("id").toString());

                            SharedPreferences child = getSharedPreferences("child", MODE_PRIVATE);
                            SharedPreferences.Editor edit = child.edit();
                            edit.putString("id", jo.getString("id"));
                            edit.putString("name", jo.getString("_name"));
                            edit.putString("address", jo.getString("_address"));

                            edit.putString("mobile", jo.getString("_mobile"));



                            edit.apply();



                            //  public items(String id,String carname, String carnbr,String lati,String time,String date,String longi){
                        }

                        Intent intent = new Intent(AddUserChild.this, ProfileActivity.class);


                        startActivity(intent);

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
