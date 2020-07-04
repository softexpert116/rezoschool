package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class Class implements Serializable {
    public String level;
    public String name;
    public String start_time;
    public String end_time;
    public String courses;

    public Class(String level, String name, String start_time, String end_time, String courses) {
        this.level = level;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.courses = courses;
    }
    public Class() {
        this.level = "";
        this.name = "";
        this.start_time = "";
        this.end_time = "";
        this.courses = "";
    }
}
