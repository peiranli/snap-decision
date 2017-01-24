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

import com.parse.ParseUser;

import java.io.File;

/**
 * Defines the graphing Menu of the application. Contains
 * Buttons to go to the graphs for the three versions of the game
 * of the app.
 */
public class GraphMenuActivity extends Activity {
    static int points = -1;
    static int artificial_level = -1;
    static int level = -1;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_menu_activity);
        Intent startIntent = getIntent();
        //points = startIntent.getIntExtra("points", -1);
        //artificial_level = startIntent.getIntExtra("artificial_level", -1);
        //level = startIntent.getIntExtra("level", -1);
        SharedPreferences settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);
        connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));

        points = settings.getInt("points", -1);
        artificial_level = settings.getInt("artificial_level", -1);
        level = settings.getInt("level", -1);

        //Button viewTermsButton = (Button) findViewById(R.id.viewTermsButton);
        //Button aboutButton = (Button) findViewById(R.id.aboutButton);
        Button graphButton = (Button) findViewById(R.id.graphButton);
        Button timeGraphButton = (Button) findViewById(R.id.timeButton);

        System.out.println("points: "+ points);
        System.out.println("artificial_level: " + artificial_level);
        System.out.println("level: "+level);

    }


    public void onNormalGraph(View v)
    {
        /*
        Intent intent = new Intent(
                WelcomeActivity.this,
                ViewDataActivity.class);
        startActivity(intent);
        */
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            Intent i = new Intent(this, GraphActivity.class);
            startActivityForResult(i, 1);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please connect to WiFi to view your data",
                    Toast.LENGTH_LONG).show();
            System.out.println("network is not connected");
        }
        //Intent i = new Intent(this, ViewDataActivity.class);

    }

    public void onTimedGraph(View v)
    {
        /*
        Intent intent = new Intent(
                WelcomeActivity.this,
                ViewDataActivity.class);
        startActivity(intent);
        */
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            Intent i = new Intent(this, TimedGraphActivity.class);
            startActivityForResult(i, 1);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please connect to WiFi to view your data",
                    Toast.LENGTH_LONG).show();
            System.out.println("network is not connected");
        }
        //Intent i = new Intent(this, ViewDataActivity.class);

    }


    public void onLimitGraph(View v) {
          /*
        Intent intent = new Intent(
                WelcomeActivity.this,
                ViewDataActivity.class);
        startActivity(intent);
        */
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            Intent i = new Intent(this, LimitsGraphActivity.class);
            startActivityForResult(i, 1);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please connect to WiFi to view your data",
                    Toast.LENGTH_LONG).show();
            System.out.println("network is not connected");
        }
        //Intent i = new Intent(this, ViewDataActivity.class);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    //Handles Input of the back button, redirect to main menu
    public void onBackPressed() {
        Intent intent = new Intent(GraphMenuActivity.this,
                WelcomeActivity.class);
        startActivity(intent);

        return;
    }

}
