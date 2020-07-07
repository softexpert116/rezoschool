package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.Date;

public class Absence implements Serializable {
    public String _id;
    public Date date;
    public String url;
    public String school_id;
    public String uid;

    public Absence(String _id, Date date, String url, String school_id, String uid) {
        this._id = _id;
        this.date = date;
        this.url = url;
        this.school_id = school_id;
        this.uid = uid;
    }
    public Absence() {
        this._id = "";
        this.date = new Date();
        this.url = "";
        this.school_id = "";
        this.uid = "";
    }
}
