package com.davis.tyler.magpiehunt.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.IOnHuntDeleteResponse;
import com.davis.tyler.magpiehunt.R;

public class DialogAddHunt extends Dialog {
    private Button btn_yes;
    private Button btn_no;
    private Hunt hunt;
    private TextView txt_warning;
    private IOnHuntDeleteResponse response;
    private RecyclerView.ViewHolder viewHolder;
    private ActivityBase activity;



    public DialogAddHunt(Activity a) {
        super(a);
        activity = (ActivityBase)a;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_hunt_add);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btn_no = findViewById(R.id.btn_no);
        btn_yes = findViewById(R.id.btn_yes);
        txt_warning = findViewById(R.id.txt_warning);
        txt_warning.setText("Would you like to add"+hunt.getName()+" hunt to your collections?");
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onAddHuntEvent(hunt);

                dismiss();
                activity = null;
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                dismiss();
                activity = null;
            }
        });


    }

    public void setHunt(Hunt h){
        hunt = h;
        //TODO set img_badge here when you get CMS communicator imgs working...
    }

}
