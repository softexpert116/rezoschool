package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class Tweet implements Serializable {
    public String _id;
    public String uid;
    public String media;
    public String description;
    public String date;
    public int like;
    public int dislike;
    public ArrayList<Comment> comments;

    public Tweet(String _id, String uid, String media, String description, String date, int like, int dislike, ArrayList<Comment> comments) {
        this._id = _id;
        this.uid = uid;
        this.media = media;
        this.description = description;
        this.date = date;
        this.like = like;
        this.dislike = dislike;
        this.comments = comments;
    }
    public Tweet() {
        this._id = "";
        this.uid = "";
        this.media = "";
        this.description = "";
        this.date = "";
        this.like = 0;
        this.dislike = 0;
        this.comments = new ArrayList<>();
    }
}
