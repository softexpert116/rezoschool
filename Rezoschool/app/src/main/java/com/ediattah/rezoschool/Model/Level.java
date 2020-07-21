package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;

public class Level implements Serializable {
    public String name;
    public String fee;

    public Level(String name,String fee) {
        this.name = name;this.fee = fee;
    }
    public Level() {
        this.name = "";this.fee = "";
    }
}
