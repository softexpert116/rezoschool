package com.ediattah.rezoschool.Model;//package com.example.ujs.afterwork.com.ujs.rezoschool.Model;
//
import java.io.Serializable;
import java.util.ArrayList;

public class VideoGroup implements Serializable {
    public String _id;
    public String creator_id;
    public ArrayList<String> member_ids;
    public String name;
    public String room;
    public long timestamp;

    public VideoGroup(String _id, String creator_id, ArrayList<String> member_ids, String name, String room, long timestamp) {
        this._id = _id;
        this.creator_id = creator_id;
        this.member_ids = member_ids;
        this.name = name;
        this.room = room;
        this.timestamp = timestamp;
    }
    public VideoGroup() {
        this._id = "";
        this.creator_id = "";
        this.member_ids = new ArrayList<>();
        this.name = "";
        this.room = "";
        this.timestamp = 0;
    }
}
