package com.example.dots;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Timothy on 1/28/2015.
 * //replaced with popup screen in welcome activity
 */
public class ViewTermsActivity extends Activity {
    static String msg = "Terms and Conditions \n Welcome! This app is part of a research study at UC San Diego. By " +
            "tapping the \"I agree\" button below, you indicate that you understand that the app's data " +
            "will be used for research purposes and you agree to allow us to use that data. The data " +
            "collected does NOT contain any personal data, but only performance statistics, " +
            "such as accuracy, etc. If you tap \"I do not agree\" the application will exit.";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_terms_activity);
        TextView message = (TextView)findViewById(R.id.terms);
        message.setText(msg);
        message.setGravity(Gravity.CENTER);

    }
    public void onViewTermsBackClick(View v)
    {
        finish();
    }

}
