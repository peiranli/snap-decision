package com.example.dots;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;


import java.io.IOException;
import java.util.Random;

/**
 * The main visual screen for the application, draws the dots that make up the
 * image of static for the decisions made. Is called from the Game activity classes.
 */
public class AnimatedView extends ImageView {
    Integer i = new Integer(3);
    private Context mContext;
    private Handler h;
    private Random randNum;
    private Dot[] dotArray;
    private boolean direction; //true is right, false is left
    // protected boolean start = false;
    private Dot test;
    private final int FRAME_RATE = 60;
    private Button rightButton;
    private Button leftButton;
    private String right = "right";
    private String left = "left";

    public TrialVector[] vectors;

    public String getData() {
        //return data;
        return "";
    }


    /**
     * Checks the brightness of the screen from the mobile device
     * Used for displaying an alert in game screens notifying users
     * of need to turn brightness up
     */
    public int getBrightness() {
        int brightness = -1;
        try {
            brightness = android.provider.Settings.System.getInt(
                    getContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return brightness;
    }

    /**
     * Constructor for the animated view. Takes context and attributess
     * from game activities
     */
    public AnimatedView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        randNum = new Random();

    }

    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        if (w == 0 || h == 0) return;
        else {
            if(GameActivity.dirDotArray != null) {
                for (int i = 0; i < GameActivity.dirDotArray.length; i++) {
                    GameActivity.dirDotArray[i].setY(randNum.nextInt(h));
                    GameActivity.dirDotArray[i].setX(randNum.nextInt(w));
                }
            }
        }
    }

    private Runnable r = new Runnable() {
        public void run() {
            invalidate();
        }
    };

    /**
     * Draws the actual dots of the activity.
     * This includes placing dots in certain positions, and setting the direction
     * and speed in which they will move
     * @param c
     */
    protected void onDraw(Canvas c) {
        if(GameActivity.newTrial) //randomize all dots
        {
            for (Dot d : GameActivity.dirDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
                }
            for (Dot d : GameActivity.randDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
            }
            GameActivity.newTrial = false;
        }
        int speed = 0;
        if(GameActivity.direction == 1)
        {
            speed =  GameActivity.currLevel.getSpeed();
        }
        else
        {
            speed =  (-1)* GameActivity.currLevel.getSpeed();
        }
        for (Dot d : GameActivity.dirDotArray) {

            d.setX(d.getX() + speed);
            if (GameActivity.direction == 1 && (d.getX() > this.getWidth() + 50) /*(ball.getBitmap().getWidth())*/ /*|| (x < -50)*/) {
                d.setY(randNum.nextInt(this.getHeight()));
                //System.out.println(this.getHeight());
                d.setX(-100);
            } else if (GameActivity.direction == 0 && (d.getX() < -50)) {
                d.setY(randNum.nextInt(this.getHeight()));
                d.setX(this.getWidth() + 50);
            }
            if (GameActivity.start) {
                //System.out.println("drawing");
                c.drawBitmap(d.getBitmap(), d.getX(), d.getY(), null);
            }
        }

        //c.drawBitmap(ball.getBitmap(),  x, y, null);
        h.postDelayed(r, FRAME_RATE);
    }
}
