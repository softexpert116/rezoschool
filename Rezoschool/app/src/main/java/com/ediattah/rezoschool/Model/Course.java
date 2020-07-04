package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Course implements Serializable {
    public String name;

    public Course(String name) {
        this.name = name;
    }
    public Course() {
        this.name = "";
    }
}
