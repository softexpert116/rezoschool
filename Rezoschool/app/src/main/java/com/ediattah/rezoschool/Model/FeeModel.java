package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class FeeModel implements Serializable {
    public int _id;
    public int school_id;
    public String level;
    public int student_id;
    public String fee;

    public FeeModel(int _id, int school_id, int student_id, String level, String fee) {
        this._id = _id;
        this.school_id = school_id;
        this.student_id = student_id;
        this.level = level;
        this.fee = fee;
    }
    public FeeModel() {
        this._id = 0;
        this.school_id = 0;
        this.student_id = 0;
        this.level = "";
        this.fee = "";
    }
}
