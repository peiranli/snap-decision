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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.common.io.Files;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class defining the functionality of the main menu screen of the application. Contains buttons
 * to move to most other screens of the app.
 */
public class WelcomeActivity extends Activity {
    static int points = -1;
    static int artificial_level = -1;
    static int level = -1;
    ConnectivityManager connectivityManager;
    private PopupWindow popupWindow;
    private ScrollView popper;
    private ScrollView popper2;
    public static ParseException uniqueCheck; //determine whether to keep searching for a new username
    public static boolean logInFailed = false; //if logged in while checking duplicate names, stop signing up
    public static int uniqueInt; //force user to have different parse username
    public static final String PREFS_NAME = "Dots_Pref";
    private boolean correctEyes = false; //user's vision available for study (survey)
    private boolean correctAge = false; //user's age available for study (survey)
    Button acceptButton;
    private String TAG = "Welcome";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String androidId = android.os.Build.MODEL;
    Encryptor encryptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        Intent startIntent = getIntent();
        //points = startIntent.getIntExtra("points", -1);
        //artificial_level = startIntent.getIntExtra("artificial_level", -1);
        //level = startIntent.getIntExtra("level", -1);
        SharedPreferences settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);
        connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));

        points = settings.getInt("points", -1);
        artificial_level = settings.getInt("artificial_level", -1);
        level = settings.getInt("level", -1);

        Button viewTermsButton = (Button) findViewById(R.id.viewTermsButton);
        //Button aboutButton = (Button) findViewById(R.id.aboutButton);
        Button playButton = (Button) findViewById(R.id.playButton);
        Button showDataButton = (Button) findViewById(R.id.showDataButton);
        acceptButton = (Button) findViewById(R.id.acceptTerms);
        popper = (ScrollView) findViewById(R.id.ScrollView01);//information about the study window
        popper2 = (ScrollView)findViewById(R.id.ScrollView02);//survey for study eligibility
        popper.setVisibility(View.GONE);
        popper2.setVisibility(View.GONE);
        System.out.println("points: " + points);
        System.out.println("artificial_level: " + artificial_level);
        System.out.println("level: " + level);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }

    public void onGetStartedClick(View v) {
        System.out.println("on get started");
    }

    /*
     * Run a test clicked, switch to the game menu screen
     */
    public void onPlayClick(View v) {
        Intent intent;
        //ParseUser user = ParseUser.getCurrentUser();
        /*if (!user.getBoolean("new"))
        {
            intent = new Intent(
                    WelcomeActivity.this,
                    NewUserMessageActivity.class);
            intent.putExtra("points", 0);
            intent.putExtra("artificial_level", 1);
            intent.putExtra("level", 1);
           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }*/

        intent = new Intent(
                WelcomeActivity.this,
                GameMenuActivity.class);
        intent.putExtra("points", points);
        intent.putExtra("artificial_level", artificial_level);
        intent.putExtra("level", level);

        startActivity(intent);
    }

    /**
     * The about button was pressed, switch to the about screen
     */
    public void onAboutClick(View v) {
        Intent intent = new Intent(
                WelcomeActivity.this,
                AboutActivity.class);
        startActivity(intent);
    }

    public void onViewTermsClick(View v) {
        showPopUp();
    }


    /**
     * Switch to the graphing screen. Can only access graphing if on wifi
     */
    public void onShowDataClick(View v) {
        /*
        Intent intent = new Intent(
                WelcomeActivity.this,
                ViewDataActivity.class);
        startActivity(intent);
        */
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            Intent i = new Intent(this, GraphMenuActivity.class);
            startActivityForResult(i, 1);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please connect to WiFi to view your data",
                    Toast.LENGTH_LONG).show();
            System.out.println("network is not connected");
        }
        //Intent i = new Intent(this, ViewDataActivity.class);

    }


    // old logout method
    public void logOutOld(View v) {

        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please connect to WiFi to view your data",
                    Toast.LENGTH_LONG).show();
            System.out.println("network is not connected");
        }
        ParseUser.logOut();
        //finish();
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + "data.txt");
        File file = new File(getExternalFilesDir(null),"data.txt");
        boolean fileExists = file.exists();
        if (fileExists)
            file.delete();
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    /**
     * Help log out, pop us out onto initial login screen
     */
    public void logOutHelper() {
        System.out.println("logging out");
        //ParseUser.logOut();
        //finish();
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + "data.txt");
        File file = new File(getExternalFilesDir(null),"data.txt");
        boolean fileExists = file.exists();
        if(fileExists)
            file.delete();
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }


    /**
     * Main logout function. Will inform the user if they are not connected to wifi,
     * as data cannot be uploaded in that case
     * @param v
     */
    public void logOut(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Are you sure you want to log out?");
        // set dialog message
        alertDialogBuilder
                .setMessage("You aren't connected to the Internet. If you log out now, trial data won't be uploaded for this session until you login again while connected to WiFi. Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        WelcomeActivity.this.logOutHelper();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            logOutHelper();
        } else {
            // show it
            alertDialog.show();
            System.out.println("network is not connected");
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private class viewTermsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }


    }

    /**
     * Decline button for the view terms task.
     * Will Stop the survey acceptance process
     * @param v
     */
    public void onDecline(View v) {
        popper.setVisibility(View.GONE);
    }

    /**
     * Accept button for the view terms task.
     * Will continue to survey acceptance process, opening another window
     * determining eligibility
     * @param v
     */
    public void onAccept(View v) {
        //syncWithServer();
        popper.setVisibility(View.GONE);
        popper2.setVisibility(View.VISIBLE);
    }

    /**
     * Survey eligibility buttons, asks about the user's
     * visual eligibility
     * @param view
     */
    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.yesRadio:
                if (checked) {
                    correctEyes = true;
                }
                break;
            case R.id.noRadio:
                if(checked) {
                    correctEyes = false;
                }
                break;
        }
    }

    /**
     * Survey eligibility test, asks about the
     * age of the user (18+ eligible)
     */
    public void checkDate() {
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date picked = new Date(year,month,day);//date picked by the user.
        Date eighteen = new Date(); //finds the current time. Used for finding eighteen years in the past
        eighteen.setYear(eighteen.getYear() - 18);
        picked.setYear(picked.getYear() - 1900); //fix date taken from datePicker
        System.err.println(eighteen + "\n" + picked);
        if(eighteen.after(picked)) {
            correctAge = true;
        }
        else {
            correctAge = false;
        }
    }

    public void onDateChanged(View v) {
        checkDate();
    }

    /**
     * The eligibility survey has been completed.
     * If the user is ineligible, the program will save info in their
     * user profile ensuring they can't sign up later
     * @param v
     */
    public void onSurveyComplete(View v) {
        //perform checks on form and allow entrance to the study if available
        checkDate();
        if(correctEyes && correctAge) {
            syncWithServer();
        }
        else {
            saveIneligible();
            Toast.makeText(WelcomeActivity.this, "Thanks for offering to share your data, " +
                    "but the research study does not include visually impaired subjects or children under 18.", Toast.LENGTH_LONG).show();
        }
        popper2.setVisibility(View.GONE);
    }


    /**
     * Saves the data marking the user as
     * ineligible for the study.
     */
    public void saveIneligible() {
        ArrayList<String> usernameFileData = new ArrayList<String>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usernameFile = prefs.getString("usernametxt", "default") + ".txt";
        //Changed to allow for easy install/uninstall
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)
        if(file.exists()) {
            System.err.println("userFile exists");
        }
        else{
            System.err.println("userFile does not exist");
        }
        Scanner usernameStream = null;
        try {
            usernameStream = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String information = null;
        try {
            information = usernameStream.nextLine();
        } catch (NoSuchElementException e) {
            information = null;
        }
        while (information != null) {
            usernameFileData.add(information);
            try {
                information = usernameStream.nextLine();
            } catch (NoSuchElementException e) {
                information = null;
            }
        }
        file.delete();
        //file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)

        try {
            FileOutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
            Log.i("GameActivity", "file is in: " + file.getAbsolutePath());
            for (int i = 0; i < LoginSignupActivity.ELIGIBLE_INDEX; i++) {
                outputStreamWriter.write(usernameFileData.get(i));
                outputStreamWriter.write("\n");
            }
            outputStreamWriter.write("false");
            outputStreamWriter.close();
            LoginSignupActivity.usernameFileData.set(LoginSignupActivity.ELIGIBLE_INDEX,"false");
        } catch (FileNotFoundException ex) {
        } catch (IOException e) {
            Log.e("s", "File write failed: " + e.toString());
        }
    }


    /**
     * Shows the view terms popup, explaining the study and offering
     * the user to accept/decline
     */
    private void showPopUp() {

        popper.setVisibility(View.VISIBLE);
        ArrayList<String> usernameFileData = new ArrayList<String>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usernameFile = prefs.getString("usernametxt", "default") + ".txt";
        //Changed to allow for easy install/uninstall
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)
        if(file.exists()) {
            System.err.println("userFile exists");
        }
        else{
            System.err.println("userFile does not exist");
        }
        Scanner usernameStream = null;
        try {
            usernameStream = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String information = null;
        try {
            information = usernameStream.nextLine();
        } catch (NoSuchElementException e) {
            information = null;
        }
        while (information != null) {
            usernameFileData.add(information);
            try {
                information = usernameStream.nextLine();
            } catch (NoSuchElementException e) {
                information = null;
            }
        }
        if(usernameFileData.get(LoginSignupActivity.ELIGIBLE_INDEX).equals("true")) {
            acceptButton.setVisibility(View.VISIBLE);
            System.err.println("User eligible for study");
        }
        else {
            acceptButton.setVisibility(View.GONE);
            System.err.println("User Ineligible for study");
        }
    }


    /**
     * Helper function for accepting the survey. Attempts to interface
     * with the Parse surveys, creating a new username for this machine,
     * unless the username password already exists, then logs in.
     */
    private void syncWithServer() {

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String usernametxt = prefs.getString("usernametxt", "default");
        final String passwordtxt = prefs.getString("passwordtxt", "default");
        int enrollment = prefs.getInt("enrollment",0);
        String usernameEncrypted = encryptor.encrypt(usernametxt,androidId);
        String fakeEmail = usernameEncrypted + "@gitdots.firebaseapp.com";
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");

        } else {
            //if(sharedPrefs.contains)
            System.out.println("network is not connected");
            Toast.makeText(getApplicationContext(),
                    "Please Connect to WiFi to enroll in the study", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(enrollment == 0){
            signUp(fakeEmail,passwordtxt);
            saveEnrollment();
        }
        signIn(fakeEmail,passwordtxt);



    }

    private void saveEnrollment(){
        ArrayList<String> usernameFileData = new ArrayList<String>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usernameFile = prefs.getString("usernametxt", "default") + ".txt";
        //Changed to allow for easy install/uninstall
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)
        if(file.exists()) {
            System.err.println("userFile exists");
        }
        else{
            System.err.println("userFile does not exist");
        }
        Scanner usernameStream = null;
        try {
            usernameStream = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String information = null;
        try {
            information = usernameStream.nextLine();
        } catch (NoSuchElementException e) {
            information = null;
        }
        while (information != null) {
            usernameFileData.add(information);
            try {
                information = usernameStream.nextLine();
            } catch (NoSuchElementException e) {
                information = null;
            }
        }
        file.delete();
        //file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)

        try {
            FileOutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
            Log.i("GameActivity", "file is in: " + file.getAbsolutePath());
            for (int i = 0; i < LoginSignupActivity.ENROLL_INDEX; i++) {
                outputStreamWriter.write(usernameFileData.get(i));
                outputStreamWriter.write("\n");
            }
            outputStreamWriter.write("1");
            outputStreamWriter.close();
            LoginSignupActivity.usernameFileData.set(LoginSignupActivity.ENROLL_INDEX,"1");
        } catch (FileNotFoundException ex) {
        } catch (IOException e) {
            Log.e("s", "File write failed: " + e.toString());
        }
    }



    /**
     * Physical back button pressed.
     * Will either remove popup boxes, or run log out.
     */
    public void onBackPressed() {

        if (popper.isShown()) {
            popper.setVisibility(View.GONE);
        }
        else if(popper2.isShown()) {
            popper2.setVisibility(View.GONE);
        }
        else
        {
            logOut(null);
        }
        return;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signIn(String username, String password){
        Log.d(TAG, "signIn:" + username);

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    public void signUp(String username, String password) {
        Log.d(TAG, "signUp:" + username);

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}


