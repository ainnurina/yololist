package com.example.ToList.model;

public class Items extends ItemId{
    private String itemid;
    private String itemName;
    private String listid;
    private int status;

    public Items() {
        super();
    }

    public Items(String itemid, String itemName, String listid, int status) {
        this.itemid = itemid;
        this.itemName = itemName;
        this.listid = listid;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
