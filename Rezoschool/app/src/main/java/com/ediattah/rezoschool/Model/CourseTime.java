package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class CourseTime implements Serializable {
    public int dayOfWeek;
    public String start_time;
    public String end_time;

    public CourseTime(int dayOfWeek, String start_time, String end_time) {
        this.dayOfWeek = dayOfWeek;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    public CourseTime() {
        this.dayOfWeek = 0;
        this.start_time = "";
        this.end_time = "";
    }
}
