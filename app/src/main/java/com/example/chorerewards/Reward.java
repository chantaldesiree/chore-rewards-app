package com.example.chorerewards;

import java.io.Serializable;

//Defines a reward.
public class Reward implements Serializable
{
    String name;
    long points;

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }

    public void setPointValue(long points)
    {
        this.points = points;
    }

    public long getPointValue()
    {
        return points;
    }
}