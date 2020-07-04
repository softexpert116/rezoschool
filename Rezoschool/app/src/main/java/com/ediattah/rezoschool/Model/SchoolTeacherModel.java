package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class SchoolTeacherModel implements Serializable {
    public int _id;
    public UserModel teacher;
    public Course course;

    public SchoolTeacherModel(int _id, UserModel teacher, Course course) {
        this._id = _id;
        this.teacher = teacher;
        this.course = course;
    }
}
