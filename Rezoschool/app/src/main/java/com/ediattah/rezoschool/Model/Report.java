package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Report implements Serializable {
    public String _id;
    public String uid;
    public String tweet_id;
    public String content;

    public Report(String _id, String uid, String tweet_id, String content) {
        this._id = _id;
        this.uid = uid;
        this.tweet_id = tweet_id;
        this.content = content;
    }
    public Report() {
        this._id = "";
        this.uid = "";
        this.tweet_id = "";
        this.content = "";
    }
}
