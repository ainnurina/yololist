package com.example.ToList.model;

public class Items {
    private String itemid;
    private String itemName;
    private String listid;
    private String itemStatus;


    public Items() {
        super();
    }

    public Items(String itemid, String itemName, String itemStatus, String listid) {
        super();
        this.itemid = itemid;
        this.itemName = itemName;
        this.listid = listid;
        this.itemStatus = itemStatus;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }
}
