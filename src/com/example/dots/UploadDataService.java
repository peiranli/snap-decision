package com.example.dots;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Hung on 7/15/2016.
 * Save data service updated for the offline version of the app.
 * Current version used by the app.
 */
public class UploadDataService extends IntentService{
        public static final String PREFS_NAME = "Dots_Pref";
        private DatabaseReference mDatabase;
        int points = -1;
        int artificial_level = -1;
        int level = -1;

    /**
     * Created by Timothy on 11/9/2014.
     */
        public UploadDataService() {
            super("UploadDataService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            points = prefs.getInt("points", -1);
            artificial_level = prefs.getInt("artificial_level", -1);
            level = prefs.getInt("level", -1);
            System.out.println("uploading offline data");
            uploadLocalData();
        }


    private void uploadLocalData(){


        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString("usernametxt", "default");
        String filename =  username + "data.txt";
        File file = new File(getExternalFilesDir(null),filename);
        Encryptor encryptor = new Encryptor();
        String usernameE = encryptor.encrypt(username,Build.ID);
        if(file.exists()) {
            System.err.println("userFile exists");
        }
        Scanner userDataStream = null;
        try {
            userDataStream = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(userDataStream == null)
            return;
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
