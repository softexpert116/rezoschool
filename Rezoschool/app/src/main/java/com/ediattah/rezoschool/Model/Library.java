package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Library implements Serializable {
    public String _id;
    public String uid;
    public String school_id;
    public String title;
    public String description;
    public String url;
    public String category;
    public boolean isPublic;
    public boolean isAllow;

    public Library(String _id, String uid, String school_id, String title, String description, String url, String category, boolean isPublic, boolean isAllow) {
        this._id = _id;
        this.uid = uid;
        this.school_id = school_id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.category = category;
        this.isPublic = isPublic;
        this.isAllow = isAllow;
    }
    public Library() {
        this._id = "";
        this.uid = "";
        this.school_id = "";
        this.title = "";
        this.description = "";
        this.url = "";
        this.category = "";
        this.isPublic = false;
        this.isAllow = false;
    }
}
