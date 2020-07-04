package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class TransactionModel implements Serializable {
    public int _id;
    public int student_id;
    public String amount;
    public String date;
    public String method;

    public TransactionModel(int _id, int student_id, String amount, String date, String method) {
        this._id = _id;
        this.student_id = student_id;
        this.amount = amount;
        this.date = date;
        this.method = method;
    }
    public TransactionModel() {
        this._id = 0;
        this.student_id = 0;
        this.date = "";
        this.amount = "";
        this.method = "";
    }
}
