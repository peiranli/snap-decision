package com.example.dots;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * The login and signup activity of the app.
 * Asks for a username and password, when signup is hit,
 * username is encrypted and saved as a file.
 * Login credentials are then saved on the device.
 */
public class LoginSignupActivity extends Activity implements View.OnClickListener {
    // Declare Variables
    final Context cont = this;
    final public static int NUM_DATA_ELEMENTS = 5;
    Button loginbutton;
    Button signup;
    Button runOffline;
    String usernametxt;
    String passwordtxt;
    String nameOfUser;
    public static ArrayList<String> usernameFileData = new ArrayList<String>();
    EditText password;
    EditText username;
    Encryptor encryptor;
    java.util.Date date; // date for DOB Check
    private ProgressDialog pd;
    public static final String PREFS_NAME = "Dots_Pref";
    public static final int POINTS_INDEX = 4;
    public static final int ART_LEVEL_INDEX = 5;
    public static final int LEVEL_INDEX = 6;
    public static final int ELIGIBLE_INDEX = 7;
    public static final int ENROLL_INDEX = 8;
    SharedPreferences sharedPrefs;
    boolean offline = false;
    boolean clicked = false;

    private String TAG = "LoginSignup";
    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usernameFileData = new ArrayList<String>();
        final Context context = getApplicationContext();
        encryptor = new Encryptor();
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        sharedPrefs = getSharedPreferences("users", Context.MODE_PRIVATE);
        // Get the view from loginsignup.xml
        setContentView(R.layout.loginsignup);
        // Locate EditTexts in main.xml
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.signup).setOnClickListener(this);
        // Locate Buttons in loginsignup.xml
        loginbutton = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);
        //runOffline = (Button) findViewById(R.id.runOffline);

    }








    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        if(datePicker!=null) {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            return calendar.getTime();
        }
        else
            return null;
    }






    private void usernameSignupFileParse() {
        usernametxt = username.getText().toString();
        passwordtxt = password.getText().toString();
        String usernameFile = usernametxt + ".txt";
        //Changed for easy install/uninstall
        //File file = new File(Environment.getExternalStorageDirectory()+"/"+usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);
        boolean fileExists = file.exists();
        if(fileExists) {
            System.out.println("Username File Exists, Not performing signup");
            Toast.makeText(getApplicationContext(),
                    "Error, Username already in use",
                    Toast.LENGTH_LONG).show();

        }
        else {
            try {
                FileOutputStream os = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                Log.i("SignupActivity", "file is in: " + file.getAbsolutePath());
                outputStreamWriter.write(usernametxt);
                outputStreamWriter.write("\n");
                outputStreamWriter.write(passwordtxt);
                outputStreamWriter.write("\n");
                outputStreamWriter.write("device_id" + android.os.Build.MODEL);
                outputStreamWriter.write("\n");
                outputStreamWriter.write("user" + usernametxt);
                outputStreamWriter.write("\n");
                outputStreamWriter.write("0");//for initial points
                outputStreamWriter.write("\n");
                outputStreamWriter.write("1");//for initial artificial level
                outputStreamWriter.write("\n");
                outputStreamWriter.write("1");//for initial level
                outputStreamWriter.write("\n");
                outputStreamWriter.write("true");//for study availability
                outputStreamWriter.write("\n");
                outputStreamWriter.write("0");//for enroll information
                outputStreamWriter.close();

            }
            catch (FileNotFoundException ex) {
            } catch (IOException e) {
                Log.e("s", "File write failed: " + e.toString());
            }
            /*Set up a new data file for the user*/
            String dataFileString = usernametxt + "DataFile.txt";
            File dataFile = new File(getExternalFilesDir(null), dataFileString);
            System.out.println("LoginSignupActivity: new file");
            Toast.makeText(getApplicationContext(),
                    "Sign-up Successful! Please Login to continue",
                    Toast.LENGTH_LONG).show();
            try {
                System.out.println("writing to new file");
                FileOutputStream os = new FileOutputStream(dataFile, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                outputStreamWriter.write("Android/" + android.os.Build.MODEL + "\n");
                outputStreamWriter.write(android.os.Build.VERSION.SDK_INT + "\n");
                outputStreamWriter.write("defaultId\n");
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                outputStreamWriter.write(dm.heightPixels + " " + dm.widthPixels + "\n");
                outputStreamWriter.write("\n");
                outputStreamWriter.close();
            } catch (FileNotFoundException ex) {

                } catch (IOException ex) {
                    //Log.e("LoginSignUpActivity", "File write failed: " + e.toString());
                }

        }
    }
    private void usernameLoginFileParse() {
        usernametxt = username.getText().toString();
        passwordtxt = password.getText().toString();
        String usernameFile = usernametxt + ".txt";
        // 1 and 0 after brightness are placeholders for now, as well as 60 for framerate
        // and 35, which is dot size
        //File file = new File(Environment.getExternalStorageDirectory()+"/"+usernameFile);
        File file = new File(getExternalFilesDir(null), usernameFile);
        boolean fileExists = file.exists();
        if(fileExists) {
            System.out.println("Username File Exists, Attempting to log in");
            //for populating personal data from file. to be held in arraylist in strings
            Scanner usernameStream = null;
            try {
                usernameStream = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String information = null;
            try {
                information = usernameStream.nextLine();
            }
            catch(NoSuchElementException e) {
                information = null;
            }
            while(information != null) {
                usernameFileData.add(information);
                try {
                    information = usernameStream.nextLine();
                }
                catch(NoSuchElementException e) {
                    information = null;
                }
            }
            if(usernameFileData.size() < 2) {
                Toast.makeText(getApplicationContext(),
                        "Error, username is invalid, please try a different username",
                        Toast.LENGTH_LONG).show();
            }
            else if(usernametxt.equals(usernameFileData.get(0)) && passwordtxt.equals(usernameFileData.get(1))) {
                /*Toast.makeText(getApplicationContext(),
                        "Login Successful! Preparing personal data",
                        Toast.LENGTH_LONG).show();*/

                UserInfo.setUsername(usernametxt);//set a global variable so the username file can be retrieved.
                    //intent.putExtra("points", p.getInt("points"));
                    // intent.putExtra("artificial_level", p.getInt("artificial_level"));
                    // intent.putExtra("level", p.getInt("level"));
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("points", Integer.parseInt(usernameFileData.get(POINTS_INDEX)));
                    editor.putInt("artificial_level", Integer.parseInt(usernameFileData.get(ART_LEVEL_INDEX)));
                    editor.putInt("level", Integer.parseInt(usernameFileData.get(LEVEL_INDEX)));
                    editor.putString("username_file", usernameFile);
                    editor.putString("usernametxt", usernametxt);
                    editor.putString("passwordtxt", passwordtxt);
                    editor.putInt("enrollment", Integer.parseInt(usernameFileData.get(ENROLL_INDEX)));
                    // Commit the edits!
                    editor.commit();
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    //Login();
                Intent intent;
                intent = new Intent(
                        LoginSignupActivity.this,
                        WelcomeActivity.class);
                startActivity(intent);
                System.out.println("points; "+Integer.parseInt(usernameFileData.get(POINTS_INDEX)) + " artificial_level: " +
                        Integer.parseInt(usernameFileData.get(ART_LEVEL_INDEX)));
                Toast.makeText(getApplicationContext(),
                        "Successfully Logged in online",
                        Toast.LENGTH_LONG).show();




                Toast.makeText(getApplicationContext(),
                        "Successfully Logged in",
                        Toast.LENGTH_LONG).show();
                 finish();

            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Login Failed, Double check username/password",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Error, username doesn't exist, please sign up",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String username = this.username.getText().toString();
        if (TextUtils.isEmpty(username)) {
            this.username.setError("Required.");
            valid = false;
        } else {
            this.username.setError(null);
        }

        /*String password = this.password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            this.password.setError("Required.");
            valid = false;
        } else {
            this.password.setError(null);
        }*/

        return valid;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.login){
            usernameLoginFileParse();
            /*if(online()) {
                signIn(fakeEmail,
                        encryptor.encrypt(password.getText().toString(), androidId));
            }*/
        }
        else if(id == R.id.signup){
            usernameSignupFileParse();
            /*if(online()) {
                signUp(fakeEmail,
                        encryptor.encrypt(password.getText().toString(), androidId));
            }*/


        }
    }


}