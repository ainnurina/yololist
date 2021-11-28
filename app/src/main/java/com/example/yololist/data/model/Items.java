package com.example.yololist.data.model;

public class Items {
    private String itemid;
    private String itemName;
    private boolean itemchecked;
    private String listid;

    public Items() {
        super();
    }

    public Items(String itemid, String itemName, boolean itemchecked, String listid) {
        super();
        this.itemid = itemid;
        this.itemName = itemName;
        this.itemchecked = itemchecked;
        this.listid = listid;
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

    public boolean isItemchecked() {
        return itemchecked;
    }

    public void setItemchecked(boolean itemchecked) {
        this.itemchecked = itemchecked;
    }
}
