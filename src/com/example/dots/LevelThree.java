package com.example.dots;

import java.util.Random;

/**
 * Created by Timothy on 11/12/2014.
 * A level of the original version of the game view.
 */

public class LevelThree implements Level {
    private int numDots = 100;
    private double coherence = 0.1;
    private double penaltyTime = 2.0;
    private int speed = 10;
    private int dotSize = 10;
    private double brightness = 0.5;
    private int artificial_level = 1;
    private int artificial_level_counter = 0;
    private int level_identifier = 3;
    private int trialNumber = 0;
    private TrialVector trials = new TrialVector(200);

    private Random randNum;

    //graduation conditions
    private int numTrials = 200;
    private double requiredAccuracy = 0.85;
    public LevelThree()
    {
        randNum = new Random();
    }

    public int getDirection()
    {
        return randNum.nextInt(2);
    }

    public void addTrial(int i )
    {
        trials.addTrial(i);
        if(i == 1)
        {
            artificial_level_counter++;
            trialNumber++;
        }
    }

    public boolean graduateLevel()
    {
        if(trials.size() >= numTrials && trials.percentCorrect() >= requiredAccuracy)
        {
            return true;
        }
        else
            return false;
    }

    public int getLevel()
    {
        return level_identifier;
    }


    public int getNumDots()
    {
        return numDots;
    }

    public double getCoherence()
    {
        return coherence;
    }
    public double getPenaltyTime()
    {
        return penaltyTime;
    }

    public int getSpeed()
    {
        return speed;
    }

    public int dotSize()
    {
        return dotSize;
    }

    public double getBrightness()
    {
        return brightness;
    }

    public int getTrialNumber()
    {
        return trialNumber;
    }

    public int getPoints()
    {
        return artificial_level_counter;
    }

    public String toString()
    {
        return ""+level_identifier;
    }
}
