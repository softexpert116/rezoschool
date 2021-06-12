package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class User implements Serializable {
    public String _id;
    public String name;
    public String photo;
    public String email;
    public String phone;
    public String country;
    public String city;
    public String type;
    public String token;
    public boolean isAllow;
    public int status;
    public String username;
    public String password;
    public String senderID;

    public User(String _id, String name, String photo, String email, String phone, String country, String city, String type, String token, boolean isAllow, int status, String username_sms, String password_sms, String senderID) {
        this._id = _id;
        this.name = name;
        this.photo = photo;
        this.phone = phone;
        this.email = email;
        this.country = country;
        this.city = city;
        this.type = type;
        this.token = token;
        this.isAllow = isAllow;
        this.status = status;
        this.username = username_sms;
        this.password = password_sms;
        this.senderID = senderID;
    }
    public User() {
        this._id = "";
        this.name = "";
        this.photo = "";
        this.phone = "";
        this.email = "";
        this.country = "";
        this.city = "";
        this.type = "";
        this.token = "";
        this.isAllow = false;
        this.status = 0;
        this.username = "";
        this.password = "";
        this.senderID = "";
    }
}
