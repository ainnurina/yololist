package com.example.yololist.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class Shopper {

    public String userName;
    public String email;
    private String userId;
    //private String userName;
    //private String email;

    public Shopper(){}

    public Shopper(String userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() { return email; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}