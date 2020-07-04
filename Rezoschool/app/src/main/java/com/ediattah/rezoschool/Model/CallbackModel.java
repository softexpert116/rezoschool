package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class CallbackModel implements Serializable {
    public int _id;
    public int school_id;
    public String number;
    public String message;

    public CallbackModel(int _id, int school_id, String number, String message) {
        this._id = _id;
        this.school_id = school_id;
        this.number = number;
        this.message = message;
    }
    public CallbackModel() {
        this._id = 0;
        this.school_id = 0;
        this.number = "";
        this.message = "";
    }
}
