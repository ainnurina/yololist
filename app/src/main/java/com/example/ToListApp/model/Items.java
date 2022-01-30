package com.example.ToListApp.model;

public class Items {
    private String itemid;
    private String itemName;
    private String listid;
    private int itemQty;
    private int status;

    public Items() {
        super();
    }

    public Items(String itemid, String itemName, String listid, int itemQty, int status) {
        this.itemid = itemid;
        this.itemName = itemName;
        this.listid = listid;
        this.itemQty = itemQty;
        this.status = status;
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

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
