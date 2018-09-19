package com.davis.tyler.magpiehunt.Views;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Fragments.FragmentQuiz;
import com.davis.tyler.magpiehunt.R;

public class ViewCheckbox extends RelativeLayout implements CompoundButton.OnCheckedChangeListener{
    private View rootView;
    private TextView choice;
    private CheckBox checkBox;
    private FragmentQuiz listener;
    public ViewCheckbox(Context context) {
        super(context);
        rootView = inflate(context, R.layout.view_question, this);
        choice = rootView.findViewById(R.id.txt_answer);
        checkBox = rootView.findViewById(R.id.cb_answer);
        checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(this);
        choice.setText("Possible Answer");
    }



    public void setCheckBox(boolean b){
        System.out.println("setting checkbox to: "+b);
        checkBox.setChecked(b);
    }

    public boolean isChecked(){return checkBox.isChecked();}

    public void setAnswer(String s){
        choice.setText(s);
    }

    public void setListener(FragmentQuiz f){
        listener = f;
    }
    public String getAnswer(){return choice.getText().toString();}

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){
            if(listener != null){
                listener.clearCheckBoxes();
                compoundButton.setChecked(true);
            }
        }
    }

}
