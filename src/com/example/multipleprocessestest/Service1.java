package com.example.multipleprocessestest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.util.Properties;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class Service1 extends Service {
    static String TAG = "1Service";
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
                FileLock lock = ((FileOutputStream) os).getChannel().lock();
                try {
                    properties.setProperty("ser1", String.valueOf(i));
                    Log.e(TAG, "writed " + i + " FOR PID =  " + id);
                    properties.store(os, null);
                } catch (IOException e) {
                    Log.e(TAG, "io exception storing prefs", e);

                } finally {
                    lock.release();
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            Log.e(TAG, "io exception closing output", e);
                        }
                    }
                    
                    Log.e(TAG, "success unlocked!");
                }
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /*try {
                FileOutputStream out = new FileOutputStream(mPropsFile);
                try {
                    FileLock lock = out.getChannel().lock();
                    try {
                        FileInputStream in = new FileInputStream(mPropsFile);
                        mProperties.load(in);
                        in.close();
                        Log.e(TAG, "success props load");
                    } catch (IOException e) {
                        Log.e(TAG, "io exception", e);
                    } finally {
                        lock.release();
                        Log.e(TAG, "success unlocked!");
                    }
                } catch (Exception e1) {
                    Log.e(TAG, "exception while lock", e1);
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e(TAG, "can't close stream", e);
                    }
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "file not found", e);
            }*/
        }        
        
        File dir = new File(
                Environment.getExternalStorageDirectory(), ".statist");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File propsFile = new File(dir, "shared.prefs");
        if (!propsFile.exists()) {
            try {
                propsFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "file not created");
            }
        }
        
        Properties properties = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(propsFile);
            properties.load(in);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e(TAG, "PROP = " +properties.getProperty("ser1"));
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