package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.Date;

public class Exam implements Serializable {
    public String _id;
    public Date date;
    public int num1;
    public int num2;
    public String school_id;
    public String uid;
    public String course_name;
    public String course_coef;
    public String title;

    public Exam(String _id, Date date, int num1, int num2, String school_id, String uid, String course_name, String course_coef, String title) {
        this._id = _id;
        this.date = date;
        this.num1 = num1;
        this.num2 = num2;
        this.school_id = school_id;
        this.uid = uid;
        this.course_name = course_name;
        this.course_coef = course_coef;
        this.title = title;
    }
    public Exam() {
        this._id = "";
        this.date = new Date();
        this.num1 = 0;
        this.num2 = 0;
        this.school_id = "";
        this.uid = "";
        this.course_name = "";
        this.course_coef = "";
        this.title = "";
    }
}
