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
 * The always random coherence version of the application's animated view,
 * which is used in the game view of the test your limits. Based off AnimatedView class
 */
public class LimitsAnimatedView extends ImageView {
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

    public LimitsAnimatedView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        randNum = new Random();

    }

    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        if (w == 0 || h == 0) return;
        else {
            if(LimitsGameActivity.dirDotArray != null) {
                for (int i = 0; i < LimitsGameActivity.dirDotArray.length; i++) {
                    LimitsGameActivity.dirDotArray[i].setY(randNum.nextInt(h));
                    LimitsGameActivity.dirDotArray[i].setX(randNum.nextInt(w));
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
        if(LimitsGameActivity.newTrial) //randomize all dots
        {
            for (Dot d : LimitsGameActivity.dirDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
                }
            for (Dot d : LimitsGameActivity.randDotArray) {
                d.setX(randNum.nextInt(this.getWidth()));
                d.setY(randNum.nextInt(this.getHeight()));
            }
            LimitsGameActivity.newTrial = false;
        }
        int speed = 0;
        if(LimitsGameActivity.direction == 1)
        {
            speed =  LimitsGameActivity.currLevel.getSpeed();
        }
        else
        {
            speed =  (-1)* LimitsGameActivity.currLevel.getSpeed();
        }
        for (Dot d : LimitsGameActivity.dirDotArray) {

            d.setX(d.getX() + speed);
            if (LimitsGameActivity.direction == 1 && (d.getX() > this.getWidth() + 50) /*(ball.getBitmap().getWidth())*/ /*|| (x < -50)*/) {
                d.setY(randNum.nextInt(this.getHeight()));
                //System.out.println(this.getHeight());
                d.setX(-100);
            } else if (LimitsGameActivity.direction == 0 && (d.getX() < -50)) {
                d.setY(randNum.nextInt(this.getHeight()));
                d.setX(this.getWidth() + 50);
            }
            if (LimitsGameActivity.start) {
                //System.out.println("drawing");
                c.drawBitmap(d.getBitmap(), d.getX(), d.getY(), null);
            }
        }

        //c.drawBitmap(ball.getBitmap(),  x, y, null);
        h.postDelayed(r, FRAME_RATE);
    }
}
