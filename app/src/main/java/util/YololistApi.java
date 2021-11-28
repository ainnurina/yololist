package util;

import android.app.Application;

public class YololistApi extends Application {
    private String username;
    private String userId;
    private static YololistApi instance;

    public static YololistApi getInstance()  {
        if (instance == null)   {
            instance = new YololistApi();
        }
        return instance;
    }

    public YololistApi() {}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username)    {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId)    {
        this.userId = userId;
    }
}
