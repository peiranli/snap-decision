package com.example.dots;

/**
 * Created by Timothy on 11/9/2014.
 * An interface defining a 'level' of the app. Sets the coherence and various other parameters
 * for the animated view of the original game view
 */
public interface Level
{
    public void addTrial(int i );
    public boolean graduateLevel();
    public int getLevel();
    public int getNumDots();
    public double getCoherence();
    public double getPenaltyTime();
    public int getSpeed();
    public int dotSize();
    public int getDirection();
    public double getBrightness();
    public int getTrialNumber();
    public int getPoints();
    public String toString();
}
