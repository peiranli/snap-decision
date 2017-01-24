package com.example.dots;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.common.io.Files;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends Activity {

    /**
     * Activity shown for when the app is opened
     * Will either load onto the main screen, if the user
     * switches back to the app, or the login screen,
     * if the data is not there to use.
     */
    public static final String PREFS_NAME = "Dots_Pref";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // If current user is NOT anonymous user
        // Get current user data from Parse.com
        //ParseUser currentUser = ParseUser.getCurrentUser();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getString("username_file", "default").equals("default") && LoginSignupActivity.usernameFileData.size() > 0) {
                // Send logged in users to Welcome.class

                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                //finish();
            } else {
                // Send user to LoginSignupActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginSignupActivity.class);
                startActivity(intent);
                //finish();
            }
        }


    public void onResume()
    {
        super.onResume();
        /*
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent); */
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Send logged in users to Welcome.class

            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            //finish();
        } else {
            // Send user to LoginSignupActivity.class
            Intent intent = new Intent(MainActivity.this,
                    LoginSignupActivity.class);
            startActivity(intent);
            //finish();
        }
    }
}
