package com.ediattah.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class PsychologySection implements Serializable {
    public String _id;
    public String name;
    public String description;
    public int score;
    public ArrayList<QA> qas;

    public PsychologySection(String _id, String name, String description, int score, ArrayList<QA> qas) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.score = score;
        this.qas = qas;
    }
    public PsychologySection() {
        this._id = "";
        this.name = "";
        this.description = "";
        this.score = 0;
        this.qas = new ArrayList<>();
    }
}
