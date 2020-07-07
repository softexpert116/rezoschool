package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.Date;

public class Syllabus implements Serializable {
    public String _id;
    public Date date;
    public String time;
    public String course;
    public String school_id;
    public String uid;

    public Syllabus(String _id, Date date, String time, String course, String school_id, String uid) {
        this._id = _id;
        this.date = date;
        this.time = time;
        this.course = course;
        this.school_id = school_id;
        this.uid = uid;
    }
    public Syllabus() {
        this._id = "";
        this.date = new Date();
        this.time = "";
        this.course = "";
        this.school_id = "";
        this.uid = "";
    }
}
