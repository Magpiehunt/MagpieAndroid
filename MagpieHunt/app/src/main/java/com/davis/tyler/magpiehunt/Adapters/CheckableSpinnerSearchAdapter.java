package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CheckableSpinnerSearchAdapter extends BaseAdapter {
    private static final String TAG = "CheckableSearch";
    private Context context;
    private String selected_item;
    private List<String> all_items;
    private List<FilterHolder> holders;
    private FilterHolder titleHolder;
    private ActivityBase listener;

    public CheckableSpinnerSearchAdapter(Context context){
        holders = new LinkedList<>();
        this.context = context;

        this.all_items = new LinkedList<>();
        all_items.add("CLOSEST TO ME");
        all_items.add("NUMBER OF BADGES");
        all_items.add("WALKING DISTANCE");
        this.selected_item = all_items.get(0);
    }
    @Override
    public int getCount() {
        return all_items.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if(i < 1)
            return null;
        else
            return all_items.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final CheckableSpinnerSearchAdapter.FilterHolder holder;
        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.checkable_spinner_item, viewGroup, false);

            holder = new CheckableSpinnerSearchAdapter.FilterHolder();
            holder.title = view.findViewById(R.id.hunt_title);
            holder.checkBox = view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }
        else{
            holder = (CheckableSpinnerSearchAdapter.FilterHolder) view.getTag();
        }

        if(i < 1){
            holders.add(holder);
            holder.checkBox.setVisibility(View.GONE);
            holder.title.setText(selected_item);
            titleHolder = holder;
            Log.e(TAG, "title holder set to: "+titleHolder);
        }
        else{
            final int pos = i -1;
            holders.add(holder);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.title.setText(all_items.get(pos));
            final String filter = all_items.get(pos);
            boolean isSelected = selected_item.equals(filter);

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(isSelected);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                        for(FilterHolder h: holders){
                            if(!holder.equals(h)){
                                h.checkBox.setChecked(false);
                            }
                        }
                        selected_item = filter;
                        holders.get(0).title.setText(selected_item);
                        titleHolder.title.setText(selected_item);
                        Log.e(TAG,"checked: "+filter+" holder is: "+titleHolder);

                    }
                    else {
                    }
                }
            });

            holder.title.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    holder.checkBox.toggle();
                }
            });
        }
        return view;
    }

    private class FilterHolder{
        private TextView title;
        private CheckBox checkBox;

    }//end inner class: LandmarkHolder
}
