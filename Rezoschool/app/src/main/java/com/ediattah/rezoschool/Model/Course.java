package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Course implements Serializable {
    public String name;
    public ArrayList<CourseTime> times;
    public Course(String name, ArrayList<CourseTime> times) {
        this.name = name;
        this.times = times;
    }
    public Course() {
        this.name = "";
        this.times = new ArrayList<>();
    }
}
