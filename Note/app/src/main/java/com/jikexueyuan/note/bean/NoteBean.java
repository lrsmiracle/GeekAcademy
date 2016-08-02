package com.jikexueyuan.note.bean;

/**
 * Created by Liurs on 2016/5/25.
 **/
public class NoteBean {

    private String time;
    private String context;
    private int _id;

    public NoteBean(int _id, String context, String time) {
        this._id = _id;
        this.context = context;
        this.time = time;
    }

    @Override
    public String toString() {
        return "NoteBean{" +
                "_id='" + _id + '\'' +
                ", time='" + time + '\'' +
                ", context='" + context + '\'' +
                '}';
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public NoteBean() {

    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public NoteBean(String context, String time) {

        this.context = context;
        this.time = time;
    }
}
