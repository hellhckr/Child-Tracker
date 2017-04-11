package com.codemazk.codboy.missing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class volentdetails extends AppCompatActivity {

    private ArrayList<Item> itemObj = new ArrayList<>();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volentdetails);

        list=(ListView)findViewById(R.id.details);
        new Getchild().execute();
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







            Json = jsonParser.makeServiceCall("http://project.codemazk.com/scms/missing/getvol.php", readfromserver.GET);
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pg = new ProgressDialog(volentdetails.this);
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
                            String name=jo.getString("_place").toString();
                            String mob=jo.getString("_mobile").toString();


                            itemObj.add(new Item(id,name,mob));
                            //  public items(String id,String carname, String carnbr,String lati,String time,String date,String longi){
                        }

                        DisplayAdapter disadpt = new DisplayAdapter(volentdetails.this, itemObj);

                        list.setAdapter(disadpt);


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

    private class DisplayAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;
        ArrayList<Item> itemobj = new ArrayList<>();
        Item itemobjtemp = null;

        public DisplayAdapter(Activity activity, ArrayList<Item> report) {
            this.itemobj = report;
            inflater = (LayoutInflater) activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // TODO Auto-generated constructor stub
        }
        public int getCount() {
            return itemobj.size();
        }

        public Item getItem(int position) {
            return itemobj.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {
            final DisplayAdapter.Holder mHolder;


            if (view == null) {

                view = inflater.inflate(R.layout.list_items, null);


                mHolder = new DisplayAdapter.Holder();
                //link to widgets
                mHolder.id = (TextView) view.findViewById(R.id.id);

                mHolder.name = (TextView) view.findViewById(R.id.nam);
                mHolder.mobile = (TextView) view.findViewById(R.id.mob);



                Item io = itemobj.get(pos);
                //  if(io.getA().equals("P"))



                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent n=new Intent(volentdetails.this,RehabChildDetails.class);
                        n.putExtra("id",mHolder.id.getText().toString());
                        n.putExtra("v","v");
                        startActivity(n);
                    }
                });
                view.setTag(mHolder);

            } else
                mHolder = (DisplayAdapter.Holder) view.getTag();

            if (itemobj.size() <= 0) {
                mHolder.name.setText("no_Data");
            } else {
                itemobjtemp = null;
                itemobjtemp = itemobj.get(pos);


                mHolder.id.setText(itemobjtemp.getIdd());


                mHolder.name.setText(itemobjtemp.getName());
                mHolder.mobile.setText(itemobjtemp.getMobile());


            }
            return view;

        }



        public class Holder {

            TextView name,  id,mobile;


        }

    }

}
