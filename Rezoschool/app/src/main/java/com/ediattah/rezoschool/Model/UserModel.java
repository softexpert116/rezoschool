package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class UserModel implements Serializable {
    public int _id;
    public String name;
    public String photo;
    public String email;
    public String password;
    public String country_code;
    public String country_name;
    public String phone;
    public String city;
    public String user_type;
    public String description;
    public String school_number;
    public String school_type;
    public String school_grade;

    public UserModel(int _id, String name, String photo, String email, String password, String country_code, String country_name, String phone, String city, String user_type, String description, String school_number, String school_type, String school_grade) {
        this._id = _id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.country_code = country_code;
        this.country_name = country_name;
        this.phone = phone;
        this.city = city;
        this.user_type = user_type;
        this.description = description;
        this.school_number = school_number;
        this.school_type = school_type;
        this.school_grade = school_grade;
    }
    public UserModel() {
        this._id = 0;
        this.name = "";
        this.phone = "";
        this.email = "";
        this.password = "";
        this.country_code = "";
        this.country_name = "";
        this.phone = "";
        this.city = "";
        this.user_type = "";
        this.description = "";
        this.school_number = "";
        this.school_type = "";
        this.school_grade = "";
    }
}
