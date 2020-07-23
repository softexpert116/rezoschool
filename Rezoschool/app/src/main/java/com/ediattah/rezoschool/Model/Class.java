package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class Class implements Serializable {
    public String level;
    public String name;
    public String courses;

    public Class(String level, String name, String courses) {
        this.level = level;
        this.name = name;
        this.courses = courses;
    }
    public Class() {
        this.level = "";
        this.name = "";
        this.courses = "";
    }
}
