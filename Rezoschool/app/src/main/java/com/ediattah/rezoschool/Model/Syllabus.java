package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.Date;

public class Syllabus implements Serializable {
    public String _id;
    public Date date;
    public CourseTime time;
    public String course;
    public String school_id;
    public String uid;
    public String title;
    public String thematic;
    public String summary;
    public String title_next;
    public String thematic_next;
    public String attach;
    public String comment;

    public Syllabus(String _id, Date date, CourseTime time, String course, String school_id, String uid, String title, String thematic, String summary, String title_next, String thematic_next, String attach, String comment) {
        this._id = _id;
        this.date = date;
        this.time = time;
        this.course = course;
        this.school_id = school_id;
        this.uid = uid;
        this.title = title;
        this.thematic = thematic;
        this.summary = summary;
        this.title_next = title_next;
        this.thematic_next = thematic_next;
        this.attach = attach;
        this.comment = comment;
    }
    public Syllabus() {
        this._id = "";
        this.date = new Date();
        this.time = new CourseTime();
        this.course = "";
        this.school_id = "";
        this.uid = "";
        this.title = "";
        this.thematic = "";
        this.summary = "";
        this.title_next = "";
        this.thematic_next = "";
        this.attach = "";
        this.comment = "";
    }
}
