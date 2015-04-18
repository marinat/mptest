package com.example.multipleprocessestest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.util.Properties;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class Service4 extends Service {
    String TAG = "4Service";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
     
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onStartCommand method is called");
        for (int  i =0; i< 10; i++) {
            OutputStream os = null;
            File propsFile = new File(Environment.getExternalStorageDirectory() + "/.statist", "shared.prefs");
            if (!propsFile.exists()) {
                try {
                    propsFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, "file not created");
                }
            }
            int id = android.os.Process.myPid();
            Properties properties = new Properties();
            try {
                os = new FileOutputStream(propsFile);
                properties.setProperty(String.valueOf(id), String.valueOf(i));
                Log.e(TAG, "writed " + i + " FOR PID =  " + id);
                properties.store(os, null);
            } catch (IOException e) {
                Log.e(TAG, "io exception storing prefs", e);

            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG, "io exception closing output", e);
                    }
                }
            }
        }        
        return Service.START_NOT_STICKY;
    }
     
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.i(TAG,"onCreate Method is called");
        super.onCreate();
        
    }
 
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.i(TAG,"OnDestroy Method is called");
        super.onDestroy();
    }
 
    @Override
    public boolean stopService(Intent name) {
        // TODO Auto-generated method stub
        Log.i(TAG,"stopService Method is called");
        return super.stopService(name);
    }
 
}