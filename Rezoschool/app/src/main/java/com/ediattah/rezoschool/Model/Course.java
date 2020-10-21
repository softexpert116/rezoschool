package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Course implements Serializable {
    public String name;
    public String coef;
    public ArrayList<CourseTime> times;
    public Course(String name, String coef, ArrayList<CourseTime> times) {
        this.name = name;
        this.coef = coef;
        this.times = times;
    }
    public Course() {
        this.name = "";
        this.coef = "1";
        this.times = new ArrayList<>();
    }
}
