package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Student implements Serializable {
    public String uid;
    public String parent_id;
    public String school_id;
    public String class_name;
    public boolean isNew;
    public boolean isAllow;

    public Student(String uid, String parent_id, String school_id, String class_name, boolean isNew, boolean isAllow) {
        this.uid = uid;
        this.parent_id = parent_id;
        this.school_id = school_id;
        this.class_name = class_name;
        this.isNew = isNew;
        this.isAllow = isAllow;
    }
    public Student() {
        this.uid = "";
        this.parent_id = "";
        this.school_id = "";
        this.class_name = "";
        this.isNew = false;
        this.isAllow = false;
    }
}
