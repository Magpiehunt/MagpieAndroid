package com.davis.tyler.magpiehunt.Hunts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Question implements Serializable {
    private String answer;
    private LinkedList<String> choices;
    private String question;
    private boolean isCompleted;

    public Question(LinkedList<String> choices, String answer, String question){
        this.choices = choices;
        this.answer = answer;
        this.question = question;
    }
    public Question(){}

    public void setIsCompleted(boolean b){isCompleted = b;}

    public void setAnswer(String a)
    {
        this.answer= a;
    }

    public void setAllChoices(LinkedList<String> strings)
    {
        this.choices = strings;
    }

    public void setChoice( int index, String newText)
    {
        this.choices.set(index, newText);
    }

    public void setQuestion(String ques){ this.question = ques;}

    public String getQuestion(){ return this.question;}

    public LinkedList<String> getAllChoices()
    {
        return this.choices;
    }

    public LinkedList<String> getAllChoicesRandom(){
        Collections.shuffle(choices);
        return choices;
    }

    public String getChoice(int index)
    {
        return this.choices.get(index);
    }

    public String getAnswer()
    {
        return this.answer;
    }
    public boolean getIsCompleted(){return isCompleted;}

}
