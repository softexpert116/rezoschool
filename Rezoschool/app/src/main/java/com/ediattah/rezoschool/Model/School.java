package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class School implements Serializable {
    public String _id;
    public String uid;
    public String number;
    public String type;
    public String area;
    public boolean isPublic;
    public ArrayList<Course> courses;
    public ArrayList<Class> classes;
    public ArrayList<Level> levels;
    public ArrayList<Teacher> teachers;
    public ArrayList<Student> students;

    public School(String _id, String uid, String number, String type, String area, boolean isPublic, ArrayList<Course> courses, ArrayList<Class> classes, ArrayList<Level> levels, ArrayList<Teacher> teachers, ArrayList<Student> students) {
        this._id = _id;
        this.uid = uid;
        this.number = number;
        this.type = type;
        this.area = area;
        this.isPublic = isPublic;
        this.courses = courses;
        this.classes = classes;
        this.levels = levels;
        this.teachers = teachers;
        this.students = students;
    }
    public School() {
        this._id = "";
        this.uid = "";
        this.number = "";
        this.type = "";
        this.area = "";
        this.isPublic = false;
        this.courses = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.students = new ArrayList<>();
    }
}
