package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

import java.util.List;
import java.util.Set;

public class CheckableSpinnerAdapter extends BaseAdapter {
    private Context context;
    private Set<Hunt> selected_items;
    private List<Hunt> all_items;
    private String header;
    private HuntManager huntManager;

    public CheckableSpinnerAdapter(Context context, HuntManager huntManager, Set<Hunt> selected_items){
        this.huntManager = huntManager;
        this.context = context;
        this.header = "FILTER";
        this.all_items = huntManager.getAllDownloadedUndeletedHunts();
        this.selected_items = selected_items;
    }
    public void updateSpinnerItems(){

        all_items = huntManager.getAllDownloadedUndeletedHunts();
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
        final  HuntHolder holder;
        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.checkable_spinner_item, viewGroup, false);

            holder = new HuntHolder();
            holder.title = view.findViewById(R.id.hunt_title);
            holder.checkBox = view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }
        else{
            holder = (HuntHolder) view.getTag();
        }

        if(i < 1){
            holder.checkBox.setVisibility(View.GONE);
            holder.title.setText(header);
        }
        else{
            final int pos = i -1;
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.title.setText(all_items.get(pos).getName());
            final Hunt hunt = all_items.get(pos);
            boolean isSelected = selected_items.contains(hunt);

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(isSelected);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){

                        hunt.setIsFocused(true);
                        selected_items.add(hunt);
                        //filter out displayed hunts on map by only having downloaded hunts in the selected list
                        selected_items = huntManager.getSelectedDownloadedHunts();

                    }
                    else {
                        hunt.setIsFocused(false);
                        selected_items.remove(hunt);
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

    private class HuntHolder{
        private TextView title;
        private CheckBox checkBox;

    }//end inner class: LandmarkHolder
}
