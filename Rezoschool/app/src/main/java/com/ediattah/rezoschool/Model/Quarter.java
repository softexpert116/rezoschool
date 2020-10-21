package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Quarter implements Serializable {
    public String name;
    public long first_time;
    public long last_time;

    public Quarter(String name, long first_time, long last_time) {
        this.name = name;
        this.first_time = first_time;
        this.last_time = last_time;
    }
}
