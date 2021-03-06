package com.example.ToListApp.model;

import com.google.firebase.Timestamp;

public class List {
    private String listid;
    private String title;
    private Timestamp timeAdded;
    private String shopName;
    private Timestamp datePlan;
    private Float totalbudget;
    private Float totalexpenses;
    private String statusList;
    private String userId;


    public List() {
        super();
    }

    public List(String listid, String title, Timestamp timeAdded, String shopName, Timestamp datePlan, Float totalbudget, Float totalexpenses, String statusList, String userId) {
        this.listid = listid;
        this.title = title;
        this.timeAdded = timeAdded;
        this.shopName = shopName;
        this.datePlan = datePlan;
        this.totalbudget = totalbudget;
        this.totalexpenses = totalexpenses;
        this.statusList = statusList;
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

    public Timestamp getDatePlan() {
        return datePlan;
    }

    public void setDatePlan(Timestamp datePlan) {
        this.datePlan = datePlan;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Float getTotalbudget() {
        return totalbudget;
    }

    public void setTotalbudget(Float totalbudget) {
        this.totalbudget = totalbudget;
    }

    public Float getTotalexpenses() {
        return totalexpenses;
    }

    public void setTotalexpenses(Float totalexpenses) {
        this.totalexpenses = totalexpenses;
    }

    public String getStatusList() {
        return statusList;
    }

    public void setStatusList(String statusList) {
        this.statusList = statusList;
    }
}
