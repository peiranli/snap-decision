package com.example.dots;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.common.io.Files;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Timed Main game version of the app. Is based on the default version of the game
 */
public class TimedGameActivity extends Activity implements SensorEventListener {
    public static boolean timesUp = false;
    public static int trialsCorrect = 0; // for updating score text
    public static int direction = 0;
    public static Level currLevel = new LevelOne();
    public static boolean start = false;
    private static int response = 0;
    private static long responseTime;
    private static long startTime, endTime;
    public static Dot[] dirDotArray, randDotArray;
    public static boolean newTrial = true;
    private static int artificial_level = 1, points = 0, step = 50;
    private static ParseUser user;
    private static NumberProgressBar progressBar;
    private static SharedPreferences settings;
    private TextView mTextField;
    private static Level[] levels = {
            new LevelOne(), new LevelTwo(), new LevelThree(), new LevelFour(), new LevelFive(),
            new LevelSix(), new LevelSeven(), new LevelEight()
    };
    private SensorManager mSensorManager;
    private AudioManager mAudioManager;
    private Sensor mLight;
    private static CountDownTimer timer;
    private static float light = -1;
    private static String date = "None";
    private static long gameStartTime = System.currentTimeMillis();
    ConnectivityManager connectivityManager;
    //private static

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trialsCorrect = 0;
        //user = ParseUser.getCurrentUser();
        settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);

        // int userLevel = getIntent().getIntExtra("level", 1);
        int fixedLevel = 4;
        initLevel(levels[fixedLevel]); //Hung removed a -1 to fix an out of bounds index

        setContentView(R.layout.timed_game_activity);
        mTextField = (TextView) findViewById(R.id.timer);
        initializeButtons();
        start = false;
        timesUp = false;
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
                writeToFile();
                timesUp = true;
            }

        }.start();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        TextView info = (TextView) findViewById(R.id.info);
        info.setText("Score: " + trialsCorrect);

    }

    protected void onResume() {
        timesUp = false;
        mTextField = (TextView) findViewById(R.id.timer);
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
                writeToFile();
                timesUp = true;
            }

        }.start();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please turn up your volume to play the game!").setCancelable(false)
                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog message = builder.create();
            message.show();
        }
        super.onResume();
    }

    protected void onPause() {

        timer.cancel();
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "Accuracy :" + accuracy);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            Log.i("Sensor Changed", "onSensor Change :" + event.values[0]);
            light = event.values[0];
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    void initLevel(Level lvl) {
        currLevel = lvl;
        int numDirDots = (int) (currLevel.getNumDots() * currLevel.getCoherence());
        dirDotArray = new Dot[numDirDots];
        int numRandDots = (int) (currLevel.getNumDots() - currLevel.getNumDots() * currLevel.getCoherence());
        randDotArray = new Dot[numRandDots];
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getRealMetrics(metrics);
        System.out.println("height: " + metrics.heightPixels);
        System.out.println("width: " + metrics.widthPixels);
        System.out.println("number of dots: " + numDirDots);
        //percentage of screen size
        //int dot_size = (int)(metrics.heightPixels*0.03);
        // flat screen size
        //int dot_size = 25;
        int dot_sizeX = (int) (0.0393701 * metrics.xdpi);
        int dot_sizeY = (int) (0.0393701 * metrics.ydpi);
        for (int i = 0; i < dirDotArray.length; i++) {
            dirDotArray[i] = new Dot();
            dirDotArray[i].setBitmapDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.small_white_dot));
            dirDotArray[i].setBitmapDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(dirDotArray[i].getBitmap(), dot_sizeX, dot_sizeY, false)));
        }
        for (int i = 0; i < randDotArray.length; i++) {
            randDotArray[i] = new Dot();
            randDotArray[i].setBitmapDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.small_white_dot));
            randDotArray[i].setBitmapDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(randDotArray[i].getBitmap(), dot_sizeX, dot_sizeY, false)));
        }
        start = false;
    }

    void initializeButtons() {
        Button center = (Button) findViewById(R.id.center);
        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        TimedAnimatedView animation = (TimedAnimatedView) findViewById(R.id.anim_view);
        TimedRandDotsView rand = (TimedRandDotsView) findViewById(R.id.rand_dots);
        TextView info = (TextView) findViewById(R.id.info);
        progressBar = (NumberProgressBar) findViewById(R.id.progressBar);

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
        Intent startingIntent = getIntent();
        //points = startingIntent.getIntExtra("points", -1);
        // artificial_level = startingIntent.getIntExtra("artificial_level", -1);


        //System.out.println("points; "+user.getInt("points") + " artificial_level: " +  user.getInt("artificial_level"));
        //center.setId(1);
        //left.setId(2);
        //right.setId(3);

        //center.setOnTouchListener(new MyTouchListener());
        //left.setOnTouchListener(new MyTouchListener());
        // right.setOnTouchListener(new MyTouchListener());

        left.setClickable(false);
        right.setClickable(false);
    }

    private void writeToFile() {

        String fileName = "data.txt";
        // 1 and 0 after brightness are placeholders for now, as well as 60 for framerate
        // and 35, which is dot size
        String data = date+"\t"+light+"\t"+currLevel+"\t"+currLevel.getTrialNumber()+"\t"+60+"\t"+currLevel.getBrightness()+
                "\t"+1+"\t"+0+"\t"+currLevel.getNumDots()+"\t"+35+"\t"+currLevel.getSpeed()+"\t"+
                currLevel.getPenaltyTime()+"\t"+currLevel.getCoherence()+"\t"+direction+"\t"+
                response+"\t"+responseTime+"\n";
        //File file = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
        File file = new File(getExternalFilesDir(null),/*LoginSignupActivity.usernameFileData.get(0) +*/ "data.txt");
        boolean fileExists = file.exists();
        if(fileExists)
            System.out.println("The file exists");
        try {
            FileOutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
            Log.i("GameActivity", "file is in: " + file.getAbsolutePath());
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Intent saveData = new Intent(this, SaveDataService.class);
            //saveData.putExtra("data", data);
            saveData.putExtra("game_type","test speed");
            saveData.putExtra("points", points);
            saveData.putExtra("artificial_level", artificial_level);
            saveData.putExtra("level", currLevel.getLevel());
            saveData.putExtra("date",date);
            saveData.putExtra("light",light);
            saveData.putExtra("currLevel",currLevel.toString());
            saveData.putExtra("trial_number",currLevel.getTrialNumber());
            saveData.putExtra("framerate",60);
            saveData.putExtra("brightness",currLevel.getBrightness());
            saveData.putExtra("placeholder",1);
            saveData.putExtra("placeholder2",0);
            saveData.putExtra("numDots",currLevel.getNumDots());
            saveData.putExtra("dot_size",35);
            saveData.putExtra("speed",currLevel.getSpeed());
            saveData.putExtra("penalty_time",currLevel.getPenaltyTime());
            saveData.putExtra("coherence",currLevel.getCoherence());
            saveData.putExtra("direction",direction);
            saveData.putExtra("response",response);
            saveData.putExtra("response_time",responseTime);
            startService(saveData);
        }
        catch(FileNotFoundException ex) {
            System.err. println("Data.txt doesn't exist");
        }
        catch (IOException e) {Log.e("s", "File write failed: " + e.toString());
        }
    }

    public void clickLeft(View v) {
        response = 0;
        processResponse();
    }

    public void clickRight(View v) {
        response = 1;
        processResponse();
    }

    public void clickCenter(View v) {
        //timer specific, end game if out of time
        if (timesUp) {

            NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

           // if (currentNetworkInfo != null && currentNetworkInfo.isConnected()) {
                System.out.println("network is connected");
                Intent i = new Intent(this, GameMenuActivity.class);
                startActivityForResult(i, 1);
                finish();
           // }
        }

        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        Button center = (Button) findViewById(R.id.center);
        System.out.println("Clicking center");
        left.setClickable(true);
        right.setClickable(true);
        center.setClickable(false);
        direction = currLevel.getDirection();
        start = true;
        newTrial = true;
        startTime = SystemClock.elapsedRealtime();
    }

    private void processResponse() {
        TextView info = (TextView) findViewById(R.id.info);
        boolean correct = false;
        endTime = SystemClock.elapsedRealtime();
        responseTime = endTime - startTime;
        Button center = (Button) findViewById(R.id.center);
        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        System.out.println("Clicking left");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        date = df.format(Calendar.getInstance().getTime());
        if (response == 0) {
            if (direction == 0) //0 is left
            {
                currLevel.addTrial(1);
                correct = true;
                points++;
                trialsCorrect++;
                //info.setText("Points:     " + points + "/" + step + " Level:   " + artificial_level);
                info.setText("Score: " + trialsCorrect);
                progressBar.setProgress((int) (((double) points / step) * 100));
            } else {
                currLevel.addTrial(0);
            }

        }
        if (response == 1) {
            System.out.println("Clicking right");
            if (direction == 1) //1 is false
            {
                currLevel.addTrial(1);
                correct = true;
                points++;
                trialsCorrect++;
                //info.setText("Points:     " + points + "/" + step + " Level:   " + artificial_level);
                info.setText("Score: " + trialsCorrect);
                progressBar.setProgress((int) (((double) points / step) * 100));
            } else {
                currLevel.addTrial(0);
            }
        }
        start = false;
        if (correct) // play sound for right or wrong
        {
            playCorrectSound();
            //center.setClickable(true);
            //left.setClickable(false);
            //right.setClickable(false);
        } else {
            playIncorrectSound();
            timeOut();
        }
        if (points >= step) {
            //playGradMessageSound();
            progressBar.setProgress(0);
            points = 0;
            artificial_level++;
            //info.setText("Points:   " + points + "/" + step + " Level:    " + artificial_level);
            info.setText("Score: " + trialsCorrect);
            //initNextStep(); //change this to better name later, like nextStep()
        }
        SharedPreferences settings = getSharedPreferences(LoginSignupActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("points", points
        );
        editor.putInt("artificial_level", artificial_level);
        editor.putInt("level", currLevel.getLevel());
        // Commit the edits!
        //editor.commit();
        // write data
        writeToFile();
        //check for graduation
        if (currLevel.graduateLevel()) {
            currLevel = levels[currLevel.getLevel()];
            initLevel(currLevel);
        }
        initLevel(currLevel);
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (currLevel.getTrialNumber() % 10 == 0) {

            //Disabled because of offline support
            /*if (currentNetworkInfo == null || !currentNetworkInfo.isConnected())
                Toast.makeText(getApplicationContext(),
                        "Please connect to WiFi to synchronize your data",
                        Toast.LENGTH_LONG).show();*/
        }
        if ((System.currentTimeMillis() - gameStartTime) > 20000) {
            Intent intent = new Intent(
                    TimedGameActivity.this,
                    TimedGraphActivity.class

            );
        }
        center.setClickable(true);
        left.setClickable(false);
        right.setClickable(false);
        start = false;

    }


    private void playCorrectSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.correctsound);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }

    private void playGradMessageSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.levelup);
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        SystemClock.sleep(500);
        mp.start();
        //level up dialog box with okay button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Congratulations! Level up!").setCancelable(false)
                .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog message = builder.create();
        message.show();
        start = false;
    }

    private void playIncorrectSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.error);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }

    public void logOut(View v) {
        ParseUser.logOut();
        //finish();
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + "timedData.txt");
        File file = new File(getExternalFilesDir(null), /*LoginSignupActivity.usernameFileData.get(0) +*/ "timedData.txt");
        boolean fileExists = file.exists();
        if (fileExists)
            file.delete();
        Intent intent = new Intent(TimedGameActivity.this, LoginSignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void initNextStep() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.levelup);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        SystemClock.sleep(500);
        mp.start();
        //level up dialog box with okay button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Congratulations! Level up!").setCancelable(false)
                .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog message = builder.create();
        message.show();
    }

    public void timeOut() {
        Button center = (Button) findViewById(R.id.center);
        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        TimedAnimatedView animation = (TimedAnimatedView) findViewById(R.id.anim_view);
        TimedRandDotsView rand = (TimedRandDotsView) findViewById(R.id.rand_dots);

        center.setClickable(false);
        left.setClickable(false);
        right.setClickable(false);
        // make views invisible
        center.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
        animation.setVisibility(View.INVISIBLE);
        rand.setVisibility(View.INVISIBLE);
        new CountDownTimer((long) currLevel.getPenaltyTime() * 1000, 1000) {
            Button center = (Button) findViewById(R.id.center);
            Button left = (Button) findViewById(R.id.left);
            Button right = (Button) findViewById(R.id.right);
            TimedAnimatedView animation = (TimedAnimatedView) findViewById(R.id.anim_view);
            TimedRandDotsView rand = (TimedRandDotsView) findViewById(R.id.rand_dots);

            public void onTick(long arg0) {
            }

            public void onFinish() {
                center.setClickable(true);
                center.setVisibility(View.VISIBLE);
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                left.setClickable(false);
                right.setClickable(false);
                animation.setVisibility(View.VISIBLE);
                rand.setVisibility(View.VISIBLE);
            }

        }.start();

        //start = true;
        //center.setPressed(true);
    }

