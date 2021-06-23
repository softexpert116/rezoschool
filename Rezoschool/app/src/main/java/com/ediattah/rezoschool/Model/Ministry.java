package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class Ministry implements Serializable {
    public String _id;
    public String uid;
    public String type;

    public Ministry(String _id, String uid, String type) {
        this._id = _id;
        this.uid = uid;
        this.type = type;
    }
    public Ministry() {
        this._id = "";
        this.uid = "";
        this.type = "";
    }
}
