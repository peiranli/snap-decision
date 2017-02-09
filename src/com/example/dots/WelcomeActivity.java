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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.example.dots.R.raw.error;

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
    private String androidId = Build.ID;
    Encryptor encryptor;
    private DatabaseReference mDatabase;
    int enrollment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        Intent startIntent = getIntent();
        //points = startIntent.getIntExtra("points", -1);
        //artificial_level = startIntent.getIntExtra("artificial_level", -1);
        //level = startIntent.getIntExtra("level", -1);
        SharedPreferences settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);
        String username = settings.getString("usernametxt","default");
        String password = settings.getString("passwordtxt","default");
        connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        points = settings.getInt("points", -1);
        artificial_level = settings.getInt("artificial_level", -1);
        level = settings.getInt("level", -1);
        enrollment = settings.getInt("enrollment",-1);
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
        System.out.println("enrollmentStatus: " + enrollment);

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


        if(enrollment == 1) {

            String usernameEncrypted = encryptor.encrypt(username, androidId);
            String fakeEmail = usernameEncrypted + "@gitdots.com";

            signIn(fakeEmail, password);
        }else{
            if(mAuth.getCurrentUser() != null)
                mAuth.signOut();
        }




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
        /*File file = new File(getExternalFilesDir(null),"data.txt");
        boolean fileExists = file.exists();
        if(fileExists)
            file.delete();*/
        if(mAuth.getCurrentUser() != null)
            mAuth.signOut();
        Intent intent = new Intent(this, LoginSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        String fakeEmail = usernameEncrypted + "@gitdots.com";
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

        }
        signIn(fakeEmail,passwordtxt);



    }

    private void saveEnrollment(){
        ArrayList<String> usernameFileData = new ArrayList<String>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usernameFile = prefs.getString("usernametxt", "default") + ".txt";
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("enrollment",1);
        editor.commit();
        int enrollment = prefs.getInt("enrollment",-1);
        System.out.println("saved enrollment status: " + enrollment);
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
            Log.i("WelcomeActivity", "file is in: " + file.getAbsolutePath());
            for (int i = 0; i < LoginSignupActivity.ENROLL_INDEX; i++) {
                outputStreamWriter.write(usernameFileData.get(i));
                outputStreamWriter.write("\n");
            }
            outputStreamWriter.write("1");
            outputStreamWriter.close();
            LoginSignupActivity.usernameFileData.set(LoginSignupActivity.ENROLL_INDEX,"1");
        } catch (FileNotFoundException ex) {
        } catch (IOException e) {
            Log.e("WelcomeActivity", "File write failed: " + e.toString());
        }
    }



    /**
     * Physical back button pressed.
     * Will either remove popup boxes, or run log out.
     */
    @Override
    public void onBackPressed() {

        if (popper.isShown()) {
            popper.setVisibility(View.GONE);
        }
        else if(popper2.isShown()) {
            popper2.setVisibility(View.GONE);
        }
        else
        {
            if(mAuth.getCurrentUser()!=null)
                mAuth.signOut();
            Intent intent = new Intent(this, LoginSignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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

        final Intent uploadData = new Intent(this,UploadDataService.class);
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            System.out.println("start upload service");
                            startService(uploadData);
                            //uploadLocalData();
                        }
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

        final Intent uploadData = new Intent(this,UploadDataService.class);
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        System.out.println("executing createUserWithEmail");
                        if(task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            saveEnrollment();
                            startService(uploadData);
                            //uploadLocalData();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            Log.w(TAG, "createUserWithEmail", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    public boolean online(){
        Context context = getApplicationContext();

        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
            System.out.println("network is connected");
            return true;
        } else {
            //if(sharedPrefs.contains)
            System.out.println("network is not connected");
            return false;
        }
    }

    private void uploadLocalData(){


        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString("usernametxt", "default");
        String filename =  username + "data.txt";
        File file = new File(getExternalFilesDir(null),filename);
        Encryptor encryptor = new Encryptor();
        String usernameE = encryptor.encrypt(username,androidId);
        if(file.exists()) {
            System.err.println("userFile exists");
        }
        Scanner userDataStream = null;
        try {
            userDataStream = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lastUploadedDate = userDataStream.nextLine();
        String[] parsedData = null;
        String information = null;
        List<String> tempData= new ArrayList<>();
        while(userDataStream.hasNextLine()) {
            information = userDataStream.nextLine();
            parsedData = parseLocalData(information); //decide to move to upLoadService
            tempData.add(information);
            if(lastUploadedDate.equals(""))
                break;
            if(parsedData[0].equals(lastUploadedDate))
                break;

        }
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("light").setValue(parsedData[1]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("current_Level").setValue(parsedData[2]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("trial_number").setValue(parsedData[3]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("frame_rate").setValue(parsedData[4]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("brightness").setValue(parsedData[5]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("placeholder").setValue(parsedData[6]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("placeholder2").setValue(parsedData[7]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("num_dots").setValue(parsedData[8]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("dot_size").setValue(parsedData[9]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("speed").setValue(parsedData[10]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("penalty_time").setValue(parsedData[11]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("coherence").setValue(parsedData[12]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("direction").setValue(parsedData[13]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("response").setValue(parsedData[14]);
        mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("response_time").setValue(parsedData[15]);
        lastUploadedDate = parsedData[0];
        while (userDataStream.hasNextLine()){
            information = userDataStream.nextLine();
            parsedData = parseLocalData(information);
            tempData.add(information);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("light").setValue(parsedData[1]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("current_Level").setValue(parsedData[2]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("trial_number").setValue(parsedData[3]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("frame_rate").setValue(parsedData[4]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("brightness").setValue(parsedData[5]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("placeholder").setValue(parsedData[6]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("placeholder2").setValue(parsedData[7]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("num_dots").setValue(parsedData[8]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("dot_size").setValue(parsedData[9]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("speed").setValue(parsedData[10]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("penalty_time").setValue(parsedData[11]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("coherence").setValue(parsedData[12]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("direction").setValue(parsedData[13]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("response").setValue(parsedData[14]);
            mDatabase.child("data").child(usernameE).child(parsedData[16]).child(parsedData[0]).child("response_time").setValue(parsedData[15]);
            lastUploadedDate = parsedData[0];
        }

        userDataStream.close();
        file.delete();
        file = new File(getExternalFilesDir(null),filename);


        try {
            FileOutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter dataStream = new OutputStreamWriter(os);
            Log.i("Upload Data:", "file is in: " + file.getAbsolutePath());
            dataStream.write(lastUploadedDate);
            dataStream.write("\n");
            for(int i=0;i<tempData.size();i++){
                dataStream.write(tempData.get(i));
                dataStream.write("\n");
            }
            dataStream.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mDatabase.child("users").child(usernameE).child("points").setValue(points);
        mDatabase.child("users").child(usernameE).child("artLevel").setValue(artificial_level);
        mDatabase.child("users").child(usernameE).child("level").setValue(level);


    }

    private String[] parseLocalData(String data){
        String[] result = data.split("\\t");
        String date = result[0];
        float light = Float.parseFloat(result[1]);
        String currLevel = result[2];
        int trialNumber = Integer.parseInt(result[3]);
        double framerate = Double.parseDouble(result[4]);
        double brightness = Double.parseDouble(result[5]);
        int placeholder = Integer.parseInt(result[6]);
        int placeholder2 = Integer.parseInt(result[7]);
        int numDots = Integer.parseInt(result[8]);
        int dotSize = Integer.parseInt(result[9]);
        int speed = Integer.parseInt(result[10]);
        double penaltyTime = Double.parseDouble(result[11]);
        double coherence = Double.parseDouble(result[12]);
        int direction = Integer.parseInt(result[13]);
        int response = Integer.parseInt(result[14]);
        long responseTime = Long.parseLong(result[15]);
        String gameType = result[16];
        return result;
    }
}


