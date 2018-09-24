package com.davis.tyler.magpiehunt.Hunts;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedList;

public class Quiz implements Serializable {
    //TODO finish this after finding out how many questions per quiz.
    private LinkedList<Question> questions;
    //question object, lists of answers and questions inside of that. quiz object will have list of questions. getters/setters/helper methods as needed

    //CREATE A FIELD FOR A QUIZ INSIDE OF THE BADGE OBJECT

    public Quiz(LinkedList<Question> quizQuestions)
    {
        this.questions = quizQuestions;
    }


    public Quiz()
    {

    }

    public void setQuestions(LinkedList<Question> quizQuestions)
    {
        this.questions = quizQuestions;
    }

    public LinkedList<Question> getQuestions()
    {
        return this.questions;
    }






}
