package com.example.ToList.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class List {
    private String listid;
    private String title;
    private Timestamp timeAdded;
    private int totitem;
    private String shopName;
    private Date datePlan;
    private Float totalbudget;
    private Float totalexpenses;
    private String userId;


    public List()   {
        super();
    }

    public List(String listid, String title, Timestamp timeAdded, int totalitem, String shopName, Date datePlan, Float totalbudget, Float totalexpenses, String userId) {
        this.listid = listid;
        this.title = title;
        this.timeAdded = timeAdded;
        this.totitem = totitem;
        this.shopName = shopName;
        this.datePlan = datePlan;
        this.totalbudget = totalbudget;
        this.totalexpenses = totalexpenses;
        this.userId = userId;
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

    public Date getDatePlan() { return datePlan; }

    public void setDatePlan(Date datePlan) { this.datePlan = datePlan;}

    public String getShopName() { return shopName; }

    public void setShopName(String shopName) { this.shopName = shopName; }

    public Float getTotalbudget() { return totalbudget; }

    public void setTotalbudget(Float totalbudget) { this.totalbudget = totalbudget; }

    public Float getTotalexpenses() { return totalexpenses; }

    public void setTotalexpenses(Float totalexpenses) { this.totalexpenses = totalexpenses; }
}