/*
    class MyTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView info = (TextView)findViewById(R.id.info);
            boolean correct = false;
            endTime = SystemClock.elapsedRealtime();
            responseTime = endTime - startTime;
            Button center = (Button)findViewById(R.id.center);
            Button left = (Button)findViewById(R.id.left);
            Button right = (Button)findViewById(R.id.right);
            int id = v.getId();
            switch (id) {
                case 1: //center
                    System.out.println("Clicking center");
                    left.setClickable(true);
                    right.setClickable(true);
                    direction = currLevel.getDirection();
                    start = true;
                    startTime = SystemClock.elapsedRealtime();
                    break;
                case 2: //left
                    System.out.println("Clicking left");
                    response = 0;
                    if(direction == 0) //0 is left
                    {
                        currLevel.addTrial(1);
                        correct = true;
                        points++;
                        info.setText("Points: "+ points + " Level: "+ artificial_level);
                        progressBar.incrementProgressBy(1);
                    }
                    else
                    {
                        currLevel.addTrial(0);
                    }

                    break;
                case 3: //right
                    System.out.println("Clicking right");
                    response = 1;
                    if(direction == 1) //1 is false
                    {
                        currLevel.addTrial(1);
                        correct = true;
                        points++;
                        info.setText("Points: "+ points + " Level: "+ artificial_level);
                        progressBar.incrementProgressBy(1);
                    }
                    else
                    {
                        currLevel.addTrial(0);
                    }
                    break;
            }
            if(id == 2 || id ==3)
            {
                start = false;
                if(correct) // play sound for right or wrong
                {
                    playCorrectSound();
                }
                else
                {
                    playIncorrectSound();
                }
                if(points == 200)
                {
                    progressBar.setProgress(0);
                    points = 0;
                    artificial_level++;
                    info.setText("Points: "+ points + " Level: "+ artificial_level);
                    initNextStep(); //change this to better name later, like nextStep()
                }

                // write data
                writeToFile();
                //check for graduation
                if(currLevel.graduateLevel())
                {
                    initLevel(new LevelTwo());
                }
            }
            return true;
        }
    } */

    public void onBackClick(View v) {
        //onBackPressed();

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
