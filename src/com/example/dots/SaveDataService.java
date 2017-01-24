package com.example.dots;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.google.common.io.Files;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by Timothy on 11/9/2014.
 * Helper class designed to assist game activities in saving to
 * server. Uses parse logins currently. Uploads data.txt from
 * local files.
 */
public class SaveDataService extends IntentService {
    ArrayList<String> usernameFileData = new ArrayList<String>();
    int userPoints; // New point value to be updated
    int userArtLevel; //artificial level of the user
    int userLevel; //actual level of the user
    public static final int POINTS_INDEX = 4;
    public static final int ART_LEVEL_INDEX = 5;
    public static final int LEVEL_INDEX = 6;
    public static final String PREFS_NAME = "Dots_Pref";
    private DatabaseReference mDatabase;



    public SaveDataService() {
        super("SaveDataService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // getting user data
        final Intent innerIntent = intent;
        userPoints = intent.getIntExtra("points", -1);
        userArtLevel = intent.getIntExtra("artificial_level", -1);
        userLevel = intent.getIntExtra("level", -1);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String usernameFile = prefs.getString("usernametxt", "default") + ".txt";
        String username = prefs.getString("usernametxt", "default");
        Encryptor encryptor = new Encryptor();
        String usernameE = encryptor.encrypt(username,android.os.Build.MODEL);
        //Changed to allow for easy install/uninstall
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)
        if(file.exists()) {
            System.err.println("userFile exists");
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
        usernameFileData.set(POINTS_INDEX, Integer.toString(userPoints));
        usernameFileData.set(ART_LEVEL_INDEX, Integer.toString(userArtLevel));
        usernameFileData.set(LEVEL_INDEX, Integer.toString(userLevel));
        file.delete();
        //file = new File(Environment.getExternalStorageDirectory() + "/" + usernameFile);
        file = new File(getExternalFilesDir(null), usernameFile);        //read in previous information(mostly for username/password)

        try {
            FileOutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
            Log.i("GameActivity", "file is in: " + file.getAbsolutePath());
            for (int i = 0; i < POINTS_INDEX; i++) {
                outputStreamWriter.write(usernameFileData.get(i));
                outputStreamWriter.write("\n");
            }

            outputStreamWriter.write(Integer.toString(userPoints)); //toString to comply with previous formatting
            outputStreamWriter.write("\n");
            outputStreamWriter.write(Integer.toString(userArtLevel));
            outputStreamWriter.write("\n");
            outputStreamWriter.write(Integer.toString(userLevel));
            outputStreamWriter.write("\n");
            outputStreamWriter.write(usernameFileData.get(LoginSignupActivity.ELIGIBLE_INDEX));
            outputStreamWriter.write("\n");
            outputStreamWriter.write(usernameFileData.get(LoginSignupActivity.ENROLL_INDEX));
            outputStreamWriter.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException e) {
            Log.e("s", "File write failed: " + e.toString());
        }
        //Parse data upload, altered to support offline usage -Hung 07 / 20 / 16
        Context context = getApplicationContext();
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (currentNetworkInfo != null && currentNetworkInfo.isConnected() && mDatabase != null) {
            mDatabase.child("users").child(usernameE).child("device_id").setValue(android.os.Build.MODEL);
            //String data = innerIntent.getStringExtra("data");
            //mDatabase.child("users").child(usernameE).child("data").setValue(data);
            //byte[] fileByteArr = innerIntent.getByteArrayExtra("byteArr");
            /*ParseFile dataFile = new ParseFile(fileByteArr);
            dataFile.saveInBackground();*/
            String date = innerIntent.getStringExtra("date");
            float light = innerIntent.getFloatExtra("light",0);
            String currLevel = innerIntent.getStringExtra("currLevel");;
            int trialNumber = innerIntent.getIntExtra("trial_number",0);
            double framerate = innerIntent.getDoubleExtra("framerate",60);
            double brightness = innerIntent.getDoubleExtra("brightness",0);
            int placeholder = innerIntent.getIntExtra("placeholder",1);
            int placeholder2 = innerIntent.getIntExtra("placeholder2",0);
            int numDots = innerIntent.getIntExtra("numDots",0);
            int dotSize = innerIntent.getIntExtra("dot_size",35);
            int speed = innerIntent.getIntExtra("speed",0);
            double penaltyTime = innerIntent.getDoubleExtra("penalty_time",0);
            double coherence = innerIntent.getDoubleExtra("coherence",0);
            int direction = innerIntent.getIntExtra("direction",-1);
            int response = innerIntent.getIntExtra("response",0);
            long responseTime = innerIntent.getLongExtra("response_time",0);
            String gameType = innerIntent.getStringExtra("game_type");

            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("light").setValue(light);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("current_Level").setValue(currLevel);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("trial_number").setValue(trialNumber);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("frame_rate").setValue(framerate);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("brightness").setValue(brightness);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("placeholder").setValue(placeholder);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("placeholder2").setValue(placeholder2);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("num_dots").setValue(numDots);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("dot_size").setValue(dotSize);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("speed").setValue(speed);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("penalty_time").setValue(penaltyTime);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("coherence").setValue(coherence);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("direction").setValue(direction);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("response").setValue(response);
            mDatabase.child("data").child(usernameE).child(gameType).child(date).child("response_time").setValue(responseTime);
            System.out.println("save data");
            int points = innerIntent.getIntExtra("points", -1);
            int artificial_level = innerIntent.getIntExtra("artificial_level", -1);
            int level = innerIntent.getIntExtra("level", -1);
            mDatabase.child("users").child(usernameE).child("points").setValue(points);
            mDatabase.child("users").child(usernameE).child("artLevel").setValue(artificial_level);
            mDatabase.child("users").child(usernameE).child("level").setValue(level);
                            /*ParseQuery<ParseObject> query = ParseQuery.getQuery("TrialData");
                            query.whereEqualTo("user", ParseUser.getCurrentUser());
                            query.whereEqualTo("device_id", android.os.Build.MODEL);
                            ParseObject p = new ParseObject("TrialData");
                            try {
                                p = query.getFirst();
                                if (p == null) {
                                    p = new ParseObject("TrialData");
                                    p.put("user", ParseUser.getCurrentUser());
                                    p.put("device_id", android.os.Build.MODEL);
                                }
                            } catch (ParseException ex) {
                            }*/



                            /*p.put("points", points);
                            p.put("artificial_level", artificial_level);
                            p.put("dataFile", dataFile);
                            p.put("user", ParseUser.getCurrentUser());
                            p.put("device_id", android.os.Build.MODEL);
                            p.put("level", level);
                            //p.saveInBackground();
                            //p.saveEventually(new );
                            p.pinInBackground();
                            p.saveInBackground();*/
                        }


         else {
            //if(sharedPrefs.contains)
            System.out.println("network is not connected");
        }


    }
}
