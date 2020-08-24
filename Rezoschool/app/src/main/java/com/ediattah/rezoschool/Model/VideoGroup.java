package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class VideoGroup implements Serializable {
    public String _id;
    public User creator;
    public ArrayList<User> members;
    public String name;
    public String room;
    public long timestamp;

    public VideoGroup(String _id, User creator, ArrayList<User> members, String name, String room, long timestamp) {
        this._id = _id;
        this.creator = creator;
        this.members = members;
        this.name = name;
        this.room = room;
        this.timestamp = timestamp;
    }
    public VideoGroup() {
        this._id = "";
        this.creator = new User();
        this.members = new ArrayList<>();
        this.name = "";
        this.room = "";
        this.timestamp = 0;
    }
}
