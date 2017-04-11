package com.codemazk.codboy.missing;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.startek.fm210.tstlib;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchFinger extends AppCompatActivity  implements FM220_Scanner_Interface {

    private acpl_FM220_SDK FM220SDK;
    public tstlib Ts=new tstlib();

    byte[] minu_code1 = new byte[512];
    byte[] decoded;
        int t;
        private Button Capture_PreView,add,verify;
    String fingerdata,id;

    private ImageView imageView;
    private static final String Telecom_Device_Key = "ACPLDEMO";
    byte [] isotem;

    //region USB intent and functions

    private UsbManager manager;
    private PendingIntent mPermissionIntent;
    private UsbDevice usb_Dev;
    private static final String ACTION_USB_PERMISSION = "com.ACPL.FM220_Telecom.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                int pid, vid;
                pid = device.getProductId();
                vid = device.getVendorId();
                if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                    FM220SDK.stopCaptureFM220();
                    FM220SDK.unInitFM220();
                    usb_Dev=null;
                    Message("FM220 disconnected");
                    DisableCapture();
                }
            }
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            int pid, vid;
                            pid = device.getProductId();
                            vid = device.getVendorId();
                            if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {

                                    Message("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    Message("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    } else {
                        Message("User Blocked USB connection");
                        Message("FM220 ready");
                        DisableCapture();
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // call method to set up device communication
                        int pid, vid;
                        pid = device.getProductId();
                        vid = device.getVendorId();
                        if ((pid == 0x8225)  && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                Message("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                   Message("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    Message("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        if (getIntent() != null) {
            return;
        }
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev==null) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call method to set up device communication & Check pid
                    int pid, vid;
                    pid = device.getProductId();
                    vid = device.getVendorId();
                    if ((pid == 0x8225)  && (vid == 0x0bca)) {
                        if (manager != null) {
                            if (!manager.hasPermission(device)) {
                               Message("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            }
//                            else {
//                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
//                                if (res.getResult()) {
//                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
//                                    EnableCapture();
//                                }
//                                else {
//                                    textMessage.setText("Error :-"+res.getError());
//                                    DisableCapture();
//                                }
//                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }



    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
            FM220SDK.unInitFM220();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    //endregion



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_finger);


//        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);

        imageView = (ImageView)  findViewById(R.id.imageView);
        Capture_PreView = (Button)  findViewById(R.id.button);
        add = (Button)  findViewById(R.id.button4);
        verify = (Button)  findViewById(R.id.button5);

        add.setVisibility(View.VISIBLE);
        verify.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n=new Intent(SearchFinger.this,Home.class);
                startActivity(n);
            }
        });
        verify.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // new Getchild().execute();
                CheckMatch(isotem,SearchFinger.this);
            }
        });
        //Region USB initialisation and Scanning for device
        SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
        boolean oldDevType = sp.getBoolean("FM220type", true);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter);
        UsbDevice device = null;
        for ( UsbDevice mdevice : manager.getDeviceList().values()) {
            int pid, vid;
            pid = mdevice.getProductId();
            vid = mdevice.getVendorId();
            boolean devType;
            if ((pid == 0x8225) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,true);
                devType=true;
            }
            else if ((pid == 0x8220) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,false);
                devType=false;
            } else {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                devType=oldDevType;
            }
            if (oldDevType != devType) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("FM220type", devType);
                editor.commit();
            }
            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                device  = mdevice;
                if (!manager.hasPermission(device)) {
                    Message("FM220 requesting permission");
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    try {
                        Intent intent = this.getIntent();
                        if (intent != null) {
                            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                                finishAffinity();
                            }

                        }
                        fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
                        if (res.getResult()) {
                            Message("FM220 ready. " + res.getSerialNo());
                            EnableCapture();
                        } else {
                            Message("Error :-" + res.getError());
                            DisableCapture();
                        }
                    }catch(Exception e){

                    }
                }
                break;
            }
        }
        if (device == null) {
            Message("Please connect FM220");
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
        }

        //endregion



            Capture_PreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisableCapture();
                    FM220SDK.CaptureFM220(2,true,true);
                }
            });




    }

    private void DisableCapture() {

        imageView.setImageBitmap(null);
    }
    private void EnableCapture() {

    }
    @Override
    public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    Message(statusMessage);
                   // textMessage.invalidate();
                }
                if (DisplayImage) {
                    imageView.setImageBitmap(ScanImage);
                    imageView.invalidate();
                }
            }
        });
    }

     void Message(String message) {
         View parentLayout = findViewById(R.id.root_view);
         Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                 .setAction("CLOSE", new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {

                     }
                 })
                 .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                 .show();
     }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
         @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized())  EnableCapture();
                if (result.getResult()) {
                    imageView.setImageBitmap(result.getScanImage());
                     isotem  = result.getISO_Template();

                   // writeToFile(isotem,SearchFinger.this);
                  //  fingerdata=getStringImage(result.getScanImage());

                    // ISO TEMPLET of FingerPrint.....
//                    isotem is byte value of fingerprints

                  //  textMessage.setText("Success No:"+Integer.toString(result.getNFIQ())+"  SrNo:"+result.getSerialNo()+" finger id : "+isotem.toString());
                } else {
                    imageView.setImageBitmap(null);
                   // textMessage.setText(result.getError());
                }
                imageView.invalidate();
               // textMessage.invalidate();
            }
        });
    }

    private void CheckMatch(byte[] data,Context context){

        try {

            File path = context.getExternalFilesDir(null);

            File[] listOfFiles = path.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println("File " + listOfFiles[i].getName());
                  //  File file=new File(path, "text.txt");
                    File file=new File(path, listOfFiles[i].getName());

                    int length = (int) file.length();
                  //  Toast.makeText(context, String.valueOf(length), Toast.LENGTH_SHORT).show();
                    byte[] bytes = new byte[length];

                    FileInputStream in = new FileInputStream(file);
                    try {
                        in.read(bytes);
                    } finally {
                        in.close();
                    }
                    in.close();


                     t = Ts.FP_ISOminutiaMatchEx(bytes,data);
                    if(t==0){
                        Toast.makeText(context, "Finger Print Matches", Toast.LENGTH_SHORT).show();
                Intent n = new Intent(SearchFinger.this, display.class);
                n.putExtra("name", listOfFiles[i].getName());
                startActivity(n);
                finish();
                        break;
                    }
                    else if(t==-1){
                     //   Toast.makeText(context, "Fingerptint Doesn't Matches", Toast.LENGTH_SHORT).show();
             /*   Intent n = new Intent(SearchFinger.this, Home.class);

                startActivity(n);
                finish();*/
                    }
                    else{
                      //  Toast.makeText(context,"Unknown Result Code = "+ String.valueOf(t), Toast.LENGTH_SHORT).show();
               /* Intent n = new Intent(SearchFinger.this, Home.class);

                startActivity(n);
                finish();*/
                    }
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            if(t!=0) {
                Intent n = new Intent(SearchFinger.this, Home.class);

                startActivity(n);
                finish();
            }

        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        finally {

        }
    }

    private void writeToFile(byte[] data,Context context) {
        try {

            File path = context.getExternalFilesDir(null);
            File file=new File(path, "text.txt");

            OutputStream stream=new FileOutputStream(file);
            stream.write(data);
            Toast.makeText(context, "Enrolled", Toast.LENGTH_SHORT).show();
            stream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        finally {

        }
    }

}















