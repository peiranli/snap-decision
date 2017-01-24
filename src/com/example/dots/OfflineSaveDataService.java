package com.example.dots;

import android.app.IntentService;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Hung on 7/15/2016.
 * Save data service updated for the offline version of the app.
 * Current version used by the app.
 */
public class OfflineSaveDataService extends IntentService{
    /**
     * Created by Timothy on 11/9/2014.
     */
        public OfflineSaveDataService() {
            super("OfflineSaveDataService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // getting user data
            ParseQuery<ParseObject> query = ParseQuery.getQuery("TrialData");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("device_id", android.os.Build.MODEL);
            ParseObject p = new ParseObject("TrialData");
            try {
                p = query.getFirst();
                if(p == null) {
                    p = new ParseObject("TrialData");
                    p.put("user", ParseUser.getCurrentUser());
                    p.put("device_id", android.os.Build.MODEL);
                }
            }
            catch (ParseException ex)
            {
            }
            byte[] fileByteArr =  intent.getByteArrayExtra("byteArr");
            ParseFile dataFile = new ParseFile(fileByteArr);
            dataFile.saveInBackground();
            System.out.println("save data");
            int points = intent.getIntExtra("points", -1);
            int artificial_level = intent.getIntExtra("artificial_level", -1);
            int level = intent.getIntExtra("level", -1);
            p.put("points", points);
            p.put("artificial_level", artificial_level);
            p.put("dataFile", dataFile);
            p.put("user", ParseUser.getCurrentUser());
            p.put("device_id", android.os.Build.MODEL);
            p.put("level", level);
            //p.saveInBackground();
            //p.saveEventually(new );
            p.pinInBackground();
            p.saveInBackground();

        }
    }
