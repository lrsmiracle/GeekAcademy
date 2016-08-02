package com.jikexueyuan.appwithhttp.model;

/**
 * Created by Liurs on 2016/7/4.
 **/
public class ListContentModel {

    private long Id;
    private String title;
    private String time;
    private String content;

    public ListContentModel(long id, String content, String time, String title) {
        this.Id = id;
        this.content = content;
        this.time = time;
        this.title = title;
    }
    public ListContentModel() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ListContentModel{" +
                "content='" + content + '\'' +
                ", Id=" + Id +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
