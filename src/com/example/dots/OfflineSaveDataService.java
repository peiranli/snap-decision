package com.example.dots;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Hung on 7/15/2016.
 * Save data service updated for the offline version of the app.
 * Current version used by the app.
 */
public class OfflineSaveDataService extends IntentService{
        public static final String PREFS_NAME = "Dots_Pref";
    /**
     * Created by Timothy on 11/9/2014.
     */
        public OfflineSaveDataService() {
            super("OfflineSaveDataService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            final Intent innerIntent = intent;
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String fileName = "data.txt";
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
            // 1 and 0 after brightness are placeholders for now, as well as 60 for framerate
            // and 35, which is dot size
            String data = date+"\t"+light+"\t"+currLevel+"\t"+trialNumber+"\t"+60+"\t"+brightness+
                    "\t"+1+"\t"+0+"\t"+numDots+"\t"+35+"\t"+speed+"\t"+
                    penaltyTime+"\t"+coherence+"\t"+direction+"\t"+
                    response+"\t"+responseTime+"\t"+gameType+"\n";
            //File file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
            File file = new File(getExternalFilesDir(null),LoginSignupActivity.usernameFileData.get(0) + "data.txt");
            boolean fileExists = file.exists();
            if(fileExists)
                System.out.println("The file exists");
            try {
                FileOutputStream os = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                Log.i("GameActivity", "file is in: " + file.getAbsolutePath());
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch(FileNotFoundException ex) {
                    System.err. println("Data.txt doesn't exist");
            }
            catch (IOException e) {Log.e("s", "File write failed: " + e.toString());
            }

        }
    }
