package com.davis.tyler.magpiehunt.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;

public class DialogMoveCloser extends Dialog {
    private Button yes;
    private Badge mBadge;
    private ImageView img_badge;
    private Context context;



    public DialogMoveCloser(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_move_closer);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        yes = (Button) findViewById(R.id.btn_okay);
        img_badge = (ImageView)findViewById(R.id.img_badge);
        ImageManager im = new ImageManager();
        im.fillBadgeImage(context, mBadge, img_badge);
        context = null;
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                dismiss();
            }
        });


    }

    public void setBadge(Badge badge, Context context){
        mBadge = badge;
        this.context = context;

        //TODO set img_badge here when you get CMS communicator imgs working...
    }

}
