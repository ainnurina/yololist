package com.example.yololist.model;

import com.google.firebase.Timestamp;

public class List {
    private String listid;
    private String title;
    private Timestamp timeAdded;
    private int totitem;
    private String userId;
    private String uid;


    public List()   {
        super();
    }

    public List(String listid, String title, Timestamp timeAdded)   {
        super();
        this.listid = listid;
        this.title = title;
        this.timeAdded = timeAdded;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotitem() {
        return totitem;
    }

    public void setTotitem(int totitem) {
        this.totitem = totitem;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

}
