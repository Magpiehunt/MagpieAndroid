package com.davis.tyler.magpiehunt.Hunts;

import java.io.Serializable;

public class Answer implements Serializable{
    private int mAnswer;
    public Answer(int a){
        mAnswer = a;
    }
    public Answer(){}
    public int getAnswer(){return mAnswer;}
    public void setAnswer(int a){mAnswer = a;}
}
