package com.example.dots;

import android.app.Activity;
//import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;


/**
 * A menu for containing paths to the three different
 * versions of the game.
 */
public class GameMenuActivity extends Activity {
    static int points = -1;
    static int artificial_level = -1;
    static int level = -1;
    public static boolean logInFailed = false; //if logged in while checking duplicate names, stop signing up
    public static int uniqueInt = 0; //force user to have different parse username
    public static String testUsername;
    public static final String PREFS_NAME = "Dots_Pref";
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uniqueInt = 0;
        setContentView(R.layout.game_menu_activity);
        Intent startIntent = getIntent();
        //points = startIntent.getIntExtra("points", -1);
        //artificial_level = startIntent.getIntExtra("artificial_level", -1);
        //level = startIntent.getIntExtra("level", -1);
        SharedPreferences settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);
        connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));

        points = settings.getInt("points", -1);
        artificial_level = settings.getInt("artificial_level", -1);
        level = settings.getInt("level", -1);

        Button timeButton = (Button) findViewById(R.id.timeButton);
        Button playButton = (Button) findViewById(R.id.playButton);

        System.out.println("points: "+ points);
        System.out.println("artificial_level: " + artificial_level);
        System.out.println("level: "+level);
        loginParseUser();

    }

    public void onPlayClick(View v)
    {
        Intent intent;
        //ParseUser user = ParseUser.getCurrentUser();
        /*if (!user.getBoolean("new"))
        {
            intent = new Intent(
                    GameMenuActivity.this,
                    NewUserMessageActivity.class);
            intent.putExtra("points", 0);
            intent.putExtra("artificial_level", 1);
            intent.putExtra("level", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }*/
        //else {
            intent = new Intent(
                    GameMenuActivity.this,
                    GameActivity.class);
            intent.putExtra("points", points);
            intent.putExtra("artificial_level", artificial_level);
            intent.putExtra("level", level);
        //}
        startActivity(intent);
    }


    public void onTimePlay(View v)
    {
        Intent intent;
        /*ParseUser user = ParseUser.getCurrentUser();
        if (!user.getBoolean("new"))
        {
            intent = new Intent(
                    GameMenuActivity.this,
                    NewUserMessageActivity.class);
            intent.putExtra("points", 0);
            intent.putExtra("artificial_level", 1);
            intent.putExtra("level", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }*/
        //else {
            intent = new Intent(
                    GameMenuActivity.this,
                    TimedGameActivity.class);
            intent.putExtra("points", points);
            intent.putExtra("artificial_level", artificial_level);
            intent.putExtra("level", level);
        //}
        startActivity(intent);
    }

    public void onPracticePlay(View v)
    {
        Intent intent;
        /*ParseUser user = ParseUser.getCurrentUser();
        if(!user.getBoolean("new"))
        {
            intent = new Intent(
                    GameMenuActivity.this,
                    NewUserMessageActivity.class);
            intent.putExtra("points", 0);
            intent.putExtra("artificial_level", 1);
            intent.putExtra("level", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        }*/
        //else {
            intent = new Intent(
                     GameMenuActivity.this,
                     LimitsGameActivity.class);
            intent.putExtra("points", 0);
            intent.putExtra("artificial_level", 1);
            intent.putExtra("level", 1);

        //}
        startActivity(intent);
    }

    //Handles Input of the back button, redirect to main menu
    public void onBackPressed() {
        Intent intent = new Intent(GameMenuActivity.this,
                            WelcomeActivity.class);
        startActivity(intent);

        return;
    }

    public void loginParseUser() {
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            final String usernametxt = prefs.getString("usernametxt", "default");
            final String passwordtxt = prefs.getString("passwordtxt", "default");

            logInFailed = false;
            ParseUser.logInInBackground(usernametxt, passwordtxt,
                    new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user == null) {
                                logInFailed = true;
                            }

                        }
                    });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ParseUser newUser = new ParseUser(); //attempt to create new user
            //if it didn't exist before
            newUser.setUsername(usernametxt);
            newUser.setPassword(passwordtxt);
            testUsername = usernametxt;
            while (logInFailed) { //make a new parse user if we couldn't log in, keep trying until
                //a new user is made and logged in.
                System.err.println("Looping");
                newUser.setUsername(testUsername);
                newUser.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {

                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                ParseUser.logInInBackground(testUsername, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (e == null && user != null) {
                                    logInFailed = false;
                                    System.err.println("logged in");
                                }
                            }
                        });
                uniqueInt++;
                testUsername = usernametxt + uniqueInt;
                System.err.println(testUsername);
                newUser = new ParseUser();
                newUser.setUsername(testUsername);
                newUser.setPassword(passwordtxt);
            }
        }
    }
}
