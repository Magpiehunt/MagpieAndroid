package com.davis.tyler.magpiehunt.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.FileSystemManager;

import java.io.IOException;

public class ActivitySplashScreen extends AppCompatActivity {
    SharedPreferences sharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileSystemManager fm = new FileSystemManager();
        sharedPreferences = getSharedPreferences("userdetails", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            try{
                fm.clearFileSystem(this);
            }catch(Exception e){
                e.printStackTrace();
            }
            sharedPreferences.edit().putBoolean("firstrun", false).commit();
        }

        try{
            fm.initializationCheck(getApplicationContext());
        }catch(IOException e){

        }
        startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
        finish();

    }
}
