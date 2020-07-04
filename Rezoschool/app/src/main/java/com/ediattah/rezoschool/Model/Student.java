package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Student implements Serializable {
    public String uid;
    public String class_name;
    public boolean isNew;

    public Student(String uid, String class_name, boolean isNew) {
        this.uid = uid;
        this.class_name = class_name;
        this.isNew = isNew;
    }
    public Student() {
        this.uid = "";
        this.class_name = "";
        this.isNew = false;
    }
}
