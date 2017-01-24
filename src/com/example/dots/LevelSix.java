package com.example.dots;

import java.util.Random;

/**
 * Created by Timothy on 11/12/2014.
 * A level of the original version of the game view.
 */
public class LevelSix implements Level {
    private int numDots = 100;
    private double coherence = 0.05;
    private double penaltyTime = 2.0;
    private int speed = 10;
    private int dotSize = 10;
    private double brightness = 0.5;
    private int artificial_level = 1;
    private int artificial_level_counter = 0;
    private int level_identifier = 6;
    private int trialNumber = 0;
    private Random randNum;

    //graduation conditions
    private int numTrials = 400;
    private double requiredAccuracy = 0.75;
    private TrialVector trials = new TrialVector(numTrials);
    private int numTrials2 = 500;
    //private double requiredAccuracy2 = ;
    //private TrialVector trials2 = new TrialVector(numTrials2);

    public LevelSix()
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
        if((trials.size() >= numTrials && trials.percentCorrect() >= requiredAccuracy) ||
                (trialNumber == numTrials2))
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
