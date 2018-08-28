package com.davis.tyler.magpiehunt.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import com.davis.tyler.magpiehunt.Fragments.FragmentSigninInitial;
import com.davis.tyler.magpiehunt.Fragments.FragmentWalkthroughContainer;
import com.davis.tyler.magpiehunt.R;

public class ActivitySignIn extends AppCompatActivity implements FragmentSigninInitial.FragmentSigninInitialListener{
    private FragmentSigninInitial mFragmentSigninInitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_gradient, null));
        mFragmentSigninInitial = new FragmentSigninInitial();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.currentfragment, mFragmentSigninInitial);

        ft.commit();
    }


    @Override
    public void swipedLeftEvent() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.currentfragment, new FragmentWalkthroughContainer());
        ft.addToBackStack(null);
        ft.commit();
    }
}
