package com.ediattah.rezoschool.Model;

import java.io.Serializable;

public class DayCourseModel implements Serializable {
    public Course course;
    public String date;
    public String time;

    public DayCourseModel(Course course, String date, String time) {
        this.course = course;
        this.date = date;
        this.time = time;
    }
}
