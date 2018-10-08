package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.davis.tyler.magpiehunt.Hunts.HuntManager;

public class FragmentDataHunts extends Fragment {

    // data object we want to retain
    private HuntManager data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(HuntManager data) {
        this.data = data;
    }

    public HuntManager getData() {
        return data;
    }
}
