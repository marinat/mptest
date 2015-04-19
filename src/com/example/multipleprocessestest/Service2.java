package com.example.multipleprocessestest;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

public class Service2 extends Service {
    static String TAG = "1Service";

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
            FileOutputStream os;
            Properties properties = new Properties();
            try {
                os = new FileOutputStream(propsFile);
                FileChannel channel = os.getChannel();
                Log.e(TAG, "starting lock write");
                FileLock lock = channel.lock();
                Log.e(TAG, "finished lock write");
                try {
                    properties.setProperty("contention", String.valueOf(i));
                    Log.e(TAG, "write property contention: " + i);
                    properties.store(os, null);
                } catch (IOException e) {
                    Log.e(TAG, "io exception storing prefs", e);
                } finally {
                    Log.e(TAG, "starting unlock write");
                    lock.release();
                    Log.e(TAG, "finished unlock write");
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG, "io exception closing output", e);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Read properties
        Properties properties = new Properties();
        try {
            FileChannel channel = new RandomAccessFile(propsFile, "rw").getChannel();
            Log.e(TAG, "starting lock read");
            FileLock lock = channel.lock();
            Log.e(TAG, "finished lock read");
            InputStream in = null;
            try {
                in = Channels.newInputStream(channel);
                properties.load(in);
                Log.e(TAG, "PROP = " + properties.getProperty("contention"));
            } catch (Exception e) {
                Log.e(TAG, "exception while reading properties", e);
            } finally {
                Log.e(TAG, "starting unlock read");
                lock.release();
                Log.e(TAG, "finished unlock read");
                if (in != null) {
                    in.close();
                }
                channel.close();
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