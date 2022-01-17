package com.example.ToList.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class Shopper {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;
    public String email;
    private String userId;
    //private String userName;
    //private String email;

    public Shopper(){}

    public Shopper(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }



    public String getEmail() { return email; }

    public void setUserId(String userId) {
        this.userId = userId;
    }




    public void setEmail(String email) {
        this.email = email;
    }
}