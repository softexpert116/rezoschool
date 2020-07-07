package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.Date;

public class Exam implements Serializable {
    public String _id;
    public Date date;
    public String result;
    public String school_id;
    public String uid;

    public Exam(String _id, Date date, String result, String school_id, String uid) {
        this._id = _id;
        this.date = date;
        this.result = result;
        this.school_id = school_id;
        this.uid = uid;
    }
    public Exam() {
        this._id = "";
        this.date = new Date();
        this.result = "";
        this.school_id = "";
        this.uid = "";
    }
}
