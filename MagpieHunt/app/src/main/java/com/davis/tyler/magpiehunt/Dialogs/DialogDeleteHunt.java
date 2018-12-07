package com.davis.tyler.magpiehunt.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.IOnHuntDeleteResponse;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;

public class DialogDeleteHunt extends Dialog {
    private Button btn_yes;
    private Button btn_no;
    private Hunt hunt;
    private TextView txt_warning;
    private IOnHuntDeleteResponse response;
    private RecyclerView.ViewHolder viewHolder;



    public DialogDeleteHunt(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_hunt_delete);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btn_no = findViewById(R.id.btn_no);
        btn_yes = findViewById(R.id.btn_yes);
        txt_warning = findViewById(R.id.txt_warning);
        txt_warning.setText("Are you sure you want to delete the "+hunt.getName()+" hunt?");
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                response.onDelete(hunt, viewHolder);
                response = null;
                viewHolder = null;
                dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                dismiss();
                response.onRestore();
                response = null;
                viewHolder = null;
            }
        });


    }

    public void setHunt(Hunt h, IOnHuntDeleteResponse r, RecyclerView.ViewHolder viewHolder){
        hunt = h;
        response = r;
        this.viewHolder = viewHolder;
    }

}
