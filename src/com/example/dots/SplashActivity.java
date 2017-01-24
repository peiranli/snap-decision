package com.example.dots;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

/**
 * Created by Timothy on 2/10/2015.
 */
public class SplashActivity extends Activity {

        // Splash screen timer
        private static int SPLASH_TIME_OUT = 1000;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    //Intent i = new Intent(SplashScreen.this, MainActivity.class);
                   // startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }

