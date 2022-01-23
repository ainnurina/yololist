package com.example.ToList.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class Shopper {

    public String username;
    public String email;
    private String userId;
    private String phoneno;


    public Shopper(){}

    public Shopper(String username, String email, String userId, String phoneno) {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.phoneno = phoneno;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}