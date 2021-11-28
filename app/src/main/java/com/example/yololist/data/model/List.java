package com.example.yololist.data.model;

public class List {
    private String listid;
    private String title;
    private String date;
    private int totitem;


    public List()   {
        super();
    }

    public List(String listid, String title, String date)   {
        super();
        this.listid = listid;
        this.title = title;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
