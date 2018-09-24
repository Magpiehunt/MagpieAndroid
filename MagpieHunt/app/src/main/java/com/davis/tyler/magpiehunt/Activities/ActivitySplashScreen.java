package com.davis.tyler.magpiehunt.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.FileSystemManager;

import java.io.IOException;

public class ActivitySplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileSystemManager fm = new FileSystemManager();
        try{
            fm.initializationCheck(getApplicationContext());
        }catch(IOException e){

        }
        startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
        finish();

    }
}
