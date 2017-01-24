package com.example.dots;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * A drawable dot for the AnimatedView Activity, displays a small white square.
 */
public class Dot
{
  BitmapDrawable bitmap;
  int x, y; //coordinates for the dot

  /**
   * Default constructor for Dot, initializes it at position 0,0
   */
  public Dot()
  {
    x = 0;
    y = 0;
  }

  /*
   * Two arg constructor for Dot,
   * Sets the position based on args.
   */
  public Dot(int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Enables visuals of the dot, sets the drawable on which the
   * dot is drawn.
   * @param b
   */
  public void setBitmapDrawable(BitmapDrawable b)
  {
    bitmap = b;
  }
  /**
    * Sets Bitmap on which the dot is drawn
    * @return
    */
  public Bitmap getBitmap()
  {
    return bitmap.getBitmap();
  }

  /**
    * Sets the x position of the dot.
    * @param x
    */
  public void setX(int x)
  {
    this.x = x;
  }

  /**
   * Sets the y position of the dot
   * @param y
   */
  public void setY(int y)
  {
    this.y = y;
  }

  /**
   * Gets the x position of the dot
   * @return
   */
  public int getX()
  {
    return x;
  }

  /**
   * Gets the y position of the dot
   * @return
   */
  public int getY(){ return y;}


    /**
     * Created by Timothy on 10/14/2014.
     */
    public static class ParseApplication {
    }
}
