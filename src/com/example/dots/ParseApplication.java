package com.example.dots;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

/**
 * Special class for interfacing with parse.
 */
public class ParseApplication extends Application {
    private String APPLICATION_KEY = "MuXCbBhVDkIJJaQ0v5STPeXKnirVNIKEN3X6wrpS";//"r5OfwYgT0eNJXMZJIcTeAgrZuUWHpA0bCfkA7AVz";
    private String CLIENT_KEY = "JfpYGCtlde2W37EiPemYJZCZ32KbyJ2qlhyJ1UHg";//"6i60qXpvd3rwqE3VPZWtC0Kl9nUKLHDiOARddGaN";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(getApplicationContext());
        // Add your initialization code here
        Parse.initialize(this, APPLICATION_KEY, CLIENT_KEY);

       // ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
