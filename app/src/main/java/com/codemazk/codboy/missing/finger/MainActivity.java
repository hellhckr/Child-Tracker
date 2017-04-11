package com.codemazk.codboy.missing.finger;


/**
 * *****************************************************************************
 * C O P Y R I G H T  A N D  C O N F I D E N T I A L I T Y  N O T I C E
 * <p>
 * Copyright Â© 2008-2009 Access Computech Pvt. Ltd. All rights reserved.
 * This is proprietary information of Access Computech Pvt. Ltd.and is
 * subject to applicable licensing agreements. Unauthorized reproduction,
 * transmission or distribution of this file and its contents is a
 * violation of applicable laws.
 * *****************************************************************************
 * <p>
 * project FM220_Android_SDK
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.codemazk.codboy.missing.R;
import com.startek.fm210.tstlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements FM220_Scanner_Interface {
    private acpl_FM220_SDK FM220SDK;

    private Button Capture_No_Preview,Capture_PreView,Capture_BackGround,sample,match,enroll;
    private TextView textMessage;
    private ImageView imageView;
    public tstlib Ts=new tstlib();



    byte[] fd;
    private static final String Telecom_Device_Key = "ACPLDEMO";

    //region USB intent and functions

    private UsbManager manager;
    private PendingIntent mPermissionIntent;
    private UsbDevice usb_Dev;
    private static final String ACTION_USB_PERMISSION = "com.ACPL.FM220_Telecom.USB_PERMISSION";
    byte [] isotem;
    OutputStream stream;

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
                    textMessage.setText("FM220 disconnected");
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
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                }
                            }
                        }
                    } else {
                        textMessage.setText("User Blocked USB connection");
                        textMessage.setText("FM220 ready");
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
                            Toast.makeText(context,"Wrong device type application restart required!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
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
                                textMessage.setText("FM220 requesting permission");
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
        setContentView(R.layout.activity_main);


//        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
        textMessage = (TextView) findViewById(R.id.textMessage);
        Capture_PreView = (Button) findViewById(R.id.button2);
        Capture_No_Preview = (Button) findViewById(R.id.button);
        Capture_BackGround= (Button) findViewById(R.id.button3);
        enroll=(Button)findViewById(R.id.enroll);
        match=(Button)findViewById(R.id.match);
        sample= (Button) findViewById(R.id.sample);
        imageView = (ImageView)  findViewById(R.id.imageView);

        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckMatch(isotem,MainActivity.this);
            }
        });

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile(isotem,MainActivity.this);
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
                    textMessage.setText("FM220 requesting permission");
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    Intent intent = this.getIntent();
                    if (intent != null) {
                        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            finishAffinity();
                        }
                    }
                    fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                    if (res.getResult()) {
                        textMessage.setText("FM220 ready. "+res.getSerialNo());
                        EnableCapture();
                    }
                    else {
                        textMessage.setText("Error :-"+res.getError());
                        DisableCapture();
                    }
                }
                break;
            }
        }
        if (device == null) {
            textMessage.setText("Pl connect FM220");
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
        }


        sample.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Ts.FP_DestroyEnrollHandle();
    }

});

        Capture_BackGround.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                textMessage.setText("Pl wait..");
                imageView.setImageBitmap(null);
                FM220SDK.CaptureFM220(2);

            }
        });

        Capture_No_Preview.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                FM220SDK.CaptureFM220(2,true,false);
            }
        });

        Capture_PreView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                FM220SDK.CaptureFM220(2,true,true);
            }
        });
    }

    private void DisableCapture() {
        Capture_BackGround.setEnabled(false);
        Capture_No_Preview.setEnabled(false);
        Capture_PreView.setEnabled(false);
        imageView.setImageBitmap(null);
    }
    private void EnableCapture() {
        Capture_BackGround.setEnabled(true);
        Capture_No_Preview.setEnabled(true);
        Capture_PreView.setEnabled(true);
    }
    @Override
    public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    textMessage.setText(statusMessage);
                    textMessage.invalidate();
                }
                if (DisplayImage) {
                    imageView.setImageBitmap(ScanImage);
                    imageView.invalidate();
                }
            }
        });
    }

    @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized())  EnableCapture();
                if (result.getResult()) {
                    imageView.setImageBitmap(result.getScanImage());
                    isotem  = result.getISO_Template();   // ISO TEMPLET of FingerPrint.....
//                    isotem is byte value of fingerprints


                    textMessage.setText("Success NFIQ:"+ Integer.toString(result.getNFIQ())+"  SrNo:"+result.getSerialNo()+"iji"+isotem.length);
                } else {
                    imageView.setImageBitmap(null);
                    textMessage.setText(result.getError());
                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }

    private void CheckMatch(byte[] data,Context context){

        try {

            File path = context.getExternalFilesDir(null);
            File file=new File(path, "text.txt");

            int length = (int) file.length();
            Toast.makeText(context, String.valueOf(length), Toast.LENGTH_SHORT).show();
            byte[] bytes = new byte[length];

            FileInputStream in = new FileInputStream(file);
           /* BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));*/
            try {
                in.read(bytes);
            } finally {
                in.close();
            }
            in.close();


            int t = Ts.FP_ISOminutiaMatchEx(data,bytes);
            if(t==0){
                Toast.makeText(context, "Finger Print Matches", Toast.LENGTH_SHORT).show();
            }
            else if(t==-1){
                Toast.makeText(context, "Fingerptint Doesn't Matches", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,"Unknown Result Code = "+ String.valueOf(t), Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
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
