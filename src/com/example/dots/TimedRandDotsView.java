package com.example.dots;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Random;

/**
 * The random dots view for the timed game activity. Based off the RandDotsView class
 */
public class TimedRandDotsView extends ImageView
{

  private Context mContext;
  int x = -1;
  int y = -1;
  private int xVelocity = 10;
  private int yVelocity = 5;
  public static int randDots = 90;

  private Handler h;
  private Random randNum;
  public static Dot[] dotArray;
  private Dot test;
  private final int FRAME_RATE = 60;

  public static int getRandDots()
  {
    return randDots;
  }

  public TimedRandDotsView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    TimedAnimatedView animation = (TimedAnimatedView)findViewById(R.id.anim_view);
    mContext = context;
    h = new Handler();
    randNum = new Random();
    System.out.println(this.getHeight());

  }
  
  public void onSizeChanged(int w, int h, int oldW, int oldH)
  {
    if(w == 0 || h == 0) return;
    else
    {
      for(int i =0; i < TimedGameActivity.randDotArray.length; i++)
      {
          TimedGameActivity.randDotArray[i].setY(randNum.nextInt(this.getHeight()));
          TimedGameActivity.randDotArray[i].setX(randNum.nextInt(this.getWidth()));
      }
    }
  }
  
  private Runnable r = new Runnable()
  {
    public void run()
    {
      invalidate();
    }
  };
  
  protected void onDraw(Canvas c)
  {
    for(Dot d: TimedGameActivity.randDotArray)
    {
     d.setX(randNum.nextInt(this.getWidth()));
     d.setY(randNum.nextInt(this.getHeight()));
     TimedAnimatedView animation = (TimedAnimatedView)findViewById(R.id.anim_view);
     if(TimedGameActivity.start)
     c.drawBitmap(d.getBitmap(),  d.getX(), d.getY(), null); 
    }
    
    h.postDelayed(r,  FRAME_RATE);
  }

}
