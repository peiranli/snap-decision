package com.example.dots;

/**
 * Created by Hung on 5/25/2016.
 */
/* Unused as of now, intended to hold username and password for offline usage, moved
   to the arrayList contained in LoginSignup 9/2/2016
 * Continues to be unused, ignore this class
 */
public class UserInfo {

    private static String username;
    private static String password;

    public static void setUsername(String user) {

        username = user;

    }
    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }
}
