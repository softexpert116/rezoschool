package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class CommentModel implements Serializable {
    public String _id;
    public String uid;
    public String tweet_id;
    public String date;
    public String description;

    public CommentModel(String _id, String uid, String tweet_id, String date, String description) {
        this._id = _id;
        this.uid = uid;
        this.tweet_id = tweet_id;
        this.date = date;
        this.description = description;
    }
    public CommentModel() {
        this._id = "";
        this.uid = "";
        this.tweet_id = "";
        this.date = "";
        this.description = "";
    }
}
