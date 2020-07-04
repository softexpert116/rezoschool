package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Teacher implements Serializable {
    public String uid;
    public String courses;

    public Teacher(String uid, String courses) {
        this.uid = uid;
        this.courses = courses;
    }
    public Teacher() {
        this.uid = "";
        this.courses = "";
    }
}
