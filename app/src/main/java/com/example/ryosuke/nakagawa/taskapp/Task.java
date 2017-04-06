package com.example.ryosuke.nakagawa.taskapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ryosuke on 2017/01/22.
 */
public class Task extends RealmObject implements Serializable {
    private String title;//taitoru
    private String contents;
    private String category;
    private Date date;

    //idをプライマリーキーとして設定
    @PrimaryKey
    private  int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }



}
