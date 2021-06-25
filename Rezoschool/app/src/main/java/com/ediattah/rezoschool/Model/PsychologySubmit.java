package com.ediattah.rezoschool.Model;
//
import java.io.Serializable;

public class PsychologySubmit implements Serializable {
    public String _id;
    public String agent_id;
    public String student_id;

    public PsychologySubmit(String _id, String agent_id, String student_id) {
        this._id = _id;
        this.agent_id = agent_id;
        this.student_id = student_id;
    }
    public PsychologySubmit() {
        this._id = "";
        this.agent_id = "";
        this.student_id = "";
    }
}
