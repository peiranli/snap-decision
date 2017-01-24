package com.example.dots;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Old message used to confirm acceptance of the study.
 * Is no longer in use.
 */
public class NewUserMessageActivity extends Activity {

    static String msg = "Welcome! This app is part of a research study at UC San Diego. By " +
            "tapping the \"I agree\" button below, you indicate that you understand that the app's data " +
            "will be used for research purposes and you agree to allow us to use that data. The data " +
            "collected does NOT contain any personal data, but only performance statistics, " +
             "such as accuracy, etc. If you tap \"I do not agree\" the application will exit.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickIAgree();
        /*setContentView(R.layout.activity_new_user_message);
        TextView message = (TextView)findViewById(R.id.newUserMessage);
        message.setText(msg);*/ /*commented out message in lieu of other terms agreement*/

    }


    public void clickIAgree(View v)
    {
        Intent startIntent = getIntent();
        Intent intent = new Intent(
                NewUserMessageActivity.this,
                GameMenuActivity.class);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("new", true);
        user.saveInBackground();
        intent.putExtra("points", startIntent.getIntExtra("points", -1));
        intent.putExtra("artificial_level", startIntent.getIntExtra("artificial_level", -1));
        intent.putExtra("level", startIntent.getIntExtra("level", -1));
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    /*Duplicate method used to override message, kept around in case message is needed again*/
    public void clickIAgree()
    {
        Intent startIntent = getIntent();
        Intent intent = new Intent(
                NewUserMessageActivity.this,
                GameMenuActivity.class);
        ParseUser user = ParseUser.getCurrentUser();
        user.put("new", true);
        user.saveInBackground();
        intent.putExtra("points", startIntent.getIntExtra("points", -1));
        intent.putExtra("artificial_level", startIntent.getIntExtra("artificial_level", -1));
        intent.putExtra("level", startIntent.getIntExtra("level", -1));
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
    public void clickIDontAgree(View v)
    {
        //ParseUser user = ParseUser.getCurrentUser();
        //user.logOut();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_user_message, menu);
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
}
