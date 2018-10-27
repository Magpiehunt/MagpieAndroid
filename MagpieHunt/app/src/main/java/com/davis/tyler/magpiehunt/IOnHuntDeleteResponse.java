package com.davis.tyler.magpiehunt;

import android.support.v7.widget.RecyclerView;

import com.davis.tyler.magpiehunt.Hunts.Hunt;

public interface IOnHuntDeleteResponse {
    public void onDelete(Hunt h,RecyclerView.ViewHolder viewHolder);
    public void onRestore();
}
