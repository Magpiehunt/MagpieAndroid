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

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Hunts.Question;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Views.ViewCheckbox;

import java.util.Collections;
import java.util.LinkedList;

public class FragmentQuiz extends Fragment implements View.OnClickListener{
    public static final String TAG = "Fragmentquiz";

    private HuntManager mHuntManager;
    private LinearLayout layout_answers;
    private Button btn_submit;
    private TextView txt_question;
    private LinkedList<ViewCheckbox> checkboxes;
    LinkedList<String> choices;
    private String answer;
    LinkedList<Question> questionList;
    private int question_num;
    private Badge badge;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();

        txt_question = view.findViewById(R.id.txt_quiz_question);
        layout_answers = view.findViewById(R.id.layout_answers);

        btn_submit = view.findViewById(R.id.btn_submit_answer);
        btn_submit.setOnClickListener(this);
        badge = mHuntManager.getFocusBadge();

        questionList = badge.getQuiz().getQuestions();
        choices = new LinkedList<>();
        checkboxes = new LinkedList<>();
        ((ActivityBase)getActivity()).setBackButtonOnOff(true);
        reset();
        updateQuiz();

        return view;
    }
    public static FragmentQuiz newInstance(HuntManager huntManager) {
        FragmentQuiz f = new FragmentQuiz();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }
    public void updateFragment(){
        reset();
        updateQuiz();
    }

    public void updateQuiz(){
        if(question_num < questionList.size()) {
            Question question = questionList.get(question_num);
            LinkedList<String> a_list = question.getAllChoices();
            Collections.shuffle(a_list);
            System.out.println("Size: "+a_list.size());
            answer = question.getAnswer();
            for (String s : a_list) {
                ViewCheckbox temp = new ViewCheckbox(getContext());
                temp.setAnswer(s);
                temp.setOnClickListener(this);

                temp.setListener(this);
                checkboxes.add(temp);
                layout_answers.addView(temp);
                txt_question.setText(question.getQuestion());
            }
        }
        else{
            Fragment f = getParentFragment();
            mHuntManager.getFocusBadge().setmIsCompleted(true);

            if(f instanceof FragmentList){
                ((FragmentList) f).setFragment(FragmentList.FRAGMENT_BADGE_OBTAINED);
            }
            else if(f instanceof FragmentMap){
                ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_BADGE_OBTAINED);
            }
        }
    }

    public void reset(){
        question_num = 0;
        findFirstUncompletedQuestion();
        clearQuestion();
    }

    public void clearQuestion(){
        if(checkboxes != null){
            for(ViewCheckbox cb: checkboxes)
                cb.setListener(null);
        }
        layout_answers.removeAllViews();
        checkboxes = new LinkedList<>();
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
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        Log.e(TAG, "setArguments, huntman: "+mHuntManager);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_submit_answer){
            boolean correct = false;
            for(ViewCheckbox cb: checkboxes){
                if(cb.isChecked()){
                    if(cb.getAnswer().equalsIgnoreCase(answer)){
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
        ((ActivityBase)getActivity()).setPagerSwipe(false);
        Fragment f = getParentFragment();
        if(f instanceof FragmentList){
            ((FragmentList) f).setFragment(FragmentList.FRAGMENT_TIMER);
        }
        else if(f instanceof FragmentMap){
            ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_TIMER);
        }
    }
    public void findFirstUncompletedQuestion(){
        while(question_num < questionList.size() && questionList.get(question_num).getIsCompleted()) {
            question_num++;
        }

    }
    public void answeredCorrectly(){
        questionList.get(question_num).setIsCompleted(true);
        findFirstUncompletedQuestion();
        clearQuestion();
        updateQuiz();
    }
}
