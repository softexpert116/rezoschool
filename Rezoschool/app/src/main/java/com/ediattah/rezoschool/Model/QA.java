package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class QA implements Serializable {
    public String question;
    public ArrayList<String> answers;
    public String corrects;
    public int points;
    public boolean isSingleChoice;

    public QA(String question, ArrayList<String> answers, String corrects, int points, boolean isSingleChoice) {
        this.question = question;
        this.answers = answers;
        this.corrects = corrects;
        this.points = points;
        this.isSingleChoice = isSingleChoice;
    }
    public QA() {
        this.question = "";
        this.answers = new ArrayList<>();
        this.corrects = "0";
        this.points = 0;
        this.isSingleChoice = false;
    }
}
