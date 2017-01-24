package com.example.dots;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Random;

/**
 * An image maker for the dots main activity, spawns
 * a lot of dots on the screen, randomly
 */
public class RandDotsView extends ImageView
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
  
  public RandDotsView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    AnimatedView animation = (AnimatedView)findViewById(R.id.anim_view);
    mContext = context;
    h = new Handler();
    randNum = new Random();
    System.out.println(this.getHeight());

  }

  /**
   * Resets where the dots are in the screen if the size changes.
   * @param w
   * @param h
   * @param oldW
   * @param oldH
   */
  public void onSizeChanged(int w, int h, int oldW, int oldH)
  {
    if(w == 0 || h == 0) return;
    else
    {
      for(int i =0; i < GameActivity.randDotArray.length; i++)
      {
          GameActivity.randDotArray[i].setY(randNum.nextInt(this.getHeight()));
          GameActivity.randDotArray[i].setX(randNum.nextInt(this.getWidth()));
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


  /**
   * Draws the screen using the dots in the array.
   * does this randomly.
   * @param c
   */
  protected void onDraw(Canvas c)
  {
    for(Dot d: GameActivity.randDotArray)
    {
     d.setX(randNum.nextInt(this.getWidth()));
     d.setY(randNum.nextInt(this.getHeight()));
     AnimatedView animation = (AnimatedView)findViewById(R.id.anim_view);
     if(GameActivity.start)
     c.drawBitmap(d.getBitmap(),  d.getX(), d.getY(), null); 
    }
    
    h.postDelayed(r,  FRAME_RATE);
  }

}
