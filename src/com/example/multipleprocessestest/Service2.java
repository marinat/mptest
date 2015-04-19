package com.example.multipleprocessestest;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

public class Service2 extends Service {
    static String TAG = "2Service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand method is called");
        //int id = android.os.Process.myPid();

        File dir = new File(Environment.getExternalStorageDirectory(), ".statist");
        if (!dir.mkdir() && !dir.isDirectory()) {
            Log.e(TAG, "Error while creating directory: file with same name already exists.");
            //todo something meaningful here
        }

        File lockerFile = new File(dir, ".locker");
        if (!lockerFile.exists()) {
            try {
                lockerFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Locker not created");
                //todo something meaningful here
            }
        }

        File propsFile = new File(dir, "shared.prefs");
        if (!propsFile.exists()) {
            try {
                propsFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "File not created");
                //todo something meaningful here
            }
        }

        //Write properties
        for (int i = 0; i < 10; i++) {
            Properties properties = new Properties();
            FileOutputStream propsOutputStream;
            FileOutputStream lockerOutputStream;
            try {
                lockerOutputStream = new FileOutputStream(lockerFile);
                FileChannel channel = lockerOutputStream.getChannel();
                Log.e(TAG, "starting lock write");
                FileLock lock = channel.lock();
                Log.e(TAG, "finished lock write");

                propsOutputStream = new FileOutputStream(propsFile);
                try {
                    properties.setProperty("contention", String.valueOf(i));
                    Log.e(TAG, "write property contention: " + i);
                    properties.store(propsOutputStream, null);
                } catch (IOException e) {
                    Log.e(TAG, "io exception storing properties", e);
                } finally {
                    try {
                        propsOutputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "io exception closing output", e);
                    }
                    Log.e(TAG, "starting unlock write");
                    lock.release();
                    Log.e(TAG, "finished unlock write");
                    try {
                        lockerOutputStream.close();
                        channel.close();
                    } catch (IOException e) {
                        Log.e(TAG, "io exception closing lock output", e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Read properties
        Properties properties = new Properties();
        FileInputStream propsInputStream;
        FileOutputStream lockerOutputStream;
        try {
            lockerOutputStream = new FileOutputStream(lockerFile);
            FileChannel channel = lockerOutputStream.getChannel();
            Log.e(TAG, "starting lock read");
            FileLock lock = channel.lock();
            Log.e(TAG, "finished lock read");

            propsInputStream = new FileInputStream(propsFile);
            try {
                properties.load(propsInputStream);
                Log.e(TAG, "PROP = " + properties.getProperty("contention"));
            } catch (IOException e) {
                Log.e(TAG, "exception while reading properties", e);
            } finally {
                try {
                    propsInputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "io exception closing input", e);
                }
                Log.e(TAG, "starting unlock read");
                lock.release();
                Log.e(TAG, "finished unlock read");
                try {
                    lockerOutputStream.close();
                    channel.close();
                } catch (IOException e) {
                    Log.e(TAG, "io exception closing lock output", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception while acquiring file read lock", e);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate Method is called");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy Method is called");
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i(TAG, "stopService Method is called");
        return super.stopService(name);
    }

}