package com.davis.tyler.magpiehunt.Hunts;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Question implements Serializable{
    private Answer mAnswer;
    private LinkedList<String> mChoices;

    public Question(LinkedList<String> choices, Answer answer){
        mChoices = choices;
        mAnswer = answer;
    }
    public Question(){}

    public void setAnswer(Answer a){mAnswer= a;}
    public void setChoices(LinkedList<String> strings){mChoices = strings;}

    public LinkedList<String> getChoices(){return mChoices;}
    public Answer getAnswer(){return mAnswer;}

}
