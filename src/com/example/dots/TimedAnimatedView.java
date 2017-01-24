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
 * Animated view for the timed game version of the app
 * based off the AnimatedView class
 */
public class TimedAnimatedView extends ImageView {
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

    public TimedAnimatedView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        randNum = new Random();

    }

    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        if (w == 0 || h == 0) return;
        else {
            if(TimedGameActivity.dirDotArray != null) {
                for (int i = 0; i < TimedGameActivity.dirDotArray.length; i++) {
                    TimedGameActivity.dirDotArray[i].setY(randNum.nextInt(h));
                    TimedGameActivity.dirDotArray[i].setX(randNum.nextInt(w));
                }
            }
        }
    }

    private Runnable r = new Runnable() {
        public void run() {
            invalidate();
        }
    };

    protected void onDraw(Canvas c) {
        if(TimedGameActivity.newTrial) //randomize all dots
        {
            for (Dot d : TimedGameActivity.dirDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
                }
            for (Dot d : TimedGameActivity.randDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
            }
            TimedGameActivity.newTrial = false;
        }
        int speed = 0;
        if(TimedGameActivity.direction == 1)
        {
            speed =  TimedGameActivity.currLevel.getSpeed();
        }
        else
        {
            speed =  (-1)* TimedGameActivity.currLevel.getSpeed();
        }
        for (Dot d : TimedGameActivity.dirDotArray) {

            d.setX(d.getX() + speed);
            if (TimedGameActivity.direction == 1 && (d.getX() > this.getWidth() + 50) /*(ball.getBitmap().getWidth())*/ /*|| (x < -50)*/) {
                d.setY(randNum.nextInt(this.getHeight()));
                //System.out.println(this.getHeight());
                d.setX(-100);
            } else if (TimedGameActivity.direction == 0 && (d.getX() < -50)) {
                d.setY(randNum.nextInt(this.getHeight()));
                d.setX(this.getWidth() + 50);
            }
            if (TimedGameActivity.start) {
                //System.out.println("drawing");
                c.drawBitmap(d.getBitmap(), d.getX(), d.getY(), null);
            }
        }

        //c.drawBitmap(ball.getBitmap(),  x, y, null);
        h.postDelayed(r, FRAME_RATE);
    }
}
