package com.ediattah.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class PsychologyResult implements Serializable {
    public String _id;
    public String school_id;
    public String uid;
    public String section_id;
    public String section_name;
    public String name;
    public String birth;
    public String className;
    public int score;
    public long timestamp;

    public PsychologyResult(String _id, String school_id, String uid, String section_id, String section_name, String name, String birth, String className, int score, long timestamp) {
        this._id = _id;
        this.school_id = school_id;
        this.uid = uid;
        this.section_id = section_id;
        this.section_name = section_name;
        this.name = name;
        this.birth = birth;
        this.className = className;
        this.score = score;
        this.timestamp = timestamp;
    }
    public PsychologyResult() {
        this._id = "";
        this.school_id = "";
        this.uid = "";
        this.section_id = "";
        this.section_name = "";
        this.name = "";
        this.birth = "";
        this.className = "";
        this.score = 0;
        this.timestamp = 0;
    }
}
