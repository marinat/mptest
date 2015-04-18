package com.example.multipleprocessestest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Properties;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    static String TAG = "activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*for (int  i =0; i< 10; i++) {
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
                properties.setProperty("act", String.valueOf(i));
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
        }*/

        Intent s1Intent = new Intent(this, Service1.class);
        startService(s1Intent);
        
        /*Intent s2Intent = new Intent(this, Service2.class);
        startService(s2Intent);
        
        Intent s3Intent = new Intent(this, Service3.class);
        startService(s3Intent);
        
        Intent s4Intent = new Intent(this, Service4.class);
        startService(s4Intent);
        
        Intent s5Intent = new Intent(this, Service5.class);
        startService(s5Intent);
        
        Intent s6Intent = new Intent(this, Service6.class);
        startService(s6Intent);*/

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


        /*
        FileOutputStream os;
        Properties properties = new Properties();
        try {
            os = new FileOutputStream(propsFile);
            FileLock lock = ((FileOutputStream) os).getChannel().lock();
            FileInputStream in = null;
            try {
                in = new FileInputStream(propsFile);
                properties.load(in);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                lock.release();
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG, "io exception closing output", e);
                    }
                }
                in.close();

            }
            Log.e(TAG, "PROP = " + properties.getProperty("ser1"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        */


        Properties properties = new Properties();
        try {
            FileChannel channel = new RandomAccessFile(propsFile, "rw").getChannel();
            FileLock lock = channel.lock();

            InputStream in = Channels.newInputStream(channel);
            properties.load(in);

            lock.release();
            channel.close();

            Log.e(TAG, "PROP = " + properties.getProperty("ser1"));
        } catch (Exception e) {
            Log.e(TAG, "Error while get PROP: " + e.toString());
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
