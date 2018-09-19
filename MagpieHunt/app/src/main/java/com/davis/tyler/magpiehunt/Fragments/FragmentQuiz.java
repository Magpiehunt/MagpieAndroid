package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Views.ViewCheckbox;

import java.util.LinkedList;

public class FragmentQuiz extends Fragment implements View.OnClickListener{
    public static final String TAG = "Fragmentquiz";

    private HuntManager mHuntManager;
    private LinearLayout layout_answers;
    private Button btn_submit;
    private TextView txt_question;
    private LinkedList<ViewCheckbox> checkboxes;
    LinkedList<LinkedList<String>> choices;
    LinkedList<String> questions;
    LinkedList<String> answers;
    private int question_num;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        txt_question = view.findViewById(R.id.txt_quiz_question);
        layout_answers = view.findViewById(R.id.layout_answers);
        reset();
        btn_submit = view.findViewById(R.id.btn_submit_answer);
        btn_submit.setOnClickListener(this);
        LinkedList<String> quiz_question;
        questions = new LinkedList<>();
        questions.add("Question one for our hypothetical quiz is right here, pick an answer.");
        questions.add("This is the second question.");
        answers = new LinkedList<>();
        answers.add("answer");
        answers.add("choice 2");
        choices = new LinkedList<>();
        quiz_question = new LinkedList<>();
        checkboxes = new LinkedList<>();
        quiz_question.add("possiblility");
        quiz_question.add("another possible answer");
        quiz_question.add("another choice");
        quiz_question.add("answer");
        choices.add(quiz_question);
        quiz_question = new LinkedList<>();
        quiz_question.add("choice 1");
        quiz_question.add("choice 2");
        quiz_question.add("choice 3");
        quiz_question.add("choice 4");
        choices.add(quiz_question);
        updateQuiz();

        return view;
    }
    public static FragmentQuiz newInstance(HuntManager huntManager) {
        FragmentQuiz f = new FragmentQuiz();
        Bundle args = new Bundle();
        args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    public void updateQuiz(){
        if(question_num < choices.size()) {
            LinkedList<String> a_list = choices.get(question_num);
            for (String s : a_list) {
                ViewCheckbox temp = new ViewCheckbox(getContext());
                temp.setAnswer(s);
                temp.setOnClickListener(this);
                temp.setListener(this);
                checkboxes.add(temp);
                layout_answers.addView(temp);
                txt_question.setText(questions.get(question_num));
            }
        }
        else{
            Fragment f = getParentFragment();
            mHuntManager.getFocusBadge().setmIsCompleted(true);

            if(f instanceof FragmentHome){
                ((FragmentHome) f).setFragment(FragmentHome.FRAGMENT_BADGE_OBTAINED);
            }
            else if(f instanceof FragmentMap){
                ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_BADGE_OBTAINED);
            }
        }
    }

    public void reset(){
        question_num = 0;
        clearQuestion();
    }

    public void clearQuestion(){
        if(checkboxes != null){
            for(ViewCheckbox cb: checkboxes)
                cb.setListener(null);
        }
        layout_answers.removeAllViews();
    }
    public void clearCheckBoxes(){
        for(ViewCheckbox cb: checkboxes){
            cb.setCheckBox(false);
        }
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Log.e(TAG, "setArguments, args: "+args);
        mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        Log.e(TAG, "setArguments, huntman: "+mHuntManager);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_submit_answer){
            boolean correct = false;
            for(ViewCheckbox cb: checkboxes){
                if(cb.isChecked()){
                    if(cb.getAnswer().equalsIgnoreCase(answers.get(question_num))){
                        correct = true;
                    }
                }
            }
            if(correct){
                answeredCorrectly();
            }
            else
                answeredIncorrectly();
        }
    }

    public void answeredIncorrectly(){
        Fragment f = getParentFragment();
        if(f instanceof FragmentHome){
            ((FragmentHome) f).setFragment(FragmentHome.FRAGMENT_TIMER);
        }
        else if(f instanceof FragmentMap){
            ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_TIMER);
        }
    }

    public void answeredCorrectly(){
        question_num++;
        clearQuestion();
        updateQuiz();
    }
}
