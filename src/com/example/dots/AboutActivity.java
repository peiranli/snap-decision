package com.example.dots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by Timothy on 11/17/2014.
 * Details the about page of the program. Displays a string giving information about the app
 */
public class AboutActivity extends Activity {

    static String msg = "http://www.google.com";
    private static boolean termsClicked = false;
    static String terms = "Terms and Conditions";
    //App information text
    static String htmlText = "<p>This app is a project made by the Reinagel lab at UC San Diego. More information about the "+
            " lab can be found at its website at <a href=http://reinagellab.org/>http://reinagellab.org<a> For more information or inquiries, please email visRT@ratrix.org.</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        TextView message = (TextView)findViewById(R.id.AboutMessage);
        //message.setText(msg);
        message.setText(Html.fromHtml(htmlText));
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setGravity(Gravity.CENTER);
        //message.setText(msg);

        //textView.setText(Html.fromHtml(textContent, mImageGetter, null));

    }
    /**
    public void onViewTermsClick(View v)
    {
        TextView terms = (TextView)findViewById(R.id.TermsConditions);
        Button viewTermsButton = (Button) findViewById(R.id.ViewTermsButton);
        if(!termsClicked)
        {
            viewTermsButton.setText("Hide Terms");
            terms.setText(R.string.terms_and_conditions);
           // terms.setText(terms);
            termsClicked = true;
        }
        else
        {
            viewTermsButton.setText("View Terms");
            terms.setText("");
            termsClicked = false;
        }
    } */

    /*
     * Returns to the Main menu of the application when touch screen back button pressed
     */
    public void onBackClick(View v)
    {
        Intent intent = new Intent(AboutActivity.this,
                WelcomeActivity.class);
        startActivity(intent);

        return;
    }

    /*
     * *Old*New user messages, should not affect normal usage.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_user_message, menu);
        return true;
    }

    /*
     * *Old*New user options menu, should no longer be visible
     */
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

    //Handles Input of the back button(Phone's button), redirect to main menu
    public void onBackPressed() {
        Intent intent = new Intent(AboutActivity.this,
                WelcomeActivity.class);
        startActivity(intent);

        return;
    }
}
