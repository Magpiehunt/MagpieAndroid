package com.davis.tyler.magpiehunt.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;

public class ActivitySplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(getApplicationContext(), ActivitySignIn.class));
        finish();

    }
}
