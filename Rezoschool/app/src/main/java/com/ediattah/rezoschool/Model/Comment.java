package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Comment implements Serializable {
    public String _id;
    public String uid;
    public String comment;
    public String date;

    public Comment(String _id, String uid, String comment, String date) {
        this._id = _id;
        this.uid = uid;
        this.comment = comment;
        this.date = date;
    }
    public Comment() {
        this._id = "";
        this.uid = "";
        this.comment = "";
        this.date = "";
    }
}
