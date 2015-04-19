package com.example.multipleprocessestest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

public class MainActivity extends ActionBarActivity {
    static String TAG = "activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent s1Intent = new Intent(this, Service1.class);
        startService(s1Intent);

        Intent s2Intent = new Intent(this, Service2.class);
        startService(s2Intent);

        Intent s3Intent = new Intent(this, Service3.class);
        startService(s3Intent);

        Intent s4Intent = new Intent(this, Service4.class);
        startService(s4Intent);

        Intent s5Intent = new Intent(this, Service5.class);
        startService(s5Intent);

        Intent s6Intent = new Intent(this, Service6.class);
        startService(s6Intent);

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}